package com.hak.wymi.persistance.pojos.balancetransaction;

import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;

import java.util.List;

public interface BalanceTransactionDao {
    void process(BalanceTransaction balanceTransaction) throws InvalidValueException;

    boolean cancel(BalanceTransaction balanceTransaction) throws InvalidValueException;

    List<BalanceTransaction> getForUser(Class transactionTypeClass, String userName, Integer firstResult, Integer maxResults);
}
