package com.project.user.service;

import com.project.user.dto.SellerModerationRequest;
import com.project.user.dto.SellerOnboardingRequest;
import com.project.user.dto.SellerProfileDto;
import com.project.user.entity.Role;
import com.project.user.entity.SellerProfile;
import com.project.user.entity.SellerVerificationStatus;
import com.project.user.entity.User;
import com.project.user.exception.BadRequestException;
import com.project.user.exception.ResourceNotFoundException;
import com.project.user.repository.SellerProfileRepository;
import com.project.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SellerProfileService {

    private static final Logger log = LoggerFactory.getLogger(SellerProfileService.class);

    private final SellerProfileRepository sellerProfileRepository;
    private final UserRepository userRepository;

    public SellerProfileService(SellerProfileRepository sellerProfileRepository, UserRepository userRepository) {
        this.sellerProfileRepository = sellerProfileRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SellerProfileDto submitOnboarding(Long userId, SellerOnboardingRequest request) {
        log.info("Processing seller onboarding for userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (sellerProfileRepository.findByUserId(userId).isPresent()) {
            throw new BadRequestException("A seller profile already exists for this account");
        }

        String slug = generateSlug(request.getStoreName());
        if (sellerProfileRepository.existsByStoreSlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        SellerProfile profile = SellerProfile.builder()
                .user(user)
                .storeName(request.getStoreName().trim())
                .storeSlug(slug)
                .businessRegistrationNumber(request.getBusinessRegistrationNumber())
                .taxIdentificationNumber(request.getTaxIdentificationNumber())
                .storeDescription(request.getStoreDescription())
                .logoUrl(request.getLogoUrl())
                .bannerUrl(request.getBannerUrl())
                .verificationStatus(SellerVerificationStatus.PENDING)
                .build();

        // Update user role to ROLE_SELLER if not already
        user.setRole(Role.ROLE_SELLER);
        userRepository.save(user);

        SellerProfile saved = sellerProfileRepository.save(profile);
        log.info("Seller profile submitted: id={}, storeName={}", saved.getId(), saved.getStoreName());
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public SellerProfileDto getProfileByUserId(Long userId) {
        return sellerProfileRepository.findByUserId(userId)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found for user: " + userId));
    }

    @Transactional
    public SellerProfileDto updateSellerProfile(Long userId, com.project.user.dto.UpdateSellerProfileRequest request) {
        log.info("Updating seller profile for userId: {}", userId);
        SellerProfile profile = sellerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found for user: " + userId));

        if (!profile.getStoreName().equalsIgnoreCase(request.getStoreName().trim())) {
            String newSlug = generateSlug(request.getStoreName());
            if (sellerProfileRepository.existsByStoreSlug(newSlug) && !newSlug.equals(profile.getStoreSlug())) {
                newSlug = newSlug + "-" + System.currentTimeMillis();
            }
            profile.setStoreName(request.getStoreName().trim());
            profile.setStoreSlug(newSlug);
        }

        profile.setStoreDescription(request.getStoreDescription());
        profile.setLogoUrl(request.getLogoUrl());
        profile.setBannerUrl(request.getBannerUrl());
        profile.setBusinessRegistrationNumber(request.getBusinessRegistrationNumber());
        profile.setTaxIdentificationNumber(request.getTaxIdentificationNumber());

        SellerProfile updated = sellerProfileRepository.save(profile);
        log.info("Seller profile updated for userId={}: storeName={}", userId, updated.getStoreName());
        return mapToDto(updated);
    }

    @Transactional(readOnly = true)
    public SellerProfileDto getProfileById(Long id) {
        return sellerProfileRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<SellerProfileDto> getPendingSellers() {
        return sellerProfileRepository.findByVerificationStatus(SellerVerificationStatus.PENDING).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SellerProfileDto> getAllSellers() {
        return sellerProfileRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional
    public SellerProfileDto approveSeller(Long profileId) {
        SellerProfile profile = sellerProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found with id: " + profileId));

        profile.setVerificationStatus(SellerVerificationStatus.APPROVED);
        profile.setRejectionReason(null);
        SellerProfile updated = sellerProfileRepository.save(profile);
        log.info("Approved seller profile id={}", profileId);
        return mapToDto(updated);
    }

    @Transactional
    public SellerProfileDto rejectSeller(Long profileId, SellerModerationRequest request) {
        SellerProfile profile = sellerProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found with id: " + profileId));

        String reason = (request != null && request.getReason() != null) ? request.getReason() : "Requirements not met";
        profile.setVerificationStatus(SellerVerificationStatus.REJECTED);
        profile.setRejectionReason(reason);
        SellerProfile updated = sellerProfileRepository.save(profile);
        log.info("Rejected seller profile id={}, reason={}", profileId, reason);
        return mapToDto(updated);
    }

    @Transactional
    public SellerProfileDto suspendSeller(Long profileId, SellerModerationRequest request) {
        SellerProfile profile = sellerProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found with id: " + profileId));

        String reason = (request != null && request.getReason() != null) ? request.getReason() : "Policy violation";
        profile.setVerificationStatus(SellerVerificationStatus.SUSPENDED);
        profile.setSuspensionReason(reason);
        SellerProfile updated = sellerProfileRepository.save(profile);
        log.info("Suspended seller profile id={}, reason={}", profileId, reason);
        return mapToDto(updated);
    }

    public static String generateSlug(String input) {
        if (input == null) return "";
        return input.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    public SellerProfileDto mapToDto(SellerProfile profile) {
        if (profile == null) return null;
        return SellerProfileDto.builder()
                .id(profile.getId())
                .userId(profile.getUser() != null ? profile.getUser().getId() : null)
                .storeName(profile.getStoreName())
                .storeSlug(profile.getStoreSlug())
                .businessRegistrationNumber(profile.getBusinessRegistrationNumber())
                .taxIdentificationNumber(profile.getTaxIdentificationNumber())
                .storeDescription(profile.getStoreDescription())
                .logoUrl(profile.getLogoUrl())
                .bannerUrl(profile.getBannerUrl())
                .verificationStatus(profile.getVerificationStatus())
                .rejectionReason(profile.getRejectionReason())
                .suspensionReason(profile.getSuspensionReason())
                .commissionRate(profile.getCommissionRate())
                .ratingAvg(profile.getRatingAvg())
                .ratingCount(profile.getRatingCount())
                .createdAt(profile.getCreatedAt())
                .build();
    }
}
