package com.hak.wymi.persistance.pojos.user;

import java.security.Principal;

public interface BalanceDao {
    Balance get(Principal principal);
}
