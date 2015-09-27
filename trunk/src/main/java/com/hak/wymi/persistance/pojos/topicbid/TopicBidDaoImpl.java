package com.hak.wymi.persistance.pojos.topicbid;

import com.hak.wymi.persistance.pojos.balancetransaction.TransactionState;
import com.hak.wymi.persistance.utility.DaoHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class TopicBidDaoImpl implements TopicBidDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean save(TopicBidCreation topicBidCreation) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            final TopicBid topicBid = topicBidCreation.getTopicBid();

            topicBidCreation.setState(TransactionState.UNPROCESSED);
            session.save(topicBid);
            session.refresh(topicBid);
            topicBidCreation.setTopicBidId(topicBid.getTopicBidId());

            session.save(topicBidCreation);
            session.flush();
            session.refresh(topicBidCreation);
            return true;
        });
    }

    @Override
    public List<TopicBid> get(String topicName) {
        final Session session = sessionFactory.openSession();
        final List<TopicBid> topicBids = session.createQuery("from TopicBid where topic.name=:topicName")
                .setParameter("topicName", topicName)
                .list();
        session.close();
        return topicBids;
    }

    @Override
    public TopicBidCreation getTransaction(Integer topicBidId) {
        final Session session = sessionFactory.openSession();
        final TopicBidCreation topicBidCreation = (TopicBidCreation) session.createQuery("from TopicBidCreation where topicBidId=:topicBidId")
                .setParameter("topicBidId", topicBidId)
                .uniqueResult();
        topicBidCreation.getTransactionLog().getCanceled();
        session.close();
        return topicBidCreation;
    }
}
