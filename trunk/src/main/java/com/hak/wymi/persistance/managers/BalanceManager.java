package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.user.Balance;
import com.hak.wymi.persistance.pojos.user.BalanceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BalanceManager {
    @Autowired
    private BalanceDao balanceDao;

    @Transactional
    public Balance get(Integer userId) {
        return balanceDao.get(userId);
    }
}
