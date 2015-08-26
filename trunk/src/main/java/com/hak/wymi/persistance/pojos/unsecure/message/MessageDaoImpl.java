package com.hak.wymi.persistance.pojos.unsecure.message;

import com.hak.wymi.utility.DaoHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class MessageDaoImpl implements MessageDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean save(Message message) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> session.persist(message));
    }

    @Override
    public boolean update(Message message) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> session.update(message));
    }

    @Override
    public List<Message> getAllReceived(Principal principal) {
        Session session = sessionFactory.openSession();
        List<Message> postList = session.createQuery("from Message where destinationUser.name=:destinationUserName and destinationDeleted=false")
                .setParameter("destinationUserName", principal.getName())
                .list();
        session.close();
        return postList;
    }

    @Override
    public List<Message> getAllSent(Principal principal) {
        return new LinkedList<>();
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

        Session session = sessionFactory.openSession();
        Message message = (Message) session
                .createQuery("from Message where " + columnName + ".name=:userName and messageId=:messageId")
                .setParameter("userName", principal.getName())
                .setParameter("messageId", messageId)
                .uniqueResult();
        session.close();
        return message;
    }
}
