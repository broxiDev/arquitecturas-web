# Implementation Plan: recommendation-service MVP

## Phase 1: Setup & Infrastructure

- [ ] Task: Update pom.xml with required dependencies
    - [ ] Add `spring-boot-starter-data-jpa`
    - [ ] Add `org.postgresql:postgresql` (runtime scope)
    - [ ] Add `spring-boot-starter-validation`
    - [ ] Add `spring-cloud-starter-openfeign`
    - [ ] Add `com.h2database:h2` (test scope, for integration tests)

- [ ] Task: Create docker-compose.yml (patrón kitchen-service)
    - [ ] Define `recommendation-postgres` container (postgres:16, puerto 5435:5432, db: recommendation_db, user: root, sin password)
    - [ ] Crear `init.sql` con schema y datos de ejemplo
    - [ ] Configurar volume `recommendation_postgres_data`
    - [ ] Agregar include en root docker-compose.yml

- [ ] Task: Update application.yml
    - [ ] Agregar datasource PostgreSQL (`jdbc:postgresql://localhost:5435/recommendation_db`, user: root, sin password)
    - [ ] Configurar JPA: `ddl-auto: update`, dialect PostgreSQL, `open-in-view: false`
    - [ ] Set default profile a `dev`
    - [ ] Agregar `recommendation.cache.ttl-hours: 6`
    - [ ] Agregar Feign client URL properties

- [ ] Task: Update RecommendationServiceApplication.java
    - [ ] Agregar `@EnableFeignClients`

- [ ] Task: Conductor - User Manual Verification 'Setup & Infrastructure' (Protocol in workflow.md)

## Phase 2: Domain Layer (Entidades JPA, Repositorios, DTOs)

- [ ] Task: Crear entidades JPA en paquete `entity/`
    - [ ] `PerfilPreferencia` (@Entity, @Table): id, userId (unique), dietaryPreferences (String), lastUpdated
    - [ ] `HistorialComprasCache` (@Entity, @Table): id, userId (unique), lastUpdated + @OneToMany a OrdenCache
    - [ ] `OrdenCache` (@Entity, @Table): id, productId, productName, purchasedAt + @ManyToOne a HistorialComprasCache
    - [ ] `RecomendacionResultado` (@Entity, @Table): id, userId (unique), generatedAt + @OneToMany a ProductoRecomendado
    - [ ] `ProductoRecomendado` (@Entity, @Table): id, productId, productName, reason, dietaryCategory + @ManyToOne a RecomendacionResultado

- [ ] Task: Crear repositorios JPA en paquete `repository/`
    - [ ] `PerfilPreferenciaRepository extends JpaRepository<PerfilPreferencia, Long>` con `findByUserId`
    - [ ] `HistorialComprasCacheRepository extends JpaRepository<HistorialComprasCache, Long>` con `findByUserId`
    - [ ] `RecomendacionCacheRepository extends JpaRepository<RecomendacionResultado, Long>` con `findByUserId`

- [ ] Task: Crear DTOs (records) en paquete `dto/`
    - [ ] `RecomendacionResponseDTO`, `ProductoRecomendadoDTO`, `PerfilPreferenciaDTO`
    - [ ] DTOs para Feign: `UsuarioResponseDTO`, `OrdenDTO`, `ProductoDTO`

- [ ] Task: Conductor - User Manual Verification 'Domain Layer' (Protocol in workflow.md)

## Phase 3: Client Layer (OpenFeign + Mocks)

- [ ] Task: Crear UsuarioClient (interface + Feign + Mock)
    - [ ] `UsuarioClient.java` — interface: `UsuarioResponseDTO getUsuario(Long id)`
    - [ ] `UsuarioClientFeign.java` — `@FeignClient(name="user-service")`, `@Profile("!dev")`, extends UsuarioClient
    - [ ] `UsuarioClientMockImpl.java` — `@Component`, `@Profile("dev")`, datos mock

