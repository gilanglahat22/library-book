package com.library.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the primary security configuration that enforces API key requirements
 */
@Configuration
@EnableWebSecurity
@Order(1) // Set to highest priority
public class WebSecurityConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

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

    @Bean
    @Primary // Ensure this is the primary security configuration
    public SecurityFilterChain strictSecurityFilterChain(HttpSecurity http) throws Exception {
        Map<String, String> apiKeys = getApiKeys();
        logger.info("STRICT SECURITY CONFIG: Configuring security with {} API keys", apiKeys.size());
        
        // Create a very strict API key filter that will be applied first
        StrictApiKeyFilter apiKeyFilter = new StrictApiKeyFilter(apiKeyHeaderName, apiKeys);
        
        http.securityMatcher("/**") // Apply to all requests
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests()
                .requestMatchers("/", "/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/books/**").hasAnyRole("ADMIN", "BOOKS")
                .requestMatchers("/authors/**").hasAnyRole("ADMIN", "AUTHORS") 
                .requestMatchers("/borrowed-books/**").hasAnyRole("ADMIN", "BORROWED_BOOKS")
                .requestMatchers("/security-test/books-role").hasAnyRole("ADMIN", "BOOKS")
                .requestMatchers("/security-test/authors-role").hasAnyRole("ADMIN", "AUTHORS")
                .requestMatchers("/security-test/admin-role").hasRole("ADMIN")
                .requestMatchers("/api-auth-test").authenticated()
                .anyRequest().authenticated()
            .and()
            // Add our strict filter first in the chain
            .addFilterBefore(apiKeyFilter, WebAsyncManagerIntegrationFilter.class);
        
        logger.info("STRICT SECURITY CONFIGURATION COMPLETE");
        return http.build();
    }
    
    @Bean
    public Map<String, String> getApiKeys() {
        Map<String, String> apiKeys = new HashMap<>();
        apiKeys.put(adminApiKey, "ADMIN");
        apiKeys.put(booksApiKey, "BOOKS");
        apiKeys.put(authorsApiKey, "AUTHORS");
        apiKeys.put(borrowedBooksApiKey, "BORROWED_BOOKS");
        
        logger.info("API Keys loaded: {} keys configured", apiKeys.size());
        return apiKeys;
    }
} 