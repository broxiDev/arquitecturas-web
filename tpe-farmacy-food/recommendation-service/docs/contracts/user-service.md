# Contrato: user-service - Obtener Usuario

## Endpoint

| | |
|---|---|
| **Método** | `GET` |
| **URL** | `/api/v1/usuarios/{id}` |
| **Servicio** | `user-service` (puerto default `8086`) |
| **Feign Client** | `UsuarioClientFeign` |

> **Estado: IMPLEMENTADO** en user-service (`UserController.getById`).

## Parámetros

| Nombre | Tipo | Ubicación | Requerido | Descripción |
|--------|------|-----------|-----------|-------------|
| `id` | `Long` | Path variable | Sí | ID del usuario |

## Respuesta

**Status:** `200 OK`

**Body:** `UserResponse`

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `Long` | ID del usuario |
| `name` | `String` | Nombre completo del usuario |
| `email` | `String` | Email del usuario |
| `dietaryPreferences` | `List<String>` | Lista de preferencias dietarias (ej: `["VEGANO", "SIN_GLUTEN"]`) |
| `createdAt` | `LocalDateTime` | Fecha de registro del usuario |

## Ejemplo

**Request:**
```
GET /api/v1/usuarios/1
```

**Response:**
```json
{
  "id": 1,
  "name": "Juan Perez",
  "email": "juan@example.com",
  "dietaryPreferences": ["VEGANO", "SIN_GLUTEN"],
  "createdAt": "2026-05-19T10:30:00"
}
```

## Errores

| Status | Condición |
|--------|-----------|
| `404 Not Found` | El usuario con el `id` dado no existe |

## Notas

- recommendation-service usa este endpoint para obtener las preferencias dietarias del usuario y filtrar productos recomendados
- Las preferencias vienen como `List<String>`, recommendation-service las convierte a String separado por comas para almacenar en cache local
- Si user-service no está disponible, el perfil `dev` usa `UsuarioClientMockImpl` con datos hardcodeados para usuarios 1 y 2
