package com.library.controller;

import com.library.model.Author;
import com.library.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/authors")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Author Management", description = "APIs for managing authors in the library")
public class AuthorController {
    
    private final AuthorService authorService;
    
    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }
    
    @Operation(summary = "Get all authors with pagination and filtering", description = "Retrieves a paginated list of authors with optional search term")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved authors",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input parameters")
    })
    @GetMapping
    public ResponseEntity<Page<Author>> getAllAuthors(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir,
            @Parameter(description = "Search term for name or nationality") @RequestParam(required = false) String search) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Author> authors;
        if (search != null && !search.trim().isEmpty()) {
            authors = authorService.findBySearchTerm(search, pageable);
        } else {
            authors = authorService.findAll(pageable);
        }
        
        return ResponseEntity.ok(authors);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Author>> getAllAuthorsWithoutPagination() {
        List<Author> authors = authorService.findAll();
        return ResponseEntity.ok(authors);
    }
    
    @Operation(summary = "Get author by ID", description = "Retrieves an author by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved author"),
        @ApiResponse(responseCode = "404", description = "Author not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Author> getAuthorById(
            @Parameter(description = "Author ID") @PathVariable Long id) {
        Optional<Author> author = authorService.findByIdWithBooks(id);
        return author.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/with-books")
    public ResponseEntity<Author> getAuthorWithBooks(@PathVariable Long id) {
        Optional<Author> author = authorService.findByIdWithBooks(id);
        return author.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/by-name/{name}")
    public ResponseEntity<Author> getAuthorByName(@PathVariable String name) {
        Optional<Author> author = authorService.findByName(name);
        return author.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get authors by nationality", description = "Retrieves a list of authors by their nationality")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved authors")
    @GetMapping("/nationality/{nationality}")
    public ResponseEntity<List<Author>> getAuthorsByNationality(
            @Parameter(description = "Author nationality") @PathVariable String nationality) {
        List<Author> authors = authorService.findByNationality(nationality);
        return ResponseEntity.ok(authors);
    }
    
    @Operation(summary = "Get authors by birth year range", description = "Retrieves a list of authors born within a specified year range")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved authors")
    @GetMapping("/birth-year")
    public ResponseEntity<List<Author>> getAuthorsByBirthYearRange(
            @Parameter(description = "Start year") @RequestParam Integer startYear,
            @Parameter(description = "End year") @RequestParam Integer endYear) {
        List<Author> authors = authorService.findByBirthYearRange(startYear, endYear);
        return ResponseEntity.ok(authors);
    }
    
    @Operation(summary = "Create a new author", description = "Creates a new author in the library")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Author created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<Author> createAuthor(
            @Parameter(description = "Author object to create") @Valid @RequestBody Author author) {
        Author savedAuthor = authorService.save(author);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAuthor);
    }
    
    @Operation(summary = "Update an author", description = "Updates an existing author by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Author updated successfully"),
        @ApiResponse(responseCode = "404", description = "Author not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Author> updateAuthor(
            @Parameter(description = "Author ID") @PathVariable Long id,
            @Parameter(description = "Updated author object") @Valid @RequestBody Author author) {
        Optional<Author> existingAuthor = authorService.findById(id);
        if (existingAuthor.isPresent()) {
            author.setId(id);
            Author updatedAuthor = authorService.save(author);
            return ResponseEntity.ok(updatedAuthor);
        }
        return ResponseEntity.notFound().build();
    }
    
    @Operation(summary = "Delete an author", description = "Deletes an author by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Author deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Author not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(
            @Parameter(description = "Author ID") @PathVariable Long id) {
        Optional<Author> author = authorService.findById(id);
        if (author.isPresent()) {
            authorService.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/{id}/books-count")
    public ResponseEntity<Long> getBookCountByAuthor(@PathVariable Long id) {
        if (!authorService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        Long count = authorService.countBooksByAuthor(id);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<Author>> searchAuthors(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Author> authors = authorService.findBySearchTerm(query, pageable);
        return ResponseEntity.ok(authors);
    }
} 