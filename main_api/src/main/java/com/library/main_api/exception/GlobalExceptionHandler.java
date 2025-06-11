package com.library.main_api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiIntegratorException.class)
    public ResponseEntity<Object> handleApiIntegratorException(ApiIntegratorException ex) {
        log.error("API Integrator Exception: {}", ex.getMessage());
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", ex.getStatusCode());
        body.put("error", "API Integrator Error");
        body.put("message", ex.getMessage());
        
        return new ResponseEntity<>(body, HttpStatus.valueOf(ex.getStatusCode()));
    }
    
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Object> handleWebClientResponseException(WebClientResponseException ex) {
        log.error("WebClient Response Exception: {}", ex.getMessage());
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", ex.getStatusCode().value());
        body.put("error", getReasonForStatus(ex.getStatusCode().value()));
        body.put("message", ex.getMessage());
        
        return new ResponseEntity<>(body, ex.getStatusCode());
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtException(Exception ex) {
        log.error("Uncaught exception: {}", ex.getMessage(), ex);
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "An unexpected error occurred");
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Helper method to get a reason phrase for a status code
     */
    private String getReasonForStatus(int statusCode) {
        try {
            return HttpStatus.valueOf(statusCode).getReasonPhrase();
        } catch (IllegalArgumentException e) {
            if (statusCode >= 100 && statusCode < 200) return "Informational";
            if (statusCode >= 200 && statusCode < 300) return "Success";
            if (statusCode >= 300 && statusCode < 400) return "Redirection";
            if (statusCode >= 400 && statusCode < 500) return "Client Error";
            if (statusCode >= 500 && statusCode < 600) return "Server Error";
            return "Unknown Error";
        }
    }
} 