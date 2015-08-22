package com.hak.wymi.persistance.pojos.unsecure.commenttransaction;

import java.util.List;

public interface CommentTransactionDao {
    public boolean save(CommentTransaction commentTransaction);

    public boolean cancel(CommentTransaction commentTransaction);

    public List<CommentTransaction> getUnprocessed();
}
