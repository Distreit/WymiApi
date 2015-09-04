package com.hak.wymi.persistance.pojos.unsecure.dao;

import com.hak.wymi.persistance.pojos.unsecure.BalanceTransaction;

public interface BalanceTransactionDao {
    boolean process(BalanceTransaction balanceTransaction);

    boolean cancel(BalanceTransaction balanceTransaction);
}
