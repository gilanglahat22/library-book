package com.library.controller;

import com.library.model.Author;
import com.library.service.AuthorService;
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
public class AuthorController {
    
    private final AuthorService authorService;
    
    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }
    
    @GetMapping
    public ResponseEntity<Page<Author>> getAllAuthors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {
        
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
    
    @GetMapping("/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable Long id) {
        Optional<Author> author = authorService.findById(id);
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
    
    @GetMapping("/by-nationality/{nationality}")
    public ResponseEntity<List<Author>> getAuthorsByNationality(@PathVariable String nationality) {
        List<Author> authors = authorService.findByNationality(nationality);
        return ResponseEntity.ok(authors);
    }
    
    @GetMapping("/by-birth-year")
    public ResponseEntity<List<Author>> getAuthorsByBirthYearRange(
            @RequestParam Integer startYear,
            @RequestParam Integer endYear) {
        List<Author> authors = authorService.findByBirthYearRange(startYear, endYear);
        return ResponseEntity.ok(authors);
    }
    
    @PostMapping
    public ResponseEntity<?> createAuthor(@Valid @RequestBody Author author) {
        try {
            // Check if author with same name already exists
            if (authorService.existsByName(author.getName())) {
                return ResponseEntity.badRequest()
                        .body("Author with name '" + author.getName() + "' already exists");
            }
            
            Author savedAuthor = authorService.save(author);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAuthor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating author: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAuthor(@PathVariable Long id, @Valid @RequestBody Author authorDetails) {
        try {
            if (!authorService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            Author updatedAuthor = authorService.update(id, authorDetails);
            return ResponseEntity.ok(updatedAuthor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating author: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAuthor(@PathVariable Long id) {
        try {
            if (!authorService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            authorService.deleteById(id);
            return ResponseEntity.ok().body("Author deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting author: " + e.getMessage());
        }
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