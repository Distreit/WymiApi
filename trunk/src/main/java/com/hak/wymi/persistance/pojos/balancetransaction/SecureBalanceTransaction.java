package com.hak.wymi.persistance.pojos.balancetransaction;

import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.utility.transactionprocessor.TransactionProcessor;
import org.joda.time.DateTime;

public class SecureBalanceTransaction implements SecureToSend {

    private final Integer transactionId;

    private final String transactionType;

    private final Integer amount;

    private final String recipientName;

    private final String url;

    private final DateTime created;

    private final DateTime commitTime;

    private final String sourceName;

    private final String type;

    public SecureBalanceTransaction(BalanceTransaction transaction) {
        this.transactionId = transaction.getTransactionId();
        this.transactionType = transaction.getClass().getSimpleName().replace("Transaction", "");
        this.amount = transaction.getAmount();
        this.sourceName = transaction.getSource().getName();
        this.recipientName = transaction.getDestination().getName();
        this.url = transaction.getTargetUrl();
        this.created = transaction.getCreated();
        this.commitTime = new DateTime(this.created.plusMillis(TransactionProcessor.TRANSACTION_WAIT_PERIOD));
        this.type = transaction.getClass().getSimpleName();
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

    public DateTime getCreated() {
        return created;
    }

    public DateTime getCommitTime() {
        return commitTime;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getType() {
        return type;
    }
}
