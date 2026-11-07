package com.springboot.quitq.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ApiVersionFilter implements Filter {

    private static final Pattern VERSIONED_PATH = Pattern.compile("^/api/(v\\d+)(/.*)?$");

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain filterChain)
            throws ServletException, IOException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String contextPath = httpRequest.getContextPath();
        if (contextPath == null) {
            contextPath = "";
        }

        String requestUri = httpRequest.getRequestURI();
        String path = requestUri;
        if (requestUri != null && requestUri.startsWith(contextPath)) {
            path = requestUri.substring(contextPath.length());
        }

        int semicolon = path.indexOf(';');
        if (semicolon != -1) {
            path = path.substring(0, semicolon);
        }

        Matcher matcher = VERSIONED_PATH.matcher(path);
        if (!matcher.matches()) {
            filterChain.doFilter(request, response);
            return;
        }

        String version = matcher.group(1);
        String remainingPath = matcher.group(2);
        String servletPath = "/api" + (remainingPath == null ? "" : remainingPath);

        httpResponse.setHeader("X-API-Version", version);
        filterChain.doFilter(
                new VersionedRequestWrapper(httpRequest, contextPath + servletPath, servletPath),
                httpResponse);
    }

    private static final class VersionedRequestWrapper extends HttpServletRequestWrapper {

        private final String requestUri;
        private final String servletPath;

        private VersionedRequestWrapper(HttpServletRequest request, String requestUri, String servletPath) {
            super(request);
            this.requestUri = requestUri;
            this.servletPath = servletPath;
        }

        @Override
        public String getRequestURI() {
            return requestUri;
        }

        @Override
        public String getServletPath() {
            return servletPath;
        }
    }
}
