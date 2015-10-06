package com.hak.wymi.persistance.pojos.post;

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
public class PostDonationDaoImpl implements PostDonationDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean save(PostDonation postDonation) {
        final Session session = sessionFactory.getCurrentSession();
        postDonation.setState(TransactionState.UNPROCESSED);
        session.persist(postDonation);
        session.refresh(postDonation);
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<PostDonation> getUnprocessed() {
        return sessionFactory.getCurrentSession()
                .createQuery("from PostDonation p where p.state=:state")
                .setParameter("state", TransactionState.UNPROCESSED)
                .list();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<PostDonation> get(String topicName) {
        return sessionFactory.getCurrentSession()
                .createQuery("from PostDonation where state=:state and post.topic.name=:topicName")
                .setParameter("state", TransactionState.PROCESSED)
                .setParameter("topicName", topicName)
                .list();
    }
}
