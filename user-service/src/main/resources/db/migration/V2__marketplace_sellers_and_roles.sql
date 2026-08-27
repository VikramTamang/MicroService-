-- Migration V2: Marketplace Sellers and Roles

CREATE TABLE IF NOT EXISTS seller_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    store_name VARCHAR(150) NOT NULL UNIQUE,
    store_slug VARCHAR(170) NOT NULL UNIQUE,
    business_registration_number VARCHAR(100),
    tax_identification_number VARCHAR(100),
    store_description TEXT,
    logo_url TEXT,
    banner_url TEXT,
    verification_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    rejection_reason TEXT,
    suspension_reason TEXT,
    commission_rate DECIMAL(5, 2) NOT NULL DEFAULT 10.00,
    rating_avg DECIMAL(3, 2) DEFAULT 0.00,
    rating_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_seller_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Seed default Seller accounts (Password: Password@123 -> $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi)
INSERT INTO users (first_name, last_name, email, password, phone_number, role, address, city, postal_code, enabled, status, failed_login_attempts)
VALUES 
('Alex', 'TechSeller', 'seller1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '+14155552671', 'ROLE_SELLER', '101 Silicon Way', 'San Francisco', '94107', true, 'ACTIVE', 0),
('Emma', 'HomeSeller', 'seller2@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '+12125553892', 'ROLE_SELLER', '202 Design Blvd', 'New York', '10001', true, 'ACTIVE', 0);

-- Seed Seller Profiles for the sellers
INSERT INTO seller_profiles (user_id, store_name, store_slug, business_registration_number, tax_identification_number, store_description, logo_url, verification_status, commission_rate, rating_avg, rating_count)
SELECT u.id, 'Apex Electronics Store', 'apex-electronics-store', 'REG-US-99210', 'TAX-881290', 'Official premium consumer electronics, wireless audio, and gaming peripherals.', 'https://images.unsplash.com/photo-1550009158-9ebf69173e03?auto=format&fit=crop&w=300&q=80', 'APPROVED', 8.50, 4.9, 142
FROM users u WHERE u.email = 'seller1@example.com';

INSERT INTO seller_profiles (user_id, store_name, store_slug, business_registration_number, tax_identification_number, store_description, logo_url, verification_status, commission_rate, rating_avg, rating_count)
SELECT u.id, 'Nordic Home & Living', 'nordic-home-and-living', 'REG-US-77182', 'TAX-441902', 'Minimalist, smart home ambient lighting, acoustic aesthetics, and modern desk gear.', 'https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=300&q=80', 'APPROVED', 10.00, 4.8, 89
FROM users u WHERE u.email = 'seller2@example.com';
