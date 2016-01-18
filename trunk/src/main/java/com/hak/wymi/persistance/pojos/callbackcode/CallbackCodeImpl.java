package com.hak.wymi.persistance.pojos.callbackcode;

import com.hak.wymi.persistance.pojos.user.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
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
    public void delete(User user, CallbackCodeType type) {
        sessionFactory.getCurrentSession().createQuery("DELETE FROM CallbackCode where user.userId=:userId AND type=:type")
                .setParameter("userId", user.getUserId())
                .setParameter("type", type)
                .executeUpdate();

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

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public CallbackCode getFromCode(String code) {
        return (CallbackCode) sessionFactory
                .getCurrentSession()
                .createQuery("from CallbackCode c where c.code=:code")
                .setParameter("code", code)
                .uniqueResult();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void cleanUp() {
        final DateTime dateTime = DateTime.now().plusDays(-3);
        final Session session = sessionFactory.getCurrentSession();

        session.createQuery("DELETE FROM CallbackCode where UNIX_TIMESTAMP(created) < :date")
                .setParameter("date", dateTime.getMillis() / 1000)
                .executeUpdate();

        session.createQuery("UPDATE User u SET newEmail = null WHERE u.newEmail IS NOT NULL AND u.userId NOT IN (SELECT c.user.userId FROM CallbackCode c WHERE type='EMAIL_CHANGE')")
                .executeUpdate();

        // TODO: Figure out what to do with unvalidated user accounts.
    }
}
