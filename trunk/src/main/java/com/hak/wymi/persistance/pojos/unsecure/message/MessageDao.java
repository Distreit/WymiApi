package com.hak.wymi.persistance.pojos.unsecure.message;

import com.hak.wymi.persistance.pojos.unsecure.balance.Balance;

import java.security.Principal;
import java.util.List;

public interface MessageDao {
    public boolean save(Message message);

    List<Balance> getIncoming(Principal principal);

    List<Balance> getSent(Principal principal);
}
