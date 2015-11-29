package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.coinbaseresponse.CoinbaseRawResponse;
import com.hak.wymi.persistance.pojos.coinbaseresponse.CoinbaseRawResponseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CoinbaseResponseManager {
    @Autowired
    private CoinbaseRawResponseDao coinbaseRawResponseDao;


    @Transactional
    public void saveNewResponse(String responseText) {
        CoinbaseRawResponse response = new CoinbaseRawResponse();
        response.setProcessed(false);
        response.setResponseText(responseText);
        coinbaseRawResponseDao.save(response);
    }
}
