package com.hak.wymi.persistance.pojos.user;

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
            final Balance balance = new Balance();
            balance.setUser(user);
            balance.setCurrentBalance(0);
            session.persist(user);
            session.refresh(user);
            session.persist(balance);
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
            final User user = (User) session.createQuery("from User where lower(name)=:name")
                    .setParameter("name", name.toLowerCase(Locale.ENGLISH))
                    .uniqueResult();
            user.getSubscriptions().size();
            user.getFilters().size();
            session.close();
            return user;
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
