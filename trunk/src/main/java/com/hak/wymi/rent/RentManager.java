package com.hak.wymi.rent;

import com.hak.wymi.persistance.managers.CommentDonationManager;
import com.hak.wymi.persistance.managers.OwnershipTransactionManager;
import com.hak.wymi.persistance.managers.PostDonationManager;
import com.hak.wymi.persistance.managers.TopicBidManager;
import com.hak.wymi.persistance.managers.TopicManager;
import com.hak.wymi.persistance.managers.UserTopicRankManager;
import com.hak.wymi.persistance.pojos.balancetransaction.DonationTransaction;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InsufficientFundsException;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.ownershiptransaction.OwnershipTransaction;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.topicbid.TopicBid;
import com.hak.wymi.persistance.pojos.topicbid.TopicBidDispersion;
import com.hak.wymi.persistance.pojos.usertopicrank.UserTopicRank;
import com.hak.wymi.persistance.ranker.UserTopicRanker;
import com.hak.wymi.utility.TransactionProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RentManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(RentManager.class);

    @Autowired
    private TopicManager topicManager;

    @Autowired
    private TopicBidManager topicBidManager;

    @Autowired
    private OwnershipTransactionManager ownershipTransactionManager;

    @Autowired
    private CommentDonationManager commentDonationManager;

    @Autowired
    private PostDonationManager postDonationManager;

    @Autowired
    private UserTopicRankManager userTopicRankManager;

    @Autowired
    private TransactionProcessor transactionProcessor;

    @Value("${ranking.delta}")
    private Double minDelta;

    @Value("${ranking.maxIterations}")
    private Integer maxIterations;

    @Value("${ranking.dampeningFactor}")
    private Double dampeningFactor;

    @Scheduled(fixedRate = 5000)
    public void checkRent() {
        topicManager.getRentDue().stream().forEach(this::processTopic);
        ownershipTransactionManager.getRentPeriodExpired().stream().forEach(this::processRentPeriodExpired);
    }

    private void processTopic(Topic topic) {
        final List<TopicBid> bids = topicBidManager.getForRentTransaction(topic.getName());
        TopicBid maxBid = null;
        if (!bids.isEmpty()) {
            maxBid = bids.stream().max(Comparator.comparing(TopicBid::getCurrentBalance)).get();
        }

        final OwnershipTransaction transaction = new OwnershipTransaction(topic, maxBid);
        if (ownershipTransactionManager.saveOrUpdate(transaction, bids)) {
            if (maxBid == null || maxBid.getUser().equals(topic.getOwner())) {
                LOGGER.info("Topic {} ownership staying with {} as there are no bids.",
                        topic.getName(), topic.getOwner().getName());
            } else {
                LOGGER.info("Topic {} going to transfer ownership to {} unless owner responds by {}.",
                        topic.getName(), maxBid.getUser().getName(), transaction.getWaitingPeriodExpiration());
                //TODO: Email/Message current owner with expiration time, need to create way for owner to claim site first.
            }
        }
    }

    public void processRentPeriodExpired(OwnershipTransaction ownershipTransaction) {
        final Topic topic = ownershipTransaction.getTopic();
        final UserTopicRanker userTopicRanker = new UserTopicRanker(topic);

        final List<? extends DonationTransaction> donations = Stream.concat(
                commentDonationManager.get(topic.getName()).stream(),
                postDonationManager.get(topic.getName()).stream()
        ).collect(Collectors.toList());

        userTopicRanker.runOn(donations, minDelta, maxIterations, dampeningFactor);
        if (userTopicRankManager.save(userTopicRanker)) {
            List<UserTopicRank> winningRanks = userTopicRanker.getUserRanks();

            // Sort by highest to lowest rank and return top half.
            winningRanks = winningRanks.stream()
                    .sorted((a, b) -> b.getRank().compareTo(a.getRank()))
                    .limit(winningRanks.size() / 2)
                    .collect(Collectors.toList());

            List<TopicBidDispersion> dispersions = ownershipTransactionManager.process(ownershipTransaction, winningRanks);
            if (dispersions != null) {
                dispersions.stream().forEach(this::process);
            }
        }
    }

    private void process(TopicBidDispersion topicBidDispersion) {
        try {
            transactionProcessor.process(topicBidDispersion);
        } catch (InvalidValueException | InsufficientFundsException e) {
            LOGGER.error("Error dispersing topic bid. This shouldn't happen!!", e);
        }
    }
}
