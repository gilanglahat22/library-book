package com.library.main_api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class IpAddressFilter extends OncePerRequestFilter {

    private final List<String> allowedIps;
    
    public IpAddressFilter(String[] allowedIps) {
        this.allowedIps = Arrays.asList(allowedIps);
        log.info("IP Address Filter initialized with allowed IPs: {}", this.allowedIps);
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        // For Swagger UI and API docs, allow without IP check
        String path = request.getRequestURI();
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Get client IP
        String clientIp = getClientIp(request);
        log.debug("Request from IP: {}, path: {}", clientIp, path);
        
        // Check if IP is allowed
        if (isIpAllowed(clientIp)) {
            log.debug("IP {} is allowed, proceeding with request", clientIp);
            filterChain.doFilter(request, response);
        } else {
            log.warn("Blocked request from unauthorized IP: {}", clientIp);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\":\"Access denied\",\"message\":\"Your IP address is not allowed to access this API\"}");
        }
    }
    
    private boolean isPublicPath(String path) {
        return path.contains("/swagger-ui") || 
               path.contains("/docs") || 
               path.contains("/v3/api-docs") || 
               path.contains("/actuator");
    }
    
    private boolean isIpAllowed(String clientIp) {
        return allowedIps.contains(clientIp);
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Get the first IP in case of multiple proxies
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
} 