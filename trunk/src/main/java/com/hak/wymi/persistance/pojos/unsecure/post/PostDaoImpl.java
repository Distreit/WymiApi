package com.hak.wymi.persistance.pojos.unsecure.post;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class PostDaoImpl implements PostDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Post> getAll(String topicName) {
        Session session = this.sessionFactory.openSession();
        List<Post> postList = session.createQuery("from Post p where p.topic.name=:topicName")
                .setParameter("topicName", topicName)
                .list();
        session.close();
        return postList;
    }

    @Override
    @Secured("ROLE_USER")
    public boolean save(Post post) {
        return saveOrUpdate(post, true);
    }

    private boolean saveOrUpdate(Post post, boolean save) {
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            if (save) {
                session.persist(post);
            } else {
                session.update(post);
            }
            tx.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            return false;
        }
    }
}
