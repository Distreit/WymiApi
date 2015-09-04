package com.hak.wymi.persistance.utility;

import com.hak.wymi.persistance.pojos.unsecure.BalanceTransaction;
import com.hak.wymi.persistance.pojos.unsecure.TransactionState;
import com.hak.wymi.persistance.pojos.unsecure.User;
import com.hak.wymi.persistance.pojos.unsecure.dao.BalanceTransactionDao;
import com.hak.wymi.persistance.pojos.unsecure.dao.CommentTransactionDao;
import com.hak.wymi.persistance.pojos.unsecure.dao.PostTransactionDao;
import com.hak.wymi.utility.JSONConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

@Service
public class BalanceTransactionManager {
    public static final int TRANSACTION_WAIT_PERIOD = 15000;
    private static final Logger LOGGER = LoggerFactory.getLogger(BalanceTransactionManager.class);
    private static final int QUEUE_START_SIZE = 50;
    private final BlockingQueue<BalanceTransaction> queue = new LinkedBlockingQueue<>();
    private final PriorityBlockingQueue<BalanceTransaction> preprocessQueue = new PriorityBlockingQueue<>(QUEUE_START_SIZE, (first, second) -> first.getCreated().compareTo(second.getCreated()));
    private final ConcurrentMap<Integer, Set<BalanceTransaction>> userTransactions = new ConcurrentHashMap<>();
    @Autowired
    private CommentTransactionDao commentTransactionDao;
    @Autowired
    private PostTransactionDao postTransactionDao;
    @Autowired
    private BalanceTransactionDao balanceTransactionDao;
    private boolean processQueue;

    @Scheduled(fixedRate = 5000)
    public void checkPreprocessQueue() {
        while (preprocessQueueHasValueToProcess()) {
            queue.add(preprocessQueue.remove());
        }
    }

    private boolean preprocessQueueHasValueToProcess() {
        final BalanceTransaction transaction = preprocessQueue.peek();
        return transaction != null && new Date(transaction.getCreated().getTime() + TRANSACTION_WAIT_PERIOD).before(new Date());
    }

    @Async
    public void start() {
        addUnprocessedTransactions();
        processQueue = true;
        this.run();
    }

    public void run() {
        while (processQueue) {
            try {
                process(queue.take());
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    private void addUnprocessedTransactions() {
        postTransactionDao.getUnprocessed().forEach(this::add);
        commentTransactionDao.getUnprocessed().forEach(this::add);
    }

    private void process(BalanceTransaction transaction) {
        final Integer userId = transaction.getSourceUserId();
        if (userTransactions.containsKey(userId)) {
            userTransactions.get(userId).remove(transaction);
        }
        if (transaction.getState() == TransactionState.UNPROCESSED) {
            balanceTransactionDao.process(transaction);
        } else if (LOGGER.isErrorEnabled()) {
            LOGGER.error("Transaction without UNPROCESSED state trying to be processed. {}", JSONConverter.getJSON(transaction, true));
        }

    }

    public void add(BalanceTransaction transaction) {
        addToUserMap(transaction);
        preprocessQueue.add(transaction);
    }

    private void addToUserMap(BalanceTransaction transaction) {
        final Integer userId = transaction.getSourceUserId();
        if (!userTransactions.containsKey(userId)) {
            userTransactions.put(userId, new HashSet<>());
        }

        userTransactions.get(userId).add(transaction);
    }

    public Set<BalanceTransaction> getTransactionsForUser(Integer userId) {
        return userTransactions.get(userId);
    }

    public boolean cancel(User user, int transactionId) {
        final boolean[] result = {false};
        userTransactions.get(user.getUserId())
                .stream()
                .filter(transaction ->
                        transaction.getTransactionId().equals(transactionId)
                                && transaction.getState() == TransactionState.UNPROCESSED)
                .findFirst()
                .ifPresent(transaction -> {
                    if (preprocessQueue.remove(transaction)) {
                        userTransactions.get(user.getUserId()).remove(transaction);
                        balanceTransactionDao.cancel(transaction);
                        result[0] = true;
                    }
                });
        return result[0];
    }
}
