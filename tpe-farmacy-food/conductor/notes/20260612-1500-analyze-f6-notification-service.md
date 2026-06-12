# Análisis F6 — Notificaciones (notification-service)

**Responsable:** Mati  
**Microservicio:** notification-service  
**Puerto:** 8087  
**Base path:** `/api/v1/notificaciones`

---

## User Stories Cubiertas

- **US-16**: Cliente recibe notificación push cuando un producto que marcó como favorito vuelve a estar disponible en una heladera cercana

## Entidades (MongoDB — notification-service DB)

### Subscription (MongoDB)

| Campo             | Tipo          | Descripción                                   |
|-------------------|---------------|-----------------------------------------------|
| _id               | ObjectId      | Auto-generated                                |
| userId            | Long          | Referencia a user-service                     |
| deviceToken       | String        | Token de dispositivo para push (FCM)          |
| productPreferences| List<Long>    | IDs de productos favoritos                    |
| createdAt         | LocalDateTime | Auto                                          |
| updatedAt         | LocalDateTime | Auto                                          |

**Notas:**
- Un usuario puede tener una única Subscription (1 deviceToken por usuario para MVP).
- `productPreferences` es la lista de productos que el usuario quiere monitorear.
- `deviceToken` es el FCM registration token del dispositivo móvil.

### Notificacion (MongoDB) — auditoría

| Campo             | Tipo          | Descripción                                |
|-------------------|---------------|--------------------------------------------|
| _id               | ObjectId      | Auto-generated                             |
| userId            | Long          | Referencia a user-service                  |
| productId         | Long          | Referencia a product-service               |
| fridgeId          | Long          | Referencia a fridge-service                |
| message           | String        | Texto de la notificación                   |
| read              | Boolean       | Si el usuario ya leyó la notificación      |
| sentAt            | LocalDateTime | Cuándo se envió                            |
| readAt            | LocalDateTime | Cuándo se leyó (nullable)                  |

**Notas:**
- Tabla de auditoría para rastrear notificaciones enviadas y su estado de lectura.
- Permite listar notificaciones (leído/no leído) para el usuario.

## DTOs (Records)

- `SuscripcionCreateDTO(Long userId, String deviceToken, List<Long> productPreferences)`
- `SuscripcionUpdateDTO(String deviceToken, List<Long> productPreferences)`
- `SuscripcionResponseDTO(String id, Long userId, String deviceToken, List<Long> productPreferences, LocalDateTime createdAt)`
- `NotificacionSendDTO(Long userId, Long productId, Long fridgeId, String message)`
- `NotificacionResponseDTO(String id, Long userId, Long productId, Long fridgeId, String message, Boolean read, LocalDateTime sentAt)`

## Endpoints REST (notification-service)

| Método | Path                                           | Descripción                                    | US    |
|--------|------------------------------------------------|------------------------------------------------|-------|
| POST   | `/api/v1/notificaciones/suscribir`              | Suscribirse a notificaciones                   | US-16 |
| PUT    | `/api/v1/notificaciones/suscribir/{userId}`    | Actualizar suscripción (agregar/quitar favoritos) | US-16 |
| POST   | `/api/v1/notificaciones/enviar`                 | Enviar notificación push (interno)            | US-16 |
| GET    | `/api/v1/notificaciones/usuario/{userId}`       | Listar notificaciones de un usuario           | US-16 |
| PUT    | `/api/v1/notificaciones/{id}/leer`             | Marcar notificación como leída                | US-16 |

### Detalle de POST /api/v1/notificaciones/suscribir

- **Body:** `SuscripcionCreateDTO`
- **Flujo:**
  1. Crear o actualizar suscripción del usuario
  2. Si ya existe suscripción para el userId, actualizar deviceToken y productPreferences
  3. Retornar `SuscripcionResponseDTO`

### Detalle de POST /api/v1/notificaciones/enviar (interno)

- **Body:** `NotificacionSendDTO`
- **Flujo:**
  1. Buscar usuarios suscritos al `productId` en sus `productPreferences`
  2. Para cada usuario, enviar notificación push via Firebase Cloud Messaging (FCM)
  3. Guardar registro de notificación en MongoDB
  4. Retornar cantidad de notificaciones enviadas
- **Este endpoint es llamado por:**
  - `fridge-service` cuando un producto vuelve a estar disponible (heladera cambia de OUT_OF_SERVICE/MAINTENANCE a ACTIVE)

