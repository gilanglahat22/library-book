package com.library.main_api.controller;

import com.library.main_api.service.ApiIntegratorService;
import com.library.main_api.service.ApiIntegratorService.ApiType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authors API", description = "API for managing authors")
public class AuthorController {

    private final ApiIntegratorService apiIntegratorService;

    @GetMapping
    @Operation(summary = "Get all authors", description = "Retrieves a list of all authors")
    public Mono<ResponseEntity<Map>> getAllAuthors(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort) {
        
        StringBuilder path = new StringBuilder("/authors");
        boolean hasQueryParams = false;
        
        if (page != null) {
            path.append(hasQueryParams ? "&" : "?").append("page=").append(page);
            hasQueryParams = true;
        }
        
        if (size != null) {
            path.append(hasQueryParams ? "&" : "?").append("size=").append(size);
            hasQueryParams = true;
        }
        
        if (sort != null) {
            path.append(hasQueryParams ? "&" : "?").append("sort=").append(sort);
        }
        
        return apiIntegratorService.makeGetRequest(path.toString(), ApiType.AUTHORS)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get author by ID", description = "Retrieves an author by their ID")
    public Mono<ResponseEntity<Map>> getAuthorById(@PathVariable Long id) {
        return apiIntegratorService.makeGetRequest("/authors/" + id, ApiType.AUTHORS)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @Operation(summary = "Create a new author", description = "Creates a new author")
    public Mono<ResponseEntity<Map>> createAuthor(@RequestBody Map<String, Object> author) {
        return apiIntegratorService.makePostRequest("/authors", author, ApiType.AUTHORS)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an author", description = "Updates an existing author")
    public Mono<ResponseEntity<Map>> updateAuthor(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> author) {
        
        return apiIntegratorService.makePutRequest("/authors/" + id, author, ApiType.AUTHORS)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an author", description = "Deletes an author by their ID")
    public Mono<ResponseEntity<Map>> deleteAuthor(@PathVariable Long id) {
        return apiIntegratorService.makeDeleteRequest("/authors/" + id, ApiType.AUTHORS)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/search")
    @Operation(summary = "Search authors", description = "Search authors by name or nationality")
    public Mono<ResponseEntity<Map>> searchAuthors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String nationality) {
        
        StringBuilder path = new StringBuilder("/authors/search?");
        boolean hasQueryParams = false;
        
        if (name != null) {
            path.append("name=").append(name);
            hasQueryParams = true;
        }
        
        if (nationality != null) {
            path.append(hasQueryParams ? "&" : "").append("nationality=").append(nationality);
        }
        
        return apiIntegratorService.makeGetRequest(path.toString(), ApiType.AUTHORS)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}/books")
    @Operation(summary = "Get books by author", description = "Retrieves all books by a specific author")
    public Mono<ResponseEntity<Map>> getBooksByAuthor(@PathVariable Long id) {
        return apiIntegratorService.makeGetRequest("/authors/" + id + "/books", ApiType.AUTHORS)
                .map(ResponseEntity::ok);
    }
} 