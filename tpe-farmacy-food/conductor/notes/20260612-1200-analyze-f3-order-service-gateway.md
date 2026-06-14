# Análisis F3 — Órdenes y Pagos (order-service + api-gateway)

**Responsables:** Fiore (order-service) + Mati (api-gateway)  
**Microservicio:** order-service  
**Puerto order-service:** 8083  
**Puerto api-gateway:** 8080  
**Base path:** `/api/v1/ordenes`

---

## User Stories Cubiertas

- **US-08**: Cliente selecciona productos de una heladera y genera una orden
- **US-09**: Cliente paga orden a través de gateway de pago (PayPal)
- **US-10**: Sistema autoriza apertura de heladera post-pago
- **US-11**: Admin de heladera ve detalle de órdenes procesadas

## Entidades (order-service — PostgreSQL)

### Order

| Campo       | Tipo            | Restricciones                                  |
|-------------|-----------------|------------------------------------------------|
| id          | Long            | PK, auto-generated                             |
| userId      | Long            | @NotNull — referencia a user-service           |
| fridgeId    | Long            | @NotNull — referencia a fridge-service          |
| items       | List<OrderItem>  | @OneToMany(cascade=ALL)                        |
| total       | BigDecimal      | @NotNull, @Positive                             |
| status      | String          | @NotBlank, enum: PENDING, PAID, CANCELLED, PICKED_UP |
| paymentId   | String          | ID externo del pago (PayPal), nullable          |
| createdAt   | LocalDateTime   | Auto, no modificable                           |
| updatedAt   | LocalDateTime   | Auto                                           |

**Status flow:** PENDING → PAID → PICKED_UP (o CANCELLED desde PENDING/PAID)

### OrderItem

| Campo       | Tipo            | Restricciones                          |
|-------------|-----------------|----------------------------------------|
| id          | Long            | PK, auto-generated                    |
| orderId     | Long            | FK → Order.id                          |
| productId   | Long            | @NotNull — referencia a product-service |
| productName | String          | @NotBlank — desnormalizado del producto |
| quantity    | Integer         | @NotNull, @Min(1)                     |
| unitPrice   | BigDecimal      | @NotNull, @Positive — precio al momento de la orden |

**Notas:**
- `productName` y `unitPrice` se desnormalizan (copian del product-service al crear la orden) para no depender de availability del product-service posteriormente.
- `userId` y `fridgeId` son referencias lógicas sin FK (database per service pattern).

## DTOs (Records)

### Orden

- `OrdenCreateDTO(Long userId, Long fridgeId, List<OrdenItemDTO> items)`
- `OrdenItemDTO(Long productId, String productName, Integer quantity, BigDecimal unitPrice)`
- `OrdenResponseDTO(Long id, Long userId, Long fridgeId, List<OrdenItemDTO> items, BigDecimal total, String status, String paymentId, LocalDateTime createdAt, LocalDateTime updatedAt)`
- `OrdenCancelDTO(String motivo)` — opcional
- `PagoResponseDTO(String paymentId, String status, String redirectUrl)`

## Endpoints REST (order-service)

| Método | Path                                     | Descripción                                     | US    |
|--------|------------------------------------------|------------------------------------------------|-------|
| POST   | `/api/v1/ordenes`                        | Crear orden                                     | US-08 |
| GET    | `/api/v1/ordenes`                        | Listar órdenes (admin)                          | US-11 |
| GET    | `/api/v1/ordenes/{id}`                   | Obtener orden por ID                            | US-08 |
| GET    | `/api/v1/ordenes/usuario/{userId}`       | Listar órdenes de un usuario                    | US-08 |
| PUT    | `/api/v1/ordenes/{id}/cancelar`          | Cancelar orden                                  | US-08 |
| POST   | `/api/v1/ordenes/{id}/pagar`            | Pagar orden                                     | US-09 |
| POST   | `/api/v1/ordenes/{id}/confirmar-retiro` | Confirmar retiro de productos                   | US-10 |

### Detalle de POST /api/v1/ordenes

- **Body:** `OrdenCreateDTO`
- **Flujo:**
  1. Validar userId con user-service (OpenFeign)
  2. Verificar stock disponible en fridge-service para cada producto (OpenFeign)
  3. Crear orden con status PENDING
  4. Retornar 201 Created con `OrdenResponseDTO`

### Detalle de POST /api/v1/ordenes/{id}/pagar

- **Flujo:**
  1. Verificar orden existe y está en status PENDING
  2. Integrar con gateway de pago externo (PayPal)
  3. Si pago exitoso: actualizar status a PAID, guardar paymentId
  4. Notificar a fridge-service para reservar stock y autorizar apertura (OpenFeign)
  5. Retornar `PagoResponseDTO`

**Nota sobre PayPal:** Para MVP, se simulará el gateway de pago con un servicio mock que retorna éxito. La integración real queda como mejora futura.

### Detalle de POST /api/v1/ordenes/{id}/confirmar-retiro

