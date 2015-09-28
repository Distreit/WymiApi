package com.hak.wymi.persistance.pojos.usertopicrank;

import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.ranker.UserTopicRanker;
import com.hak.wymi.persistance.utility.DaoHelper;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class UserTopicRankDaoImpl implements UserTopicRankDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Boolean save(UserTopicRanker ranker, Topic topic) {

        List<UserTopicRank> ranks = ranker.getUserRanks(topic);

        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            session.createQuery("delete UserTopicRank where userTopic.topic.topicId=:topicId")
                    .setParameter("topicId", topic.getTopicId())
                    .executeUpdate();
            ranks.stream().forEach(session::save);
            return true;
        });
    }
}
