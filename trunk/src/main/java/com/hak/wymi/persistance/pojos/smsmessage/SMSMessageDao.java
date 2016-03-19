package com.hak.wymi.persistance.pojos.smsmessage;

import java.util.List;

public interface SMSMessageDao {
    void save(SMSMessage message);

    List<SMSMessage> getUnsent();

    void update(SMSMessage message);

    List<SMSMessage> getForNumber(String number);
}
