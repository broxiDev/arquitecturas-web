-- Crear tablas e insertar datos de ejemplo para kitchen-service

CREATE TABLE IF NOT EXISTS daily_plan (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    cocina_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    UNIQUE (date, cocina_id)
);

CREATE TABLE IF NOT EXISTS plan_item (
    id BIGSERIAL PRIMARY KEY,
    daily_plan_id BIGINT NOT NULL REFERENCES daily_plan(id),
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    suggested_quantity INTEGER NOT NULL CHECK (suggested_quantity >= 1),
    UNIQUE (daily_plan_id, product_id)
);

-- Plan diario de hoy para COCINA-DULCE
-- sugerido = ceil(totalVendido/7) - remainderTotal
-- Brownie: ceil(70/7)=10, remainder=5, sugerido=5
-- Cheesecake: ceil(49/7)=7, remainder=3, sugerido=4
-- Tiramisú: ceil(56/7)=8, remainder=5, sugerido=3
INSERT INTO daily_plan (date, cocina_id, created_at) VALUES (CURRENT_DATE, 'COCINA-DULCE', NOW());
INSERT INTO plan_item (daily_plan_id, product_id, product_name, suggested_quantity) VALUES
(1, 101, 'Brownie de Chocolate', 5),
(1, 102, 'Cheesecake', 4),
(1, 103, 'Tiramisú', 3);

-- Plan diario de hoy para COCINA-CELIACA
-- Tostada: ceil(84/7)=12, remainder=4, sugerido=8
-- Bowl Quinoa: ceil(56/7)=8, remainder=4, sugerido=4
-- Rolls: ceil(70/7)=10, remainder=5, sugerido=5
INSERT INTO daily_plan (date, cocina_id, created_at) VALUES (CURRENT_DATE, 'COCINA-CELIACA', NOW());
INSERT INTO plan_item (daily_plan_id, product_id, product_name, suggested_quantity) VALUES
(2, 201, 'Tostada de Palta Sin Gluten', 8),
(2, 202, 'Bowl de Quinoa Sin Gluten', 4),
(2, 203, 'Rolls de Primavera de Arroz', 5);

-- Plan diario de hoy para COCINA-VEGANA
-- Buddha Bowl: ceil(84/7)=12, remainder=5, sugerido=7
-- Salteado Tofu: ceil(49/7)=7, remainder=3, sugerido=4
-- Curry: ceil(63/7)=9, remainder=5, sugerido=4
INSERT INTO daily_plan (date, cocina_id, created_at) VALUES (CURRENT_DATE, 'COCINA-VEGANA', NOW());
INSERT INTO plan_item (daily_plan_id, product_id, product_name, suggested_quantity) VALUES
(3, 301, 'Buddha Bowl Vegano', 7),
(3, 302, 'Salteado de Tofu', 4),
(3, 303, 'Curry de Garbanzos', 4);