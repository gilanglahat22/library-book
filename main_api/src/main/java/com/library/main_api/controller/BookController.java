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
@RequestMapping("/books")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Books API", description = "API for managing books")
public class BookController {

    private final ApiIntegratorService apiIntegratorService;

    @GetMapping
    @Operation(summary = "Get all books", description = "Retrieves a list of all books")
    public Mono<ResponseEntity<Map>> getAllBooks(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort) {
        
        StringBuilder path = new StringBuilder("/books");
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
        
        return apiIntegratorService.makeGetRequest(path.toString(), ApiType.BOOKS)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID", description = "Retrieves a book by its ID")
    public Mono<ResponseEntity<Map>> getBookById(@PathVariable Long id) {
        return apiIntegratorService.makeGetRequest("/books/" + id, ApiType.BOOKS)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @Operation(summary = "Create a new book", description = "Creates a new book")
    public Mono<ResponseEntity<Map>> createBook(@RequestBody Map<String, Object> book) {
        return apiIntegratorService.makePostRequest("/books", book, ApiType.BOOKS)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a book", description = "Updates an existing book")
    public Mono<ResponseEntity<Map>> updateBook(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> book) {
        
        return apiIntegratorService.makePutRequest("/books/" + id, book, ApiType.BOOKS)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book", description = "Deletes a book by its ID")
    public Mono<ResponseEntity<Map>> deleteBook(@PathVariable Long id) {
        return apiIntegratorService.makeDeleteRequest("/books/" + id, ApiType.BOOKS)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/search")
    @Operation(summary = "Search books", description = "Search books by title, author, or ISBN")
    public Mono<ResponseEntity<Map>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn) {
        
        StringBuilder path = new StringBuilder("/books/search?");
        boolean hasQueryParams = false;
        
        if (title != null) {
            path.append("title=").append(title);
            hasQueryParams = true;
        }
        
        if (author != null) {
            path.append(hasQueryParams ? "&" : "").append("author=").append(author);
            hasQueryParams = true;
        }
        
        if (isbn != null) {
            path.append(hasQueryParams ? "&" : "").append("isbn=").append(isbn);
        }
        
        return apiIntegratorService.makeGetRequest(path.toString(), ApiType.BOOKS)
                .map(ResponseEntity::ok);
    }
} 