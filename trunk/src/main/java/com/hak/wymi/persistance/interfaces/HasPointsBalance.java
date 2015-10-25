package com.hak.wymi.persistance.interfaces;

import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;

public interface HasPointsBalance {
    void addPoints(Integer amount) throws InvalidValueException;

    void removePoints(Integer amount) throws InvalidValueException;

    void incrementTransactionCount();

    String getName();

    Integer getBalanceId();
}
