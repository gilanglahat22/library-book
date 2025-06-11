package com.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/security-test")
@Tag(name = "Security Test", description = "Endpoints for testing API key security")
public class SecurityTestController {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityTestController.class);
    
    @Value("${api.key.header.name:X-API-KEY}")
    private String apiKeyHeaderName;

    @GetMapping("/books-role")
    @Operation(
        summary = "Test Books Role Access", 
        description = "This endpoint requires BOOKS role access",
        security = @SecurityRequirement(name = "apiKey")
    )
    public ResponseEntity<Map<String, Object>> testBooksRole(
            @RequestHeader(value = "X-API-KEY", required = false) String apiKey) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Books role test accessed with auth: {}", auth);
        
        Map<String, Object> response = new HashMap<>();
        response.put("endpoint", "/security-test/books-role");
        response.put("requiredRole", "BOOKS");
        response.put("authentication", auth != null ? auth.getName() : "none");
        response.put("authorities", auth != null ? auth.getAuthorities() : "none");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/authors-role")
    @Operation(
        summary = "Test Authors Role Access", 
        description = "This endpoint requires AUTHORS role access",
        security = @SecurityRequirement(name = "apiKey")
    )
    public ResponseEntity<Map<String, Object>> testAuthorsRole(
            @RequestHeader(value = "X-API-KEY", required = false) String apiKey) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Authors role test accessed with auth: {}", auth);
        
        Map<String, Object> response = new HashMap<>();
        response.put("endpoint", "/security-test/authors-role");
        response.put("requiredRole", "AUTHORS");
        response.put("authentication", auth != null ? auth.getName() : "none");
        response.put("authorities", auth != null ? auth.getAuthorities() : "none");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/admin-role")
    @Operation(
        summary = "Test Admin Role Access", 
        description = "This endpoint requires ADMIN role access",
        security = @SecurityRequirement(name = "apiKey")
    )
    public ResponseEntity<Map<String, Object>> testAdminRole(
            @RequestHeader(value = "X-API-KEY", required = false) String apiKey) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Admin role test accessed with auth: {}", auth);
        
        Map<String, Object> response = new HashMap<>();
        response.put("endpoint", "/security-test/admin-role");
        response.put("requiredRole", "ADMIN");
        response.put("authentication", auth != null ? auth.getName() : "none");
        response.put("authorities", auth != null ? auth.getAuthorities() : "none");
        
        return ResponseEntity.ok(response);
    }
} 