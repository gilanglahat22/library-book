package com.library.main_api.controller;

import com.library.main_api.service.ApiIntegratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/books")
@CrossOrigin
@Tag(name = "Books", description = "Book management endpoints")
public class BookController {

    private final ApiIntegratorService apiService;

    @Autowired
    public BookController(ApiIntegratorService apiService) {
        this.apiService = apiService;
    }

    @GetMapping
    @Operation(summary = "Get all books with pagination and filtering")
    public ResponseEntity<Object> getAllBooks(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "title") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Integer endYear,
            @RequestParam(required = false) Boolean available) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", size);
        params.put("sortBy", sortBy);
        params.put("sortDir", sortDir);
        
        if (search != null) params.put("search", search);
        if (category != null) params.put("category", category);
        if (authorId != null) params.put("authorId", authorId);
        if (startYear != null) params.put("startYear", startYear);
        if (endYear != null) params.put("endYear", endYear);
        if (available != null) params.put("available", available);
        
        return apiService.get("/books", Object.class, params);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all books without pagination")
    public ResponseEntity<Object> getAllBooksWithoutPagination() {
        return apiService.get("/books/all", Object.class);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID")
    public ResponseEntity<Object> getBookById(@PathVariable Long id) {
        return apiService.get("/books/" + id, Object.class);
    }

    @GetMapping("/isbn/{isbn}")
    @Operation(summary = "Get book by ISBN")
    public ResponseEntity<Object> getBookByIsbn(@PathVariable String isbn) {
        return apiService.get("/books/isbn/" + isbn, Object.class);
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all book categories")
    public ResponseEntity<Object> getAllCategories() {
        return apiService.get("/books/categories", Object.class);
    }

    @GetMapping("/available")
    @Operation(summary = "Get available books")
    public ResponseEntity<Object> getAvailableBooks(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "title") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", size);
        params.put("sortBy", sortBy);
        params.put("sortDir", sortDir);
        
        return apiService.get("/books/available", Object.class, params);
    }

    @GetMapping("/unavailable")
    @Operation(summary = "Get unavailable books")
    public ResponseEntity<Object> getUnavailableBooks(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "title") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", size);
        params.put("sortBy", sortBy);
        params.put("sortDir", sortDir);
        
        return apiService.get("/books/unavailable", Object.class, params);
    }

    @PostMapping
    @Operation(summary = "Create a new book")
    public ResponseEntity<Object> createBook(@RequestBody Object book) {
        return apiService.post("/books", book, Object.class);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a book")
    public ResponseEntity<Object> updateBook(@PathVariable Long id, @RequestBody Object book) {
        return apiService.put("/books/" + id, book, Object.class);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book")
    public ResponseEntity<Object> deleteBook(@PathVariable Long id) {
        return apiService.delete("/books/" + id, Object.class);
    }

    @GetMapping("/{id}/availability")
    @Operation(summary = "Check book availability")
    public ResponseEntity<Object> checkBookAvailability(@PathVariable Long id) {
        return apiService.get("/books/" + id + "/availability", Object.class);
    }

    @GetMapping("/{id}/borrow-count")
    @Operation(summary = "Get current borrow count")
    public ResponseEntity<Object> getCurrentBorrowCount(@PathVariable Long id) {
        return apiService.get("/books/" + id + "/borrow-count", Object.class);
    }

    @GetMapping("/search")
    @Operation(summary = "Search books")
    public ResponseEntity<Object> searchBooks(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "title") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("query", query);
        params.put("page", page);
        params.put("size", size);
        params.put("sortBy", sortBy);
        params.put("sortDir", sortDir);
        
        return apiService.get("/books/search", Object.class, params);
    }

    @GetMapping("/by-category/{category}")
    @Operation(summary = "Get books by category")
    public ResponseEntity<Object> getBooksByCategory(
            @PathVariable String category,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "title") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", size);
        params.put("sortBy", sortBy);
        params.put("sortDir", sortDir);
        
        return apiService.get("/books/by-category/" + category, Object.class, params);
    }

    @GetMapping("/by-author/{authorId}")
    @Operation(summary = "Get books by author")
    public ResponseEntity<Object> getBooksByAuthor(
            @PathVariable Long authorId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "title") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", size);
        params.put("sortBy", sortBy);
        params.put("sortDir", sortDir);
        
        return apiService.get("/books/by-author/" + authorId, Object.class, params);
    }
} 