package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.ownershiptransaction.OwnershipTransaction;
import com.hak.wymi.persistance.pojos.ownershiptransaction.OwnershipTransactionDao;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.topic.TopicDao;
import com.hak.wymi.persistance.pojos.topicbid.TopicBid;
import com.hak.wymi.persistance.pojos.topicbid.TopicBidCreation;
import com.hak.wymi.persistance.pojos.topicbid.TopicBidDispersion;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.persistance.pojos.user.UserDao;
import com.hak.wymi.persistance.pojos.usertopicrank.UserTopicRank;
import com.hak.wymi.rent.RentManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Service
public class OwnershipTransactionManager {
    @Autowired
    private UserDao userDao;

    @Autowired
    private TopicDao topicDao;

    @Autowired
    private TopicBidManager topicBidManager;

    @Autowired
    private OwnershipTransactionDao ownershipTransactionDao;

    @Autowired
    private RentManager rentManager;

    @Value("${topic.ownership.ownerBidMultiplier}")
    private double ownerBidMultiplier;

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

    @Transactional(rollbackFor = {InvalidValueException.class})
    public void claim(String topicName, String userName, Integer amount) throws InvalidValueException {
        final User user = userDao.getFromName(userName);
        final Topic topic = topicDao.get(topicName);
        final OwnershipTransaction transaction = getRentPeriodNotExpired(topic);

        if (amount >= getClaimAmount(transaction)
                && user.getName().equals(topic.getOwner().getName())
                && !transaction.getWinningBid().getUser().getName().equals(user.getName())) {

            final TopicBidCreation topicBidCreation = topicBidManager.create(topicName, userName, amount);

            final List<TopicBid> failedBids = new LinkedList<>();
            failedBids.add(transaction.getWinningBid());
            transaction.setWinningBid(topicBidCreation.getTopicBid());
            saveOrUpdate(transaction, failedBids);
            rentManager.processRentPeriodExpired(transaction);
        }
    }

    public int getClaimAmount(OwnershipTransaction transaction) {
        return (int) (transaction.getWinningBid().getCurrentBalance() * ownerBidMultiplier);
    }
}
