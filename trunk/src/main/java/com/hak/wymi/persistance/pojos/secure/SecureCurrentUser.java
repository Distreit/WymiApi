package com.hak.wymi.persistance.pojos.secure;

import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.pojos.unsecure.User;

import java.security.Principal;

public class SecureCurrentUser implements SecureToSend {
    private final String name;

    public SecureCurrentUser(User user, Principal principal) {
        if (principal.getName().equalsIgnoreCase(user.getName())) {
            this.name = user.getName();
        } else {
            this.name = "";
        }
    }

    public String getName() {
        return name;
    }
}
