# Contrato: order-service - Historial de Ventas

## Endpoint: Ventas Recientes

| | |
|---|---|
| **Método** | `GET` |
| **URL** | `/api/v1/ordenes/historial-ventas` |
| **Servicio** | `order-service` (puerto default `8083`) |
| **Feign Client** | `OrdenClientFeign` |

> **Estado: PENDIENTE DE IMPLEMENTAR** en order-service.
> kitchen-service ya tiene el Feign client listo, pero order-service aún no expone este endpoint.

## Parámetros

| Nombre | Tipo | Ubicación | Requerido | Descripción |
|--------|------|-----------|-----------|-------------|
| `from` | `LocalDate` | Query param | Sí | Fecha inicio del rango (formato `YYYY-MM-DD`) |
| `to` | `LocalDate` | Query param | Sí | Fecha fin del rango (formato `YYYY-MM-DD`) |

## Respuesta

**Status:** `200 OK`

**Body:** `List<VentaHistoricaResponseDTO>`

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `productId` | `Long` | ID del producto |
| `productName` | `String` | Nombre del producto |
| `fridgeId` | `Long` | ID de la heladera donde se vendió |
| `quantity` | `Integer` | Cantidad vendida |
| `totalAmount` | `BigDecimal` | Monto total de la venta |
| `date` | `LocalDate` | Fecha de la venta |

## Ejemplo

**Request:**
```
GET /api/v1/ordenes/historial-ventas?from=2026-06-11&to=2026-06-17
```

**Response:**
```json
[
  {
    "productId": 101,
    "productName": "Brownie de Chocolate",
    "fridgeId": 1,
    "quantity": 10,
    "totalAmount": 75000.00,
    "date": "2026-06-19"
  },
  {
    "productId": 101,
    "productName": "Brownie de Chocolate",
    "fridgeId": 2,
    "quantity": 8,
    "totalAmount": 60000.00,
    "date": "2026-06-18"
  },
  {
    "productId": 201,
    "productName": "Tostada de Palta Sin Gluten",
    "fridgeId": 3,
    "quantity": 12,
    "totalAmount": 86400.00,
    "date": "2026-06-19"
  },
  {
    "productId": 301,
    "productName": "Buddha Bowl Vegano",
    "fridgeId": 5,
    "quantity": 12,
    "totalAmount": 102000.00,
    "date": "2026-06-19"
  }
]
```

## Notas

- kitchen-service usa estos datos para consultar ventas históricas individuales
- Si order-service no está disponible, el perfil `dev` usa `OrdenClientMockImpl` con datos que cubren las 3 cocinas

---

## Endpoint: Ventas por Cocina

| | |
|---|---|
| **Método** | `GET` |
| **URL** | `/api/v1/ordenes/historial-ventas/cocina/{cocinaId}` |
| **Servicio** | `order-service` (puerto default `8083`) |
| **Feign Client** | `OrdenClientFeign` |

> **Estado: PENDIENTE DE IMPLEMENTAR** en order-service.
> kitchen-service ya tiene el Feign client listo para este endpoint. Este endpoint debe retornar las ventas agregadas por producto para una cocina fantasma específica en un rango de fechas.

### Parámetros

| Nombre | Tipo | Ubicación | Requerido | Descripción |
|--------|------|-----------|-----------|-------------|
| `cocinaId` | `String` | Path variable | Sí | ID de la cocina fantasma |
| `from` | `LocalDate` | Query param | Sí | Fecha inicio del rango (formato `YYYY-MM-DD`) |
| `to` | `LocalDate` | Query param | Sí | Fecha fin del rango (formato `YYYY-MM-DD`) |

### Respuesta

**Status:** `200 OK`

**Body:** `List<ProductoVentaDTO>`

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `productId` | `Long` | ID del producto |
| `productName` | `String` | Nombre del producto |
| `totalVendido` | `Integer` | Cantidad total vendida en el rango |
| `totalMonto` | `BigDecimal` | Monto total acumulado |

### Ejemplos

#### COCINA-DULCE

