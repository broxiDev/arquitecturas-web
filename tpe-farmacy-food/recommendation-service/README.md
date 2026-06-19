# Recommendation Service — Preferencias y Recomendaciones (F5)

**Puerto:** 8085  
**Base Path:** `/api/v1/recomendaciones`  
**Base de Datos:** PostgreSQL (cache de preferencias, historial y recomendaciones)  
**Swagger:** http://localhost:8085/swagger-ui.html

---

## Arquitectura — Comunicación con otros servicios

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         RECOMMENDATION SERVICE (8085)                        │
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  GET /api/v1/recomendaciones/{userId}                                │   │
│  └──────────────────────────────┬───────────────────────────────────────┘   │
│                                 │                                            │
│                                 ▼                                            │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │                     RecomendacionService                             │   │
│  │                                                                      │   │
│  │  1. Verificar cache (PostgreSQL)                                     │   │
│  │  2. Si cache válido (< 6hs) → retornar                               │   │
│  │  3. Si cache expirado/inexistente → generar recomendaciones          │   │
│  └──────┬───────────────────┬───────────────────┬───────────────────────┘   │
│         │                   │                   │                            │
│         ▼                   ▼                   ▼                            │
│  ┌──────────────┐   ┌──────────────┐   ┌──────────────┐                     │
│  │ Preferencia  │   │   Cache      │   │  OrdenClient │                     │
│  │   Service    │   │   Service    │   │              │                     │
│  └──────┬───────┘   └──────┬───────┘   └──────┬───────┘                     │
│         │                   │                   │                            │
└─────────┼───────────────────┼───────────────────┼────────────────────────────┘
          │                   │                   │
          │                   │                   │
          ▼                   ▼                   ▼
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│  user-service   │  │   PostgreSQL    │  │  order-service  │
│    (8086)       │  │  recommendation │  │    (8083)       │
│                 │  │      _db        │  │                 │
│ GET /api/v1/    │  │                 │  │ GET /api/v1/    │
│ usuarios/{id}   │  │ • perfil_       │  │ ordenes/usuario │
│                 │  │   preferencia   │  │   /{userId}     │
│ → preferencias  │  │ • historial_    │  │                 │
│   dietarias     │  │   compras_cache │  │ → historial de  │
│                 │  │ • recomendacion │  │   compras       │
│                 │  │   _resultado    │  │                 │
└─────────────────┘  └─────────────────┘  └─────────────────┘
          │                                        │
          │                                        │
          └────────────────┬───────────────────────┘
                           │
                           ▼
                  ┌─────────────────┐
                  │ product-service │
                  │    (8081)       │
                  │                 │
                  │ GET /api/v1/    │
                  │ productos       │
                  │ ?category={cat} │
                  │                 │
                  │ → productos por │
                  │   categoría     │
                  └─────────────────┘
```

### Flujo completo de generación de recomendaciones

```
Cliente                    recommendation-service              Servicios externos
  │                               │                                    │
  │  GET /recomendaciones/{id}    │                                    │
  │──────────────────────────────>│                                    │
  │                               │                                    │
  │                               │─── Verificar cache PostgreSQL ────>│
  │                               │<───────────── null ────────────────│
  │                               │                                    │
  │                               │─── GET /usuarios/{id} ────────────>│ user-service
  │                               │<─── {dietaryPreferences:[...]} ────│
  │                               │                                    │
  │                               │─── GET /ordenes/usuario/{id} ─────>│ order-service
  │                               │<─── [{items:[{productId,...}]}] ───│
  │                               │                                    │
  │                               │─── GET /productos?category=X ─────>│ product-service
  │                               │<─── [{id,name,dietaryCategory}] ───│
  │                               │                                    │
  │                               │  [Filtrar por preferencias]        │
  │                               │  [Excluir ya comprados]            │
  │                               │  [Ordenar por popularidad]         │
  │                               │  [Top 5 productos]                 │
  │                               │                                    │
  │                               │─── Guardar en cache PostgreSQL ───>│
  │                               │                                    │
  │<────── RecomendacionResponse ─│                                    │
  │        {productos:[...]}      │                                    │
```

---

## Probar endpoints

### Health check
```bash
curl http://localhost:8085/api/v1/recomendaciones/health
```

### Obtener recomendaciones para un usuario
```bash
curl http://localhost:8085/api/v1/recomendaciones/1
```

### Respuesta de ejemplo
```json
{
  "userId": 1,
  "productos": [
    {
      "productId": 301,
      "productName": "Ensalada Vegana Premium",
      "reason": "Producto recomendado basado en tus preferencias dietarias",
      "dietaryCategory": "VEGANO"
    },
    {
      "productId": 302,
      "productName": "Bowl Vegano Energético",
      "reason": "Producto recomendado basado en tus preferencias dietarias",
      "dietaryCategory": "VEGANO"
    }
  ],
  "generatedAt": "2026-06-19T00:30:00"
}
```

---

## User Stories

### US-15 — Sugerencias de productos basadas en historial y preferencias

El cliente quiere ver productos sugeridos basados en sus preferencias dietarias e historial de compras.

**Flujo:**
```
GET /api/v1/recomendaciones/{userId}
    ├── Verificar cache en PostgreSQL (TTL: 6 horas)
    ├── Si cache válido → retornar RecomendacionResponseDTO
    ├── Si cache expirado/inexistente:
    │   ├── Consultar user-service → preferencias dietarias
    │   ├── Consultar order-service → historial de compras
    │   ├── Consultar product-service → productos por categoría
    │   ├── Filtrar por preferencias dietarias
    │   ├── Excluir productos ya comprados
    │   ├── Ordenar por popularidad
    │   ├── Tomar top 5
    │   └── Guardar en cache y retornar
    └── Devuelve RecomendacionResponseDTO
