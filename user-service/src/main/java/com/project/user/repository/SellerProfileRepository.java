package com.project.user.repository;

import com.project.user.entity.SellerProfile;
import com.project.user.entity.SellerVerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerProfileRepository extends JpaRepository<SellerProfile, Long> {
    Optional<SellerProfile> findByUserId(Long userId);
    Optional<SellerProfile> findByStoreSlug(String storeSlug);
    boolean existsByStoreName(String storeName);
    boolean existsByStoreSlug(String storeSlug);
    List<SellerProfile> findByVerificationStatus(SellerVerificationStatus status);
}
