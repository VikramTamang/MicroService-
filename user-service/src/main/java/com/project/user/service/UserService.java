package com.project.user.service;

import com.project.user.dto.SellerProfileDto;
import com.project.user.dto.UpdateProfileRequest;
import com.project.user.dto.UserDto;
import com.project.user.dto.UserStatusUpdateRequest;
import com.project.user.entity.SellerProfile;
import com.project.user.entity.User;
import com.project.user.entity.UserStatus;
import com.project.user.exception.ResourceNotFoundException;
import com.project.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        return userRepository.findByRole("ROLE_CUSTOMER").stream()
                .map(this::mapToDto)
                .toList();
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

        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
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
                .enabled(user.isEnabled())
                .sellerProfile(sellerDto)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
