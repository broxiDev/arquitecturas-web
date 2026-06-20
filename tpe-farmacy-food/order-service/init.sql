CREATE TABLE IF NOT EXISTS orders (
    order_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    fridge_id BIGINT NOT NULL,
    total DOUBLE PRECISION NOT NULL,
    status VARCHAR(255),
    payment_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
    order_item_id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(order_id),
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255),
    quantity INTEGER,
    unit_price DOUBLE PRECISION
);

INSERT INTO orders (user_id, fridge_id, total, status, payment_id, created_at, updated_at)
SELECT * FROM (VALUES
    (1, 1, 2500.00, 'PENDING',    NULL::VARCHAR,         NOW(),        NULL::TIMESTAMP),
    (2, 1, 3200.00, 'PAID',      'pay_abc123',          NOW() - INTERVAL '1 hour', NOW() - INTERVAL '30 minutes'),
    (1, 2, 1500.00, 'PICKED_UP', 'pay_def456',          NOW() - INTERVAL '2 days',  NOW() - INTERVAL '1 day'),
    (3, 1, 1800.00, 'CANCELLED', NULL::VARCHAR,         NOW() - INTERVAL '3 days',  NOW() - INTERVAL '2 days')
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM orders LIMIT 1);

INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price)
SELECT * FROM (VALUES
    (1, 101, 'Ensalada César', 2, 750.00),
    (1, 102, 'Bowl Proteico',  1, 1000.00),
    (2, 103, 'Wrap de Pollo',  4, 800.00),
    (3, 101, 'Ensalada César', 2, 750.00),
    (4, 104, 'Smoothie Detox', 2, 900.00)
) AS tmp
WHERE (SELECT COUNT(*) FROM orders LIMIT 1) <= 4;
