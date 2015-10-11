package com.hak.wymi.persistance.pojos.topic;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

@Repository
@SuppressWarnings("unchecked")
public class TopicDaoImpl implements TopicDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean update(Topic topic) {
        sessionFactory.getCurrentSession().update(topic);
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Topic update(Topic partialTopic, Principal principal) {
        final Session session = sessionFactory.getCurrentSession();
        final Topic topic = (Topic) session.createQuery("from Topic where lower(name)=:name and owner.name=:ownerName")
                .setParameter("name", partialTopic.getName().toLowerCase(Locale.ENGLISH))
                .setParameter("ownerName", principal.getName())
                .uniqueResult();

        topic.setFeePercent(partialTopic.getFeePercent());
        topic.setFeeFlat(partialTopic.getFeeFlat());
        session.update(topic);
        topic.getOwner();
        return topic;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<Topic> getAll(int firstResult, int maxResults) {
        return sessionFactory.getCurrentSession()
                .createQuery("from Topic")
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .list();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<Topic> getRentDue() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Topic where rentDueDate<:now")
                .setParameter("now", new DateTime())
                .list();
    }

    @Override
    @Secured("ROLE_USER")
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean save(Topic topic) {
        final Session session = sessionFactory.getCurrentSession();
        session.persist(topic);
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Topic get(String name) {
        if (name != null && !"".equals(name)) {
            final Session session = sessionFactory.getCurrentSession();
            final Topic topic = (Topic) session.createQuery("from Topic where lower(name)=:name")
                    .setParameter("name", name.toLowerCase(Locale.ENGLISH))
                    .uniqueResult();
            if (topic != null) {
                topic.getSubscribers().size();
                topic.getFilters().size();
            }
            return topic;
        }
        return null;
    }
}
