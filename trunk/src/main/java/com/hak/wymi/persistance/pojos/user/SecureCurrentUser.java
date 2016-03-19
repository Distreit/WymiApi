package com.hak.wymi.persistance.pojos.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.pojos.topic.Topic;
import org.jadira.usertype.spi.utils.lang.StringUtils;

import java.security.Principal;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class SecureCurrentUser implements SecureToSend {
    @JsonIgnore
    private final Integer userId;

    private final String name;
    private final String email;
    private final Boolean willingJuror;
    private final Collection<String> subscriptions;
    private final Collection<String> filters;
    private final Boolean receivedFunds;

    public SecureCurrentUser(User user, Principal principal) {
        if (principal.getName().equalsIgnoreCase(user.getName())) {
            this.name = user.getName();
            this.email = user.getEmail();
            this.willingJuror = user.getWillingJuror();
            this.subscriptions = user.getSubscriptions().stream().map(Topic::getName).sorted().collect(Collectors.toCollection(LinkedList::new));
            this.filters = user.getFilters().stream().map(Topic::getName).sorted().collect(Collectors.toCollection(LinkedList::new));
            this.userId = user.getUserId();
            if (StringUtils.isNotEmpty(user.getPhoneNumber()) || user.getReceivedFunds()) {
                this.receivedFunds = user.getReceivedFunds();
            } else {
                this.receivedFunds = null;
            }
        } else {
            this.name = "";
            this.email = "";
            this.willingJuror = null;
            this.subscriptions = new LinkedList<>();
            this.filters = new LinkedList<>();
            this.userId = null;
            this.receivedFunds = null;
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

    public Integer getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getWillingJuror() {
        return willingJuror;
    }

    public Boolean getReceivedFunds() {
        return receivedFunds;
    }
}
