package com.hak.wymi.persistance.pojos.smsmessage;

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
public class SMSMessageDaoImpl implements SMSMessageDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void save(SMSMessage message) {
        sessionFactory.getCurrentSession().save(message);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void update(SMSMessage message) {
        sessionFactory.getCurrentSession().update(message);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<SMSMessage> getUnsent() {
        return sessionFactory
                .getCurrentSession()
                .createCriteria(SMSMessage.class)
                .add(Restrictions.eq("sent", false))
                .addOrder(Order.asc("created"))
                .list();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<SMSMessage> getForNumber(String number) {
        return sessionFactory
                .getCurrentSession()
                .createCriteria(SMSMessage.class)
                .add(Restrictions.eq("number", number))
                .list();
    }
}
