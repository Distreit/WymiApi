package com.hak.wymi.persistance.pojos.topicbid;

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
public class TopicBidDaoImpl implements TopicBidDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override

    @Transactional(propagation = Propagation.MANDATORY)
    public boolean save(TopicBidCreation topicBidCreation) {
        final Session session = sessionFactory.getCurrentSession();
        final TopicBid topicBid = topicBidCreation.getTopicBid();
        topicBid.setTopicBidCreation(topicBidCreation);

        topicBidCreation.setState(TransactionState.UNPROCESSED);
        session.save(topicBid);
        session.refresh(topicBid);
        topicBidCreation.setTopicBidId(topicBid.getTopicBidId());

        session.save(topicBidCreation);
        session.flush();
        session.refresh(topicBidCreation);
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<TopicBid> get(String topicName, TopicBidState state) {
        final Session session = sessionFactory.getCurrentSession();
        final List<TopicBid> topicBids = session.createQuery("from TopicBid where topic.name=:topicName and state=:state")
                .setParameter("topicName", topicName)
                .setParameter("state", state)
                .list();
        return topicBids;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public TopicBidCreation getTransaction(Integer topicBidId) {
        final Session session = sessionFactory.getCurrentSession();
        final TopicBidCreation topicBidCreation = (TopicBidCreation) session.createQuery("from TopicBidCreation where topicBidId=:topicBidId")
                .setParameter("topicBidId", topicBidId)
                .uniqueResult();
        topicBidCreation.getTransactionLog().getCanceled();
        return topicBidCreation;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<TopicBid> getForRentTransaction(String topicName) {
        final Session session = sessionFactory.getCurrentSession();
        final List<TopicBid> topicBids = session
                .createQuery("from TopicBid where topic.name=:topicName and state=:state and topicBidCreation.state=:creationState order by created")
                .setParameter("topicName", topicName)
                .setParameter("state", TopicBidState.WAITING)
                .setParameter("creationState", TransactionState.PROCESSED)
                .list();
        return topicBids;
    }
}
