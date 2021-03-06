package com.hak.wymi.rent;

import com.hak.wymi.persistance.pojos.balancetransaction.DonationTransaction;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.comment.CommentDonationDao;
import com.hak.wymi.persistance.pojos.ownershiptransaction.OwnershipTransaction;
import com.hak.wymi.persistance.pojos.ownershiptransaction.OwnershipTransactionDao;
import com.hak.wymi.persistance.pojos.post.PostDonationDao;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.topicbid.TopicBid;
import com.hak.wymi.persistance.pojos.topicbid.TopicBidDao;
import com.hak.wymi.persistance.pojos.topicbid.TopicBidDispersion;
import com.hak.wymi.persistance.pojos.usertopicrank.UserTopicRank;
import com.hak.wymi.persistance.pojos.usertopicrank.UserTopicRankDao;
import com.hak.wymi.persistance.ranker.UserTopicRanker;
import com.hak.wymi.utility.transactionprocessor.TransactionProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RentManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(RentManager.class);

    @Autowired
    private TopicBidDao topicBidDao;

    @Autowired
    private OwnershipTransactionDao ownershipTransactionDao;

    @Autowired
    private CommentDonationDao commentDonationDao;

    @Autowired
    private PostDonationDao postDonationDao;

    @Autowired
    private UserTopicRankDao userTopicRankDao;

    @Autowired
    private TransactionProcessor transactionProcessor;

    @Value("${ranking.delta}")
    private Double minDelta;

    @Value("${ranking.maxIterations}")
    private Integer maxIterations;

    @Value("${ranking.dampeningFactor}")
    private Double dampeningFactor;

    @Transactional
    public void processTopic(Topic topic) {
        final List<TopicBid> bids = topicBidDao.getForRentTransaction(topic.getName());
        TopicBid maxBid = null;
        if (!bids.isEmpty()) {
            maxBid = bids.stream().max(Comparator.comparing(TopicBid::getCurrentBalance)).get();
        }

        final OwnershipTransaction transaction = new OwnershipTransaction(topic, maxBid);
        if (ownershipTransactionDao.saveOrUpdate(transaction, bids)) {
            if (maxBid == null || maxBid.getUser().equals(topic.getOwner())) {
                LOGGER.info("Topic {} ownership staying with {} as there are no bids.",
                        topic.getName(), topic.getOwner().getName());
            } else {
                LOGGER.info("Topic {} going to transfer ownership to {} unless owner responds by {}.",
                        topic.getName(), maxBid.getUser().getName(), transaction.getWaitingPeriodExpiration());
                //TODO: Email/Message current owner with expiration time, need to createPostTrial way for owner to claim site first.
            }
        }
    }

    @Transactional
    public void processRentPeriodExpired(OwnershipTransaction ownershipTransaction) {
        final Topic topic = ownershipTransaction.getTopic();
        final UserTopicRanker userTopicRanker = new UserTopicRanker(topic);

        final List<? extends DonationTransaction> donations = Stream.concat(
                commentDonationDao.get(topic.getName()).stream(),
                postDonationDao.get(topic.getName()).stream()
        ).collect(Collectors.toList());

        userTopicRanker.runOn(donations, minDelta, maxIterations, dampeningFactor);
        if (userTopicRankDao.save(userTopicRanker)) {
            List<UserTopicRank> winningRanks = userTopicRanker.getUserRanks();

            // Sort by highest to lowest rank and return top half.
            winningRanks = winningRanks.stream()
                    .sorted((a, b) -> b.getRank().compareTo(a.getRank()))
                    .limit(winningRanks.size() / 2)
                    .collect(Collectors.toList());

            List<TopicBidDispersion> dispersions = ownershipTransactionDao.process(ownershipTransaction, winningRanks);
            if (dispersions != null) {
                dispersions.stream().forEach(this::process);
            }
        }
    }

    private void process(TopicBidDispersion topicBidDispersion) {
        try {
            transactionProcessor.process(topicBidDispersion);
        } catch (InvalidValueException e) {
            LOGGER.error("Error dispersing topic bid. This shouldn't happen!!", e);
        }
    }
}
