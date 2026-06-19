# Specification: recommendation-service MVP

## Overview
Implementar la estructura base del recommendation-service (Puerto 8085), un microservicio Spring Boot que provee recomendaciones personalizadas de productos. Para este MVP mínimo se implementa la estructura completa (entidades JPA/PostgreSQL, repositorios, clientes OpenFeign, controller) con un endpoint funcional que retorna recomendaciones básicas, sin algoritmo de recomendación avanzado.

## Restricciones de Código
- **Sin programación funcional**: No usar streams, lambdas, method references ni Optional chaining. Usar bucles for/while tradicionales e if/else explícitos.
- **Comentarios en español**: Todo el código debe tener comentarios explicativos en español donde sea necesario.
- **Servicios modularizados**: Separar la lógica en servicios independientes por responsabilidad (preferencias, cache, recomendación).

## User Stories
- **US-15**: Cliente ve sugerencias de productos basadas en historial de compras y usuarios similares (estructura base)

## Functional Requirements

### FR-1: Entidades JPA (PostgreSQL)
- `PerfilPreferencia` (@Entity): id (Long, auto), userId (Long, unique), dietaryPreferences (String, comma-separated), lastUpdated (LocalDateTime)
- `HistorialComprasCache` (@Entity): id (Long, auto), userId (Long, unique), lastUpdated (LocalDateTime)
  - `@OneToMany` a `OrdenCache` (productId, productName, purchasedAt)
- `RecomendacionResultado` (@Entity): id (Long, auto), userId (Long, unique), generatedAt (LocalDateTime)
  - `@OneToMany` a `ProductoRecomendado` (productId, productName, reason, dietaryCategory)

### FR-2: Repositorios JPA
- `PerfilPreferenciaRepository`: `Optional<PerfilPreferencia> findByUserId(Long userId)`
- `HistorialComprasCacheRepository`: `Optional<HistorialComprasCache> findByUserId(Long userId)`
- `RecomendacionCacheRepository`: `Optional<RecomendacionResultado> findByUserId(Long userId)`

### FR-3: DTOs (Records)
- `RecomendacionResponseDTO(Long userId, List<ProductoRecomendadoDTO> productos, LocalDateTime generatedAt)`
- `ProductoRecomendadoDTO(Long productId, String productName, String reason, String dietaryCategory)`
- `PerfilPreferenciaDTO(Long userId, List<String> dietaryPreferences)`
- DTOs compartidos para Feign: `UsuarioResponseDTO`, `OrdenDTO`, `ProductoDTO`

### FR-4: OpenFeign Clients (patrón kitchen-service)
Cada cliente sigue el patrón: Interface plana + Feign sub-interface (@Profile("!dev")) + Mock impl (@Profile("dev"))

- **UsuarioClient**: `GET /api/v1/usuarios/{id}` → obtener perfil de usuario (preferencias)
- **OrdenClient**: `GET /api/v1/ordenes/usuario/{userId}` → obtener historial de compras
- **ProductoClient**: `GET /api/v1/productos?categoria={cat}` → obtener productos por categoría dietaria

### FR-5: Endpoint REST
| Método | Path | Descripción |
|--------|------|-------------|
| GET | `/api/v1/recomendaciones/{userId}` | Obtener recomendaciones para un usuario |

**Flujo del endpoint:**
1. Verificar cache de recomendaciones en PostgreSQL (RecomendacionResultado)
2. Si cache válido (< 6 horas), retornar cache
3. Si no hay cache o expiró:
   a. Consultar UsuarioClient por preferencias
   b. Consultar OrdenClient por historial
   c. Consultar ProductoClient por productos de categorías afines
   d. Generar recomendaciones básicas (filtrar por preferencias dietarias, excluir comprados)
   e. Guardar en cache y retornar RecomendacionResponseDTO

### FR-6: Configuración
- PostgreSQL: `jdbc:postgresql://localhost:5435/recommendation_db` (user: root, sin password)
- Puerto: 8085
- Profile default: `dev` (mocks activos)
- TTL cache: 6 horas (configurable vía `recommendation.cache.ttl-hours`)

### FR-7: Docker Compose (patrón kitchen-service)
- `docker-compose.yml` local con container `recommendation-postgres` (postgres:16, puerto 5435:5432)
- `init.sql` con schema y datos de ejemplo
- Agregado al root docker-compose.yml vía `include`

## Non-Functional Requirements
- **NFR-1**: >80% test coverage (unit + integration)
- **NFR-2**: Swagger/OpenAPI documentation en `/api/v1/recomendaciones/v3/api-docs`
- **NFR-3**: Comentarios en español en todo el código
- **NFR-4**: Sin programación funcional (no streams, no lambdas)

## Acceptance Criteria
- [ ] El servicio compila y arranca sin errores (`mvn clean compile`)
- [ ] `GET /api/v1/recomendaciones/{userId}` retorna recomendaciones en formato RecomendacionResponseDTO
- [ ] En profile `dev`, los Feign clients usan mocks (no requieren otros servicios corriendo)
- [ ] En profile `!dev`, los Feign clients llaman a los servicios reales vía HTTP
- [ ] Cache en PostgreSQL funciona: segunda llamada dentro de 6hs retorna datos cacheados
- [ ] Unit tests para servicios pasan
- [ ] Integration tests para el endpoint REST pasan
- [ ] docker-compose.yml levanta PostgreSQL dedicado (`recommendation-postgres`)
- [ ] Swagger UI accesible
- [ ] Código sin streams/lambdas, con comentarios en español

## Out of Scope
- Algoritmo de collaborative filtering avanzado
- UI/Frontend integration
- Autenticación/autorización (JWT)
- Circuit breakers / fallbacks avanzados
- PUT/POST endpoints para gestionar preferencias (eso es user-service)
