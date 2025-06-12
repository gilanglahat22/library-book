package com.library.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Filter that adds automatic admin authentication for Swagger UI requests
 */
public class SwaggerAuthorizationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(SwaggerAuthorizationFilter.class);
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    // Paths that are related to Swagger UI
    private final String[] swaggerPaths = {
        "/swagger-ui/**", 
        "/api-docs/**", 
        "/v3/api-docs/**", 
        "/swagger-ui.html", 
        "/webjars/**",
        "/swagger-resources/**",
        "/configuration/**"
    };
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // Check if this is a Swagger UI related request
        if (isSwaggerPath(path)) {
            logger.debug("SWAGGER AUTH FILTER: Processing Swagger UI request: {} {}", request.getMethod(), path);
            
            // Add admin authentication for Swagger UI
            String adminRole = "ADMIN";
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "swagger-ui", null, List.of(new SimpleGrantedAuthority("ROLE_" + adminRole)));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            logger.debug("SWAGGER AUTH FILTER: Added automatic authentication for Swagger UI with role: {}", adminRole);
        }
        
        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
    
    private boolean isSwaggerPath(String path) {
        return Arrays.stream(swaggerPaths)
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
} 