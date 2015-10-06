package com.hak.wymi.persistance.pojos.comment;

import com.hak.wymi.persistance.pojos.balancetransaction.TransactionState;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
@SuppressWarnings("unchecked")
public class CommentCreationImpl implements CommentCreationDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean save(CommentCreation commentCreation) {
        final Session session = sessionFactory.getCurrentSession();
        commentCreation.setState(TransactionState.UNPROCESSED);
        session.save(commentCreation.getComment());
        session.refresh(commentCreation.getComment());
        commentCreation.setCommentId(commentCreation.getComment().getCommentId());
        session.save(commentCreation);
        session.flush();
        session.refresh(commentCreation);
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<CommentCreation> getUnprocessed() {
        final Session session = sessionFactory.getCurrentSession();
        final List<CommentCreation> commentCommentList = session
                .createQuery("from CommentCreation p where p.state=:state")
                .setParameter("state", TransactionState.UNPROCESSED)
                .list();
        return commentCommentList;
    }
}
