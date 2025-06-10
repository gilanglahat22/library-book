package com.library.controller;

import com.library.model.BorrowedBook;
import com.library.model.BorrowedBook.BorrowStatus;
import com.library.service.BorrowedBookService;
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
public class BorrowedBookController {
    
    private final BorrowedBookService borrowedBookService;
    
    @Autowired
    public BorrowedBookController(BorrowedBookService borrowedBookService) {
        this.borrowedBookService = borrowedBookService;
    }
    
    @GetMapping
    public ResponseEntity<Page<BorrowedBook>> getAllBorrowedBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "borrowDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BorrowStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate borrowDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<BorrowedBook> borrowedBooks;
        
        if (search != null && !search.trim().isEmpty() && startDate != null && endDate != null) {
            borrowedBooks = borrowedBookService.findBySearchTermAndDateRange(search, startDate, endDate, pageable);
        } else if (search != null && !search.trim().isEmpty()) {
            borrowedBooks = borrowedBookService.findBySearchTerm(search, pageable);
        } else if (status != null) {
            borrowedBooks = borrowedBookService.findByStatus(status, pageable);
        } else if (borrowDate != null) {
            borrowedBooks = borrowedBookService.findByBorrowDate(borrowDate, pageable);
        } else if (startDate != null && endDate != null) {
            borrowedBooks = borrowedBookService.findByBorrowDateRange(startDate, endDate, pageable);
        } else {
            borrowedBooks = borrowedBookService.findAll(pageable);
        }
        
        return ResponseEntity.ok(borrowedBooks);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<BorrowedBook>> getAllBorrowedBooksWithoutPagination() {
        List<BorrowedBook> borrowedBooks = borrowedBookService.findAll();
        return ResponseEntity.ok(borrowedBooks);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BorrowedBook> getBorrowedBookById(@PathVariable Long id) {
        Optional<BorrowedBook> borrowedBook = borrowedBookService.findById(id);
        return borrowedBook.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<BorrowedBook>> getBorrowedBooksByMember(@PathVariable Long memberId) {
        List<BorrowedBook> borrowedBooks = borrowedBookService.findByMemberId(memberId);
        return ResponseEntity.ok(borrowedBooks);
    }
    
    @GetMapping("/member/{memberId}/active")
    public ResponseEntity<List<BorrowedBook>> getActiveBorrowsByMember(@PathVariable Long memberId) {
        List<BorrowedBook> borrowedBooks = borrowedBookService.findActiveBorrowsByMember(memberId);
        return ResponseEntity.ok(borrowedBooks);
    }
    
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<BorrowedBook>> getBorrowedBooksByBook(@PathVariable Long bookId) {
        List<BorrowedBook> borrowedBooks = borrowedBookService.findByBookId(bookId);
        return ResponseEntity.ok(borrowedBooks);
    }
    
    @GetMapping("/overdue")
    public ResponseEntity<List<BorrowedBook>> getOverdueBooks() {
        List<BorrowedBook> overdueBooks = borrowedBookService.findOverdueBooks();
        return ResponseEntity.ok(overdueBooks);
    }
    
    @GetMapping("/due-on/{date}")
    public ResponseEntity<List<BorrowedBook>> getBooksDueOn(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<BorrowedBook> booksDue = borrowedBookService.findBooksDueOn(date);
        return ResponseEntity.ok(booksDue);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<BorrowedBook>> searchBorrowedBooks(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "borrowDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<BorrowedBook> borrowedBooks;
        if (startDate != null && endDate != null) {
            borrowedBooks = borrowedBookService.findBySearchTermAndDateRange(query, startDate, endDate, pageable);
        } else {
            borrowedBooks = borrowedBookService.findBySearchTerm(query, pageable);
        }
        
        return ResponseEntity.ok(borrowedBooks);
    }
    
    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(
            @RequestParam Long memberId,
            @RequestParam Long bookId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @RequestParam(required = false) String notes) {
        try {
            BorrowedBook borrowedBook = borrowedBookService.borrowBook(memberId, bookId, dueDate, notes);
            return ResponseEntity.status(HttpStatus.CREATED).body(borrowedBook);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error borrowing book: " + e.getMessage());
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createBorrowedBook(@Valid @RequestBody BorrowedBook borrowedBook) {
        try {
            BorrowedBook savedBorrowedBook = borrowedBookService.save(borrowedBook);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBorrowedBook);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating borrowed book record: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBorrowedBook(@PathVariable Long id, @Valid @RequestBody BorrowedBook borrowedBookDetails) {
        try {
            if (!borrowedBookService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            BorrowedBook updatedBorrowedBook = borrowedBookService.update(id, borrowedBookDetails);
            return ResponseEntity.ok(updatedBorrowedBook);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating borrowed book: " + e.getMessage());
        }
    }
    
    @PatchMapping("/{id}/return")
    public ResponseEntity<?> returnBook(@PathVariable Long id) {
        try {
            if (!borrowedBookService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            BorrowedBook returnedBook = borrowedBookService.returnBook(id);
            return ResponseEntity.ok(returnedBook);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error returning book: " + e.getMessage());
        }
    }
    
    @PatchMapping("/{id}/mark-lost")
    public ResponseEntity<?> markBookAsLost(@PathVariable Long id) {
        try {
            if (!borrowedBookService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            BorrowedBook lostBook = borrowedBookService.markAsLost(id);
            return ResponseEntity.ok(lostBook);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error marking book as lost: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBorrowedBook(@PathVariable Long id) {
        try {
            if (!borrowedBookService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            borrowedBookService.deleteById(id);
            return ResponseEntity.ok().body("Borrowed book record deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting borrowed book record: " + e.getMessage());
        }
    }
    
    @PostMapping("/mark-overdue")
    public ResponseEntity<?> markOverdueBooks() {
        try {
            borrowedBookService.markAsOverdue();
            return ResponseEntity.ok().body("Overdue books marked successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error marking overdue books: " + e.getMessage());
        }
    }
    
    @GetMapping("/statistics/current-borrows")
    public ResponseEntity<Long> getCurrentBorrowsCount() {
        Long count = borrowedBookService.countCurrentBorrows();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/statistics/overdue-count")
    public ResponseEntity<Long> getOverdueBooksCount() {
        Long count = borrowedBookService.countOverdueBooks();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/by-status/{status}")
    public ResponseEntity<Page<BorrowedBook>> getBorrowedBooksByStatus(
            @PathVariable BorrowStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "borrowDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<BorrowedBook> borrowedBooks = borrowedBookService.findByStatus(status, pageable);
        return ResponseEntity.ok(borrowedBooks);
    }
} 