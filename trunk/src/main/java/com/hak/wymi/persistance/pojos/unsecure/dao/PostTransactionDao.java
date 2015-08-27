package com.hak.wymi.persistance.pojos.unsecure.dao;

import com.hak.wymi.persistance.pojos.unsecure.PostTransaction;

import java.util.List;

public interface PostTransactionDao {
    boolean save(PostTransaction postTransaction);

    List<PostTransaction> getUnprocessed();
}
