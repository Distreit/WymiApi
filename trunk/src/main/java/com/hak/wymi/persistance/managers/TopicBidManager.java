package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.topic.TopicDao;
import com.hak.wymi.persistance.pojos.topicbid.TopicBid;
import com.hak.wymi.persistance.pojos.topicbid.TopicBidCreation;
import com.hak.wymi.persistance.pojos.topicbid.TopicBidDao;
import com.hak.wymi.persistance.pojos.topicbid.TopicBidState;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.persistance.pojos.user.UserDao;
import com.hak.wymi.utility.transactionprocessor.TransactionProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TopicBidManager {
    @Autowired
    private TopicBidDao topicBidDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TopicDao topicDao;

    @Autowired
    private TransactionProcessor transactionProcessor;

    @Transactional
    public List<TopicBid> get(String topicName, TopicBidState waiting) {
        return topicBidDao.get(topicName, waiting);
    }

    @Transactional(rollbackFor = {InvalidValueException.class})
    public void save(TopicBidCreation topicBidCreation) throws InvalidValueException {
        topicBidDao.save(topicBidCreation);
        transactionProcessor.process(topicBidCreation);
    }

    @Transactional
    public List<TopicBid> getForRentTransaction(String name) {
        return topicBidDao.getForRentTransaction(name);
    }

    @Transactional(rollbackFor = {InvalidValueException.class})
    public TopicBid cancel(Integer topicBidId, String userName) throws InvalidValueException {
        final TopicBidCreation topicBidCreation = topicBidDao.getTransaction(topicBidId);
        final User user = userDao.getFromName(userName);
        transactionProcessor.cancel(user, topicBidCreation);
        return topicBidCreation.getTopicBid();
    }

    @Transactional(rollbackFor = {InvalidValueException.class})
    public TopicBidCreation create(String topicName, String username, int amount) throws InvalidValueException {
        final User user = userDao.getFromName(username);
        final Topic topic = topicDao.get(topicName);
        final TopicBid topicBid = new TopicBid(user, topic);
        final TopicBidCreation topicBidCreation = new TopicBidCreation(topicBid, amount);

        topicBidDao.save(topicBidCreation);
        transactionProcessor.process(topicBidCreation);
        return topicBidCreation;
    }
}
