# Análisis F2 — Gestión de Heladeras (fridge-service)

**Responsable:** Ale  
**Microservicio:** fridge-service  
**Puerto:** 8082  
**Base path:** `/api/v1/heladeras`

---

## User Stories Cubiertas

- **US-04**: Cliente ve mapa de heladeras cercanas con estado operativo
- **US-05**: Cliente ve stock disponible en una heladera específica
- **US-06**: Admin de heladera recibe actualización automática de stock (retiro/devolución)
- **US-07**: Admin de heladera recibe alerta cuando heladera cambia a estado "fuera de servicio" o "en mantenimiento"

## Entidades

### Fridge (PostgreSQL — fridge-service DB)

| Campo            | Tipo            | Restricciones                              |
|------------------|-----------------|-------------------------------------------|
| id               | Long            | PK, auto-generated                        |
| name             | String          | @NotBlank, max 150                        |
| latitude         | Double          | @NotNull                                  |
| longitude        | Double          | @NotNull                                  |
| address          | String          | @NotBlank, max 300                        |
| status           | String          | @NotBlank, enum: ACTIVE, MAINTENANCE, OUT_OF_SERVICE |
| lastMaintenance  | LocalDate       | Opcional                                   |
| createdAt        | LocalDateTime   | Auto, no modificable                      |
| updatedAt        | LocalDateTime   | Auto                                      |

**Notas:**
- `latitude` y `longitude` como campos separados (Double) para facilitar queries de cercanía.
- `status` como String con validación de enum. Valores: ACTIVE, MAINTENANCE, OUT_OF_SERVICE.

### FridgeStock (PostgreSQL — fridge-service DB)

| Campo       | Tipo         | Restricciones                                |
|-------------|--------------|-----------------------------------------------|
| id          | Long         | PK, auto-generated                            |
| fridgeId    | Long         | FK → Fridge.id, @NotNull                     |
| productId  | Long         | @NotNull — ID de producto en product-service  |
| quantity    | Integer      | @NotNull, @Min(0)                             |
| updatedAt   | LocalDateTime| Auto                                          |

**Notas:**
- `productId` es una referencia lógica al product-service (sin FK, ya que es otro microservicio con base de datos separada).
- La combinación (fridgeId, productId) debe ser única → `@UniqueConstraint`.

### FridgeStatusEvent (PostgreSQL — fridge-service DB)

| Campo       | Tipo            | Restricciones                        |
|-------------|-----------------|--------------------------------------|
| id          | Long            | PK, auto-generated                   |
| fridgeId    | Long            | FK → Fridge.id                       |
| oldStatus   | String          | Estado anterior                      |
| newStatus   | String          | Estado nuevo                         |
| timestamp   | LocalDateTime   | Auto                                 |

**Notas:**
- Tabla de auditoría para cambios de estado. Permite rastrear historial y disparar notificaciones.

## DTOs (Records)

- `HeladeraCreateDTO(String name, Double latitude, Double longitude, String address, String status)`
- `HeladeraUpdateDTO(String name, Double latitude, Double longitude, String address, String status)`
- `HeladeraResponseDTO(Long id, String name, Double latitude, Double longitude, String address, String status, LocalDate lastMaintenance, LocalDateTime createdAt, LocalDateTime updatedAt)`
- `StockCreateDTO(Long productId, Integer quantity)`
- `StockUpdateDTO(Integer quantity)`
- `StockResponseDTO(Long id, Long fridgeId, Long productId, Integer quantity, LocalDateTime updatedAt)`

## Endpoints REST

| Método | Path                                        | Descripción                                        | US    |
|--------|---------------------------------------------|----------------------------------------------------|-------|
| POST   | `/api/v1/heladeras`                         | Registrar heladera                                 | —     |
| GET    | `/api/v1/heladeras`                         | Listar heladeras (con filtros de cercanía)         | US-04 |
| GET    | `/api/v1/heladeras?lat={lat}&lng={lng}&radius={km}` | Heladeras cercanas por ubicación         | US-04 |
| GET    | `/api/v1/heladeras/{id}`                    | Obtener heladera por ID                            | US-04 |
| PUT    | `/api/v1/heladeras/{id}`                    | Actualizar heladera (incl. cambio de estado)       | US-07 |
| GET    | `/api/v1/heladeras/{id}/stock`              | Obtener stock completo de una heladera             | US-05 |
| PUT    | `/api/v1/heladeras/{id}/stock`              | Actualizar stock (por order-service al retirar/devolver) | US-06 |
| POST   | `/api/v1/heladeras/{id}/stock`              | Agregar item de stock a una heladera               | —     |

