# API: user-service

**Puerto default:** `8086`  
**Base path:** `/api/v1/usuarios`  
**Swagger docs:** `/api/v1/usuarios/v3/api-docs`  
**DB:** PostgreSQL 16 (`user_db`, schema auto-managed con `ddl-auto: update`)

---

## Endpoints

### Health Check

| | |
|---|---|
| **Método** | `GET` |
| **URL** | `/api/v1/usuarios/health` |
| **Consumidor** | Discovery service (Eureka) |
| **Estado** | ✅ Implementado |

**Respuesta:** `200 OK`
```json
{ "status": "UP", "service": "user-service" }
```

---

### Registrar usuario

| | |
|---|---|
| **Método** | `POST` |
| **URL** | `/api/v1/usuarios/registrar` |
| **Consumidor** | Frontend |
| **Estado** | ✅ Implementado |

**Request Body:**

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `name` | `String` | Sí | Nombre completo |
| `email` | `String` | Sí | Email (único en el sistema) |
| `passwordHash` | `String` | Sí | Contraseña hasheada |
| `dietaryPreferences` | `List<String>` | No | Ej: `["vegano", "sin gluten"]` |

**Respuesta:** `201 Created` — `UserResponse`

**Errores:**
| Status | Condición |
|--------|-----------|
| `400 Bad Request` | Validación fallida |
| `409 Conflict` | Email ya registrado (`DuplicateEmailException`) |

---

### Obtener usuario por ID

| | |
|---|---|
| **Método** | `GET` |
| **URL** | `/api/v1/usuarios/{id}` |
| **Consumidor** | Frontend, `order-service` (`UserClient`), `recommendation-service` (`UsuarioClientFeign`) |
| **Estado** | ✅ Implementado |

**Parámetros:**

| Nombre | Tipo | Ubicación | Requerido |
|--------|------|-----------|-----------|
| `id` | `Long` | Path variable | Sí |

**Respuesta:** `200 OK` — `UserResponse`

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `Long` | ID del usuario |
| `name` | `String` | Nombre completo |
| `email` | `String` | Email |
| `dietaryPreferences` | `List<String>` | Preferencias dietarias |
| `createdAt` | `LocalDateTime` | Fecha de registro |

> `passwordHash` **nunca se retorna** en la respuesta.

**Error:** `404 Not Found` si el id no existe

---

### Actualizar preferencias dietarias

| | |
|---|---|
| **Método** | `PUT` |
| **URL** | `/api/v1/usuarios/{id}/preferencias` |
| **Consumidor** | Frontend |
| **Estado** | ✅ Implementado |

**Request Body:** Lista plana de strings (reemplaza todas las preferencias)
```json
["vegetariano", "sin gluten"]
```

**Respuesta:** `200 OK` — `UserResponse` actualizado  
**Error:** `404 Not Found` si el usuario no existe

---

### Obtener historial de compras

| | |
|---|---|
| **Método** | `GET` |
| **URL** | `/api/v1/usuarios/{id}/historial` |
| **Consumidor** | Frontend |
| **Estado** | ✅ Implementado (proxy a order-service) |

**Comportamiento:**
1. Verifica que el usuario existe (si no → `404`)
2. Delega a `order-service` via Feign (`GET /api/v1/ordenes/usuario/{userId}`)
3. Retorna lo que devuelva order-service sin transformación

**Respuesta:** `200 OK` — `List<?>` (formato definido por order-service)  
**Error:** `404 Not Found` si el usuario no existe

---

## Dependencias externas (outgoing)

### OrderServiceClient (Feign)

| | |
|---|---|
| **Cliente** | `OrderServiceClient` |
| **Servicio destino** | `order-service` (Eureka) |
| **Endpoint** | `GET /api/v1/ordenes/usuario/{userId}` |
| **Uso** | `UserServiceImpl.getPurchaseHistory()` |
| **Estado** | ✅ Implementado |

---

## Servicios que consumen user-service

### order-service

| | |
|---|---|
| **Cliente** | `UserClient` (Feign) |
| **Endpoint consumido** | `GET /api/v1/usuarios/{id}` |
| **DTO** | `UserResponseDTO(id, name, email)` |
| **Estado** | ✅ Implementado |

### recommendation-service

| | |
|---|---|
| **Cliente** | `UsuarioClientFeign` (Feign, perfil `!dev`) |
| **Mock** | `UsuarioClientMockImpl` (perfil `dev`, datos hardcodeados) |
| **Endpoint consumido** | `GET /api/v1/usuarios/{id}` |
| **DTO** | `UsuarioResponseDTO(id, name, email, dietaryPreferences, createdAt)` |
| **Estado** | ✅ Implementado |

---

## Diagrama de integración

```
                    ┌─────────────────────────────────┐
                    │         API GATEWAY             │
                    │   /api/v1/usuarios/** ───────┐  │
                    └──────────────────────────────┘  │
                                                      │
                    ┌─────────────────────────────────┘
                    ▼
         ┌──────────────────────────┐
         │       user-service       │  :8086
         │  @EnableFeignClients     │
         │  DB: PostgreSQL (user_db)│
         └──────┬───────────▲───────┘
                │           │
                │ Feign     │ Feign
                ▼           │
    ┌───────────────────┐   │
    │   order-service   │   │
    │ /ordenes/usuario/ │   │
    └───────────────────┘   │
                            │
         ┌──────────────────┼──────────────┐
         │                  │              │
         ▼                  │              ▼
  ┌──────────────┐          │    ┌─────────────────────┐
  │order-service │          │    │recommendation-service│
  │(UserClient)  │──────────┘    │(UsuarioClientFeign) │
  │GET /usuarios │               │GET /usuarios/{id}   │
  │/{id}         │               │(mock en dev)        │
  └──────────────┘               └─────────────────────┘
```

## Notas

- `passwordHash` se almacena pero **nunca se expone** en respuestas
- No hay autenticación ni Spring Security — endpoints públicos
- `recommendation-service` tiene mock en perfil `dev`, no impacta si user-service no está corriendo
- Hibernate `ddl-auto: update` maneja el schema automáticamente — no necesita scripts SQL
