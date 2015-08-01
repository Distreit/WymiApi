package com.hak.wymi.persistance.pojos.user;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.security.Principal;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean save(User user) {
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.persist(user);
            tx.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            return false;
        }
    }

    @Override
    public User get(Principal principal) {
        return getFromName(principal.getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public User getFromName(String name) {
        if (name != null && !name.equals("")) {
            Session session = this.sessionFactory.openSession();
            List<User> userList = session.createQuery("from User where name=:name").setParameter("name", name).list();
            session.close();
            if (userList.size() == 1) {
                return userList.get(0);
            }
        }
        return null;
    }

    @Override
    public User getFromEmail(String email) {
        if (email != null && !email.equals("")) {
            Session session = this.sessionFactory.openSession();
            List<User> userList = session.createQuery("from User where email=:email").setParameter("email", email).list();
            session.close();
            if (userList.size() == 1) {
                return userList.get(0);
            }
        }
        return null;
    }
}
