package com.hak.wymi.persistance.pojos.usertopicrank;

import com.hak.wymi.persistance.ranker.UserTopicRanker;

@FunctionalInterface
public interface UserTopicRankDao {

    /**
     * Saves each users rank to the database for a given topic. Erases and existing ranks for that particular topic.
     *
     * @param ranker The ranker containing the user/rank information.
     *
     * @return True if the saveOrUpdate was successful
     */
    Boolean save(UserTopicRanker ranker);
}
