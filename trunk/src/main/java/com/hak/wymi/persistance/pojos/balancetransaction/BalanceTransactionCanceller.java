package com.hak.wymi.persistance.pojos.balancetransaction;

import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.message.Message;
import com.hak.wymi.persistance.pojos.user.Balance;
import com.hak.wymi.persistance.utility.DaoHelper;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BalanceTransactionCanceller {
    private final LockOptions pessimisticWrite = new LockOptions(LockMode.PESSIMISTIC_WRITE);

    @Autowired
    private SessionFactory sessionFactory;

    private static boolean cancelUnprocessed(Session session, BalanceTransaction transaction) {
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

    public boolean cancel(BalanceTransaction transaction) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            if (transaction.getState() == TransactionState.UNPROCESSED) {
                return cancelUnprocessed(session, transaction);
            } else if (transaction.getState() == TransactionState.PROCESSED) {
                return cancelProcessed(session, transaction);
            }
            return false;
        });
    }

    private boolean cancelProcessed(Session session, BalanceTransaction transaction) {
        final TransactionLog transactionLog = (TransactionLog) session
                .load(TransactionLog.class, transaction.getTransactionLog().getTransactionLogId(), pessimisticWrite);

        if (!transactionLog.getCanceled()) {
            final HasPointsBalance sourceBalance = (HasPointsBalance) session
                    .load(Balance.class, transaction.getSourceUserId(), pessimisticWrite);

            final boolean cancelTargetSuccessful = removePointsFromTarget(session, transaction, transactionLog);

            session.buildLockRequest(pessimisticWrite).lock(transaction.getDestination());

            final HasPointsBalance siteBalance = (HasPointsBalance) session.load(Balance.class, -1, pessimisticWrite);

            final boolean removePointsFromTaxerSuccessful = removePointsFromTaxer(session, transaction, transactionLog);

            transactionLog.setCanceled(Boolean.TRUE);

            if (sourceBalance.addPoints(transactionLog.getAmountPayed())
                    && cancelTargetSuccessful
                    && transaction.getDestination().removePoints(transactionLog.getDestinationReceived())
                    && siteBalance.removePoints(transactionLog.getSiteReceived())
                    && removePointsFromTaxerSuccessful) {

                session.update(sourceBalance);
                session.update(transaction.getDestination());
                session.update(siteBalance);

                session.update(transactionLog);
                return cancelUnprocessed(session, transaction);
            }
        }

        return false;
    }

    private boolean removePointsFromTaxer(Session session, BalanceTransaction transaction, TransactionLog transactionLog) {
        if (transaction.getTaxerUserId() != null) {
            final HasPointsBalance topicOwnerBalance = (HasPointsBalance) session
                    .load(Balance.class, transaction.getTaxerUserId(), pessimisticWrite);

            if (topicOwnerBalance.removePoints(transactionLog.getTaxerReceived())) {
                session.update(topicOwnerBalance);
                return true;
            }
            return false;
        }
        return true;
    }

    private boolean removePointsFromTarget(Session session, BalanceTransaction transaction, TransactionLog transactionLog) {
        if (transaction.getTarget() != null) {
            final HasPointsBalance targetBalance = (HasPointsBalance) session
                    .load(transaction.getTarget().getClass(), transaction.getTarget().getBalanceId(), pessimisticWrite);

            if (targetBalance.removePoints(transactionLog.getTargetReceived())) {
                session.update(targetBalance);
                return true;
            }
            return false;
        }
        return true;
    }
}
