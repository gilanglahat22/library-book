package com.library.main_api.exception;

import org.springframework.http.HttpStatus;

public class ApiIntegratorException extends RuntimeException {

    private final HttpStatus statusCode;
    private final String responseBody;
    private final String contentType;

    public ApiIntegratorException(String message, HttpStatus statusCode, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.contentType = null;
    }
    
    public ApiIntegratorException(String message, HttpStatus statusCode, String responseBody, String contentType) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.contentType = contentType;
    }

    public ApiIntegratorException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        this.responseBody = cause.getMessage();
        this.contentType = null;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    @Override
    public String toString() {
        return "ApiIntegratorException{" +
                "message='" + getMessage() + '\'' +
                ", statusCode=" + statusCode +
                ", contentType='" + contentType + '\'' +
                '}';
    }
} 