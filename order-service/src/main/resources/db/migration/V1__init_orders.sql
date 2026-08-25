CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tracking_number VARCHAR(64) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    user_email VARCHAR(150) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'CONFIRMED',
    shipping_address VARCHAR(255) NOT NULL,
    shipping_city VARCHAR(100) NOT NULL,
    shipping_postal_code VARCHAR(20) NOT NULL,
    customer_phone VARCHAR(30),
    payment_method VARCHAR(50) DEFAULT 'CREDIT_CARD',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- Seed sample initial order for testing
INSERT INTO orders (tracking_number, user_id, user_email, total_amount, status, shipping_address, shipping_city, shipping_postal_code, customer_phone, payment_method, notes)
VALUES ('ORD-202608-0001', 2, 'customer@example.com', 329.98, 'DELIVERED', '456 Market St', 'Commerce City', '20002', '+1987654321', 'CREDIT_CARD', 'Leave package at front porch');

INSERT INTO order_items (order_id, product_id, product_name, unit_price, quantity, subtotal)
VALUES 
(1, 1, 'Pro Sound Wireless ANC Headphones', 249.99, 1, 249.99),
(1, 4, 'Ergonomic Precision Mouse', 79.99, 1, 79.99);
