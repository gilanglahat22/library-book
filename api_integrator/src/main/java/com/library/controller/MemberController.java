package com.library.controller;

import com.library.model.Member;
import com.library.model.Member.MembershipStatus;
import com.library.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/members")
@CrossOrigin(origins = "http://localhost:3000")
public class MemberController {
    
    private final MemberService memberService;
    
    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
    
    @GetMapping
    public ResponseEntity<Page<Member>> getAllMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) MembershipStatus status) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Member> members;
        
        if (search != null && !search.trim().isEmpty()) {
            members = memberService.findBySearchTerm(search, pageable);
        } else if (status != null) {
            members = memberService.findByStatus(status, pageable);
        } else {
            members = memberService.findAll(pageable);
        }
        
        return ResponseEntity.ok(members);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Member>> getAllMembersWithoutPagination() {
        List<Member> members = memberService.findAll();
        return ResponseEntity.ok(members);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        Optional<Member> member = memberService.findById(id);
        return member.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/with-borrowed-books")
    public ResponseEntity<Member> getMemberWithBorrowedBooks(@PathVariable Long id) {
        Optional<Member> member = memberService.findByIdWithBorrowedBooks(id);
        return member.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/by-email/{email}")
    public ResponseEntity<Member> getMemberByEmail(@PathVariable String email) {
        Optional<Member> member = memberService.findByEmail(email);
        return member.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/with-overdue-books")
    public ResponseEntity<List<Member>> getMembersWithOverdueBooks() {
        List<Member> members = memberService.findMembersWithOverdueBooks();
        return ResponseEntity.ok(members);
    }
    
    @GetMapping("/active-no-borrows")
    public ResponseEntity<Page<Member>> getActiveMembersWithNoBorrows(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Member> members = memberService.findActiveMembersWithNoBorrows(pageable);
        return ResponseEntity.ok(members);
    }
    
    @PostMapping
    public ResponseEntity<?> createMember(@Valid @RequestBody Member member) {
        try {
            // Check if member with same email already exists
            if (memberService.existsByEmail(member.getEmail())) {
                return ResponseEntity.badRequest()
                        .body("Member with email '" + member.getEmail() + "' already exists");
            }
            
            Member savedMember = memberService.save(member);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMember);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating member: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMember(@PathVariable Long id, @Valid @RequestBody Member memberDetails) {
        try {
            if (!memberService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            Member updatedMember = memberService.update(id, memberDetails);
            return ResponseEntity.ok(updatedMember);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating member: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        try {
            if (!memberService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            memberService.deleteById(id);
            return ResponseEntity.ok().body("Member deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting member: " + e.getMessage());
        }
    }
    
    @PatchMapping("/{id}/suspend")
    public ResponseEntity<?> suspendMember(@PathVariable Long id) {
        try {
            if (!memberService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            Member suspendedMember = memberService.suspendMember(id);
            return ResponseEntity.ok(suspendedMember);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error suspending member: " + e.getMessage());
        }
    }
    
    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activateMember(@PathVariable Long id) {
        try {
            if (!memberService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            Member activatedMember = memberService.activateMember(id);
            return ResponseEntity.ok(activatedMember);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error activating member: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}/can-borrow")
    public ResponseEntity<Boolean> canMemberBorrow(@PathVariable Long id) {
        if (!memberService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        boolean canBorrow = memberService.canBorrow(id);
        return ResponseEntity.ok(canBorrow);
    }
    
    @GetMapping("/{id}/current-borrows-count")
    public ResponseEntity<Long> getCurrentBorrowsCount(@PathVariable Long id) {
        if (!memberService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        Long count = memberService.countCurrentBorrowsByMember(id);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/{id}/total-borrows-count")
    public ResponseEntity<Long> getTotalBorrowsCount(@PathVariable Long id) {
        if (!memberService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        Long count = memberService.countTotalBorrowsByMember(id);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<Member>> searchMembers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Member> members = memberService.findBySearchTerm(query, pageable);
        return ResponseEntity.ok(members);
    }
    
    @GetMapping("/by-status/{status}")
    public ResponseEntity<Page<Member>> getMembersByStatus(
            @PathVariable MembershipStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Member> members = memberService.findByStatus(status, pageable);
        return ResponseEntity.ok(members);
    }
} 