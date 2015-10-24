package com.hak.wymi.persistance.pojos.user;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

@Repository
@SuppressWarnings("unchecked")
public class UserDaoImpl implements UserDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean save(User user) {
        final Session session = sessionFactory.getCurrentSession();
        final Balance balance = new Balance();

        balance.setUser(user);
        balance.setCurrentBalance(0);

        session.save(user);
        session.refresh(user);

        balance.setUserId(user.getUserId());
        session.save(balance);
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public User get(Principal principal) {
        return getFromName(principal.getName());
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public User getFromName(String name) {
        final Session session = sessionFactory.getCurrentSession();
        final User user = (User) session.createQuery("from User where lower(name)=:name")
                .setParameter("name", name.toLowerCase(Locale.ENGLISH))
                .uniqueResult();
        user.getSubscriptions().size();
        user.getFilters().size();
        return user;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public User getFromEmail(String email) {
        if (email != null && !"".equals(email)) {
            final Session session = sessionFactory.getCurrentSession();
            final List<User> userList = session.createQuery("from User where lower(email)=:email")
                    .setParameter("email", email.toLowerCase(Locale.ENGLISH))
                    .list();
            if (userList.size() == 1) {
                return userList.get(0);
            }
        }
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean update(User user) {
        final Session session = sessionFactory.getCurrentSession();
        session.update(user);
        return true;
    }
}
