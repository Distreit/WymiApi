package com.hak.wymi.persistance.pojos.ownershiptransaction;

import com.hak.wymi.persistance.pojos.balancetransaction.BalanceTransaction;
import com.hak.wymi.persistance.pojos.balancetransaction.BalanceTransactionCanceller;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.topicbid.TopicBid;
import com.hak.wymi.persistance.pojos.topicbid.TopicBidDispersion;
import com.hak.wymi.persistance.pojos.topicbid.TopicBidState;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.persistance.pojos.usertopicrank.UserTopicRank;
import com.hak.wymi.persistance.utility.DaoHelper;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Repository
@SuppressWarnings("unchecked")
public class OwnershipTransactionDaoImpl implements OwnershipTransactionDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(OwnershipTransactionDaoImpl.class);

    private static final int HOURS_IN_A_DAY = 24;
    private static final int RENT_PERIOD_SECONDS = 30 * 24 * 60 * 60;

    private final LockOptions pessimisticWrite = new LockOptions(LockMode.PESSIMISTIC_WRITE);

    @Autowired
    private SessionFactory sessionFactory;

    @Value("${site.taxRate}")
    private Double taxRate;

    @Override
    public List<TopicBidDispersion> process(OwnershipTransaction ownershipTransaction, List<UserTopicRank> winningRanks) {
        final List<TopicBidDispersion> transactions = new LinkedList<>();

        if (DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            session.buildLockRequest(pessimisticWrite).lock(ownershipTransaction);

            final Topic topic = ownershipTransaction.getTopic();
            session.buildLockRequest(pessimisticWrite).lock(topic);

            final TopicBid topicBid = ownershipTransaction.getWinningBid();
            session.buildLockRequest(pessimisticWrite).lock(topicBid);

            int amount = topicBid.getCurrentBalance();

            if (amount > 0) {
                final int siteTax;
                if (winningRanks.size() > 0) {
                    siteTax = (int) Math.max(1, amount * taxRate);
                    amount -= siteTax;

                    final int eachGets = amount / winningRanks.size();
                    final int remainder = amount - (eachGets * winningRanks.size());

                    LOGGER.debug("Site tax: {}, Each: {}, Remainder: {}", siteTax, eachGets, remainder);

                    final int iLength = winningRanks.size();
                    for (int i = 0; i < iLength; i += 1) {
                        int portion = eachGets;
                        if (i < remainder) {
                            portion += 1;
                        }
                        TopicBidDispersion dispersion = new TopicBidDispersion(winningRanks.get(i).getUser(), topicBid, portion);
                        LOGGER.debug("User: {}, Portion: {}", winningRanks.get(i).getUser().getName(), portion);
                        session.save(dispersion);
                        transactions.add(dispersion);
                    }
                } else {
                    siteTax = topicBid.getCurrentBalance();
                }

                final User siteUser = (User) session.load(User.class, -1, pessimisticWrite);
                final TopicBidDispersion topicBidDispersion = new TopicBidDispersion(siteUser, topicBid, siteTax);
                transactions.add(topicBidDispersion);
                session.save(topicBidDispersion);
            }

            ownershipTransaction.setState(OwnershipTransactionState.PROCESSED);
            topic.setOwner(topicBid.getUser());
            topicBid.setState(TopicBidState.PROCESSED);

            session.update(topicBid);
            session.update(ownershipTransaction);
            session.update(topic);
            return true;
        })) {
            // TODO: EMAIL NEW OWNER.
            return transactions;
        }
        return new LinkedList<>();
    }

    @Override
    public boolean save(OwnershipTransaction ownershipTransaction, List<TopicBid> failedBids) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            final Topic topic = ownershipTransaction.getTopic();
            session.buildLockRequest(new LockOptions(LockMode.PESSIMISTIC_WRITE)).lock(topic);

            if (failedBids != null) {
                final TopicBid winningBid = ownershipTransaction.getWinningBid();
                if (winningBid != null) {
                    failedBids.stream()
                            .filter(b -> !b.equals(winningBid))
                            .forEach(t -> BalanceTransactionCanceller
                                    .cancelUnprocessed(session, (BalanceTransaction) t.getTopicBidCreation()));
                    winningBid.setState(TopicBidState.ACCEPTED);
                    session.update(winningBid);
                }
            }

            final int randHours = ThreadLocalRandom.current().nextInt(0, HOURS_IN_A_DAY);
            topic.setRentDueDate(topic
                    .getRentDueDate()
                    .plusSeconds(RENT_PERIOD_SECONDS)
                    .dayOfMonth()
                    .roundFloorCopy()
                    .plusHours(randHours));
            session.update(topic);
            session.save(ownershipTransaction);
            return true;
        });
    }

    @Override
    public List<OwnershipTransaction> getRentPeriodExpired() {
        final Session session = sessionFactory.openSession();
        final List<OwnershipTransaction> ownershipTransactions = session
                .createQuery("from OwnershipTransaction where waitingPeriodExpiration<:now and state=:state")
                .setParameter("now", new DateTime())
                .setParameter("state", OwnershipTransactionState.WAITING)
                .list();
        session.close();
        return ownershipTransactions;
    }
}
