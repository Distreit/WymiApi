package com.hak.wymi.persistance.pojos.post;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class PostDaoImpl implements PostDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Post> getAll(String topicName) {
        final Session session = sessionFactory.openSession();
        final List<Post> postList = session.createQuery("from Post p where p.topic.name=:topicName")
                .setParameter("topicName", topicName)
                .list();
        session.close();
        return postList;
    }

    @Override
    public Post get(Integer postId) {
        if (postId != null) {
            final Session session = sessionFactory.openSession();
            final List<Post> postList = session.createQuery("from Post where postId=:postId")
                    .setParameter("postId", postId)
                    .list();
            session.close();
            if (postList.size() == 1) {
                return postList.get(0);
            }
        }
        return null;
    }
}
