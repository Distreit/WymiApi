package com.hak.wymi.persistance.pojos.transactions.comment.creation;

import com.hak.wymi.persistance.pojos.transactions.TransactionState;
import com.hak.wymi.persistance.utility.DaoHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class CommentCreationImpl implements CommentCreationDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean save(CommentCreation commentCreation) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            commentCreation.setState(TransactionState.UNPROCESSED);
            session.save(commentCreation.getComment());
            session.refresh(commentCreation.getComment());
            commentCreation.setCommentId(commentCreation.getComment().getCommentId());
            session.save(commentCreation);
            session.flush();
            session.refresh(commentCreation);
            return true;
        });
    }

    @Override
    public List<CommentCreation> getUnprocessed() {
        final Session session = sessionFactory.openSession();
        final List<CommentCreation> commentCommentList = session
                .createQuery("from CommentCreation p where p.state=:state")
                .setParameter("state", TransactionState.UNPROCESSED)
                .list();
        session.close();
        return commentCommentList;
    }
}
