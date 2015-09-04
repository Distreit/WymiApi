package com.hak.wymi.persistance.pojos.transactions.comment;

import com.hak.wymi.persistance.pojos.transactions.TransactionState;
import com.hak.wymi.persistance.utility.DaoHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class CommentTransactionDaoImpl implements CommentTransactionDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean save(CommentTransaction commentTransaction) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            commentTransaction.setState(TransactionState.UNPROCESSED);
            session.persist(commentTransaction);
            session.refresh(commentTransaction);
            return true;
        });
    }

    @Override
    public List<CommentTransaction> getUnprocessed() {
        final Session session = sessionFactory.openSession();
        final List<CommentTransaction> commentTransactionList = session
                .createQuery("from CommentTransaction p where p.state=:state")
                .setParameter("state", TransactionState.UNPROCESSED)
                .list();
        session.close();
        return commentTransactionList;
    }
}
