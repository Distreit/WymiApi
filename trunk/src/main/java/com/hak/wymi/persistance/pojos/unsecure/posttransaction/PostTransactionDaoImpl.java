package com.hak.wymi.persistance.pojos.unsecure.posttransaction;

import com.hak.wymi.persistance.pojos.unsecure.post.Post;
import org.hibernate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class PostTransactionDaoImpl implements PostTransactionDao {
    protected static final Logger logger = LoggerFactory.getLogger(PostTransactionDao.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean save(PostTransaction postTransaction) {
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        try {
            postTransaction.setProcessed(false);
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

    @Override
    public List<PostTransaction> getUnprocessed() {
        Session session = this.sessionFactory.openSession();
        List<PostTransaction> postTransactionList = session.createQuery("from PostTransaction p where p.processed=false")
                .list();
        session.close();
        return postTransactionList;
    }
}
