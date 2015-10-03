package com.hak.wymi.rent;

import com.hak.wymi.persistance.pojos.comment.CommentDonationDao;
import com.hak.wymi.persistance.pojos.ownershiptransaction.OwnershipTransaction;
import com.hak.wymi.persistance.pojos.ownershiptransaction.OwnershipTransactionDao;
import com.hak.wymi.persistance.pojos.post.PostDonationDao;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.topic.TopicDao;
import com.hak.wymi.persistance.pojos.topicbid.TopicBid;
import com.hak.wymi.persistance.pojos.topicbid.TopicBidDao;
import com.hak.wymi.persistance.pojos.usertopicrank.UserTopicRank;
import com.hak.wymi.persistance.pojos.usertopicrank.UserTopicRankDao;
import com.hak.wymi.persistance.ranker.UserTopicRanker;
import com.hak.wymi.utility.BalanceTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RentManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(RentManager.class);

    @Autowired
    private TopicDao topicDao;

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
    private BalanceTransactionManager balanceTransactionManager;

    @Value("${ranking.delta}")
    private Double minDelta;

    @Value("${ranking.maxIterations}")
    private Integer maxIterations;

    @Value("${ranking.dampeningFactor}")
    private Double dampeningFactor;

    @Scheduled(fixedRate = 5000)
    public void checkRent() {
        topicDao.getRentDue().stream().forEach(this::processTopic);
        ownershipTransactionDao.getRentPeriodExpired().stream().forEach(this::processRentPeriodExpired);
    }

    private void processTopic(Topic topic) {
        final List<TopicBid> bids = topicBidDao.getForRentTransaction(topic.getName());
        TopicBid maxBid = null;
        if (!bids.isEmpty()) {
            maxBid = bids.stream().max(Comparator.comparing(TopicBid::getCurrentBalance)).get();
        }

        final OwnershipTransaction transaction = new OwnershipTransaction(topic, maxBid);
        if (ownershipTransactionDao.save(transaction, bids)) {
            if (maxBid != null && !maxBid.getUser().equals(topic.getOwner())) {
                LOGGER.info("Topic {} going to transfer ownership to {} unless owner responds by {}.",
                        topic.getName(), maxBid.getUser().getName(), transaction.getWaitingPeriodExpiration());
                //TODO: Email/Message current owner with expiration time.
            } else {
                LOGGER.info("Topic {} ownership staying with {} as there are no bids.",
                        topic.getName(), topic.getOwner().getName());
            }
        }
    }

    private void processRentPeriodExpired(OwnershipTransaction ownershipTransaction) {
        UserTopicRanker userTopicRanker = new UserTopicRanker();
        UserTopicRanker.runOn(ownershipTransaction.getTopic(),
                commentDonationDao,
                postDonationDao,
                userTopicRankDao,
                minDelta,
                maxIterations,
                dampeningFactor,
                userTopicRanker);

        List<UserTopicRank> winningRanks = userTopicRanker
                .getUserRanks(ownershipTransaction.getTopic());

        winningRanks = winningRanks.stream()
                .sorted((a, b) -> b.getRank().compareTo(a.getRank()))
                .limit(winningRanks.size() / 2)
                .collect(Collectors.toList());

        ownershipTransactionDao.process(ownershipTransaction, winningRanks)
                .stream()
                .forEach(balanceTransactionManager::process);
    }
}
