package com.hak.wymi.controllers.rest.helpers;

import com.fasterxml.jackson.annotation.JsonValue;
import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.managers.BalanceManager;
import com.hak.wymi.persistance.pojos.balancetransaction.BalanceTransaction;
import com.hak.wymi.persistance.pojos.balancetransaction.SecureBalanceTransaction;
import com.hak.wymi.persistance.pojos.user.Balance;
import com.hak.wymi.persistance.pojos.user.SecureBalance;
import com.hak.wymi.utility.TransactionProcessor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class UniversalResponse {
    private static final String DATA = "data";
    private static final String ERRORS = "errors";
    private static final String MESSAGES = "messages";
    private static final String TRANSACTIONS = "transactions";
    private static final String BALANCE = "balance";
    private static final int INITIAL_SIZE = 5;

    private final ConcurrentMap<String, Object> entries;

    private ErrorList errorList;

    private LinkedList<String> messageList;

    public UniversalResponse() {
        this.entries = new ConcurrentHashMap<>(INITIAL_SIZE);
    }

    @JsonValue
    public ConcurrentMap<String, Object> getData() {
        return entries;
    }

    public UniversalResponse setData(int value) {
        final ConcurrentMap<String, Object> container = new ConcurrentHashMap<>();
        container.put("value", value);
        this.entries.put(DATA, container);
        return this;
    }

    public UniversalResponse setData(SecureToSend secureToSend) {
        this.entries.put(DATA, secureToSend);
        return this;
    }

    public UniversalResponse setData(List<SecureToSend> secureToSend) {
        this.entries.put(DATA, secureToSend);
        return this;
    }

    public UniversalResponse addUnknownError() {
        this.addError(new ResponseError("Unknown error"));
        return this;
    }

    public UniversalResponse addError(String errorMessage) {
        this.addError(new ResponseError(errorMessage));
        return this;
    }

    public UniversalResponse addError(ResponseError responseError) {
        if (!this.entries.containsKey(ERRORS)) {
            errorList = new ErrorList();
            this.entries.put(ERRORS, errorList);
        }
        errorList.add(responseError);
        return this;
    }

    public UniversalResponse addMessage(String message) {
        if (!this.entries.containsKey(MESSAGES)) {
            messageList = new LinkedList<>();
            this.entries.put(MESSAGES, messageList);
        }
        messageList.add(message);
        return this;
    }

    public UniversalResponse addTransactions(Set<SecureToSend> transactionsForUser) {
        this.entries.put(TRANSACTIONS, transactionsForUser);
        return this;
    }

    public UniversalResponse addTransactions(Integer userId, TransactionProcessor transactionProcessor, BalanceManager balanceManager) {
        final Set<BalanceTransaction> userTransactions = transactionProcessor.getTransactionsForUser(userId);

        if (userTransactions != null) {
            this.addTransactions(userTransactions.stream()
                    .map(SecureBalanceTransaction::new)
                    .collect(Collectors.toCollection(HashSet::new)));
        }
        if (balanceManager != null) {
            this.addBalance(userId, balanceManager);
        }
        return this;
    }

    public UniversalResponse addBalance(SecureBalance balance) {
        this.entries.put(BALANCE, balance.getCurrentBalance());
        return this;
    }

    public UniversalResponse addBalance(Integer userId, BalanceManager balanceManager) {
        final Balance balance = balanceManager.get(userId);
        if (balance != null) {
            this.addBalance(new SecureBalance(balance));
        }
        return this;
    }
}
