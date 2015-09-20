package com.hak.wymi.persistance.pojos.topicbid;

import java.util.List;

public interface TopicBidDao {
    boolean save(TopicBidCreation topicBidCreation);

    List<TopicBid> get(String topicName);

    TopicBidCreation getTransaction(Integer topicBidId);
}
