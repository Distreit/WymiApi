package com.hak.wymi.persistance.pojos.unsecure.dao;

import com.hak.wymi.persistance.pojos.unsecure.Message;

import java.security.Principal;
import java.util.List;

public interface MessageDao {
    boolean save(Message message);

    boolean update(Message message);

    List<Message> getAllReceived(Principal principal);

    List<Message> getAllSent(Principal principal);

    Message getReceived(Principal principal, Integer messageId);

    Message getSent(Principal principal, Integer messageId);
}
