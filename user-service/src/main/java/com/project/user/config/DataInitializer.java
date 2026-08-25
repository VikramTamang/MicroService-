package com.project.user.config;

import com.project.user.entity.Role;
import com.project.user.entity.User;
import com.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Ensure Admin user exists with correct BCrypt encoded password
        userRepository.findByEmail("admin@example.com").ifPresentOrElse(
                admin -> {
                    admin.setPassword(passwordEncoder.encode("Password@123"));
                    admin.setRole(Role.ROLE_ADMIN);
                    admin.setEnabled(true);
                    userRepository.save(admin);
                    log.info("Verified and updated Admin account password for admin@example.com");
                },
                () -> {
                    User admin = User.builder()
                            .firstName("System")
                            .lastName("Admin")
                            .email("admin@example.com")
                            .password(passwordEncoder.encode("Password@123"))
                            .role(Role.ROLE_ADMIN)
                            .phoneNumber("+1234567890")
                            .address("100 Admin Plaza")
                            .city("Tech City")
                            .postalCode("10001")
                            .enabled(true)
                            .build();
                    userRepository.save(admin);
                    log.info("Created default Admin account: admin@example.com");
                }
        );

        // Ensure Customer user exists
        userRepository.findByEmail("customer@example.com").ifPresentOrElse(
                customer -> {
                    customer.setPassword(passwordEncoder.encode("Password@123"));
                    customer.setRole(Role.ROLE_CUSTOMER);
                    customer.setEnabled(true);
                    userRepository.save(customer);
                    log.info("Verified and updated Customer account password for customer@example.com");
                },
                () -> {
                    User customer = User.builder()
                            .firstName("John")
                            .lastName("Customer")
                            .email("customer@example.com")
                            .password(passwordEncoder.encode("Password@123"))
                            .role(Role.ROLE_CUSTOMER)
                            .phoneNumber("+1987654321")
                            .address("456 Market St")
                            .city("Commerce City")
                            .postalCode("20002")
                            .enabled(true)
                            .build();
                    userRepository.save(customer);
                    log.info("Created default Customer account: customer@example.com");
                }
        );
    }
}
