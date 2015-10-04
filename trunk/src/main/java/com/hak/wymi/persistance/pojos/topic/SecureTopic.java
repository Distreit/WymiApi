package com.hak.wymi.persistance.pojos.topic;

import com.hak.wymi.persistance.interfaces.SecureToSend;
import org.joda.time.DateTime;

public class SecureTopic implements SecureToSend {

    private final String name;
    private final Integer feeFlat;
    private final Integer feePercent;
    private final String owner;
    private final Integer subscriberCount;
    private final Integer filterCount;
    private final DateTime rentDueDate;

    public SecureTopic(Topic topic) {
        this.name = topic.getName();
        this.feeFlat = topic.getFeeFlat();
        this.feePercent = topic.getFeePercent();
        this.owner = topic.getOwner().getName();
        this.subscriberCount = topic.getSubscriberCount();
        this.filterCount = topic.getFilterCount();
        this.rentDueDate = topic.getRentDueDate().dayOfMonth().roundFloorCopy();
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

    public DateTime getRentDueDate() {
        return rentDueDate;
    }
}
