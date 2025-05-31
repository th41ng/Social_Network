package com.socialapp.filters;

import com.socialapp.utils.JwtUtils;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class JwtFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String header = httpRequest.getHeader("Authorization");
        String requestPath = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        logger.debug("JwtFilter processing request: {} {} with Authorization header: {}",
                httpRequest.getMethod(), requestPath, (header != null && header.startsWith("Bearer ")));

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                String username = JwtUtils.validateTokenAndGetUsername(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    UsernamePasswordAuthenticationToken authentication
                            = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("Successfully authenticated user '{}' from token for request: {}", username, requestPath);
                } else if (username == null) {
                    logger.warn("Token validation failed (username is null) for request: {}. Token: {}", requestPath, token);
                } else {

                    logger.debug("Authentication already exists in SecurityContext for user '{}', request: {}",
                            SecurityContextHolder.getContext().getAuthentication().getName(), requestPath);
                }
            } catch (Exception e) {
                logger.warn("Invalid JWT token for request {}: {}. Error: {}", requestPath, token, e.getMessage());

                SecurityContextHolder.clearContext();
            }
        } else {
            logger.debug("No Authorization Bearer token found for request: {}", requestPath);
        }

        chain.doFilter(request, response);
    }
}
