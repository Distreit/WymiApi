package com.hak.wymi.persistance.pojos.unsecure.dao;

import com.hak.wymi.persistance.pojos.unsecure.User;

import java.security.Principal;

public interface UserDao {
    boolean save(User user);

    User get(Principal principal);

    User getFromName(String name);

    User getFromEmail(String email);

    boolean update(User user);
}
