package com.hak.wymi.persistance.pojos.unsecure.balance;

import com.hak.wymi.persistance.pojos.unsecure.balance.Balance;
import com.hak.wymi.persistance.pojos.unsecure.balance.BalanceDao;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;

import java.security.Principal;

@Repository
@SuppressWarnings("unchecked")
public class BalanceDaoImpl implements BalanceDao {

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
            session.close();
            return true;
        } catch (HibernateException e) {
            return false;
        }
    }
}
