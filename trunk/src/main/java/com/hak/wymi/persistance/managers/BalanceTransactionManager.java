package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.balancetransaction.BalanceTransaction;
import com.hak.wymi.persistance.pojos.balancetransaction.BalanceTransactionDao;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.comment.CommentDonationDao;
import com.hak.wymi.persistance.pojos.post.PostDonationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Service
public class BalanceTransactionManager {
    @Autowired
    private BalanceTransactionDao balanceTransactionDao;

    @Autowired
    private PostDonationDao postDonationDao;

    @Autowired
    private CommentDonationDao commentDonationDao;

    @Transactional(rollbackFor = {InvalidValueException.class})
    public void process(BalanceTransaction transaction) throws InvalidValueException {
        balanceTransactionDao.process(transaction);
    }

    @Transactional(rollbackFor = {InvalidValueException.class})
    public boolean cancel(BalanceTransaction transaction) throws InvalidValueException {
        return balanceTransactionDao.cancel(transaction);
    }

    @Transactional
    public List<BalanceTransaction> getUnprocessedTransactions() {
        final List<BalanceTransaction> unprocessedTransactions = new LinkedList<>();
        unprocessedTransactions.addAll(postDonationDao.getUnprocessed());
        unprocessedTransactions.addAll(commentDonationDao.getUnprocessed());
        return unprocessedTransactions;
    }
}
