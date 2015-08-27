package com.hak.wymi.persistance.pojos.unsecure;

import java.util.Date;

public interface BalanceTransaction {
    void setState(TransactionState state);

    Date getCreated();

    Integer getAmount();

    Integer getSourceUserId();

    Integer getDestinationUserId();

    Integer getTargetId();

    Class getTargetClass();

    User getSourceUser();

    String getTargetUrl();
}
