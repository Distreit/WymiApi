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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class BalanceTransactionDaoImpl implements BalanceTransactionDao {
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
                final Balance sourceBalance = getBalance(session, transaction.getSourceUserId());
                final Balance destinationBalance = getBalance(session, transaction.getDestinationUserId());
                final Balance adminBalance = getBalance(session, -1);
                final HasPointsBalance target = (HasPointsBalance) session
                        .load(transaction.getTargetClass(), transaction.getTargetId(), pessimisticWrite);

                final Integer tax = Math.max(1, (int) (amount * TAX_RATE));

                if (sourceBalance.removePoints(amount)
                        && destinationBalance.addPoints(amount - tax)
                        && target.addPoints(amount)
                        && adminBalance.addPoints(tax)) {

                    transaction.setState(TransactionState.PROCESSED);
                    session.update(sourceBalance);
                    session.update(destinationBalance);
                    session.update(target);
                    session.update(transaction);
                    return true;
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