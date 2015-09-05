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
    public boolean process(BalanceTransaction transaction) {
        final boolean result = DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            final Integer amount = transaction.getAmount();
            final Balance sourceBalance = getBalance(session, transaction.getSourceUserId());
            final Balance destinationBalance = getBalance(session, transaction.getDestinationUserId());
            final HasPointsBalance target = (HasPointsBalance) session.load(transaction.getTargetClass(), transaction.getTargetId(), pessimisticWrite);
            session.buildLockRequest(pessimisticWrite).lock(transaction);

            if (sourceBalance.removePoints(amount) && destinationBalance.addPoints(amount) && target.addPoints(amount)) {
                transaction.setState(TransactionState.PROCESSED);
                session.update(sourceBalance);
                session.update(destinationBalance);
                session.update(target);
                session.update(transaction);
                return true;
            }
            return false;
        });
        if (!result) {
            cancel(transaction);
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
                            "Transaction from to %s for %d canceled.",
                            transaction.getTargetUrl(),
                            transaction.getAmount()
                    ));

            if (transaction.getDependent() != null) {
                session.delete(transaction.getDependent());
            }

            message.setSourceDeleted(Boolean.TRUE);
            session.update(transaction);
            session.save(message);
            return true;
        });
    }
}