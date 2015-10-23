package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InsufficientFundsException;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.comment.CommentCreation;
import com.hak.wymi.persistance.pojos.comment.CommentCreationDao;
import com.hak.wymi.utility.TransactionProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentCreationManager {
    @Autowired
    private CommentCreationDao commentCreationDao;

    @Autowired
    private TransactionProcessor transactionProcessor;

    @Transactional(rollbackFor = {InsufficientFundsException.class, InvalidValueException.class})
    public void save(CommentCreation transaction) throws InsufficientFundsException, InvalidValueException {
        commentCreationDao.save(transaction);
        transactionProcessor.process(transaction);
    }

    @Transactional
    public List<CommentCreation> getUnprocessed() {
        return commentCreationDao.getUnprocessed();
    }
}
