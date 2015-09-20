package com.hak.wymi.persistance.pojos.topicbid;

import com.hak.wymi.persistance.interfaces.SecureToSend;

public class SecureTopicBid implements SecureToSend {
    final private String topicName;

    final private String userName;

    final private Integer amount;

    public SecureTopicBid(TopicBid topicBid) {
        this.topicName = topicBid.getTopic().getName();
        this.userName = topicBid.getUser().getName();
        this.amount = topicBid.getCurrentBalance();
    }

    public String getTopicName() {
        return topicName;
    }

    public String getUserName() {
        return userName;
    }

    public Integer getAmount() {
        return amount;
    }
}
