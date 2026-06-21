# Order Service

Microservicio de órdenes y pagos. Puerto: `8083`.

```
Cliente            Kitchen              Order               Fridge
  │                  │                   │                    │
  │ POST plan-diario│                   │                    │
  │ ────────────────>│                   │                    │
  │                  │                   │                    │
  │                  │ GET sales/cocina  │                    │
  │                  │ ──────────────────>│                    │
  │                  │                   │                    │
  │                  │   GET products    │                    │
  │                  │   by cocina       │                    │
  │                  │   (product-svc)   │                    │
  │                  │                   │                    │
  │                  │   findCompleted   │                    │
  │                  │   OrdersBetween   │                    │
  │                  │   (DB)            │                    │
  │                  │                   │                    │
  │                  │ <────── sales ────│                    │
  │                  │                   │                    │
  │                  │ GET /remanente    │                    │
  │                  │ ───────────────────────────────────────>│
  │                  │                   │                    │
  │                  │ <───────── stock ───────────────────────│
  │                  │                   │                    │
  │                  │                   │                    │
  │                  │ calc: avg - stock │                    │
  │                  │ = suggestedQty    │                    │
  │                  │                   │                    │
  │                  │ save DailyPlan    │                    │
  │                  │ (DB propia)       │                    │
  │                  │                   │                    │
  │ <─── plan ───────│                   │                    │
  │                  │                   │                    │
```

## Endpoints

### `GET /api/v1/ordenes/historial-ventas`

Historial de ventas (órdenes con status `PAID` o `PICKED_UP`).

```bash
# Todos los registros
curl 'http://localhost:8083/api/v1/ordenes/historial-ventas' -H 'accept: */*'

# Por rango de fechas
curl 'http://localhost:8083/api/v1/ordenes/historial-ventas?from=2026-06-01&to=2026-06-22' -H 'accept: */*'

# Por producto
curl 'http://localhost:8083/api/v1/ordenes/historial-ventas?productId=101' -H 'accept: */*'

# Por heladera
curl 'http://localhost:8083/api/v1/ordenes/historial-ventas?fridgeId=1' -H 'accept: */*'

# Combinado
curl 'http://localhost:8083/api/v1/ordenes/historial-ventas?from=2026-06-01&to=2026-06-22&productId=103&fridgeId=1' -H 'accept: */*'
```

### `GET /api/v1/ordenes/historial-ventas/cocina/{cocinaId}`

Ventas agregadas por producto para una cocina específica. Lo consume kitchen-service para `plan-diario`.

```bash
curl 'http://localhost:8083/api/v1/ordenes/historial-ventas/cocina/COCINA-DULCE?from=2026-06-14&to=2026-06-20' -H 'accept: */*'
```

## Plan diario (kitchen-service)

Con los datos de prueba, podés generar un plan diario desde kitchen-service:

```bash
# Generar plan para COCINA-DULCE
curl -X POST 'http://localhost:8084/api/v1/cocina/plan-diario?cocinaId=COCINA-DULCE&fecha=2026-06-22'

# Consultar el plan generado
curl 'http://localhost:8084/api/v1/cocina/plan-diario?cocinaId=COCINA-DULCE&fecha=2026-06-22'
```

## Datos de prueba

El archivo `init.sql` se ejecuta al iniciar PostgreSQL con Docker e inserta:

- **16 órdenes** con status `PAID` o `PICKED_UP`
- Distribuidas en los últimos 12 días
- Productos de las 3 cocinas:
  - `COCINA-DULCE` (IDs 101-103) → fridges 1, 2
  - `COCINA-CELIACA` (IDs 201-203) → fridges 3, 4
  - `COCINA-VEGANA` (IDs 301-303) → fridges 5, 6

Para reiniciar los datos:

```bash
docker-compose down -v && docker-compose up -d
```

### Agregar órdenes vía API

Requiere product-service, fridge-service y payment-gateway corriendo.

```bash
# 1. Crear orden (status PENDING)
curl -X POST 'http://localhost:8083/api/v1/ordenes' \
  -H 'Content-Type: application/json' \
  -d '{"userId":1,"fridgeId":1,"items":[{"productId":101,"productName":"Brownie de Chocolate","quantity":5,"unitPrice":7500}]}'

# 2. Pagar (status → PAID)
curl -X POST 'http://localhost:8083/api/v1/ordenes/17/pagar'

# 3. Confirmar retiro (status → PICKED_UP, opcional)
curl -X POST 'http://localhost:8083/api/v1/ordenes/17/confirmar-retiro'
```
