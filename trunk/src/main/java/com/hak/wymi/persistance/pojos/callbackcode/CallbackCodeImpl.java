package com.hak.wymi.persistance.pojos.callbackcode;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Repository
@SuppressWarnings("unchecked")
public class CallbackCodeImpl implements CallbackCodeDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void save(CallbackCode callbackCode) {
        sessionFactory.getCurrentSession().persist(callbackCode);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public CallbackCode getFromUserName(String userName, String code, CallbackCodeType type) {
        return (CallbackCode) sessionFactory
                .getCurrentSession()
                .createQuery("from CallbackCode c where lower(c.user.name)=:name and c.code=:code and c.type=:type")
                .setParameter("code", code)
                .setParameter("type", type)
                .setParameter("name", userName.toLowerCase(Locale.ENGLISH))
                .uniqueResult();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void delete(CallbackCode callbackCode) {
        sessionFactory.getCurrentSession().delete(callbackCode);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public CallbackCode getFromCode(String code, CallbackCodeType type) {
        return (CallbackCode) sessionFactory
                .getCurrentSession()
                .createQuery("from CallbackCode c where c.code=:code and c.type=:type")
                .setParameter("code", code)
                .setParameter("type", type)
                .uniqueResult();
    }
}
