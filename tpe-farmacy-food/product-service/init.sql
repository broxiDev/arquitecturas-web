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
('cocina-sur'),
('cocina-norte');

INSERT INTO products (name, description, dietary_category, price, image_url, nutritional_info, conservacion_temperature, catalogo_id, created_at, updated_at) VALUES
('Ensalada César', 'Lechuga romana, crutones, parmesano y aderezo césar clásico', 'CLASICA', 7500.00, '/images/ensalada-cesar.jpg', 'Calorías: 350kcal, Proteínas: 25g, Carbohidratos: 15g, Grasas: 20g', 4.00, 1, NOW(), NOW()),
('Bowl Proteico', 'Bowl con pollo grillé, quinoa, palta y vegetales frescos', 'CLASICA', 9500.00, '/images/bowl-proteico.jpg', 'Calorías: 520kcal, Proteínas: 40g, Carbohidratos: 35g, Grasas: 22g', 4.00, 1, NOW(), NOW()),
('Wrap de Pollo', 'Wrap integral con pollo, verduras y salsa de yogur', 'CLASICA', 6800.00, '/images/wrap-pollo.jpg', 'Calorías: 420kcal, Proteínas: 30g, Carbohidratos: 40g, Grasas: 15g', 4.00, 1, NOW(), NOW()),
('Ensalada Vegana Premium', 'Quinoa, vegetales asados, tofu marinado y vinagreta de limón', 'VEGANO', 8500.00, '/images/ensalada-vegana.jpg', 'Calorías: 380kcal, Proteínas: 18g, Carbohidratos: 30g, Grasas: 22g', 4.00, 2, NOW(), NOW()),
('Bowl Vegano Energético', 'Arroz integral, tofu, brócoli, zanahoria y salsa de sésamo', 'VEGANO', 9200.00, '/images/bowl-vegano.jpg', 'Calorías: 450kcal, Proteínas: 20g, Carbohidratos: 50g, Grasas: 18g', 4.00, 2, NOW(), NOW()),
('Smoothie Verde', 'Espinaca, banana, manzana verde y jengibre', 'VEGANO', 4500.00, '/images/smoothie-verde.jpg', 'Calorías: 180kcal, Proteínas: 5g, Carbohidratos: 35g, Grasas: 3g', 4.00, 2, NOW(), NOW());
