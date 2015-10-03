package com.hak.wymi.persistance.pojos.ownershiptransaction;

import com.hak.wymi.persistance.pojos.topicbid.TopicBid;
import com.hak.wymi.persistance.pojos.topicbid.TopicBidDispersion;
import com.hak.wymi.persistance.pojos.usertopicrank.UserTopicRank;

import java.util.List;

public interface OwnershipTransactionDao {
    List<TopicBidDispersion> process(OwnershipTransaction ownershipTransaction, List<UserTopicRank> winningRanks);

    boolean save(OwnershipTransaction ownershipTransaction, List<TopicBid> failedBids);

    List<OwnershipTransaction> getRentPeriodExpired();
}
