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
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
    private boolean run = false;

    public BalanceTransactionManager() {
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
                e.printStackTrace();
                continue;
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
        queue.add(balanceTransaction);
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        run = false;
    }
}
