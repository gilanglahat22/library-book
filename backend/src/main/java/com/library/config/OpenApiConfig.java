package com.library.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI libraryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library Management System API")
                        .description("REST API for Library Management System")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Library Admin")
                                .email("admin@library.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local server")
                ));
    }
} 