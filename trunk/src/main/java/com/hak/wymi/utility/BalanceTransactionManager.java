package com.hak.wymi.utility;

import com.hak.wymi.persistance.pojos.unsecure.commenttransaction.CommentTransaction;
import com.hak.wymi.persistance.pojos.unsecure.commenttransaction.CommentTransactionDao;
import com.hak.wymi.persistance.pojos.unsecure.posttransaction.PostTransaction;
import com.hak.wymi.persistance.pojos.unsecure.posttransaction.PostTransactionDao;
import com.hak.wymi.persistance.pojos.unsecure.transactions.BalanceTransaction;
import com.hak.wymi.persistance.pojos.unsecure.transactions.BalanceTransactionDao;
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
    protected static final Logger logger = LoggerFactory.getLogger(PostTransactionDao.class);

    @Autowired
    private CommentTransactionDao commentTransactionDao;

    @Autowired
    private PostTransactionDao postTransactionDao;

    @Autowired
    private BalanceTransactionDao balanceTransactionDao;

    private BlockingQueue<BalanceTransaction> queue = new LinkedBlockingQueue<>();
    private PriorityBlockingQueue<BalanceTransaction> preprocessQueue = new PriorityBlockingQueue<>(50, new Comparator<BalanceTransaction>() {
        @Override
        public int compare(BalanceTransaction transactionA, BalanceTransaction transactionB) {
            return transactionA.getCreated().compareTo(transactionB.getCreated());
        }
    });

    private boolean run = false;

    public BalanceTransactionManager() {
    }

    @Scheduled(fixedRate = 5000)
    private void checkPreprocessQueue() {
        while (preprocessQueueHasValueToProcess()) {
            queue.add(preprocessQueue.remove());
        }
    }

    private boolean preprocessQueueHasValueToProcess() {
        BalanceTransaction transaction = preprocessQueue.peek();
        return transaction != null && new Date(transaction.getCreated().getTime() + 60000).before(new Date());
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
                logger.error(e.getMessage());
            }
        }
    }

    private void addUnprocessedTransactions() {
        postTransactionDao.getUnprocessed().forEach(this::add);
        commentTransactionDao.getUnprocessed().forEach(this::add);
    }

    private void process(BalanceTransaction t) {
        if (t instanceof PostTransaction) {
            balanceTransactionDao.process((PostTransaction) t);
        } else if (t instanceof CommentTransaction) {
            balanceTransactionDao.process((CommentTransaction) t);
        }
    }

    public void add(BalanceTransaction balanceTransaction) {
        System.out.println(this);
        preprocessQueue.add(balanceTransaction);
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        run = false;
    }
}
