package com.library.main_api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class ApiIntegratorConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApiIntegratorConfig.class);

    @Value("${api.integrator.timeout:5000}")
    private int timeout;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
        
        // Configure message converters to handle different content types
        configureMessageConverters(restTemplate);
        
        restTemplate.setErrorHandler(new CustomResponseErrorHandler());
        return restTemplate;
    }
    
    private void configureMessageConverters(RestTemplate restTemplate) {
        // Create a list for the message converters
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        
        // Add StringHttpMessageConverter first - this will handle text/html responses
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringConverter.setSupportedMediaTypes(Arrays.asList(
            MediaType.TEXT_PLAIN,
            MediaType.TEXT_HTML,
            new MediaType("text", "*"),
            MediaType.ALL
        ));
        messageConverters.add(stringConverter);
        
        // Add Jackson converter with support for additional media types
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
        jacksonConverter.setSupportedMediaTypes(Arrays.asList(
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_OCTET_STREAM,
            new MediaType("application", "*+json")
        ));
        messageConverters.add(jacksonConverter);
        
        // Add all other default converters
        for (HttpMessageConverter<?> converter : restTemplate.getMessageConverters()) {
            if (!(converter instanceof StringHttpMessageConverter) && 
                !(converter instanceof MappingJackson2HttpMessageConverter)) {
                messageConverters.add(converter);
            }
        }
        
        // Set the converters to the RestTemplate
        restTemplate.setMessageConverters(messageConverters);
        
        logger.info("Configured RestTemplate message converters to handle multiple content types");
        for (HttpMessageConverter<?> converter : messageConverters) {
            logger.info("Converter: {} supports: {}", 
                converter.getClass().getSimpleName(), 
                converter.getSupportedMediaTypes());
        }
    }
    
    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setConnectionRequestTimeout(timeout);
        factory.setReadTimeout(timeout);
        return factory;
    }
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }
    
    // Custom error handler that doesn't throw exceptions for 4xx responses
    private static class CustomResponseErrorHandler extends DefaultResponseErrorHandler {
        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            HttpStatus statusCode = HttpStatus.valueOf(response.getStatusCode().value());
            // Only treat 5xx responses as errors, let the service handle 4xx responses
            return statusCode.is5xxServerError();
        }
        
        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            if (logger.isErrorEnabled()) {
                MediaType contentType = response.getHeaders().getContentType();
                String responseBody = extractResponseBody(response);
                logger.error("Error response: {} - Content-Type: {} - Body: {}", 
                    response.getStatusCode(), contentType, responseBody);
            }
            super.handleError(response);
        }
        
        private String extractResponseBody(ClientHttpResponse response) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            } catch (IOException e) {
                return "Could not extract response body: " + e.getMessage();
            }
        }
    }
} 