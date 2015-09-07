package com.hak.wymi.persistance.pojos.user;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.security.Principal;

@Repository
@SuppressWarnings("unchecked")
public class BalanceDaoImpl implements BalanceDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Balance get(Principal principal) {
        final Session session = sessionFactory.openSession();
        final Balance balance = (Balance) session.createQuery("from Balance where user.name=:userName")
                .setParameter("userName", principal.getName())
                .uniqueResult();
        session.close();
        return balance;
    }
}
