package com.hak.wymi.persistance.pojos.ownershiptransaction;

import com.hak.wymi.persistance.pojos.topicbid.TopicBid;

import java.util.List;

public interface OwnershipTransactionDao {
    boolean process(OwnershipTransaction ownershipTransaction);

    boolean save(OwnershipTransaction ownershipTransaction, List<TopicBid> failedBids);
}
