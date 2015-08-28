package com.hak.wymi.persistance.utility;

import com.hak.wymi.persistance.pojos.unsecure.BalanceTransaction;
import com.hak.wymi.persistance.pojos.unsecure.dao.BalanceTransactionDao;
import com.hak.wymi.persistance.pojos.unsecure.dao.CommentTransactionDao;
import com.hak.wymi.persistance.pojos.unsecure.dao.PostTransactionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

@Service
public class BalanceTransactionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(BalanceTransactionManager.class);

    private static final int ONE_MINUTE = 60000;
    private static final int QUEUE_START_SIZE = 50;

    private final BlockingQueue<BalanceTransaction> queue = new LinkedBlockingQueue<>();
    private final PriorityBlockingQueue<BalanceTransaction> preprocessQueue = new PriorityBlockingQueue<>(QUEUE_START_SIZE, (first, second) -> first.getCreated().compareTo(second.getCreated()));

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
        return transaction != null && new Date(transaction.getCreated().getTime() + ONE_MINUTE).before(new Date());
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
        balanceTransactionDao.process(transaction);
    }

    public void add(BalanceTransaction balanceTransaction) {
        preprocessQueue.add(balanceTransaction);
    }
}
