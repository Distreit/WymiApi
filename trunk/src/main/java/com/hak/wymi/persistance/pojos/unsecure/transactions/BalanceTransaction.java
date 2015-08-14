package com.hak.wymi.persistance.pojos.unsecure.transactions;

public abstract class BalanceTransaction {
    public abstract void setState(TransactionState state) ;

    public abstract TransactionState getState();
}
