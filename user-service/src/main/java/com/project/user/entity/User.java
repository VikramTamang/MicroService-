package com.project.user.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 30)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role = Role.ROLE_CUSTOMER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private UserStatus status = UserStatus.ACTIVE;

    private String address;
    private String city;
    private String postalCode;
    private String avatarUrl;

    private boolean enabled = true;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SellerProfile sellerProfile;

    private Integer failedLoginAttempts = 0;

    private LocalDateTime lockoutUntil;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public User() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public SellerProfile getSellerProfile() { return sellerProfile; }
    public void setSellerProfile(SellerProfile sellerProfile) { this.sellerProfile = sellerProfile; }

    public Integer getFailedLoginAttempts() { return failedLoginAttempts != null ? failedLoginAttempts : 0; }
    public void setFailedLoginAttempts(Integer failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }

    public LocalDateTime getLockoutUntil() { return lockoutUntil; }
    public void setLockoutUntil(LocalDateTime lockoutUntil) { this.lockoutUntil = lockoutUntil; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final User user = new User();
        public Builder id(Long id) { user.setId(id); return this; }
        public Builder firstName(String firstName) { user.setFirstName(firstName); return this; }
        public Builder lastName(String lastName) { user.setLastName(lastName); return this; }
        public Builder email(String email) { user.setEmail(email); return this; }
        public Builder password(String password) { user.setPassword(password); return this; }
        public Builder phoneNumber(String phoneNumber) { user.setPhoneNumber(phoneNumber); return this; }
        public Builder role(Role role) { user.setRole(role); return this; }
        public Builder status(UserStatus status) { user.setStatus(status); return this; }
        public Builder address(String address) { user.setAddress(address); return this; }
        public Builder city(String city) { user.setCity(city); return this; }
        public Builder postalCode(String postalCode) { user.setPostalCode(postalCode); return this; }
        public Builder avatarUrl(String avatarUrl) { user.setAvatarUrl(avatarUrl); return this; }
        public Builder enabled(boolean enabled) { user.setEnabled(enabled); return this; }
        public Builder sellerProfile(SellerProfile sellerProfile) { user.setSellerProfile(sellerProfile); return this; }
        public Builder failedLoginAttempts(Integer failedLoginAttempts) { user.setFailedLoginAttempts(failedLoginAttempts); return this; }
        public Builder lockoutUntil(LocalDateTime lockoutUntil) { user.setLockoutUntil(lockoutUntil); return this; }
        public User build() { return user; }
    }
}
