-- Create Database
CREATE DATABASE restaurant_db;
USE restaurant_db;

-- Table: categories (for menu organization)
CREATE TABLE categories
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255) NOT NULL UNIQUE,
    display_order INT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table: menus
CREATE TABLE menus
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    description VARCHAR(500),
    price       DECIMAL(10, 2) NOT NULL,
    image_url   VARCHAR(500),
    available   BOOLEAN        NOT NULL DEFAULT TRUE,
    is_promo    BOOLEAN                 DEFAULT FALSE,
    promo_price DECIMAL(10, 2),
    category_id BIGINT,
    created_by  BIGINT, -- Which cashier created/modified this menu
    created_at  TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE SET NULL
);

-- Table: cashiers (with authentication)
CREATE TABLE cashiers
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL, -- Hashed password
    display_name  VARCHAR(255) NOT NULL,
    role          ENUM ('ADMIN', 'CASHIER') DEFAULT 'CASHIER',
    is_active     BOOLEAN                   DEFAULT TRUE,
    last_login    TIMESTAMP    NULL,
    created_at    TIMESTAMP                 DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP                 DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table: cashier_sessions (for tracking logins)
CREATE TABLE cashier_sessions
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    cashier_id    BIGINT       NOT NULL,
    session_token VARCHAR(255) NOT NULL UNIQUE,
    expires_at    TIMESTAMP    NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cashier_id) REFERENCES cashiers (id) ON DELETE CASCADE
);

-- Table: orders
CREATE TABLE orders
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number   VARCHAR(255) UNIQUE,
    total          DECIMAL(10, 2)                                                                NOT NULL,
    status         ENUM ('PENDING', 'CONFIRMED', 'PREPARING', 'READY', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    order_type     ENUM ('CUSTOMER_SELF', 'CASHIER_ASSISTED')                                    NOT NULL,
    payment_method ENUM ('QR_CODE', 'CASH', 'CREDIT_CARD'),
    payment_status ENUM ('PENDING', 'PAID', 'FAILED', 'REFUNDED')                                         DEFAULT 'PENDING',
    customer_name  VARCHAR(255),
    cashier_id     BIGINT, -- For cashier-assisted orders
    created_at     TIMESTAMP                                                                              DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP                                                                              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cashier_id) REFERENCES cashiers (id) ON DELETE SET NULL
);

-- Table: order_items
CREATE TABLE order_items
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id   BIGINT         NOT NULL,
    menu_id    BIGINT         NOT NULL,
    quantity   INT            NOT NULL,
    price      DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    FOREIGN KEY (menu_id) REFERENCES menus (id) ON DELETE CASCADE
);

-- Table: invoices (for laporan/reports)
CREATE TABLE invoices
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(255) UNIQUE                     NOT NULL,
    order_id       BIGINT                                  NOT NULL,
    cashier_id     BIGINT                                  NOT NULL,
    total_amount   DECIMAL(10, 2)                          NOT NULL,
    tax_amount     DECIMAL(10, 2) DEFAULT 0,
    final_amount   DECIMAL(10, 2)                          NOT NULL,
    payment_method ENUM ('QR_CODE', 'CASH', 'CREDIT_CARD') NOT NULL,
    created_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders (id),
    FOREIGN KEY (cashier_id) REFERENCES cashiers (id)
);

-- Table: menu_audit_log (track menu changes for pengaturan)
CREATE TABLE menu_audit_log
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    menu_id    BIGINT                                                                          NOT NULL,
    cashier_id BIGINT                                                                          NOT NULL,
    action     ENUM ('CREATED', 'UPDATED', 'PRICE_CHANGED', 'AVAILABILITY_CHANGED', 'DELETED') NOT NULL,
    old_values JSON, -- Store old values for audit
    new_values JSON, -- Store new values for audit
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (menu_id) REFERENCES menus (id) ON DELETE CASCADE,
    FOREIGN KEY (cashier_id) REFERENCES cashiers (id)
);

