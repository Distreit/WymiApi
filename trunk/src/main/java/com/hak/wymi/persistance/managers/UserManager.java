package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.user.SecureCurrentUser;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.persistance.pojos.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
public class UserManager {

    @Autowired
    private UserDao userDao;

    @Transactional
    public User get(Principal principal) {
        return userDao.get(principal);
    }

    @Transactional
    public void save(User user) {
        userDao.save(user);
    }

    @Transactional
    public User getFromName(String userName) {
        return userDao.getFromName(userName);
    }

    @Transactional
    public void update(User user) {
        userDao.update(user);
    }

    @Transactional
    public User getFromEmail(String email) {
        return userDao.getFromEmail(email);
    }

    @Transactional
    public SecureCurrentUser getSecureCurrent(Principal principal) {
        return new SecureCurrentUser(userDao.getFromName(principal.getName()), principal);
    }
}
