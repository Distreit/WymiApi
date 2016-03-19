package com.hak.wymi.persistance.pojos.user;

import java.security.Principal;

public interface UserDao {
    void save(User user);

    User get(Principal principal);

    User getFromName(String name);

    User getFromEmail(String email, boolean includeNewEmails);

    void update(User user);

    User getFromEmail(String email);

    User getFromPhoneNumber(String phoneNumber);
}
