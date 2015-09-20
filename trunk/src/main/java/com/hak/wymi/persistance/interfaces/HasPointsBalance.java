package com.hak.wymi.persistance.interfaces;

public interface HasPointsBalance {
    boolean addPoints(Integer amount);

    boolean removePoints(Integer amount);

    void incrementTransactionCount();

    String getName();

    Integer getBalanceId();
}
