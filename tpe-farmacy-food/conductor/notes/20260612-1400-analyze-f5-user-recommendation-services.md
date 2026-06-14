# Análisis F5 — Preferencias y Recomendaciones (user-service + recommendation-service)

**Responsables:** Mati (user-service) + Nahue (recommendation-service)  
**Microservicio:** user-service (puerto 8086) + recommendation-service (puerto 8085)  
**Base paths:** `/api/v1/usuarios` (user-service) y `/api/v1/recomendaciones` (recommendation-service)

---

## User Stories Cubiertas

- **US-14**: Cliente registra preferencias dietarias al registrarse (tipo de dieta, alergias, productos favoritos)
- **US-15**: Cliente ve sugerencias de productos basadas en historial de compras y usuarios similares

---

## Parte A: user-service (Mati)

### Entidades (PostgreSQL — user-service DB)

#### User

| Campo               | Tipo            | Restricciones                              |
|---------------------|-----------------|-------------------------------------------|
| id                  | Long            | PK, auto-generated                        |
| name                | String          | @NotBlank, max 150                        |
| email               | String          | @NotBlank, @Email, unique                  |
| passwordHash        | String          | @NotBlank — hash BCrypt                    |
| dietaryPreferences  | String          | Lista separada por comas o JSON           |
| createdAt           | LocalDateTime   | Auto, no modificable                      |
| updatedAt           | LocalDateTime   | Auto                                      |

**Notas sobre `dietaryPreferences`:**
- Para MVP, se almacena como String con valores separados por comas (ej: "VEGANO,SIN_GLUTEN")
- Futuro: migrar a tabla separada `UserDietaryPreference` si se necesita más granularidad
- Solo se establece al registrarse (US-14 nota: "para la primera entrega asumimos que esto solo se podrá hacer al registrarse")

### DTOs (Records) — user-service

- `UsuarioRegistroDTO(String name, String email, String password, String dietaryPreferences)`
- `UsuarioUpdatePreferenciasDTO(String dietaryPreferences)`
- `UsuarioResponseDTO(Long id, String name, String email, String dietaryPreferences, LocalDateTime createdAt)`

### Endpoints REST (user-service)

| Método | Path                                    | Descripción                                     | US    |
|--------|-----------------------------------------|------------------------------------------------|-------|
| POST   | `/api/v1/usuarios/registro`             | Registrar usuario con preferencias dietarias    | US-14 |
| GET    | `/api/v1/usuarios/{id}`                  | Obtener perfil de usuario                      | US-14 |
| PUT    | `/api/v1/usuarios/{id}/preferencias`     | Actualizar preferencias dietarias              | US-14 |
| GET    | `/api/v1/usuarios/{id}/historial`        | Obtener historial de compras (proxy a order-service) | US-15 |

### Detalle de POST /api/v1/usuarios/registro

- **Body:** `UsuarioRegistroDTO`
- **Flujo:**
  1. Validar email único
  2. Hashear contraseña con BCrypt
  3. Persistir usuario con preferencias
  4. Retornar 201 Created con `UsuarioResponseDTO`
- **Nota:** Email y password no se retornan en respuesta normal (password nunca).

### Detalle de PUT /api/v1/usuarios/{id}/preferencias

- **Body:** `UsuarioUpdatePreferenciasDTO`
- **Nota:** Aunque para MVP solo se setea al registrar, exponemos PUT para permitir actualización

### Detalle de GET /api/v1/usuarios/{id}/historial

- **Propiedad:** Este endpoint es un proxy — consulta order-service vía OpenFeign y retorna las órdenes del usuario
- **Respuesta:** Lista de órdenes (DTOs de order-service) o wrapper propio
- **Client:** `OrdenClient.java` consume `GET /api/v1/ordenes/usuario/{userId}` de order-service

### Integraciones (user-service)

#### Integración Saliente: user-service → order-service (OpenFeign)

- **Propósito:** Obtener historial de compras del usuario
- **Endpoint consumido:** `GET /api/v1/ordenes/usuario/{userId}`
- **Client:** `OrdenClient.java`

### Estructura de Paquetes (user-service)

