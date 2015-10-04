package com.hak.wymi.persistance.pojos.ownershiptransaction;

import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.topicbid.TopicBid;
import com.hak.wymi.persistance.pojos.topicbid.TopicBidDispersion;
import com.hak.wymi.persistance.pojos.usertopicrank.UserTopicRank;

import java.util.List;

public interface OwnershipTransactionDao {
    /**
     * Changes topic ownership based on the winning bid and divides the bid between the top contributors to the topic.
     *
     * @param ownershipTransaction The transaction that contains information about who won the bid and for how much.
     * @param winningRanks         This should be a sorted list of users who contributed to the topic. Sorted from
     *                             highest to lowest.
     *
     * @return A list of dispersion transactions that need to be processed, or null when the transaction fails.
     */
    List<TopicBidDispersion> process(OwnershipTransaction ownershipTransaction, List<UserTopicRank> winningRanks);

    /**
     * @param ownershipTransaction The ownership transaction to saveOrUpdate.
     * @param failedBids           These bids will be canceled.
     *
     * @return true when successful
     */
    boolean saveOrUpdate(OwnershipTransaction ownershipTransaction, List<TopicBid> failedBids);

    List<OwnershipTransaction> getRentPeriodExpired();

    OwnershipTransaction getRentPeriodNotExpired(Topic topic);
}
