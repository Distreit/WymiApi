package com.hak.wymi.persistance.pojos.unsecure.transactions;

import com.hak.wymi.persistance.pojos.unsecure.commenttransaction.CommentTransactionAbstract;
import com.hak.wymi.persistance.pojos.unsecure.posttransaction.PostTransactionAbstract;

public interface BalanceTransactionDao {
    boolean process(PostTransactionAbstract postTransaction);

    boolean process(CommentTransactionAbstract commentTransaction);
}
