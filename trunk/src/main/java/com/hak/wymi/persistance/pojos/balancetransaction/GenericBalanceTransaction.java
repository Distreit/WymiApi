package com.hak.wymi.persistance.pojos.balancetransaction;

import com.hak.wymi.persistance.pojos.PersistentObject;
import com.hak.wymi.validations.groups.Creation;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.validation.constraints.Null;

@MappedSuperclass
public abstract class GenericBalanceTransaction extends PersistentObject implements BalanceTransaction {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transactionLogId")
    private TransactionLog transactionLog;

    @Enumerated(EnumType.STRING)
    @Null(groups = Creation.class)
    private TransactionState state;

    @Override
    public TransactionState getState() {
        return this.state;
    }

    @Override
    public void setState(TransactionState state) {
        this.state = state;
    }

    @Override
    public TransactionLog getTransactionLog() {
        return transactionLog;
    }

    @Override
    public void setTransactionLog(TransactionLog transactionLog) {
        this.transactionLog = transactionLog;
    }
}
