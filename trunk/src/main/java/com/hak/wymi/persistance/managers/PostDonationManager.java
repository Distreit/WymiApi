package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.post.Post;
import com.hak.wymi.persistance.pojos.post.PostDao;
import com.hak.wymi.persistance.pojos.post.PostDonation;
import com.hak.wymi.persistance.pojos.post.PostDonationDao;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.persistance.pojos.user.UserDao;
import com.hak.wymi.utility.transactionprocessor.TransactionProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostDonationManager {
    @Autowired
    private PostDonationDao postDonationDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private TransactionProcessor transactionProcessor;

    @Transactional
    public List<PostDonation> get(String topicName) {
        return postDonationDao.get(topicName);
    }

    @Transactional
    public List<PostDonation> getUnprocessed() {
        return postDonationDao.getUnprocessed();
    }

    @Transactional(rollbackFor = InvalidValueException.class)
    public void save(PostDonation postDonation, String userName, Integer postId) throws InvalidValueException {
        final User user = userDao.getFromName(userName);
        final Post post = postDao.get(postId);

        if (post.getUser().getUserId().equals(user.getUserId())) {
            throw new InvalidValueException("Cannot donate to your own post.");
        }

        postDonation.setPost(post);
        postDonation.setSourceUser(user);
        postDonationDao.save(postDonation);
        transactionProcessor.add(postDonation);
    }

    @Transactional
    public List<PostDonation> getPrivateTransactions(String userName, Integer firstResult, Integer maxResults) {
        return postDonationDao.getForUser(userName, firstResult, maxResults);
    }
}
