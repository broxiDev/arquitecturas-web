# FarmacyFood — Stack Tecnológico

## Lenguaje de Programación
- **Java 21** — Versión LTS moderna con features como records, pattern matching y virtual threads.

## Frameworks Backend
- **Spring Boot 3.3+** — Framework principal para microservicios con autoconfiguración.
- **Spring Cloud Gateway** — API Gateway para enrutamiento, filtros y balanceo de carga.
- **Spring Cloud OpenFeign** — Comunicación entre microservicios vía HTTP.
- **Spring Data JPA** — Persistencia con repositorios sobre PostgreSQL y MongoDB.

## Bases de Datos
- **PostgreSQL** — Base de datos relacional principal para datos estructurados (usuarios, pedidos, productos).
  - product-service, fridge-service, order-service, user-service: PostgreSQL principal
  - kitchen-service: PostgreSQL para DailyPlan y PlanItem (datos transaccionales)
- **MongoDB** — Base de datos NoSQL para datos flexibles (preferencias, historial, recomendaciones).
  - kitchen-service: MongoDB para VentaHistorica (datos históricos agregados)
  - recommendation-service: MongoDB para perfiles de preferencia, historial cacheado y recomendaciones
  - notification-service: MongoDB para suscripciones y auditoría de notificaciones

## Dependencias por Microservicio
- **Spring Data JPA** — Usado en product-service, fridge-service, order-service, user-service, kitchen-service (PostgreSQL)
- **Spring Data MongoDB** — Usado en kitchen-service, recommendation-service, notification-service
- **Spring Cloud OpenFeign** — Usado en fridge-service, order-service, user-service, kitchen-service, recommendation-service
- **spring-boot-starter-validation** — Usado en todos los servicios con entidades
- **Lombok** — Usado en todos los servicios
- **SpringDoc OpenAPI** — Usado en todos los servicios para documentación Swagger

## Patrones de Integración
- **Comunicación síncrona** via OpenFeign entre servicios:
    - fridge-service → notification-service (cambio de estado de heladera)
    - order-service → fridge-service, user-service (verificar stock, validar usuario)
    - kitchen-service → order-service (historial de ventas por cocina)
    - kitchen-service → fridge-service (remanente de stock en heladeras)
    - user-service → order-service (historial de compras del usuario)
    - recommendation-service → user-service, order-service, product-service (datos para recomendaciones)
- **Gateway de pago mockeado** en order-service (PagoGateway interface con MockImpl para MVP)
- **Firebase Cloud Messaging mockeado** en notification-service (interface NotificacionPushService con MockImpl para MVP)

## Infraestructura y DevOps
- **Maven** — Gestión de dependencias y build multi-módulo.
- **Docker + Docker Compose** — Contenerización de microservicios y bases de datos.
- **OpenAPI / Swagger** — Documentación interactiva de todas las APIs REST.

### Docker Strategy: Database per Service (Individual Containers)

Each microservice has its own `docker-compose.yml` with dedicated database containers. The root `docker-compose.yml` uses the `include` directive to compose everything together.

| Scenario | Command | What runs |
|---|---|---|
| **Individual dev** | `docker compose up` inside service folder | Only that service's DBs |
| **Full integration** | `docker compose up` in project root | All services + all DBs |

**Structure:**
```
tpe-farmacy-food/
├── docker-compose.yml              ← Orchestrator (includes all services)
├── kitchen-service/
│   └── docker-compose.yml          ← kitchen-postgres, kitchen-mongo
├── order-service/
│   └── docker-compose.yml          ← order-postgres
├── product-service/
│   └── docker-compose.yml          ← product-postgres
└── ...
```

**Naming convention:** `<service>-postgres`, `<service>-mongo` to avoid container name collisions.

## Arquitectura
- Sistema de microservicios con un API Gateway central (Spring Cloud Gateway) y servicios independientes.
- Cada microservicio con su propia base de datos (Database per Service).
- Comunicación síncrona via OpenFeign entre servicios.

## Frontend
- **React 18** — Biblioteca de UI con hooks y JSX.
- **Vite 6** — Bundler y dev server rápido con HMR. Proxy configurado para redirigir `/api/v1/*` al API Gateway parseando `VITE_API_BASE_URL`.
- **Tailwind CSS v3** — Framework de estilos utilitarios, mobile-first.
- **Zustand 5** — Estado global ligero con API de hooks. Stores: `cartStore` (carrito), `uiStore` (filtros/loading), y stores por feature.
- **react-router-dom** — Enrutamiento declarativo con `<BrowserRouter>`, `<Routes>` y `<NavLink>`.
- **Axios** — Cliente HTTP con instancia centralizada, interceptors para JWT futuro y manejo de errores 401.
- **Servicios por dominio** — Cada dominio (`productos/`, `heladeras/`, `ordenes/`, etc.) con arquitectura de doble implementación:
  - `mock.js` — datos mock JSON con latencia simulada (perfil `dev`)
  - `http.js` — llamadas reales via Axios al API Gateway (perfil `!dev`)
  - `index.js` — selecciona mock o HTTP según `VITE_USE_MOCK`
