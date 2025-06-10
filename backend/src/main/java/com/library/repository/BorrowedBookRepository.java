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
    
    List<BorrowedBook> findByMemberId(Long memberId);
    
    List<BorrowedBook> findByBookId(Long bookId);
    
    List<BorrowedBook> findByStatus(BorrowStatus status);
    
    Page<BorrowedBook> findByStatus(BorrowStatus status, Pageable pageable);
    
    Page<BorrowedBook> findByMemberIdAndStatus(Long memberId, BorrowStatus status, Pageable pageable);
    
    @Query("SELECT DISTINCT bb FROM BorrowedBook bb " +
           "LEFT JOIN FETCH bb.book b " +
           "LEFT JOIN FETCH bb.member m " +
           "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(m.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<BorrowedBook> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT bb FROM BorrowedBook bb WHERE bb.borrowDate = :borrowDate")
    Page<BorrowedBook> findByBorrowDate(@Param("borrowDate") LocalDate borrowDate, Pageable pageable);
    
    @Query("SELECT bb FROM BorrowedBook bb WHERE bb.borrowDate BETWEEN :startDate AND :endDate")
    Page<BorrowedBook> findByBorrowDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);
    
    @Query("SELECT bb FROM BorrowedBook bb WHERE bb.dueDate < :currentDate AND bb.status = 'BORROWED'")
    List<BorrowedBook> findOverdueBooks(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT bb FROM BorrowedBook bb WHERE bb.dueDate = :date AND bb.status = 'BORROWED'")
    List<BorrowedBook> findBooksDueOn(@Param("date") LocalDate date);
    
    @Query("SELECT bb FROM BorrowedBook bb WHERE " +
           "(LOWER(bb.book.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(bb.member.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "bb.borrowDate BETWEEN :startDate AND :endDate")
    Page<BorrowedBook> findBySearchTermAndDateRange(@Param("searchTerm") String searchTerm, 
                                                   @Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate, 
                                                   Pageable pageable);
    
    @Query("SELECT bb FROM BorrowedBook bb WHERE bb.member.id = :memberId AND bb.status != 'RETURNED' ORDER BY bb.borrowDate DESC")
    List<BorrowedBook> findActiveBorrowsByMember(@Param("memberId") Long memberId);
    
    @Query("SELECT COUNT(bb) FROM BorrowedBook bb WHERE bb.status = 'BORROWED'")
    Long countCurrentBorrows();
    
    @Query("SELECT COUNT(bb) FROM BorrowedBook bb WHERE bb.status = 'OVERDUE'")
    Long countOverdueBooks();
} 