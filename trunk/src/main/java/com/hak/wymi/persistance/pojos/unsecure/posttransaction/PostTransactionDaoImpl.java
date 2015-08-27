package com.hak.wymi.persistance.pojos.unsecure.posttransaction;

import com.hak.wymi.persistance.pojos.unsecure.message.Message;
import com.hak.wymi.persistance.pojos.unsecure.transactions.TransactionState;
import com.hak.wymi.utility.DaoHelper;
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
    public boolean save(PostTransactionAbstract postTransaction) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            postTransaction.setState(TransactionState.UNPROCESSED);
            session.persist(postTransaction);
        });
    }

    @Override
    public boolean cancel(PostTransactionAbstract postTransaction) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            postTransaction.setState(TransactionState.CANCELED);
            final Message message = new Message(
                    postTransaction.getSourceUser(),
                    null,
                    "Transfer failure.",
                    String.format("Failed to transfer %d points to the post '%s' in the topic '%s', transaction was canceled.",
                            postTransaction.getAmount(),
                            postTransaction.getPost().getTitle(),
                            postTransaction.getPost().getTopic().getName()));

            message.setSourceDeleted(Boolean.TRUE);
            session.update(postTransaction);
            session.save(message);
        });
    }

    @Override
    public List<PostTransactionAbstract> getUnprocessed() {
        final Session session = sessionFactory.openSession();
        final List<PostTransactionAbstract> postTransactionList = session
                .createQuery("from PostTransactionAbstract p where p.state=:state")
                .setParameter("state", TransactionState.UNPROCESSED)
                .list();
        session.close();
        return postTransactionList;
    }
}
