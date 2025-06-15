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
@RequestMapping("/authors")
@CrossOrigin
@Tag(name = "Authors", description = "Author management endpoints")
public class AuthorController {

    private final ApiIntegratorService apiService;

    @Autowired
    public AuthorController(ApiIntegratorService apiService) {
        this.apiService = apiService;
    }

    @GetMapping
    @Operation(summary = "Get all authors with pagination and filtering")
    public ResponseEntity<Object> getAllAuthors(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", size);
        params.put("sortBy", sortBy);
        params.put("sortDir", sortDir);
        
        if (search != null) params.put("search", search);
        
        return apiService.get("/authors", Object.class, params);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all authors without pagination")
    public ResponseEntity<Object> getAllAuthorsWithoutPagination() {
        return apiService.get("/authors/all", Object.class);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get author by ID")
    public ResponseEntity<Object> getAuthorById(@PathVariable Long id) {
        return apiService.get("/authors/" + id, Object.class);
    }

    @GetMapping("/{id}/with-books")
    @Operation(summary = "Get author with their books")
    public ResponseEntity<Object> getAuthorWithBooks(@PathVariable Long id) {
        return apiService.get("/authors/" + id + "/with-books", Object.class);
    }

    @GetMapping("/by-name/{name}")
    @Operation(summary = "Get author by name")
    public ResponseEntity<Object> getAuthorByName(@PathVariable String name) {
        return apiService.get("/authors/by-name/" + name, Object.class);
    }

    @GetMapping("/nationality/{nationality}")
    @Operation(summary = "Get authors by nationality")
    public ResponseEntity<Object> getAuthorsByNationality(@PathVariable String nationality) {
        return apiService.get("/authors/nationality/" + nationality, Object.class);
    }

    @GetMapping("/birth-year")
    @Operation(summary = "Get authors by birth year range")
    public ResponseEntity<Object> getAuthorsByBirthYearRange(
            @RequestParam Integer startYear,
            @RequestParam Integer endYear) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("startYear", startYear);
        params.put("endYear", endYear);
        
        return apiService.get("/authors/birth-year", Object.class, params);
    }

    @PostMapping
    @Operation(summary = "Create a new author")
    public ResponseEntity<Object> createAuthor(@RequestBody Object author) {
        return apiService.post("/authors", author, Object.class);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an author")
    public ResponseEntity<Object> updateAuthor(@PathVariable Long id, @RequestBody Object author) {
        return apiService.put("/authors/" + id, author, Object.class);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an author")
    public ResponseEntity<Object> deleteAuthor(@PathVariable Long id) {
        return apiService.delete("/authors/" + id, Object.class);
    }

    @GetMapping("/{id}/books-count")
    @Operation(summary = "Get book count by author")
    public ResponseEntity<Object> getBookCountByAuthor(@PathVariable Long id) {
        return apiService.get("/authors/" + id + "/books-count", Object.class);
    }

    @GetMapping("/search")
    @Operation(summary = "Search authors")
    public ResponseEntity<Object> searchAuthors(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("query", query);
        params.put("page", page);
        params.put("size", size);
        params.put("sortBy", sortBy);
        params.put("sortDir", sortDir);
        
        return apiService.get("/authors/search", Object.class, params);
    }
} 