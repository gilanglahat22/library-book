package com.library.service;

import com.library.model.Member;
import com.library.model.Member.MembershipStatus;
import com.library.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Transactional
public class MemberService {
    
    private final MemberRepository memberRepository;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    
    public Page<Member> findAll(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }
    
    public List<Member> findAll() {
        return memberRepository.findAll();
    }
    
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }
    
    public Optional<Member> findByIdWithBorrowedBooks(Long id) {
        return memberRepository.findByIdWithBorrowedBooks(id);
    }
    
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
    
    public Page<Member> findBySearchTerm(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return memberRepository.findAll(pageable);
        }
        return memberRepository.findBySearchTerm(searchTerm.trim(), pageable);
    }
    
    public Page<Member> findByStatus(MembershipStatus status, Pageable pageable) {
        return memberRepository.findByStatus(status, pageable);
    }
    
    public List<Member> findMembersWithOverdueBooks() {
        return memberRepository.findMembersWithOverdueBooks();
    }
    
    public Page<Member> findActiveMembersWithNoBorrows(Pageable pageable) {
        return memberRepository.findActiveMembersWithNoBorrows(pageable);
    }
    
    public Member save(Member member) {
        validateMember(member);
        
        // Set membership date if not provided
        if (member.getMembershipDate() == null) {
            member.setMembershipDate(LocalDateTime.now());
        }
        
        // Set default status if not provided
        if (member.getStatus() == null) {
            member.setStatus(MembershipStatus.ACTIVE);
        }
        
        return memberRepository.save(member);
    }
    
    public Member update(Long id, Member memberDetails) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
        
        member.setName(memberDetails.getName());
        member.setEmail(memberDetails.getEmail());
        member.setPhone(memberDetails.getPhone());
        member.setAddress(memberDetails.getAddress());
        
        // Only update status if provided
        if (memberDetails.getStatus() != null) {
            member.setStatus(memberDetails.getStatus());
        }
        
        validateMember(member);
        return memberRepository.save(member);
    }
    
    public void deleteById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
        
        Long activeBorrows = memberRepository.countCurrentBorrowsByMember(id);
        if (activeBorrows > 0) {
            throw new RuntimeException("Cannot delete member with active borrows. Please return all books first.");
        }
        
        memberRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return memberRepository.existsById(id);
    }
    
    public boolean existsByEmail(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }
    
    public Long countCurrentBorrowsByMember(Long memberId) {
        return memberRepository.countCurrentBorrowsByMember(memberId);
    }
    
    public Long countTotalBorrowsByMember(Long memberId) {
        return memberRepository.countTotalBorrowsByMember(memberId);
    }
    
    public Member suspendMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
        
        member.setStatus(MembershipStatus.SUSPENDED);
        return memberRepository.save(member);
    }
    
    public Member activateMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
        
        member.setStatus(MembershipStatus.ACTIVE);
        return memberRepository.save(member);
    }
    
    public boolean canBorrow(Long memberId) {
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            return false;
        }
        
        Member member = memberOpt.get();
        if (member.getStatus() != MembershipStatus.ACTIVE) {
            return false;
        }
        
        // Check if member has too many active borrows (assuming max 5)
        Long activeBorrows = countCurrentBorrowsByMember(memberId);
        return activeBorrows < 5;
    }
    
    private void validateMember(Member member) {
        if (member.getName() == null || member.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Member name is required");
        }
        
        if (member.getName().length() > 100) {
            throw new IllegalArgumentException("Name must not exceed 100 characters");
        }
        
        if (member.getEmail() == null || member.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        
        if (member.getEmail().length() > 100) {
            throw new IllegalArgumentException("Email must not exceed 100 characters");
        }
        
        if (!EMAIL_PATTERN.matcher(member.getEmail()).matches()) {
            throw new IllegalArgumentException("Email format is invalid");
        }
        
        // Check for duplicate email (excluding current member if updating)
        Optional<Member> existingMember = memberRepository.findByEmail(member.getEmail());
        if (existingMember.isPresent() && !existingMember.get().getId().equals(member.getId())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        if (member.getPhone() != null && member.getPhone().length() > 20) {
            throw new IllegalArgumentException("Phone must not exceed 20 characters");
        }
        
        if (member.getAddress() != null && member.getAddress().length() > 200) {
            throw new IllegalArgumentException("Address must not exceed 200 characters");
        }
    }
} 