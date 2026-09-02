package com.project.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "seller_profiles")
public class SellerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    private User user;

    @Column(nullable = false, unique = true, length = 150)
    private String storeName;

    @Column(nullable = false, unique = true, length = 170)
    private String storeSlug;

    @Column(length = 100)
    private String businessRegistrationNumber;

    @Column(length = 100)
    private String taxIdentificationNumber;

    @Column(columnDefinition = "TEXT")
    private String storeDescription;

    @Column(columnDefinition = "TEXT")
    private String logoUrl;

    @Column(columnDefinition = "TEXT")
    private String bannerUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SellerVerificationStatus verificationStatus = SellerVerificationStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(columnDefinition = "TEXT")
    private String suspensionReason;

    @Column(precision = 5, scale = 2)
    private BigDecimal commissionRate = new BigDecimal("10.00");

    @Column(precision = 3, scale = 2)
    private BigDecimal ratingAvg = BigDecimal.ZERO;

    private Integer ratingCount = 0;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public SellerProfile() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getStoreSlug() { return storeSlug; }
    public void setStoreSlug(String storeSlug) { this.storeSlug = storeSlug; }

    public String getBusinessRegistrationNumber() { return businessRegistrationNumber; }
    public void setBusinessRegistrationNumber(String businessRegistrationNumber) { this.businessRegistrationNumber = businessRegistrationNumber; }

    public String getTaxIdentificationNumber() { return taxIdentificationNumber; }
    public void setTaxIdentificationNumber(String taxIdentificationNumber) { this.taxIdentificationNumber = taxIdentificationNumber; }

    public String getStoreDescription() { return storeDescription; }
    public void setStoreDescription(String storeDescription) { this.storeDescription = storeDescription; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getBannerUrl() { return bannerUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }

    public SellerVerificationStatus getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(SellerVerificationStatus verificationStatus) { this.verificationStatus = verificationStatus; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public String getSuspensionReason() { return suspensionReason; }
    public void setSuspensionReason(String suspensionReason) { this.suspensionReason = suspensionReason; }

    public BigDecimal getCommissionRate() { return commissionRate; }
    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }

    public BigDecimal getRatingAvg() { return ratingAvg; }
    public void setRatingAvg(BigDecimal ratingAvg) { this.ratingAvg = ratingAvg; }

    public Integer getRatingCount() { return ratingCount != null ? ratingCount : 0; }
    public void setRatingCount(Integer ratingCount) { this.ratingCount = ratingCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final SellerProfile profile = new SellerProfile();
        public Builder id(Long id) { profile.setId(id); return this; }
        public Builder user(User user) { profile.setUser(user); return this; }
        public Builder storeName(String storeName) { profile.setStoreName(storeName); return this; }
        public Builder storeSlug(String storeSlug) { profile.setStoreSlug(storeSlug); return this; }
        public Builder businessRegistrationNumber(String businessRegistrationNumber) { profile.setBusinessRegistrationNumber(businessRegistrationNumber); return this; }
        public Builder taxIdentificationNumber(String taxIdentificationNumber) { profile.setTaxIdentificationNumber(taxIdentificationNumber); return this; }
        public Builder storeDescription(String storeDescription) { profile.setStoreDescription(storeDescription); return this; }
        public Builder logoUrl(String logoUrl) { profile.setLogoUrl(logoUrl); return this; }
        public Builder bannerUrl(String bannerUrl) { profile.setBannerUrl(bannerUrl); return this; }
        public Builder verificationStatus(SellerVerificationStatus verificationStatus) { profile.setVerificationStatus(verificationStatus); return this; }
        public Builder rejectionReason(String rejectionReason) { profile.setRejectionReason(rejectionReason); return this; }
        public Builder suspensionReason(String suspensionReason) { profile.setSuspensionReason(suspensionReason); return this; }
        public Builder commissionRate(BigDecimal commissionRate) { profile.setCommissionRate(commissionRate); return this; }
        public Builder ratingAvg(BigDecimal ratingAvg) { profile.setRatingAvg(ratingAvg); return this; }
        public Builder ratingCount(Integer ratingCount) { profile.setRatingCount(ratingCount); return this; }
        public SellerProfile build() { return profile; }
    }
}
