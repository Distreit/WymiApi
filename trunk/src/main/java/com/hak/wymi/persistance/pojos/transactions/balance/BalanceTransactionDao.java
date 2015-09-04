package com.hak.wymi.persistance.pojos.transactions.balance;

public interface BalanceTransactionDao {
    boolean process(BalanceTransaction balanceTransaction);

    boolean cancel(BalanceTransaction balanceTransaction);
}
