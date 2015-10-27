package com.hak.wymi.persistance.pojos.user;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@SuppressWarnings("unchecked")
public class BalanceDaoImpl implements BalanceDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Balance get(Integer userId) {
        return (Balance) sessionFactory.getCurrentSession()
                .createQuery("from Balance where user.userId=:userId")
                .setParameter("userId", userId)
                .uniqueResult();
    }
}