```

**Endpoints:**
| Método | Path | Descripción |
|---|---|---|
| GET | `/api/v1/recomendaciones/{userId}` | Obtener recomendaciones para un usuario |
| GET | `/api/v1/recomendaciones/health` | Health check del servicio |

---

## Integraciones

### OpenFeign Clients

| Cliente | Servicio | Endpoint consumido | Propósito |
|---|---|---|---|
| `UsuarioClient` | user-service | `GET /api/v1/usuarios/{id}` | Obtener preferencias dietarias del usuario |
| `OrdenClient` | order-service | `GET /api/v1/ordenes/usuario/{userId}` | Obtener historial de compras |
| `ProductoClient` | product-service | `GET /api/v1/productos?category={cat}` | Obtener productos por categoría dietaria |

### Mock vs Real

Los clientes tienen dos implementaciones controladas por Spring Profile:

- **`dev`** (default): `UsuarioClientMockImpl` / `OrdenClientMockImpl` / `ProductoClientMockImpl` — datos hardcodeados
- **`!dev`**: `UsuarioClientFeign` / `OrdenClientFeign` / `ProductoClientFeign` — llamadas reales via OpenFeign

Para cambiar, modificar `spring.profiles.active` en `application.yml`.

### Contratos de API

Ver documentación detallada en [`docs/contracts/`](./docs/contracts/):
- [user-service.md](./docs/contracts/user-service.md) — Estado: ✅ IMPLEMENTADO
- [order-service.md](./docs/contracts/order-service.md) — Estado: ✅ IMPLEMENTADO
- [product-service.md](./docs/contracts/product-service.md) — Estado: ✅ IMPLEMENTADO

---

## Datos de ejemplo (profile dev)

Al arrancar en profile `dev`, los mocks proveen:

**Usuario 1:**
- Preferencias: `VEGANO`, `SIN_GLUTEN`
- Historial: Ensalada Vegana (x2), Bowl Sin Gluten (x1), Wrap Vegano (x1)

**Usuario 2:**
- Preferencias: `VEGETARIANO`
- Historial: Ensalada Vegetariana (x1), Bowl Vegetariano (x2)

**Productos disponibles (mock):**
| Categoría | Productos |
|---|---|
| VEGANO | Ensalada Vegana Premium, Bowl Vegano Energético, Wrap Vegano Mediterráneo, Smoothie Bowl Vegano |
| SIN_GLUTEN | Ensalada Sin Gluten, Bowl Sin Gluten Proteico, Wrap Sin Gluten |
| VEGETARIANO | Ensalada Vegetariana, Bowl Vegetariano, Wrap Vegetariano |

---

## Docker

### Desarrollo individual
```bash
cd recommendation-service/
docker compose down -v   # Resetear datos
docker compose up -d     # Levantar con datos de ejemplo
```

Esto levanta:
- `recommendation-postgres` — PostgreSQL 16 (puerto 5435, DB: `recommendation_db`, user: `root`, sin password)

### Integración completa
```bash
cd tpe-farmacy-food/
docker compose up -d
```

---

## Tests

```bash
mvn test -pl recommendation-service
```

**13 tests:**
- `PreferenciaServiceImplTest` — lógica de obtención de preferencias (3 tests)
- `CacheServiceImplTest` — manejo de cache con TTL (5 tests)
- `RecomendacionServiceImplTest` — algoritmo de recomendación (3 tests)
- `RecomendacionControllerTest` — endpoints REST con MockMvc (2 tests)

---

## Estructura de paquetes

```
com.farmacyfood.recommendation/
├── RecommendationServiceApplication.java
├── controller/
│   ├── HealthController.java
│   └── RecomendacionController.java
├── service/
│   ├── PreferenciaService.java          # Gestión de preferencias dietarias
│   ├── PreferenciaServiceImpl.java
│   ├── CacheService.java                # Manejo de cache PostgreSQL
│   ├── CacheServiceImpl.java
│   ├── RecomendacionService.java        # Algoritmo de recomendación
│   └── RecomendacionServiceImpl.java
├── repository/
│   ├── PerfilPreferenciaRepository.java
│   ├── HistorialComprasCacheRepository.java
│   └── RecomendacionCacheRepository.java
├── entity/
│   ├── PerfilPreferencia.java
│   ├── HistorialComprasCache.java
│   ├── OrdenCache.java
│   ├── RecomendacionResultado.java
│   └── ProductoRecomendado.java
├── dto/
│   ├── RecomendacionResponseDTO.java
│   ├── ProductoRecomendadoDTO.java
│   ├── PerfilPreferenciaDTO.java
│   ├── UsuarioResponseDTO.java
│   ├── OrdenDTO.java
│   ├── OrderItemDTO.java
│   └── ProductoDTO.java
├── client/
│   ├── UsuarioClient.java               # Interface
│   ├── UsuarioClientFeign.java          # @Profile("!dev")
│   ├── UsuarioClientMockImpl.java       # @Profile("dev")
│   ├── OrdenClient.java
│   ├── OrdenClientFeign.java
│   ├── OrdenClientMockImpl.java
│   ├── ProductoClient.java
│   ├── ProductoClientFeign.java
│   └── ProductoClientMockImpl.java
├── exception/
│   ├── UsuarioNotFoundException.java
│   └── GlobalExceptionHandler.java
└── docs/
    └── contracts/
        ├── user-service.md
        ├── order-service.md
        └── product-service.md
```
