package com.hak.wymi.persistance.pojos.topic;

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
public class TopicDaoImpl implements TopicDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean update(Topic topic) {
        return saveOrUpdate(topic, false);
    }

    @Override
    public List<Topic> getAll() {
        Session session = this.sessionFactory.openSession();
        List<Topic> topicList = session.createQuery("from Topic").list();
        session.close();
        return topicList;
    }

    @Override
    @Secured("ROLE_USER")
    public boolean save(Topic topic) {
        return saveOrUpdate(topic, true);
    }

    private boolean saveOrUpdate(Topic topic, boolean save) {
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            if (save) {
                session.persist(topic);
            } else {
                session.update(topic);
            }
            tx.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            return false;
        }
    }

    @Override
    public Topic get(String name) {
        if (name != null && !name.equals("")) {
            Session session = this.sessionFactory.openSession();
            List<Topic> topicList = session.createQuery("from Topic where lower(name)=:name")
                    .setParameter("name", name.toLowerCase())
                    .list();
            session.close();
            if (topicList.size() == 1) {
                return topicList.get(0);
            }
        }
        return null;
    }
}
