package com.library;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Simple utility to test API Key authentication
 * Run this class to verify that API Key authentication is working
 */
public class TestApiAuth {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final String ADMIN_API_KEY = "admin-api-key-123";
    private static final String BOOKS_API_KEY = "books-api-key-456";
    
    public static void main(String[] args) {
        System.out.println("API Authentication Test\n");
        
        // Test home endpoint - should be accessible without API key
        testEndpoint("/", null, true);
        
        // Test auth test endpoint with and without API key
        testEndpoint("/api-auth-test", null, true);
        testEndpoint("/api-auth-test", ADMIN_API_KEY, true);
        
        // Test books endpoint with and without API key
        testEndpoint("/books", null, false);
        testEndpoint("/books", BOOKS_API_KEY, true);
        testEndpoint("/books", ADMIN_API_KEY, true);
        
        // Test single book endpoint with and without API key
        testEndpoint("/books/1", null, false);
        testEndpoint("/books/1", BOOKS_API_KEY, true);
        
        // Test authors endpoint with and without API key
        testEndpoint("/authors", null, false);
        testEndpoint("/authors", "authors-api-key-789", true);
        
        System.out.println("\nTests completed.");
    }
    
    private static void testEndpoint(String endpoint, String apiKey, boolean expectSuccess) {
        try {
            URL url = new URL(BASE_URL + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            // Set API Key header if provided
            if (apiKey != null && !apiKey.isEmpty()) {
                conn.setRequestProperty(API_KEY_HEADER, apiKey);
            }
            
            int statusCode = conn.getResponseCode();
            boolean success = (statusCode >= 200 && statusCode < 300);
            
            System.out.println("-------------------------------------");
            System.out.printf("Endpoint: %s\n", endpoint);
            System.out.printf("API Key: %s\n", apiKey != null ? apiKey : "NONE");
            System.out.printf("Status: %d %s\n", statusCode, conn.getResponseMessage());
            System.out.printf("Success: %s\n", success ? "YES" : "NO");
            System.out.printf("Expected: %s\n", expectSuccess ? "SUCCESS" : "FAILURE");
            System.out.printf("Result: %s\n", (success == expectSuccess) ? "PASS" : "FAIL");
            
            // Read response for informational purposes
            if (success) {
                Scanner scanner = new Scanner(conn.getInputStream());
                String responseBody = scanner.useDelimiter("\\A").next();
                if (responseBody.length() > 150) {
                    responseBody = responseBody.substring(0, 147) + "...";
                }
                System.out.printf("Response: %s\n", responseBody);
                scanner.close();
            }
            
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
} 