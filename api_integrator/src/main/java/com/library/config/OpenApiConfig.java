package com.library.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${api.key.header.name:X-API-KEY}")
    private String apiKeyHeader;

    @Bean
    public OpenAPI libraryOpenAPI() {
        final String securitySchemeName = "apiKey";
        
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
                ))
                .externalDocs(new ExternalDocumentation()
                        .description("Library Management System Documentation")
                        .url("https://github.com/your-repository/library-management"))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name(apiKeyHeader)))
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName));
    }
    
    @Bean
    public GroupedOpenApi booksApi() {
        return GroupedOpenApi.builder()
                .group("books")
                .pathsToMatch("/books/**")
                .displayName("Book Management API")
                .build();
    }
    
    @Bean
    public GroupedOpenApi authorsApi() {
        return GroupedOpenApi.builder()
                .group("authors")
                .pathsToMatch("/authors/**")
                .displayName("Author Management API")
                .build();
    }
    
    @Bean
    public GroupedOpenApi borrowedBooksApi() {
        return GroupedOpenApi.builder()
                .group("borrowed-books")
                .pathsToMatch("/borrowed-books/**")
                .displayName("Borrowed Books API")
                .build();
    }
} 