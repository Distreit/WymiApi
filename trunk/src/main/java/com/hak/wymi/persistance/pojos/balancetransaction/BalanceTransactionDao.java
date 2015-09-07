package com.hak.wymi.persistance.pojos.balancetransaction;

public interface BalanceTransactionDao {
    boolean process(BalanceTransaction balanceTransaction);

    boolean cancel(BalanceTransaction balanceTransaction);
}
