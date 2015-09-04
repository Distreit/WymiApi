package com.hak.wymi.persistance.pojos.transactions.balance;

import com.hak.wymi.persistance.pojos.transactions.TransactionState;
import com.hak.wymi.persistance.pojos.user.User;

import java.util.Date;

public interface BalanceTransaction {
    TransactionState getState();

    void setState(TransactionState state);

    Date getCreated();

    Integer getAmount();

    Integer getSourceUserId();

    Integer getDestinationUserId();

    Integer getTargetId();

    Class getTargetClass();

    User getSourceUser();

    String getTargetUrl();

    User getDestinationUser();

    Integer getTransactionId();
}
