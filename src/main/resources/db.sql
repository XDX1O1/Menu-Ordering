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