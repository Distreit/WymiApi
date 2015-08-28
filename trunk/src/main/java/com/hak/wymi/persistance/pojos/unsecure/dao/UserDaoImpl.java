package com.hak.wymi.persistance.pojos.unsecure.dao;

import com.hak.wymi.persistance.pojos.unsecure.User;
import com.hak.wymi.persistance.utility.DaoHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

@Repository
@SuppressWarnings("unchecked")
public class UserDaoImpl implements UserDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean save(User user) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            session.persist(user);
            return true;
        });
    }

    @Override
    public User get(Principal principal) {
        return getFromName(principal.getName());
    }

    @Override
    public User getFromName(String name) {
        if (name != null && !"".equals(name)) {
            final Session session = sessionFactory.openSession();
            final List<User> userList = session.createQuery("from User where lower(name)=:name")
                    .setParameter("name", name.toLowerCase(Locale.ENGLISH))
                    .list();
            session.close();
            if (userList.size() == 1) {
                return userList.get(0);
            }
        }
        return null;
    }

    @Override
    public User getFromEmail(String email) {
        if (email != null && !"".equals(email)) {
            final Session session = sessionFactory.openSession();
            final List<User> userList = session.createQuery("from User where lower(email)=:email")
                    .setParameter("email", email.toLowerCase(Locale.ENGLISH))
                    .list();
            session.close();
            if (userList.size() == 1) {
                return userList.get(0);
            }
        }
        return null;
    }

    @Override
    public boolean update(User user) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            session.update(user);
            return true;
        });
    }
}
