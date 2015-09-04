package com.hak.wymi.persistance.pojos.transactions.comment;

import java.util.List;

public interface CommentTransactionDao {
    boolean save(CommentTransaction commentTransaction);

    List<CommentTransaction> getUnprocessed();
}
