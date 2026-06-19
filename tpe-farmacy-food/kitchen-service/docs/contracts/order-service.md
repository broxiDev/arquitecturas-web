# Contrato: order-service - Historial de Ventas

## Endpoint

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
    "productName": "Ensalada César",
    "fridgeId": 1,
    "quantity": 12,
    "totalAmount": 6000.00,
    "date": "2026-06-16"
  },
  {
    "productId": 102,
    "productName": "Bowl Proteico",
    "fridgeId": 1,
    "quantity": 5,
    "totalAmount": 3500.00,
    "date": "2026-06-15"
  }
]
```

## Notas

- kitchen-service usa estos datos para calcular el promedio diario de ventas y generar el plan diario (`PlanDiarioServiceImpl.generarPlan`)
- El rango de fechas que se consulta es siempre los últimos 7 días (`DIAS_HISTORIAL = 7`)
- Si order-service no está disponible, el perfil `dev` usa `OrdenClientMockImpl` con datos hardcodeados
