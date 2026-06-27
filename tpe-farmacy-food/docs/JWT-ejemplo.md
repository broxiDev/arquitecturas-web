# Patrón de Autenticación JWT para Microservicios

Basado en el proyecto `msvc-auth` (Spring Boot 3.2.5 + Java 21 + JWT + Spring Cloud Gateway).

## Arquitectura General

```
                    ┌─────────────────────┐
                    │    API GATEWAY      │
                    │  JwtAuthentication  │
                    │  Filter (Global)    │
                    └──────┬──┬──┬────────┘
                           │  │  │
         ┌─────────────────┘  │  └──────────────────┐
         ▼                    ▼                     ▼
┌────────────────┐   ┌──────────────┐   ┌──────────────────┐
│  msvc-auth     │   │ msvc-usuario │   │  msvc-recurso    │
│  (token issuer)│   │ HeaderAuth   │   │  HeaderAuth      │
│  Sin JWT filter│   │ Filter       │   │  Filter          │
│                │   │              │   │  (con Feign)     │
└────────────────┘   └──────────────┘   └──────────────────┘
```

- **API Gateway** es el único punto de entrada. Valida todos los JWTs, extrae la identidad del usuario, y la propaga como headers HTTP a los servicios internos.
- **msvc-auth** solo emite tokens. No valida tokens entrantes porque confía en el gateway.
- **Servicios internos** no validan JWTs. Confían en los headers `X-User` y `X-Role` que inyecta el gateway.

---

## 1. msvc-auth — Servicio de Autenticación

### Dependencias (pom.xml)

- `spring-boot-starter-web`
- `spring-boot-starter-security`
- `spring-boot-starter-data-jpa`
- `io.jsonwebtoken:jjwt:0.9.1` + `javax.xml.bind:jaxb-api:2.3.1` (necesario para Java 9+)
- Driver de base de datos (MySQL, PostgreSQL, etc.)
- Lombok (opcional)

### Entidad Usuario

| Campo     | Tipo   | Nota                        |
|-----------|--------|-----------------------------|
| id        | Long   | Autogenerado (IDENTITY)     |
| username  | String | Nombre de usuario           |
| password  | String | Hash BCrypt                 |
| rol       | String | Ej: `"ADMIN"`, `"USER"`     |

### SecurityConfig

