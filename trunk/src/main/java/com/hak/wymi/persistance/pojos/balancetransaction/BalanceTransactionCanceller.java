package com.hak.wymi.persistance.pojos.balancetransaction;

import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.message.Message;
import com.hak.wymi.persistance.pojos.user.Balance;
import com.hak.wymi.utility.JSONConverter;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BalanceTransactionCanceller {
    private final LockOptions pessimisticWrite = new LockOptions(LockMode.PESSIMISTIC_WRITE);

    @Autowired
    private SessionFactory sessionFactory;

    private static boolean cancelUnprocessed(Session session, BalanceTransaction transaction) {
        transaction.setState(TransactionState.CANCELED);

        if (transaction.getDependent() == null) {
            session.update(transaction);
        } else {
            session.delete(transaction);
            session.delete(transaction.getDependent());
        }

        final Message message = transaction.getCancellationMessage();
        if (message != null) {
            message.setSourceDeleted(Boolean.TRUE);
            session.save(message);
        }
        return true;
    }

    @Transactional
    public boolean cancel(BalanceTransaction transaction) throws InvalidValueException {
        final Session session = sessionFactory.getCurrentSession();
        if (transaction.getState() == TransactionState.UNPROCESSED) {
            return cancelUnprocessed(session, transaction);
        } else if (transaction.getState() == TransactionState.PROCESSED) {
            return cancelProcessed(session, transaction);
        }
        throw new UnsupportedOperationException("Transaction state not supported. \n" + JSONConverter.getJSON(transaction, true));
    }

    private boolean cancelProcessed(Session session, BalanceTransaction transaction) throws InvalidValueException {
        final TransactionLog transactionLog = (TransactionLog) session
                .load(TransactionLog.class, transaction.getTransactionLog().getTransactionLogId(), pessimisticWrite);

        if (!transactionLog.getCanceled()) {
            final HasPointsBalance sourceBalance = (HasPointsBalance) session
                    .load(transaction.getSource().getClass(), transaction.getSource().getBalanceId(), pessimisticWrite);

            final HasPointsBalance siteBalance = (HasPointsBalance) session
                    .load(Balance.class, -1, pessimisticWrite);

            session.buildLockRequest(pessimisticWrite).lock(transaction.getDestination());

            removePointsFromTarget(session, transaction, transactionLog);
            removePointsFromTaxer(session, transaction, transactionLog);
            sourceBalance.addPoints(transactionLog.getAmountPayed());
            transaction.getDestination().removePoints(transactionLog.getDestinationReceived());
            siteBalance.removePoints(transactionLog.getSiteReceived());

            transactionLog.setCanceled(Boolean.TRUE);

            session.update(sourceBalance);
            session.update(transaction.getDestination());
            session.update(siteBalance);

            session.update(transactionLog);
            return cancelUnprocessed(session, transaction);
        }

        return false;
    }

    private void removePointsFromTaxer(Session session, BalanceTransaction transaction, TransactionLog transactionLog)
            throws InvalidValueException {

        if (transaction.getTaxerUserId() != null) {
            final HasPointsBalance topicOwnerBalance = (HasPointsBalance) session
                    .load(Balance.class, transaction.getTaxerUserId(), pessimisticWrite);

            topicOwnerBalance.removePoints(transactionLog.getTaxerReceived());
            session.update(topicOwnerBalance);
        }
    }

    private void removePointsFromTarget(Session session, BalanceTransaction transaction, TransactionLog transactionLog)
            throws InvalidValueException {

        if (transaction.getTarget() != null) {
            final HasPointsBalance targetBalance = (HasPointsBalance) session
                    .load(transaction.getTarget().getClass(), transaction.getTarget().getBalanceId(), pessimisticWrite);

            targetBalance.removePoints(transactionLog.getTargetReceived());
            session.update(targetBalance);

        }
    }
}
