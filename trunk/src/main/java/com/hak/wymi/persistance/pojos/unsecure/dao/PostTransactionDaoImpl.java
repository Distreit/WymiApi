package com.hak.wymi.persistance.pojos.unsecure.dao;

import com.hak.wymi.persistance.pojos.unsecure.PostTransaction;
import com.hak.wymi.persistance.pojos.unsecure.TransactionState;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class PostTransactionDaoImpl implements PostTransactionDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean save(PostTransaction postTransaction) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            postTransaction.setState(TransactionState.UNPROCESSED);
            session.persist(postTransaction);
            session.refresh(postTransaction);
            return true;
        });
    }

    @Override
    public List<PostTransaction> getUnprocessed() {
        final Session session = sessionFactory.openSession();
        final List<PostTransaction> postTransactionList = session
                .createQuery("from PostTransaction p where p.state=:state")
                .setParameter("state", TransactionState.UNPROCESSED)
                .list();
        session.close();
        return postTransactionList;
    }
}
