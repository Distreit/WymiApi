package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.message.Message;
import com.hak.wymi.persistance.pojos.message.MessageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
public class MessageManager {

    @Autowired
    private MessageDao messageDao;

    @Transactional
    public List<Message> getAllReceived(Principal principal) {
        return messageDao.getAllReceived(principal);
    }

    @Transactional
    public Message getReceived(Principal principal, Integer messageId) {
        return messageDao.getReceived(principal, messageId);
    }

    @Transactional
    public void update(Message message) {
        messageDao.update(message);
    }

    @Transactional
    public Message getSent(Principal principal, Integer messageId) {
        return messageDao.getSent(principal, messageId);
    }
}
