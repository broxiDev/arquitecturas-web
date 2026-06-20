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

## Ejemplos

| ID | Nombre | Cocina |
|----|--------|--------|
| 101 | Brownie de Chocolate | COCINA-DULCE |
| 102 | Cheesecake | COCINA-DULCE |
| 103 | Tiramisú | COCINA-DULCE |
| 201 | Tostada de Palta Sin Gluten | COCINA-CELIACA |
| 202 | Bowl de Quinoa Sin Gluten | COCINA-CELIACA |
| 203 | Rolls de Primavera de Arroz | COCINA-CELIACA |
| 301 | Buddha Bowl Vegano | COCINA-VEGANA |
| 302 | Salteado de Tofu | COCINA-VEGANA |
| 303 | Curry de Garbanzos | COCINA-VEGANA |

**Request:**
```
GET /api/v1/productos/101/nombre
```

**Response:**
```
"Brownie de Chocolate"
```

**Request:**
```
GET /api/v1/productos/301/nombre
```

**Response:**
```
"Buddha Bowl Vegano"
```

## Errores

| Status | Condición |
|--------|-----------|
| `404 Not Found` | El producto con el `id` dado no existe (`ProductNotFoundException`) |

## Notas

- Endpoint liviano pensado para comunicación entre microservicios
- Si product-service no está disponible, el perfil `dev` usa `ProductoClientMockImpl` con un map hardcodeado de 9 productos (3 por cocina)

---

## Endpoint: Registrar Producto en Catálogo

| | |
|---|---|
| **Método** | `POST` |
| **URL** | `/api/v1/productos/cocina/{cocinaId}` |
| **Servicio** | `product-service` (puerto default `8081`) |
| **Feign Client** | `ProductoClientFeign` |

> **Estado: IMPLEMENTADO** en product-service.

### Parámetros

| Nombre | Tipo | Ubicación | Requerido | Descripción |
|--------|------|-----------|-----------|-------------|
| `cocinaId` | `String` | Path variable | Sí | ID de la cocina fantasma |

### Request Body

```json
{
  "name": "Brownie de Chocolate",
  "description": "Brownie intenso con centro de chocolate fundido",
  "dietaryCategory": "DULCE",
  "price": 7500.00,
  "imageUrl": "/images/brownie-chocolate.jpg",
  "nutritionalInfo": "Calorías: 450kcal, Proteínas: 8g, Carbohidratos: 55g, Grasas: 22g",
  "conservacionTemperature": 4.00
}
```

### Respuesta

**Status:** `200 OK`

```json
{
  "id": 101,
  "name": "Brownie de Chocolate",
  "description": "Brownie intenso con centro de chocolate fundido",
  "dietaryCategory": "DULCE",
  "price": 7500.00,
  "imageUrl": "/images/brownie-chocolate.jpg",
  "nutritionalInfo": "Calorías: 450kcal, Proteínas: 8g, Carbohidratos: 55g, Grasas: 22g",
  "conservacionTemperature": 4.00,
  "cocinaId": "cocina-dulce",
  "createdAt": "2026-06-14T10:00:00",
  "updatedAt": "2026-06-14T10:00:00"
}
```