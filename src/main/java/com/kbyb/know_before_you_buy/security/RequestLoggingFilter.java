package com.kbyb.know_before_you_buy.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * RequestLoggingFilter logs basic request metadata for traceability.
 */
@Component
@Order(2) // run after ApiKeyFilter
public class RequestLoggingFilter extends OncePerRequestFilter {

    // A logger instance used for logging request details.
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    /**
     * The main filter method that logs request information before passing the request
     * to the next filter in the chain.
     *
     * @param request The incoming HttpServletRequest.
     * @param response The HttpServletResponse to be sent.
     * @param filterChain The FilterChain to continue processing the request.
     * @throws ServletException if a servlet-related error occurs.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String method = request.getMethod();
        String path = request.getRequestURI();
        String remoteAddr = request.getRemoteAddr();
        boolean hasKey = request.getHeader("X-API-KEY") != null;

        log.info("Request: method={} path={} remoteIp={} apiKeyPresent={}", method, path, remoteAddr, hasKey);

        filterChain.doFilter(request, response);
    }
}
