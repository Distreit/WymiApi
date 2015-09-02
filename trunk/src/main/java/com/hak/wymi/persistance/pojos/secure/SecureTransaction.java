package com.hak.wymi.persistance.pojos.secure;

import com.hak.wymi.persistance.pojos.unsecure.BalanceTransaction;
import com.hak.wymi.persistance.pojos.unsecure.interfaces.SecureToSend;

import java.util.Date;

public class SecureTransaction implements SecureToSend {

    private final Integer amount;

    private final String recipientName;

    private final String url;

    private final Date created;

    public SecureTransaction(BalanceTransaction transaction) {
        this.amount = transaction.getAmount();
        this.recipientName = transaction.getDestinationUser().getName();
        this.url = transaction.getTargetUrl();
        this.created = transaction.getCreated();
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
}
