package com.hak.wymi.persistance.pojos.unsecure.posttransaction;

import java.util.List;

public interface PostTransactionDao {
    public boolean save(PostTransaction postTransaction);

    public List<PostTransaction> getUnprocessed();
}
