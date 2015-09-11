package com.hak.wymi.persistance.pojos.topic;

import com.hak.wymi.persistance.interfaces.SecureToSend;

public class SecureTopic implements SecureToSend {

    private final String name;
    private final Integer feeFlat;
    private final Integer feePercent;
    private final String owner;
    private final Integer subscriberCount;
    private final Integer filterCount;

    public SecureTopic(Topic topic) {
        this.name = topic.getName();
        this.feeFlat = topic.getFeeFlat();
        this.feePercent = topic.getFeePercent();
        this.owner = topic.getOwner().getName();
        this.subscriberCount = topic.getSubscriberCount();
        this.filterCount = topic.getFilterCount();
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public Integer getSubscriberCount() {
        return subscriberCount;
    }

    public Integer getFilterCount() {
        return filterCount;
    }

    public Integer getFeeFlat() {
        return feeFlat;
    }

    public Integer getFeePercent() {
        return feePercent;
    }

    public String getUrl() {
        return String.format("t/%s", this.getName());
    }
}
