package com.hak.wymi.persistance.pojos.balancetransaction.exceptions;

import com.hak.wymi.utility.JSONConverter;

public class InsufficientFundsException extends InvalidValueException {
    private static final long serialVersionUID = 2925561926123895336L;

    public InsufficientFundsException(Integer amount, Object obj) {
        super(String.format("Insufficient funds to remove %d points from %s",
                amount, JSONConverter.getJSON(obj, true)));
    }
}
