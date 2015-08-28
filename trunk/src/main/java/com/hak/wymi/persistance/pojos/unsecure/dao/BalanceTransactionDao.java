package com.hak.wymi.persistance.pojos.unsecure.dao;

import com.hak.wymi.persistance.pojos.unsecure.BalanceTransaction;

@FunctionalInterface
public interface BalanceTransactionDao {
    boolean process(BalanceTransaction postTransaction);
}
