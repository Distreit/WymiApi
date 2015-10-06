package com.hak.wymi.persistance.pojos.usertopicrank;

import com.hak.wymi.persistance.ranker.UserTopicRanker;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class UserTopicRankDaoImpl implements UserTopicRankDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Boolean save(UserTopicRanker ranker) {
        final List<UserTopicRank> ranks = ranker.getUserRanks();

        if (ranks.isEmpty()) {
            return true;
        }

        final Session session = sessionFactory.getCurrentSession();
        session.createQuery("delete UserTopicRank where userTopic.topic.topicId=:topicId")
                .setParameter("topicId", ranker.getTopic().getTopicId())
                .executeUpdate();
        ranks.stream().forEach(session::save);
        return true;
    }
}
