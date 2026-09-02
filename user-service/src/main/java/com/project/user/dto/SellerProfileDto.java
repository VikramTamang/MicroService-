package com.project.user.dto;

import com.project.user.entity.SellerVerificationStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SellerProfileDto {
    private Long id;
    private Long userId;
    private String storeName;
    private String storeSlug;
    private String businessRegistrationNumber;
    private String taxIdentificationNumber;
    private String storeDescription;
    private String logoUrl;
    private String bannerUrl;
    private SellerVerificationStatus verificationStatus;
    private String rejectionReason;
    private String suspensionReason;
    private BigDecimal commissionRate;
    private BigDecimal ratingAvg;
    private Integer ratingCount;
    private LocalDateTime createdAt;

    public SellerProfileDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

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

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final SellerProfileDto dto = new SellerProfileDto();
        public Builder id(Long id) { dto.setId(id); return this; }
        public Builder userId(Long userId) { dto.setUserId(userId); return this; }
        public Builder storeName(String storeName) { dto.setStoreName(storeName); return this; }
        public Builder storeSlug(String storeSlug) { dto.setStoreSlug(storeSlug); return this; }
        public Builder businessRegistrationNumber(String businessRegistrationNumber) { dto.setBusinessRegistrationNumber(businessRegistrationNumber); return this; }
        public Builder taxIdentificationNumber(String taxIdentificationNumber) { dto.setTaxIdentificationNumber(taxIdentificationNumber); return this; }
        public Builder storeDescription(String storeDescription) { dto.setStoreDescription(storeDescription); return this; }
        public Builder logoUrl(String logoUrl) { dto.setLogoUrl(logoUrl); return this; }
        public Builder bannerUrl(String bannerUrl) { dto.setBannerUrl(bannerUrl); return this; }
        public Builder verificationStatus(SellerVerificationStatus verificationStatus) { dto.setVerificationStatus(verificationStatus); return this; }
        public Builder rejectionReason(String rejectionReason) { dto.setRejectionReason(rejectionReason); return this; }
        public Builder suspensionReason(String suspensionReason) { dto.setSuspensionReason(suspensionReason); return this; }
        public Builder commissionRate(BigDecimal commissionRate) { dto.setCommissionRate(commissionRate); return this; }
        public Builder ratingAvg(BigDecimal ratingAvg) { dto.setRatingAvg(ratingAvg); return this; }
        public Builder ratingCount(Integer ratingCount) { dto.setRatingCount(ratingCount); return this; }
        public Builder createdAt(LocalDateTime createdAt) { dto.setCreatedAt(createdAt); return this; }
        public SellerProfileDto build() { return dto; }
    }
}
