package com.library.main_api.exception;

import lombok.Getter;

@Getter
public class ApiIntegratorException extends RuntimeException {
    
    private final int statusCode;
    
    public ApiIntegratorException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public ApiIntegratorException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }
} 