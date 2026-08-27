package com.project.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerOnboardingRequest {

    @NotBlank(message = "Store name is required")
    @Size(min = 3, max = 150, message = "Store name must be between 3 and 150 characters")
    private String storeName;

    private String businessRegistrationNumber;
    private String taxIdentificationNumber;
    private String storeDescription;
    private String logoUrl;
    private String bannerUrl;
}
