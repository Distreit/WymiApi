package com.hak.wymi.persistance.pojos.unsecure.message;

import java.security.Principal;
import java.util.List;

public interface MessageDao {
    public boolean save(Message message);

    List<Message> getIncoming(Principal principal);

    List<Message> getSent(Principal principal);
}
