package com.hak.wymi.persistance.pojos.ownershiptransaction;

import com.hak.wymi.persistance.pojos.balancetransaction.BalanceTransaction;
import com.hak.wymi.persistance.pojos.balancetransaction.BalanceTransactionCanceller;
import com.hak.wymi.persistance.pojos.message.Message;
import com.hak.wymi.persistance.pojos.message.MessageDao;
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

import java.util.Collection;
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

    @Autowired
    private MessageDao messageDao;

    @Value("${site.taxRate}")
    private Double taxRate;

    /**
     * Creates and saves a list of dispersion transactions based on the amounts passed in.
     *
     * @param session      The transactions session.
     * @param winningRanks The list of users to create transactions for.
     * @param eachGets     Each user is guaranteed to get at least this amount.
     * @param remainder    This value should be less than the number of users. The top n users will each get an extra
     *                     point.
     * @param topicBid     The winning bid for control of the topic.
     *
     * @return A list of dispersion transactions that have been saved to the database and need to be processed.
     */
    private static Collection<? extends TopicBidDispersion>
    createUserDispersions(Session session, List<UserTopicRank> winningRanks, int eachGets, int remainder, TopicBid topicBid) {
        final List<TopicBidDispersion> transactions = new LinkedList<>();
        final int iLength = winningRanks.size();

        for (int i = 0; i < iLength; i += 1) {
            int portion = eachGets;
            if (i < remainder) {
                portion += 1;
            }
            final TopicBidDispersion dispersion = new TopicBidDispersion(winningRanks.get(i).getUser(), topicBid, portion);
            LOGGER.debug("User: {}, Portion: {}", winningRanks.get(i).getUser().getName(), portion);
            session.save(dispersion);
            transactions.add(dispersion);
        }
        return transactions;
    }

    @Override
    public List<TopicBidDispersion> process(OwnershipTransaction ownershipTransaction, List<UserTopicRank> winningRanks) {
        final List<TopicBidDispersion> transactions = new LinkedList<>();

        final boolean changeSuccessful = DaoHelper.genericTransaction(sessionFactory.openSession(),
                session -> changeOwnerAndSplitRent(session, transactions, ownershipTransaction, winningRanks));

        if (changeSuccessful) {
            final String messageText = String
                    .format("Congratulations, you are now/still the administrator of the topic %s",
                            ownershipTransaction.getTopic().getUrl());
            final Message message = new Message(
                    ownershipTransaction.getTopic().getOwner(), null, "Topic ownership", messageText);
            messageDao.save(message);
            return transactions;
        }
        return null;
    }

    private boolean changeOwnerAndSplitRent(Session session,
                                            List<TopicBidDispersion> transactions,
                                            OwnershipTransaction ownershipTransaction,
                                            List<UserTopicRank> winningRanks) {
        session.buildLockRequest(pessimisticWrite).lock(ownershipTransaction);

        final Topic topic = ownershipTransaction.getTopic();
        session.buildLockRequest(pessimisticWrite).lock(topic);

        final TopicBid topicBid = ownershipTransaction.getWinningBid();
        session.buildLockRequest(pessimisticWrite).lock(topicBid);

        if (topicBid.getCurrentBalance() > 0) {
            transactions.addAll(divideBalanceBetweenContributors(session, winningRanks, topicBid));
        }

        ownershipTransaction.setState(OwnershipTransactionState.PROCESSED);
        topic.setOwner(topicBid.getUser());
        topicBid.setState(TopicBidState.PROCESSED);

        session.update(topicBid);
        session.update(ownershipTransaction);
        session.update(topic);
        return true;
    }

    /**
     * Creates and saves a list of dispersion transactions to divide the bids balance between the contributors listed
     * and keeps a portion for the site.
     *
     * @param session      The transactions session
     * @param winningRanks The bids balance will be divided between the users listed here and the site.
     * @param topicBid     The winning bid for control of the topic.
     *
     * @return A list of new dispersion transactions that need to be processed.
     */
    private Collection<? extends TopicBidDispersion>
    divideBalanceBetweenContributors(Session session, List<UserTopicRank> winningRanks, TopicBid topicBid) {
        final List<TopicBidDispersion> transactions = new LinkedList<>();
        final int siteTax;
        int amount = topicBid.getCurrentBalance();
        if (winningRanks.isEmpty()) {
            siteTax = topicBid.getCurrentBalance();
        } else {
            siteTax = (int) Math.max(1, amount * taxRate);
            amount -= siteTax;
            final int eachGets = amount / winningRanks.size();
            final int remainder = amount - (eachGets * winningRanks.size());
            LOGGER.debug("Site tax: {}, Each: {}, Remainder: {}", siteTax, eachGets, remainder);

            transactions.addAll(createUserDispersions(session, winningRanks, eachGets, remainder, topicBid));
        }

        final User siteUser = (User) session.load(User.class, -1, pessimisticWrite);
        final TopicBidDispersion topicBidDispersion = new TopicBidDispersion(siteUser, topicBid, siteTax);
        transactions.add(topicBidDispersion);
        session.save(topicBidDispersion);
        return transactions;
    }

    @Override
    public boolean save(OwnershipTransaction ownershipTransaction, List<TopicBid> failedBids) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            final Topic topic = ownershipTransaction.getTopic();
            session.buildLockRequest(new LockOptions(LockMode.PESSIMISTIC_WRITE)).lock(topic);

            if (failedBids != null) {
                final TopicBid winningBid = ownershipTransaction.getWinningBid();
                if (winningBid != null) {
                    cancelFailedBids(session, failedBids, winningBid);
                    winningBid.setState(TopicBidState.ACCEPTED);
                    session.update(winningBid);
                }
            }

            topic.setRentDueDate(getNextRentExpirationDate(topic));
            session.update(topic);
            session.save(ownershipTransaction);
            return true;
        });
    }

    private void cancelFailedBids(Session session, List<TopicBid> failedBids, TopicBid winningBid) {
        failedBids.stream()
                .filter(b -> !b.equals(winningBid))
                .forEach(t -> BalanceTransactionCanceller
                        .cancelUnprocessed(session, (BalanceTransaction) t.getTopicBidCreation()));
    }

    private DateTime getNextRentExpirationDate(Topic topic) {
        final int randHours = ThreadLocalRandom.current().nextInt(0, HOURS_IN_A_DAY);
        return topic.getRentDueDate()
                .plusSeconds(RENT_PERIOD_SECONDS)
                .dayOfMonth()
                .roundFloorCopy()
                .plusHours(randHours);
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
