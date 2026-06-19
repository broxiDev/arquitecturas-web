-- Schema y datos de ejemplo para recommendation-service

-- Tabla de perfiles de preferencia
CREATE TABLE IF NOT EXISTS perfil_preferencia (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    dietary_preferences VARCHAR(500),
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de cache de historial de compras
CREATE TABLE IF NOT EXISTS historial_compras_cache (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de ordenes cacheadas (hijas de historial)
CREATE TABLE IF NOT EXISTS orden_cache (
    id BIGSERIAL PRIMARY KEY,
    historial_id BIGINT NOT NULL REFERENCES historial_compras_cache(id),
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DOUBLE PRECISION NOT NULL,
    purchased_at TIMESTAMP NOT NULL
);

-- Tabla de resultados de recomendacion (cache)
CREATE TABLE IF NOT EXISTS recomendacion_resultado (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de productos recomendados (hijos de recomendacion)
CREATE TABLE IF NOT EXISTS producto_recomendado (
    id BIGSERIAL PRIMARY KEY,
    recomendacion_id BIGINT NOT NULL REFERENCES recomendacion_resultado(id),
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    reason VARCHAR(500),
    dietary_category VARCHAR(100)
);

-- Datos de ejemplo
INSERT INTO perfil_preferencia (user_id, dietary_preferences, last_updated)
VALUES (1, 'VEGANO,SIN_GLUTEN', CURRENT_TIMESTAMP);

INSERT INTO perfil_preferencia (user_id, dietary_preferences, last_updated)
VALUES (2, 'VEGETARIANO', CURRENT_TIMESTAMP);
