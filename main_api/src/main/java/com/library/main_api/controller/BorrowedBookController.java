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
@RequestMapping("/borrowed-books")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Borrowed Books API", description = "API for managing borrowed books")
public class BorrowedBookController {

    private final ApiIntegratorService apiIntegratorService;

    @GetMapping
    @Operation(summary = "Get all borrowed books", description = "Retrieves a list of all borrowed books")
    public Mono<ResponseEntity<Map>> getAllBorrowedBooks(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort) {
        
        StringBuilder path = new StringBuilder("/borrowed-books");
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
        
        return apiIntegratorService.makeGetRequest(path.toString(), ApiType.BORROWED_BOOKS)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get borrowed book by ID", description = "Retrieves a borrowed book by its ID")
    public Mono<ResponseEntity<Map>> getBorrowedBookById(@PathVariable Long id) {
        return apiIntegratorService.makeGetRequest("/borrowed-books/" + id, ApiType.BORROWED_BOOKS)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @Operation(summary = "Borrow a book", description = "Records a book being borrowed")
    public Mono<ResponseEntity<Map>> borrowBook(@RequestBody Map<String, Object> borrowedBook) {
        return apiIntegratorService.makePostRequest("/borrowed-books", borrowedBook, ApiType.BORROWED_BOOKS)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update borrowed book record", description = "Updates an existing borrowed book record")
    public Mono<ResponseEntity<Map>> updateBorrowedBook(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> borrowedBook) {
        
        return apiIntegratorService.makePutRequest("/borrowed-books/" + id, borrowedBook, ApiType.BORROWED_BOOKS)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete borrowed book record", description = "Deletes a borrowed book record by its ID")
    public Mono<ResponseEntity<Map>> deleteBorrowedBook(@PathVariable Long id) {
        return apiIntegratorService.makeDeleteRequest("/borrowed-books/" + id, ApiType.BORROWED_BOOKS)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/{id}/return")
    @Operation(summary = "Return a book", description = "Records a book being returned")
    public Mono<ResponseEntity<Map>> returnBook(@PathVariable Long id) {
        return apiIntegratorService.makePostRequest("/borrowed-books/" + id + "/return", Map.of(), ApiType.BORROWED_BOOKS)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue books", description = "Gets a list of books that are overdue")
    public Mono<ResponseEntity<Map>> getOverdueBooks() {
        return apiIntegratorService.makeGetRequest("/borrowed-books/overdue", ApiType.BORROWED_BOOKS)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get books borrowed by user", description = "Gets a list of books borrowed by a specific user")
    public Mono<ResponseEntity<Map>> getBooksBorrowedByUser(@PathVariable Long userId) {
        return apiIntegratorService.makeGetRequest("/borrowed-books/user/" + userId, ApiType.BORROWED_BOOKS)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/book/{bookId}")
    @Operation(summary = "Get borrow history for book", description = "Gets the borrow history for a specific book")
    public Mono<ResponseEntity<Map>> getBorrowHistoryForBook(@PathVariable Long bookId) {
        return apiIntegratorService.makeGetRequest("/borrowed-books/book/" + bookId, ApiType.BORROWED_BOOKS)
                .map(ResponseEntity::ok);
    }
} 