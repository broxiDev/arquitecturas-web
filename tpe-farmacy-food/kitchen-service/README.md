# Kitchen Service — Planificación de Cocina (F4)

**Puerto:** 8084  
**Base Path:** `/api/v1/cocina`  
**Bases de Datos:** PostgreSQL (planes) + MongoDB (historial)

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

**Ejemplo de respuesta:**
```json
{
  "id": 1,
  "date": "2026-06-14",
  "createdAt": "2026-06-14T08:00:00",
  "items": [
    { "productId": 101, "productName": "Ensalada César", "suggestedQuantity": 10 },
    { "productId": 102, "productName": "Bowl Proteico", "suggestedQuantity": 6 }
  ]
}
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

**Flujo de consulta:**
```
GET /api/v1/cocina/historial-ventas?from=2026-06-01&to=2026-06-14&fridgeId=123
    └── Query en MongoDB (VentaHistorica)
        ├── Filtros opcionales: from, to, productId, fridgeId
        └── Devuelve lista de registros de ventas
```

**Ejemplo de respuesta:**
```json
[
  {
    "productId": 101,
    "productName": "Ensalada César",
    "fridgeId": 1,
    "quantity": 12,
    "totalAmount": 6000.00,
    "date": "2026-06-13"
  },
  {
    "productId": 101,
    "productName": "Ensalada César",
    "fridgeId": 2,
    "quantity": 8,
    "totalAmount": 4000.00,
    "date": "2026-06-12"
  }
]
```

**Endpoints:**
| Método | Path | Descripción |
|---|---|---|
| GET | `/api/v1/cocina/historial-ventas` | Todas las ventas |
| GET | `/api/v1/cocina/historial-ventas?from=&to=` | Filtrar por rango de fechas |
| GET | `/api/v1/cocina/historial-ventas?productId=` | Filtrar por producto |
| GET | `/api/v1/cocina/historial-ventas?fridgeId=` | Filtrar por heladera |

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
docker compose up -d
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

## Estructura de paquetes

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
│   │   ├── DailyPlan.java
│   │   └── PlanItem.java
│   └── mongo/
│       └── VentaHistorica.java
├── dto/
│   ├── PlanDiarioResponseDTO.java
│   ├── ItemPlanDTO.java
│   ├── VentaHistoricaResponseDTO.java
│   ├── VentasResumenDTO.java
│   └── ProductoVentaDTO.java
├── client/
│   ├── OrdenClient.java
│   ├── OrdenClientMockImpl.java
│   ├── OrdenClientFeign.java
│   ├── ProductoClient.java
│   ├── ProductoClientMockImpl.java
│   └── ProductoClientFeign.java
└── exception/
    ├── PlanNotFoundException.java
    └── GlobalExceptionHandler.java
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
