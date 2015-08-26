package com.hak.wymi.persistance.pojos.unsecure.commenttransaction;

import com.hak.wymi.persistance.pojos.unsecure.message.Message;
import com.hak.wymi.persistance.pojos.unsecure.transactions.TransactionState;
import com.hak.wymi.utility.DaoHelper;
import org.hibernate.*;
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
        });
    }

    @Override
    public boolean cancel(CommentTransaction commentTransaction) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            commentTransaction.setState(TransactionState.CANCELED);

            String messageString = "Failed to transfer %d points to the comment by %s in the post '%s', transaction was canceled.";
            messageString = String.format(messageString,
                    commentTransaction.getAmount(),
                    commentTransaction.getComment().getAuthor().getName(),
                    commentTransaction.getComment().getPost().getTitle());

            Message message = new Message(commentTransaction.getSourceUser(), null, "Transfer failure.", messageString);
            message.setSourceDeleted(true);
            session.update(commentTransaction);
            session.save(message);
        });
    }

    @Override
    public List<CommentTransaction> getUnprocessed() {
        Session session = sessionFactory.openSession();
        List<CommentTransaction> commentTransactionList = session
                .createQuery("from CommentTransaction p where p.state=:state")
                .setParameter("state", TransactionState.UNPROCESSED)
                .list();
        session.close();
        return commentTransactionList;
    }
}
