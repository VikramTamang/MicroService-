package com.project.user.controller;

import com.project.user.dto.ApiResponse;
import com.project.user.dto.SellerOnboardingRequest;
import com.project.user.dto.SellerProfileDto;
import com.project.user.service.SellerProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sellers")
@Tag(name = "Seller Profile", description = "Endpoints for seller onboarding and store management")
public class SellerController {

    private final SellerProfileService sellerProfileService;

    public SellerController(SellerProfileService sellerProfileService) {
        this.sellerProfileService = sellerProfileService;
    }

    @PostMapping("/onboarding")
    @Operation(summary = "Submit onboarding application to become a verified seller")
    public ResponseEntity<ApiResponse<SellerProfileDto>> submitOnboarding(
            @RequestHeader(value = "X-User-Id", required = false) Long authUserId,
            @Valid @RequestBody SellerOnboardingRequest request
    ) {
        if (authUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("Authentication required"));
        }
        SellerProfileDto profile = sellerProfileService.submitOnboarding(authUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(profile, "Seller onboarding application submitted successfully"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current seller store profile")
    public ResponseEntity<ApiResponse<SellerProfileDto>> getMyProfile(
            @RequestHeader(value = "X-User-Id", required = false) Long authUserId
    ) {
        if (authUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("Authentication required"));
        }
        SellerProfileDto profile = sellerProfileService.getProfileByUserId(authUserId);
        return ResponseEntity.ok(ApiResponse.success(profile, "Seller profile retrieved successfully"));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current seller store profile")
    public ResponseEntity<ApiResponse<SellerProfileDto>> updateMyProfile(
            @RequestHeader(value = "X-User-Id", required = false) Long authUserId,
            @Valid @RequestBody com.project.user.dto.UpdateSellerProfileRequest request
    ) {
        if (authUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("Authentication required"));
        }
        SellerProfileDto profile = sellerProfileService.updateSellerProfile(authUserId, request);
        return ResponseEntity.ok(ApiResponse.success(profile, "Seller store profile updated successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get public seller profile by ID")
    public ResponseEntity<ApiResponse<SellerProfileDto>> getSellerById(@PathVariable("id") Long id) {
        SellerProfileDto profile = sellerProfileService.getProfileById(id);
        return ResponseEntity.ok(ApiResponse.success(profile, "Seller profile retrieved successfully"));
    }
}
