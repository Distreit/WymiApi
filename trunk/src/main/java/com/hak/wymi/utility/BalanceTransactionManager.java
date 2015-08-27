package com.hak.wymi.utility;

import com.hak.wymi.persistance.pojos.unsecure.BalanceTransaction;
import com.hak.wymi.persistance.pojos.unsecure.CommentTransaction;
import com.hak.wymi.persistance.pojos.unsecure.PostTransaction;
import com.hak.wymi.persistance.pojos.unsecure.dao.CommentTransactionDao;
import com.hak.wymi.persistance.pojos.unsecure.dao.PostTransactionDao;
import com.hak.wymi.persistance.pojos.unsecure.BalanceTransaction;
import com.hak.wymi.persistance.pojos.unsecure.dao.BalanceTransactionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class BalanceTransactionManager implements Runnable, ApplicationListener<ContextClosedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BalanceTransactionManager.class);
    private static final int ONE_MINUTE = 60000;

    @Autowired
    private CommentTransactionDao commentTransactionDao;

    @Autowired
    private PostTransactionDao postTransactionDao;

    @Autowired
    private BalanceTransactionDao balanceTransactionDao;

    private final BlockingQueue<BalanceTransaction> queue = new LinkedBlockingQueue<>();
    private final PriorityBlockingQueue<BalanceTransaction> preprocessQueue = new PriorityBlockingQueue<>(50, new Comparator<BalanceTransaction>() {
        @Override
        public int compare(BalanceTransaction transactionA, BalanceTransaction transactionB) {
            return transactionA.getCreated().compareTo(transactionB.getCreated());
        }
    });

    private boolean run;

    public BalanceTransactionManager() {
        // Needed for bean creation.
    }

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

    @PostConstruct
    public void start() {
        addUnprocessedTransactions();
        run = true;
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (run) {
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

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        run = false;
    }
}
