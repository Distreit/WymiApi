package com.hak.wymi.persistance.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class BTMInitializer {
    @Autowired
    private BalanceTransactionManager balanceTransactionManager;

    @PostConstruct
    public void initialize() {
        balanceTransactionManager.start();
    }
}
