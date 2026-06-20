# Contrato: product-service

## Endpoints

### Health Check

| | |
|---|---|
| **Método** | `GET` |
| **URL** | `/api/v1/productos/health` |
| **Servicio** | `product-service` (puerto default `8081`) |

**Response:** `200 OK`
```json
{
  "status": "UP",
  "service": "product-service"
}
```

---

### Listar Productos (con filtro opcional por categoría)

| | |
|---|---|
| **Método** | `GET` |
| **URL** | `/api/v1/productos` |
| **Servicio** | `product-service` (puerto default `8081`) |

**Parámetros:**

| Nombre | Tipo | Ubicación | Requerido | Descripción |
|--------|------|-----------|-----------|-------------|
| `category` | `String` | Query param | No | Categoría dietaria (`VEGANO`, `SIN_GLUTEN`, `VEGETARIANO`, `CLASICA`) |

**Response:** `200 OK` — `List<ProductResponse>`

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `Long` | ID del producto |
| `name` | `String` | Nombre del producto |
| `description` | `String` | Descripción del producto |
| `dietaryCategory` | `String` | Categoría dietaria |
| `price` | `BigDecimal` | Precio del producto |
| `imageUrl` | `String` | URL de la imagen |
| `createdAt` | `LocalDateTime` | Fecha de creación |
| `updatedAt` | `LocalDateTime` | Fecha de última actualización |

**Ejemplo:**
```
GET /api/v1/productos?category=VEGANO
```
```json
[
  {
    "id": 4,
    "name": "Ensalada Vegana Premium",
    "description": "Quinoa, vegetales asados, tofu marinado y vinagreta de limón",
    "dietaryCategory": "VEGANO",
    "price": 8500.00,
    "imageUrl": "/images/ensalada-vegana.jpg",
    "createdAt": "2026-06-19T10:30:00",
    "updatedAt": "2026-06-19T10:30:00"
  }
]
```

---

### Obtener Producto por ID

| | |
|---|---|
| **Método** | `GET` |
| **URL** | `/api/v1/productos/{id}` |
| **Servicio** | `product-service` (puerto default `8081`) |

**Parámetros:**

| Nombre | Tipo | Ubicación | Requerido | Descripción |
|--------|------|-----------|-----------|-------------|
| `id` | `Long` | Path | Sí | ID del producto |

**Response:** `200 OK` — `ProductResponse`

**Errores:** `404 Not Found` — `{"error": "Producto no encontrado", "message": "Producto no encontrado con id: 99"}`

---

### Crear Producto

| | |
|---|---|
| **Método** | `POST` |
| **URL** | `/api/v1/productos` |
| **Servicio** | `product-service` (puerto default `8081`) |
| **Acceso** | Admin de catálogo |

**Body:** `ProductRequest`
```json
{
  "name": "Nuevo Producto",
  "description": "Descripción del producto",
  "dietaryCategory": "VEGANO",
  "price": 5000.00,
  "imageUrl": "/images/nuevo.jpg"
}
```

**Response:** `201 Created` — `ProductResponse`

---

### Actualizar Producto

| | |
|---|---|
| **Método** | `PUT` |
| **URL** | `/api/v1/productos/{id}` |
| **Servicio** | `product-service` (puerto default `8081`) |
| **Acceso** | Admin de catálogo |

**Body:** `ProductRequest` (mismo que creación)

**Response:** `200 OK` — `ProductResponse`

**Errores:** `404 Not Found`

---

### Eliminar Producto

| | |
|---|---|
| **Método** | `DELETE` |
| **URL** | `/api/v1/productos/{id}` |
| **Servicio** | `product-service` (puerto default `8081`) |
| **Acceso** | Admin de catálogo |

**Response:** `204 No Content`

**Errores:** `404 Not Found`

---

### Obtener Nombre de Producto (Consumo Interno)

| | |
|---|---|
| **Método** | `GET` |
| **URL** | `/api/v1/productos/{id}/nombre` |
| **Servicio** | `product-service` (puerto default `8081`) |
| **Feign Client** | `ProductoClientFeign` |

**Parámetros:**

| Nombre | Tipo | Ubicación | Requerido | Descripción |
|--------|------|-----------|-----------|-------------|
| `id` | `Long` | Path | Sí | ID del producto |

**Response:** `200 OK` — `String` (nombre del producto, sin envoltorio DTO)

**Ejemplo:**
```
GET /api/v1/productos/1/nombre
```
```
"Ensalada César"
```

**Errores:** `404 Not Found`

---

### Obtener Productos por Cocina

| | |
|---|---|
| **Método** | `GET` |
| **URL** | `/api/v1/productos/cocina/{cocinaId}` |
| **Servicio** | `product-service` (puerto default `8081`) |

**Parámetros:**

| Nombre | Tipo | Ubicación | Requerido | Descripción |
|--------|------|-----------|-----------|-------------|
| `cocinaId` | `String` | Path | Sí | ID de la cocina fantasma (ej: `cocina-sur`) |

**Response:** `200 OK` — `List<ProductResponse>`

**Ejemplo:**
```
GET /api/v1/productos/cocina/cocina-sur
```
```json
[
  {
    "id": 4,
    "name": "Ensalada Vegana Premium",
    "description": "Quinoa, vegetales asados, tofu marinado y vinagreta de limón",
    "dietaryCategory": "VEGANO",
    "price": 8500.00,
    "imageUrl": "/images/ensalada-vegana.jpg",
    "cocinaId": "cocina-sur",
    "createdAt": "2026-06-19T10:30:00",
    "updatedAt": "2026-06-19T10:30:00"
  }
]
```

**Errores:** No aplica (retorna lista vacía si la cocina no existe o no tiene productos)

---

## Consumidores

- **`fridge-service`**: Consulta existencia de productos para agregar al stock de heladeras
- **`kitchen-service`**: Mapea nombres de productos e inventarios en planificación diaria
- **`recommendation-service`**: Sugiere ítems del catálogo basados en preferencias del usuario
