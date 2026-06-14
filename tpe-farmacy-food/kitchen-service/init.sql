-- Datos de ejemplo para kitchen-service (solo dev)

-- Plan diario de hoy
INSERT INTO daily_plan (date, created_at) VALUES (CURRENT_DATE, NOW());

-- Items del plan (usar el id del plan insertado, que será 1)
INSERT INTO plan_item (daily_plan_id, product_id, product_name, suggested_quantity) VALUES
(1, 101, 'Ensalada César', 10),
(1, 102, 'Bowl Proteico', 6),
(1, 103, 'Wrap de Pollo', 8);
