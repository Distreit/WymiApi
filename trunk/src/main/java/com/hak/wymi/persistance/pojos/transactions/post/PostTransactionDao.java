package com.hak.wymi.persistance.pojos.transactions.post;

import java.util.List;

public interface PostTransactionDao {
    boolean save(PostTransaction postTransaction);

    List<PostTransaction> getUnprocessed();
}