-- Indexes for better performance
CREATE INDEX idx_menus_category_id ON menus (category_id);
CREATE INDEX idx_menus_available ON menus (available);
CREATE INDEX idx_menus_created_by ON menus (created_by);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_orders_payment_status ON orders (payment_status);
CREATE INDEX idx_orders_created_at ON orders (created_at);
CREATE INDEX idx_orders_cashier_id ON orders (cashier_id);
CREATE INDEX idx_order_items_order_id ON order_items (order_id);
CREATE INDEX idx_order_items_menu_id ON order_items (menu_id);
CREATE INDEX idx_invoices_order_id ON invoices (order_id);
CREATE INDEX idx_invoices_cashier_id ON invoices (cashier_id);
CREATE INDEX idx_invoices_created_at ON invoices (created_at);
CREATE INDEX idx_cashier_sessions_token ON cashier_sessions (session_token);
CREATE INDEX idx_cashier_sessions_expires ON cashier_sessions (expires_at);
CREATE INDEX idx_menu_audit_menu_id ON menu_audit_log (menu_id);
CREATE INDEX idx_menu_audit_cashier_id ON menu_audit_log (cashier_id);
CREATE INDEX idx_menu_audit_created_at ON menu_audit_log (created_at);

DROP DATABASE restaurant_db;

SHOW TABLES;

SELECT * FROM categories;

-- Insert sample categories (Based on your menu images)
INSERT INTO categories (name, display_order)
VALUES ('SEMUA', 1),
       ('PROMO', 2),
       ('PAKET KOMBO', 3),
       ('MAKANAN UTAMA', 4),
       ('SOUP', 5),
       ('MINUMAN', 6),
       ('SIDE DISH', 7),
       ('DESSERT', 8),
       ('SEASONAL', 9);

-- Insert sample cashiers with hashed passwords
-- Password for all: "password123" (hashed with bcrypt)
INSERT INTO cashiers (username, password_hash, display_name, role, is_active)
VALUES ('kasir1', '$2a$10$N9qo8uLOickgx2ZMRZoMye.J8I..sJ9HXp6cSYwQD6o6e7F7QY8W2', 'kasir 1', 'CASHIER', TRUE),
       ('kasir2', '$2a$10$N9qo8uLOickgx2ZMRZoMye.J8I..sJ9HXp6cSYwQD6o6e7F7QY8W2', 'kasir 2', 'CASHIER', TRUE),
       ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye.J8I..sJ9HXp6cSYwQD6o6e7F7QY8W2', 'Administrator', 'ADMIN', TRUE);

-- Insert sample menus (with created_by reference)
INSERT INTO menus (name, description, price, category_id, available, is_promo, promo_price, created_by)
VALUES
-- PROMO Items
('Paket Hemat 1', 'Nasi + Ayam Goreng + Es Teh', 25000, 2, TRUE, TRUE, 22000, 3),
('Paket Keluarga', '4x Nasi + 4x Ayam Goreng + 4x Es Teh', 100000, 2, TRUE, TRUE, 85000, 3),
('Promo Akhir Tahun', 'Burger + Kentang + Cola Special', 50000, 2, TRUE, TRUE, 45000, 3),

-- PAKET KOMBO
('Combo A', 'Burger + Kentang + Cola', 45000, 3, TRUE, FALSE, NULL, 3),
('Combo B', '2 Burger + 2 Kentang + 2 Cola', 80000, 3, TRUE, FALSE, NULL, 3),
('Combo Family', '4 Burger + 4 Kentang + 4 Cola', 150000, 3, TRUE, FALSE, NULL, 3),

-- MAKANAN UTAMA
('Nasi Goreng Spesial', 'Nasi goreng dengan ayam dan sayuran', 35000, 4, TRUE, FALSE, NULL, 3),
('Ayam Goreng Crispy', 'Ayam goreng crispy dengan bumbu special', 28000, 4, TRUE, FALSE, NULL, 3),
('Burger Beef', 'Burger dengan daging sapi pilihan', 42000, 4, TRUE, FALSE, NULL, 3),
('Spaghetti Carbonara', 'Pasta dengan saus carbonara creamy', 38000, 4, TRUE, FALSE, NULL, 3),

-- SOUP
('Sop Ayam', 'Sop ayam dengan sayuran segar', 18000, 5, TRUE, FALSE, NULL, 3),
('Sop Iga', 'Sop iga sapi dengan kuah bening', 35000, 5, TRUE, FALSE, NULL, 3),

-- MINUMAN
('Es Teh Manis', 'Es teh manis segar', 8000, 6, TRUE, FALSE, NULL, 3),
('Jus Jeruk', 'Jus jeruk segar', 15000, 6, TRUE, FALSE, NULL, 3),
('Kopi Hitam', 'Kopi hitam aromatik', 12000, 6, TRUE, FALSE, NULL, 3),
('Air Mineral', 'Air mineral botol 600ml', 5000, 6, TRUE, FALSE, NULL, 3),

-- SIDE DISH
('Kentang Goreng', 'Kentang goreng renyah', 15000, 7, TRUE, FALSE, NULL, 3),
('Onion Ring', 'Onion ring crispy', 12000, 7, TRUE, FALSE, NULL, 3),
('Salad Sayur', 'Salad sayuran segar', 10000, 7, TRUE, FALSE, NULL, 3),

