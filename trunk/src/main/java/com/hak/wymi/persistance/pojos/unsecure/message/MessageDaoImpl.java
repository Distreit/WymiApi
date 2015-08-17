package com.hak.wymi.persistance.pojos.unsecure.message;

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

    @Override
    public boolean update(Message message) {
        return saveOrUpdate(message, false);
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
            return true;
        } catch (HibernateException e) {
            logger.error(e.getMessage());
            if (tx != null) {
                tx.rollback();
            }
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public List<Message> getAllReceived(Principal principal) {
        Session session = this.sessionFactory.openSession();
        List<Message> postList = session.createQuery("from Message where destinationUser.name=:destinationUserName and destinationDeleted=false")
                .setParameter("destinationUserName", principal.getName())
                .list();
        session.close();
        return postList;
    }

    @Override
    public List<Message> getAllSent(Principal principal) {
        return null;
    }

    @Override
    public Message getReceived(Principal principal, Integer messageId) {
        return get(principal, messageId, false);
    }

    @Override
    public Message getSent(Principal principal, Integer messageId) {
        return get(principal, messageId, true);
    }

    private Message get(Principal principal, Integer messageId, Boolean sent) {
        String columnName;
        if (sent) {
            columnName = "sourceUser";
        } else {
            columnName = "destinationUser";
        }

        Session session = this.sessionFactory.openSession();
        Message message = (Message) session
                .createQuery("from Message where " + columnName + ".name=:userName and messageId=:messageId")
                .setParameter("userName", principal.getName())
                .setParameter("messageId", messageId)
                .uniqueResult();
        session.close();
        return message;
    }
}
