package com.hak.wymi.persistance.pojos.unsecure.commenttransaction;

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
public class CommentTransactionDaoImpl implements CommentTransactionDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean save(CommentTransactionAbstract commentTransaction) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            commentTransaction.setState(TransactionState.UNPROCESSED);
            session.persist(commentTransaction);
            session.refresh(commentTransaction);
        });
    }

    @Override
    public boolean cancel(CommentTransactionAbstract commentTransaction) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            commentTransaction.setState(TransactionState.CANCELED);

            String messageString = "Failed to transfer %d points to the comment by %s in the post '%s', transaction was canceled.";
            messageString = String.format(messageString,
                    commentTransaction.getAmount(),
                    commentTransaction.getComment().getAuthor().getName(),
                    commentTransaction.getComment().getPost().getTitle());

            final Message message = new Message(commentTransaction.getSourceUser(), null, "Transfer failure.", messageString);
            message.setSourceDeleted(Boolean.TRUE);
            session.update(commentTransaction);
            session.save(message);
        });
    }

    @Override
    public List<CommentTransactionAbstract> getUnprocessed() {
        final Session session = sessionFactory.openSession();
        final List<CommentTransactionAbstract> commentTransactionList = session
                .createQuery("from CommentTransactionAbstract p where p.state=:state")
                .setParameter("state", TransactionState.UNPROCESSED)
                .list();
        session.close();
        return commentTransactionList;
    }
}