-- DESSERT
('Ice Cream Vanilla', 'Ice cream vanilla lembut', 10000, 8, TRUE, FALSE, NULL, 3),
('Pudding Coklat', 'Pudding coklat manis', 8000, 8, TRUE, FALSE, NULL, 3),
('Cheese Cake', 'Cheese cake lembut', 20000, 8, TRUE, FALSE, NULL, 3),

-- SEASONAL
('Menu Musiman Panas', 'Menu khusus musim panas', 30000, 9, FALSE, FALSE, NULL, 3), -- Not available
('Menu Festival', 'Menu khusus festival bulan ini', 35000, 9, TRUE, FALSE, NULL, 3);

-- Insert sample orders (mix of customer self and cashier assisted)
INSERT INTO orders (order_number, total, status, order_type, payment_method, payment_status, customer_name, cashier_id)
VALUES
-- Customer self orders (QR payment)
('ORD-001', 85000, 'COMPLETED', 'CUSTOMER_SELF', 'QR_CODE', 'PAID', 'Budi Santoso', NULL),
('ORD-002', 45000, 'COMPLETED', 'CUSTOMER_SELF', 'QR_CODE', 'PAID', 'Sari Dewi', NULL),

-- Cashier assisted orders (cash payment)
('ORD-003', 22000, 'COMPLETED', 'CASHIER_ASSISTED', 'CASH', 'PAID', 'Customer Walk-in', 1),
('ORD-004', 80000, 'PREPARING', 'CASHIER_ASSISTED', 'CASH', 'PAID', 'Customer Takeaway', 1),
('ORD-005', 150000, 'PENDING', 'CASHIER_ASSISTED', 'QR_CODE', 'PENDING', 'Family Order', 2),

-- Mixed payment methods
('ORD-006', 120000, 'COMPLETED', 'CASHIER_ASSISTED', 'CASH', 'PAID', 'Corporate Order', 1),
('ORD-007', 75000, 'COMPLETED', 'CUSTOMER_SELF', 'QR_CODE', 'PAID', 'Rina Melati', NULL);

-- Insert sample order items
INSERT INTO order_items (order_id, menu_id, quantity, price)
VALUES (1, 2, 1, 85000),  -- Paket Keluarga
       (2, 5, 1, 45000),  -- Combo A
       (3, 1, 1, 22000),  -- Paket Hemat 1
       (4, 5, 1, 45000),  -- Combo A
       (4, 16, 2, 15000), -- Jus Jeruk x2
       (4, 22, 1, 20000), -- Cheese Cake
       (5, 6, 1, 150000), -- Combo Family
       (6, 8, 2, 42000),  -- Burger Beef x2
       (6, 17, 2, 12000), -- Kopi Hitam x2
       (6, 20, 2, 15000), -- Kentang Goreng x2
       (7, 7, 1, 35000),  -- Nasi Goreng Spesial
       (7, 13, 1, 15000), -- Jus Jeruk
       (7, 19, 1, 15000), -- Kentang Goreng
       (7, 24, 1, 10000);
-- Ice Cream Vanilla

-- Insert sample invoices for reports
INSERT INTO invoices (invoice_number, order_id, cashier_id, total_amount, tax_amount, final_amount, payment_method)
VALUES ('INV-001', 1, 1, 85000, 0, 85000, 'QR_CODE'),
       ('INV-002', 2, 1, 45000, 0, 45000, 'QR_CODE'),
       ('INV-003', 3, 1, 22000, 0, 22000, 'CASH'),
       ('INV-004', 6, 1, 120000, 0, 120000, 'CASH');

-- Insert sample menu audit logs (for pengaturan tracking)
INSERT INTO menu_audit_log (menu_id, cashier_id, action, old_values, new_values)
VALUES
    (1, 3, 'PRICE_CHANGED', '{"price": 27000}', '{"price": 25000, "promo_price": 22000}'),
    (25, 3, 'AVAILABILITY_CHANGED', '{"available": true}', '{"available": false}'),
    (8, 1, 'UPDATED', '{"description": "Burger dengan daging sapi"}', '{"description": "Burger dengan daging sapi pilihan"}');

SELECT * FROM cashiers;

UPDATE cashiers
SET password_hash = '$2a$10$0r2c3irpg2AdjgufnJtEHey8clUYjUVVX32VrQYBsRt27mY1YgIBC'
WHERE username IN ('kasir1', 'kasir2', 'admin');