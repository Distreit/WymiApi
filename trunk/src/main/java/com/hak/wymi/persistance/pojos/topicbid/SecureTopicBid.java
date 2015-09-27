package com.hak.wymi.persistance.pojos.topicbid;

import com.hak.wymi.persistance.interfaces.SecureToSend;

public class SecureTopicBid implements SecureToSend {
    private final Integer topicBidId;

    private final String topicName;

    private final String userName;

    private final Integer amount;

    public SecureTopicBid(TopicBid topicBid) {
        this.topicBidId = topicBid.getTopicBidId();
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

    public Integer getTopicBidId() {
        return topicBidId;
    }
}
