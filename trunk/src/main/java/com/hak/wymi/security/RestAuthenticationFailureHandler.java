package com.hak.wymi.security;

import com.hak.wymi.controllers.rest.helpers.ErrorList;
import com.hak.wymi.utility.JSONConverter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class RestAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    public RestAuthenticationFailureHandler() {
        super();
    }

    public RestAuthenticationFailureHandler(String defaultFailureUrl) {
        super(defaultFailureUrl);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        PrintWriter writer = response.getWriter();
        writer.write(JSONConverter.getJSON(new ErrorList(exception.getMessage()), true));
        writer.flush();
    }

    @Override
    public void setDefaultFailureUrl(String defaultFailureUrl) {
        super.setDefaultFailureUrl(defaultFailureUrl);
    }

    @Override
    protected boolean isUseForward() {
        return super.isUseForward();
    }

    @Override
    public void setUseForward(boolean forwardToDestination) {
        super.setUseForward(forwardToDestination);
    }

    @Override
    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        super.setRedirectStrategy(redirectStrategy);
    }

    @Override
    protected RedirectStrategy getRedirectStrategy() {
        return super.getRedirectStrategy();
    }

    @Override
    protected boolean isAllowSessionCreation() {
        return super.isAllowSessionCreation();
    }

    @Override
    public void setAllowSessionCreation(boolean allowSessionCreation) {
        super.setAllowSessionCreation(allowSessionCreation);
    }
}
