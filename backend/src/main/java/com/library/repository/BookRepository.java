package com.library.repository;

import com.library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    Optional<Book> findByIsbn(String isbn);
    
    List<Book> findByCategory(String category);
    
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    Page<Book> findByCategoryIgnoreCase(String category, Pageable pageable);
    
    Page<Book> findByAuthorId(Long authorId, Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.category) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.author.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.isbn) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Book> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE b.publishingYear BETWEEN :startYear AND :endYear")
    Page<Book> findByPublishingYearBetween(@Param("startYear") Integer startYear, @Param("endYear") Integer endYear, Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0")
    Page<Book> findAvailableBooks(Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE b.availableCopies = 0")
    Page<Book> findUnavailableBooks(Pageable pageable);
    
    @Query("SELECT DISTINCT b.category FROM Book b ORDER BY b.category")
    List<String> findAllCategories();
    
    @Query("SELECT COUNT(bb) FROM BorrowedBook bb WHERE bb.book.id = :bookId AND bb.status = 'BORROWED'")
    Long countCurrentBorrowsByBook(@Param("bookId") Long bookId);
    
    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.author WHERE b.id = :id")
    Optional<Book> findByIdWithAuthor(@Param("id") Long id);
} 