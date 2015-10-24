package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.balancetransaction.BalanceTransaction;
import com.hak.wymi.persistance.pojos.balancetransaction.BalanceTransactionDao;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BalanceTransactionManager {
    @Autowired
    private BalanceTransactionDao balanceTransactionDao;

    @Transactional(rollbackFor = {InvalidValueException.class})
    public void process(BalanceTransaction transaction) throws InvalidValueException {
        balanceTransactionDao.process(transaction);
    }

    @Transactional(rollbackFor = {InvalidValueException.class})
    public boolean cancel(BalanceTransaction transaction) throws InvalidValueException {
        return balanceTransactionDao.cancel(transaction);
    }
}
