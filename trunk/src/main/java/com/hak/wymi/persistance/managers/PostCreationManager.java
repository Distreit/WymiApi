package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.post.PostCreation;
import com.hak.wymi.persistance.pojos.post.PostCreationDao;
import com.hak.wymi.utility.transactionprocessor.TransactionProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostCreationManager {
    @Autowired
    private PostCreationDao postCreationDao;

    @Autowired
    private TransactionProcessor transactionProcessor;

    @Transactional(rollbackFor = {InvalidValueException.class})
    public void save(PostCreation transaction) throws InvalidValueException {
        postCreationDao.save(transaction);
        transactionProcessor.process(transaction);
    }

    @Transactional
    public List<PostCreation> getUnprocessed() {
        return postCreationDao.getUnprocessed();
    }
}
