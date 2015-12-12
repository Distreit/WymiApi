package com.hak.wymi.persistance.pojos.user;

public interface BalanceDao {
    Balance get(Integer userId);

    void update(Balance balance);
}
