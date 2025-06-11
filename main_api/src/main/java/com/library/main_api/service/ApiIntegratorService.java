package com.library.main_api.service;

import com.library.main_api.config.ApiIntegratorConfig;
import com.library.main_api.exception.ApiIntegratorException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiIntegratorService {

    private final WebClient apiIntegratorWebClient;
    private final ApiIntegratorConfig apiIntegratorConfig;

    /**
     * Make a GET request to the API Integrator
     * 
     * @param path the path to call
     * @param apiType the type of API (books, authors, borrowed-books)
     * @return the response body as a Map
     */
    @CircuitBreaker(name = "apiIntegrator", fallbackMethod = "fallbackGetRequest")
    @Retry(name = "apiIntegrator", fallbackMethod = "fallbackGetRequest")
    public Mono<Map> makeGetRequest(String path, ApiType apiType) {
        log.debug("Making GET request to API Integrator: {}", path);
        
        return apiIntegratorWebClient.get()
                .uri(path)
                .header(apiIntegratorConfig.getApiKeyHeader(), getApiKeyForType(apiType))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(statusCode -> statusCode.is4xxClientError() || statusCode.is5xxServerError(), response -> 
                    response.bodyToMono(String.class)
                            .flatMap(error -> Mono.error(
                                new ApiIntegratorException("Error calling API Integrator: " + error, response.statusCode().value())
                            ))
                )
                .bodyToMono(Map.class)
                .doOnSuccess(response -> log.debug("Successfully received response from API Integrator"))
                .doOnError(error -> log.error("Error calling API Integrator: {}", error.getMessage()));
    }

    /**
     * Make a POST request to the API Integrator
     * 
     * @param path the path to call
     * @param body the request body
     * @param apiType the type of API (books, authors, borrowed-books)
     * @return the response body as a Map
     */
    @CircuitBreaker(name = "apiIntegrator", fallbackMethod = "fallbackPostRequest")
    @Retry(name = "apiIntegrator", fallbackMethod = "fallbackPostRequest")
    public Mono<Map> makePostRequest(String path, Object body, ApiType apiType) {
        log.debug("Making POST request to API Integrator: {}", path);
        
        return apiIntegratorWebClient.post()
                .uri(path)
                .header(apiIntegratorConfig.getApiKeyHeader(), getApiKeyForType(apiType))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(statusCode -> statusCode.is4xxClientError() || statusCode.is5xxServerError(), response -> 
                    response.bodyToMono(String.class)
                            .flatMap(error -> Mono.error(
                                new ApiIntegratorException("Error calling API Integrator: " + error, response.statusCode().value())
                            ))
                )
                .bodyToMono(Map.class)
                .doOnSuccess(response -> log.debug("Successfully received response from API Integrator"))
                .doOnError(error -> log.error("Error calling API Integrator: {}", error.getMessage()));
    }

    /**
     * Make a PUT request to the API Integrator
     * 
     * @param path the path to call
     * @param body the request body
     * @param apiType the type of API (books, authors, borrowed-books)
     * @return the response body as a Map
     */
    @CircuitBreaker(name = "apiIntegrator", fallbackMethod = "fallbackPutRequest")
    @Retry(name = "apiIntegrator", fallbackMethod = "fallbackPutRequest")
    public Mono<Map> makePutRequest(String path, Object body, ApiType apiType) {
        log.debug("Making PUT request to API Integrator: {}", path);
        
        return apiIntegratorWebClient.put()
                .uri(path)
                .header(apiIntegratorConfig.getApiKeyHeader(), getApiKeyForType(apiType))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(statusCode -> statusCode.is4xxClientError() || statusCode.is5xxServerError(), response -> 
                    response.bodyToMono(String.class)
                            .flatMap(error -> Mono.error(
                                new ApiIntegratorException("Error calling API Integrator: " + error, response.statusCode().value())
                            ))
                )
                .bodyToMono(Map.class)
                .doOnSuccess(response -> log.debug("Successfully received response from API Integrator"))
                .doOnError(error -> log.error("Error calling API Integrator: {}", error.getMessage()));
    }

    /**
     * Make a DELETE request to the API Integrator
     * 
     * @param path the path to call
     * @param apiType the type of API (books, authors, borrowed-books)
     * @return the response body as a Map
     */
    @CircuitBreaker(name = "apiIntegrator", fallbackMethod = "fallbackDeleteRequest")
    @Retry(name = "apiIntegrator", fallbackMethod = "fallbackDeleteRequest")
    public Mono<Map> makeDeleteRequest(String path, ApiType apiType) {
        log.debug("Making DELETE request to API Integrator: {}", path);
        
        return apiIntegratorWebClient.delete()
                .uri(path)
                .header(apiIntegratorConfig.getApiKeyHeader(), getApiKeyForType(apiType))
                .retrieve()
                .onStatus(statusCode -> statusCode.is4xxClientError() || statusCode.is5xxServerError(), response -> 
                    response.bodyToMono(String.class)
                            .flatMap(error -> Mono.error(
                                new ApiIntegratorException("Error calling API Integrator: " + error, response.statusCode().value())
                            ))
                )
                .bodyToMono(Map.class)
                .doOnSuccess(response -> log.debug("Successfully received response from API Integrator"))
                .doOnError(error -> log.error("Error calling API Integrator: {}", error.getMessage()));
    }

    /**
     * Get the appropriate API key based on the API type
     */
    private String getApiKeyForType(ApiType apiType) {
        return switch (apiType) {
            case BOOKS -> apiIntegratorConfig.getBooksApiKey();
            case AUTHORS -> apiIntegratorConfig.getAuthorsApiKey();
            case BORROWED_BOOKS -> apiIntegratorConfig.getBorrowedBooksApiKey();
            case ADMIN -> apiIntegratorConfig.getAdminApiKey();
        };
    }

    /**
     * Fallback methods for circuit breaker
     */
    private Mono<Map> fallbackGetRequest(String path, ApiType apiType, Exception ex) {
        log.error("Fallback for GET request to {}: {}", path, ex.getMessage());
        return createFallbackResponse(ex);
    }

    private Mono<Map> fallbackPostRequest(String path, Object body, ApiType apiType, Exception ex) {
        log.error("Fallback for POST request to {}: {}", path, ex.getMessage());
        return createFallbackResponse(ex);
    }

    private Mono<Map> fallbackPutRequest(String path, Object body, ApiType apiType, Exception ex) {
        log.error("Fallback for PUT request to {}: {}", path, ex.getMessage());
        return createFallbackResponse(ex);
    }

    private Mono<Map> fallbackDeleteRequest(String path, ApiType apiType, Exception ex) {
        log.error("Fallback for DELETE request to {}: {}", path, ex.getMessage());
        return createFallbackResponse(ex);
    }

    private Mono<Map> createFallbackResponse(Exception ex) {
        Map<String, Object> fallbackResponse = Map.of(
            "error", "Service temporarily unavailable",
            "message", "The API Integrator service is currently unavailable. Please try again later.",
            "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
            "originalError", ex.getMessage()
        );
        return Mono.just(fallbackResponse);
    }

    /**
     * Enum for API types
     */
    public enum ApiType {
        BOOKS, AUTHORS, BORROWED_BOOKS, ADMIN
    }
} 