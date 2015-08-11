package com.hak.wymi.persistance.pojos.unsecure.posttransaction;

import com.hak.wymi.persistance.pojos.unsecure.post.Post;
import com.hak.wymi.persistance.pojos.unsecure.user.User;
import org.hibernate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.bind.ValidationException;

@Repository
@SuppressWarnings("unchecked")
public class PostTransactionDaoImpl implements PostTransactionDao {
    protected static final Logger logger = LoggerFactory.getLogger(PostTransactionDao.class);

    @Autowired
    private SessionFactory sessionFactory;

    LockOptions lockOptions = new LockOptions(LockMode.PESSIMISTIC_WRITE);

    @Override
    public boolean save(PostTransaction postTransaction) throws ValidationException {
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        Integer amount = postTransaction.getAmount();

        try {
            User sourceUser = (User) session.load(User.class, postTransaction.getSourceUser().getUserId(), lockOptions);
            User destinationUser = (User) session.load(User.class, postTransaction.getPost().getUser().getUserId(), lockOptions);
            Post post = (Post) session.load(Post.class, postTransaction.getPost().getPostId(), lockOptions);

            sourceUser.removePoints(amount);
            destinationUser.addPoints(amount);
            post.addPoints(amount);

            session.update(sourceUser);
            session.update(destinationUser);
            session.update(post);
            session.persist(postTransaction);

            tx.commit();

            return true;
        } catch (HibernateException e) {
            logger.error(e.getMessage());
            tx.rollback();
            return false;
        } finally {
            session.close();
        }
    }
}