### Detalle de GET /api/v1/heladeras (con filtros)

- **Parámetros query opcionales:** `lat`, `lng`, `radius` (km), `status`
- **Sin filtros:** lista todas las heladeras
- **Con lat+lng+radius:** filtra por cercanía usando fórmula Haversine en query JPQL o Native Query
- **Con status:** filtra por estado operativo
- **Respuesta:** `List<HeladeraResponseDTO>`

### Detalle de PUT /api/v1/heladeras/{id}/stock

- **Body:** `StockUpdateDTO` (o lista de actualizaciones para batch)
- **Uso principal:** order-service llama para decrementar/incrementar stock al crear/cancelar orden
- **Respuesta:** `StockResponseDTO` o lista actualizada

### Detalle de PUT /api/v1/heladeras/{id} (cambio de estado)

- Si el `status` cambia, se registra un `FridgeStatusEvent`
- Si el nuevo status es `ACTIVE` y antes era `OUT_OF_SERVICE`/`MAINTENANCE`, se notifica a notification-service (fridge-service → notification-service) que productos pueden estar disponibles nuevamente

## Integraciones

### Integración Saliente: fridge-service → notification-service

- **Protocolo:** REST via OpenFeign
- **Trigger:** Cuando el status de una heladera cambia de OUT_OF_SERVICE/MAINTENANCE a ACTIVE
- **Endpoint en notification-service:** `POST /api/v1/notificaciones/producto-disponible`
- **Payload:** `{ "fridgeId": Long, "productIds": List<Long> }` (o similar)
- **Propósito:** Notificar a usuarios suscritos que un producto favorito está nuevamente disponible

### Integración Entrante: order-service → fridge-service

- **Protocolo:** REST via API Gateway o directo
- **Endpoints consumidos:** 
  - `GET /api/v1/heladeras/{id}/stock` — order-service verifica stock disponible antes de crear orden
  - `PUT /api/v1/heladeras/{id}/stock` — order-service decrementa stock al pagar, incrementa al cancelar/retirar
- **Nota:** Para MVP, order-service consulta y modifica stock directamente en fridge-service

### Integración Entrante: product-service (lectura referencia)

- fridge-service no consume product-service directamente
- `productId` en FridgeStock es una referencia lógica (sin FK). La consistencia se mantiene a nivel de aplicación.

## Estructura de Paquetes

```
com.farmacyfood.fridge/
├── FridgeServiceApplication.java
├── controller/
│   ├── HeladeraController.java
│   └── StockController.java
├── service/
│   ├── HeladeraService.java
│   ├── HeladeraServiceImpl.java
│   ├── StockService.java
│   └── StockServiceImpl.java
├── repository/
│   ├── HeladeraRepository.java
│   ├── StockRepository.java
│   └── StatusEventRepository.java
├── entity/
│   ├── Heladera.java
│   ├── StockHeladera.java
│   └── EventoEstadoHeladera.java
├── dto/
│   ├── HeladeraCreateDTO.java
│   ├── HeladeraUpdateDTO.java
│   ├── HeladeraResponseDTO.java
│   ├── StockCreateDTO.java
│   ├── StockUpdateDTO.java
│   └── StockResponseDTO.java
├── client/
│   └── NotificacionClient.java          # OpenFeign client
├── exception/
│   ├── HeladeraNotFoundException.java
│   ├── StockInsuficienteException.java
│   └── GlobalExceptionHandler.java
└── config/
    ├── SwaggerConfig.java
    └── FeignConfig.java
```

## Dependencias Maven a Agregar

- `spring-boot-starter-data-jpa`
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
```

## Decisiones Tomadas

1. **Separación de controllers:** `HeladeraController` (CRUD de heladeras) y `StockController` (gestión de stock bajo `/heladeras/{id}/stock`).
2. **Búsqueda por cercanía:** Se usará Native Query con fórmula Haversine. MVP simple: filtro por radio en km. El endpoint soporta tanto listado general como búsqueda por cercanía.
3. **Stock como entidad separada:** No embebida en Fridge porque tiene su propio ciclo de vida y necesita ser actualizada independientemente.
4. **Evento de estado:** Se crea tabla `FridgeStatusEvent` para auditoría y para derivar notificaciones.
5. **Sin FK a product-service:** `productId` es referencia lógica (database per service pattern).
6. **Nomenclatura en español:** Endpoints y DTOs en español, entidades JPA en español siguiendo la guía del producto.