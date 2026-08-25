package com.project.user.controller;

import com.project.user.dto.ApiResponse;
import com.project.user.dto.UpdateProfileRequest;
import com.project.user.dto.UserDto;
import com.project.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for user profile and administrative user queries")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(
            @RequestHeader(value = "X-User-Email", required = false) String headerEmail,
            Authentication authentication
    ) {
        String email = (headerEmail != null && !headerEmail.isBlank()) ? headerEmail
                : (authentication != null ? authentication.getName() : null);

        if (email == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure("Unauthenticated user"));
        }

        UserDto userDto = userService.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(userDto, "User profile retrieved successfully"));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(
            @RequestHeader(value = "X-User-Email", required = false) String headerEmail,
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        String email = (headerEmail != null && !headerEmail.isBlank()) ? headerEmail
                : (authentication != null ? authentication.getName() : null);

        if (email == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure("Unauthenticated user"));
        }

        UserDto updated = userService.updateProfile(email, request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Profile updated successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID (Internal / Admin)")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        UserDto userDto = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(userDto, "User retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get all registered users (Admin only)")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users, "All users retrieved successfully"));
    }
}