**Request:**
```
GET /api/v1/ordenes/historial-ventas/cocina/COCINA-DULCE?from=2026-06-11&to=2026-06-17
```

**Response:**
```json
[
  {
    "productId": 101,
    "productName": "Brownie de Chocolate",
    "totalVendido": 70,
    "totalMonto": 525000.00
  },
  {
    "productId": 102,
    "productName": "Cheesecake",
    "totalVendido": 49,
    "totalMonto": 465500.00
  },
  {
    "productId": 103,
    "productName": "Tiramisú",
    "totalVendido": 56,
    "totalMonto": 492800.00
  }
]
```

**Cálculo de plan diario (últimos 7 días):**
- Brownie: avg = ceil(70/7) = 10, remainder = 5, sugerido = 10 - 5 = **5**
- Cheesecake: avg = ceil(49/7) = 7, remainder = 3, sugerido = 7 - 3 = **4**
- Tiramisú: avg = ceil(56/7) = 8, remainder = 5, sugerido = 8 - 5 = **3**

#### COCINA-CELIACA

**Request:**
```
GET /api/v1/ordenes/historial-ventas/cocina/COCINA-CELIACA?from=2026-06-11&to=2026-06-17
```

**Response:**
```json
[
  {
    "productId": 201,
    "productName": "Tostada de Palta Sin Gluten",
    "totalVendido": 84,
    "totalMonto": 604800.00
  },
  {
    "productId": 202,
    "productName": "Bowl de Quinoa Sin Gluten",
    "totalVendido": 56,
    "totalMonto": 548800.00
  },
  {
    "productId": 203,
    "productName": "Rolls de Primavera de Arroz",
    "totalVendido": 70,
    "totalMonto": 455000.00
  }
]
```

**Cálculo de plan diario (últimos 7 días):**
- Tostada: avg = ceil(84/7) = 12, remainder = 4, sugerido = 12 - 4 = **8**
- Bowl Quinoa: avg = ceil(56/7) = 8, remainder = 4, sugerido = 8 - 4 = **4**
- Rolls: avg = ceil(70/7) = 10, remainder = 5, sugerido = 10 - 5 = **5**

#### COCINA-VEGANA

**Request:**
```
GET /api/v1/ordenes/historial-ventas/cocina/COCINA-VEGANA?from=2026-06-11&to=2026-06-17
```

**Response:**
```json
[
  {
    "productId": 301,
    "productName": "Buddha Bowl Vegano",
    "totalVendido": 84,
    "totalMonto": 714000.00
  },
  {
    "productId": 302,
    "productName": "Salteado de Tofu",
    "totalVendido": 49,
    "totalMonto": 382200.00
  },
  {
    "productId": 303,
    "productName": "Curry de Garbanzos",
    "totalVendido": 63,
    "totalMonto": 579600.00
  }
]
```

**Cálculo de plan diario (últimos 7 días):**
- Buddha Bowl: avg = ceil(84/7) = 12, remainder = 5, sugerido = 12 - 5 = **7**
- Salteado Tofu: avg = ceil(49/7) = 7, remainder = 3, sugerido = 7 - 3 = **4**
- Curry: avg = ceil(63/7) = 9, remainder = 5, sugerido = 9 - 5 = **4**

### Notas

- kitchen-service usa estos datos para calcular el promedio diario de ventas por producto y generar el plan diario de producción
- La fórmula es: `sugerido = ceil(totalVendido / 7) - remainderTotal` (solo si > 0)
- El rango de fechas que se consulta es siempre los últimos 7 días (`HISTORY_DAYS = 7`)
- Las ventas deben estar filtradas por `cocinaId` (es decir, solo productos vendidos desde heladeras asociadas a esa cocina fantasma)
- `totalVendido` es la suma de todas las cantidades vendidas de ese producto en el rango de fechas
- `totalMonto` es la suma del monto total de las ventas de ese producto en el rango
- Si order-service no está disponible, el perfil `dev` usa `OrdenClientMockImpl` con datos diferenciados por cocina
- Las cocinas disponibles son: `COCINA-DULCE` (postres), `COCINA-CELIACA` (sin gluten), `COCINA-VEGANA` (vegana)