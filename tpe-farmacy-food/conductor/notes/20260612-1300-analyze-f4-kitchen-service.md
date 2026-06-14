# Análisis F4 — Planificación de Cocina (kitchen-service)

**Responsable:** Nahue  
**Microservicio:** kitchen-service  
**Puerto:** 8084  
**Base path:** `/api/v1/cocina`

---

## User Stories Cubiertas

- **US-12**: Admin de cocina recibe reporte diario de producción sugerida basado en ventas históricas, stock remanente y tendencias
- **US-13**: Admin de cocina ve historial de ventas por producto y por heladera

## Entidades

### DailyPlan (PostgreSQL — kitchen-service DB)

| Campo       | Tipo            | Restricciones                     |
|-------------|-----------------|-----------------------------------|
| id          | Long            | PK, auto-generated                |
| date        | LocalDate       | @NotNull, unique                  |
| createdAt   | LocalDateTime   | Auto, no modificable              |
| items       | List<PlanItem>  | @OneToMany(cascade=ALL)           |

### PlanItem (PostgreSQL — kitchen-service DB)

| Campo            | Tipo            | Restricciones                      |
|------------------|-----------------|------------------------------------|
| id               | Long            | PK, auto-generated                 |
| dailyPlanId      | Long            | FK → DailyPlan.id                  |
| productId        | Long            | @NotNull — referencia a product-service |
| productName      | String          | @NotBlank — desnormalizado         |
| suggestedQuantity| Integer          | @NotNull, @Min(1)                  |

**Notas:**
- `productName` está desnormalizado para evitar llamada a product-service cada vez que se consulta el plan.
- La combinación (dailyPlanId, productId) debe ser única → `@UniqueConstraint`.

### VentaHistorica (MongoDB — kitchen-service DB)

| Campo       | Tipo     | Descripción                                    |
|-------------|----------|------------------------------------------------|
| _id         | ObjectId | Auto-generated                                 |
| productId   | Long     | Referencia a product-service                   |
| productName | String   | Nombre del producto (desnormalizado)           |
| fridgeId    | Long     | Referencia a fridge-service                   |
| quantity    | Integer  | Cantidad vendida en ese registro              |
| totalAmount | BigDecimal| Monto total de esa venta                     |
| date        | LocalDate| Fecha de la venta                             |

**Notas:**
- MongoDB para datos históricos agregados: permite queries flexibles y escalabilidad para reportes.
- Los datos se sincronizan desde order-service periódicamente (o por evento).

## DTOs (Records)

- `PlanDiarioResponseDTO(Long id, LocalDate date, List<ItemPlanDTO> items, LocalDateTime createdAt)`
- `ItemPlanDTO(Long productId, String productName, Integer suggestedQuantity)`
- `VentaHistoricaResponseDTO(Long productId, String productName, Long fridgeId, Integer quantity, BigDecimal totalAmount, LocalDate date)`
- `VentasResumenDTO(List<ProductoVentaDTO> productos, LocalDate from, LocalDate to)`
- `ProductoVentaDTO(Long productId, String productName, Integer totalVendido, BigDecimal totalMonto)`

## Endpoints REST (kitchen-service)

| Método | Path                                         | Descripción                                    | US    |
|--------|----------------------------------------------|------------------------------------------------|-------|
| GET    | `/api/v1/cocina/plan-diario`                 | Obtener plan diario para hoy                   | US-12 |
| GET    | `/api/v1/cocina/plan-diario?fecha=YYYY-MM-DD`| Obtener plan diario para fecha específica      | US-12 |
| POST   | `/api/v1/cocina/plan-diario?fecha=YYYY-MM-DD`| Generar/actualizar plan diario                 | US-12 |
| GET    | `/api/v1/cocina/historial-ventas`            | Ver historial de ventas con filtros            | US-13 |
| GET    | `/api/v1/cocina/historial-ventas?from=&to=`  | Filtrar por rango de fechas                    | US-13 |
| GET    | `/api/v1/cocina/historial-ventas?productId=` | Filtrar por producto                           | US-13 |
| GET    | `/api/v1/cocina/historial-ventas?fridgeId=`  | Filtrar por heladera                           | US-13 |

### Detalle de POST /api/v1/cocina/plan-diario

- **Flujo:**
  1. Consultar order-service vía OpenFeign por ventas de los últimos N días
  2. Calcular sugerencia de producción basada en promedio de ventas, ajustado por tendencia
  3. Almacenar en PostgreSQL como DailyPlan con PlanItems
  4. Retornar `PlanDiarioResponseDTO`
- **Si ya existe plan para esa fecha:** sobrescribir (PUT semántico via POST)

### Detalle de GET /api/v1/cocina/plan-diario

