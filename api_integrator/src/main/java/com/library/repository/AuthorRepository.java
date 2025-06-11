package com.library.repository;

import com.library.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    Optional<Author> findByName(String name);
    
    List<Author> findByNationality(String nationality);
    
    Page<Author> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    @Query("SELECT a FROM Author a WHERE " +
           "LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.nationality) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Author> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT a FROM Author a LEFT JOIN FETCH a.books WHERE a.id = :id")
    Optional<Author> findByIdWithBooks(@Param("id") Long id);
    
    @Query("SELECT a FROM Author a WHERE a.birthYear BETWEEN :startYear AND :endYear")
    List<Author> findByBirthYearBetween(@Param("startYear") Integer startYear, @Param("endYear") Integer endYear);
    
    @Query("SELECT COUNT(b) FROM Book b WHERE b.author.id = :authorId")
    Long countBooksByAuthor(@Param("authorId") Long authorId);
} 