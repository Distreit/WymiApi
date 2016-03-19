package com.hak.wymi.persistance.pojos.user;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Locale;

@Repository
@SuppressWarnings("unchecked")
public class UserDaoImpl implements UserDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void save(User user) {
        final Session session = sessionFactory.getCurrentSession();
        final Balance balance = new Balance();

        balance.setUser(user);
        balance.setCurrentBalance(0);

        session.save(user);
        session.refresh(user);

        balance.setUserId(user.getUserId());
        session.save(balance);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public User get(Principal principal) {
        return getFromName(principal.getName());
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public User getFromName(String name) {
        return (User) sessionFactory
                .getCurrentSession()
                .createQuery("from User where lower(name)=:name")
                .setParameter("name", name.toLowerCase(Locale.ENGLISH))
                .uniqueResult();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public User getFromEmail(String email) {
        return getFromEmail(email, false);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public User getFromPhoneNumber(String phoneNumber) {
        return (User) sessionFactory.getCurrentSession()
                .createQuery("from User where phoneNumber=:phoneNumber")
                .setParameter("phoneNumber", phoneNumber)
                .uniqueResult();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public User getFromEmail(String email, boolean includeNewEmails) {
        final String newEmail;
        if (includeNewEmails) {
            newEmail = email.toLowerCase(Locale.ENGLISH);
        } else {
            newEmail = "DOESN'T_EXIST";
        }

        return (User) sessionFactory.getCurrentSession()
                .createQuery("from User where lower(email)=:email OR lower(newEmail)=:newEmail")
                .setParameter("email", email.toLowerCase(Locale.ENGLISH))
                .setParameter("newEmail", newEmail)
                .uniqueResult();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void update(User user) {
        sessionFactory.getCurrentSession().update(user);
    }
}
