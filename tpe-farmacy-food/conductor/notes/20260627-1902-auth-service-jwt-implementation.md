# Task Summary: Auth-Service JWT Implementation

## Fecha
2026-06-27 19:02

## Qué se hizo
Implementación completa del auth-service con autenticación JWT en el microservicio `auth-service`.

## Archivos creados/modificados

### auth-service/ (código de producción)
- `pom.xml` — Dependencias: web, security, jpa, jjwt 0.12.6, eureka-client, postgresql, validation, lombok
- `AuthServiceApplication.java` — Main class con @EnableDiscoveryClient
- `entity/AuthUser.java` — Entidad JPA: id, username (unique), password, rol
- `dto/LoginRequest.java` — Record con username, password
- `dto/RegisterRequest.java` — Record con username, password, rol, name, email
- `dto/AuthResponse.java` — Record con token
- `repository/AuthUserRepository.java` — findByUsername, existsByUsername
- `service/AuthService.java` — Interface con register y login
- `service/AuthServiceImpl.java` — Validación de roles (cliente/cocina), BCrypt, JWT generation
- `controller/AuthController.java` — POST /api/auth/registrar y /api/auth/login
- `config/SecurityConfig.java` — CSRF disable, /api/auth/** permitAll, BCrypt
- `config/JwtUtil.java` — HS512, generateToken, validate, getUsername, getRole
- `config/DataSeeder.java` — CommandLineRunner para seed data admin-heladera
- `exception/DuplicateUserException.java`
- `exception/InvalidCredentialsException.java`
- `exception/GlobalExceptionHandler.java` — Mapeo de excepciones a HTTP status codes
- `application.yml` — Puerto 8088, PostgreSQL, eureka, jwt.secret
- `docker-compose.yml` — PostgreSQL 16, puerto 5439, auth_db

### auth-service/ (tests)
- `repository/AuthUserRepositoryTest.java` — 4 tests (@DataJpaTest con H2)
- `config/JwtUtilTest.java` — 5 tests (generación, validación, extracción, expiración)
- `service/AuthServiceImplTest.java` — 7 tests (Mockito, register y login)
- `controller/AuthControllerTest.java` — 6 tests (MockMvc, status codes y response body)

### Documentación
- `docs/auth-integration-guide.md` — Guía detallada para integrar servicios con auth JWT
- `conductor/tracks/auth_service_jwt_20260627/` — Spec, plan, metadata

### Modificaciones existentes
- `pom.xml` raíz — Agregado auth-service module
- `docker-compose.yml` raíz — Agregado include de auth-service
- `conductor/tracks.md` — Agregado track

## Decisiones técnicas
- **Opción A** para manejo de passwords: auth-service es dueño de credenciales
- **Opción C** para vinculación: por username (clave natural)
- **jjwt 0.12.6** con HS512
- Seed data: admin-heladera / admin123 con rol adminDeHeladera
- Roles permitidos en registro público: cliente, cocina
- Registro no se comunica con user-service (frontend hace segundo llamado)

## Tests
- Total: 22 tests, 0 failures, 0 errors
- Compilación: OK

## Pendientes (otros tracks)
- Implementar JwtAuthenticationFilter en api-gateway
- Implementar HeaderAuthFilter + SecurityConfig en cada servicio interno
- Implementar FeignClientConfig en servicios con Feign
- Implementar endpoint de perfil en user-service
