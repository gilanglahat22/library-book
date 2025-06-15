package com.library.main_api.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiIntegratorException.class)
    public ResponseEntity<Object> handleApiIntegratorException(ApiIntegratorException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("status", ex.getStatusCode().value());
        body.put("error", ex.getStatusCode().getReasonPhrase());
        
        // Add the response body as details
        String responseBody = ex.getResponseBody();
        body.put("details", responseBody);
        
        // Add content type if available
        if (ex.getContentType() != null) {
            body.put("contentType", ex.getContentType());
        }
        
        // Log the detailed error
        logger.error("API Integrator Exception: {} - Status: {} - Content-Type: {} - Body: {}", 
                ex.getMessage(), ex.getStatusCode(), ex.getContentType(), responseBody);
        
        return new ResponseEntity<>(body, ex.getStatusCode());
    }
    
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<Object> handleRestClientException(RestClientException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Error in communication with external service");
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        body.put("details", ex.getMessage());
        
        logger.error("Rest Client Exception: {}", ex.getMessage(), ex);
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "An unexpected error occurred");
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        body.put("details", ex.getMessage());
        
        logger.error("Unhandled exception: {}", ex.getMessage(), ex);
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 