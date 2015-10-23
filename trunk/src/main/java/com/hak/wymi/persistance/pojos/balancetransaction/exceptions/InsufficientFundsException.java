package com.hak.wymi.persistance.pojos.balancetransaction.exceptions;

public class InsufficientFundsException extends Exception {
    private static final long serialVersionUID = 2925561926123895336L;

    public InsufficientFundsException(String s) {
        super(s);
    }
}
