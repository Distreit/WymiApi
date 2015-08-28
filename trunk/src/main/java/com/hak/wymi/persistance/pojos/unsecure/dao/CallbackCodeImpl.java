package com.hak.wymi.persistance.pojos.unsecure.dao;

import com.hak.wymi.persistance.pojos.unsecure.CallbackCode;
import com.hak.wymi.persistance.pojos.unsecure.CallbackCodeType;
import com.hak.wymi.persistance.utility.DaoHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;

@Repository
@SuppressWarnings("unchecked")
public class CallbackCodeImpl implements CallbackCodeDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean save(CallbackCode callbackCode) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            session.persist(callbackCode);
            return true;
        });
    }

    @Override
    public CallbackCode getFromUserName(String userName, String code, CallbackCodeType type) {
        final Session session = sessionFactory.openSession();
        final List<CallbackCode> registerList = session.createQuery(
                "from CallbackCode c where lower(c.user.name)=:name and c.code=:code and c.type=:type")
                .setParameter("code", code)
                .setParameter("type", type)
                .setParameter("name", userName.toLowerCase(Locale.ENGLISH))
                .list();
        session.close();
        if (registerList.size() == 1) {
            return registerList.get(0);
        }
        return null;
    }

    @Override
    public boolean delete(CallbackCode callbackCode) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            session.delete(callbackCode);
            return true;
        });
    }

    @Override
    public CallbackCode getFromCode(String code, CallbackCodeType type) {
        final Session session = sessionFactory.openSession();
        final List<CallbackCode> registerList = session.createQuery(
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
