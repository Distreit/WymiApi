package com.hak.wymi.persistance.pojos.unsecure.balance;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;

import java.security.Principal;

@Repository
@SuppressWarnings("unchecked")
public class BalanceDaoImpl implements BalanceDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(BalanceDaoImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Balance get(Principal principal) {
        return null;
    }

    @Override
    @Secured("ROLE_VALIDATED")
    public boolean save(Balance balance) {
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            balance.setBalance(0);
            session.persist(balance);
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
}
