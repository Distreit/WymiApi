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

    private final LockOptions pessimisticWrite = new LockOptions(LockMode.PESSIMISTIC_WRITE);

    @Autowired
    private SessionFactory sessionFactory;

    private Balance getBalance(Session session, Integer userId) {
        return (Balance) session
                .createQuery("from Balance where user.userId=:userId")
                .setParameter("userId", userId)
                .setLockOptions(pessimisticWrite)
                .uniqueResult();
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

        final boolean fromBalanceSuccessful = removePointsFromSourceUser(session, transaction);
        final boolean toTargetSuccessful = addPointsToTarget(session, transaction);
        final boolean toSplitSuccessful = splitPointsToReceivers(session, transaction);

        transaction.setState(TransactionState.PROCESSED);
        session.update(transaction);

        return fromBalanceSuccessful && toTargetSuccessful && toSplitSuccessful;
    }

    private boolean splitPointsToReceivers(Session session, BalanceTransaction transaction) {
        Integer siteTax = paySite(session, transaction);
        if (siteTax == null || siteTax < 0) {
            return false;
        }

        Integer topicTax = payTopicOwner(session, transaction, siteTax);
        if (topicTax == null || topicTax < 0) {
            return false;
        }

        return paySubmitter(session, transaction, siteTax, topicTax);
    }

    private boolean paySubmitter(Session session, BalanceTransaction transaction, Integer siteTax, Integer topicTax) {
        final Balance submitterBalance = getBalance(session, transaction.getDestinationUserId());
        int remainingAmount = transaction.getAmount() - siteTax - topicTax;
        if (remainingAmount == 0) {
            LOGGER.debug(String.format("The contributor %s got %d", submitterBalance.getUser().getName(), remainingAmount));
            return true;
        } else {
            if (submitterBalance.addPoints(remainingAmount)) {
                LOGGER.debug(String.format("The contributor %s got %d", submitterBalance.getUser().getName(), remainingAmount));
                session.update(submitterBalance);
                return true;
            }
        }

        return false;
    }

    private Integer payTopicOwner(Session session, BalanceTransaction transaction, Integer siteTax) {
        final Double topicTaxRate = Double.valueOf(transaction.getTaxRate()) / 100.0;

        if (topicTaxRate != 0) {
            final Balance topicOwnerBalance = getBalance(session, transaction.getTaxerUserId());
            Integer topicTax = (int) Math.max((transaction.getAmount() * topicTaxRate), 1);
            topicTax = Math.min(transaction.getAmount() - siteTax, topicTax);
            if (topicOwnerBalance.addPoints(topicTax)) {
                session.update(topicOwnerBalance);
                LOGGER.debug(String.format("The owner %s got %d", topicOwnerBalance.getUser().getName(), topicTax));
                return topicTax;
            }
        } else if (topicTaxRate == 0) {
            return 0;
        }

        return null;
    }

    private Integer paySite(Session session, BalanceTransaction transaction) {
        final Balance sitesBalance = getBalance(session, -1);
        final Integer tax = Math.max(1, (int) (transaction.getAmount() * TAX_RATE));

        if (sitesBalance.addPoints(tax)) {
            session.update(sitesBalance);
            LOGGER.debug(String.format("The site(%s) got %d", sitesBalance.getUser().getName(), tax));
            return tax;
        }
        return null;
    }

    private boolean addPointsToTarget(Session session, BalanceTransaction transaction) {
        final HasPointsBalance target = (HasPointsBalance) session
                .load(transaction.getTargetClass(), transaction.getTargetId(), pessimisticWrite);
        if (target.addPoints(transaction.getAmount())) {
            session.update(target);
            LOGGER.debug(String.format("Target got %d", transaction.getAmount()));
            return true;
        }

        return false;
    }

    private boolean removePointsFromSourceUser(Session session, BalanceTransaction transaction) {
        final Balance fromBalance = getBalance(session, transaction.getSourceUserId());
        if (fromBalance.removePoints(transaction.getAmount())) {
            session.update(fromBalance);
            LOGGER.debug(String.format("The donator %s spent %d", fromBalance.getUser().getName(), transaction.getAmount()));
            return true;
        }
        return false;
    }

    @Override
    public boolean cancel(BalanceTransaction transaction) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            transaction.setState(TransactionState.CANCELED);
            final Message message = new Message(
                    transaction.getSourceUser(),
                    null,
                    "Transfer failure",
                    String.format(
                            "Transaction from %s for %d canceled.",
                            transaction.getTargetUrl(),
                            transaction.getAmount()
                    ));

            if (transaction.getDependent() == null) {
                session.update(transaction);
            } else {
                session.delete(transaction);
                session.delete(transaction.getDependent());
            }

            message.setSourceDeleted(Boolean.TRUE);
            session.save(message);
            return true;
        });
    }
}