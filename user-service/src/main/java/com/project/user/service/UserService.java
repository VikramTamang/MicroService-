package com.project.user.service;

import com.project.user.dto.ChangePasswordRequest;
import com.project.user.dto.SellerProfileDto;
import com.project.user.dto.UpdateProfileRequest;
import com.project.user.dto.UserDto;
import com.project.user.dto.UserStatusUpdateRequest;
import com.project.user.entity.SellerProfile;
import com.project.user.entity.User;
import com.project.user.entity.UserStatus;
import com.project.user.exception.BadRequestException;
import com.project.user.exception.ResourceNotFoundException;
import com.project.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserDto> getCustomers() {
        return userRepository.findByRole(com.project.user.entity.Role.ROLE_CUSTOMER).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional
    public UserDto createCustomer(com.project.user.dto.CreateCustomerAdminRequest request) {
        log.info("Admin creating customer with email: {}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail().trim())) {
            throw new BadRequestException("User with email " + request.getEmail() + " already exists");
        }

        User customer = User.builder()
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .role(com.project.user.entity.Role.ROLE_CUSTOMER)
                .status(UserStatus.ACTIVE)
                .enabled(true)
                .build();

        User saved = userRepository.save(customer);
        return mapToDto(saved);
    }

    @Transactional
    public UserDto updateCustomerById(Long id, UpdateProfileRequest request) {
        log.info("Admin updating customer id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setPostalCode(request.getPostalCode());
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        User updated = userRepository.save(user);
        return mapToDto(updated);
    }

    @Transactional
    public void resetPasswordAdmin(Long id, com.project.user.dto.ResetPasswordAdminRequest request) {
        log.info("Admin resetting password for user id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setFailedLoginAttempts(0);
        user.setLockoutUntil(null);
        userRepository.save(user);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        log.info("Admin deleting customer id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        if (user.getRole() == com.project.user.entity.Role.ROLE_ADMIN) {
            throw new BadRequestException("Cannot delete administrator account");
        }

        userRepository.delete(user);
    }

    @Transactional
    public UserDto updateProfile(String email, UpdateProfileRequest request) {
        log.info("Updating profile for user email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setPostalCode(request.getPostalCode());
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    @Transactional
    public UserDto updateAvatar(String email, String avatarUrl) {
        log.info("Updating avatar for user email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        user.setAvatarUrl(avatarUrl);
        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        log.info("Changing password for user email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New password and confirm password do not match");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("New password cannot be the same as the current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password successfully changed for user email: {}", email);
    }

    @Transactional
    public UserDto updateUserStatus(Long userId, UserStatusUpdateRequest request) {
        log.info("Updating user id={} status to {}", userId, request.getStatus());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setStatus(request.getStatus());
        user.setEnabled(request.getStatus() == UserStatus.ACTIVE);
        User updated = userRepository.save(user);
        return mapToDto(updated);
    }

    public UserDto mapToDto(User user) {
        SellerProfileDto sellerDto = null;
        if (user.getSellerProfile() != null) {
            SellerProfile sp = user.getSellerProfile();
            sellerDto = SellerProfileDto.builder()
                    .id(sp.getId())
                    .userId(user.getId())
                    .storeName(sp.getStoreName())
                    .storeSlug(sp.getStoreSlug())
                    .businessRegistrationNumber(sp.getBusinessRegistrationNumber())
                    .taxIdentificationNumber(sp.getTaxIdentificationNumber())
                    .storeDescription(sp.getStoreDescription())
                    .logoUrl(sp.getLogoUrl())
                    .bannerUrl(sp.getBannerUrl())
                    .verificationStatus(sp.getVerificationStatus())
                    .rejectionReason(sp.getRejectionReason())
                    .suspensionReason(sp.getSuspensionReason())
                    .commissionRate(sp.getCommissionRate())
                    .ratingAvg(sp.getRatingAvg())
                    .ratingCount(sp.getRatingCount())
                    .createdAt(sp.getCreatedAt())
                    .build();
        }

        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .status(user.getStatus())
                .address(user.getAddress())
                .city(user.getCity())
                .postalCode(user.getPostalCode())
                .avatarUrl(user.getAvatarUrl())
                .enabled(user.isEnabled())
                .sellerProfile(sellerDto)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
