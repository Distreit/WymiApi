package com.hak.wymi.persistance.pojos.unsecure.posttransaction;

import javax.xml.bind.ValidationException;

public interface PostTransactionDao {
    public boolean save(PostTransaction postTransaction) throws ValidationException;
}
