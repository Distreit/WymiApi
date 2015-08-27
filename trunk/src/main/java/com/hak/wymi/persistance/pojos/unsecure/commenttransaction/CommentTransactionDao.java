package com.hak.wymi.persistance.pojos.unsecure.commenttransaction;

import java.util.List;

public interface CommentTransactionDao {
    boolean save(CommentTransactionAbstract commentTransaction);

    boolean cancel(CommentTransactionAbstract commentTransaction);

    List<CommentTransactionAbstract> getUnprocessed();
}
