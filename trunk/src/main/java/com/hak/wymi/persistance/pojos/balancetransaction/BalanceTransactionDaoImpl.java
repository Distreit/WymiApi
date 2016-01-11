package com.hak.wymi.persistance.pojos.balancetransaction;

import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.comment.CommentCreation;
import com.hak.wymi.persistance.pojos.comment.CommentDonation;
import com.hak.wymi.persistance.pojos.post.PostCreation;
import com.hak.wymi.persistance.pojos.post.PostDonation;
import com.hak.wymi.persistance.pojos.user.Balance;
import com.hak.wymi.utility.jsonconverter.JSONConverter;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class BalanceTransactionDaoImpl implements BalanceTransactionDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(BalanceTransactionDaoImpl.class);

    private static final Double ONE_HUNDRED = 100.0;
    private final LockOptions pessimisticWrite = new LockOptions(LockMode.PESSIMISTIC_WRITE);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private BalanceTransactionCanceller balanceTransactionCanceller;

    @Value("${site.taxRate}")
    private Double taxRate;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void process(BalanceTransaction transaction) throws InvalidValueException {

        final Session session = sessionFactory.getCurrentSession();
        final TransactionLog transactionLog = new TransactionLog(transaction);
        transaction.setTransactionLog(transactionLog);
        if (transaction.getAmount() > 0) {
            try {
                processNonZeroTransaction(transaction, session);
            } catch (InvalidValueException e) {
                transaction.setTransactionLog(null);
                throw e;
            }
        } else if (transaction.getAmount() == 0) {
            transaction.setState(TransactionState.PROCESSED);
            transactionLog.setAmountPayed(0);
            transactionLog.setTaxerReceived(0);
            transactionLog.setSiteReceived(0);
            transactionLog.setTargetReceived(0);
            transactionLog.setDestinationReceived(0);
            session.save(transaction.getTransactionLog());
            session.update(transaction);
        }
    }

    private void processNonZeroTransaction(BalanceTransaction transaction, Session session) throws InvalidValueException {
        session.buildLockRequest(pessimisticWrite).lock(transaction);

        removePointsFromSourceUser(session, transaction);
        addPointsToTarget(session, transaction);
        splitPointsToReceivers(session, transaction);

        transaction.setState(TransactionState.PROCESSED);
        session.save(transaction.getTransactionLog());
        session.update(transaction);
    }

    private void splitPointsToReceivers(Session session, BalanceTransaction transaction) throws InvalidValueException {
        final Integer startingAmount = transaction.getAmount();
        final int siteTax = paySite(session, transaction);
        final int topicTax = payTopicOwner(session, transaction, siteTax);
        final int finalAmount = paySubmitter(session, transaction, siteTax, topicTax);

        if (startingAmount - siteTax - topicTax - finalAmount != 0) {
            throw new InvalidValueException(String.format(
                    "Transaction values didn't add up!!! (site tax: %d, topic tax: %d, final: %d, starting: %d)%n %s",
                    siteTax, topicTax, finalAmount, startingAmount, JSONConverter.getJSON(transaction, true)));
        }
    }

    private int paySubmitter(Session session, BalanceTransaction transaction, int siteTax, int topicTax) throws InvalidValueException {
        HasPointsBalance destination;
        try {
            session.buildLockRequest(pessimisticWrite).lock(transaction.getDestination());
            destination = transaction.getDestination();
        } catch (NonUniqueObjectException exception) {
            LOGGER.trace("Transaction destination wasn't loaded yet so we couldn't lock it. Loading with lock instead.", exception);
            destination = (HasPointsBalance) session
                    .load(transaction.getDestination().getClass(), transaction.getDestination().getBalanceId(), pessimisticWrite);
        }

        final int remainingAmount = transaction.getAmount() - siteTax - topicTax;

        transaction.getTransactionLog().setDestinationReceived(remainingAmount);

        if (remainingAmount == 0) {
            LOGGER.debug(String.format("The contributor %s got %d", destination.getName(), remainingAmount));
            return remainingAmount;
        } else {
            destination.addPoints(remainingAmount);
            LOGGER.debug(String.format("The contributor %s got %d", destination.getName(), remainingAmount));
            session.update(destination);
            return remainingAmount;
        }
    }

    private int payTopicOwner(Session session, BalanceTransaction transaction, Integer siteTax) throws InvalidValueException {
        final Double topicTaxRate = transaction.getTaxRate() / ONE_HUNDRED;

        if (topicTaxRate == 0) {
            transaction.getTransactionLog().setTaxerReceived(0);
            return 0;
        }

        final HasPointsBalance topicOwnerBalance = (HasPointsBalance) session.load(Balance.class, transaction.getTaxerUserId());
        int topicTax = (int) Math.max(transaction.getAmount() * topicTaxRate, 1);

        topicTax = Math.min(transaction.getAmount() - siteTax, topicTax);
        topicOwnerBalance.addPoints(topicTax);
        transaction.getTransactionLog().setTaxerReceived(topicTax);
        session.update(topicOwnerBalance);
        LOGGER.debug(String.format("The owner %s got %d", topicOwnerBalance.getName(), topicTax));
        return topicTax;
    }

    private int paySite(Session session, BalanceTransaction transaction) throws InvalidValueException {
        if (!transaction.shouldPaySiteTax()) {
            transaction.getTransactionLog().setSiteReceived(0);
            return 0;
        }

        final HasPointsBalance sitesBalance = (HasPointsBalance) session.load(Balance.class, -1, pessimisticWrite);
        final Integer tax = Math.max(1, (int) (transaction.getAmount() * taxRate));

        sitesBalance.addPoints(tax);
        transaction.getTransactionLog().setSiteReceived(tax);
        session.update(sitesBalance);
        LOGGER.debug(String.format("The site(%s) got %d", sitesBalance.getName(), tax));
        return tax;
    }

    private void addPointsToTarget(Session session, BalanceTransaction transaction) throws InvalidValueException {
        if (transaction.getTarget() != null) {
            final HasPointsBalance targetBalance = (HasPointsBalance) session
                    .load(transaction.getTarget().getClass(), transaction.getTarget().getBalanceId(), pessimisticWrite);

            targetBalance.addPoints(transaction.getAmount());
            transaction.getTransactionLog().setTargetReceived(transaction.getAmount());
            if (transaction.isUniqueToUser()) {
                targetBalance.incrementTransactionCount();
            }
            session.update(targetBalance);
            LOGGER.debug(String.format("Target got %d", transaction.getAmount()));

        }
    }

    private void removePointsFromSourceUser(Session session, BalanceTransaction transaction) throws InvalidValueException {
        final HasPointsBalance fromBalance = (HasPointsBalance) session.load(transaction.getSource().getClass(), transaction.getSource().getBalanceId(), pessimisticWrite);

        fromBalance.removePoints(transaction.getAmount());
        transaction.getTransactionLog().setAmountPayed(transaction.getAmount());
        session.update(fromBalance);
        LOGGER.debug(String.format("The donator %s spent %d", fromBalance.getName(), transaction.getAmount()));
    }

    @Override
    public boolean cancel(BalanceTransaction transaction) throws InvalidValueException {
        return balanceTransactionCanceller.cancel(transaction);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<BalanceTransaction> getForUser(Class type, String userName, Integer firstResult, Integer maxResults) {
        Session session = sessionFactory.getCurrentSession();
        Query criteria = null;

        if (type.equals(CommentDonation.class)) {
            criteria = session.createQuery("FROM CommentDonation WHERE state=:state AND sourceUser.name=:userName AND comment.deleted != true ORDER BY created DESC");
        } else if (type.equals(CommentCreation.class)) {
            criteria = session.createQuery("FROM CommentCreation WHERE state=:state AND comment.author.name=:userName AND comment.deleted != true ORDER BY created DESC");
        } else if (type.equals(PostDonation.class)) {
            criteria = session.createQuery("FROM PostDonation WHERE state=:state AND sourceUser.name=:userName AND post.deleted != true ORDER BY created DESC");
        } else if (type.equals(PostCreation.class)) {
            criteria = session.createQuery("FROM PostCreation WHERE state=:state AND post.user.name=:userName AND post.deleted != true ORDER BY created DESC");
        }

        if (criteria != null) {
            return criteria.setParameter("state", TransactionState.PROCESSED)
                    .setParameter("userName", userName)
                    .setFirstResult(firstResult)
                    .setMaxResults(maxResults)
                    .list();
        }
        return new LinkedList<>();
    }
}