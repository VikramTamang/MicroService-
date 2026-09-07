package com.project.user.dto;

import com.project.user.entity.Role;
import com.project.user.entity.UserStatus;
import java.time.LocalDateTime;

public class UserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Role role;
    private UserStatus status;
    private String address;
    private String city;
    private String postalCode;
    private String avatarUrl;
    private boolean enabled;
    private SellerProfileDto sellerProfile;
    private LocalDateTime createdAt;

    public UserDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public SellerProfileDto getSellerProfile() { return sellerProfile; }
    public void setSellerProfile(SellerProfileDto sellerProfile) { this.sellerProfile = sellerProfile; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final UserDto dto = new UserDto();
        public Builder id(Long id) { dto.setId(id); return this; }
        public Builder firstName(String firstName) { dto.setFirstName(firstName); return this; }
        public Builder lastName(String lastName) { dto.setLastName(lastName); return this; }
        public Builder email(String email) { dto.setEmail(email); return this; }
        public Builder phoneNumber(String phoneNumber) { dto.setPhoneNumber(phoneNumber); return this; }
        public Builder role(Role role) { dto.setRole(role); return this; }
        public Builder status(UserStatus status) { dto.setStatus(status); return this; }
        public Builder address(String address) { dto.setAddress(address); return this; }
        public Builder city(String city) { dto.setCity(city); return this; }
        public Builder postalCode(String postalCode) { dto.setPostalCode(postalCode); return this; }
        public Builder avatarUrl(String avatarUrl) { dto.setAvatarUrl(avatarUrl); return this; }
        public Builder enabled(boolean enabled) { dto.setEnabled(enabled); return this; }
        public Builder sellerProfile(SellerProfileDto sellerProfile) { dto.setSellerProfile(sellerProfile); return this; }
        public Builder createdAt(LocalDateTime createdAt) { dto.setCreatedAt(createdAt); return this; }
        public UserDto build() { return dto; }
    }
}
