package com.project.user.dto;

import com.project.user.entity.Role;
import com.project.user.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private boolean enabled;
    private SellerProfileDto sellerProfile;
    private LocalDateTime createdAt;
}
