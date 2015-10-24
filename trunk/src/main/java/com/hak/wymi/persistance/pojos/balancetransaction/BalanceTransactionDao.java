package com.hak.wymi.persistance.pojos.balancetransaction;

import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;

public interface BalanceTransactionDao {
    void process(BalanceTransaction balanceTransaction) throws InvalidValueException;

    boolean cancel(BalanceTransaction balanceTransaction) throws InvalidValueException;
}
