# API: notification-service

**Puerto default:** `8087`  
**Base path:** `/api/v1/notificaciones`  
**Swagger docs:** `/api/v1/notificaciones/v3/api-docs`

---

## Endpoints

### Health Check

| | |
|---|---|
| **Método** | `GET` |
| **URL** | `/api/v1/notificaciones/health` |
| **Consumidor** | Discovery service (Eureka) |
| **Estado** | ✅ Implementado (`HealthController`) |

**Respuesta:** `200 OK`
```json
{ "status": "UP", "service": "notification-service" }
```

---

### Suscribir / Actualizar suscripción

| | |
|---|---|
| **Método** | `POST` |
| **URL** | `/api/v1/notificaciones/suscribir` |
| **Consumidor** | Frontend (user registration / preferences) |
| **Estado** | ✅ Implementado |

**Request Body:**

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `userId` | `Long` | Sí | ID del usuario |
| `deviceToken` | `String` | Sí | Token del dispositivo para push |
| `productPreferences` | `List<Long>` | No | IDs de productos a monitorear |

**Respuesta:** `201 Created` — `SubscriptionResponseDTO`

---

### Obtener suscripción

| | |
|---|---|
| **Método** | `GET` |
| **URL** | `/api/v1/notificaciones/suscribir/{userId}` |
| **Consumidor** | Frontend |
| **Estado** | ✅ Implementado |

**Respuesta:** `200 OK` — `SubscriptionResponseDTO`  
**Error:** `404 Not Found` si el userId no tiene suscripción

---

### Actualizar suscripción parcialmente

| | |
|---|---|
| **Método** | `PUT` |
| **URL** | `/api/v1/notificaciones/suscribir/{userId}` |
| **Consumidor** | Frontend |
| **Estado** | ✅ Implementado |

**Request Body** (ambos campos opcionales para PATCH):

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `deviceToken` | `String` | Nuevo token de dispositivo |
| `productPreferences` | `List<Long>` | Nueva lista de productos |

**Respuesta:** `200 OK` — `SubscriptionResponseDTO`

---

### Eliminar suscripción

| | |
|---|---|
| **Método** | `DELETE` |
| **URL** | `/api/v1/notificaciones/suscribir/{userId}` |
| **Consumidor** | Frontend |
| **Estado** | ✅ Implementado |

**Respuesta:** `204 No Content`  
**Error:** `404 Not Found` si el userId no tiene suscripción

---

### Enviar notificaciones (heladera reconectada)

| | |
|---|---|
| **Método** | `POST` |
| **URL** | `/api/v1/notificaciones/enviar` |
| **Consumidor** | `fridge-service` (`NotificacionClientFeign`) |
| **Estado** | ✅ Implementado |

> **NOTA:** El Feign client en fridge-service actualmente apunta a `/api/v1/notificaciones/producto-disponible` — debe corregirse a `/api/v1/notificaciones/enviar`.

**Request Body:**

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `fridgeId` | `Long` | Sí | ID de la heladera que se reconectó |
| `productIds` | `List<Long>` | Sí | IDs de productos ahora disponibles |

**Respuesta:** `202 Accepted` (sin body)

---

### Obtener notificaciones de un usuario

| | |
|---|---|
| **Método** | `GET` |
| **URL** | `/api/v1/notificaciones/usuario/{userId}` |
| **Consumidor** | Frontend |
| **Estado** | ✅ Implementado |

**Respuesta:** `200 OK` — `List<NotificationResponseDTO>`

**NotificationResponseDTO:**

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `String` | ID en MongoDB |
| `userId` | `Long` | ID del usuario |
| `productId` | `Long` | ID del producto disponible |
| `fridgeId` | `Long` | ID de la heladera |
| `message` | `String` | Mensaje de la notificación |
| `read` | `Boolean` | Si fue leída |
| `sentAt` | `LocalDateTime` | Fecha de envío |
| `readAt` | `LocalDateTime` | `null` si no fue leída aún |

---

### Obtener notificaciones no leídas

| | |
|---|---|
| **Método** | `GET` |
| **URL** | `/api/v1/notificaciones/usuario/{userId}/no-leidas` |
| **Consumidor** | Frontend |
| **Estado** | ✅ Implementado |

**Respuesta:** `200 OK` — `List<NotificationResponseDTO>` (solo las no leídas)

---

### Marcar notificación como leída

| | |
|---|---|
| **Método** | `PUT` |
| **URL** | `/api/v1/notificaciones/{id}/leer` |
| **Consumidor** | Frontend |
| **Estado** | ✅ Implementado |

**Respuesta:** `200 OK` — `NotificationResponseDTO` (con `read: true`, `readAt` actualizado)  
**Error:** `404 Not Found` si el id no existe

---

## Diagrama de integración

```
┌──────────────┐     POST /enviar (fridgeId, productIds)     ┌──────────────────────┐
│ fridge-service │ ────────────────────────────────────────────▶ notification-service │
└──────────────┘                                               └──────────────────────┘
                                                                       │
                                                          ┌────────────┴────────────┐
                                                          │                         │
                                                          ▼                         ▼
                                                   MongoDB                      Push (mock)
                                              (notificaciones)              (NotificationPushMockImpl)
                                              (suscripciones)
```

## Notas

- notification-service **no depende de ningún otro microservicio** — opera solo con MongoDB
- Las notificaciones push están mockeadas en `NotificationPushMockImpl` (solo logea)
- Las suscripciones son upsert: `POST /suscribir` crea o actualiza según exista el userId
