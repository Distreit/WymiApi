package com.hak.wymi.persistance.pojos.unsecure.posttransaction;

import com.hak.wymi.persistance.pojos.unsecure.message.Message;
import com.hak.wymi.persistance.pojos.unsecure.transactions.TransactionState;
import org.hibernate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class PostTransactionDaoImpl implements PostTransactionDao {
    protected static final Logger logger = LoggerFactory.getLogger(PostTransactionDao.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean save(PostTransaction postTransaction) {
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        try {
            postTransaction.setState(TransactionState.UNPROCESSED);
            session.persist(postTransaction);
            tx.commit();
            return true;
        } catch (HibernateException e) {
            logger.error(e.getMessage());
            if (tx != null) {
                tx.rollback();
            }
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean cancel(PostTransaction postTransaction) {
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        try {
            postTransaction.setState(TransactionState.CANCELED);
            Message message = new Message(
                    postTransaction.getSourceUser(),
                    null,
                    "Transfer failure.",
                    String.format("Failed to transfer %d points to the post '%s' in the topic '%s', transaction was canceled.",
                            postTransaction.getAmount(),
                            postTransaction.getPost().getTitle(),
                            postTransaction.getPost().getTopic().getName()));

            message.setSourceDeleted(true);
            session.update(postTransaction);
            session.save(message);
            tx.commit();
            return true;
        } catch (HibernateException e) {
            logger.error(e.getMessage());
            if (tx != null) {
                tx.rollback();
            }
            tx.rollback();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public List<PostTransaction> getUnprocessed() {
        Session session = this.sessionFactory.openSession();
        List<PostTransaction> postTransactionList = session
                .createQuery("from PostTransaction p where p.state=:state")
                .setParameter("state", TransactionState.UNPROCESSED)
                .list();
        session.close();
        return postTransactionList;
    }
}
