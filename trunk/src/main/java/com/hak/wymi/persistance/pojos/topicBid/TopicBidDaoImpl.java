package com.hak.wymi.persistance.pojos.topicBid;

import com.hak.wymi.persistance.pojos.balancetransaction.TransactionState;
import com.hak.wymi.persistance.utility.DaoHelper;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class TopicBidDaoImpl implements TopicBidDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean save(TopicBidCreation topicBidCreation) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            TopicBid topicBid = topicBidCreation.getTopicBid();

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
}
