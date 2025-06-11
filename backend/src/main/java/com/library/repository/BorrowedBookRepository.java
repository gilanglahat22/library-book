package com.library.repository;

import com.library.model.BorrowedBook;
import com.library.model.BorrowedBook.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowedBookRepository extends JpaRepository<BorrowedBook, Long> {
    
    Page<BorrowedBook> findByStatus(BorrowStatus status, Pageable pageable);
    
    Page<BorrowedBook> findByMemberId(Long memberId, Pageable pageable);
    
    Page<BorrowedBook> findByBookId(Long bookId, Pageable pageable);
    
    Page<BorrowedBook> findByBorrowDate(LocalDate borrowDate, Pageable pageable);
    
    Page<BorrowedBook> findByBorrowDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    Page<BorrowedBook> findByStatusAndDueDateBefore(BorrowStatus status, LocalDate date, Pageable pageable);
    
    @Query("SELECT bb FROM BorrowedBook bb WHERE " +
           "LOWER(bb.member.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(bb.book.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<BorrowedBook> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT bb FROM BorrowedBook bb WHERE " +
           "(LOWER(bb.member.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(bb.book.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "bb.borrowDate BETWEEN :startDate AND :endDate")
    Page<BorrowedBook> findBySearchTermAndDateRange(
            @Param("searchTerm") String searchTerm,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
    
    @Query("SELECT bb FROM BorrowedBook bb WHERE bb.status = 'BORROWED' AND bb.dueDate < :date")
    List<BorrowedBook> findOverdueBooks(@Param("date") LocalDate date);
    
    @Query("SELECT bb FROM BorrowedBook bb WHERE bb.dueDate = :date")
    List<BorrowedBook> findBooksDueOn(@Param("date") LocalDate date);
    
    @Query("SELECT bb FROM BorrowedBook bb WHERE bb.member.id = :memberId AND bb.status = 'BORROWED'")
    List<BorrowedBook> findActiveBorrowsByMember(@Param("memberId") Long memberId);
    
    @Query("SELECT COUNT(bb) FROM BorrowedBook bb WHERE bb.status = 'BORROWED'")
    Long countCurrentBorrows();
} 