```
com.farmacyfood.user/
├── UserServiceApplication.java
├── controller/
│   └── UsuarioController.java
├── service/
│   ├── UsuarioService.java
│   └── UsuarioServiceImpl.java
├── repository/
│   └── UsuarioRepository.java
├── entity/
│   └── Usuario.java
├── dto/
│   ├── UsuarioRegistroDTO.java
│   ├── UsuarioUpdatePreferenciasDTO.java
│   └── UsuarioResponseDTO.java
├── client/
│   └── OrdenClient.java              # OpenFeign
├── exception/
│   ├── UsuarioNotFoundException.java
│   ├── EmailDuplicadoException.java
│   └── GlobalExceptionHandler.java
└── config/
    ├── SwaggerConfig.java
    └── FeignConfig.java
```

### Dependencias Maven a Agregar (user-service)

- `spring-boot-starter-data-jpa`
- `postgresql` (driver)
- `spring-boot-starter-validation`
- `spring-cloud-starter-openfeign`
- Ya tiene: `spring-boot-starter-web`, `spring-cloud-starter-netflix-eureka-client`, `springdoc`, `lombok`

### Configuración application.yml (user-service)

Agregar:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/farmacyfood
    username: farmacyfood
    password: farmacyfood
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

---

## Parte B: recommendation-service (Nahue)

### Entidades (MongoDB — recommendation-service DB)

#### PerfilPreferencia (MongoDB)

| Campo               | Tipo         | Descripción                              |
|---------------------|--------------|------------------------------------------|
| _id                 | ObjectId     | Auto-generated                           |
| userId              | Long         | Referencia a user-service               |
| dietaryPreferences  | List<String> | Preferencias del usuario                |
| lastUpdated         | LocalDateTime| Última actualización del perfil          |

#### HistorialComprasCache (MongoDB)

| Campo       | Tipo         | Descripción                              |
|-------------|--------------|------------------------------------------|
| _id         | ObjectId     | Auto-generated                           |
| userId      | Long         | Referencia a user-service               |
| orders      | List<OrdenDTO>| Resumen cacheado de compras             |
| lastUpdated | LocalDateTime| Última sincronización                    |

#### RecomendacionResultado (MongoDB) — cache

| Campo              | Tipo          | Descripción                             |
|--------------------|---------------|-----------------------------------------|
| _id                | ObjectId      | Auto-generated                          |
| userId             | Long          | Referencia a user-service               |
| recommendedProducts| List<Long>    | IDs de productos recomendados           |
| reasons            | List<String>  | Razones de recomendación                |
| generatedAt        | LocalDateTime | Timestamp de generación                 |

**Notas:**
- MongoDB es ideal para el recommendation-service porque los datos son flexibles (preferencias, historial cacheado) y no requieren relaciones complejas.
- El cache de recomendaciones tiene TTL: se regenera si es más antiguo que X horas.

### DTOs (Records) — recommendation-service

- `RecomendacionResponseDTO(Long userId, List<ProductoRecomendadoDTO> productos, LocalDateTime generatedAt)`
- `ProductoRecomendadoDTO(Long productId, String productName, String reason, String dietaryCategory)`
- `PerfilPreferenciaDTO(Long userId, List<String> dietaryPreferences)`

### Endpoints REST (recommendation-service)

| Método | Path                                    | Descripción                              | US    |
|--------|-----------------------------------------|------------------------------------------|-------|
| GET    | `/api/v1/recomendaciones/{userId}`      | Obtener recomendaciones para un usuario  | US-15 |

### Detalle de GET /api/v1/recomendaciones/{userId}

- **Flujo:**
  1. Verificar cache de recomendaciones para userId (en MongoDB)
  2. Si cache válido (menos de N horas), retornar cache
  3. Si no hay cache o expiró:
     a. Consultar user-service vía OpenFeign por preferencias: `GET /api/v1/usuarios/{userId}`
     b. Consultar order-service vía OpenFeign por historial: `GET /api/v1/ordenes/usuario/{userId}`
     c. Buscar usuarios con preferencias similares y obtener sus productos comprados
     d. Generar recomendaciones basadas en:
        - Productos de la misma categoría dietaria que le gustan
        - Productos populares entre usuarios similares
        - Productos nuevos en categorías de preferencia
     e. Guardar en cache y retornar
