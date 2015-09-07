package com.hak.wymi.persistance.pojos.transactions.balance;

import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.balance.Balance;
import com.hak.wymi.persistance.pojos.message.Message;
import com.hak.wymi.persistance.pojos.transactions.TransactionState;
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
                final BalanceTransaction transaction = (BalanceTransaction) session
                        .load(balanceTransaction.getClass(), balanceTransaction.getTransactionId(), pessimisticWrite);

                final Integer amount = transaction.getAmount();

                final Balance wherePointsAreComingFrom = getBalance(session, transaction.getSourceUserId());
                final Balance wherePointsAreGoingTo = getBalance(session, transaction.getDestinationUserId());
                final Balance sitesBalance = getBalance(session, -1);

                final HasPointsBalance target = (HasPointsBalance) session
                        .load(transaction.getTargetClass(), transaction.getTargetId(), pessimisticWrite);

                final Integer tax = Math.max(1, (int) (amount * TAX_RATE));

                Balance topicOwnerBalance = null;
                final Double topicOwnerTaxRate = Double.valueOf(transaction.getTaxRate()) / 100.0;
                Integer topicTax = 0;
                if (topicOwnerTaxRate != null && topicOwnerTaxRate != 0) {
                    topicOwnerBalance = getBalance(session, transaction.getTaxerUserId());
                    topicTax = (int) Math.max(((amount - tax) * topicOwnerTaxRate), 1);
                }

                if (wherePointsAreComingFrom.removePoints(amount)
                        && target.addPoints(amount)
                        && sitesBalance.addPoints(tax)) {

                    LOGGER.info(String.format("Target got %d", amount));
                    LOGGER.info(String.format("The donator %s spent %d", wherePointsAreComingFrom.getUser().getName(), amount));
                    LOGGER.info(String.format("The admin %s got %d", sitesBalance.getUser().getName(), tax));

                    int donation = amount - tax;
                    int topicOwnerTake = 0;
                    if (topicTax > 0 && donation > 0) {
                        topicOwnerTake = Math.min(donation, topicTax);
                        donation -= topicOwnerTake;
                    }

                    if (wherePointsAreGoingTo.addPoints(donation)) {
                        LOGGER.info(String.format("The contributor %s got %d", wherePointsAreGoingTo.getUser().getName(), donation));
                        if (topicTax != 0) {
                            if (topicOwnerBalance.addPoints(topicOwnerTake)) {
                                LOGGER.info(String.format("The owner %s got %d", topicOwnerBalance.getUser().getName(), topicOwnerTake));
                            } else {
                                return false;
                            }
                        }

                        if (amount - tax - donation - topicOwnerTake == 0) {
                            transaction.setState(TransactionState.PROCESSED);
                            session.update(wherePointsAreComingFrom);
                            session.update(wherePointsAreGoingTo);
                            session.update(target);
                            session.update(transaction);
                            return true;
                        } else {
                            LOGGER.error("TRANSACTION AMOUNTS DIDN'T ADD UP!");
                        }
                    }
                }
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

            if (transaction.getDependent() != null) {
                session.delete(transaction);
                session.delete(transaction.getDependent());
            } else {
                session.update(transaction);
            }

            message.setSourceDeleted(Boolean.TRUE);
            session.save(message);
            return true;
        });
    }
}