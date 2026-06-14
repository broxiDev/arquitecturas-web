-- Crear tablas e insertar datos de ejemplo para kitchen-service

CREATE TABLE IF NOT EXISTS daily_plan (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL UNIQUE,
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS plan_item (
    id BIGSERIAL PRIMARY KEY,
    daily_plan_id BIGINT NOT NULL REFERENCES daily_plan(id),
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    suggested_quantity INTEGER NOT NULL CHECK (suggested_quantity >= 1),
    UNIQUE (daily_plan_id, product_id)
);

-- Plan diario de hoy
INSERT INTO daily_plan (date, created_at) VALUES (CURRENT_DATE, NOW());

-- Items del plan
INSERT INTO plan_item (daily_plan_id, product_id, product_name, suggested_quantity) VALUES
(1, 101, 'Ensalada César', 10),
(1, 102, 'Bowl Proteico', 6),
(1, 103, 'Wrap de Pollo', 8);
