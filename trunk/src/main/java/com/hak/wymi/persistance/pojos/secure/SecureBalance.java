package com.hak.wymi.persistance.pojos.secure;

import com.hak.wymi.persistance.pojos.unsecure.Balance;
import com.hak.wymi.persistance.pojos.unsecure.interfaces.SecureToSend;

public class SecureBalance implements SecureToSend {
    private final Integer currentBalance;

    public SecureBalance(Balance balance) {
        this.currentBalance = balance.getCurrentBalance();
    }

    public Integer getCurrentBalance() {
        return currentBalance;
    }
}
