package com.hak.wymi.persistance.pojos.topic;

import com.hak.wymi.persistance.utility.DaoHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;

import java.security.Principal;
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
    public Topic update(Topic partialTopic, Principal principal) {
        final Session firstSession = sessionFactory.openSession();
        final Topic topic = (Topic) firstSession.createQuery("from Topic where lower(name)=:name and owner.name=:ownerName")
                .setParameter("name", partialTopic.getName().toLowerCase(Locale.ENGLISH))
                .setParameter("ownerName", principal.getName())
                .uniqueResult();
        firstSession.close();


        if (DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            topic.setFeePercent(partialTopic.getFeePercent());
            topic.setFeeFlat(partialTopic.getFeeFlat());
            session.update(topic);
            topic.getOwner();
            return true;
        })) {
            return topic;
        }
        return null;
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
            final Topic topic = (Topic) session.createQuery("from Topic where lower(name)=:name")
                    .setParameter("name", name.toLowerCase(Locale.ENGLISH))
                    .uniqueResult();
            topic.getSubscribers().size();
            session.close();
            return topic;
        }
        return null;
    }
}
