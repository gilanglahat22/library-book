package com.library.service;

import com.library.model.Book;
import com.library.model.BorrowedBook;
import com.library.model.BorrowedBook.BorrowStatus;
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
    
    public Optional<BorrowedBook> findById(Long id) {
        return borrowedBookRepository.findById(id);
    }
    
    public Page<BorrowedBook> findByStatus(BorrowStatus status, Pageable pageable) {
        return borrowedBookRepository.findByStatus(status, pageable);
    }
    
    public Page<BorrowedBook> findByMemberId(Long memberId, Pageable pageable) {
        return borrowedBookRepository.findByMemberId(memberId, pageable);
    }
    
    public Page<BorrowedBook> findByBookId(Long bookId, Pageable pageable) {
        return borrowedBookRepository.findByBookId(bookId, pageable);
    }
    
    public Page<BorrowedBook> findByBorrowDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return borrowedBookRepository.findByBorrowDateBetween(startDate, endDate, pageable);
    }
    
    public Page<BorrowedBook> findOverdueBooks(Pageable pageable) {
        return borrowedBookRepository.findByStatusAndDueDateBefore(BorrowStatus.BORROWED, LocalDate.now(), pageable);
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
    
    public BorrowedBook borrowBook(BorrowedBook borrowedBook) {
        Book book = bookService.findById(borrowedBook.getBook().getId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        
        Member member = memberService.findById(borrowedBook.getMember().getId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalArgumentException("No copies available for borrowing");
        }
        
        // Set default values if not provided
        if (borrowedBook.getBorrowDate() == null) {
            borrowedBook.setBorrowDate(LocalDate.now());
        }
        if (borrowedBook.getDueDate() == null) {
            borrowedBook.setDueDate(borrowedBook.getBorrowDate().plusDays(14)); // Default loan period: 14 days
        }
        borrowedBook.setStatus(BorrowStatus.BORROWED);
        
        // Update book available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookService.save(book);
        
        return borrowedBookRepository.save(borrowedBook);
    }
    
    public Optional<BorrowedBook> returnBook(Long id, LocalDate returnDate) {
        Optional<BorrowedBook> borrowedBookOpt = borrowedBookRepository.findById(id);
        if (borrowedBookOpt.isPresent()) {
            BorrowedBook borrowedBook = borrowedBookOpt.get();
            if (borrowedBook.getStatus() != BorrowStatus.BORROWED) {
                throw new IllegalArgumentException("Book is not in borrowed status");
            }
            
            borrowedBook.setReturnDate(returnDate);
            borrowedBook.setStatus(BorrowStatus.RETURNED);
            
            // Update book available copies
            Book book = borrowedBook.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookService.save(book);
            
            return Optional.of(borrowedBookRepository.save(borrowedBook));
        }
        return Optional.empty();
    }
    
    public Optional<BorrowedBook> updateStatus(Long id, BorrowStatus status) {
        Optional<BorrowedBook> borrowedBookOpt = borrowedBookRepository.findById(id);
        if (borrowedBookOpt.isPresent()) {
            BorrowedBook borrowedBook = borrowedBookOpt.get();
            borrowedBook.setStatus(status);
            return Optional.of(borrowedBookRepository.save(borrowedBook));
        }
        return Optional.empty();
    }
    
    public void delete(Long id) {
        borrowedBookRepository.deleteById(id);
    }
    
    public Long countCurrentBorrows() {
        return borrowedBookRepository.countCurrentBorrows();
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