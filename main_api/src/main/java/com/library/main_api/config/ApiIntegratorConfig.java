package com.library.main_api.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@Getter
public class ApiIntegratorConfig {

    @Value("${api-integrator.base-url}")
    private String baseUrl;

    @Value("${api-integrator.timeout}")
    private int timeout;

    @Value("${api-integrator.api-key-header}")
    private String apiKeyHeader;

    @Value("${api-integrator.api-keys.admin}")
    private String adminApiKey;

    @Value("${api-integrator.api-keys.books}")
    private String booksApiKey;

    @Value("${api-integrator.api-keys.authors}")
    private String authorsApiKey;

    @Value("${api-integrator.api-keys.borrowed-books}")
    private String borrowedBooksApiKey;

    @Bean
    public WebClient apiIntegratorWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
                .responseTimeout(Duration.ofMillis(timeout))
                .doOnConnected(conn -> 
                    conn.addHandlerLast(new ReadTimeoutHandler(timeout, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(timeout, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
} 