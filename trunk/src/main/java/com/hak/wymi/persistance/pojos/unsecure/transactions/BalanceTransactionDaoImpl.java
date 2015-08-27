package com.hak.wymi.persistance.pojos.unsecure.transactions;

import com.hak.wymi.persistance.pojos.unsecure.balance.Balance;
import com.hak.wymi.persistance.pojos.unsecure.comment.Comment;
import com.hak.wymi.persistance.pojos.unsecure.commenttransaction.CommentTransactionAbstract;
import com.hak.wymi.persistance.pojos.unsecure.commenttransaction.CommentTransactionDao;
import com.hak.wymi.persistance.pojos.unsecure.post.Post;
import com.hak.wymi.persistance.pojos.unsecure.posttransaction.PostTransactionAbstract;
import com.hak.wymi.persistance.pojos.unsecure.posttransaction.PostTransactionDao;
import com.hak.wymi.utility.DaoHelper;
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

    @Autowired
    private CommentTransactionDao commentTransactionDao;
    @Autowired
    private PostTransactionDao postTransactionDao;

    @Override
    public boolean process(PostTransactionAbstract postTransaction) {
        final boolean result = DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            final Integer amount = postTransaction.getAmount();
            final Balance sourceBalance = getBalance(session, postTransaction.getSourceUser().getUserId());
            final Balance destinationBalance = getBalance(session, postTransaction.getPost().getUser().getUserId());
            final Post post = (Post) session.load(Post.class, postTransaction.getPost().getPostId(), pessimisticWrite);

            session.buildLockRequest(pessimisticWrite).lock(postTransaction);

            sourceBalance.removePoints(amount);
            destinationBalance.addPoints(amount);
            post.addPoints(amount);
            postTransaction.setState(TransactionState.PROCESSED);

            session.update(sourceBalance);
            session.update(destinationBalance);
            session.update(post);
            session.update(postTransaction);
        });
        if (!result) {
            postTransactionDao.cancel(postTransaction);
        }
        return result;
    }

    @Override
    public boolean process(CommentTransactionAbstract commentTransaction) {
        final boolean result = DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            final Integer amount = commentTransaction.getAmount();

            final Balance sourceBalance = getBalance(session, commentTransaction.getSourceUser().getUserId());
            final Balance destinationBalance = getBalance(session, commentTransaction.getComment().getAuthor().getUserId());
            final Comment comment = (Comment) session.load(Comment.class, commentTransaction.getComment().getCommentId(), pessimisticWrite);

            session.buildLockRequest(pessimisticWrite).lock(commentTransaction);

            sourceBalance.removePoints(amount);
            destinationBalance.addPoints(amount);
            comment.addPoints(amount);
            commentTransaction.setState(TransactionState.PROCESSED);

            session.update(sourceBalance);
            session.update(destinationBalance);
            session.update(comment);
            session.update(commentTransaction);
        });
        if (!result) {
            commentTransactionDao.cancel(commentTransaction);
        }
        return result;
    }

    private Balance getBalance(Session session, Integer userId) {
        return (Balance) session
                .createQuery("from Balance where user.userId=:userId")
                .setParameter("userId", userId)
                .setLockOptions(pessimisticWrite)
                .uniqueResult();
    }
}