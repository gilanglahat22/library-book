package com.library.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

/**
 * Controller to help with Swagger UI troubleshooting
 */
@RestController
@Hidden // Hide from Swagger UI documentation
public class SwaggerUIHelperController {

    private static final Logger logger = LoggerFactory.getLogger(SwaggerUIHelperController.class);
    
    @Value("${api.key.header.name:X-API-KEY}")
    private String apiKeyHeaderName;
    
    /**
     * Endpoint to check Swagger UI access
     */
    @GetMapping("/swagger-test")
    public ResponseEntity<Map<String, Object>> swaggerTest() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Swagger UI access test successful");
        response.put("status", "OK");
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            response.put("authenticated", auth.isAuthenticated());
            response.put("principal", auth.getPrincipal().toString());
            response.put("authorities", auth.getAuthorities().toString());
        }
        
        logger.info("Swagger test: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
} 