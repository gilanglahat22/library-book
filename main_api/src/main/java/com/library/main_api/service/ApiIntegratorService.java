package com.library.main_api.service;

import com.library.main_api.exception.ApiIntegratorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Map;

@Service
public class ApiIntegratorService {

    private static final Logger logger = LoggerFactory.getLogger(ApiIntegratorService.class);
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKeyHeaderName;
    private final String adminApiKey;
    private final String booksApiKey;
    private final String authorsApiKey;
    private final String borrowedBooksApiKey;
    private final boolean bypassAuth;

    @Autowired
    public ApiIntegratorService(
            RestTemplate restTemplate,
            @Value("${API_INTEGRATOR_BASE_URL:${MAIN_API_URL:http://localhost:${API_INTEGRATOR_PORT:8080}}}") String baseUrl,
            @Value("${API_KEY_HEADER_NAME}")           String apiKeyHeaderName,
            @Value("${API_KEY_ADMIN}")                 String adminApiKey,
            @Value("${API_KEY_BOOKS}")                 String booksApiKey,
            @Value("${API_KEY_AUTHORS}")               String authorsApiKey,
            @Value("${API_KEY_BORROWED_BOOKS}")        String borrowedBooksApiKey,
            @Value("${api.bypass.auth:true}")          boolean bypassAuth
    ) {
        this.baseUrl = baseUrl.endsWith("/")
            ? baseUrl.substring(0, baseUrl.length() - 1)
            : baseUrl;
        logger.info("API Integrator base URL: {}", this.baseUrl);

        this.restTemplate        = restTemplate;
        this.apiKeyHeaderName    = apiKeyHeaderName;
        this.adminApiKey         = adminApiKey;
        this.booksApiKey         = booksApiKey;
        this.authorsApiKey       = authorsApiKey;
        this.borrowedBooksApiKey = borrowedBooksApiKey;
        this.bypassAuth          = bypassAuth;

        logger.info("Using API key header: {}, bypass auth: {}", apiKeyHeaderName, bypassAuth);
    }

    public <T> ResponseEntity<T> get(String path, Class<T> responseType) {
        return exchange(path, HttpMethod.GET, null, responseType, null);
    }

    public <T> ResponseEntity<T> get(String path, Class<T> responseType, Map<String, Object> queryParams) {
        return exchange(path, HttpMethod.GET, null, responseType, queryParams);
    }

    public <T, R> ResponseEntity<T> post(String path, R body, Class<T> responseType) {
        return exchange(path, HttpMethod.POST, body, responseType, null);
    }

    public <T, R> ResponseEntity<T> put(String path, R body, Class<T> responseType) {
        return exchange(path, HttpMethod.PUT, body, responseType, null);
    }

    public <T> ResponseEntity<T> delete(String path, Class<T> responseType) {
        return exchange(path, HttpMethod.DELETE, null, responseType, null);
    }

    public <T, R> ResponseEntity<T> exchange(
        String path,
        HttpMethod method,
        R body,
        Class<T> responseType,
        Map<String, ?> queryParams
    ) {
        // 1) Normalize path
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        // 2) Build URL + query params
        // Use fromHttpUrl(baseUrl + path) so embedded query strings are parsed correctly
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + path);
        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.forEach((k, v) -> {
                if (v != null) builder.queryParam(k, v);
            });
        }
        String url = builder.toUriString();

        // 3) Prepare headers
        HttpHeaders headers = new HttpHeaders();
        if (body != null) {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }
        headers.setAccept(Arrays.asList(
            MediaType.APPLICATION_JSON,
            MediaType.TEXT_PLAIN,
            MediaType.ALL
        ));

        // 4) Inject API-Key if bypassAuth
        if (bypassAuth) {
            String apiKey = determineApiKey(path);
            headers.set(apiKeyHeaderName, apiKey);
            logger.debug("Injecting API key '{}' for path {}", apiKey, path);
        }

        // 5) Build entity and log outbound
        HttpEntity<R> requestEntity = new HttpEntity<>(body, headers);
        logger.debug("→ {} {}", method, url);
        logger.debug("→ Headers: {}", headers);
        if (body != null) logger.debug("→ Body: {}", body);

        try {
            ResponseEntity<T> response;
            if (responseType == Object.class) {
                ResponseEntity<String> htmlResp = restTemplate.exchange(
                    url, method, requestEntity, String.class
                );
                @SuppressWarnings("unchecked")
                ResponseEntity<T> casted = (ResponseEntity<T>) htmlResp;
                response = casted;
            } else {
                response = restTemplate.exchange(
                    url, method, requestEntity, responseType
                );
            }

            // 6) Log inbound
            logger.debug("← Status : {}", response.getStatusCode());
            logger.debug("← Headers: {}", response.getHeaders());
            logger.debug("← Body   : {}", response.getBody());

            return response;
        } catch (HttpStatusCodeException ex) {
            org.springframework.http.HttpStatus status = org.springframework.http.HttpStatus.valueOf(ex.getStatusCode().value());
            MediaType ct = ex.getResponseHeaders().getContentType();
            logger.error("Error Response Status: {}", status);
            logger.error("Error Response Body: {}", ex.getResponseBodyAsString());
            throw new ApiIntegratorException(
                "Error calling API integrator: " + status,
                status,
                ex.getResponseBodyAsString(),
                ct != null ? ct.toString() : "unknown"
            );
        } catch (Exception ex) {
            logger.error("Unexpected exception: {}", ex.getMessage(), ex);
            throw new ApiIntegratorException("Unexpected error calling API integrator", ex);
        }
    }

    private String determineApiKey(String path) {
        if (path.startsWith("/books"))        return booksApiKey;
        if (path.startsWith("/authors"))      return authorsApiKey;
        if (path.startsWith("/borrowed-books")) return borrowedBooksApiKey;
        return adminApiKey;
    }
}
