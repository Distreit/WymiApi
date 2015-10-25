package com.hak.wymi.persistance.pojos.user;

import com.hak.wymi.persistance.interfaces.SecureToSend;

public class SecureUser implements SecureToSend {
    private final String userName;

    public SecureUser(User user) {
        this.userName = user.getName();
    }

    public String getUserName() {
        return userName;
    }
}
