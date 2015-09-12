package com.hak.wymi.persistance.pojos.user;

import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.pojos.topic.Topic;

import java.security.Principal;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class SecureCurrentUser implements SecureToSend {
    private final String name;
    private final Collection<String> subscriptions;
    private final Collection<String> filters;

    public SecureCurrentUser(User user, Principal principal) {
        if (principal.getName().equalsIgnoreCase(user.getName())) {
            this.name = user.getName();
            this.subscriptions = user.getSubscriptions().stream().map(Topic::getName).sorted().collect(Collectors.toCollection(LinkedList::new));
            this.filters = user.getFilters().stream().map(Topic::getName).sorted().collect(Collectors.toCollection(LinkedList::new));
        } else {
            this.name = "";
            this.subscriptions = new LinkedList<>();
            this.filters = new LinkedList<>();
        }
    }

    public String getName() {
        return name;
    }

    public Collection<String> getSubscriptions() {
        return subscriptions;
    }

    public Collection<String> getFilters() {
        return filters;
    }
}