### Detalle de GET /api/v1/notificaciones/usuario/{userId}

- **Respuesta:** `List<NotificacionResponseDTO>`
- **Query params opcionales:** `?leido=false` para filtrar solo no leídas

### Detalle de PUT /api/v1/notificaciones/{id}/leer

- Marca notificación como leída
- Actualiza `read = true` y `readAt = now`

## Integración con Firebase Cloud Messaging (FCM)

Para MVP, se usarán dos enfoques:

### Opción A: FCM Real
- Agregar dependencia `firebase-admin` al pom.xml
- Configurar credenciales de Firebase en `application.yml`
- El servicio envía notificaciones push reales al dispositivo del usuario

### Opción B: Mock FCM (MVP recomendado)
- Interface `NotificacionPushService` con implementación `NotificacionPushMockImpl`
- La implementación mock registra la notificación en log y en MongoDB
- Permite desarrollar y probar sin configurar Firebase
- `NotificacionPushFcmImpl` para futura integración real

**Decisión para MVP:** Se implementa con Mock. La interface queda lista para swap a FCM real.

## Integraciones

### Integración Entrante: fridge-service → notification-service

- **Endpoint consumido por fridge-service:** `POST /api/v1/notificaciones/enviar`
- **Trigger:** Cuando el status de una heladera cambia a ACTIVE
- **Payload:** `{ "fridgeId": Long, "productIds": List<Long> }` → notification-service busca suscripciones con esos productIds y envía push

### Integración Entrante (futura): order-service → notification-service

- Posible extensión: notificar al usuario cuando su orden cambia de estado
- No se implementa en MVP

## Estructura de Paquetes

```
com.farmacyfood.notification/
├── NotificationServiceApplication.java
├── controller/
│   └── NotificacionController.java
├── service/
│   ├── SuscripcionService.java
│   ├── SuscripcionServiceImpl.java
│   ├── NotificacionService.java
│   ├── NotificacionServiceImpl.java
│   ├── push/
│   │   ├── NotificacionPushService.java       # Interface
│   │   └── NotificacionPushMockImpl.java      # Mock para MVP
├── repository/
│   ├── SuscripcionRepository.java             # MongoDB
│   └── NotificacionRepository.java            # MongoDB
├── entity/
│   ├── Suscripcion.java
│   └── Notificacion.java
├── dto/
│   ├── SuscripcionCreateDTO.java
│   ├── SuscripcionUpdateDTO.java
│   ├── SuscripcionResponseDTO.java
│   ├── NotificacionSendDTO.java
│   └── NotificacionResponseDTO.java
├── exception/
│   ├── SuscripcionNotFoundException.java
│   └── GlobalExceptionHandler.java
└── config/
    ├── SwaggerConfig.java
    └── MongoConfig.java
```

## Dependencias Maven a Agregar

- `spring-boot-starter-data-mongodb`
- `spring-boot-starter-validation`
- Ya tiene: `spring-boot-starter-web`, `spring-cloud-starter-netflix-eureka-client`, `springdoc`, `lombok`
- **Futuro (no MVP):** `firebase-admin`

## Configuración application.yml

Agregar:
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/farmacyfood_notification
```

## Decisiones Tomadas

1. **MongoDB para notificaciones:** Los datos de suscripciones y notificaciones son flexibles y no relacionales. MongoDB es ideal para este caso de uso.
2. **Mock FCM para MVP:** Se implementa interface con mock para no depender de Firebase en desarrollo. El swap a implementación real es trivial.
3. **Un deviceToken por usuario:** Para MVP, un usuario tiene un solo dispositivo registrado. Futuro: soportar múltiples dispositivos.
4. **Endpoint interno `/enviar`:** El endpoint POST `/api/v1/notificaciones/enviar` es llamado internamente por fridge-service. No requiere autenticación de usuario final, sino autenticación inter-servicio (JWT de servicio).
5. **Auditoría de notificaciones:** Se guarda historial en MongoDB para que el usuario pueda ver sus notificaciones (leídas y no leídas).
6. **Nomenclatura en español:** Endpoints (`/suscribir`, `/enviar`, `/leer`), DTOs y entities en español.
7. **Sin OpenFeign:** notification-service no consume otros servicios (solo es consumido). Por ahora no necesita Feign clients.