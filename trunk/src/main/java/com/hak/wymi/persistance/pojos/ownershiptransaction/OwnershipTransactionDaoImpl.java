package com.hak.wymi.persistance.pojos.ownershiptransaction;

import com.hak.wymi.persistance.pojos.balancetransaction.BalanceTransaction;
import com.hak.wymi.persistance.pojos.balancetransaction.BalanceTransactionCanceller;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.topicbid.TopicBid;
import com.hak.wymi.persistance.pojos.topicbid.TopicBidState;
import com.hak.wymi.persistance.utility.DaoHelper;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Repository
@SuppressWarnings("unchecked")
public class OwnershipTransactionDaoImpl implements OwnershipTransactionDao {
    private static final int HOURS_IN_A_DAY = 24;
    private static final int RENT_PERIOD_IN_DAYS = 30;

    @Autowired
    private SessionFactory sessionFactory;


    @Override
    public boolean process(OwnershipTransaction ownershipTransaction) {
        return false;
    }

    @Override
    public boolean save(OwnershipTransaction ownershipTransaction, List<TopicBid> failedBids) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            final Topic topic = ownershipTransaction.getTopic();
            session.buildLockRequest(new LockOptions(LockMode.PESSIMISTIC_WRITE)).lock(topic);

            if (failedBids != null) {
                final TopicBid winningBid = ownershipTransaction.getWinningBid();
                if (winningBid != null) {
                    failedBids.stream().filter(b -> !b.equals(winningBid))
                            .forEach(t -> BalanceTransactionCanceller.cancelUnprocessed(session, (BalanceTransaction) t.getTopicBidCreation()));
                    winningBid.setState(TopicBidState.ACCEPTED);
                    session.update(winningBid);
                }
            }

            final int randHours = ThreadLocalRandom.current().nextInt(0, HOURS_IN_A_DAY);
            topic.setRentDueDate(topic.getRentDueDate().plusDays(RENT_PERIOD_IN_DAYS).dayOfMonth().roundFloorCopy().plusHours(randHours));
            session.update(topic);
            session.save(ownershipTransaction);
            return true;
        });
    }
}
