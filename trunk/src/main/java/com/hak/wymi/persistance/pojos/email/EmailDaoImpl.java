package com.hak.wymi.persistance.pojos.email;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class EmailDaoImpl implements EmailDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void save(Email email) {
        sessionFactory.getCurrentSession().save(email);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void update(Email email) {
        sessionFactory.getCurrentSession().update(email);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<Email> getUnsent() {
        return sessionFactory
                .getCurrentSession()
                .createCriteria(Email.class)
                .add(Restrictions.eq("sent", false))
                .addOrder(Order.asc("created"))
                .list();
    }
}
