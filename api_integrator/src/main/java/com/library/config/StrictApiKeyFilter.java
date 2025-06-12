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
 * A very strict API key filter that immediately rejects requests without valid API keys
 * except for specifically whitelisted paths.
 */
public class StrictApiKeyFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(StrictApiKeyFilter.class);
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
        "/webjars/**",
        "/swagger-resources/**",
        "/configuration/**",
        "/csrf",
        "/error"
    };
    
    public StrictApiKeyFilter(String headerName, Map<String, String> apiKeys) {
        this.headerName = headerName;
        this.apiKeys = apiKeys;
        logger.info("StrictApiKeyFilter initialized with header: {} and {} API keys", headerName, apiKeys.size());
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        logger.debug("STRICT FILTER: Processing request: {} {}", request.getMethod(), path);
        
        // Check if path is public
        if (isPublicPath(path)) {
            logger.debug("STRICT FILTER: Allowing public path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }
        
        // Get API key from header
        String apiKey = request.getHeader(headerName);
        logger.debug("STRICT FILTER: API Key in header {}: {}", headerName, apiKey != null ? "present" : "missing");
        
        // If API key is missing or empty, reject immediately
        if (apiKey == null || apiKey.trim().isEmpty()) {
            logger.warn("STRICT FILTER: Rejected request to {} - API key missing", path);
            sendUnauthorizedResponse(response, "API key is missing");
            return;
        }
        
        // If API key is invalid, reject immediately
        if (!apiKeys.containsKey(apiKey)) {
            logger.warn("STRICT FILTER: Rejected request to {} - Invalid API key", path);
            sendUnauthorizedResponse(response, "Invalid API key");
            return;
        }
        
        // If we get here, API key is valid
        String role = apiKeys.get(apiKey);
        logger.debug("STRICT FILTER: Valid API key for role: {}", role);
        
        // Set up authentication
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                apiKey, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
        
        // Important: Set the authentication in the SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.debug("STRICT FILTER: Authentication set with role: ROLE_{}", role);
        
        // Continue with authenticated request
        filterChain.doFilter(request, response);
    }
    
    private boolean isPublicPath(String path) {
        return Arrays.stream(publicPaths)
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
    
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(String.format(
                "{\"error\":\"Unauthorized\",\"message\":\"%s\",\"header\":\"%s\"}",
                message, headerName));
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Always apply this filter
        return false;
    }
} 