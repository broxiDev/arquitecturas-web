# Implementation Plan: Auth-Service con JWT

## Phase 1: Setup & Infrastructure

- [ ] Task: Verify auth-service module structure exists
    - [ ] Confirm `auth-service/pom.xml` tiene todas las dependencias (web, security, jpa, jjwt 0.12.6, eureka-client, postgresql, validation, lombok)
    - [ ] Confirm módulo listado en parent `pom.xml`
    - [ ] Confirm `application.yml` con puerto 8088, datasource PostgreSQL, eureka, jwt.secret
    - [ ] Confirm `docker-compose.yml` con PostgreSQL 16 puerto 5439, db: auth_db
    - [ ] Confirm `init.sql` creado

- [ ] Task: Conductor - User Manual Verification 'Setup & Infrastructure' (Protocol in workflow.md)

## Phase 2: Domain Layer (Entity, DTOs, Repository)

- [ ] Task: Review and update `AuthUser.java` entity
    - [ ] Verificar campos: id (Long, IDENTITY), username (unique, not null), password (not null), rol (not null)
    - [ ] Verificar tabla: `auth_users`

- [ ] Task: Review and update DTOs
    - [ ] `LoginRequest` — record con `@NotBlank String username`, `@NotBlank String password`
    - [ ] `AuthResponse` — record con `String token`

- [ ] Task: Review `AuthUserRepository.java`
    - [ ] `findByUsername(String username)` → `Optional<AuthUser>`
    - [ ] `existsByUsername(String username)` → `boolean`

- [ ] Task: Write unit tests for repository
    - [ ] Test: `findByUsername` retorna Optional vacío si no existe
    - [ ] Test: `findByUsername` retorna el usuario si existe
    - [ ] Test: `existsByUsername` retorna true/false correctamente

- [ ] Task: Conductor - User Manual Verification 'Domain Layer' (Protocol in workflow.md)

## Phase 3: Security & JWT Layer

- [ ] Task: Review `SecurityConfig.java`
    - [ ] Verificar CSRF deshabilitado
    - [ ] Verificar `/api/auth/**` como público (permitAll)
    - [ ] Verificar `PasswordEncoder` bean con BCrypt
    - [ ] Verificar httpBasic y formLogin deshabilitados

- [ ] Task: Review `JwtUtil.java`
    - [ ] Verificar algoritmo HS512 con `Keys.hmacShaKeyFor()`
    - [ ] Verificar claims: sub (username), role, iat, exp
    - [ ] Verificar `expirationMs` configurable via `jwt.expiration-ms`
    - [ ] Verificar `validate()` captura `JwtException` e `IllegalArgumentException`
    - [ ] Verificar `getUsername()` y `getRole()` extraen del payload

- [ ] Task: Write unit tests for JwtUtil
    - [ ] Test: `generateToken` produce token válido que pasa `validate()`
    - [ ] Test: `getUsername` extrae subject correctamente
    - [ ] Test: `getRole` extrae claim role correctamente
    - [ ] Test: `validate` retorna false para token inválido
    - [ ] Test: `validate` retorna false para token expirado

- [ ] Task: Conductor - User Manual Verification 'Security & JWT Layer' (Protocol in workflow.md)

## Phase 4: Service Layer

- [ ] Task: Review `AuthService.java` interface
    - [ ] `AuthUser register(LoginRequest request)`
    - [ ] `AuthResponse login(LoginRequest request)`

- [ ] Task: Review `AuthServiceImpl.java`
    - [ ] `register()`:
        - [ ] Validar que rol sea "cliente" o "cocina"
        - [ ] Validar que username no exista (existsByUsername)
        - [ ] Hashear password con BCrypt
        - [ ] Guardar AuthUser con rol proporcionado
        - [ ] Si username duplicado → `DuplicateUserException` (400)
        - [ ] Si rol inválido → `InvalidCredentialsException` (400)
    - [ ] `login()`:
        - [ ] Buscar por username → si no existe → `InvalidCredentialsException` (401)
        - [ ] Verificar password con BCrypt.matches → si no coincide → 401
        - [ ] Generar JWT con jwtUtil.generateToken(username, rol)
        - [ ] Retornar AuthResponse con token

