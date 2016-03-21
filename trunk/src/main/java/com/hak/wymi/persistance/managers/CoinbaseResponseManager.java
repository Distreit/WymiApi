package com.hak.wymi.persistance.managers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hak.wymi.coinbase.pojos.CoinbaseCurrencyAmount;
import com.hak.wymi.coinbase.pojos.CoinbaseOrder;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.coinbaseresponse.CoinbaseRawResponse;
import com.hak.wymi.persistance.pojos.coinbaseresponse.CoinbaseRawResponseDao;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.persistance.pojos.user.UserDao;
import com.hak.wymi.utility.jsonconverter.JSONConverter;
import com.hak.wymi.utility.transactionprocessor.TransactionProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
public class CoinbaseResponseManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoinbaseResponseManager.class);

    @Autowired
    private CoinbaseRawResponseDao coinbaseRawResponseDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TransactionProcessor transactionProcessor;

    @Transactional
    public void saveNewResponse(String responseText) {
        CoinbaseRawResponse response = new CoinbaseRawResponse();
        response.setProcessed(false);
        response.setResponseText(responseText);
        coinbaseRawResponseDao.save(response);
    }

    @Transactional(rollbackFor = {IOException.class, InvalidValueException.class})
    public void process(CoinbaseRawResponse response) throws IOException, InvalidValueException {
        if (!response.getProcessed()) {
            ObjectMapper mapper = new ObjectMapper();

            JsonNode jsonResponse = mapper.readTree(response.getResponseText());
            JsonNode typeNode = jsonResponse.get("type");
            if (typeNode != null) {
                String type = typeNode.asText();
                process(type.split(":"), jsonResponse);
                response.setProcessed(true);
                coinbaseRawResponseDao.update(response);
            } else {
                LOGGER.error("BAD Coinbase response: " + jsonResponse.asText());
            }
        }
    }

    private void process(String[] type, JsonNode jsonResponse) throws JsonProcessingException, InvalidValueException {
        switch (type[0]) {
            case "wallet":
                processWalletResponse(type, jsonResponse);
                break;
            default:
                LOGGER.error("Unhandled response type:" + jsonResponse.asText());
        }
    }

    private void processWalletResponse(String[] type, JsonNode jsonResponse) throws JsonProcessingException, InvalidValueException {
        switch (type[1]) {
            case "orders":
                processWalletOrdersResponse(type, jsonResponse);
                break;
            default:
                LOGGER.error("Unhandled wallet response type:" + jsonResponse.asText());
        }
    }

    private void processWalletOrdersResponse(String[] type, JsonNode jsonResponse) throws JsonProcessingException, InvalidValueException {
        switch (type[2]) {
            case "mispaid":
            case "paid":
                processWalletOrdersPaidResponse(jsonResponse);
                break;
            default:
                LOGGER.error("Unhandled wallet order response type:" + jsonResponse.asText());
        }
    }

    private void processWalletOrdersPaidResponse(JsonNode jsonResponse) throws JsonProcessingException, InvalidValueException {
        JsonNode dataNode = jsonResponse.get("data");
        if (dataNode != null) {
            JsonNode resourceNode = dataNode.get("resource");
            if (resourceNode != null) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    processWalletOrdersPaidResponse(mapper.treeToValue(resourceNode, CoinbaseOrder.class));
                } catch (JsonProcessingException e) {
                    LOGGER.error("Error processing wallet response paid resource:" + resourceNode.asText());
                    throw e;
                }
            }
        }
    }

    private void processWalletOrdersPaidResponse(CoinbaseOrder order) throws InvalidValueException {
        CoinbaseCurrencyAmount amountObject = order.getAmount();
        if (amountObject.getCurrency().equals("USD") && order.getMetadata() != null && order.getMetadata().containsKey("custom")) {
            User destinationUser = userDao.getFromName(order.getMetadata().get("custom"));
            int amount = ((Double) (amountObject.getAmount() * 10000)).intValue();

            transactionProcessor.createPointsFor(destinationUser, amount);
        } else {
            LOGGER.error("Order currency not in USD, or missing user name:" + JSONConverter.getJSON(order, false));
            throw new UnsupportedOperationException();
        }
    }

    @Transactional
    public List<CoinbaseRawResponse> getUnprocessed() {
        return coinbaseRawResponseDao.getUnprocessed();
    }
}
