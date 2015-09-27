package com.hak.wymi.utility;

import com.hak.wymi.persistance.pojos.balancetransaction.BalanceTransaction;
import com.hak.wymi.persistance.pojos.balancetransaction.BalanceTransactionDao;
import com.hak.wymi.persistance.pojos.balancetransaction.TransactionState;
import com.hak.wymi.persistance.pojos.comment.CommentCreationDao;
import com.hak.wymi.persistance.pojos.comment.CommentDonationDao;
import com.hak.wymi.persistance.pojos.post.PostCreationDao;
import com.hak.wymi.persistance.pojos.post.PostDonationDao;
import com.hak.wymi.persistance.pojos.user.User;
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
    public static final int TRANSACTION_WAIT_PERIOD = 5000;
    private static final Logger LOGGER = LoggerFactory.getLogger(BalanceTransactionManager.class);
    private static final int QUEUE_START_SIZE = 50;

    private final BlockingQueue<BalanceTransaction> queue = new LinkedBlockingQueue<>();

    private final PriorityBlockingQueue<BalanceTransaction> preprocessQueue = new PriorityBlockingQueue<>(QUEUE_START_SIZE, (first, second) -> first.getCreated().compareTo(second.getCreated()));

    private final ConcurrentMap<Integer, Set<BalanceTransaction>> userTransactions = new ConcurrentHashMap<>();

    @Autowired
    private CommentDonationDao commentDonationDao;

    @Autowired
    private PostDonationDao postDonationDao;

    @Autowired
    private BalanceTransactionDao balanceTransactionDao;

    @Autowired
    private CommentCreationDao commentCreationDao;

    @Autowired
    private PostCreationDao postCreationDao;
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
        postDonationDao.getUnprocessed().forEach(this::add);
        commentDonationDao.getUnprocessed().forEach(this::add);

        commentCreationDao.getUnprocessed().forEach(this::addToProcessQueue);
        postCreationDao.getUnprocessed().forEach(this::addToProcessQueue);
    }

    public boolean process(BalanceTransaction transaction) {
        final Integer userId = transaction.getSourceUserId();
        if (userTransactions.containsKey(userId)) {
            userTransactions.get(userId).remove(transaction);
        }
        if (transaction.getState() == TransactionState.UNPROCESSED) {
            return balanceTransactionDao.process(transaction);
        } else if (LOGGER.isErrorEnabled()) {
            LOGGER.error("Transaction without UNPROCESSED state trying to be processed. {}",
                    JSONConverter.getJSON(transaction, Boolean.TRUE));
        }
        return false;
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

    public boolean cancel(User user, BalanceTransaction transaction) {
        return transaction.getSourceUser().getUserId().equals(user.getUserId()) && balanceTransactionDao.cancel(transaction);
    }
}
