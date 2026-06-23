CREATE TABLE IF NOT EXISTS catalogos (
    id BIGSERIAL PRIMARY KEY,
    cocina_id VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    dietary_category VARCHAR(100),
    price NUMERIC(10, 2) NOT NULL,
    image_url VARCHAR(500),
    nutritional_info VARCHAR(2000),
    conservacion_temperature NUMERIC(5, 2),
    catalogo_id BIGINT REFERENCES catalogos(id),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE
);

INSERT INTO catalogos (cocina_id) VALUES
('COCINA-DULCE'),
('COCINA-CELIACA'),
('COCINA-VEGANA');

INSERT INTO products (id, name, description, dietary_category, price, image_url, nutritional_info, conservacion_temperature, catalogo_id, created_at, updated_at) VALUES
-- COCINA-DULCE (IDs 101-103)
(101, 'Brownie de Chocolate', 'Brownie intenso con centro de chocolate fundido y nueces caramelizadas', 'DULCE', 7500.00, '/images/brownie-chocolate.jpg', 'Calorías: 450kcal, Proteínas: 8g, Carbohidratos: 55g, Grasas: 22g', 4.00, 1, NOW(), NOW()),
(102, 'Cheesecake', 'Cheesecake clásico con base de galletas y coulis de frutos rojos', 'DULCE', 9500.00, '/images/cheesecake.jpg', 'Calorías: 380kcal, Proteínas: 12g, Carbohidratos: 35g, Grasas: 18g', 4.00, 1, NOW(), NOW()),
(103, 'Tiramisú', 'Tiramisú tradicional con mascarpone, café espresso y cacao', 'DULCE', 8800.00, '/images/tiramisu.jpg', 'Calorías: 420kcal, Proteínas: 10g, Carbohidratos: 45g, Grasas: 20g', 4.00, 1, NOW(), NOW()),
-- COCINA-CELIACA (IDs 201-203)
(201, 'Tostada de Palta Sin Gluten', 'Tostada de pan sin gluten con palta, huevo pochado y semillas', 'SIN_GLUTEN', 7200.00, '/images/tostada-palta-sin-gluten.jpg', 'Calorías: 320kcal, Proteínas: 15g, Carbohidratos: 28g, Grasas: 16g', 4.00, 2, NOW(), NOW()),
(202, 'Bowl de Quinoa Sin Gluten', 'Bowl de quinoa con pollo, vegetales asados y aliño de limón', 'SIN_GLUTEN', 9800.00, '/images/bowl-quinoa-sin-gluten.jpg', 'Calorías: 520kcal, Proteínas: 35g, Carbohidratos: 42g, Grasas: 18g', 4.00, 2, NOW(), NOW()),
(203, 'Rolls de Primavera de Arroz', 'Rolls de papel de arroz con verduras frescas, zanahoria y salsa de soja', 'SIN_GLUTEN', 6500.00, '/images/rolls-primavera-arroz.jpg', 'Calorías: 280kcal, Proteínas: 8g, Carbohidratos: 35g, Grasas: 10g', 4.00, 2, NOW(), NOW()),
-- COCINA-VEGANA (IDs 301-303)
(301, 'Buddha Bowl Vegano', 'Bowl de quinoa, garbanzos asados, palta, edamame y tahini', 'VEGANO', 8500.00, '/images/buddha-bowl-vegano.jpg', 'Calorías: 480kcal, Proteínas: 22g, Carbohidratos: 45g, Grasas: 20g', 4.00, 3, NOW(), NOW()),
(302, 'Salteado de Tofu', 'Tofu marinado al wok con brócoli, zanahoria y salsa de sésamo', 'VEGANO', 7800.00, '/images/salteado-tofu.jpg', 'Calorías: 380kcal, Proteínas: 20g, Carbohidratos: 30g, Grasas: 18g', 4.00, 3, NOW(), NOW()),
(303, 'Curry de Garbanzos', 'Curry espeso de garbanzos con coco, espinacas y arroz basmati', 'VEGANO', 9200.00, '/images/curry-garbanzos.jpg', 'Calorías: 450kcal, Proteínas: 18g, Carbohidratos: 48g, Grasas: 16g', 4.00, 3, NOW(), NOW());

-- Resetea la secuencia de IDs para que no genere conflictos con próximos inserts
SELECT setval('products_id_seq', 303);