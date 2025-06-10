package com.library.service;

import com.library.model.Author;
import com.library.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AuthorService {
    
    private final AuthorRepository authorRepository;
    
    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }
    
    public Page<Author> findAll(Pageable pageable) {
        return authorRepository.findAll(pageable);
    }
    
    public List<Author> findAll() {
        return authorRepository.findAll();
    }
    
    public Optional<Author> findById(Long id) {
        return authorRepository.findById(id);
    }
    
    public Optional<Author> findByIdWithBooks(Long id) {
        return authorRepository.findByIdWithBooks(id);
    }
    
    public Optional<Author> findByName(String name) {
        return authorRepository.findByName(name);
    }
    
    public Page<Author> findBySearchTerm(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return authorRepository.findAll(pageable);
        }
        return authorRepository.findBySearchTerm(searchTerm.trim(), pageable);
    }
    
    public List<Author> findByNationality(String nationality) {
        return authorRepository.findByNationality(nationality);
    }
    
    public List<Author> findByBirthYearRange(Integer startYear, Integer endYear) {
        return authorRepository.findByBirthYearBetween(startYear, endYear);
    }
    
    public Author save(Author author) {
        validateAuthor(author);
        return authorRepository.save(author);
    }
    
    public Author update(Long id, Author authorDetails) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));
        
        author.setName(authorDetails.getName());
        author.setBiography(authorDetails.getBiography());
        author.setNationality(authorDetails.getNationality());
        author.setBirthYear(authorDetails.getBirthYear());
        
        validateAuthor(author);
        return authorRepository.save(author);
    }
    
    public void deleteById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));
        
        Long bookCount = authorRepository.countBooksByAuthor(id);
        if (bookCount > 0) {
            throw new RuntimeException("Cannot delete author with existing books. Please delete or reassign books first.");
        }
        
        authorRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return authorRepository.existsById(id);
    }
    
    public boolean existsByName(String name) {
        return authorRepository.findByName(name).isPresent();
    }
    
    public Long countBooksByAuthor(Long authorId) {
        return authorRepository.countBooksByAuthor(authorId);
    }
    
    private void validateAuthor(Author author) {
        if (author.getName() == null || author.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Author name is required");
        }
        
        if (author.getName().length() > 100) {
            throw new IllegalArgumentException("Author name must not exceed 100 characters");
        }
        
        if (author.getBiography() != null && author.getBiography().length() > 500) {
            throw new IllegalArgumentException("Biography must not exceed 500 characters");
        }
        
        if (author.getNationality() != null && author.getNationality().length() > 50) {
            throw new IllegalArgumentException("Nationality must not exceed 50 characters");
        }
        
        if (author.getBirthYear() != null && (author.getBirthYear() < 0 || author.getBirthYear() > java.time.Year.now().getValue())) {
            throw new IllegalArgumentException("Birth year must be valid");
        }
    }
} 