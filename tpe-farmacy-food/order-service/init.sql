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
    -- COCINA-DULCE: fridges 1 y 2, products 101-103
    (1, 1, 45000.00, 'PAID',      'pay_001', NOW() - INTERVAL '1 day',   NOW() - INTERVAL '1 day'),
    (2, 1, 75000.00, 'PAID',      'pay_002', NOW() - INTERVAL '2 days',  NOW() - INTERVAL '2 days'),
    (1, 2, 60000.00, 'PICKED_UP', 'pay_003', NOW() - INTERVAL '3 days',  NOW() - INTERVAL '2 days'),
    (3, 2, 88000.00, 'PAID',      'pay_004', NOW() - INTERVAL '4 days',  NOW() - INTERVAL '4 days'),
    (2, 1, 83000.00, 'PICKED_UP', 'pay_005', NOW() - INTERVAL '5 days',  NOW() - INTERVAL '4 days'),
    (1, 2, 25000.00, 'PAID',      'pay_006', NOW() - INTERVAL '6 days',  NOW() - INTERVAL '6 days'),
    (3, 1, 97000.00, 'PAID',      'pay_007', NOW() - INTERVAL '7 days',  NOW() - INTERVAL '7 days'),
    (2, 2, 56000.00, 'PICKED_UP', 'pay_008', NOW() - INTERVAL '8 days',  NOW() - INTERVAL '7 days'),
    (1, 1, 72000.00, 'PAID',      'pay_009', NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days'),
    (3, 2, 64000.00, 'PAID',      'pay_010', NOW() - INTERVAL '12 days', NOW() - INTERVAL '12 days'),

    -- COCINA-CELIACA: fridges 3 y 4, products 201-203
    (4, 3, 72000.00, 'PAID',      'pay_011', NOW() - INTERVAL '3 days',  NOW() - INTERVAL '3 days'),
    (5, 4, 85000.00, 'PICKED_UP', 'pay_012', NOW() - INTERVAL '5 days',  NOW() - INTERVAL '4 days'),
    (4, 3, 68000.00, 'PAID',      'pay_013', NOW() - INTERVAL '9 days',  NOW() - INTERVAL '9 days'),

    -- COCINA-VEGANA: fridges 5 y 6, products 301-303
    (6, 5, 85000.00, 'PAID',      'pay_014', NOW() - INTERVAL '2 days',  NOW() - INTERVAL '2 days'),
    (7, 6, 78000.00, 'PICKED_UP', 'pay_015', NOW() - INTERVAL '4 days',  NOW() - INTERVAL '3 days'),
    (6, 5, 82000.00, 'PAID',      'pay_016', NOW() - INTERVAL '6 days',  NOW() - INTERVAL '6 days')
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM orders LIMIT 1);

INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price)
SELECT * FROM (VALUES
    -- Order 1: DULCE, fridge 1
    (1, 101, 'Brownie de Chocolate', 2, 7500.00),
    (1, 102, 'Cheesecake',           3, 9500.00),
    -- Order 2: DULCE, fridge 1
    (2, 101, 'Brownie de Chocolate', 10, 7500.00),
    -- Order 3: DULCE, fridge 2
    (3, 101, 'Brownie de Chocolate', 8, 7500.00),
    -- Order 4: DULCE, fridge 2
    (4, 103, 'Tiramisú',             10, 8800.00),
    -- Order 5: DULCE, fridge 1
    (5, 102, 'Cheesecake',           5, 9500.00),
    (5, 103, 'Tiramisú',             4, 8800.00),
    -- Order 6: DULCE, fridge 2
    (6, 101, 'Brownie de Chocolate', 2, 7500.00),
    (6, 102, 'Cheesecake',           1, 9500.00),
    -- Order 7: DULCE, fridge 1
    (7, 101, 'Brownie de Chocolate', 6, 7500.00),
    (7, 102, 'Cheesecake',           3, 9500.00),
    (7, 103, 'Tiramisú',             2, 8800.00),
    -- Order 8: DULCE, fridge 2
    (8, 101, 'Brownie de Chocolate', 4, 7500.00),
    (8, 103, 'Tiramisú',             3, 8800.00),
    -- Order 9: DULCE, fridge 1
    (9, 101, 'Brownie de Chocolate', 5, 7500.00),
    (9, 102, 'Cheesecake',           3, 9500.00),
    -- Order 10: DULCE, fridge 2
    (10, 102, 'Cheesecake',          4, 9500.00),
    (10, 103, 'Tiramisú',            3, 8800.00),

    -- Order 11: CELIACA, fridge 3
    (11, 201, 'Tostada de Palta Sin Gluten',    5, 7200.00),
    (11, 202, 'Bowl de Quinoa Sin Gluten',      4, 8500.00),
    -- Order 12: CELIACA, fridge 4
    (12, 201, 'Tostada de Palta Sin Gluten',    4, 7200.00),
    (12, 203, 'Rolls de Primavera de Arroz',    8, 6800.00),
    -- Order 13: CELIACA, fridge 3
    (13, 202, 'Bowl de Quinoa Sin Gluten',      8, 8500.00),

    -- Order 14: VEGANA, fridge 5
    (14, 301, 'Buddha Bowl Vegano',             10, 8500.00),
    -- Order 15: VEGANA, fridge 6
    (15, 302, 'Salteado de Tofu',               10, 7800.00),
    -- Order 16: VEGANA, fridge 5
    (16, 303, 'Curry de Garbanzos',             10, 8200.00)
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM order_items LIMIT 1);
