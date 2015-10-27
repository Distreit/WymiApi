package com.hak.wymi.persistance.pojos.user;

import java.security.Principal;

public interface UserDao {
    void save(User user);

    User get(Principal principal);

    User getFromName(String name);

    User getFromEmail(String email);

    void update(User user);
}
