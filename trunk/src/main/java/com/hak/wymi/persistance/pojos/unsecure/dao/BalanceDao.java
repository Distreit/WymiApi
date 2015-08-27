package com.hak.wymi.persistance.pojos.unsecure.dao;

import com.hak.wymi.persistance.pojos.unsecure.Balance;

import java.security.Principal;

public interface BalanceDao {
    boolean save(Balance post);

    Balance get(Principal principal);
}