```java
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic();
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

- CSRF deshabilitado (API stateless).
- Todo `/api/auth/**` es público (registro y login).
- No se registra ningún filtro JWT acá. Este servicio no valida tokens entrantes.

### JwtUtil

```java
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(String username, String rol) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hora
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public boolean validate(String token) { /* parsea, captura JwtException */ }
    public String getUsername(String token) { /* extrae subject */ }
    public String getRole(String token) { /* extrae claim "role" */ }
}
```

- Algoritmo: **HS512** (HMAC-SHA512).
- Claims incluidos: `sub` (username), `role`, `iat`, `exp`.
- La clave secreta se configura via `jwt.secret` en `application.properties`.

### AuthController

| Método | Endpoint                | Body                      | Respuesta                     |
|--------|------------------------|---------------------------|-------------------------------|
| POST   | `/api/auth/registrar`  | `Usuario` (JSON)          | `201 Created` + usuario       |
| POST   | `/api/auth/login`      | `{ username, password }`  | `200 OK` + `{ "token": "..."}` |

**Flujo de registro:**
1. Verificar si el username ya existe → si existe, `400 Bad Request`.
2. Encodear password con `passwordEncoder.encode()`.
3. Guardar usuario.

**Flujo de login:**
1. Buscar usuario por username → si no existe, `401`.
2. Verificar password con `passwordEncoder.matches()` → si no coincide, `401`.
3. Generar JWT con `jwtUtil.generateToken(username, rol)`.
4. Devolver `{ "token": "eyJhbGciOiJIUzUxMiJ9..." }`.

---

## 2. API Gateway — JwtAuthenticationFilter

No está en `msvc-auth`, sino en el proyecto del **API Gateway** (Spring Cloud Gateway).

```java
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    @Value("${jwt.secret}")
    private String secret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Rutas públicas: /api/auth/** → pasar sin validar
        // Rutas protegidas: validar Bearer token
        // Si válido → extraer username y role del JWT
        // → mutar request agregando headers:
        //     X-User: <username>
        //     X-Role: ROLE_<role>
        //     Authorization: Bearer <jwt>
        // Si inválido → responder 401
    }

    @Override
    public int getOrder() { return -1; }
}
```

**Responsabilidades:**
- Ejecutarse antes que cualquier otro filtro (order = -1).
- Dejar pasar sin validar las rutas `/api/auth/**`.
- Para el resto, validar el token JWT del header `Authorization`.
- Si el token es válido, extraer `username` (subject) y `role` (claim).
- Propagar la identidad al microservicio destino mediante headers HTTP:
  - `X-User`: el nombre de usuario
  - `X-Role`: el rol con prefijo `ROLE_` (ej: `ROLE_ADMIN`)
  - `Authorization`: se reenvía igual (necesario para llamadas Feign entre servicios)

**Configuración de rutas (application.yml):**

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: msvc-auth
          uri: http://localhost:8003
          predicates:
            - Path=/api/auth/**
        - id: msvc-usuarios
          uri: http://localhost:8001
          predicates:
            - Path=/api/usuarios/**
        - id: msvc-cursos
          uri: http://localhost:8002
          predicates:
            - Path=/api/cursos/**
```

---

## 3. Servicios Internos — HeaderAuthFilter

Cada microservicio interno (ej: `msvc-usuarios`, `msvc-cursos`) necesita:

### HeaderAuthFilter (OncePerRequestFilter)

```java
@Component
public class HeaderAuthFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        String username = request.getHeader("X-User");
        String role = request.getHeader("X-Role");

        if (username != null && role != null) {
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
            Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
```

- Lee los headers `X-User` y `X-Role` inyectados por el gateway.
- Crea un `UsernamePasswordAuthenticationToken` y lo setea en el `SecurityContextHolder`.
- No valida JWTs — confía en que el gateway ya lo hizo.

### SecurityConfig del servicio interno

```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private HeaderAuthFilter headerAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .addFilterBefore(headerAuthFilter,
                UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

- `@EnableMethodSecurity` permite usar `@PreAuthorize` en los controllers.
- El `HeaderAuthFilter` se registra **antes** del filtro de autenticación por defecto de Spring Security.

### CustomAccessDeniedHandler (opcional)

```java
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        response.setStatus(403);
        response.setContentType("application/json");
        response.getWriter().write("{\"mensaje\": \"No tiene permiso\"}");
    }
}
```

---

## 4. Propagación en Llamadas Feign (Entre Servicios)

Si un servicio (ej: `msvc-cursos`) necesita llamar a otro (ej: `msvc-usuarios`) mediante Feign Client:

```java
@Configuration
public class FeignClientConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                template.header("Authorization", request.getHeader("Authorization"));
                template.header("X-User", request.getHeader("X-User"));
                template.header("X-Role", request.getHeader("X-Role"));
            }
        };
    }
}
```

- Toma los headers `Authorization`, `X-User` y `X-Role` del request HTTP actual y los propaga a las llamadas Feign salientes.
- Se vincula al `@FeignClient` mediante `configuration = FeignClientConfig.class`.

---

## 5. Configuración Compartida

Todos los servicios (auth, gateway, usuarios, cursos) deben tener en sus properties:

```properties
jwt.secret=clave.super.secreta.jwt.123
```

**El valor debe ser idéntico en todos.** El gateway usa este secret para validar los JWTs entrantes. El auth service lo usa para firmar los JWTs salientes.

---

## 6. Flujo Completo (Paso a Paso)

### Registro
```
Cliente → POST /api/auth/registrar → Gateway (ruta pública) → msvc-auth
  → valida que el usuario no exista
  → hashea password con BCrypt
  → guarda en BD
  → responde 201
```

### Login
```
Cliente → POST /api/auth/login → Gateway (ruta pública) → msvc-auth
  → busca usuario por username
  → verifica password con BCrypt
  → genera JWT (HS512, subject=username, claim=role, exp=1h)
  → responde { "token": "eyJ..." }
```

### Request autenticado a un recurso
```
Cliente → GET /api/usuarios con header "Authorization: Bearer <jwt>"
  → Gateway:
      - detecta que NO es ruta pública
      - valida el JWT (firma, expiración)
      - extrae username y role
      - agrega headers "X-User", "X-Role" y reenvía
  → msvc-usuarios:
      - HeaderAuthFilter lee X-User y X-Role
      - setea SecurityContext
      - el controller ejecuta la lógica (con @PreAuthorize si aplica)
```

### Llamada entre servicios via Feign
```
msvc-cursos → FeignClient → msvc-usuarios
  → FeignClientConfig toma headers del request actual (Authorization, X-User, X-Role)
  → los propaga al request saliente
  → msvc-usuarios recibe los headers y ejecuta su HeaderAuthFilter
```

---

## 7. Resumen de Componentes por Servicio

| Servicio         | Componentes clave                                              |
|------------------|---------------------------------------------------------------|
| **msvc-auth**    | `AuthController`, `JwtUtil`, `UsuarioRepository`, `SecurityConfig` (sin JWT filter) |
| **Gateway**      | `JwtAuthenticationFilter` (GlobalFilter), rutas en application.yml |
| **Servicio int.**| `HeaderAuthFilter`, `SecurityConfig` con `@EnableMethodSecurity` |
| **Servicio int.**| `FeignClientConfig` (RequestInterceptor) — solo si usa Feign  |
| **(todos)**      | `jwt.secret` en application.properties                        |

## 8. Consideraciones

- No hay `UserDetailsService` ni sesiones. La autenticación es manual en el controller de auth.
- Cada request es stateless. El `SecurityContext` se reconstruye desde los headers en cada llamada.
- El gateway es el punto único de validación de JWTs. Los servicios internos confían en la red interna y en los headers que inyecta el gateway.
- Los roles viajan dentro del JWT y se propagan como header. No hay consultas a BD por permisos en cada request.
