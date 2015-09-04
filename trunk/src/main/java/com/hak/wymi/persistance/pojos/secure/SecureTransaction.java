package com.hak.wymi.persistance.pojos.secure;

import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.pojos.unsecure.BalanceTransaction;
import com.hak.wymi.persistance.utility.BalanceTransactionManager;

import java.util.Date;

public class SecureTransaction implements SecureToSend {

    private final Integer transactionId;

    private final String transactionType;

    private final Integer amount;

    private final String recipientName;

    private final String url;

    private final Date created;

    private final Date commitTime;

    public SecureTransaction(BalanceTransaction transaction) {
        this.transactionId = transaction.getTransactionId();
        this.transactionType = transaction.getClass().getSimpleName().replace("Transaction", "");
        this.amount = transaction.getAmount();
        this.recipientName = transaction.getDestinationUser().getName();
        this.url = transaction.getTargetUrl();
        this.created = transaction.getCreated();
        this.commitTime = new Date(this.created.getTime() + BalanceTransactionManager.TRANSACTION_WAIT_PERIOD);
    }

    public Integer getAmount() {
        return amount;
    }

    public String getUrl() {
        return url;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public Date getCreated() {
        return created;
    }

    public Date getCommitTime() {
        return commitTime;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }
}
