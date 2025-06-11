package com.library.controller;

import com.library.model.BorrowedBook;
import com.library.model.BorrowedBook.BorrowStatus;
import com.library.service.BorrowedBookService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/borrowed-books")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Borrowed Books", description = "Endpoints for managing borrowed books")
public class BorrowedBookController {
    
    private final BorrowedBookService borrowedBookService;
    
    @Autowired
    public BorrowedBookController(BorrowedBookService borrowedBookService) {
        this.borrowedBookService = borrowedBookService;
    }
    
    @GetMapping
    @Operation(summary = "Get all borrowed books with pagination")
    public Page<BorrowedBook> getAllBorrowedBooks(Pageable pageable) {
        return borrowedBookService.findAll(pageable);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get a borrowed book by ID")
    public ResponseEntity<BorrowedBook> getBorrowedBookById(
            @Parameter(description = "Borrowed book ID") @PathVariable Long id) {
        return borrowedBookService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get borrowed books by status")
    public Page<BorrowedBook> getByStatus(
            @Parameter(description = "Borrow status") @PathVariable BorrowStatus status,
            Pageable pageable) {
        return borrowedBookService.findByStatus(status, pageable);
    }
    
    @GetMapping("/member/{memberId}")
    @Operation(summary = "Get borrowed books by member ID")
    public Page<BorrowedBook> getByMemberId(
            @Parameter(description = "Member ID") @PathVariable Long memberId,
            Pageable pageable) {
        return borrowedBookService.findByMemberId(memberId, pageable);
    }
    
    @GetMapping("/book/{bookId}")
    @Operation(summary = "Get borrowed books by book ID")
    public Page<BorrowedBook> getByBookId(
            @Parameter(description = "Book ID") @PathVariable Long bookId,
            Pageable pageable) {
        return borrowedBookService.findByBookId(bookId, pageable);
    }
    
    @GetMapping("/date-range")
    @Operation(summary = "Get borrowed books by date range")
    public Page<BorrowedBook> getByDateRange(
            @Parameter(description = "Start date (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {
        return borrowedBookService.findByBorrowDateBetween(startDate, endDate, pageable);
    }
    
    @GetMapping("/overdue")
    @Operation(summary = "Get overdue books")
    public Page<BorrowedBook> getOverdueBooks(Pageable pageable) {
        return borrowedBookService.findOverdueBooks(pageable);
    }
    
    @PostMapping
    @Operation(summary = "Borrow a book")
    public BorrowedBook borrowBook(@RequestBody BorrowedBook borrowedBook) {
        return borrowedBookService.borrowBook(borrowedBook);
    }
    
    @PutMapping("/{id}/return")
    @Operation(summary = "Return a borrowed book")
    public ResponseEntity<BorrowedBook> returnBook(
            @Parameter(description = "Borrowed book ID") @PathVariable Long id,
            @Parameter(description = "Return date (yyyy-MM-dd)") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate) {
        return borrowedBookService.returnBook(id, returnDate != null ? returnDate : LocalDate.now())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "Update borrowed book status")
    public ResponseEntity<BorrowedBook> updateStatus(
            @Parameter(description = "Borrowed book ID") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam BorrowStatus status) {
        return borrowedBookService.updateStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a borrowed book record")
    public ResponseEntity<Void> deleteBorrowedBook(
            @Parameter(description = "Borrowed book ID") @PathVariable Long id) {
        borrowedBookService.delete(id);
        return ResponseEntity.ok().build();
    }
} 