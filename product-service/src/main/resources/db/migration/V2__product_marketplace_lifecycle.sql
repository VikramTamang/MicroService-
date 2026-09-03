-- Migration V2: Product Marketplace Lifecycle, Multi-Seller & Auditing
ALTER TABLE products ADD COLUMN seller_id BIGINT NOT NULL DEFAULT 1;
ALTER TABLE products ADD COLUMN sku VARCHAR(100);
ALTER TABLE products ADD COLUMN status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE';
ALTER TABLE products ADD COLUMN rejection_reason TEXT;
ALTER TABLE products ADD COLUMN suspension_reason TEXT;
ALTER TABLE products ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

CREATE TABLE IF NOT EXISTS product_audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    actor_id BIGINT NOT NULL,
    actor_role VARCHAR(30) NOT NULL,
    action VARCHAR(50) NOT NULL,
    previous_status VARCHAR(30),
    new_status VARCHAR(30),
    reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Seed Seller 1 Products (TechStore) and Seller 2 Products (Nordic Home)
UPDATE products SET seller_id = 1, sku = 'TECH-WL-01', status = 'ACTIVE' WHERE id = 1;
UPDATE products SET seller_id = 1, sku = 'TECH-KB-02', status = 'ACTIVE' WHERE id = 2;
UPDATE products SET seller_id = 1, sku = 'TECH-HUB-03', status = 'ACTIVE' WHERE id = 3;
UPDATE products SET seller_id = 2, sku = 'HOME-LMP-04', status = 'ACTIVE' WHERE id = 4;
UPDATE products SET seller_id = 2, sku = 'HOME-BOT-05', status = 'ACTIVE' WHERE id = 5;
UPDATE products SET seller_id = 2, sku = 'HOME-PLT-06', status = 'ACTIVE' WHERE id = 6;
