CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    auth_username VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS user_dietary_preferences (
    user_id BIGINT NOT NULL REFERENCES users(id),
    preference VARCHAR(255)
);

INSERT INTO users (name, email, auth_username, created_at)
SELECT * FROM (VALUES
    ('Usuario 1', 'usuario1@test.com', 'auth_usuario1', NOW()),
    ('Juan Pérez',        'juan@test.com',      'auth_juan',      NOW()),
    ('Carolina Ruiz',     'caro@test.com',      'auth_caro',      NOW()),
    ('Pedro Martínez',    'pedro@test.com',     'auth_pedro',     NOW()),
    ('Ana López',         'ana@test.com',       'auth_ana',       NOW()),
    ('Matías Bordonaro',  'matias@test.com',    'auth_matias',    NOW()),
    ('Fiorella Di Fiore', 'fiorella@test.com',  'auth_fiorella',  NOW()),
    ('Nahuel Di Fiore',   'nahuel@test.com',    'auth_nahuel',    NOW()),
    ('Gabriel Marrero',   'gabriel@test.com',   'auth_gabriel',   NOW()),
    ('Ale Machado',       'ale@test.com',       'auth_ale',       NOW())
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
