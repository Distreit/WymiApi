package com.hak.wymi.persistance.pojos.message;

import java.security.Principal;
import java.util.List;

public interface MessageDao {
    boolean save(Message message);

    void update(Message message);

    List<Message> getAllReceived(Principal principal);

    List<Message> getAllSent(Principal principal);

    Message getReceived(Principal principal, Integer messageId);

    Message getSent(Principal principal, Integer messageId);
}
