package com.hak.wymi.security;

import org.springframework.security.core.GrantedAuthority;

public class WymiAuthority implements GrantedAuthority {
    private static final long serialVersionUID = 8224820371689865968L;

    private final String authority;

    public WymiAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    @Override
    public int hashCode() {
        return authority.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WymiAuthority && ((WymiAuthority) obj).getAuthority().equals(authority);
    }
}
