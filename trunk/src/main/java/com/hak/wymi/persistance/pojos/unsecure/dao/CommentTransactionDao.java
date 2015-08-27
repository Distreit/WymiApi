package com.hak.wymi.persistance.pojos.unsecure.dao;

import com.hak.wymi.persistance.pojos.unsecure.CommentTransaction;

import java.util.List;

public interface CommentTransactionDao {
    boolean save(CommentTransaction commentTransaction);

    List<CommentTransaction> getUnprocessed();
}
