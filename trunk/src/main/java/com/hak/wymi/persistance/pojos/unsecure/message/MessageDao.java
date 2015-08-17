package com.hak.wymi.persistance.pojos.unsecure.message;

import java.security.Principal;
import java.util.List;

public interface MessageDao {
    public boolean save(Message message);

    public boolean update(Message message);

    public List<Message> getAllReceived(Principal principal);

    public List<Message> getAllSent(Principal principal);

    public Message getReceived(Principal principal, Integer messageId);

    public Message getSent(Principal principal, Integer messageId);
}
