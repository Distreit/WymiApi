package com.hak.wymi.persistance.pojos.unsecure.transactions;

import com.hak.wymi.persistance.pojos.unsecure.balance.Balance;
import com.hak.wymi.persistance.pojos.unsecure.post.Post;
import com.hak.wymi.persistance.pojos.unsecure.posttransaction.PostTransaction;
import com.hak.wymi.persistance.pojos.unsecure.user.User;
import org.hibernate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class BalanceTransactionDaoImpl implements BalanceTransactionDao {
    protected static final Logger logger = LoggerFactory.getLogger(BalanceTransactionDaoImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    LockOptions pessimisticWrite = new LockOptions(LockMode.PESSIMISTIC_WRITE);

    @Override
    public boolean process(PostTransaction postTransaction) {
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        Integer amount = postTransaction.getAmount();

        try {
            Balance sourceBalance = (Balance) session.createQuery("from Balance where user.userId=:userId")
                    .setParameter("userId", postTransaction.getSourceUser().getUserId())
                    .setLockOptions(pessimisticWrite).uniqueResult();

            Balance destinationBalance = (Balance) session.createQuery("from Balance where user.userId=:userId")
                    .setParameter("userId", postTransaction.getPost().getUser().getUserId())
                    .setLockOptions(pessimisticWrite).uniqueResult();

            Post post = (Post) session.load(Post.class, postTransaction.getPost().getPostId(), pessimisticWrite);

            session.buildLockRequest(pessimisticWrite).lock(postTransaction);

            sourceBalance.removePoints(amount);
            destinationBalance.addPoints(amount);
            post.addPoints(amount);
            postTransaction.setProcessed(true);

            session.update(sourceBalance);
            session.update(destinationBalance);
            session.update(post);
            session.update(postTransaction);

            tx.commit();

            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            tx.rollback();
            return false;
        } finally {
            session.close();
        }
    }
}
