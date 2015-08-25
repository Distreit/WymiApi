package com.hak.wymi.persistance.pojos.unsecure.posttransaction;

import com.hak.wymi.persistance.pojos.unsecure.message.Message;
import com.hak.wymi.persistance.pojos.unsecure.transactions.TransactionState;
import com.hak.wymi.utility.DaoHelper;
import org.hibernate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class PostTransactionDaoImpl implements PostTransactionDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostTransactionDao.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean save(PostTransaction postTransaction) {
        postTransaction.setState(TransactionState.UNPROCESSED);
        return DaoHelper.simpleSaveOrUpdate(postTransaction, this.sessionFactory.openSession(), true);
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
