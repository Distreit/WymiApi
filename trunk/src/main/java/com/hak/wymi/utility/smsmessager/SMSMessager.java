package com.hak.wymi.utility.smsmessager;

import com.hak.wymi.persistance.managers.SMSMessageManager;
import com.hak.wymi.persistance.pojos.smsmessage.SMSMessage;
import com.hak.wymi.utility.jsonconverter.JSONConverter;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
public class SMSMessager {
    public static final String ACCOUNT_SID = "ACf6048c95f03953818bd7615fcba2eaf6";
    public static final String AUTH_TOKEN = "f1a5985ead50e4939ad0a51e8a04a667";
    private static final Logger LOGGER = LoggerFactory.getLogger(SMSMessager.class);
    TwilioRestClient client;
    @Autowired
    private SMSMessageManager smsMessageManager;
    private Queue<SMSMessage> smsMessageQueue = new LinkedList<>();

    @Scheduled(fixedRate = 15000)
    public void sendSMSMessage() {
        if (!smsMessageQueue.isEmpty()) {
            send(smsMessageQueue.remove());
        }
    }

    @Scheduled(fixedRate = 300000)
    public void checkSMSMessageQueue() {
        if (smsMessageQueue.isEmpty()) {
            smsMessageQueue.addAll(smsMessageManager.getUnsent());
        }
    }

    public void add(SMSMessage smsMessage) {
        if (BooleanUtils.isFalse(smsMessage.getSent()) && smsMessage.getSmsMessageId() != null) {
            int id = smsMessage.getSmsMessageId();
            if (smsMessageQueue.stream().filter(e -> e.getSmsMessageId() != id).count() == 0) {
                smsMessageQueue.add(smsMessage);
            }
        }
    }

    private void send(SMSMessage smsMessage) {
        TwilioRestClient client = getClient();

        // Build a filter for the MessageList
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("Body", smsMessage.getBody()));
        params.add(new BasicNameValuePair("To", smsMessage.getNumber()));
        params.add(new BasicNameValuePair("From", "+12674605243"));

        MessageFactory messageFactory = client.getAccount().getMessageFactory();
        try {
            messageFactory.create(params);
        } catch (TwilioRestException e) {
            smsMessage.setErrorCode(Integer.toString(e.getErrorCode()));
            LOGGER.error("Failed to send SMSMessage. \n" + JSONConverter.getJSON(e, true), e);
        }

        smsMessage.setSent(true);
        smsMessage.setSentDate(new DateTime());
        smsMessageManager.update(smsMessage);
    }

    private TwilioRestClient getClient() {
        if (this.client == null) {
            this.client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
        }
        return this.client;
    }
}
