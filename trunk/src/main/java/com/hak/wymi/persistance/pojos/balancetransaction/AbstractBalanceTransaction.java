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
public abstract class AbstractBalanceTransaction extends PersistentObject implements BalanceTransaction {
    private static final long serialVersionUID = -7807553946856404558L;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transactionLogId")
    private TransactionLog transactionLog;

    @Enumerated(EnumType.STRING)
    @Null(groups = Creation.class)
    private TransactionState state;

    @Override
    public final TransactionState getState() {
        return this.state;
    }

    @Override
    public final void setState(TransactionState state) {
        this.state = state;
    }

    @Override
    public final TransactionLog getTransactionLog() {
        return transactionLog;
    }

    @Override
    public final void setTransactionLog(TransactionLog transactionLog) {
        this.transactionLog = transactionLog;
    }
}
