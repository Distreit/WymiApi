package com.hak.wymi.persistance.pojos.unsecure.commenttransaction;

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
public class CommentTransactionDaoImpl implements CommentTransactionDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentTransactionDao.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean save(CommentTransaction commentTransaction) {
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        try {
            commentTransaction.setState(TransactionState.UNPROCESSED);
            session.persist(commentTransaction);
            session.refresh(commentTransaction);
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
    public boolean cancel(CommentTransaction commentTransaction) {
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        try {
            commentTransaction.setState(TransactionState.CANCELED);
            Message message = new Message(
                    commentTransaction.getSourceUser(),
                    null,
                    "Transfer failure.",
                    String.format("Failed to transfer %d points to the comment by %s in the post '%s', transaction was canceled.",
                            commentTransaction.getAmount(),
                            commentTransaction.getComment().getAuthor().getName(),
                            commentTransaction.getComment().getPost().getTitle()));

            message.setSourceDeleted(true);
            session.update(commentTransaction);
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
    public List<CommentTransaction> getUnprocessed() {
        Session session = this.sessionFactory.openSession();
        List<CommentTransaction> commentTransactionList = session
                .createQuery("from CommentTransaction p where p.state=:state")
                .setParameter("state", TransactionState.UNPROCESSED)
                .list();
        session.close();
        return commentTransactionList;
    }
}
