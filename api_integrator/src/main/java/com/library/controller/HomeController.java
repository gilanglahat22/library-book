package com.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Home", description = "Home endpoint and diagnostics")
public class HomeController {
    
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    
    @Value("${api.key.header.name:X-API-KEY}")
    private String apiKeyHeaderName;

    @GetMapping("/")
    @Operation(summary = "Home endpoint", description = "Welcome message and API information")
    public String home() {
        return "Library Management API is running! Use /swagger-ui.html for API documentation.";
    }
    
    @GetMapping("/api-auth-test")
    @Operation(summary = "API Authentication Test", description = "Test endpoint to verify API key authentication")
    public ResponseEntity<Map<String, Object>> apiAuthTest(
            @RequestHeader(value = "X-API-KEY", required = false) String apiKey) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        
        // Log the authentication attempt
        logger.info("Authentication test request received, API Key: {}, Authentication: {}", 
                apiKey != null ? "provided" : "missing", 
                authentication != null ? "present" : "missing");
        
        // Add basic auth info
        response.put("timestamp", System.currentTimeMillis());
        response.put("apiKeyProvided", apiKey != null && !apiKey.isEmpty());
        response.put("authenticated", authentication != null && authentication.isAuthenticated());
        
        // Add request details
        if (authentication != null) {
            response.put("principal", authentication.getPrincipal());
            response.put("authorities", authentication.getAuthorities());
            response.put("name", authentication.getName());
            response.put("isAuthenticated", authentication.isAuthenticated());
        }
        
        // Add API usage hints
        response.put("apiKeyHeaderName", apiKeyHeaderName);
        response.put("howToAuthenticate", "Add the " + apiKeyHeaderName + " header with a valid API key to your requests");
        response.put("apiEndpoints", Map.of(
            "books", "/books/**",
            "authors", "/authors/**",
            "borrowedBooks", "/borrowed-books/**"
        ));
        
        return ResponseEntity.ok(response);
    }
} 