package com.hak.wymi.security;

import com.hak.wymi.persistance.pojos.unsecure.user.User;
import com.hak.wymi.persistance.pojos.unsecure.user.UserDao;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component(value = "authenticationProvider")
public class WymiAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDao userDao;

    public WymiAuthenticationProvider() {
        // Only need this for bean creation.
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        final String userName = authentication.getPrincipal().toString();
        final User user = userDao.getFromName(userName);

        if (user == null || !user.getName().equalsIgnoreCase(userName)) {
            throw new UsernameNotFoundException("Invalid credentials");
        }

        final String suppliedPasswordHash = DigestUtils.sha256Hex(authentication.getCredentials().toString());

        if (!user.getPassword().equals(suppliedPasswordHash)) {
            throw new BadCredentialsException("Invalid credentials");
        }

        final List<WymiAuthority> authorities = new ArrayList<>();
        for (String role : user.getRoles().split(",")) {
            authorities.add(new WymiAuthority(role));
        }

        return new UsernamePasswordAuthenticationToken(userName, null, authorities);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}
