package com.library.controller;

import com.library.model.Book;
import com.library.service.BookService;
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
@RequestMapping("/books")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Book Management", description = "APIs for managing books in the library")
public class BookController {
    
    private final BookService bookService;
    
    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    
    @Operation(summary = "Get all books with pagination and filtering", description = "Retrieves a paginated list of books with optional filtering by search term, category, author, year range, and availability")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved books",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input parameters")
    })
    @GetMapping
    public ResponseEntity<Page<Book>> getAllBooks(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by") @RequestParam(defaultValue = "title") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir,
            @Parameter(description = "Search term for title, category, author, or ISBN") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by category") @RequestParam(required = false) String category,
            @Parameter(description = "Filter by author ID") @RequestParam(required = false) Long authorId,
            @Parameter(description = "Start year for publishing year range") @RequestParam(required = false) Integer startYear,
            @Parameter(description = "End year for publishing year range") @RequestParam(required = false) Integer endYear,
            @Parameter(description = "Filter by availability") @RequestParam(required = false) Boolean available) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Book> books;
        
        if (search != null && !search.trim().isEmpty()) {
            books = bookService.findBySearchTerm(search, pageable);
        } else if (category != null && !category.trim().isEmpty()) {
            books = bookService.findByCategory(category, pageable);
        } else if (authorId != null) {
            books = bookService.findByAuthorId(authorId, pageable);
        } else if (startYear != null && endYear != null) {
            books = bookService.findByPublishingYearRange(startYear, endYear, pageable);
        } else if (available != null) {
            books = available ? bookService.findAvailableBooks(pageable) : bookService.findUnavailableBooks(pageable);
        } else {
            books = bookService.findAll(pageable);
        }
        
        return ResponseEntity.ok(books);
    }
    
    @Operation(summary = "Get all books without pagination", description = "Retrieves a list of all books without pagination")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all books")
    @GetMapping("/all")
    public ResponseEntity<List<Book>> getAllBooksWithoutPagination() {
        List<Book> books = bookService.findAll();
        return ResponseEntity.ok(books);
    }
    
    @Operation(summary = "Get book by ID", description = "Retrieves a book by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved book"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        Optional<Book> book = bookService.findByIdWithAuthor(id);
        return book.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get book by ISBN", description = "Retrieves a book by its ISBN")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved book"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(
            @Parameter(description = "Book ISBN") @PathVariable String isbn) {
        Optional<Book> book = bookService.findByIsbn(isbn);
        return book.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get all book categories", description = "Retrieves a list of all unique book categories")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved categories")
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = bookService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    @Operation(summary = "Get available books", description = "Retrieves a paginated list of all available books")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved available books"),
        @ApiResponse(responseCode = "400", description = "Invalid input parameters")
    })
    @GetMapping("/available")
    public ResponseEntity<Page<Book>> getAvailableBooks(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by") @RequestParam(defaultValue = "title") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Book> books = bookService.findAvailableBooks(pageable);
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/unavailable")
    public ResponseEntity<Page<Book>> getUnavailableBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Book> books = bookService.findUnavailableBooks(pageable);
        return ResponseEntity.ok(books);
    }
    
    @Operation(summary = "Create a new book", description = "Creates a new book in the library")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Book created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<Book> createBook(
            @Parameter(description = "Book object to create") @Valid @RequestBody Book book) {
        Book savedBook = bookService.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }
    
    @Operation(summary = "Update a book", description = "Updates an existing book by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book updated successfully"),
        @ApiResponse(responseCode = "404", description = "Book not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(
            @Parameter(description = "Book ID") @PathVariable Long id,
            @Parameter(description = "Updated book object") @Valid @RequestBody Book book) {
        Optional<Book> existingBook = bookService.findById(id);
        if (existingBook.isPresent()) {
            book.setId(id);
            Book updatedBook = bookService.save(book);
            return ResponseEntity.ok(updatedBook);
        }
        return ResponseEntity.notFound().build();
    }
    
    @Operation(summary = "Delete a book", description = "Deletes a book by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        Optional<Book> book = bookService.findById(id);
        if (book.isPresent()) {
            bookService.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/{id}/availability")
    public ResponseEntity<Boolean> checkBookAvailability(@PathVariable Long id) {
        if (!bookService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        boolean available = bookService.isAvailable(id);
        return ResponseEntity.ok(available);
    }
    
    @GetMapping("/{id}/borrow-count")
    public ResponseEntity<Long> getCurrentBorrowCount(@PathVariable Long id) {
        if (!bookService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        Long count = bookService.countCurrentBorrowsByBook(id);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<Book>> searchBooks(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Book> books = bookService.findBySearchTerm(query, pageable);
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/by-category/{category}")
    public ResponseEntity<Page<Book>> getBooksByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Book> books = bookService.findByCategory(category, pageable);
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/by-author/{authorId}")
    public ResponseEntity<Page<Book>> getBooksByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Book> books = bookService.findByAuthorId(authorId, pageable);
        return ResponseEntity.ok(books);
    }
} 