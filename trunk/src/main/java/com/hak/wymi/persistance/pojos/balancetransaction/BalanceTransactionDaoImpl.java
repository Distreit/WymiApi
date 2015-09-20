package com.hak.wymi.persistance.pojos.balancetransaction;

import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.message.Message;
import com.hak.wymi.persistance.pojos.user.Balance;
import com.hak.wymi.persistance.utility.DaoHelper;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class BalanceTransactionDaoImpl implements BalanceTransactionDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(BalanceTransactionDaoImpl.class);

    private static final Double TAX_RATE = 0.05;
    private static final Double ONE_HUNDRED = 100.0;

    private final LockOptions pessimisticWrite = new LockOptions(LockMode.PESSIMISTIC_WRITE);

    @Autowired
    private SessionFactory sessionFactory;

    private static boolean checkOutput(Integer startingAmount, Integer siteTax, Integer topicTax, Integer finalAmount) {
        if (startingAmount - siteTax - topicTax - finalAmount == 0) {
            return true;
        }

        LOGGER.error(String.format(
                "Transaction values didn't add up!!! (site tax: %d, topic tax: %d, final: %d, starting: %d)",
                siteTax, topicTax, finalAmount, startingAmount));
        return false;
    }

    @Override
    public boolean process(BalanceTransaction balanceTransaction) {
        final boolean result = DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            if (balanceTransaction.getAmount() > 0) {
                return processNonZeroTransaction(balanceTransaction, session);
            } else if (balanceTransaction.getAmount() == 0) {
                balanceTransaction.setState(TransactionState.PROCESSED);
                session.update(balanceTransaction);
                return true;
            }
            return false;
        });
        if (!result) {
            cancel(balanceTransaction);
        }
        return result;
    }

    private boolean processNonZeroTransaction(BalanceTransaction balanceTransaction, Session session) {
        final BalanceTransaction transaction = (BalanceTransaction) session
                .load(balanceTransaction.getClass(), balanceTransaction.getTransactionId(), pessimisticWrite);

        transaction.setTransactionLog(new TransactionLog(balanceTransaction));

        final boolean fromBalanceSuccessful = removePointsFromSourceUser(session, transaction);
        final boolean toTargetSuccessful = addPointsToTarget(session, transaction);
        final boolean toSplitSuccessful = splitPointsToReceivers(session, transaction);

        transaction.setState(TransactionState.PROCESSED);
        session.save(transaction.getTransactionLog());
        session.update(transaction);

        return fromBalanceSuccessful && toTargetSuccessful && toSplitSuccessful;
    }

    private boolean splitPointsToReceivers(Session session, BalanceTransaction transaction) {
        final Integer startingAmount = transaction.getAmount();

        final Integer siteTax = paySite(session, transaction);
        if (siteTax == null || siteTax < 0) {
            return false;
        }

        final Integer topicTax = payTopicOwner(session, transaction, siteTax);
        if (topicTax == null || topicTax < 0) {
            return false;
        }

        final Integer finalAmount = paySubmitter(session, transaction, siteTax, topicTax);
        return !(finalAmount == null || finalAmount < 0) && checkOutput(startingAmount, siteTax, topicTax, finalAmount);
    }

    private Integer paySubmitter(Session session, BalanceTransaction transaction, Integer siteTax, Integer topicTax) {
        final HasPointsBalance submitterBalance = (HasPointsBalance) session
                .load(transaction.getDestination().getClass(), transaction.getDestination().getBalanceId(), pessimisticWrite);
        final int remainingAmount = transaction.getAmount() - siteTax - topicTax;
        transaction.getTransactionLog().setDestinationReceived(remainingAmount);
        if (remainingAmount == 0) {
            LOGGER.debug(String.format("The contributor %s got %d", submitterBalance.getName(), remainingAmount));
            return remainingAmount;
        } else {
            if (submitterBalance.addPoints(remainingAmount)) {
                LOGGER.debug(String.format("The contributor %s got %d", submitterBalance.getName(), remainingAmount));
                session.update(submitterBalance);
                return remainingAmount;
            }
        }

        return null;
    }

    private Integer payTopicOwner(Session session, BalanceTransaction transaction, Integer siteTax) {
        final Double topicTaxRate = transaction.getTaxRate() / ONE_HUNDRED;

        if (topicTaxRate == 0) {
            transaction.getTransactionLog().setTaxerReceived(0);
            return 0;
        }

        final HasPointsBalance topicOwnerBalance = (HasPointsBalance) session.load(Balance.class, transaction.getTaxerUserId());
        Integer topicTax = (int) Math.max(transaction.getAmount() * topicTaxRate, 1);
        topicTax = Math.min(transaction.getAmount() - siteTax, topicTax);
        if (topicOwnerBalance.addPoints(topicTax)) {
            transaction.getTransactionLog().setTaxerReceived(topicTax);
            session.update(topicOwnerBalance);
            LOGGER.debug(String.format("The owner %s got %d", topicOwnerBalance.getName(), topicTax));
            return topicTax;
        }

        return null;
    }

    private Integer paySite(Session session, BalanceTransaction transaction) {
        if (!transaction.paySiteTax()) {
            transaction.getTransactionLog().setSiteReceived(0);
            return 0;
        }
        final HasPointsBalance sitesBalance = (HasPointsBalance) session.load(Balance.class, -1, pessimisticWrite);
        final Integer tax = Math.max(1, (int) (transaction.getAmount() * TAX_RATE));

        if (sitesBalance.addPoints(tax)) {
            transaction.getTransactionLog().setSiteReceived(tax);
            session.update(sitesBalance);
            LOGGER.debug(String.format("The site(%s) got %d", sitesBalance.getName(), tax));
            return tax;
        }
        return null;
    }

    private boolean addPointsToTarget(Session session, BalanceTransaction transaction) {
        if (transaction.getTarget() == null) {
            return true;
        }
        final HasPointsBalance targetBalance = (HasPointsBalance) session
                .load(transaction.getTarget().getClass(), transaction.getTarget().getBalanceId(), pessimisticWrite);
        if (targetBalance.addPoints(transaction.getAmount())) {
            transaction.getTransactionLog().setTargetReceived(transaction.getAmount());
            if (transaction.isUniqueToUser()) {
                targetBalance.incrementTransactionCount();
            }
            session.update(targetBalance);
            LOGGER.debug(String.format("Target got %d", transaction.getAmount()));
            return true;
        }

        return false;
    }

    private boolean removePointsFromSourceUser(Session session, BalanceTransaction transaction) {
        final HasPointsBalance fromBalance = (HasPointsBalance) session.load(Balance.class, transaction.getSourceUserId(), pessimisticWrite);
        if (fromBalance.removePoints(transaction.getAmount())) {
            transaction.getTransactionLog().setAmountPayed(transaction.getAmount());
            session.update(fromBalance);
            LOGGER.debug(String.format("The donator %s spent %d", fromBalance.getName(), transaction.getAmount()));
            return true;
        }
        return false;
    }

    @Override
    public boolean cancel(BalanceTransaction transaction) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            switch (transaction.getState()) {
                case UNCONFIRMED:
                    // Not in use.
                    break;
                case UNPROCESSED:
                    return cancelUnprocessed(session, transaction);
                case PROCESSED:
                    return cancelProcessed(session, transaction);
                default:
                    // Nothing to do.
                    break;
            }
            return false;
        });
    }

    private boolean cancelProcessed(Session session, BalanceTransaction transaction) {
        if (!transaction.paySiteTax() || transaction.getTaxRate() == 0) {
            final HasPointsBalance userBalance = (HasPointsBalance) session
                    .load(Balance.class, transaction.getSourceUserId(), pessimisticWrite);

            final HasPointsBalance destination = transaction.getDestination();
            final HasPointsBalance destinationBalance = (HasPointsBalance) session
                    .load(destination.getClass(), destination.getBalanceId(), pessimisticWrite);

            if (userBalance.addPoints(transaction.getAmount())
                    && destinationBalance.removePoints(transaction.getAmount())) {
                session.update(userBalance);
                session.update(destinationBalance);
                return true;
            }
        }
        return false;
    }

    private boolean cancelUnprocessed(Session session, BalanceTransaction transaction) {
        transaction.setState(TransactionState.CANCELED);
        final Message message = new Message(transaction.getSourceUser(), null, "Transfer failure",
                String.format("Transaction from %s for %d canceled.",
                        transaction.getTargetUrl(),
                        transaction.getAmount()));

        if (transaction.getDependent() == null) {
            session.update(transaction);
        } else {
            session.delete(transaction);
            session.delete(transaction.getDependent());
        }

        message.setSourceDeleted(Boolean.TRUE);
        session.save(message);
        return true;
    }
}