package com.library.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);
    
    @Value("${api.key.header.name:X-API-KEY}")
    private String apiKeyHeaderName;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        logger.info("Configuring CORS with API Key header: {}", apiKeyHeaderName);
        
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:8090",
                    "http://localhost:8080"      // API Integrator direct access
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders(apiKeyHeaderName, "Authorization", "Content-Type")
                .allowCredentials(true)
                .maxAge(3600); // 1 hour
        
        logger.info("CORS configuration completed with expanded origins");
    }
} 