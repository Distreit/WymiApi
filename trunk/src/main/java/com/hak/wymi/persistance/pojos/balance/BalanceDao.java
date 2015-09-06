package com.hak.wymi.persistance.pojos.balance;

import java.security.Principal;

public interface BalanceDao {
    Balance get(Principal principal);
}
