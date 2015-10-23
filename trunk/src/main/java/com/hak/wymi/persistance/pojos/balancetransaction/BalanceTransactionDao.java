package com.hak.wymi.persistance.pojos.balancetransaction;

import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InsufficientFundsException;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;

public interface BalanceTransactionDao {
    void process(BalanceTransaction balanceTransaction) throws InsufficientFundsException, InvalidValueException;

    boolean cancel(BalanceTransaction balanceTransaction) throws InvalidValueException, InsufficientFundsException;
}
