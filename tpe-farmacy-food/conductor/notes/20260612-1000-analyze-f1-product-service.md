# Análisis F1 — Catálogo de Productos (product-service)

**Responsable:** Gabi  
**Microservicio:** product-service  
**Puerto:** 8081  
**Base path:** `/api/v1/productos`

---

## User Stories Cubiertas

- **US-01**: Cliente ve listado de productos con nombre, descripción, categoría dietaria y precio
- **US-02 (Opcional)**: Filtrado por categoría dietaria (vegetariano, vegano, sin gluten)
- **US-03**: Cocina fantasma registra productos con atributos completos

## Entidades

### Product (PostgreSQL — product-service DB)

| Campo           | Tipo            | Restricciones                     |
|-----------------|-----------------|-----------------------------------|
| id              | Long            | PK, auto-generated               |
| name            | String          | @NotBlank, max 150               |
| description     | String          | @NotBlank, max 500               |
| dietaryCategory | String          | @NotBlank, enum: VEGETARIANO, VEGANO, SIN_GLUTEN, NORMAL |
| price           | BigDecimal      | @NotNull, @Positive              |
| imageUrl        | String          | URL opcional                      |
| nutritionalInfo | String          | Info nutricional opcional         |
| storageTemp     | String          | Temperatura de conservación       |
| createdAt       | LocalDateTime   | Auto, no modificable             |
| updatedAt       | LocalDateTime   | Auto, actualizado en put          |

**Notas:**
- Cada cocina fantasma tiene su propio catálogo. Se necesita campo `kitchenId` si en el futuro se requiere multi-tenant. Para MVP, asumimos un solo catálogo global.
- `dietaryCategory` como String con validación de enum allows extensibilidad futura.

## DTOs (Records)

- `ProductCreateDTO(String name, String description, String dietaryCategory, BigDecimal price, String imageUrl, String nutritionalInfo, String storageTemp)`
- `ProductUpdateDTO(String name, String description, String dietaryCategory, BigDecimal price, String imageUrl, String nutritionalInfo, String storageTemp)` — todos opcionales via @Nullable
- `ProductResponseDTO(Long id, String name, String description, String dietaryCategory, BigDecimal price, String imageUrl, String nutritionalInfo, String storageTemp, LocalDateTime createdAt, LocalDateTime updatedAt)`

## Endpoints REST

| Método | Path                                  | Descripción                              | US    |
|--------|---------------------------------------|------------------------------------------|-------|
| POST   | `/api/v1/productos`                   | Crear producto                           | US-03 |
| GET    | `/api/v1/productos`                   | Listar todos los productos               | US-01 |
| GET    | `/api/v1/productos?categoria=vegano`  | Filtrar por categoría dietaria           | US-02 |
| GET    | `/api/v1/productos/{id}`              | Obtener producto por ID                  | US-01 |
| PUT    | `/api/v1/productos/{id}`              | Actualizar producto                      | US-03 |
| DELETE | `/api/v1/productos/{id}`              | Eliminar producto                        | US-03 |

## Detalle de Endpoints

### GET /api/v1/productos
- **Parámetros query opcionales:** `categoria` (valor del enum dietaryCategory)
- **Respuesta:** `List<ProductResponseDTO>`
- **Implementación:** `ProductRepository.findAll()` con filtro JPQL `WHERE p.dietaryCategory = :categoria` cuando se proporciona el parámetro

### GET /api/v1/productos/{id}
- **Respuesta:** `ProductResponseDTO` o 404
- **Implementación:** `ProductRepository.findById(id)` con Optional

### POST /api/v1/productos
- **Body:** `ProductCreateDTO`
- **Respuesta:** 201 Created con `ProductResponseDTO`
- **Validación:** `@Valid` en el body

### PUT /api/v1/productos/{id}
- **Body:** `ProductUpdateDTO`
- **Respuesta:** `ProductResponseDTO` o 404
- **Validación:** `@Valid` en el body

### DELETE /api/v1/productos/{id}
- **Respuesta:** 204 No Content o 404

## Integraciones

**product-service es standalone.** No consume ni es consumido por otros microservicios en MVP.

- La info de productos es consultada por otros servicios (order-service, kitchen-service, recommendation-service, fridge-service) pero estos servicios accederán al catálogo vía API REST a través del API Gateway, no vía OpenFeign interno. Los DTOs compartidos se definirán en cada servicio consumidor según necesidad (sin módulo shared para MVP).
- Para MVP, otros servicios pueden cache-r o llamar al product-service via OpenFeign para obtener datos de producto (nombre, precio) al momento de crear una orden.

## Estructura de Paquetes

```
com.farmacyfood.product/
├── ProductServiceApplication.java
├── controller/
│   └── ProductoController.java
├── service/
│   ├── ProductoService.java
│   └── ProductoServiceImpl.java
├── repository/
│   └── ProductoRepository.java
├── entity/
│   └── Producto.java
├── dto/
│   ├── ProductoCreateDTO.java
│   ├── ProductoUpdateDTO.java
│   └── ProductoResponseDTO.java
├── exception/
│   ├── ProductoNotFoundException.java
│   └── GlobalExceptionHandler.java
└── config/
    └── SwaggerConfig.java
```

## Dependencias Maven a Agregar

- `spring-boot-starter-data-jpa`
- `postgresql` (driver)
- `spring-boot-starter-validation`
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

1. **Nombres en español del código:** Siguiendo la guía de producto, los endpoints y DTOs usan nombres en español (`productos`, `categoria`, `ProductoService`). Los campos internos de la entidad JPA se mantienen en inglés (convención Java).
2. **Sin multi-tenant en MVP:** Se omite `kitchenId` para el MVP. Un solo catálogo global.
3. **Sin OpenFeign:** product-service no necesita conectar con otros servicios en MVP.
4. **Enum como String:** `dietaryCategory` se almacena como String para flexibilidad, pero se valida contra valores permitidos.