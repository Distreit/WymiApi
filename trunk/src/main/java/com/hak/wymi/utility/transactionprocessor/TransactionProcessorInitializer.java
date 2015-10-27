package com.hak.wymi.utility.transactionprocessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TransactionProcessorInitializer {
    @Autowired
    private TransactionProcessor transactionProcessor;

    @PostConstruct
    public void initialize() {
        transactionProcessor.start();
    }
}
