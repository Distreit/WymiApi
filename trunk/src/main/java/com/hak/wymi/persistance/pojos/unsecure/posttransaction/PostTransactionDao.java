package com.hak.wymi.persistance.pojos.unsecure.posttransaction;

import java.util.List;

public interface PostTransactionDao {
    boolean save(PostTransactionAbstract postTransaction);

    boolean cancel(PostTransactionAbstract postTransaction);

    List<PostTransactionAbstract> getUnprocessed();
}
