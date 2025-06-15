package com.library.main_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Home", description = "Home endpoint and diagnostics")
public class HomeController {

    @GetMapping("/")
    @Operation(summary = "Home endpoint", description = "Welcome message and API information")
    public String home() {
        return "Library Management API Gateway is running! Use /swagger-ui.html for API documentation.";
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "API health check endpoint")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", System.currentTimeMillis());
        response.put("service", "main-api");
        
        return ResponseEntity.ok(response);
    }
} 