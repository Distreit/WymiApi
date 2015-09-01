package com.hak.wymi.persistance.pojos.secure;

import com.hak.wymi.persistance.pojos.unsecure.Topic;
import com.hak.wymi.persistance.pojos.unsecure.interfaces.SecureToSend;

public class SecureTopic implements SecureToSend {

    private final String name;
    private final Integer feeFlat;
    private final Integer feePercent;
    private final String owner;
    private final Integer subscribers;
    private final Integer unsubscribers;

    public SecureTopic(Topic topic) {
        this.name = topic.getName();
        this.feeFlat = topic.getFeeFlat();
        this.feePercent = topic.getFeePercent();
        this.owner = topic.getOwner().getName();
        this.subscribers = topic.getSubscribers();
        this.unsubscribers = topic.getUnsubscribers();
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public Integer getSubscribers() {
        return subscribers;
    }

    public Integer getUnsubscribers() {
        return unsubscribers;
    }

    public Integer getFeeFlat() {
        return feeFlat;
    }

    public Integer getFeePercent() {
        return feePercent;
    }
}
