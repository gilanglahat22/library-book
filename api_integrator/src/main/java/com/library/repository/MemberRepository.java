package com.library.repository;

import com.library.model.Member;
import com.library.model.Member.MembershipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    Optional<Member> findByEmail(String email);
    
    List<Member> findByStatus(MembershipStatus status);
    
    Page<Member> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    Page<Member> findByStatus(MembershipStatus status, Pageable pageable);
    
    @Query("SELECT m FROM Member m WHERE " +
           "LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(m.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(m.phone) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Member> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.borrowedBooks WHERE m.id = :id")
    Optional<Member> findByIdWithBorrowedBooks(@Param("id") Long id);
    
    @Query("SELECT COUNT(bb) FROM BorrowedBook bb WHERE bb.member.id = :memberId AND bb.status = 'BORROWED'")
    Long countCurrentBorrowsByMember(@Param("memberId") Long memberId);
    
    @Query("SELECT m FROM Member m JOIN m.borrowedBooks bb WHERE bb.status = 'OVERDUE' GROUP BY m")
    List<Member> findMembersWithOverdueBooks();
    
    @Query("SELECT m FROM Member m WHERE m.status = 'ACTIVE' AND " +
           "(SELECT COUNT(bb) FROM BorrowedBook bb WHERE bb.member = m AND bb.status = 'BORROWED') = 0")
    Page<Member> findActiveMembersWithNoBorrows(Pageable pageable);
    
    @Query("SELECT COUNT(bb) FROM BorrowedBook bb WHERE bb.member.id = :memberId")
    Long countTotalBorrowsByMember(@Param("memberId") Long memberId);
} 