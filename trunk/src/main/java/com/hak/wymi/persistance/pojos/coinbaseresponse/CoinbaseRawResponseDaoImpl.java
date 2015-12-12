package com.hak.wymi.persistance.pojos.coinbaseresponse;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class CoinbaseRawResponseDaoImpl implements CoinbaseRawResponseDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void save(CoinbaseRawResponse coinbaseRawResponse) {
        sessionFactory.getCurrentSession().save(coinbaseRawResponse);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void update(CoinbaseRawResponse coinbaseRawResponse) {
        sessionFactory.getCurrentSession().update(coinbaseRawResponse);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<CoinbaseRawResponse> getUnprocessed() {
        return sessionFactory
                .getCurrentSession()
                .createCriteria(CoinbaseRawResponse.class)
                .add(Restrictions.eq("processed", false))
                .addOrder(Order.asc("created"))
                .list();
    }
}