- **Respuesta:** `RecomendacionResponseDTO`

### Algoritmo de Recomendación (MVP)

Para la primera entrega, se usa un algoritmo simple:
1. Obtener preferencias dietarias del usuario
2. Filtrar productos de esas categorías
3. Excluir productos ya comprados por el usuario
4. Ordenar por popularidad (cuántos usuarios similares lo compraron)
5. Retornar top N (ej: top 5)

### Integraciones (recommendation-service)

#### Integración Saliente: recommendation-service → user-service (OpenFeign)

- **Propósito:** Obtener preferencias del usuario
- **Endpoint consumido:** `GET /api/v1/usuarios/{id}`
- **Client:** `UsuarioClient.java`

#### Integración Saliente: recommendation-service → order-service (OpenFeign)

- **Propósito:** Obtener historial de compras del usuario y de todos los usuarios (para matriz de similitud)
- **Endpoint consumido:** `GET /api/v1/ordenes/usuario/{userId}` y potencialmente `GET /api/v1/ordenes` para obtener todos los datos
- **Client:** `OrdenClient.java`

#### Integración Saliente: recommendation-service → product-service (OpenFeign)

- **Propósito:** Obtener detalles de productos para enriquecer recomendaciones (nombre, categoría)
- **Endpoint consumido:** `GET /api/v1/productos?categoria={cat}`
- **Client:** `ProductoClient.java`

### Estructura de Paquetes (recommendation-service)

```
com.farmacyfood.recommendation/
├── RecommendationServiceApplication.java
├── controller/
│   └── RecomendacionController.java
├── service/
│   ├── RecomendacionService.java
│   └── RecomendacionServiceImpl.java
├── repository/
│   ├── PerfilPreferenciaRepository.java    # MongoDB
│   ├── HistorialComprasCacheRepository.java # MongoDB
│   └── RecomendacionCacheRepository.java    # MongoDB
├── entity/
│   ├── PerfilPreferencia.java
│   ├── HistorialComprasCache.java
│   └── RecomendacionResultado.java
├── dto/
│   ├── RecomendacionResponseDTO.java
│   ├── ProductoRecomendadoDTO.java
│   └── PerfilPreferenciaDTO.java
├── client/
│   ├── UsuarioClient.java                  # OpenFeign
│   ├── OrdenClient.java                    # OpenFeign
│   └── ProductoClient.java                 # OpenFeign
├── exception/
│   ├── UsuarioNotFoundException.java
│   └── GlobalExceptionHandler.java
└── config/
    ├── SwaggerConfig.java
    └── FeignConfig.java
```

### Dependencias Maven a Agregar (recommendation-service)

- `spring-boot-starter-data-mongodb`
- `spring-boot-starter-validation`
- `spring-cloud-starter-openfeign`
- Ya tiene: `spring-boot-starter-web`, `spring-cloud-starter-netflix-eureka-client`, `springdoc`, `lombok`

### Configuración application.yml (recommendation-service)

Agregar:
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/farmacyfood_recommendation
```

---

## Decisiones Tomadas (F5 completo)

1. **user-service como servicio separado:** Aunque comparte integrante (Mati) con notification-service y api-gateway, es un microservicio independiente con su propia base de datos PostgreSQL.
2. **dietaryPreferences como String en MVP:** Para simplificar, se usa String separado por comas. Migración a tabla separada en futura iteración.
3. **historial como proxy:** `GET /api/v1/usuarios/{id}/historial` es un proxy a order-service, no duplica datos.
4. **recommendation-service solo con MongoDB:** No necesita PostgreSQL. Perfiles, historial cacheado y recomendaciones usan MongoDB con esquemas flexibles.
5. **Cache de recomendaciones:** Se almacenan en MongoDB con TTL. Para MVP, se regenera cada vez que se solicita si no hay cache.
6. **Algoritmo simple para MVP:** Basado en preferencias dietarias y exclusión de productos ya comprados. Se puede mejorar a collaborative filtering en iteraciones futuras.
7. **3 OpenFeign clients en recommendation-service:** Depende de user-service, order-service y product-service.
8. **Nomenclatura en español:** Endpoints (`/registro`, `/preferencias`, `/historial`, `/recomendaciones`), DTOs y entities en español.