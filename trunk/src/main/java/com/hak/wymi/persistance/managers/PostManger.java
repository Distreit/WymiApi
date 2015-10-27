package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.balancetransaction.TransactionState;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.post.Post;
import com.hak.wymi.persistance.pojos.post.PostCreation;
import com.hak.wymi.persistance.pojos.post.PostCreationDao;
import com.hak.wymi.persistance.pojos.post.PostDao;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.topic.TopicDao;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.persistance.pojos.user.UserDao;
import com.hak.wymi.utility.jsonconverter.JSONConverter;
import com.hak.wymi.utility.transactionprocessor.TransactionProcessor;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostManger {
    private static final int MILLISECONDS_IN_A_SECOND = 1000;

    @Value("${score.baseTime}")
    private Integer baseTime;

    @Value("${site.domain}")
    private String siteDomain;

    @Autowired
    private PostDao postDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TopicDao topicDao;

    @Autowired
    private PostCreationDao postCreationDao;

    @Autowired
    private TransactionProcessor transactionProcessor;

    @Transactional
    public List<Post> get(String topicName, Integer firstResult, int min) {
        return postDao.get(topicName, firstResult, min);
    }

    @Transactional
    public Post get(Integer postId) {
        return postDao.get(postId);
    }

    @Transactional
    public List<Post> get(List<String> topicList, Integer firstResult, Integer maxResults, Boolean filter, boolean trashed) {
        return postDao.get(topicList, firstResult, maxResults, filter, trashed);
    }

    @Transactional
    public void updateTrashed(int postId, boolean trashed, String userName) {
        final Post post = get(postId);
        if (post.getTopic().getOwner().getName().equals(userName) && post.getTrashed() != trashed) {
            if (!trashed) {
                post.setScore(post.getScore() - post.getBase() + getBaseTime());
            }
            post.setTrashed(trashed);
            postDao.update(post);
        } else {
            throw new UnsupportedOperationException("User is not authorized to update trashed status of post.");
        }
    }

    @Transactional(rollbackFor = {InvalidValueException.class})
    public void create(Post post, String topicName, String userName, Integer feeFlat, Integer feePercent)
            throws InvalidValueException {
        final User user = userDao.getFromName(userName);
        final Topic topic = topicDao.get(topicName);

        if (feePercent.equals(topic.getFeePercent()) && feeFlat.equals(topic.getFeeFlat())) {
            final PostCreation transaction = new PostCreation();
            transaction.setState(TransactionState.UNPROCESSED);
            transaction.setFeeFlat(topic.getFeeFlat());
            transaction.setFeePercent(topic.getFeePercent());
            transaction.setPost(post);

            post.setTopic(topic);
            post.setUser(user);
            post.setBase(getBaseTime());
            post.setScore(post.getBase());

            postCreationDao.save(transaction);
            transactionProcessor.process(transaction);
        } else {
            throw new InvalidValueException(String.format("Topic fees do not match.\nFee Flat: %d\nFee percent: %d\nTopic: %s",
                    feeFlat, feePercent, JSONConverter.getJSON(topic, true)));
        }
    }

    @Transactional
    public void delete(int postId, String userName) {
        final Post post = get(postId);
        if (!post.getDeleted() && post.getUser().getName().equals(userName)) {
            post.setDeleted(true);
            post.setTitle("DELETED");
            post.setHref("http://siteDomain");
            post.setText("DELETED");
            postDao.update(post);
        } else {
            throw new UnsupportedOperationException("User not allowed to delete post.");
        }
    }

    public double getBaseTime() {
        return new DateTime().getMillis() / MILLISECONDS_IN_A_SECOND - baseTime;
    }
}
