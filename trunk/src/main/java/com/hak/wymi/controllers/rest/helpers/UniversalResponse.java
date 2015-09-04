package com.hak.wymi.controllers.rest.helpers;

import com.fasterxml.jackson.annotation.JsonValue;
import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.pojos.secure.SecureBalance;
import com.hak.wymi.persistance.pojos.secure.SecureTransaction;
import com.hak.wymi.persistance.pojos.unsecure.Balance;
import com.hak.wymi.persistance.pojos.unsecure.BalanceTransaction;
import com.hak.wymi.persistance.pojos.unsecure.User;
import com.hak.wymi.persistance.pojos.unsecure.dao.BalanceDao;
import com.hak.wymi.persistance.utility.BalanceTransactionManager;

import java.security.Principal;
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

    public UniversalResponse setData(List<SecureToSend> secureToSend) {
        this.entries.put(DATA, secureToSend);
        return this;
    }

    public UniversalResponse setData(SecureToSend secureToSend) {
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

    public UniversalResponse addTransactions(Principal principal, User user, BalanceTransactionManager balanceTransactionManager, BalanceDao balanceDao) {
        if (principal.getName().equalsIgnoreCase(user.getName())) {
            final Set<BalanceTransaction> userTransactions = balanceTransactionManager.getTransactionsForUser(user.getUserId());

            if (userTransactions != null) {
                this.addTransactions(userTransactions.stream()
                        .map(SecureTransaction::new)
                        .collect(Collectors.toCollection(HashSet::new)));
            }
            if (balanceDao != null) {
                this.addBalance(principal, balanceDao);
            }
        }
        return this;
    }

    public UniversalResponse addBalance(SecureBalance balance) {
        this.entries.put(BALANCE, balance.getCurrentBalance());
        return this;
    }

    public UniversalResponse addBalance(Principal principal, BalanceDao balanceDao) {
        final Balance balance = balanceDao.get(principal);
        if (balance != null) {
            this.addBalance(new SecureBalance(balance));
        }
        return this;
    }
}
