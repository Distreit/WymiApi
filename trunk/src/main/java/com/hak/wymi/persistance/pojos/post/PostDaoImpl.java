package com.hak.wymi.persistance.pojos.post;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class PostDaoImpl implements PostDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<Post> get(String topicName, int firstResult, int maxResults) {
        return sessionFactory
                .getCurrentSession()
                .createQuery("FROM Post p WHERE p.topic.name=:topicName and deleted=false ORDER BY p.score DESC")
                .setParameter("topicName", topicName)
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .list();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<Post> get(List<String> topicList, int firstResult, int maxResults, boolean filtered, boolean trashed) {
        final Session session = sessionFactory.getCurrentSession();
        final Query query;
        topicList.add("");
        if (filtered || topicList.get(0).equals("ALL_TOPICS")) {
            query = session
                    .createQuery("FROM Post WHERE topic.name NOT IN (:topicNames) and deleted=false and trashed=:trashed ORDER BY score DESC");
        } else {
            query = session
                    .createQuery("FROM Post WHERE topic.name IN (:topicNames) and deleted=false and trashed=:trashed ORDER BY score DESC");
        }

        return query.setParameterList("topicNames", topicList)
                .setParameter("trashed", trashed)
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .list();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void update(Post post) {
        sessionFactory.getCurrentSession().update(post);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Post get(Integer postId) {
        return (Post) sessionFactory
                .getCurrentSession()
                .createQuery("FROM Post WHERE postId=:postId and deleted=false")
                .setParameter("postId", postId)
                .uniqueResult();
    }
}
