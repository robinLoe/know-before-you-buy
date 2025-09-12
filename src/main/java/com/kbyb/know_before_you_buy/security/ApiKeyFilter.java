package com.kbyb.know_before_you_buy.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * checks for a configured API key in the request header for write operations.
 * - GET / HEAD / OPTIONS are allowed without authentication.
 * - POST, PUT, DELETE require a valid API key in the header specified by 'api.key.header'.
 */
@Component
@Order(1) // run early in the filter chain
public class ApiKeyFilter extends OncePerRequestFilter {

    // API key header name (default X-API-KEY)
    @Value("${api.key.header:X-API-KEY}")
    private String headerName;

    // actual key, read from application.properties
    @Value("${api.key}")
    private String apiKey;

    // Paths that should not be protected (swagger, health checks, etc.)
    private static final String[] EXCLUDE_PATHS = new String[] {
        "/v3/api-docs", "/swagger-ui", "/swagger-ui.html", "/swagger-ui/", "/actuator", "/actuator/health"
    };

    private boolean isExcludedPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        for (String p : EXCLUDE_PATHS) {
            if (path.startsWith(p)) return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // allows excluded paths
        if (isExcludedPath(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // allows read-only methods
        String method = request.getMethod();
        if ("GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // validates header for write operations
        String provided = request.getHeader(headerName);
        if (provided == null || !provided.equals(apiKey)) {
            // unauthorized
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            // keep the response body simple
            response.getWriter().write("{\"error\":\"Unauthorized - missing or invalid API key\"}");
            return;
        }

        // if header is valid -> continue filter chain to controller
        filterChain.doFilter(request, response);
    }
}
