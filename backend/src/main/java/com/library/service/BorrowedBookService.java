package com.library.service;

import com.library.model.BorrowedBook;
import com.library.model.BorrowedBook.BorrowStatus;
import com.library.model.Book;
import com.library.model.Member;
import com.library.repository.BorrowedBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BorrowedBookService {
    
    private final BorrowedBookRepository borrowedBookRepository;
    private final BookService bookService;
    private final MemberService memberService;
    
    @Autowired
    public BorrowedBookService(BorrowedBookRepository borrowedBookRepository, 
                               BookService bookService, 
                               MemberService memberService) {
        this.borrowedBookRepository = borrowedBookRepository;
        this.bookService = bookService;
        this.memberService = memberService;
    }
    
    public Page<BorrowedBook> findAll(Pageable pageable) {
        return borrowedBookRepository.findAll(pageable);
    }
    
    public List<BorrowedBook> findAll() {
        return borrowedBookRepository.findAll();
    }
    
    public Optional<BorrowedBook> findById(Long id) {
        return borrowedBookRepository.findById(id);
    }
    
    public List<BorrowedBook> findByMemberId(Long memberId) {
        return borrowedBookRepository.findByMemberId(memberId);
    }
    
    public List<BorrowedBook> findByBookId(Long bookId) {
        return borrowedBookRepository.findByBookId(bookId);
    }
    
    public Page<BorrowedBook> findByStatus(BorrowStatus status, Pageable pageable) {
        return borrowedBookRepository.findByStatus(status, pageable);
    }
    
    public Page<BorrowedBook> findBySearchTerm(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return borrowedBookRepository.findAll(pageable);
        }
        return borrowedBookRepository.findBySearchTerm(searchTerm.trim(), pageable);
    }
    
    public Page<BorrowedBook> findByBorrowDate(LocalDate borrowDate, Pageable pageable) {
        return borrowedBookRepository.findByBorrowDate(borrowDate, pageable);
    }
    
    public Page<BorrowedBook> findByBorrowDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return borrowedBookRepository.findByBorrowDateBetween(startDate, endDate, pageable);
    }
    
    public Page<BorrowedBook> findBySearchTermAndDateRange(String searchTerm, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return borrowedBookRepository.findByBorrowDateBetween(startDate, endDate, pageable);
        }
        return borrowedBookRepository.findBySearchTermAndDateRange(searchTerm.trim(), startDate, endDate, pageable);
    }
    
    public List<BorrowedBook> findOverdueBooks() {
        return borrowedBookRepository.findOverdueBooks(LocalDate.now());
    }
    
    public List<BorrowedBook> findBooksDueOn(LocalDate date) {
        return borrowedBookRepository.findBooksDueOn(date);
    }
    
    public List<BorrowedBook> findActiveBorrowsByMember(Long memberId) {
        return borrowedBookRepository.findActiveBorrowsByMember(memberId);
    }
    
    public BorrowedBook borrowBook(Long memberId, Long bookId, LocalDate dueDate, String notes) {
        // Validate member can borrow
        if (!memberService.canBorrow(memberId)) {
            throw new RuntimeException("Member cannot borrow books at this time");
        }
        
        // Validate book is available
        if (!bookService.isAvailable(bookId)) {
            throw new RuntimeException("Book is not available for borrowing");
        }
        
        // Get member and book entities
        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Book book = bookService.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        // Create borrow record
        BorrowedBook borrowedBook = new BorrowedBook();
        borrowedBook.setMember(member);
        borrowedBook.setBook(book);
        borrowedBook.setBorrowDate(LocalDate.now());
        borrowedBook.setDueDate(dueDate != null ? dueDate : LocalDate.now().plusWeeks(2)); // Default 2 weeks
        borrowedBook.setStatus(BorrowStatus.BORROWED);
        borrowedBook.setNotes(notes);
        
        // Update book availability
        if (!bookService.borrowBook(bookId)) {
            throw new RuntimeException("Failed to update book availability");
        }
        
        return borrowedBookRepository.save(borrowedBook);
    }
    
    public BorrowedBook returnBook(Long borrowedBookId) {
        BorrowedBook borrowedBook = borrowedBookRepository.findById(borrowedBookId)
                .orElseThrow(() -> new RuntimeException("Borrowed book record not found"));
        
        if (borrowedBook.getStatus() == BorrowStatus.RETURNED) {
            throw new RuntimeException("Book has already been returned");
        }
        
        // Update borrow record
        borrowedBook.setReturnDate(LocalDate.now());
        borrowedBook.setStatus(BorrowStatus.RETURNED);
        
        // Update book availability
        bookService.returnBook(borrowedBook.getBook().getId());
        
        return borrowedBookRepository.save(borrowedBook);
    }
    
    public BorrowedBook save(BorrowedBook borrowedBook) {
        validateBorrowedBook(borrowedBook);
        return borrowedBookRepository.save(borrowedBook);
    }
    
    public BorrowedBook update(Long id, BorrowedBook borrowedBookDetails) {
        BorrowedBook borrowedBook = borrowedBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borrowed book not found with id: " + id));
        
        // Only allow certain fields to be updated
        borrowedBook.setDueDate(borrowedBookDetails.getDueDate());
        borrowedBook.setNotes(borrowedBookDetails.getNotes());
        
        // Handle status changes carefully
        if (borrowedBookDetails.getStatus() != null && borrowedBookDetails.getStatus() != borrowedBook.getStatus()) {
            if (borrowedBookDetails.getStatus() == BorrowStatus.RETURNED && borrowedBook.getStatus() != BorrowStatus.RETURNED) {
                borrowedBook.setReturnDate(LocalDate.now());
                bookService.returnBook(borrowedBook.getBook().getId());
            }
            borrowedBook.setStatus(borrowedBookDetails.getStatus());
        }
        
        validateBorrowedBook(borrowedBook);
        return borrowedBookRepository.save(borrowedBook);
    }
    
    public void deleteById(Long id) {
        BorrowedBook borrowedBook = borrowedBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borrowed book not found with id: " + id));
        
        // If book is still borrowed, return it first
        if (borrowedBook.getStatus() == BorrowStatus.BORROWED) {
            bookService.returnBook(borrowedBook.getBook().getId());
        }
        
        borrowedBookRepository.deleteById(id);
    }
    
    public void markAsOverdue() {
        List<BorrowedBook> overdueBooks = borrowedBookRepository.findOverdueBooks(LocalDate.now());
        for (BorrowedBook borrowedBook : overdueBooks) {
            if (borrowedBook.getStatus() == BorrowStatus.BORROWED) {
                borrowedBook.setStatus(BorrowStatus.OVERDUE);
                borrowedBookRepository.save(borrowedBook);
            }
        }
    }
    
    public BorrowedBook markAsLost(Long borrowedBookId) {
        BorrowedBook borrowedBook = borrowedBookRepository.findById(borrowedBookId)
                .orElseThrow(() -> new RuntimeException("Borrowed book record not found"));
        
        borrowedBook.setStatus(BorrowStatus.LOST);
        return borrowedBookRepository.save(borrowedBook);
    }
    
    public Long countCurrentBorrows() {
        return borrowedBookRepository.countCurrentBorrows();
    }
    
    public Long countOverdueBooks() {
        return borrowedBookRepository.countOverdueBooks();
    }
    
    public boolean existsById(Long id) {
        return borrowedBookRepository.existsById(id);
    }
    
    private void validateBorrowedBook(BorrowedBook borrowedBook) {
        if (borrowedBook.getMember() == null) {
            throw new IllegalArgumentException("Member is required");
        }
        
        if (borrowedBook.getBook() == null) {
            throw new IllegalArgumentException("Book is required");
        }
        
        if (borrowedBook.getBorrowDate() == null) {
            throw new IllegalArgumentException("Borrow date is required");
        }
        
        if (borrowedBook.getBorrowDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Borrow date cannot be in the future");
        }
        
        if (borrowedBook.getDueDate() != null && borrowedBook.getDueDate().isBefore(borrowedBook.getBorrowDate())) {
            throw new IllegalArgumentException("Due date cannot be before borrow date");
        }
        
        if (borrowedBook.getReturnDate() != null && borrowedBook.getReturnDate().isBefore(borrowedBook.getBorrowDate())) {
            throw new IllegalArgumentException("Return date cannot be before borrow date");
        }
        
        if (borrowedBook.getNotes() != null && borrowedBook.getNotes().length() > 500) {
            throw new IllegalArgumentException("Notes must not exceed 500 characters");
        }
    }
} 