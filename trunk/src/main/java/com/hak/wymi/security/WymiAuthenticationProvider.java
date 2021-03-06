package com.hak.wymi.security;

import com.hak.wymi.persistance.managers.UserManager;
import com.hak.wymi.persistance.pojos.user.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Component(value = "authenticationProvider")
public class WymiAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserManager userManager;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        final String userName = authentication.getPrincipal().toString();
        final User user = userManager.getFromName(userName);

        if (user == null || !user.getName().equalsIgnoreCase(userName)) {
            throw new UsernameNotFoundException("Invalid credentials");
        }

        final String suppliedPasswordHash = DigestUtils.sha256Hex(authentication.getCredentials().toString());

        if (!user.getPassword().equals(suppliedPasswordHash)) {
            throw new BadCredentialsException("Invalid credentials");
        }

        final List<WymiAuthority> authorities = Arrays
                .stream(user.getRoles().split(","))
                .map(WymiAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(userName, null, authorities);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}
