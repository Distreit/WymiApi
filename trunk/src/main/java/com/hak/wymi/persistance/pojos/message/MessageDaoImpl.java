package com.hak.wymi.persistance.pojos.message;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class MessageDaoImpl implements MessageDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean save(Message message) {
        sessionFactory.getCurrentSession().persist(message);
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean update(Message message) {
        sessionFactory.getCurrentSession().update(message);
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<Message> getAllReceived(Principal principal) {
        return sessionFactory.getCurrentSession().createQuery("from Message where destinationUser.name=:destinationUserName and destinationDeleted=false")
                .setParameter("destinationUserName", principal.getName())
                .list();
    }

    @Override
    public List<Message> getAllSent(Principal principal) {
        return new LinkedList<>();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Message getReceived(Principal principal, Integer messageId) {
        return get(principal, messageId, Boolean.FALSE);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Message getSent(Principal principal, Integer messageId) {
        return get(principal, messageId, Boolean.TRUE);
    }

    private Message get(Principal principal, Integer messageId, Boolean sent) {
        final Session session = sessionFactory.getCurrentSession();
        final Message message;
        if (sent) {
            message = (Message) session
                    .createQuery("from Message where sourceUser.name=:userName and messageId=:messageId")
                    .setParameter("userName", principal.getName())
                    .setParameter("messageId", messageId)
                    .uniqueResult();
        } else {
            message = (Message) session
                    .createQuery("from Message where destinationUser.name=:userName and messageId=:messageId")
                    .setParameter("userName", principal.getName())
                    .setParameter("messageId", messageId)
                    .uniqueResult();
        }
        return message;
    }
}
