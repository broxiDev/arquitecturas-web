CREATE TABLE IF NOT EXISTS heladera (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    address VARCHAR(300) NOT NULL,
    status VARCHAR(20) NOT NULL,
    cocina_id VARCHAR(50) NOT NULL,
    last_maintenance DATE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS stock_heladera (
    id SERIAL PRIMARY KEY,
    heladera_id INTEGER NOT NULL REFERENCES heladera(id),
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity >= 0),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (heladera_id, product_id)
);

-- Seed data: 3 cocinas, 2 heladeras cada una
-- COCINA-DULCE (productos 101-103)
INSERT INTO heladera (id, name, latitude, longitude, address, status, cocina_id) VALUES
(1, 'Heladera Palermo Dulce', -34.5883, -58.4223, 'Av. Santa Fe 1234, Palermo', 'ACTIVE', 'COCINA-DULCE'),
(2, 'Heladera Centro Dulce', -34.6033, -58.3817, 'Av. Corrientes 567, Microcentro', 'ACTIVE', 'COCINA-DULCE');

INSERT INTO stock_heladera (heladera_id, product_id, product_name, quantity) VALUES
(1, 101, 'Brownie de Chocolate', 3),
(1, 102, 'Cheesecake', 2),
(1, 103, 'Tiramisú', 2),
(2, 101, 'Brownie de Chocolate', 2),
(2, 102, 'Cheesecake', 1),
(2, 103, 'Tiramisú', 3);

-- COCINA-CELIACA (productos 201-203)
INSERT INTO heladera (id, name, latitude, longitude, address, status, cocina_id) VALUES
(3, 'Heladera Belgrano Celiaca', -34.5632, -58.4512, 'Av. Cabildo 890, Belgrano', 'ACTIVE', 'COCINA-CELIACA'),
(4, 'Heladera Devoto Celiaca', -34.6034, -58.5123, 'Av. Lincoln 345, Villa Devoto', 'ACTIVE', 'COCINA-CELIACA');

INSERT INTO stock_heladera (heladera_id, product_id, product_name, quantity) VALUES
(3, 201, 'Tostada de Palta Sin Gluten', 3),
(3, 202, 'Bowl de Quinoa Sin Gluten', 2),
(3, 203, 'Rolls de Primavera de Arroz', 2),
(4, 201, 'Tostada de Palta Sin Gluten', 1),
(4, 202, 'Bowl de Quinoa Sin Gluten', 2),
(4, 203, 'Rolls de Primavera de Arroz', 3);

-- COCINA-VEGANA (productos 301-303)
INSERT INTO heladera (id, name, latitude, longitude, address, status, cocina_id) VALUES
(5, 'Heladera Palermo Vegana', -34.5856, -58.4301, 'Av. Scalabrini Ortiz 1234, Palermo', 'ACTIVE', 'COCINA-VEGANA'),
(6, 'Heladera Villa Crespo Vegana', -34.5956, -58.4401, 'Av. Corrientes 4567, Villa Crespo', 'ACTIVE', 'COCINA-VEGANA');

INSERT INTO stock_heladera (heladera_id, product_id, product_name, quantity) VALUES
(5, 301, 'Buddha Bowl Vegano', 3),
(5, 302, 'Salteado de Tofu', 2),
(5, 303, 'Curry de Garbanzos', 3),
(6, 301, 'Buddha Bowl Vegano', 2),
(6, 302, 'Salteado de Tofu', 1),
(6, 303, 'Curry de Garbanzos', 2);

SELECT setval('heladera_id_seq', 6);
SELECT setval('stock_heladera_id_seq', 18);
