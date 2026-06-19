# Contrato: product-service - Nombre de Producto

## Endpoint

| | |
|---|---|
| **Método** | `GET` |
| **URL** | `/api/v1/productos/{id}/nombre` |
| **Servicio** | `product-service` (puerto default `8081`) |
| **Feign Client** | `ProductoClientFeign` |

> **Estado: IMPLEMENTADO** en product-service (`ProductController.getProductNameById`).

## Parámetros

| Nombre | Tipo | Ubicación | Requerido | Descripción |
|--------|------|-----------|-----------|-------------|
| `id` | `Long` | Path variable | Sí | ID del producto |

## Respuesta

**Status:** `200 OK`

**Body:** `String` (nombre del producto, sin envoltorio DTO)

## Ejemplo

**Request:**
```
GET /api/v1/productos/101/nombre
```

**Response:**
```
"Ensalada César"
```

## Errores

| Status | Condición |
|--------|-----------|
| `404 Not Found` | El producto con el `id` dado no existe (`ProductNotFoundException`) |

## Notas

- Endpoint liviano pensado para comunicación entre microservicios
- Si product-service no está disponible, el perfil `dev` usa `ProductoClientMockImpl` con un map hardcodeado de 3 productos (101, 102, 103)
