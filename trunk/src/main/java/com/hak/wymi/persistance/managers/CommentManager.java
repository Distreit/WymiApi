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
import com.hak.wymi.utility.jsonconverter.JSONConverter;
import com.hak.wymi.utility.transactionprocessor.TransactionProcessor;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
public class CommentManager {
    private static final int MILLISECONDS_IN_A_SECOND = 1000;

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

    @Value("${score.baseTime}")
    private Integer baseTime;

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

            comment.setBase(getBaseTime());
            comment.setPoints(0);
            comment.setDonations(0);
            comment.setDeleted(false);
            comment.setTrashed(false);

            commentCreationDao.save(transaction);
            transactionProcessor.process(transaction);
        } else {
            throw new InvalidValueException(String.format("Topic fees do not match.%nFee Flat: %d%nFee percent: %d%nTopic: %s",
                    feeFlat, feePercent, JSONConverter.getJSON(topic, true)));
        }
    }

    @Transactional
    public void update(Comment comment, String userName) {
        final Comment dbComment = commentDao.get(comment.getCommentId());

        if (dbComment.getAuthor().getName().equals(userName)) {
            dbComment.setContent(comment.getContent());
            commentDao.update(dbComment);
        } else {
            throw new UnsupportedOperationException("User not allowed to update comment.");
        }
    }

    @Transactional
    public void updateTrashed(Integer commentId, Boolean trashed, String userName) {
        final Comment comment = get(commentId);
        if (comment.getPost().getTopic().getOwner().getName().equals(userName) && comment.getTrashed() != trashed) {
            if (!trashed) {
                comment.setScore(comment.getScore() - comment.getBase() + getBaseTime());
            }
            comment.setTrashed(trashed);
            commentDao.update(comment);
        } else {
            throw new UnsupportedOperationException("User is not authorized to update trashed status of post.");
        }
    }

    public double getBaseTime() {
        return new DateTime().getMillis() / MILLISECONDS_IN_A_SECOND - baseTime;
    }
}
