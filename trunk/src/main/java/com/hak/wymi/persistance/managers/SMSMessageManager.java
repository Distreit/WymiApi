package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.smsmessage.SMSMessage;
import com.hak.wymi.persistance.pojos.smsmessage.SMSMessageDao;
import com.hak.wymi.utility.smsmessager.SMSMessager;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SMSMessageManager {
    @Autowired
    private SMSMessageDao smsDao;

    @Autowired
    private SMSMessager smsMessager;

    @Transactional
    public void save(SMSMessage message) {
        List<SMSMessage> existingMessages = smsDao.getForNumber(message.getNumber());

        for (SMSMessage existingMessage : existingMessages) {
            if (existingMessage.getCreated().plusHours(1).isAfter(DateTime.now())) {
                throw new UnsupportedOperationException("Cannot send more than one phone verification per number per hour.");
            }
        }

        smsDao.save(message);
        smsMessager.add(message);
    }

    @Transactional
    public List<SMSMessage> getUnsent() {
        return smsDao.getUnsent();
    }

    @Transactional
    public void update(SMSMessage message) {
        smsDao.update(message);
    }
}
