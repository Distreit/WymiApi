package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.comment.Comment;
import com.hak.wymi.persistance.pojos.comment.CommentDao;
import com.hak.wymi.persistance.pojos.comment.CommentDonation;
import com.hak.wymi.persistance.pojos.comment.CommentDonationDao;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.persistance.pojos.user.UserDao;
import com.hak.wymi.utility.TransactionProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentDonationManager {
    @Autowired
    private CommentDonationDao commentDonationDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private TransactionProcessor transactionProcessor;

    @Transactional(rollbackFor = {InvalidValueException.class})
    public void save(CommentDonation commentDonation, String userName, Integer commentId) throws InvalidValueException {
        final Comment comment = commentDao.get(commentId);
        if (comment.getDeleted()) {
            throw new InvalidValueException("Cannot donate to deleted comment.");
        }

        final User user = userDao.getFromName(userName);
        if (comment.getAuthorId().equals(user.getUserId())) {
            throw new InvalidValueException("Cannot donate to your own comment.");
        }

        commentDonation.setComment(comment);
        commentDonation.setSourceUser(user);

        commentDonationDao.save(commentDonation);
        transactionProcessor.add(commentDonation);
    }

    @Transactional
    public List<CommentDonation> get(String topicName) {
        return commentDonationDao.get(topicName);
    }

    @Transactional
    public List<CommentDonation> getUnprocessed() {
        return commentDonationDao.getUnprocessed();
    }
}
