CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(120) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    slug VARCHAR(220) NOT NULL UNIQUE,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    category_id BIGINT,
    image_url VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- Seed Categories
INSERT INTO categories (name, slug, description) VALUES
('Electronics', 'electronics', 'Cutting edge gadgets, audio, and personal computing'),
('Accessories', 'accessories', 'High performance accessories and peripherals'),
('Smart Home', 'smart-home', 'Connected devices and automated home solutions'),
('Wearables', 'wearables', 'Fitness trackers and next-gen smartwatches');

-- Seed Products
INSERT INTO products (name, slug, description, price, stock_quantity, category_id, image_url, active) VALUES
('Pro Sound Wireless ANC Headphones', 'pro-sound-wireless-anc-headphones', 'Studio-quality active noise cancelling over-ear headphones with 40-hour battery life.', 249.99, 45, 1, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=600&q=80', true),
('UltraSlim Mechanical Keyboard', 'ultraslim-mechanical-keyboard', 'Custom linear switches, per-key RGB lighting, and wireless multi-device pairing.', 129.99, 60, 2, 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=600&q=80', true),
('Apex 4K UHD Smart Display 27"', 'apex-4k-uhd-smart-display-27', 'IPS display with 144Hz refresh rate, HDR600, and USB-C single cable connectivity.', 499.99, 20, 1, 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=600&q=80', true),
('Ergonomic Precision Mouse', 'ergonomic-precision-mouse', 'Advanced sensor, infinite scroll wheel, and ergonomic thumb rest for all-day comfort.', 79.99, 100, 2, 'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?auto=format&fit=crop&w=600&q=80', true),
('Smart Ambient LED Lamp', 'smart-ambient-led-lamp', 'Voice controlled, 16 million colors with circadian rhythm sync.', 59.99, 85, 3, 'https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=600&q=80', true),
('Titan Smartwatch Pulse Edition', 'titan-smartwatch-pulse-edition', 'AMOLED sapphire screen, ECG monitoring, GPS, and waterproof up to 50m.', 199.99, 35, 4, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=600&q=80', true);
