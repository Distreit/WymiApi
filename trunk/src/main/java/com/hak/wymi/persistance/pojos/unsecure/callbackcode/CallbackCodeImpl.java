package com.hak.wymi.persistance.pojos.unsecure.callbackcode;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class CallbackCodeImpl implements CallbackCodeDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(CallbackCodeImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean save(CallbackCode callbackCode) {
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.persist(callbackCode);
            tx.commit();
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (tx != null) {
                tx.rollback();
            }
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public CallbackCode getFromUserName(String userName, String code, CallbackCodeType type) {
        Session session = this.sessionFactory.openSession();
        List<CallbackCode> registerList = session.createQuery(
                "from CallbackCode c where lower(c.user.name)=:name and c.code=:code and c.type=:type")
                .setParameter("code", code)
                .setParameter("type", type)
                .setParameter("name", userName.toLowerCase())
                .list();
        session.close();
        if (registerList.size() == 1) {
            return registerList.get(0);
        }
        return null;
    }

    @Override
    public boolean delete(CallbackCode callbackCode) {
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.delete(callbackCode);
            tx.commit();
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (tx != null) {
                tx.rollback();
            }
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public CallbackCode getFromCode(String code, CallbackCodeType type) {
        Session session = this.sessionFactory.openSession();
        List<CallbackCode> registerList = session.createQuery(
                "from CallbackCode c where c.code=:code and c.type=:type")
                .setParameter("code", code)
                .setParameter("type", type)
                .list();
        session.close();
        if (registerList.size() == 1) {
            return registerList.get(0);
        }
        return null;
    }
}
