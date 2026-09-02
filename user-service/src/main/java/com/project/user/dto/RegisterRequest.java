package com.project.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String phoneNumber;
    private String address;
    private String city;
    private String postalCode;
    private String role; // "ROLE_CUSTOMER" (default) or "ROLE_SELLER"
    private String storeName; // If registering as seller

    public RegisterRequest() {}

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final RegisterRequest req = new RegisterRequest();
        public Builder firstName(String firstName) { req.setFirstName(firstName); return this; }
        public Builder lastName(String lastName) { req.setLastName(lastName); return this; }
        public Builder email(String email) { req.setEmail(email); return this; }
        public Builder password(String password) { req.setPassword(password); return this; }
        public Builder phoneNumber(String phoneNumber) { req.setPhoneNumber(phoneNumber); return this; }
        public Builder address(String address) { req.setAddress(address); return this; }
        public Builder city(String city) { req.setCity(city); return this; }
        public Builder postalCode(String postalCode) { req.setPostalCode(postalCode); return this; }
        public Builder role(String role) { req.setRole(role); return this; }
        public Builder storeName(String storeName) { req.setStoreName(storeName); return this; }
        public RegisterRequest build() { return req; }
    }
}
