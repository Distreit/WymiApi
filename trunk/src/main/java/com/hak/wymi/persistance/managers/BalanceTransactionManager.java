package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.balancetransaction.BalanceTransaction;
import com.hak.wymi.persistance.pojos.balancetransaction.BalanceTransactionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BalanceTransactionManager {
    @Autowired
    private BalanceTransactionDao balanceTransactionDao;

    @Transactional
    public boolean process(BalanceTransaction transaction) {
        return balanceTransactionDao.process(transaction);
    }

    @Transactional
    public boolean cancel(BalanceTransaction transaction) {
        return balanceTransactionDao.cancel(transaction);
    }
}
