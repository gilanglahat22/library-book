package com.library.main_api.controller;

import com.library.main_api.service.ApiIntegratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/members")
@CrossOrigin
@Tag(name = "Members", description = "Member management endpoints")
public class MemberController {

    private final ApiIntegratorService apiService;

    @Autowired
    public MemberController(ApiIntegratorService apiService) {
        this.apiService = apiService;
    }

    @GetMapping
    @Operation(summary = "Get all members with pagination and filtering")
    public ResponseEntity<Object> getAllMembers(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", size);
        params.put("sortBy", sortBy);
        params.put("sortDir", sortDir);
        
        if (search != null) params.put("search", search);
        if (status != null) params.put("status", status);
        
        return apiService.get("/members", Object.class, params);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all members without pagination")
    public ResponseEntity<Object> getAllMembersWithoutPagination() {
        return apiService.get("/members/all", Object.class);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get member by ID")
    public ResponseEntity<Object> getMemberById(@PathVariable Long id) {
        return apiService.get("/members/" + id, Object.class);
    }

    @GetMapping("/{id}/with-borrowed-books")
    @Operation(summary = "Get member with their borrowed books")
    public ResponseEntity<Object> getMemberWithBorrowedBooks(@PathVariable Long id) {
        return apiService.get("/members/" + id + "/with-borrowed-books", Object.class);
    }

    @GetMapping("/by-email/{email}")
    @Operation(summary = "Get member by email")
    public ResponseEntity<Object> getMemberByEmail(@PathVariable String email) {
        return apiService.get("/members/by-email/" + email, Object.class);
    }

    @GetMapping("/with-overdue-books")
    @Operation(summary = "Get members with overdue books")
    public ResponseEntity<Object> getMembersWithOverdueBooks() {
        return apiService.get("/members/with-overdue-books", Object.class);
    }

    @GetMapping("/active-no-borrows")
    @Operation(summary = "Get active members with no borrows")
    public ResponseEntity<Object> getActiveMembersWithNoBorrows(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", size);
        params.put("sortBy", sortBy);
        params.put("sortDir", sortDir);
        
        return apiService.get("/members/active-no-borrows", Object.class, params);
    }

    @PostMapping
    @Operation(summary = "Create a new member")
    public ResponseEntity<Object> createMember(@RequestBody Object member) {
        return apiService.post("/members", member, Object.class);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a member")
    public ResponseEntity<Object> updateMember(@PathVariable Long id, @RequestBody Object member) {
        return apiService.put("/members/" + id, member, Object.class);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a member")
    public ResponseEntity<Object> deleteMember(@PathVariable Long id) {
        return apiService.delete("/members/" + id, Object.class);
    }

    @PatchMapping("/{id}/suspend")
    @Operation(summary = "Suspend a member")
    public ResponseEntity<Object> suspendMember(@PathVariable Long id) {
        return apiService.put("/members/" + id + "/suspend", null, Object.class);
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate a member")
    public ResponseEntity<Object> activateMember(@PathVariable Long id) {
        return apiService.put("/members/" + id + "/activate", null, Object.class);
    }

    @GetMapping("/{id}/can-borrow")
    @Operation(summary = "Check if member can borrow")
    public ResponseEntity<Object> canMemberBorrow(@PathVariable Long id) {
        return apiService.get("/members/" + id + "/can-borrow", Object.class);
    }

    @GetMapping("/{id}/current-borrows-count")
    @Operation(summary = "Get current borrows count")
    public ResponseEntity<Object> getCurrentBorrowsCount(@PathVariable Long id) {
        return apiService.get("/members/" + id + "/current-borrows-count", Object.class);
    }

    @GetMapping("/{id}/total-borrows-count")
    @Operation(summary = "Get total borrows count")
    public ResponseEntity<Object> getTotalBorrowsCount(@PathVariable Long id) {
        return apiService.get("/members/" + id + "/total-borrows-count", Object.class);
    }

    @GetMapping("/search")
    @Operation(summary = "Search members")
    public ResponseEntity<Object> searchMembers(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("query", query);
        params.put("page", page);
        params.put("size", size);
        params.put("sortBy", sortBy);
        params.put("sortDir", sortDir);
        
        return apiService.get("/members/search", Object.class, params);
    }

    @GetMapping("/by-status/{status}")
    @Operation(summary = "Get members by status")
    public ResponseEntity<Object> getMembersByStatus(
            @PathVariable String status,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", size);
        params.put("sortBy", sortBy);
        params.put("sortDir", sortDir);
        
        return apiService.get("/members/by-status/" + status, Object.class, params);
    }
} 