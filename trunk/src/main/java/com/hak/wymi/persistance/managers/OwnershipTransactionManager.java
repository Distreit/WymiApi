package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.ownershiptransaction.OwnershipTransaction;
import com.hak.wymi.persistance.pojos.ownershiptransaction.OwnershipTransactionDao;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.topicbid.TopicBid;
import com.hak.wymi.persistance.pojos.topicbid.TopicBidDispersion;
import com.hak.wymi.persistance.pojos.usertopicrank.UserTopicRank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OwnershipTransactionManager {
    @Autowired
    private OwnershipTransactionDao ownershipTransactionDao;

    @Transactional
    public OwnershipTransaction getRentPeriodNotExpired(Topic topic) {
        return ownershipTransactionDao.getRentPeriodNotExpired(topic);
    }

    @Transactional
    public boolean saveOrUpdate(OwnershipTransaction ownershipTransaction, List<TopicBid> failedBids) {
        return ownershipTransactionDao.saveOrUpdate(ownershipTransaction, failedBids);
    }

    @Transactional
    public List<OwnershipTransaction> getRentPeriodExpired() {
        return ownershipTransactionDao.getRentPeriodExpired();
    }

    @Transactional
    public List<TopicBidDispersion> process(OwnershipTransaction ownershipTransaction, List<UserTopicRank> winningRanks) {
        return ownershipTransactionDao.process(ownershipTransaction, winningRanks);
    }
}
