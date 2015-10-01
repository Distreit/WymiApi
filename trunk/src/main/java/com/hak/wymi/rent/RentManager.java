package com.hak.wymi.rent;

import com.hak.wymi.persistance.pojos.ownershiptransaction.OwnershipTransaction;
import com.hak.wymi.persistance.pojos.ownershiptransaction.OwnershipTransactionDao;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.topic.TopicDao;
import com.hak.wymi.persistance.pojos.topicbid.TopicBid;
import com.hak.wymi.persistance.pojos.topicbid.TopicBidDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class RentManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(RentManager.class);

    @Autowired
    private TopicDao topicDao;

    @Autowired
    private TopicBidDao topicBidDao;

    @Autowired
    private OwnershipTransactionDao ownershipTransactionDao;

    @Scheduled(fixedRate = 5000)
    public void checkRent() {
        topicDao.getRentDue().stream().forEach(this::processTopic);
    }

    private void processTopic(Topic topic) {
        final List<TopicBid> bids = topicBidDao.getForRentTransaction(topic.getName());
        TopicBid maxBid = null;
        if (bids.size() > 0) {
            maxBid = bids.stream().max(Comparator.comparing(TopicBid::getCurrentBalance)).get();
        }
        if (ownershipTransactionDao.save(new OwnershipTransaction(topic, maxBid), bids)) {
            //TODO: Email current owner with expiration time.
        }
    }
}
