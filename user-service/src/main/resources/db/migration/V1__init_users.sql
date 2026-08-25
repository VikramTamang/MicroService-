CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(30),
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_CUSTOMER',
    address VARCHAR(255),
    city VARCHAR(100),
    postal_code VARCHAR(20),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Seed default Admin and Customer accounts (Password: Password@123 -> $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi)
INSERT INTO users (first_name, last_name, email, password, phone_number, role, address, city, postal_code, enabled)
VALUES 
('System', 'Admin', 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '+1234567890', 'ROLE_ADMIN', '100 Admin Plaza', 'Tech City', '10001', true),
('John', 'Customer', 'customer@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '+1987654321', 'ROLE_CUSTOMER', '456 Market St', 'Commerce City', '20002', true);
