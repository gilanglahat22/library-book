package com.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

// Exclude the default security auto-configuration to use our custom one
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableJpaRepositories
@ComponentScan(basePackages = {"com.library"})
@EnableWebSecurity
public class LibraryManagementApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(LibraryManagementApplication.class);
    
    @Value("${api.key.header.name:X-API-KEY}")
    private String apiKeyHeaderName;
    
    @Value("${api.key.admin:admin-api-key-123}")
    private String adminApiKey;

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementApplication.class, args);
    }
    
    @Bean
    public CommandLineRunner logSecurityConfig() {
        return args -> {
            logger.info("=================================================");
            logger.info("Library Management API Security Configuration");
            logger.info("=================================================");
            logger.info("API Key Header Name: {}", apiKeyHeaderName);
            logger.info("Admin API Key (first 3 chars): {}", adminApiKey.substring(0, 3) + "...");
            logger.info("Security is ENABLED with STRICT enforcement.");
            logger.info("All API endpoints require a valid API key in the {} header.", apiKeyHeaderName);
            logger.info("Public paths: /, /swagger-ui/**, /api-docs/**, /v3/api-docs/**");
            logger.info("=================================================");
        };
    }
} 