package com.library.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class SwaggerConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(SwaggerConfig.class);

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
    public OpenAPI openAPI() {
        final String securitySchemeName = "apiKey";
        
        logger.info("Configuring Swagger/OpenAPI with API Key Header: {}", apiKeyHeaderName);
        
        return new OpenAPI()
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(new Components()
                .addSecuritySchemes(securitySchemeName, 
                    new SecurityScheme()
                        .name(apiKeyHeaderName)
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .description("API key authentication. Add your API key with the header: " + apiKeyHeaderName +
                                    "\n\nExample API keys:\n" +
                                    "- Admin (all access): " + adminApiKey + "\n" +
                                    "- Books API: " + booksApiKey + "\n" +
                                    "- Authors API: " + authorsApiKey + "\n" +
                                    "- Borrowed Books API: " + borrowedBooksApiKey)
                )
            )
            .info(new Info()
                .title("Library Management API")
                .description("API for managing books, authors, and borrowed books in a library\n\n" +
                            "**IMPORTANT**: All API endpoints require a valid API key in the " + apiKeyHeaderName + " header.\n\n" +
                            "Click the 'Authorize' button at the top to enter your API key.")
                .version("1.0")
                .contact(new Contact()
                    .name("Library Team")
                    .email("library@example.com")
                    .url("https://library-example.com")
                )
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")
                )
            );
    }
} 