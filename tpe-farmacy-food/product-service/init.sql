CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    dietary_category VARCHAR(100),
    price NUMERIC(10, 2) NOT NULL,
    image_url VARCHAR(500),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE
);

INSERT INTO products (name, description, dietary_category, price, image_url, created_at, updated_at) VALUES
('Ensalada César', 'Lechuga romana, crutones, parmesano y aderezo césar clásico', 'CLASICA', 7500.00, '/images/ensalada-cesar.jpg', NOW(), NOW()),
('Bowl Proteico', 'Bowl con pollo grillé, quinoa, palta y vegetales frescos', 'CLASICA', 9500.00, '/images/bowl-proteico.jpg', NOW(), NOW()),
('Wrap de Pollo', 'Wrap integral con pollo, verduras y salsa de yogur', 'CLASICA', 6800.00, '/images/wrap-pollo.jpg', NOW(), NOW()),
('Ensalada Vegana Premium', 'Quinoa, vegetales asados, tofu marinado y vinagreta de limón', 'VEGANO', 8500.00, '/images/ensalada-vegana.jpg', NOW(), NOW()),
('Bowl Vegano Energético', 'Arroz integral, tofu, brócoli, zanahoria y salsa de sésamo', 'VEGANO', 9200.00, '/images/bowl-vegano.jpg', NOW(), NOW()),
('Smoothie Verde', 'Espinaca, banana, manzana verde y jengibre', 'VEGANO', 4500.00, '/images/smoothie-verde.jpg', NOW(), NOW()),
('Barrita de Cereal Sin TACC', 'Barrita de quinoa, amaranto y frutos secos', 'SIN_GLUTEN', 3200.00, '/images/barrita-sin-tacc.jpg', NOW(), NOW()),
('Brownie de Almendras Sin TACC', 'Brownie húmedo de chocolate con harina de almendras', 'SIN_GLUTEN', 5500.00, '/images/brownie-sin-tacc.jpg', NOW(), NOW()),
('Pizza de Coliflor Sin TACC', 'Base de coliflor con queso vegano y vegetales', 'SIN_GLUTEN', 8900.00, '/images/pizza-coliflor.jpg', NOW(), NOW()),
('Ensalada Mediterránea', 'Lechuga, tomate, pepino, aceitunas y queso feta', 'VEGETARIANO', 7200.00, '/images/ensalada-mediterranea.jpg', NOW(), NOW()),
('Wrap de Hummus y Vegetales', 'Wrap integral con hummus, pimientos asados y rúcula', 'VEGETARIANO', 6500.00, '/images/wrap-hummus.jpg', NOW(), NOW()),
('Lasagna de Berenjena', 'Capas de berenjena, ricota, espinaca y salsa de tomate', 'VEGETARIANO', 8800.00, '/images/lasagna-berenjena.jpg', NOW(), NOW());
