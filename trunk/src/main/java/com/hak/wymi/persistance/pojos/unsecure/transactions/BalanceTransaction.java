package com.hak.wymi.persistance.pojos.unsecure.transactions;

import java.util.Date;

public abstract class BalanceTransaction {
    public abstract void setState(TransactionState state) ;

    public abstract TransactionState getState();

    public abstract Date getCreated();
}
