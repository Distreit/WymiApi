package com.hak.wymi.persistance.pojos.user;

import com.hak.wymi.persistance.interfaces.SecureToSend;

import java.security.Principal;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class SecureCurrentUser implements SecureToSend {
    private final String name;
    private final Collection<String> subscriptions;

    public SecureCurrentUser(User user, Principal principal) {
        if (principal.getName().equalsIgnoreCase(user.getName())) {
            this.name = user.getName();
            this.subscriptions = user.getSubscriptions().stream().map(u -> u.getName()).collect(Collectors.toCollection(LinkedList::new));
        } else {
            this.name = "";
            this.subscriptions = new LinkedList<>();
        }
    }

    public String getName() {
        return name;
    }

    public Collection<String> getSubscriptions() {
        return subscriptions;
    }
}
