-- Sample categories
INSERT INTO categories (name, display_order)
VALUES ('SEMUA', 1);

-- Sample cashiers (password: password123)
INSERT INTO cashiers (username, password_hash, display_name, role, is_active)
VALUES ('kasir1', '$2a$10$N9qo8uLOickgx2ZMRZoMye.J8I..sJ9HXp6cSYwQD6o6e7F7QY8W2', 'kasir 1', 'CASHIER', TRUE),
       ('kasir2', '$2a$10$N9qo8uLOickgx2ZMRZoMye.J8I..sJ9HXp6cSYwQD6o6e7F7QY8W2', 'kasir 2', 'CASHIER', TRUE),
       ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye.J8I..sJ9HXp6cSYwQD6o6e7F7QY8W2', 'Administrator', 'ADMIN', TRUE);
