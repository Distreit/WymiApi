package com.hak.wymi.security;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class JSONPrefixFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        final ResponseWrapper responseWrapper = new ResponseWrapper((HttpServletResponse) response);

        filterChain.doFilter(request, responseWrapper);

        final String responseContent = new String(responseWrapper.getDataStream(), "UTF-8");

        final String fullResponse = ")]}',\n" + responseContent;

        final byte[] responseToSend = fullResponse.getBytes("UTF-8");

        response.getOutputStream().write(responseToSend);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Not used.
    }

    @Override
    public void destroy() {
        // Not used.
    }

    private static class ResponseWrapper extends HttpServletResponseWrapper {

        private final ByteArrayOutputStream output;
        private FilterServletOutputStream filterOutput;

        public ResponseWrapper(HttpServletResponse response) {
            super(response);
            output = new ByteArrayOutputStream();
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (filterOutput == null) {
                filterOutput = new FilterServletOutputStream(output);
            }
            return filterOutput;
        }

        public byte[] getDataStream() {
            return output.toByteArray();
        }
    }

    private static class FilterServletOutputStream extends ServletOutputStream {
        private final DataOutputStream output;

        public FilterServletOutputStream(OutputStream output) {
            super();
            this.output = new DataOutputStream(output);
        }

        @Override
        public void write(int arg0) throws IOException {
            output.write(arg0);
        }

        @Override
        public void write(byte[] arg0, int arg1, int arg2) throws IOException {
            output.write(arg0, arg1, arg2);
        }

        @Override
        public void write(byte[] arg0) throws IOException {
            output.write(arg0);
        }
    }
}
