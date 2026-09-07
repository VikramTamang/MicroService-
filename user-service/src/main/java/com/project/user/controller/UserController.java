package com.project.user.controller;

import com.project.user.dto.ApiResponse;
import com.project.user.dto.UpdateProfileRequest;
import com.project.user.dto.UserDto;
import com.project.user.service.FileStorageService;
import com.project.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Endpoints for user profile and administrative user queries")
public class UserController {

    private final UserService userService;
    private final FileStorageService fileStorageService;

    public UserController(UserService userService, FileStorageService fileStorageService) {
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

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

    @PutMapping("/me/password")
    @Operation(summary = "Change current user password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestHeader(value = "X-User-Email", required = false) String headerEmail,
            Authentication authentication,
            @Valid @RequestBody com.project.user.dto.ChangePasswordRequest request
    ) {
        String email = (headerEmail != null && !headerEmail.isBlank()) ? headerEmail
                : (authentication != null ? authentication.getName() : null);

        if (email == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure("Unauthenticated user"));
        }

        userService.changePassword(email, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }

    @PostMapping(value = "/upload-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload and update profile picture avatar")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadAvatar(
            @RequestHeader(value = "X-User-Email", required = false) String headerEmail,
            Authentication authentication,
            @RequestParam("file") MultipartFile file
    ) {
        String email = (headerEmail != null && !headerEmail.isBlank()) ? headerEmail
                : (authentication != null ? authentication.getName() : null);

        if (email == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure("Unauthenticated user"));
        }

        String avatarUrl = fileStorageService.storeAvatar(file);
        userService.updateAvatar(email, avatarUrl);
        return ResponseEntity.ok(ApiResponse.success(Map.of("avatarUrl", avatarUrl), "Profile picture uploaded successfully"));
    }

    @GetMapping("/avatars/{filename:.+}")
    @Operation(summary = "Serve user profile picture avatar image")
    public ResponseEntity<Resource> serveAvatar(@PathVariable("filename") String filename) {
        Resource resource = fileStorageService.loadAvatarAsResource(filename);
        String contentType = fileStorageService.getContentType(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID (Internal / Admin)")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable("id") Long id) {
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