- [ ] Task: Write unit tests for AuthServiceImpl
    - [ ] Test: `register` crea usuario exitosamente
    - [ ] Test: `register` con username duplicado lanza DuplicateUserException
    - [ ] Test: `register` con rol inválido lanza excepción
    - [ ] Test: `register` hashea password correctamente
    - [ ] Test: `login` con credenciales válidas retorna token
    - [ ] Test: `login` con password incorrecto lanza InvalidCredentialsException
    - [ ] Test: `login` con usuario inexistente lanza InvalidCredentialsException

- [ ] Task: Review exception classes
    - [ ] `DuplicateUserException` — RuntimeException
    - [ ] `InvalidCredentialsException` — RuntimeException
    - [ ] `GlobalExceptionHandler` — `@RestControllerAdvice`
        - [ ] DuplicateUserException → 400
        - [ ] InvalidCredentialsException → 401
        - [ ] MethodArgumentNotValidException → 400 (validation errors)

- [ ] Task: Conductor - User Manual Verification 'Service Layer' (Protocol in workflow.md)

## Phase 5: Controller Layer & Seed Data

- [ ] Task: Review `AuthController.java`
    - [ ] `POST /api/auth/registrar` → `register(LoginRequest)` → 201
    - [ ] `POST /api/auth/login` → `login(LoginRequest)` → 200 + token

- [ ] Task: Write integration tests for AuthController
    - [ ] Test: POST /api/auth/registrar con datos válidos → 201
    - [ ] Test: POST /api/auth/registrar con username duplicado → 400
    - [ ] Test: POST /api/auth/registrar con rol adminDeHeladera → 400
    - [ ] Test: POST /api/auth/login con credenciales válidas → 200 + token
    - [ ] Test: POST /api/auth/login con password incorrecto → 401
    - [ ] Test: POST /api/auth/login con usuario inexistente → 401
    - [ ] Test: Swagger endpoint accesible

- [ ] Task: Implement CommandLineRunner para seed data adminDeHeladera
    - [ ] Crear `DataSeeder.java` en `config/`
    - [ ] Implementar `CommandLineRunner`
    - [ ] Verificar si `admin-heladera` ya existe (idempotente)
    - [ ] Si no existe: crear AuthUser con password hasheado y rol `adminDeHeladera`
    - [ ] Log de confirmación al iniciar

- [ ] Task: Conductor - User Manual Verification 'Controller Layer & Seed Data' (Protocol in workflow.md)

## Phase 6: Documentación de Integración

- [ ] Task: Review and confirm `docs/auth-integration-guide.md`
    - [ ] Sección api-gateway: JwtUtil, JwtAuthenticationFilter, application.yml
    - [ ] Sección servicios con Feign: HeaderAuthFilter, SecurityConfig, FeignClientConfig
    - [ ] Sección servicios sin Feign: HeaderAuthFilter, SecurityConfig
    - [ ] Sección user-service: endpoint de perfil, flujo de registro completo
    - [ ] Consideraciones generales (jwt.secret compartido, etc.)

- [ ] Task: Conductor - User Manual Verification 'Documentacion de Integracion' (Protocol in workflow.md)

## Phase 7: Final Verification

- [ ] Task: Verify full compilation (`mvn clean compile -pl auth-service`)
- [ ] Task: Run all tests (`mvn test -pl auth-service`)
- [ ] Task: Verify docker-compose syntax
- [ ] Task: Verify application.yml coherencia con spec
- [ ] Task: Create task summary in `conductor/notes/`
- [ ] Task: Conductor - User Manual Verification 'Final Verification' (Protocol in workflow.md)
