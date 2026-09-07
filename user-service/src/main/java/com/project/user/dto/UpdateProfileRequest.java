package com.project.user.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateProfileRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String phoneNumber;
    private String address;
    private String city;
    private String postalCode;
    private String avatarUrl;

    public UpdateProfileRequest() {}

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final UpdateProfileRequest req = new UpdateProfileRequest();
        public Builder firstName(String firstName) { req.setFirstName(firstName); return this; }
        public Builder lastName(String lastName) { req.setLastName(lastName); return this; }
        public Builder phoneNumber(String phoneNumber) { req.setPhoneNumber(phoneNumber); return this; }
        public Builder address(String address) { req.setAddress(address); return this; }
        public Builder city(String city) { req.setCity(city); return this; }
        public Builder postalCode(String postalCode) { req.setPostalCode(postalCode); return this; }
        public Builder avatarUrl(String avatarUrl) { req.setAvatarUrl(avatarUrl); return this; }
        public UpdateProfileRequest build() { return req; }
    }
}
