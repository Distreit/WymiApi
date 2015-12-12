package com.hak.wymi.persistance.pojos.externaltransaction;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@SuppressWarnings("unchecked")
public class TransferTransactionDaoImpl implements TransferTransactionDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void save(TransferTransaction transferTransaction) {
        sessionFactory.getCurrentSession().save(transferTransaction);
    }
}
