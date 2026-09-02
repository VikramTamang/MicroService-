package com.project.user.config;

import com.project.user.entity.Role;
import com.project.user.entity.SellerProfile;
import com.project.user.entity.SellerVerificationStatus;
import com.project.user.entity.User;
import com.project.user.repository.SellerProfileRepository;
import com.project.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, SellerProfileRepository sellerProfileRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.sellerProfileRepository = sellerProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Ensure Admin
        userRepository.findByEmail("admin@example.com").ifPresentOrElse(
                admin -> {
                    admin.setPassword(passwordEncoder.encode("Password@123"));
                    admin.setRole(Role.ROLE_ADMIN);
                    admin.setEnabled(true);
                    userRepository.save(admin);
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

        // 2. Ensure Customer
        userRepository.findByEmail("customer@example.com").ifPresentOrElse(
                customer -> {
                    customer.setPassword(passwordEncoder.encode("Password@123"));
                    customer.setRole(Role.ROLE_CUSTOMER);
                    customer.setEnabled(true);
                    userRepository.save(customer);
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

        // 3. Ensure Seller 1 (Apex TechStore)
        userRepository.findByEmail("seller1@example.com").ifPresentOrElse(
                s1 -> {
                    s1.setPassword(passwordEncoder.encode("Password@123"));
                    s1.setRole(Role.ROLE_SELLER);
                    s1.setEnabled(true);
                    userRepository.save(s1);
                    ensureSellerProfile(s1, "Apex Electronics Store", "apex-electronics-store", "REG-US-99210", "TAX-881290",
                            "Official premium consumer electronics, wireless audio, and gaming peripherals.",
                            "https://images.unsplash.com/photo-1550009158-9ebf69173e03?auto=format&fit=crop&w=300&q=80",
                            SellerVerificationStatus.APPROVED);
                },
                () -> {
                    User s1 = User.builder()
                            .firstName("Alex")
                            .lastName("TechSeller")
                            .email("seller1@example.com")
                            .password(passwordEncoder.encode("Password@123"))
                            .role(Role.ROLE_SELLER)
                            .phoneNumber("+14155552671")
                            .address("101 Silicon Way")
                            .city("San Francisco")
                            .postalCode("94107")
                            .enabled(true)
                            .build();
                    User saved = userRepository.save(s1);
                    ensureSellerProfile(saved, "Apex Electronics Store", "apex-electronics-store", "REG-US-99210", "TAX-881290",
                            "Official premium consumer electronics, wireless audio, and gaming peripherals.",
                            "https://images.unsplash.com/photo-1550009158-9ebf69173e03?auto=format&fit=crop&w=300&q=80",
                            SellerVerificationStatus.APPROVED);
                    log.info("Created default Seller 1 account: seller1@example.com");
                }
        );

        // 4. Ensure Seller 2 (Nordic Home)
        userRepository.findByEmail("seller2@example.com").ifPresentOrElse(
                s2 -> {
                    s2.setPassword(passwordEncoder.encode("Password@123"));
                    s2.setRole(Role.ROLE_SELLER);
                    s2.setEnabled(true);
                    userRepository.save(s2);
                    ensureSellerProfile(s2, "Nordic Home & Living", "nordic-home-and-living", "REG-US-77182", "TAX-441902",
                            "Minimalist, smart home ambient lighting, acoustic aesthetics, and modern desk gear.",
                            "https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=300&q=80",
                            SellerVerificationStatus.APPROVED);
                },
                () -> {
                    User s2 = User.builder()
                            .firstName("Emma")
                            .lastName("HomeSeller")
                            .email("seller2@example.com")
                            .password(passwordEncoder.encode("Password@123"))
                            .role(Role.ROLE_SELLER)
                            .phoneNumber("+12125553892")
                            .address("202 Design Blvd")
                            .city("New York")
                            .postalCode("10001")
                            .enabled(true)
                            .build();
                    User saved = userRepository.save(s2);
                    ensureSellerProfile(saved, "Nordic Home & Living", "nordic-home-and-living", "REG-US-77182", "TAX-441902",
                            "Minimalist, smart home ambient lighting, acoustic aesthetics, and modern desk gear.",
                            "https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=300&q=80",
                            SellerVerificationStatus.APPROVED);
                    log.info("Created default Seller 2 account: seller2@example.com");
                }
        );
    }

    private void ensureSellerProfile(User user, String storeName, String storeSlug, String regNo, String taxId, String desc, String logoUrl, SellerVerificationStatus status) {
        if (sellerProfileRepository.findByUserId(user.getId()).isEmpty()) {
            SellerProfile profile = SellerProfile.builder()
                    .user(user)
                    .storeName(storeName)
                    .storeSlug(storeSlug)
                    .businessRegistrationNumber(regNo)
                    .taxIdentificationNumber(taxId)
                    .storeDescription(desc)
                    .logoUrl(logoUrl)
                    .verificationStatus(status)
                    .commissionRate(new BigDecimal("10.00"))
                    .ratingAvg(new BigDecimal("4.9"))
                    .ratingCount(45)
                    .build();
            sellerProfileRepository.save(profile);
        }
    }
}
