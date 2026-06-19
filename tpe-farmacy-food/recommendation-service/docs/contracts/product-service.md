# Contrato: product-service - Productos por Categoría

## Endpoint

| | |
|---|---|
| **Método** | `GET` |
| **URL** | `/api/v1/productos` |
| **Servicio** | `product-service` (puerto default `8081`) |
| **Feign Client** | `ProductoClientFeign` |

> **Estado: IMPLEMENTADO** en product-service (`ProductController.getAllProducts`).
> El parámetro de query se llama `category` (no `categoria`).

## Parámetros

| Nombre | Tipo | Ubicación | Requerido | Descripción |
|--------|------|-----------|-----------|-------------|
| `category` | `String` | Query param | No | Categoría dietaria del producto (ej: `VEGANO`, `SIN_GLUTEN`, `VEGETARIANO`). Si no se envía, retorna todos los productos. |

## Respuesta

**Status:** `200 OK`

**Body:** `List<ProductResponse>`

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `Long` | ID del producto |
| `name` | `String` | Nombre del producto |
| `description` | `String` | Descripción del producto |
| `dietaryCategory` | `String` | Categoría dietaria del producto |
| `price` | `BigDecimal` | Precio del producto |
| `imageUrl` | `String` | URL de la imagen del producto |
| `createdAt` | `LocalDateTime` | Fecha de creación |
| `updatedAt` | `LocalDateTime` | Fecha de última actualización |

## Ejemplo

**Request:**
```
GET /api/v1/productos?category=VEGANO
```

**Response:**
```json
[
  {
    "id": 301,
    "name": "Ensalada Vegana Premium",
    "description": "Ensalada con quinoa y vegetales",
    "dietaryCategory": "VEGANO",
    "price": 8500.00,
    "imageUrl": "/images/ensalada-vegana.jpg",
    "createdAt": "2026-05-19T10:30:00",
    "updatedAt": "2026-06-13T14:30:00"
  },
  {
    "id": 302,
    "name": "Bowl Vegano Energético",
    "description": "Bowl con arroz integral y tofu",
    "dietaryCategory": "VEGANO",
    "price": 9200.00,
    "imageUrl": "/images/bowl-vegano.jpg",
    "createdAt": "2026-05-24T10:30:00",
    "updatedAt": "2026-06-15T14:30:00"
  }
]
```

## Notas

- recommendation-service consulta este endpoint una vez por cada preferencia dietaria del usuario para obtener productos candidatos a recomendar
- El campo de categoría en la respuesta es `dietaryCategory` (no `category`)
- Los productos ya comprados por el usuario se excluyen del resultado final
- Si product-service no está disponible, el perfil `dev` usa `ProductoClientMockImpl` con datos hardcodeados para las categorías VEGANO, SIN_GLUTEN y VEGETARIANO
