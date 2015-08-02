package com.hak.wymi.persistance.pojos.callbackcode;

import com.hak.wymi.persistance.pojos.user.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Component
@SuppressWarnings("unchecked")
public class CallbackCodeImpl implements CallbackCodeDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean save(CallbackCode callbackCode) {
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.persist(callbackCode);
            tx.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            return false;
        }
    }

    @Override
    public CallbackCode getFromUserName(String userName, String code, CallbackCodeType type) {
        Session session = this.sessionFactory.openSession();
        List<CallbackCode> registerList = session.createQuery(
                "from CallbackCode c where c.user.name=:name and c.code=:code and c.type=:type")
                .setParameter("code", code)
                .setParameter("type", type)
                .setParameter("name", userName)
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
            session.close();
            return true;
        } catch (HibernateException e) {
            return false;
        }
    }
}
