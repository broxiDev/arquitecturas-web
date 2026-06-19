# Contrato: order-service - Historial de Compras por Usuario

## Endpoint

| | |
|---|---|
| **Método** | `GET` |
| **URL** | `/api/v1/ordenes/usuario/{userId}` |
| **Servicio** | `order-service` (puerto default `8083`) |
| **Feign Client** | `OrdenClientFeign` |

> **Estado: IMPLEMENTADO** en order-service (`OrderController.getByUser`).

## Parámetros

| Nombre | Tipo | Ubicación | Requerido | Descripción |
|--------|------|-----------|-----------|-------------|
| `userId` | `Long` | Path variable | Sí | ID del usuario |

## Respuesta

**Status:** `200 OK`

**Body:** `List<OrderResponseDTO>`

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `orderId` | `Long` | ID de la orden |
| `userId` | `Long` | ID del usuario que realizó la compra |
| `fridgeId` | `Long` | ID de la heladera donde se retiró |
| `items` | `List<OrderItemDTO>` | Lista de productos en la orden |
| `total` | `double` | Monto total de la orden |
| `status` | `String` | Estado de la orden (ej: `COMPLETED`, `PENDING`) |
| `paymentId` | `String` | ID del pago asociado |
| `createdAt` | `LocalDateTime` | Fecha y hora de creación |
| `updatedAt` | `LocalDateTime` | Fecha y hora de última actualización |

### OrderItemDTO

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `productId` | `Long` | ID del producto |
| `productName` | `String` | Nombre del producto |
| `quantity` | `Integer` | Cantidad comprada |
| `unitPrice` | `double` | Precio unitario del producto |

## Ejemplo

**Request:**
```
GET /api/v1/ordenes/usuario/1
```

**Response:**
```json
[
  {
    "orderId": 1,
    "userId": 1,
    "fridgeId": 1,
    "items": [
      {
        "productId": 101,
        "productName": "Ensalada Vegana",
        "quantity": 2,
        "unitPrice": 8500.0
      }
    ],
    "total": 17000.0,
    "status": "COMPLETED",
    "paymentId": "PAY001",
    "createdAt": "2026-06-13T14:30:00",
    "updatedAt": "2026-06-13T14:30:00"
  }
]
```

## Notas

- recommendation-service usa este endpoint para obtener el historial de compras del usuario y excluir productos ya comprados de las recomendaciones
- Los items de cada orden se extraen para obtener los productIds de productos comprados
- El historial se cachea en PostgreSQL con el mismo TTL que las recomendaciones (6 horas por defecto)
- Si order-service no está disponible, el perfil `dev` usa `OrdenClientMockImpl` con datos hardcodeados para usuarios 1 y 2
