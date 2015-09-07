package com.hak.wymi.persistance.pojos.user;

import com.hak.wymi.persistance.interfaces.SecureToSend;

public class SecureBalance implements SecureToSend {
    private final Integer currentBalance;

    public SecureBalance(Balance balance) {
        this.currentBalance = balance.getCurrentBalance();
    }

    public Integer getCurrentBalance() {
        return currentBalance;
    }
}
