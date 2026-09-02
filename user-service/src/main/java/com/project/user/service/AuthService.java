package com.project.user.service;

import com.project.user.config.JwtService;
import com.project.user.dto.AuthResponse;
import com.project.user.dto.LoginRequest;
import com.project.user.dto.RegisterRequest;
import com.project.user.dto.UserDto;
import com.project.user.entity.Role;
import com.project.user.entity.User;
import com.project.user.exception.BadRequestException;
import com.project.user.exception.UnauthorizedException;
import com.project.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Processing user registration for email: {}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }

        Role assignedRole = Role.ROLE_CUSTOMER;
        if (request.getRole() != null && (request.getRole().equalsIgnoreCase("ROLE_SELLER") || request.getRole().equalsIgnoreCase("SELLER"))) {
            assignedRole = Role.ROLE_SELLER;
        }

        User user = User.builder()
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .role(assignedRole)
                .enabled(true)
                .build();

        if (assignedRole == Role.ROLE_SELLER) {
            String storeName = (request.getStoreName() != null && !request.getStoreName().isBlank())
                    ? request.getStoreName().trim()
                    : request.getFirstName().trim() + "'s Store";
            String slug = SellerProfileService.generateSlug(storeName) + "-" + System.currentTimeMillis();

            com.project.user.entity.SellerProfile profile = com.project.user.entity.SellerProfile.builder()
                    .user(user)
                    .storeName(storeName)
                    .storeSlug(slug)
                    .verificationStatus(com.project.user.entity.SellerVerificationStatus.PENDING)
                    .build();
            user.setSellerProfile(profile);
        }

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}, role: {}", savedUser.getId(), savedUser.getRole());

        String token = jwtService.generateToken(savedUser);
        return AuthResponse.builder()
                .token(token)
                .expiresIn(jwtService.getExpirationMs())
                .user(userService.mapToDto(savedUser))
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Processing login request for email: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (!user.isEnabled()) {
            throw new UnauthorizedException("Account is disabled. Please contact support.");
        }

        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .expiresIn(jwtService.getExpirationMs())
                .user(userService.mapToDto(user))
                .build();
    }
}
