# Product Service

Microservicio de catálogo de productos para FarmacyFood.

## Swagger

- **UI:** [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **API Docs:** [http://localhost:8081/api/v1/productos/v3/api-docs](http://localhost:8081/api/v1/productos/v3/api-docs)

## Comunicación con otros servicios

```
                        ┌─────────────────────┐
                        │    API Gateway       │
                        │  (puerto 8769)       │
                        │  /api/v1/productos/**│
                        └──────────┬───────────┘
                                   │
                                   ▼
                        ┌─────────────────────┐
                ┌───────│  product-service    │───────┐
                │       │  (puerto 8081)      │       │
                │       │  PostgreSQL 5437    │       │
                │       └─────────────────────┘       │
                │                                       │
     GET /api/v1/productos?category={cat}    GET /api/v1/productos/{id}/nombre
                │                                       │
                ▼                                       ▼
  ┌───────────────────────────┐            ┌───────────────────────────┐
  │  recommendation-service   │            │     kitchen-service        │
  │  (puerto 8084)            │            │     (puerto 8082)          │
  │  ProductoClientFeign      │            │     ProductoClientFeign    │
  │  (OpenFeign, profile !dev)│            │     (OpenFeign, profile   │
  │                           │            │      !dev)                 │
  └───────────────────────────┘            └───────────────────────────┘
```

- **recommendation-service** consulta productos por categoría para generar recomendaciones.
- **kitchen-service** consulta el nombre de un producto por ID.
- En perfil `dev`, ambos usan mocks en vez de llamadas reales.