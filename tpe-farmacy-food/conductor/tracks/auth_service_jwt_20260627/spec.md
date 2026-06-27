# Specification: Auth-Service con JWT

## Overview
Implementar un microservicio `auth-service` que centralice la autenticación del sistema FarmacyFood
mediante JWT, manejando registro, login y emisión de tokens seguros. El servicio expone endpoints
públicos para registro y login, y es el único responsable de la gestión de credenciales.

## Roles del Sistema
- **cliente** — Usuario final, registro público
- **cocina** — Personal de cocina fantasma, registro público
- **adminDeHeladera** — Encargado de heladera, **precargado como seed data** (único usuario, no se puede registrar)

## Functional Requirements

### FR-1: Registro de usuario (cliente y cocina)
- `POST /api/auth/registrar` recibe `{ username, password, rol, name, email }`
- Valida que `rol` sea `cliente` o `cocina` (cualquier otro rol → 400)
- Valida que `username` no exista (si existe → 400)
- Hashea password con BCrypt
- Crea `AuthUser` (username, password_hash, rol)
- **No se comunica con user-service** (el perfil se crea desde el frontend en un paso separado)
- Responde `201 Created`

### FR-2: Login
- `POST /api/auth/login` recibe `{ username, password }`
- Busca `AuthUser` por username (si no existe → 401)
- Verifica password con BCrypt (si no coincide → 401)
- Genera JWT (HS512) con `sub=username`, `role=rol`, exp=1h
- Responde `{ "token": "eyJ..." }`

### FR-3: Admin de heladera (seed data)
- Se precarga al iniciar auth-service via `CommandLineRunner`
- Username fijo: `admin-heladera`
- Password fija con hash BCrypt
- Rol: `adminDeHeladera`
- Se verifica que no exista antes de insertar (idempotente)

### FR-4: JWT Utility
- `generateToken(username, rol)` — HS512, claims: sub, role, iat, exp
- `validate(token)` — verifica firma y expiración
- `getUsername(token)`, `getRole(token)` — extracción de claims

### FR-5: Documentación de integración
- Generar guía en `docs/auth-integration-guide.md` especificando:
  - Filtro JWT para api-gateway
  - HeaderAuthFilter + SecurityConfig para servicios internos
  - FeignClientConfig para servicios con Feign
  - Endpoint de perfil en user-service

## Non-Functional Requirements
- CSRF deshabilitado (API stateless)
- Endpoints `/api/auth/**` públicos sin autenticación
- JWT firmado con clave secreta configurable via `jwt.secret`
- PostgreSQL como base de datos (puerto 5439, db: auth_db)
- Puerto del servicio: 8088

## Acceptance Criteria
- [ ] `mvn clean compile` pasa sin errores
- [ ] Registro de cliente con username único → 201
- [ ] Registro de cocina → 201
- [ ] Registro con rol `adminDeHeladera` → 400
- [ ] Registro con username duplicado → 400
- [ ] Login con credenciales válidas → 200 + JWT
- [ ] Login con password incorrecto → 401
- [ ] Login con usuario inexistente → 401
- [ ] Seed data de adminDeHeladera se carga al iniciar
- [ ] JWT contiene sub, role, iat, exp
- [ ] Guía de integración completa en `docs/auth-integration-guide.md`

## Out of Scope
- Comunicación con user-service (perfil se crea desde frontend en paso separado)
- JwtAuthenticationFilter en api-gateway (track separado)
- HeaderAuthFilter en servicios internos (track separado)
- Logout / token blacklist / refresh tokens
- Rate limiting
