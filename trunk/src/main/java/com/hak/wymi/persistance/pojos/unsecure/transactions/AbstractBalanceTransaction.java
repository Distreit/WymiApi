package com.hak.wymi.persistance.pojos.unsecure.transactions;

import java.util.Date;

public abstract class AbstractBalanceTransaction {
    public abstract void setState(TransactionState state);

    public abstract TransactionState getState();

    public abstract Date getCreated();
}