- Buscar DailyPlan en PostgreSQL por fecha
- Si no existe y se solicita fecha de hoy, puede generar automáticamente o retornar 404

### Detalle de GET /api/v1/cocina/historial-ventas

- Query en MongoDB con filtros opcionales: `from`, `to`, `productId`, `fridgeId`
- Retorna lista de `VentaHistoricaResponseDTO` o resumen agregado `VentasResumenDTO`

## Integraciones

### Integración Saliente: kitchen-service → order-service (OpenFeign)

- **Propósito:** Obtener historial de ventas para calcular plan de producción
- **Endpoint consumido:** `GET /api/v1/ordenes` con parámetros de fecha (o endpoint específico de historial que order-service deba exponer)
- **Client:** `OrdenClient.java` en package `client/`
- **Nota:** order-service necesita exponer un endpoint de consulta de ventas para kitchen-service. Sugerido: `GET /api/v1/ordenes/historial-ventas?from=&to=` que retorne datos agregados.

### Integración Saliente: kitchen-service → product-service (OpenFeign, opcional)

- **Propósito:** Obtener nombre de producto al generar plan (si no está cacheado)
- **Client:** `ProductoClient.java` en package `client/`
- **Alternativa:** El nombre se puede obtener al sincronizar datos y desnormalizar

## Estrategia de Doble Base de Datos

- **PostgreSQL:** Datos transaccionales (DailyPlan, PlanItem) — para el plan de producción diario
- **MongoDB:** Datos históricos (VentaHistorica) — para análisis y tendencias

**Configuración de doble datasource:**
- Se necesitará configurar dos datasources en application.yml
- PostgreSQL como primary datasource (para JPA estándar)
- MongoDB como secondary (usando Spring Data MongoDB)

**Dependencias Maven adicionales:**
- `spring-boot-starter-data-mongodb`
- MongoDB connection ya disponible en docker-compose (`mongodb:7` en puerto 27017)

## Estructura de Paquetes

```
com.farmacyfood.kitchen/
├── KitchenServiceApplication.java
├── controller/
│   ├── PlanDiarioController.java
│   └── HistorialVentasController.java
├── service/
│   ├── PlanDiarioService.java
│   ├── PlanDiarioServiceImpl.java
│   ├── HistorialVentasService.java
│   └── HistorialVentasServiceImpl.java
├── repository/
│   ├── PlanDiarioRepository.java       # JPA (PostgreSQL)
│   ├── ItemPlanRepository.java         # JPA (PostgreSQL)
│   └── VentaHistoricaRepository.java   # MongoDB
├── entity/
│   ├── postgres/
│   │   ├── PlanDiario.java
│   │   └── ItemPlan.java
│   └── mongo/
│       └── VentaHistorica.java
├── dto/
│   ├── PlanDiarioResponseDTO.java
│   ├── ItemPlanDTO.java
│   ├── VentaHistoricaResponseDTO.java
│   ├── VentasResumenDTO.java
│   └── ProductoVentaDTO.java
├── client/
│   ├── OrdenClient.java                # OpenFeign
│   └── ProductoClient.java             # OpenFeign (opcional)
├── exception/
│   ├── PlanNotFoundException.java
│   └── GlobalExceptionHandler.java
└── config/
    ├── SwaggerConfig.java
    ├── FeignConfig.java
    ├── PostgresConfig.java              # Configuración datasource PostgreSQL
    └── MongoConfig.java                 # Configuración MongoDB
```

## Dependencias Maven a Agregar

- `spring-boot-starter-data-jpa`
- `spring-boot-starter-data-mongodb`
- `postgresql` (driver)
- `spring-boot-starter-validation`
- `spring-cloud-starter-openfeign`
- Ya tiene: `spring-boot-starter-web`, `spring-cloud-starter-netflix-eureka-client`, `springdoc`, `lombok`

## Configuración application.yml

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
  data:
    mongodb:
      uri: mongodb://localhost:27017/farmacyfood_kitchen
```

## Decisiones Tomadas

1. **Doble base de datos:** PostgreSQL para datos transaccionales (planes diarios), MongoDB para datos históricos agregados. Esto sigue el patrón "database per service" donde kitchen-service tiene su propia DB, y usa PostgreSQL+Mongo según naturaleza de datos.
2. **Desnormalización:** `productName` se copia desde product-service al sincronizar datos, evitando llamadas frecuentes.
3. **Generación de plan:** El plan se genera bajo demanda (POST) o se puede consultar si ya existe (GET). No se auto-genera para MVP.
4. **Endpoint de historial en order-service:** Es necesario que order-service exponga un endpoint de ventas para que kitchen-service pueda obtener datos. Esto es un contrato que debe coordinarse con Fiore.
5. **Nomenclatura en español:** Endpoints (`/plan-diario`, `/historial-ventas`), DTOs y entities en español.