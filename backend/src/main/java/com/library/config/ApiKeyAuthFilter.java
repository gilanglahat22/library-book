package com.library.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiKeyAuthFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiKeyAuthFilter.class);
    private final String headerName;
    private final Map<String, String> apiKeys;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    // Paths that don't require API key authentication
    private final String[] publicPaths = {
        "/",
        "/swagger-ui/**", 
        "/api-docs/**", 
        "/v3/api-docs/**", 
        "/swagger-ui.html", 
        "/csrf"
    };

    public ApiKeyAuthFilter(String headerName, Map<String, String> apiKeys) {
        this.headerName = headerName;
        this.apiKeys = apiKeys;
        logger.info("Initialized ApiKeyAuthFilter with header: {} and {} API keys", headerName, apiKeys.size());
        
        // Log the API keys for debugging (don't do this in production)
        apiKeys.forEach((key, role) -> {
            logger.debug("API Key configured: {} -> ROLE_{}", key.substring(0, 3) + "...", role);
        });
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        logger.debug("Processing request: {} {}", request.getMethod(), path);
        
        // Skip filter for public paths
        if (isPublicPath(path)) {
            logger.debug("Allowing access to public path without API key: {}", path);
            filterChain.doFilter(request, response);
            return;
        }
        
        // Get API key from header
        String apiKey = request.getHeader(headerName);
        logger.debug("API Key in request header {}: {}", headerName, apiKey != null ? "present" : "missing");
        
        // Check if API key is valid
        if (apiKey != null && !apiKey.trim().isEmpty() && apiKeys.containsKey(apiKey)) {
            String role = apiKeys.get(apiKey);
            logger.debug("Valid API key found for role: {}", role);
            
            // Create authentication token with role
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    apiKey, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
            
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Continue with authenticated request
            logger.debug("Request authenticated with API key, proceeding to next filter");
            filterChain.doFilter(request, response);
        } else {
            // If no valid API key, return 401 Unauthorized
            logger.warn("Unauthorized access attempt to {} without valid API key", path);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Invalid or missing API key. Please provide a valid API key in the X-API-KEY header.\"}");
            // Do NOT call filterChain.doFilter() here - this stops the filter chain
        }
    }
    
    private boolean isPublicPath(String path) {
        return Arrays.stream(publicPaths)
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // This ensures the filter is always applied, and the logic for public paths
        // is handled within doFilterInternal
        return false;
    }
} 