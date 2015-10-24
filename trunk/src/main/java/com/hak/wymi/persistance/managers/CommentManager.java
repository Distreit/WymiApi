package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.balancetransaction.TransactionState;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.comment.Comment;
import com.hak.wymi.persistance.pojos.comment.CommentCreation;
import com.hak.wymi.persistance.pojos.comment.CommentCreationDao;
import com.hak.wymi.persistance.pojos.comment.CommentDao;
import com.hak.wymi.persistance.pojos.post.Post;
import com.hak.wymi.persistance.pojos.post.PostDao;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.persistance.pojos.user.UserDao;
import com.hak.wymi.utility.TransactionProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
public class CommentManager {
    @Autowired
    private CommentDao commentDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CommentCreationDao commentCreationDao;

    @Autowired
    private TransactionProcessor transactionProcessor;

    @Transactional
    public Comment get(Integer commentId) {
        return commentDao.get(commentId);
    }

    @Transactional
    public List<Comment> getAll(Integer postId) {
        return commentDao.getAll(postId);
    }

    @Transactional
    public void delete(Integer commentId, Principal principal) {
        commentDao.delete(commentId, principal);
    }

    @Transactional(rollbackFor = {InvalidValueException.class})
    public void create(Comment comment, String userName, Integer postId, Integer feeFlat, Integer feePercent, Integer parentCommentId)
            throws InvalidValueException {
        final Post post = postDao.get(postId);
        final Topic topic = post.getTopic();

        if (feeFlat.equals(topic.getFeeFlat()) && feePercent.equals(topic.getFeePercent())) {
            final User user = userDao.getFromName(userName);
            final CommentCreation transaction = new CommentCreation();
            transaction.setFeePercent(topic.getFeePercent());
            transaction.setFeeFlat(topic.getFeeFlat());
            transaction.setState(TransactionState.UNPROCESSED);
            transaction.setComment(comment);

            comment.setAuthor(user);
            comment.setPost(post);
            if (parentCommentId != null) {
                comment.setParentComment(commentDao.get(parentCommentId));
            }
            comment.setDeleted(Boolean.FALSE);
            comment.setPoints(0);
            comment.setDonations(0);
            commentCreationDao.save(transaction);
            transactionProcessor.process(transaction);
        } else {
            throw new InvalidValueException("Topic fees do not match.");
        }
    }
}
