package com.hak.wymi.persistance.pojos.unsecure.dao;

import com.hak.wymi.persistance.pojos.unsecure.Topic;
import com.hak.wymi.utility.DaoHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;

@Repository
@SuppressWarnings("unchecked")
public class TopicDaoImpl implements TopicDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean update(Topic topic) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            session.update(topic);
            return true;
        });
    }

    @Override
    public List<Topic> getAll() {
        final Session session = sessionFactory.openSession();
        final List<Topic> topicList = session.createQuery("from Topic").list();
        session.close();
        return topicList;
    }

    @Override
    @Secured("ROLE_USER")
    public boolean save(Topic topic) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            session.persist(topic);
            return true;
        });
    }

    @Override
    public Topic get(String name) {
        if (name != null && !"".equals(name)) {
            final Session session = sessionFactory.openSession();
            final List<Topic> topicList = session.createQuery("from Topic where lower(name)=:name")
                    .setParameter("name", name.toLowerCase(Locale.ENGLISH))
                    .list();
            session.close();
            if (topicList.size() == 1) {
                return topicList.get(0);
            }
        }
        return null;
    }
}
