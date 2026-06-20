CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS user_dietary_preferences (
    user_id BIGINT NOT NULL REFERENCES users(id),
    preference VARCHAR(255)
);

INSERT INTO users (name, email, password_hash, created_at)
SELECT * FROM (VALUES
    ('Quique TDV',        'maria@test.com',     'hash_maria_2026',    NOW()),
    ('Juan Pérez',        'juan@test.com',      'hash_juan_2026',     NOW()),
    ('Carolina Ruiz',     'caro@test.com',      'hash_caro_2026',     NOW()),
    ('Pedro Martínez',    'pedro@test.com',     'hash_pedro_2026',    NOW()),
    ('Ana López',         'ana@test.com',       'hash_ana_2026',      NOW()),
    ('Matías Bordonaro',  'matias@test.com',    'hash_matias_2026',   NOW()),
    ('Fiorella Di Fiore', 'fiorella@test.com',  'hash_fiorella_2026', NOW()),
    ('Nahuel Di Fiore',   'nahuel@test.com',    'hash_nahuel_2026',   NOW()),
    ('Gabriel Marrero',   'gabriel@test.com',   'hash_gabriel_2026',  NOW()),
    ('Ale Machado',       'ale@test.com',       'hash_ale_2026',      NOW())
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM users LIMIT 1);

INSERT INTO user_dietary_preferences (user_id, preference)
SELECT * FROM (VALUES
    (1, 'vegano'),
    (2, 'sin gluten'),
    (3, 'vegetariano'),
    (3, 'sin gluten'),
    (4, 'vegano'),
    (4, 'sin gluten'),
    (5, 'vegetariano'),
    (6, 'vegano'),
    (6, 'sin gluten'),
    (7, 'vegetariano'),
    (8, 'sin gluten'),
    (9, 'vegano'),
    (9, 'vegetariano'),
    (10, 'sin gluten'),
    (10, 'vegetariano')
) AS tmp
WHERE EXISTS (SELECT 1 FROM users WHERE id = 1);
