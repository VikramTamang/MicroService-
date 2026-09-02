package com.project.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SellerOnboardingRequest {

    @NotBlank(message = "Store name is required")
    @Size(min = 3, max = 150, message = "Store name must be between 3 and 150 characters")
    private String storeName;

    private String businessRegistrationNumber;
    private String taxIdentificationNumber;
    private String storeDescription;
    private String logoUrl;
    private String bannerUrl;

    public SellerOnboardingRequest() {}

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

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

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final SellerOnboardingRequest req = new SellerOnboardingRequest();
        public Builder storeName(String storeName) { req.setStoreName(storeName); return this; }
        public Builder businessRegistrationNumber(String businessRegistrationNumber) { req.setBusinessRegistrationNumber(businessRegistrationNumber); return this; }
        public Builder taxIdentificationNumber(String taxIdentificationNumber) { req.setTaxIdentificationNumber(taxIdentificationNumber); return this; }
        public Builder storeDescription(String storeDescription) { req.setStoreDescription(storeDescription); return this; }
        public Builder logoUrl(String logoUrl) { req.setLogoUrl(logoUrl); return this; }
        public Builder bannerUrl(String bannerUrl) { req.setBannerUrl(bannerUrl); return this; }
        public SellerOnboardingRequest build() { return req; }
    }
}
