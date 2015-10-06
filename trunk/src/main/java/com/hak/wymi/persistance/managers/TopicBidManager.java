package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.topicbid.TopicBid;
import com.hak.wymi.persistance.pojos.topicbid.TopicBidCreation;
import com.hak.wymi.persistance.pojos.topicbid.TopicBidDao;
import com.hak.wymi.persistance.pojos.topicbid.TopicBidState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TopicBidManager {
    @Autowired
    private TopicBidDao topicBidDao;

    @Transactional
    public List<TopicBid> get(String topicName, TopicBidState waiting) {
        return topicBidDao.get(topicName, waiting);
    }

    @Transactional
    public TopicBidCreation getTransaction(Integer topicBidId) {
        return topicBidDao.getTransaction(topicBidId);
    }

    @Transactional
    public boolean save(TopicBidCreation topicBidCreation) {
        return topicBidDao.save(topicBidCreation);
    }

    @Transactional
    public List<TopicBid> getForRentTransaction(String name) {
        return topicBidDao.getForRentTransaction(name);
    }
}
