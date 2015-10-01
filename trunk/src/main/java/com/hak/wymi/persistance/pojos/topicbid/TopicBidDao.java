package com.hak.wymi.persistance.pojos.topicbid;

import java.util.List;

public interface TopicBidDao {
    boolean save(TopicBidCreation topicBidCreation);

    List<TopicBid> get(String topicName, TopicBidState state);

    TopicBidCreation getTransaction(Integer topicBidId);

    List<TopicBid> getForRentTransaction(String topicName);
}
