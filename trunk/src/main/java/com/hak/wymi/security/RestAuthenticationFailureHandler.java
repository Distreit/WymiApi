package com.hak.wymi.security;

import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.utility.jsonconverter.JSONConverter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class RestAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final PrintWriter writer = response.getWriter();
        writer.write(JSONConverter.getJSON(new UniversalResponse(), Boolean.TRUE));
        writer.flush();
    }
}
