package com.hak.wymi.persistance.pojos.coinbaseresponse;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CoinbaseRawResponseDaoImpl implements CoinbaseRawResponseDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void save(CoinbaseRawResponse coinbaseRawResponse) {
        sessionFactory.getCurrentSession().save(coinbaseRawResponse);
    }
}
