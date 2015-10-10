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
public class CommentDonationDaoImpl implements CommentDonationDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean save(CommentDonation commentDonation) {
        final Session session = sessionFactory.getCurrentSession();
        commentDonation.setState(TransactionState.UNPROCESSED);
        session.persist(commentDonation);
        session.refresh(commentDonation);
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<CommentDonation> getUnprocessed() {
        return sessionFactory.getCurrentSession()
                .createQuery("from CommentDonation p where p.state=:state")
                .setParameter("state", TransactionState.UNPROCESSED)
                .list();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<CommentDonation> get(String topicName) {
        return sessionFactory.getCurrentSession()
                .createQuery("from CommentDonation where state=:state and comment.post.topic.name=:topicName")
                .setParameter("state", TransactionState.PROCESSED)
                .setParameter("topicName", topicName)
                .list();
    }
}
