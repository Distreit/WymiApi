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
                .createQuery("FROM CommentDonation p WHERE p.state=:state")
                .setParameter("state", TransactionState.UNPROCESSED)
                .list();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<CommentDonation> get(String topicName) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM CommentDonation WHERE state=:state AND comment.post.topic.name=:topicName")
                .setParameter("state", TransactionState.PROCESSED)
                .setParameter("topicName", topicName)
                .list();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<CommentDonation> getForUser(String userName, Integer firstResult, Integer maxResults) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM CommentDonation WHERE state=:state AND sourceUser.name=:userName ORDER BY created DESC")
                .setParameter("state", TransactionState.PROCESSED)
                .setParameter("userName", userName)
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .list();
    }
}
