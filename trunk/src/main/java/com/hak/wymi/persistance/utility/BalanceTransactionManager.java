package com.hak.wymi.persistance.utility;

import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.pojos.secure.SecureBalance;
import com.hak.wymi.persistance.pojos.secure.SecureTransaction;
import com.hak.wymi.persistance.pojos.unsecure.Balance;
import com.hak.wymi.persistance.pojos.unsecure.BalanceTransaction;
import com.hak.wymi.persistance.pojos.unsecure.User;
import com.hak.wymi.persistance.pojos.unsecure.dao.BalanceDao;
import com.hak.wymi.persistance.pojos.unsecure.dao.BalanceTransactionDao;
import com.hak.wymi.persistance.pojos.unsecure.dao.CommentTransactionDao;
import com.hak.wymi.persistance.pojos.unsecure.dao.PostTransactionDao;
import com.hak.wymi.persistance.pojos.unsecure.dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

@Service
public class BalanceTransactionManager {
    public static final int TRANSACTION_WAIT_PERIOD = 60000;
    private static final Logger LOGGER = LoggerFactory.getLogger(BalanceTransactionManager.class);
    private static final int QUEUE_START_SIZE = 50;

    private final BlockingQueue<BalanceTransaction> queue = new LinkedBlockingQueue<>();
    private final PriorityBlockingQueue<BalanceTransaction> preprocessQueue = new PriorityBlockingQueue<>(QUEUE_START_SIZE, (first, second) -> first.getCreated().compareTo(second.getCreated()));

    private final ConcurrentMap<Integer, Set<BalanceTransaction>> userTransactions = new ConcurrentHashMap<>();
    @Autowired
    BalanceDao balanceDao;
    @Autowired
    UserDao userDao;
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

        balanceTransactionDao.process(transaction);
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

    public void addTransactionsToResponse(Principal principal, UniversalResponse universalResponse) {
        final User user = userDao.get(principal);
        this.addTransactionsToResponse(principal, universalResponse, user);
    }

    public void addTransactionsToResponse(Principal principal, UniversalResponse universalResponse, User user) {
        if (principal.getName().equalsIgnoreCase(user.getName())) {
            final Set<BalanceTransaction> userTransactions = this.getTransactionsForUser(user.getUserId());

            if (userTransactions != null) {
                universalResponse.addTransactions(userTransactions.stream()
                        .map(SecureTransaction::new)
                        .collect(Collectors.toCollection(HashSet::new)));
            }
            this.addBalanceToResponse(principal, universalResponse);
        }
    }

    public void addBalanceToResponse(Principal principal, UniversalResponse universalResponse) {
        final Balance balance = balanceDao.get(principal);
        if (balance != null) {
            universalResponse.addBalance(new SecureBalance(balance));
        }
    }
}
