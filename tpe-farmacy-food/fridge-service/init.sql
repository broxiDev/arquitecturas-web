CREATE TABLE IF NOT EXISTS heladera (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    address VARCHAR(300) NOT NULL,
    status VARCHAR(20) NOT NULL,
    last_maintenance DATE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS heladera_cocina (
    heladera_id INTEGER NOT NULL REFERENCES heladera(id),
    cocina_id BIGINT NOT NULL,
    PRIMARY KEY (heladera_id, cocina_id)
);

CREATE TABLE IF NOT EXISTS stock_heladera (
    id SERIAL PRIMARY KEY,
    heladera_id INTEGER NOT NULL REFERENCES heladera(id),
    cocina_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity >= 0),
    price DECIMAL(10,2) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (heladera_id, cocina_id, product_id)
);

-- Seed data: 3 cocinas, 2 heladeras cada una
-- COCINA-DULCE (cocina_id=1, productos 101-103)
INSERT INTO heladera (id, name, latitude, longitude, address, status) VALUES
(1, 'Heladera Palermo Dulce', -34.5883, -58.4223, 'Av. Santa Fe 1234, Palermo', 'ACTIVE'),
(2, 'Heladera Centro Dulce', -34.6033, -58.3817, 'Av. Corrientes 567, Microcentro', 'ACTIVE');

INSERT INTO heladera_cocina (heladera_id, cocina_id) VALUES
(1, 1),
(2, 1);

INSERT INTO stock_heladera (heladera_id, cocina_id, product_id, product_name, quantity, price) VALUES
(1, 1, 101, 'Brownie de Chocolate', 3, 15.00),
(1, 1, 102, 'Cheesecake', 2, 12.50),
(1, 1, 103, 'Tiramisú', 2, 14.00),
(2, 1, 101, 'Brownie de Chocolate', 2, 15.00),
(2, 1, 102, 'Cheesecake', 1, 12.50),
(2, 1, 103, 'Tiramisú', 3, 14.00);

-- COCINA-CELIACA (cocina_id=2, productos 201-203)
INSERT INTO heladera (id, name, latitude, longitude, address, status) VALUES
(3, 'Heladera Belgrano Celiaca', -34.5632, -58.4512, 'Av. Cabildo 890, Belgrano', 'ACTIVE'),
(4, 'Heladera Devoto Celiaca', -34.6034, -58.5123, 'Av. Lincoln 345, Villa Devoto', 'ACTIVE');

INSERT INTO heladera_cocina (heladera_id, cocina_id) VALUES
(3, 2),
(4, 2);

INSERT INTO stock_heladera (heladera_id, cocina_id, product_id, product_name, quantity, price) VALUES
(3, 2, 201, 'Tostada de Palta Sin Gluten', 3, 18.00),
(3, 2, 202, 'Bowl de Quinoa Sin Gluten', 2, 16.50),
(3, 2, 203, 'Rolls de Primavera de Arroz', 2, 14.50),
(4, 2, 201, 'Tostada de Palta Sin Gluten', 1, 18.00),
(4, 2, 202, 'Bowl de Quinoa Sin Gluten', 2, 16.50),
(4, 2, 203, 'Rolls de Primavera de Arroz', 3, 14.50);

-- COCINA-VEGANA (cocina_id=3, productos 301-303)
INSERT INTO heladera (id, name, latitude, longitude, address, status) VALUES
(5, 'Heladera Palermo Vegana', -34.5856, -58.4301, 'Av. Scalabrini Ortiz 1234, Palermo', 'ACTIVE'),
(6, 'Heladera Villa Crespo Vegana', -34.5956, -58.4401, 'Av. Corrientes 4567, Villa Crespo', 'ACTIVE');

INSERT INTO heladera_cocina (heladera_id, cocina_id) VALUES
(5, 3),
(6, 3);

INSERT INTO stock_heladera (heladera_id, cocina_id, product_id, product_name, quantity, price) VALUES
(5, 3, 301, 'Buddha Bowl Vegano', 3, 20.00),
(5, 3, 302, 'Salteado de Tofu', 2, 17.00),
(5, 3, 303, 'Curry de Garbanzos', 3, 15.50),
(6, 3, 301, 'Buddha Bowl Vegano', 2, 20.00),
(6, 3, 302, 'Salteado de Tofu', 1, 17.00),
(6, 3, 303, 'Curry de Garbanzos', 2, 15.50);

SELECT setval('heladera_id_seq', 6);
SELECT setval('stock_heladera_id_seq', 18);
