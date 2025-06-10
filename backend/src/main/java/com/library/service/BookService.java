package com.library.service;

import com.library.model.Book;
import com.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {
    
    private final BookRepository bookRepository;
    
    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }
    
    public List<Book> findAll() {
        return bookRepository.findAll();
    }
    
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }
    
    public Optional<Book> findByIdWithAuthor(Long id) {
        return bookRepository.findByIdWithAuthor(id);
    }
    
    public Optional<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }
    
    public Page<Book> findBySearchTerm(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return bookRepository.findAll(pageable);
        }
        return bookRepository.findBySearchTerm(searchTerm.trim(), pageable);
    }
    
    public Page<Book> findByCategory(String category, Pageable pageable) {
        if (category == null || category.trim().isEmpty()) {
            return bookRepository.findAll(pageable);
        }
        return bookRepository.findByCategoryIgnoreCase(category.trim(), pageable);
    }
    
    public Page<Book> findByAuthorId(Long authorId, Pageable pageable) {
        return bookRepository.findByAuthorId(authorId, pageable);
    }
    
    public Page<Book> findByPublishingYearRange(Integer startYear, Integer endYear, Pageable pageable) {
        return bookRepository.findByPublishingYearBetween(startYear, endYear, pageable);
    }
    
    public Page<Book> findAvailableBooks(Pageable pageable) {
        return bookRepository.findAvailableBooks(pageable);
    }
    
    public Page<Book> findUnavailableBooks(Pageable pageable) {
        return bookRepository.findUnavailableBooks(pageable);
    }
    
    public List<String> getAllCategories() {
        return bookRepository.findAllCategories();
    }
    
    public Book save(Book book) {
        validateBook(book);
        
        // Set default values for copies if not provided
        if (book.getTotalCopies() == null || book.getTotalCopies() <= 0) {
            book.setTotalCopies(1);
        }
        if (book.getAvailableCopies() == null) {
            book.setAvailableCopies(book.getTotalCopies());
        }
        
        return bookRepository.save(book);
    }
    
    public Book update(Long id, Book bookDetails) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        
        book.setTitle(bookDetails.getTitle());
        book.setIsbn(bookDetails.getIsbn());
        book.setCategory(bookDetails.getCategory());
        book.setPublishingYear(bookDetails.getPublishingYear());
        book.setDescription(bookDetails.getDescription());
        book.setAuthor(bookDetails.getAuthor());
        
        // Handle copy count updates carefully
        if (bookDetails.getTotalCopies() != null && bookDetails.getTotalCopies() > 0) {
            int currentBorrowed = book.getTotalCopies() - book.getAvailableCopies();
            book.setTotalCopies(bookDetails.getTotalCopies());
            book.setAvailableCopies(Math.max(0, bookDetails.getTotalCopies() - currentBorrowed));
        }
        
        validateBook(book);
        return bookRepository.save(book);
    }
    
    public void deleteById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        
        Long currentBorrows = bookRepository.countCurrentBorrowsByBook(id);
        if (currentBorrows > 0) {
            throw new RuntimeException("Cannot delete book with active borrows. Please return all copies first.");
        }
        
        bookRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return bookRepository.existsById(id);
    }
    
    public boolean isAvailable(Long bookId) {
        return bookRepository.findById(bookId)
                .map(book -> book.getAvailableCopies() > 0)
                .orElse(false);
    }
    
    public boolean borrowBook(Long bookId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isEmpty()) {
            throw new RuntimeException("Book not found with id: " + bookId);
        }
        
        Book book = bookOpt.get();
        if (book.getAvailableCopies() <= 0) {
            return false; // No available copies
        }
        
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        return true;
    }
    
    public void returnBook(Long bookId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isEmpty()) {
            throw new RuntimeException("Book not found with id: " + bookId);
        }
        
        Book book = bookOpt.get();
        if (book.getAvailableCopies() < book.getTotalCopies()) {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);
        }
    }
    
    public Long countCurrentBorrowsByBook(Long bookId) {
        return bookRepository.countCurrentBorrowsByBook(bookId);
    }
    
    private void validateBook(Book book) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book title is required");
        }
        
        if (book.getTitle().length() > 200) {
            throw new IllegalArgumentException("Title must not exceed 200 characters");
        }
        
        if (book.getCategory() == null || book.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Category is required");
        }
        
        if (book.getCategory().length() > 50) {
            throw new IllegalArgumentException("Category must not exceed 50 characters");
        }
        
        if (book.getPublishingYear() == null) {
            throw new IllegalArgumentException("Publishing year is required");
        }
        
        if (book.getPublishingYear() < 0 || book.getPublishingYear() > java.time.Year.now().getValue()) {
            throw new IllegalArgumentException("Publishing year must be valid");
        }
        
        if (book.getAuthor() == null) {
            throw new IllegalArgumentException("Author is required");
        }
        
        if (book.getIsbn() != null && !book.getIsbn().trim().isEmpty()) {
            if (book.getIsbn().length() > 20) {
                throw new IllegalArgumentException("ISBN must not exceed 20 characters");
            }
        }
        
        if (book.getDescription() != null && book.getDescription().length() > 1000) {
            throw new IllegalArgumentException("Description must not exceed 1000 characters");
        }
        
        if (book.getTotalCopies() != null && book.getTotalCopies() <= 0) {
            throw new IllegalArgumentException("Total copies must be greater than 0");
        }
        
        if (book.getAvailableCopies() != null && book.getAvailableCopies() < 0) {
            throw new IllegalArgumentException("Available copies cannot be negative");
        }
        
        if (book.getTotalCopies() != null && book.getAvailableCopies() != null && 
            book.getAvailableCopies() > book.getTotalCopies()) {
            throw new IllegalArgumentException("Available copies cannot exceed total copies");
        }
    }
} 