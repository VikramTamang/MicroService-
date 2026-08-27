package com.project.user.dto;

import com.project.user.entity.SellerVerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
