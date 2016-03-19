package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.balancetransaction.BalanceTransaction;
import com.hak.wymi.persistance.pojos.balancetransaction.BalanceTransactionDao;
import com.hak.wymi.persistance.pojos.balancetransaction.TransactionState;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.comment.CommentDonationDao;
import com.hak.wymi.persistance.pojos.externaltransaction.TransferTransaction;
import com.hak.wymi.persistance.pojos.externaltransaction.TransferTransactionDao;
import com.hak.wymi.persistance.pojos.post.PostDonationDao;
import com.hak.wymi.persistance.pojos.user.Balance;
import com.hak.wymi.persistance.pojos.user.BalanceDao;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.utility.transactionprocessor.TransactionProcessor;
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

    @Autowired
    private BalanceDao balanceDao;

    @Autowired
    private TransferTransactionDao transferTransactionDao;

    @Autowired
    private TransactionProcessor transactionProcessor;

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

    @Transactional
    public List<BalanceTransaction> getPrivateTransactions(Class transactionTypeClass, String userName, Integer firstResult, Integer maxResults) {
        return balanceTransactionDao.getForUser(transactionTypeClass, userName, firstResult, maxResults);
    }

    @Transactional
    public void createPointsFor(User destinationUser, int amount) throws InvalidValueException {
        Balance siteBalance = balanceDao.get(-1);
        siteBalance.addPoints(amount);
        balanceDao.update(siteBalance);

        TransferTransaction transaction = new TransferTransaction();

        transaction.setState(TransactionState.UNPROCESSED);
        transaction.setSource(siteBalance.getUser());
        transaction.setAmount(amount);
        transaction.setDestination(destinationUser);

        transferTransactionDao.save(transaction);
        transactionProcessor.process(transaction);
    }
}
