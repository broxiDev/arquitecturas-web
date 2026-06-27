# Guía de Integración con Auth-Service (JWT)

Este documento describe qué necesita implementar cada microservicio para integrarse con el sistema de
autenticación JWT centralizado en `auth-service`.

## Arquitectura General

```
                    ┌─────────────────────┐
                    │    API GATEWAY       │
                    │  JwtAuthentication   │
                    │  Filter (Global)     │
                    └──────┬──┬──┬─────────┘
                           │  │  │
         ┌─────────────────┘  │  └──────────────────┐
         ▼                    ▼                     ▼
 ┌────────────────┐   ┌──────────────┐   ┌──────────────────┐
 │  auth-service   │   │  Servicios   │   │  Servicios sin   │
 │  (token issuer) │   │  con Feign   │   │  Feign           │
 │  Sin JWT filter │   │  HeaderAuth  │   │  HeaderAuth      │
 │                 │   │  Filter      │   │  Filter          │
 │                 │   │  + FeignCfg  │   │  (solo filter)   │
 └────────────────┘   └──────────────┘   └──────────────────┘
```

- **API Gateway** es el único punto de entrada. Valida todos los JWTs entrantes, extrae la identidad
  del usuario, y la propaga como headers HTTP a los servicios internos.
- **auth-service** solo emite tokens. No valida tokens entrantes porque confía en el gateway.
- **Servicios internos** no validan JWTs. Confían en los headers `X-User` y `X-Role` que inyecta el gateway.

---

## 1. API GATEWAY

### Dependencias a agregar en `pom.xml`

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

### Archivos a crear

```
api-gateway/src/main/java/com/farmacyfood/gateway/
├── config/
│   └── JwtUtil.java
└── filter/
    └── JwtAuthenticationFilter.java
```

### `config/JwtUtil.java`

```java
package com.farmacyfood.gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public boolean validate(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
```

### `filter/JwtAuthenticationFilter.java`

```java
package com.farmacyfood.gateway.filter;

import com.farmacyfood.gateway.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    private static final String[] PUBLIC_PATHS = { "/api/auth/" };

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Rutas públicas pasan sin validar
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                return chain.filter(exchange);
            }
        }

        // Extraer token del header Authorization
        String authHeader = exchange.getRequest().getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validate(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        // Mutar request agregando headers de identidad
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(r -> r.header("X-User", username)
                        .header("X-Role", role))
                .build();

        return chain.filter(mutatedExchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
```

### Modificar `application.yml`

Agregar al final:
```yaml
jwt:
  secret: clave.super.secreta.jwt.123
```

Agregar ruta de auth-service en `spring.cloud.gateway.routes`:
```yaml
spring:
  cloud:
    gateway:
      routes:
        # ... rutas existentes ...
        - id: auth-service
          uri: lb://auth-service
          predicates:
            - Path=/api/auth/**
```

---

## 2. SERVICIOS INTERNOS CON FEIGN

Aplica a: **user-service**, **order-service**, **fridge-service**, **kitchen-service**, **recommendation-service**

### Dependencias a agregar en `pom.xml`

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### Archivos a crear

```
src/main/java/com/farmacyfood/{domain}/
├── config/
│   ├── SecurityConfig.java
│   └── FeignClientConfig.java
└── filter/
    └── HeaderAuthFilter.java
```

### `filter/HeaderAuthFilter.java`

```java
package com.farmacyfood.{domain}.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class HeaderAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String username = request.getHeader("X-User");
        String role = request.getHeader("X-Role");

        if (username != null && role != null) {
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
```

### `config/SecurityConfig.java`

```java
package com.farmacyfood.{domain}.config;

import com.farmacyfood.{domain}.filter.HeaderAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private HeaderAuthFilter headerAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .addFilterBefore(headerAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

### `config/FeignClientConfig.java`

```java
package com.farmacyfood.{domain}.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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

### Modificar `application.yml`

Agregar al final:
```yaml
jwt:
  secret: clave.super.secreta.jwt.123
```

---

## 3. SERVICIOS INTERNOS SIN FEIGN

Aplica a: **product-service**, **notification-service**, **audit-service**

Ídem sección 2 pero **sin** `FeignClientConfig` y **sin** `spring-cloud-starter-openfeign`.

Solo necesitan:
1. Agregar `spring-boot-starter-security` al `pom.xml`
2. Crear `HeaderAuthFilter.java` (el mismo código)
3. Crear `SecurityConfig.java` (el mismo código)
4. Agregar `jwt.secret` al `application.yml`

---

## 4. USER-SERVICE — Endpoint de perfil post-registro

Además de la seguridad, user-service necesita exponer un endpoint para que el frontend cree el
perfil del usuario después de registrarse en auth-service.

### Modificar `User.java`

Agregar campo de referencia:
```java
@Column(nullable = false, unique = true)
private String authUsername;
```

### Endpoint en `UserController.java`

```java
@PostMapping
public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRegistrationRequest request) {
    // request contiene: { authUsername, name, email, ... }
    // Crear User con authUsername como identificador de referencia
}
```

### DTO `UserRegistrationRequest.java`

```java
public record UserRegistrationRequest(
    @NotBlank String authUsername,
    @NotBlank String name,
    @NotBlank String email
    // resto de campos existentes
) {}
```

### Flujo de registro completo

```
Frontend
  │
  ├── 1. POST /api/auth/registrar { username, password, rol, name, email }
  │      → Gateway (ruta pública, sin validar JWT)
  │      → auth-service crea AuthUser, responde 201
  │
  └── 2. POST /api/usuarios { authUsername, name, email }
         → Gateway (con JWT del paso 1 en Authorization header)
         → gateway valida JWT, agrega X-User, X-Role
         → user-service: HeaderAuthFilter setea SecurityContext
         → crea User con authUsername como referencia
         → responde 201
```

---

## 5. CONSIDERACIONES GENERALES

- Todos los servicios deben usar el **mismo valor** de `jwt.secret`. Si difieren, los tokens
  serán rechazados por el gateway.
- Ningún servicio interno necesita la dependencia `jjwt`. Solo auth-service y api-gateway
  la requieren.
- Los roles viajan dentro del JWT y se propagan como header `X-Role`. No hay consultas a BD
  por permisos en cada request.
- El `@EnableMethodSecurity` permite usar `@PreAuthorize` en los controllers de servicios
  internos, ej: `@PreAuthorize("hasAuthority('adminDeHeladera')")`.
