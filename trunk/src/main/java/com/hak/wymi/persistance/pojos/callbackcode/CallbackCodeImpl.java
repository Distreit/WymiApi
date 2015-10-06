package com.hak.wymi.persistance.pojos.callbackcode;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Repository
@SuppressWarnings("unchecked")
public class CallbackCodeImpl implements CallbackCodeDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean save(CallbackCode callbackCode) {
        final Session session = sessionFactory.getCurrentSession();
        session.persist(callbackCode);
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public CallbackCode getFromUserName(String userName, String code, CallbackCodeType type) {
        final Session session = sessionFactory.getCurrentSession();
        final List<CallbackCode> registerList = session.createQuery(
                "from CallbackCode c where lower(c.user.name)=:name and c.code=:code and c.type=:type")
                .setParameter("code", code)
                .setParameter("type", type)
                .setParameter("name", userName.toLowerCase(Locale.ENGLISH))
                .list();
        if (registerList.size() == 1) {
            return registerList.get(0);
        }
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean delete(CallbackCode callbackCode) {
        final Session session = sessionFactory.getCurrentSession();
        session.delete(callbackCode);
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public CallbackCode getFromCode(String code, CallbackCodeType type) {
        final Session session = sessionFactory.getCurrentSession();
        final List<CallbackCode> registerList = session.createQuery(
                "from CallbackCode c where c.code=:code and c.type=:type")
                .setParameter("code", code)
                .setParameter("type", type)
                .list();
        if (registerList.size() == 1) {
            return registerList.get(0);
        }
        return null;
    }
}
