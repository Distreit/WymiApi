package com.hak.wymi.persistance.pojos.balancetransaction.exceptions;

import com.hak.wymi.utility.JSONConverter;

public class NegativePointsException extends InvalidValueException {
    private static final long serialVersionUID = -2572029244447145379L;

    public NegativePointsException(Integer amount, Object obj) {
        super(String.format("Cannot add or remove negative points. \nAmount: %d\n%s",
                amount, JSONConverter.getJSON(obj, true)));
    }
}