- [ ] Task: Crear OrdenClient (interface + Feign + Mock)
    - [ ] `OrdenClient.java` — interface: `List<OrdenDTO> getOrdenesByUsuario(Long userId)`
    - [ ] `OrdenClientFeign.java` — `@FeignClient(name="order-service")`, `@Profile("!dev")`, extends OrdenClient
    - [ ] `OrdenClientMockImpl.java` — `@Component`, `@Profile("dev")`, datos mock

- [ ] Task: Crear ProductoClient (interface + Feign + Mock)
    - [ ] `ProductoClient.java` — interface: `List<ProductoDTO> getProductosByCategoria(String categoria)`
    - [ ] `ProductoClientFeign.java` — `@FeignClient(name="product-service")`, `@Profile("!dev")`, extends ProductoClient
    - [ ] `ProductoClientMockImpl.java` — `@Component`, `@Profile("dev")`, datos mock

- [ ] Task: Conductor - User Manual Verification 'Client Layer' (Protocol in workflow.md)

## Phase 4: Service Layer (Lógica de Negocio + Unit Tests)

- [ ] Task: Escribir unit tests para servicios (sin streams/lambdas)
    - [ ] Test: retorna recomendaciones cacheadas cuando cache es válido (< 6hs)
    - [ ] Test: regenera recomendaciones cuando cache expiró
    - [ ] Test: regenera recomendaciones cuando no hay cache
    - [ ] Test: excluye productos ya comprados
    - [ ] Test: filtra productos por preferencias dietarias

- [ ] Task: Implementar PreferenciaService (módulo preferencias)
    - [ ] `PreferenciaService.java` — interface: obtener preferencias del usuario (vía UsuarioClient + cache local)
    - [ ] `PreferenciaServiceImpl.java` — implementación con comentarios en español, sin streams

- [ ] Task: Implementar CacheService (módulo cache)
    - [ ] `CacheService.java` — interface: verificar/guardar cache de recomendaciones e historial
    - [ ] `CacheServiceImpl.java` — implementación con manejo de TTL, sin streams

- [ ] Task: Implementar RecomendacionService (módulo recomendación)
    - [ ] `RecomendacionService.java` — interface: `RecomendacionResponseDTO getRecomendaciones(Long userId)`
    - [ ] `RecomendacionServiceImpl.java` — orquesta PreferenciaService + CacheService + clients
        - For loops tradicionales para filtrar, excluir y ordenar
        - Comentarios en español explicando cada paso

- [ ] Task: Crear excepciones custom
    - [ ] `UsuarioNotFoundException`
    - [ ] `GlobalExceptionHandler` (@RestControllerAdvice) con mensajes en español

- [ ] Task: Conductor - User Manual Verification 'Service Layer' (Protocol in workflow.md)

## Phase 5: Controller Layer (REST Endpoint + Integration Tests)

- [ ] Task: Escribir integration tests para RecomendacionController (con H2)
    - [ ] Test: GET /api/v1/recomendaciones/{userId} retorna 200 con body válido
    - [ ] Test: GET /api/v1/recomendaciones/{userId} usa cache en segunda llamada
    - [ ] Test: Swagger endpoint accesible

- [ ] Task: Implementar RecomendacionController
    - [ ] Reemplazar/actualizar HealthController con `RecomendacionController`
    - [ ] `GET /api/v1/recomendaciones/{userId}` → llama a RecomendacionService
    - [ ] Anotaciones Swagger en español (@Tag, @Operation, @ApiResponse)

- [ ] Task: Conductor - User Manual Verification 'Controller Layer' (Protocol in workflow.md)

## Phase 6: Final Verification

- [ ] Task: Verificar compilación completa (`mvn clean compile`)
- [ ] Task: Ejecutar todos los tests y verificar >80% coverage (`mvn test`)
- [ ] Task: Verificar que docker-compose levanta PostgreSQL correctamente
- [ ] Task: Verificar que servicio arranca en profile dev y endpoints responden
- [ ] Task: Crear nota de resumen en `conductor/notes/`
- [ ] Task: Conductor - User Manual Verification 'Final Verification' (Protocol in workflow.md)
