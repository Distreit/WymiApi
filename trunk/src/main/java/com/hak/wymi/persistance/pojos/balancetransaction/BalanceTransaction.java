package com.hak.wymi.persistance.pojos.balancetransaction;

import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.user.User;

import java.util.Date;

public interface BalanceTransaction {
    TransactionState getState();

    void setState(TransactionState state);

    Date getCreated();

    Integer getAmount();

    Integer getSourceUserId();

    Integer getDestinationId();

    Class getDestinationClass();

    Integer getTargetId();

    Class getTargetClass();

    User getSourceUser();

    String getTargetUrl();

    Integer getTransactionId();

    Object getDependent();

    Integer getTaxerUserId();

    Integer getTaxRate();

    boolean isUniqueToUser();

    HasPointsBalance getDestinationObject();
}
