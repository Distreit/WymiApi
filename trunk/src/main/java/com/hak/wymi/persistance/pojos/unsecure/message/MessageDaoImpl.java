package com.hak.wymi.persistance.pojos.unsecure.message;

import com.hak.wymi.persistance.pojos.unsecure.balance.Balance;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.security.Principal;
import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class MessageDaoImpl implements MessageDao {
    protected static final Logger logger = LoggerFactory.getLogger(MessageDaoImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean save(Message message) {
        return saveOrUpdate(message, true);
    }

    private boolean saveOrUpdate(Message message, boolean save) {
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            if (save) {
                session.persist(message);
            } else {
                session.update(message);
            }
            tx.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            logger.error(e.getMessage());
            session.close();
            return false;
        }
    }

    @Override
    public List<Balance> getIncoming(Principal principal) {
        return null;
    }

    @Override
    public List<Balance> getSent(Principal principal) {
        return null;
    }
}
