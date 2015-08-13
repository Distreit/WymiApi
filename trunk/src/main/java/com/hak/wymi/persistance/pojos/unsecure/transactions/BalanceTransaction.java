package com.hak.wymi.persistance.pojos.unsecure.transactions;

public abstract class BalanceTransaction {
    public abstract void setProcessed(boolean processed) ;

    public abstract boolean getProcessed();
}
