# Kitchen Service — Planificación de Cocina (F4)

**Puerto:** 8084  
**Base Path:** `/api/v1/cocina`  
**Bases de Datos:** PostgreSQL (planes) + MongoDB (historial)  
**Swagger:** http://localhost:8084/swagger-ui.html

---

## Probar endpoints

### Health check
```bash
curl http://localhost:8084/api/v1/cocina/health
```

### Plan diario — Obtener el plan de hoy
```bash
curl http://localhost:8084/api/v1/cocina/plan-diario
```

### Plan diario — Obtener plan de una fecha específica
```bash
curl "http://localhost:8084/api/v1/cocina/plan-diario?fecha=2026-06-14"
```

### Plan diario — Generar plan (calcula promedio de ventas de los últimos 7 días)
```bash
curl -X POST "http://localhost:8084/api/v1/cocina/plan-diario?fecha=2026-06-14"
```

### Historial — Todas las ventas
```bash
curl http://localhost:8084/api/v1/cocina/historial-ventas
```

### Historial — Filtrar por producto
```bash
curl "http://localhost:8084/api/v1/cocina/historial-ventas?productId=101"
```

### Historial — Filtrar por heladera
```bash
curl "http://localhost:8084/api/v1/cocina/historial-ventas?fridgeId=1"
```

### Historial — Filtrar por rango de fechas
```bash
curl "http://localhost:8084/api/v1/cocina/historial-ventas?from=2026-06-01&to=2026-06-14"
```

### Historial — Filtros combinados
```bash
curl "http://localhost:8084/api/v1/cocina/historial-ventas?productId=101&from=2026-06-01&to=2026-06-14"
```

### Plan no existe (devuelve 404)
```bash
curl http://localhost:8084/api/v1/cocina/plan-diario?fecha=2099-01-01
```

---

## User Stories

### US-12 — Reporte diario de producción sugerida

El admin de cocina necesita saber qué producir cada día basándose en ventas históricas.

**Flujo de generación (POST):**
```
POST /api/v1/cocina/plan-diario?fecha=2026-06-14
    ├── Consulta order-service → GET /api/v1/ordenes/historial-ventas?from=&to= (últimos 7 días)
    ├── Agrupa ventas por producto
    ├── Calcula promedio de ventas por producto
    ├── Persiste DailyPlan + PlanItems → PostgreSQL
    └── Devuelve PlanDiarioResponseDTO
```

**Flujo de consulta (GET):**
```
GET /api/v1/cocina/plan-diario
    └── Lee DailyPlan de PostgreSQL por fecha actual
        └── Devuelve JSON con items sugeridos
        └── 404 si no existe plan para hoy
```

**Endpoints:**
| Método | Path | Descripción |
|---|---|---|
| GET | `/api/v1/cocina/plan-diario` | Obtener plan del día actual |
| GET | `/api/v1/cocina/plan-diario?fecha=YYYY-MM-DD` | Obtener plan de fecha específica |
| POST | `/api/v1/cocina/plan-diario?fecha=YYYY-MM-DD` | Generar/actualizar plan |

---

### US-13 — Historial de ventas por producto y heladera

El admin de cocina necesita ver el historial de ventas para análisis y toma de decisiones.

**Endpoints:**
| Método | Path | Descripción |
|---|---|---|
| GET | `/api/v1/cocina/historial-ventas` | Todas las ventas |
| GET | `/api/v1/cocina/historial-ventas?from=&to=` | Filtrar por rango de fechas |
| GET | `/api/v1/cocina/historial-ventas?productId=` | Filtrar por producto |
| GET | `/api/v1/cocina/historial-ventas?fridgeId=` | Filtrar por heladera |

---

## Datos de ejemplo

Al levantar Docker con `docker compose down -v && docker compose up -d` se cargan automáticamente:

**PostgreSQL (plan diario):**
| Producto | Cantidad sugerida |
|---|---|
| Ensalada César | 10 |
| Bowl Proteico | 6 |
| Wrap de Pollo | 8 |

**MongoDB (ventas históricas):**
| Producto | Heladera | Cantidad | Monto | Fecha |
|---|---|---|---|---|
| Ensalada César | 1 | 12 | $6000 | ayer |
| Ensalada César | 2 | 8 | $4000 | hace 2 días |
| Bowl Proteico | 1 | 5 | $3500 | ayer |
| Bowl Proteico | 1 | 7 | $4900 | hace 3 días |
| Wrap de Pollo | 3 | 10 | $4500 | ayer |
| Wrap de Pollo | 2 | 6 | $2700 | hace 2 días |

---

## Integraciones

### OpenFeign Clients

| Cliente | Servicio | Endpoint consumido | Propósito |
|---|---|---|---|
| `OrdenClient` | order-service | `GET /api/v1/ordenes/historial-ventas` | Obtener ventas para calcular plan |
| `ProductoClient` | product-service | `GET /api/v1/productos/{id}/nombre` | Obtener nombre de producto |

### Mock vs Real

Los clientes tienen dos implementaciones controladas por Spring Profile:

- **`dev`** (default): `OrdenClientMockImpl` / `ProductoClientMockImpl` — datos hardcodeados
- **`!dev`**: `OrdenClientFeign` / `ProductoClientFeign` — llamadas reales via OpenFeign

Para cambiar, modificar `spring.profiles.active` en `application.yml`.

---

## Docker

### Desarrollo individual
```bash
cd kitchen-service/
docker compose down -v   # Resetear datos
docker compose up -d     # Levantar con datos de ejemplo
```

Esto levanta:
- `kitchen-postgres` — PostgreSQL 16 (puerto 5432, DB: `kitchen_db`, user: `root`, sin password)
- `kitchen-mongo` — MongoDB 7 (puerto 27017, DB: `kitchen_db`)

### Integración completa
```bash
cd tpe-farmacy-food/
docker compose up -d
```

---

## Tests

```bash
mvn test -pl kitchen-service
```

**13 tests unitarios:**
- `PlanDiarioServiceImplTest` — lógica de generación y consulta de planes
- `HistorialVentasServiceImplTest` — filtros de historial
- `PlanDiarioControllerTest` — endpoints REST (MockMvc)
- `HistorialVentasControllerTest` — endpoints REST (MockMvc)
