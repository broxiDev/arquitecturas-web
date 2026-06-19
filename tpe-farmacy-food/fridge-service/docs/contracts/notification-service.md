# Contrato: notification-service - Notificación de Producto Disponible

## Endpoint

| | |
|---|---|
| **Método** | `POST` |
| **URL** | `/api/v1/notificaciones/producto-disponible` |
| **Servicio** | `notification-service` (puerto default `8087`) |
| **Feign Client** | `NotificacionClientFeign` |

> **Estado: PENDIENTE DE IMPLEMENTAR** en notification-service.
> fridge-service ya tiene el Feign client listo, pero notification-service aún no expone este endpoint.

## Parámetros

**Body (JSON):**

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `fridgeId` | `Long` | Sí | ID de la heladera que volvió a estar activa |
| `productIds` | `List<Long>` | Sí | IDs de los productos disponibles en esa heladera |

## Respuesta

**Status:** `204 No Content`

Sin cuerpo en la respuesta.

## Ejemplo

**Request:**
```json
POST /api/v1/notificaciones/producto-disponible
Content-Type: application/json

{
  "fridgeId": 1,
  "productIds": [101, 102, 103]
}
```

**Response:**
```
Status: 204 No Content
```

## Errores

| Status | Condición |
|--------|-----------|
| `400 Bad Request` | Payload inválido (campos requeridos ausentes o con tipo incorrecto) |

## Notas

- Este endpoint es llamado por fridge-service cuando una heladera cambia su estado de `OUT_OF_SERVICE` o `MAINTENANCE` a `ACTIVE`
- notification-service debe buscar las suscripciones activas para los `productIds` recibidos y disparar las notificaciones push correspondientes
- Si notification-service no está disponible, el perfil `dev` usa `NotificacionClientMockImpl` que solo loggea la notificación
- El usuario que recibe la notificación debe tener marcado ese producto como favorito (ver suscripciones en notification-service)
