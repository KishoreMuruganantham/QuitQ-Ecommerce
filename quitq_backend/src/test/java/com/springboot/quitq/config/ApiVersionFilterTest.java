package com.springboot.quitq.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class ApiVersionFilterTest {

    @Test
    public void doFilterTest_VersionedPath() throws Exception {
        ApiVersionFilter apiVersionFilter = new ApiVersionFilter();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/product/all");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<ServletRequest> forwardedRequest = new AtomicReference<>();
        FilterChain filterChain = (req, res) -> forwardedRequest.set(req);

        apiVersionFilter.doFilter(request, response, filterChain);

        assertNotNull(forwardedRequest.get());
        HttpServletRequest forwarded = (HttpServletRequest) forwardedRequest.get();
        assertEquals("/api/product/all", forwarded.getRequestURI());
        assertEquals("/api/product/all", forwarded.getServletPath());
        assertEquals("v1", response.getHeader("X-API-Version"));
    }

    @Test
    public void doFilterTest_UnversionedPath() throws Exception {
        ApiVersionFilter apiVersionFilter = new ApiVersionFilter();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/product/all");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<ServletRequest> forwardedRequest = new AtomicReference<>();
        FilterChain filterChain = (req, res) -> forwardedRequest.set(req);

        apiVersionFilter.doFilter(request, response, filterChain);

        assertSame(request, forwardedRequest.get());
        assertNull(response.getHeader("X-API-Version"));
    }
}
