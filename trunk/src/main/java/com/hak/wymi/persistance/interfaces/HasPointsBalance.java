package com.hak.wymi.persistance.interfaces;

import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;

public interface HasPointsBalance {
    public static String ADDING_NEGATIVE_POINTS_MESSAGE = "Cannot add negative points to a balance.";
    public static String REMOVING_NEGATIVE_POINTS_MESSAGE = "Cannot remove negative points from a balance.";

    void addPoints(Integer amount) throws InvalidValueException;

    void removePoints(Integer amount) throws InvalidValueException;

    void incrementTransactionCount();

    String getName();

    Integer getBalanceId();
}