- **Flujo:**
  1. Verificar orden existe y está en status PAID
  2. Actualizar status a PICKED_UP
  3. Notificar a fridge-service para liberar stock (decrementar cantidad) (OpenFeign)
  4. Retornar `OrdenResponseDTO`

### Detalle de PUT /api/v1/ordenes/{id}/cancelar

- **Flujo:**
  1. Verificar orden existe y está en status PENDING o PAID
  2. Si estaba PAID: notificar a fridge-service para liberar stock reservado
  3. Actualizar status a CANCELLED
  4. Retornar `OrdenResponseDTO`

## Integraciones (order-service)

### Integración Saliente: order-service → user-service (OpenFeign)

- **Propósito:** Validar existencia de usuario al crear orden
- **Endpoint consumido:** `GET /api/v1/usuarios/{id}` — verificar que userId es válido
- **Client:** `UsuarioClient.java` en package `client/`

### Integración Saliente: order-service → fridge-service (OpenFeign)

- **Propósito:** Verificar stock, reservar stock al pagar, liberar stock al cancelar/retirar
- **Endpoints consumidos:**
  - `GET /api/v1/heladeras/{id}/stock` — verificar stock disponible
  - `PUT /api/v1/heladeras/{id}/stock` — reservar/liberar stock
- **Client:** `HeladeraClient.java` en package `client/`

### Integración Saliente: order-service → gateway de pago (Mock para MVP)

- **Propósito:** Procesar pago
- **Patrón:** Interface `PagoGateway` con implementación `PagoGatewayMockImpl` para MVP
- **Futuro:** `PagoGatewayPayPalImpl` con integración real

## API Gateway (Mati)

### Ruta para order-service

Ya configurado en `application.yml`:
```yaml
- id: order-service
  uri: lb://order-service
  predicates:
    - Path=/api/v1/ordenes/**
```

### Filtro JWT (pendiente)

- Para MVP: rutas de lectura públicas, escritura requiere JWT
- Filtro a implementar en api-gateway: `JwtAuthenticationFilter` (spring-cloud-gateway basado en WebFlux)
- Endpoints protegidos: POST, PUT, DELETE bajo `/api/v1/ordenes/**`
- Endpoints públicos: GET `/api/v1/ordenes/health`

**Nota:** La implementación completa de JWT queda para track futuro. En este track se define la estructura de rutas.

## Estructura de Paquetes (order-service)

```
com.farmacyfood.order/
├── OrderServiceApplication.java
├── controller/
│   └── OrdenController.java
├── service/
│   ├── OrdenService.java
│   └── OrdenServiceImpl.java
├── service/pago/
│   ├── PagoGateway.java              # Interface
│   └── PagoGatewayMockImpl.java      # Mock para MVP
├── repository/
│   ├── OrdenRepository.java
│   └── OrdenItemRepository.java
├── entity/
│   ├── Orden.java
│   └── ItemOrden.java
├── dto/
│   ├── OrdenCreateDTO.java
│   ├── OrdenItemDTO.java
│   ├── OrdenResponseDTO.java
│   └── PagoResponseDTO.java
├── client/
│   ├── HeladeraClient.java            # OpenFeign
│   └── UsuarioClient.java             # OpenFeign
├── exception/
│   ├── OrdenNotFoundException.java
│   ├── StockInsuficienteException.java
│   ├── PagoFallidoException.java
│   └── GlobalExceptionHandler.java
└── config/
    ├── SwaggerConfig.java
    └── FeignConfig.java
```

## Estructura de Paquetes (api-gateway — adiciones)

```
com.farmacyfood.gateway/
├── ApiGatewayApplication.java
├── filter/
│   └── JwtAuthenticationFilter.java    # Pendiente para track futuro
└── config/
    └── SecurityConfig.java             # Pendiente para track futuro
```

## Dependencias Maven a Agregar (order-service)

- `spring-boot-starter-data-jpa`
- `postgresql` (driver)
- `spring-boot-starter-validation`
- `spring-cloud-starter-openfeign`
- Ya tiene: `spring-boot-starter-web`, `spring-cloud-starter-netflix-eureka-client`, `springdoc`, `lombok`

## Configuración application.yml (order-service)

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

1. **Desnormalización de datos:** `productName` y `unitPrice` se copian del product-service al crear la orden. Esto evita dependencia fuertemente acoplada y preserva histórico si el producto cambia de precio.
2. **Gateway de pago mockeado:** Para MVP, se usa `PagoGatewayMockImpl`. Interface permite cambiar a PayPal en futuro.
3. **Flujo de stock simplificado:** Al pagar (PAID) se reserva stock. Al retirar (PICKED_UP) se decrementa stock definitivo. Al cancelar se libera stock reservado. Se necesita colaboración con Ale (fridge-service) para definir los contratos exactos.
4. **API Gateway JWT:** Se definen las rutas en este track pero la implementación del filtro JWT se posterga al track de infraestructura/transversal de Mati.
5. **Nomenclatura en español:** Endpoints (`/ordenes`, `/cancelar`, `/pagar`, `/confirmar-retiro`), DTOs y entities en español.
6. **Referencias lógicas:** `userId`, `fridgeId`, `productId` sin FK — database per service pattern.