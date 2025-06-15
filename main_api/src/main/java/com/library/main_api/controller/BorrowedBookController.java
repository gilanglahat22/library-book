package com.library.main_api.controller;

import com.library.main_api.service.ApiIntegratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/borrowed-books")
@CrossOrigin
@Tag(name = "Borrowed Books", description = "Borrowed books management endpoints")
public class BorrowedBookController {

    private final ApiIntegratorService apiService;

    @Autowired
    public BorrowedBookController(ApiIntegratorService apiService) {
        this.apiService = apiService;
    }

    @GetMapping
    @Operation(summary = "Get all borrowed books with pagination")
    public ResponseEntity<Object> getAllBorrowedBooks(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", size);
        
        return apiService.get("/borrowed-books", Object.class, params);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a borrowed book by ID")
    public ResponseEntity<Object> getBorrowedBookById(@PathVariable Long id) {
        return apiService.get("/borrowed-books/" + id, Object.class);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get borrowed books by status")
    public ResponseEntity<Object> getByStatus(
            @PathVariable String status,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", size);
        
        return apiService.get("/borrowed-books/status/" + status, Object.class, params);
    }

    @GetMapping("/member/{memberId}")
    @Operation(summary = "Get borrowed books by member ID")
    public ResponseEntity<Object> getByMemberId(
            @PathVariable Long memberId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", size);
        
        return apiService.get("/borrowed-books/member/" + memberId, Object.class, params);
    }

    @GetMapping("/book/{bookId}")
    @Operation(summary = "Get borrowed books by book ID")
    public ResponseEntity<Object> getByBookId(
            @PathVariable Long bookId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", size);
        
        return apiService.get("/borrowed-books/book/" + bookId, Object.class, params);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get borrowed books by date range")
    public ResponseEntity<Object> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate.toString());
        params.put("endDate", endDate.toString());
        params.put("page", page);
        params.put("size", size);
        
        return apiService.get("/borrowed-books/date-range", Object.class, params);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue books")
    public ResponseEntity<Object> getOverdueBooks(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", size);
        
        return apiService.get("/borrowed-books/overdue", Object.class, params);
    }

    @PostMapping
    @Operation(summary = "Borrow a book")
    public ResponseEntity<Object> borrowBook(@RequestBody Object borrowedBook) {
        return apiService.post("/borrowed-books", borrowedBook, Object.class);
    }

    @PutMapping("/{id}/return")
    @Operation(summary = "Return a borrowed book")
    public ResponseEntity<Object> returnBook(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate) {
        
        Map<String, Object> params = new HashMap<>();
        if (returnDate != null) {
            params.put("returnDate", returnDate.toString());
        }
        
        return apiService.put("/borrowed-books/" + id + "/return", null, Object.class);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update borrowed book status")
    public ResponseEntity<Object> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("status", status);
        
        return apiService.put("/borrowed-books/" + id + "/status", null, Object.class);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a borrowed book record")
    public ResponseEntity<Object> deleteBorrowedBook(@PathVariable Long id) {
        return apiService.delete("/borrowed-books/" + id, Object.class);
    }
} 