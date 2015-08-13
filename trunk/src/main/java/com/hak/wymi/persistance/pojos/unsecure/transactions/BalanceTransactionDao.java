package com.hak.wymi.persistance.pojos.unsecure.transactions;

import com.hak.wymi.persistance.pojos.unsecure.posttransaction.PostTransaction;

public interface BalanceTransactionDao {
    public boolean process(PostTransaction postTransaction);
}
