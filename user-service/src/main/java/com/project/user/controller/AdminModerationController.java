package com.project.user.controller;

import com.project.user.dto.*;
import com.project.user.service.SellerProfileService;
import com.project.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin Moderation", description = "Endpoints for moderating users, sellers, and customer accounts")
public class AdminModerationController {

    private final SellerProfileService sellerProfileService;
    private final UserService userService;

    public AdminModerationController(SellerProfileService sellerProfileService, UserService userService) {
        this.sellerProfileService = sellerProfileService;
        this.userService = userService;
    }

    @GetMapping("/sellers")
    @Operation(summary = "Get all registered sellers (Admin)")
    public ResponseEntity<ApiResponse<List<SellerProfileDto>>> getAllSellers() {
        List<SellerProfileDto> sellers = sellerProfileService.getAllSellers();
        return ResponseEntity.ok(ApiResponse.success(sellers, "Sellers retrieved successfully"));
    }

    @GetMapping("/sellers/pending")
    @Operation(summary = "Get sellers awaiting verification (Admin)")
    public ResponseEntity<ApiResponse<List<SellerProfileDto>>> getPendingSellers() {
        List<SellerProfileDto> pending = sellerProfileService.getPendingSellers();
        return ResponseEntity.ok(ApiResponse.success(pending, "Pending sellers retrieved successfully"));
    }

    @PostMapping("/sellers/{id}/approve")
    @Operation(summary = "Approve a seller onboarding application (Admin)")
    public ResponseEntity<ApiResponse<SellerProfileDto>> approveSeller(@PathVariable("id") Long id) {
        SellerProfileDto profile = sellerProfileService.approveSeller(id);
        return ResponseEntity.ok(ApiResponse.success(profile, "Seller approved successfully"));
    }

    @PostMapping("/sellers/{id}/reject")
    @Operation(summary = "Reject a seller application with reason (Admin)")
    public ResponseEntity<ApiResponse<SellerProfileDto>> rejectSeller(
            @PathVariable("id") Long id,
            @RequestBody(required = false) SellerModerationRequest request
    ) {
        SellerProfileDto profile = sellerProfileService.rejectSeller(id, request);
        return ResponseEntity.ok(ApiResponse.success(profile, "Seller rejected successfully"));
    }

    @PostMapping("/sellers/{id}/suspend")
    @Operation(summary = "Suspend an active seller store (Admin)")
    public ResponseEntity<ApiResponse<SellerProfileDto>> suspendSeller(
            @PathVariable("id") Long id,
            @RequestBody(required = false) SellerModerationRequest request
    ) {
        SellerProfileDto profile = sellerProfileService.suspendSeller(id, request);
        return ResponseEntity.ok(ApiResponse.success(profile, "Seller suspended successfully"));
    }

    @GetMapping("/customers")
    @Operation(summary = "Get all registered marketplace customers (Admin)")
    public ResponseEntity<ApiResponse<List<UserDto>>> getCustomers() {
        List<UserDto> customers = userService.getCustomers();
        return ResponseEntity.ok(ApiResponse.success(customers, "Customers retrieved successfully"));
    }

    @GetMapping("/customers/{id}")
    @Operation(summary = "Get customer details by ID (Admin)")
    public ResponseEntity<ApiResponse<UserDto>> getCustomerById(@PathVariable("id") Long id) {
        UserDto customer = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(customer, "Customer retrieved successfully"));
    }

    @PostMapping("/customers")
    @Operation(summary = "Create a new customer account directly from admin console")
    public ResponseEntity<ApiResponse<UserDto>> createCustomer(@Valid @RequestBody CreateCustomerAdminRequest request) {
        UserDto created = userService.createCustomer(request);
        return ResponseEntity.ok(ApiResponse.success(created, "Customer created successfully"));
    }

    @PutMapping("/customers/{id}")
    @Operation(summary = "Update customer contact profile by ID (Admin)")
    public ResponseEntity<ApiResponse<UserDto>> updateCustomer(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        UserDto updated = userService.updateCustomerById(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Customer updated successfully"));
    }

    @PatchMapping("/users/{id}/status")
    @Operation(summary = "Suspend or reactivate a user account (Admin)")
    public ResponseEntity<ApiResponse<UserDto>> updateUserStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserStatusUpdateRequest request
    ) {
        UserDto userDto = userService.updateUserStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(userDto, "User status updated successfully"));
    }

    @PostMapping("/users/{id}/reset-password")
    @Operation(summary = "Reset a user password directly (Admin)")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable("id") Long id,
            @Valid @RequestBody ResetPasswordAdminRequest request
    ) {
        userService.resetPasswordAdmin(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset successfully"));
    }

    @DeleteMapping("/customers/{id}")
    @Operation(summary = "Delete a customer account (Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable("id") Long id) {
        userService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Customer account deleted successfully"));
    }
}
