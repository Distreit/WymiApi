package com.hak.wymi.persistance.pojos.topic;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    public List<Topic> getAll(int firstResult, int maxResults) {
        return sessionFactory.getCurrentSession()
                .createQuery("from Topic")
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .list();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<Topic> getFiltered(List<String> searchTerms, int firstResult, int maxResults) {
        final Criteria criteria = sessionFactory
                .getCurrentSession()
                .createCriteria(Topic.class)
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .addOrder(Order.asc("name"));

        for (String term : searchTerms) {
            criteria.add(Restrictions.ilike("name", term, MatchMode.ANYWHERE));
        }

        return criteria.list();
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
    public void save(Topic topic) {
        sessionFactory.getCurrentSession().persist(topic);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Topic get(String name) {
        return (Topic) sessionFactory.getCurrentSession()
                .createQuery("from Topic where lower(name)=:name")
                .setParameter("name", name.toLowerCase(Locale.ENGLISH))
                .uniqueResult();
    }
}
