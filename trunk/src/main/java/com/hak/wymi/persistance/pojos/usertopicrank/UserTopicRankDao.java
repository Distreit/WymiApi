package com.hak.wymi.persistance.pojos.usertopicrank;

import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.ranker.UserTopicRanker;

@FunctionalInterface
public interface UserTopicRankDao {
    Boolean save(UserTopicRanker ranker, Topic topic);
}
