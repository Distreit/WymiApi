package com.hak.wymi.persistance.pojos.unsecure.balance;

import java.security.Principal;

public interface BalanceDao {
    public boolean save(Balance post);

    Balance get(Principal principal);
}
