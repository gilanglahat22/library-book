package com.library.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * Legacy security configuration - kept for reference but no longer used.
 * See WebSecurityConfig for the active configuration.
 */
//@Configuration
//@EnableWebSecurity
@Order(100) // Lower priority than WebSecurityConfig
public class SecurityConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${api.key.header.name:X-API-KEY}")
    private String apiKeyHeaderName;
    
    @Value("${api.key.admin:admin-api-key-123}")
    private String adminApiKey;
    
    @Value("${api.key.books:books-api-key-456}")
    private String booksApiKey;
    
    @Value("${api.key.authors:authors-api-key-789}")
    private String authorsApiKey;
    
    @Value("${api.key.borrowed-books:borrowed-books-api-key-101}")
    private String borrowedBooksApiKey;

    //@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        Map<String, String> apiKeys = getApiKeys();
        logger.info("LEGACY CONFIG (DISABLED): Configuring security with {} API keys", apiKeys.size());
        
        // Initialize API key filter
        ApiKeyAuthFilter apiKeyAuthFilter = new ApiKeyAuthFilter(apiKeyHeaderName, apiKeys);
        
        http
            // Disable CSRF since we're using API keys
            .csrf(AbstractHttpConfigurer::disable)
            // Use stateless session management
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Configure authorization rules
            .authorizeHttpRequests(authz -> authz
                // Public endpoints - allow without authentication
                .requestMatchers(
                    AntPathRequestMatcher.antMatcher("/"),
                    AntPathRequestMatcher.antMatcher("/swagger-ui/**"),
                    AntPathRequestMatcher.antMatcher("/swagger-ui.html"),
                    AntPathRequestMatcher.antMatcher("/api-docs/**"),
                    AntPathRequestMatcher.antMatcher("/v3/api-docs/**"),
                    AntPathRequestMatcher.antMatcher("/csrf")
                ).permitAll()
                // Books API requires ADMIN or BOOKS role
                .requestMatchers(AntPathRequestMatcher.antMatcher("/books/**")).hasAnyRole("ADMIN", "BOOKS")
                // Authors API requires ADMIN or AUTHORS role
                .requestMatchers(AntPathRequestMatcher.antMatcher("/authors/**")).hasAnyRole("ADMIN", "AUTHORS")
                // Borrowed Books API requires ADMIN or BORROWED_BOOKS role
                .requestMatchers(AntPathRequestMatcher.antMatcher("/borrowed-books/**")).hasAnyRole("ADMIN", "BORROWED_BOOKS")
                // Security test endpoints with specific role requirements
                .requestMatchers(AntPathRequestMatcher.antMatcher("/security-test/books-role")).hasAnyRole("ADMIN", "BOOKS")
                .requestMatchers(AntPathRequestMatcher.antMatcher("/security-test/authors-role")).hasAnyRole("ADMIN", "AUTHORS")
                .requestMatchers(AntPathRequestMatcher.antMatcher("/security-test/admin-role")).hasRole("ADMIN")
                // API auth test endpoint - accessible to authenticated users
                .requestMatchers(AntPathRequestMatcher.antMatcher("/api-auth-test")).authenticated()
                // Any other request requires authentication
                .anyRequest().authenticated()
            )
            // Add the API key filter before Spring Security's UsernamePasswordAuthenticationFilter
            .addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        logger.info("LEGACY SECURITY CONFIGURATION COMPLETED (BUT DISABLED)");
        return http.build();
    }
    
    //@Bean
    public Map<String, String> getApiKeys() {
        Map<String, String> apiKeys = new HashMap<>();
        
        // Add API keys from environment variables
        apiKeys.put(adminApiKey, "ADMIN");
        apiKeys.put(booksApiKey, "BOOKS");
        apiKeys.put(authorsApiKey, "AUTHORS");
        apiKeys.put(borrowedBooksApiKey, "BORROWED_BOOKS");
        
        logger.info("LEGACY CONFIG (DISABLED): Loaded API keys - Admin: {}, Books: {}, Authors: {}, Borrowed Books: {}", 
                adminApiKey, booksApiKey, authorsApiKey, borrowedBooksApiKey);
        
        return apiKeys;
    }
} 