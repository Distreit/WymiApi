package com.hak.wymi.utility;

import com.hak.wymi.persistance.managers.BalanceTransactionManager;
import com.hak.wymi.persistance.managers.CommentDonationManager;
import com.hak.wymi.persistance.managers.PostDonationManager;
import com.hak.wymi.persistance.pojos.balancetransaction.BalanceTransaction;
import com.hak.wymi.persistance.pojos.balancetransaction.BalanceTransactionCanceller;
import com.hak.wymi.persistance.pojos.balancetransaction.TransactionState;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.user.User;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

@Service
public class TransactionProcessor {
    public static final int TRANSACTION_WAIT_PERIOD = 5000;
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionProcessor.class);
    private static final int QUEUE_START_SIZE = 50;

    private final BlockingQueue<BalanceTransaction> queue = new LinkedBlockingQueue<>();

    private final PriorityBlockingQueue<BalanceTransaction> preprocessQueue = new PriorityBlockingQueue<>(QUEUE_START_SIZE, (first, second) -> first.getCreated().compareTo(second.getCreated()));

    private final ConcurrentMap<Integer, Set<BalanceTransaction>> userTransactions = new ConcurrentHashMap<>();

    @Autowired
    private CommentDonationManager commentDonationManager;

    @Autowired
    private PostDonationManager postDonationManager;

    @Autowired
    private BalanceTransactionManager balanceTransactionManager;

    @Autowired
    private BalanceTransactionCanceller balanceTransactionCanceller;

    private boolean processQueue;

    @Scheduled(fixedRate = 5000)
    public void checkPreprocessQueue() {
        while (preprocessQueueHasValueToProcess()) {
            addToProcessQueue(preprocessQueue.remove());
        }
    }

    public void addToProcessQueue(BalanceTransaction balanceTransaction) {
        queue.add(balanceTransaction);
    }

    private boolean preprocessQueueHasValueToProcess() {
        final BalanceTransaction transaction = preprocessQueue.peek();
        return transaction != null && transaction.getCreated().plusMillis(TRANSACTION_WAIT_PERIOD).isBefore(new DateTime());
    }

    @Async
    public void start() {
        addUnprocessedTransactions();
        processQueue = true;
        this.run();
    }

    public void run() {
        while (processQueue) {
            BalanceTransaction transaction;
            try {
                transaction = queue.take();
                processTransactionFromQueue(transaction);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    private void processTransactionFromQueue(BalanceTransaction transaction) {
        try {
            process(transaction);
        } catch (InvalidValueException e) {
            cancel(transaction);
        }
    }

    private void cancel(BalanceTransaction transaction) {
        try {
            balanceTransactionCanceller.cancel(transaction);
        } catch (InvalidValueException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void addUnprocessedTransactions() {
        postDonationManager.getUnprocessed().forEach(this::add);
        commentDonationManager.getUnprocessed().forEach(this::add);
    }

    public void process(BalanceTransaction transaction) throws InvalidValueException {
        final Integer balanceId = transaction.getSource().getBalanceId();
        if (userTransactions.containsKey(balanceId)) {
            userTransactions.get(balanceId).remove(transaction);
        }
        if (transaction.getState() == TransactionState.UNPROCESSED) {
            balanceTransactionManager.process(transaction);
        } else {
            LOGGER.error("Transaction without UNPROCESSED state trying to be processed. {}",
                    JSONConverter.getJSON(transaction, Boolean.TRUE));
        }
    }

    public void add(BalanceTransaction transaction) {
        addToUserMap(transaction);
        preprocessQueue.add(transaction);
    }

    private void addToUserMap(BalanceTransaction transaction) {
        final Integer balanceId = transaction.getSource().getBalanceId();
        if (!userTransactions.containsKey(balanceId)) {
            userTransactions.put(balanceId, new HashSet<>());
        }

        userTransactions.get(balanceId).add(transaction);
    }

    public Set<BalanceTransaction> getTransactionsForUser(Integer userId) {
        return userTransactions.get(userId);
    }

    @Transactional(rollbackFor = {InvalidValueException.class})
    public void cancel(User user, int transactionId) throws InvalidValueException {
        BalanceTransaction transaction = userTransactions.get(user.getUserId())
                .stream()
                .filter(t ->
                        t.getTransactionId().equals(transactionId) && t.getState() == TransactionState.UNPROCESSED)
                .findFirst()
                .get();

        if (transaction != null && preprocessQueue.remove(transaction)) {
            userTransactions.get(user.getUserId()).remove(transaction);
            balanceTransactionManager.cancel(transaction);
        }
    }

    public boolean cancel(User user, BalanceTransaction transaction) throws InvalidValueException {
        return transaction.getSource().getBalanceId().equals(user.getBalance().getBalanceId()) && balanceTransactionManager.cancel(transaction);
    }
}
