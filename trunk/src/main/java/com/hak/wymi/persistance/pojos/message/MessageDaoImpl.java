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
        final Session session = sessionFactory.getCurrentSession();
        session.persist(message);
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean update(Message message) {
        final Session session = sessionFactory.getCurrentSession();
        session.update(message);
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<Message> getAllReceived(Principal principal) {
        final Session session = sessionFactory.getCurrentSession();
        final List<Message> postList = session.createQuery("from Message where destinationUser.name=:destinationUserName and destinationDeleted=false")
                .setParameter("destinationUserName", principal.getName())
                .list();
        return postList;
    }

    @Override
    public List<Message> getAllSent(Principal principal) {
        return new LinkedList<>();
    }

    @Override
    public Message getReceived(Principal principal, Integer messageId) {
        return get(principal, messageId, Boolean.FALSE);
    }

    @Override
    public Message getSent(Principal principal, Integer messageId) {
        return get(principal, messageId, Boolean.TRUE);
    }

    @Transactional(propagation = Propagation.MANDATORY)
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
