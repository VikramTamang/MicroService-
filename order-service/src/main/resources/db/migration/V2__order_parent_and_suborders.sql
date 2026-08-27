-- Migration V2: Parent Orders and Split Sub-Orders per Seller
CREATE TABLE IF NOT EXISTS parent_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(64) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    customer_email VARCHAR(150) NOT NULL,
    total_amount DECIMAL(12, 2) NOT NULL,
    shipping_fee DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    tax_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    payment_method VARCHAR(50) DEFAULT 'CREDIT_CARD',
    payment_status VARCHAR(30) DEFAULT 'PAID',
    derived_status VARCHAR(30) NOT NULL DEFAULT 'PLACED',
    shipping_address TEXT NOT NULL,
    shipping_city VARCHAR(100),
    shipping_postal_code VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sub_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_order_id BIGINT NOT NULL,
    sub_order_number VARCHAR(80) NOT NULL UNIQUE,
    seller_id BIGINT NOT NULL,
    subtotal DECIMAL(12, 2) NOT NULL,
    shipping_fee DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    tax DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    commission_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    status VARCHAR(30) NOT NULL DEFAULT 'PLACED',
    cancellation_reason TEXT,
    return_reason TEXT,
    carrier VARCHAR(100),
    tracking_code VARCHAR(150),
    delivered_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_suborder_parent FOREIGN KEY (parent_order_id) REFERENCES parent_orders(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sub_order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sub_order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    sku VARCHAR(100),
    unit_price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_item_suborder FOREIGN KEY (sub_order_id) REFERENCES sub_orders(id) ON DELETE CASCADE
);
