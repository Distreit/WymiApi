package com.hak.wymi.persistance.pojos.unsecure.post;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class PostDaoImpl implements PostDao {
    protected static final Logger logger = LoggerFactory.getLogger(PostDaoImpl.class);

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
    public Post get(Integer id) {
        if (id != null) {
            Session session = this.sessionFactory.openSession();
            List<Post> postList = session.createQuery("from Post where postId=:postId")
                    .setParameter("postId", id)
                    .list();
            session.close();
            if (postList.size() == 1) {
                return postList.get(0);
            }
        }
        return null;
    }

    @Override
    @Secured("ROLE_VALIDATED")
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
            return true;
        } catch (HibernateException e) {
            logger.error(e.getMessage());
            if (tx != null) {
                tx.rollback();
            }
            return false;
        } finally {
            session.close();
        }
    }
}
