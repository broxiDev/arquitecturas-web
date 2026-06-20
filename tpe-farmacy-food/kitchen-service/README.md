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

### Plan diario — Obtener el plan de hoy para COCINA-DULCE
```bash
curl "http://localhost:8084/api/v1/cocina/plan-diario?cocinaId=COCINA-DULCE"
```

### Plan diario — Obtener plan de una fecha específica
```bash
curl "http://localhost:8084/api/v1/cocina/plan-diario?cocinaId=COCINA-DULCE&fecha=2026-06-14"
```

### Plan diario — Generar plan (calcula promedio de ventas de los últimos 7 días)
```bash
curl -X POST "http://localhost:8084/api/v1/cocina/plan-diario?cocinaId=COCINA-DULCE&fecha=2026-06-14"
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
curl "http://localhost:8084/api/v1/cocina/plan-diario?cocinaId=COCINA-DULCE&fecha=2099-01-01"
```

---

## User Stories

### US-12 — Reporte diario de producción sugerida

El admin de cocina necesita saber qué producir cada día basándose en ventas históricas.

**Flujo de generación (POST):**
```
POST /api/v1/cocina/plan-diario?cocinaId=COCINA-DULCE&fecha=2026-06-14
    ├── Consulta order-service → GET /api/v1/ordenes/historial-ventas/cocina/COCINA-DULCE?from=&to= (últimos 7 días)
    ├── Consulta fridge-service → GET /api/v1/heladeras/cocina/COCINA-DULCE/remanente
    ├── Calcula promedio de ventas por producto: ceil(totalVendido / 7)
    ├── Calcula remanente total por producto en heladeras
    ├── Calcula cantidad sugerida = promedio - remanente (solo si > 0)
    ├── Persiste DailyPlan + PlanItems → PostgreSQL
    └── Devuelve PlanDiarioResponseDTO
```

**Flujo de consulta (GET):**
```
GET /api/v1/cocina/plan-diario?cocinaId=COCINA-DULCE
    └── Lee DailyPlan de PostgreSQL por fecha + cocinaId
        └── Devuelve JSON con items sugeridos
        └── 404 si no existe plan para esa fecha y cocina
```

**Endpoints:**
| Método | Path | Descripción |
|---|---|---|
| GET | `/api/v1/cocina/plan-diario?cocinaId=` | Obtener plan del día actual para una cocina |
| GET | `/api/v1/cocina/plan-diario?cocinaId=&fecha=YYYY-MM-DD` | Obtener plan de fecha específica |
| POST | `/api/v1/cocina/plan-diario?cocinaId=&fecha=YYYY-MM-DD` | Generar plan para una cocina |

**Cocinas disponibles:** `COCINA-DULCE` (postres), `COCINA-CELIACA` (sin gluten), `COCINA-VEGANA` (vegana)

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

### Plan diario (PostgreSQL)

**COCINA-DULCE:**
| Producto | Cantidad sugerida |
|---|---|
| Brownie de Chocolate | 5 |
| Cheesecake | 4 |
| Tiramisú | 3 |

**COCINA-CELIACA:**
| Producto | Cantidad sugerida |
|---|---|
| Tostada de Palta Sin Gluten | 8 |
| Bowl de Quinoa Sin Gluten | 4 |
| Rolls de Primavera de Arroz | 5 |

**COCINA-VEGANA:**
| Producto | Cantidad sugerida |
|---|---|
| Buddha Bowl Vegano | 7 |
| Salteado de Tofu | 4 |
| Curry de Garbanzos | 4 |

### Ventas históricas (MongoDB)

| Producto | Heladera | Cantidad | Monto | Cocina |
|---|---|---|---|---|
| Brownie de Chocolate | 1 | 15 | $11250 | COCINA-DULCE |
| Brownie de Chocolate | 2 | 10 | $7500 | COCINA-DULCE |
| Cheesecake | 1 | 8 | $7600 | COCINA-DULCE |
| Tostada de Palta Sin Gluten | 3 | 18 | $12960 | COCINA-CELIACA |
| Tostada de Palta Sin Gluten | 4 | 14 | $10080 | COCINA-CELIACA |
| Buddha Bowl Vegano | 5 | 22 | $18700 | COCINA-VEGANA |

---

## Queries para probar en DBeaver

### PostgreSQL — SQL

```sql
-- Ver todos los planes diarios
SELECT * FROM daily_plan;

-- Ver los items del plan
SELECT * FROM plan_item;

-- Ver plan de COCINA-DULCE con sus items (join)
SELECT dp.date, dp.cocina_id, pi.product_name, pi.suggested_quantity
FROM daily_plan dp
JOIN plan_item pi ON dp.id = pi.daily_plan_id
WHERE dp.cocina_id = 'COCINA-DULCE' AND dp.date = CURRENT_DATE;

-- Ver todos los productos sugeridos ordenados por cantidad
SELECT pi.product_name, pi.suggested_quantity, dp.cocina_id
FROM plan_item pi
JOIN daily_plan dp ON dp.id = pi.daily_plan_id
ORDER BY pi.suggested_quantity DESC;
```

### MongoDB — Script (pestaña Script en DBeaver)

```js
// Ver todas las ventas
db.ventas_historicas.find()

// Ventas del producto 101 (Brownie de Chocolate)
db.ventas_historicas.find({ productId: NumberLong(101) })

// Ventas de la heladera 3 (COCINA-CELIACA)
db.ventas_historicas.find({ fridgeId: NumberLong(3) })

// Ventas de los últimos 2 días
db.ventas_historicas.find({ date: { $gte: new Date(Date.now() - 172800000) } })
```

---

## Integraciones

### OpenFeign Clients

| Cliente | Servicio | Endpoint consumido | Propósito |
|---|---|---|---|
| `FridgeClient` | fridge-service | `GET /api/v1/heladeras/cocina/{cocinaId}/remanente` | Obtener remanente de productos en heladeras |
| `OrdenClient` | order-service | `GET /api/v1/ordenes/historial-ventas/cocina/{cocinaId}` | Obtener ventas por cocina para calcular plan |
| `ProductoClient` | product-service | `GET /api/v1/productos/{id}/nombre` | Obtener nombre de producto |

### Mock vs Real

Los clientes tienen dos implementaciones controladas por Spring Profile:

- **`dev`** (default): `FridgeClientMockImpl` / `OrdenClientMockImpl` / `ProductoClientMockImpl` — datos diferenciados por cocina
- **`!dev`**: `FridgeClientFeign` / `OrdenClientFeign` / `ProductoClientFeign` — llamadas reales via OpenFeign

Los mocks retornan datos distintos según el `cocinaId`:
- `COCINA-DULCE` → postres (Brownie, Cheesecake, Tiramisú)
- `COCINA-CELIACA` → sin gluten (Tostada de Palta, Bowl de Quinoa, Rolls de Primavera)
- `COCINA-VEGANA` → vegana (Buddha Bowl, Salteado de Tofu, Curry de Garbanzos)

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

**14 tests unitarios:**
- `PlanDiarioServiceImplTest` — lógica de generación y consulta de planes
- `HistorialVentasServiceImplTest` — filtros de historial
- `PlanDiarioControllerTest` — endpoints REST (MockMvc)
- `HistorialVentasControllerTest` — endpoints REST (MockMvc)