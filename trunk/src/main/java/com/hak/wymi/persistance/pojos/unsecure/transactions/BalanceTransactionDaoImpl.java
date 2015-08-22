package com.hak.wymi.persistance.pojos.unsecure.transactions;

import com.hak.wymi.persistance.pojos.unsecure.balance.Balance;
import com.hak.wymi.persistance.pojos.unsecure.comment.Comment;
import com.hak.wymi.persistance.pojos.unsecure.commenttransaction.CommentTransaction;
import com.hak.wymi.persistance.pojos.unsecure.commenttransaction.CommentTransactionDao;
import com.hak.wymi.persistance.pojos.unsecure.post.Post;
import com.hak.wymi.persistance.pojos.unsecure.posttransaction.PostTransaction;
import com.hak.wymi.persistance.pojos.unsecure.posttransaction.PostTransactionDao;
import org.hibernate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.bind.ValidationException;

@Repository
@SuppressWarnings("unchecked")
public class BalanceTransactionDaoImpl implements BalanceTransactionDao {
    protected static final Logger logger = LoggerFactory.getLogger(BalanceTransactionDaoImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private CommentTransactionDao commentTransactionDao;

    @Autowired
    private PostTransactionDao postTransactionDao;
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
            postTransaction.setState(TransactionState.PROCESSED);

            session.update(sourceBalance);
            session.update(destinationBalance);
            session.update(post);
            session.update(postTransaction);

            tx.commit();
            session.close();
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            if (tx != null) {
                tx.rollback();
            }
            session.close();
            if (e instanceof ValidationException) {
                postTransactionDao.cancel(postTransaction);
            }
            return false;
        }
    }

    @Override
    public boolean process(CommentTransaction commentTransaction) {
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        Integer amount = commentTransaction.getAmount();

        try {
            Balance sourceBalance = (Balance) session.createQuery("from Balance where user.userId=:userId")
                    .setParameter("userId", commentTransaction.getSourceUser().getUserId())
                    .setLockOptions(pessimisticWrite).uniqueResult();

            Balance destinationBalance = (Balance) session.createQuery("from Balance where user.userId=:userId")
                    .setParameter("userId", commentTransaction.getComment().getAuthor().getUserId())
                    .setLockOptions(pessimisticWrite).uniqueResult();

            Comment comment = (Comment) session.load(Comment.class, commentTransaction.getComment().getCommentId(), pessimisticWrite);

            session.buildLockRequest(pessimisticWrite).lock(commentTransaction);

            sourceBalance.removePoints(amount);
            destinationBalance.addPoints(amount);
            comment.addPoints(amount);
            commentTransaction.setState(TransactionState.PROCESSED);

            session.update(sourceBalance);
            session.update(destinationBalance);
            session.update(comment);
            session.update(commentTransaction);

            tx.commit();
            session.close();
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            if (tx != null) {
                tx.rollback();
            }
            session.close();
            if (e instanceof ValidationException) {
                commentTransactionDao.cancel(commentTransaction);
            }
            return false;
        }
    }
}
