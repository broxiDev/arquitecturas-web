# Contrato: fridge-service - Remanente de Productos por Cocina

## Endpoint

| | |
|---|---|
| **Método** | `GET` |
| **URL** | `/api/v1/heladeras/cocina/{cocinaId}/remanente` |
| **Servicio** | `fridge-service` (puerto default `8082`) |
| **Feign Client** | `FridgeClientFeign` |

> **Estado: PENDIENTE DE IMPLEMENTAR** en fridge-service.
> kitchen-service ya tiene el Feign client listo, pero fridge-service aún no expone este endpoint.

## Parámetros

| Nombre | Tipo | Ubicación | Requerido | Descripción |
|--------|------|-----------|-----------|-------------|
| `cocinaId` | `String` | Path variable | Sí | ID de la cocina fantasma |

## Respuesta

**Status:** `200 OK`

**Body:** `List<FridgeRemainderDTO>`

### FridgeRemainderDTO

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `fridgeId` | `Long` | ID de la heladera |
| `products` | `List<ProductRemainderDTO>` | Lista de productos con su remanente |

### ProductRemainderDTO

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `productId` | `Long` | ID del producto |
| `productName` | `String` | Nombre del producto |
| `quantity` | `Integer` | Cantidad remanente en esa heladera |

## Ejemplos

### COCINA-DULCE

**Request:**
```
GET /api/v1/heladeras/cocina/COCINA-DULCE/remanente
```

**Response:**
```json
[
  {
    "fridgeId": 1,
    "products": [
      { "productId": 101, "productName": "Brownie de Chocolate", "quantity": 3 },
      { "productId": 102, "productName": "Cheesecake", "quantity": 2 },
      { "productId": 103, "productName": "Tiramisú", "quantity": 2 }
    ]
  },
  {
    "fridgeId": 2,
    "products": [
      { "productId": 101, "productName": "Brownie de Chocolate", "quantity": 2 },
      { "productId": 102, "productName": "Cheesecake", "quantity": 1 },
      { "productId": 103, "productName": "Tiramisú", "quantity": 3 }
    ]
  }
]
```

**Cálculo de plan diario:** remanentes totales = Brownie 5, Cheesecake 3, Tiramisú 5

### COCINA-CELIACA

**Request:**
```
GET /api/v1/heladeras/cocina/COCINA-CELIACA/remanente
```

**Response:**
```json
[
  {
    "fridgeId": 3,
    "products": [
      { "productId": 201, "productName": "Tostada de Palta Sin Gluten", "quantity": 3 },
      { "productId": 202, "productName": "Bowl de Quinoa Sin Gluten", "quantity": 2 },
      { "productId": 203, "productName": "Rolls de Primavera de Arroz", "quantity": 2 }
    ]
  },
  {
    "fridgeId": 4,
    "products": [
      { "productId": 201, "productName": "Tostada de Palta Sin Gluten", "quantity": 1 },
      { "productId": 202, "productName": "Bowl de Quinoa Sin Gluten", "quantity": 2 },
      { "productId": 203, "productName": "Rolls de Primavera de Arroz", "quantity": 3 }
    ]
  }
]
```

**Cálculo de plan diario:** remanentes totales = Tostada 4, Bowl Quinoa 4, Rolls 5

### COCINA-VEGANA

**Request:**
```
GET /api/v1/heladeras/cocina/COCINA-VEGANA/remanente
```

**Response:**
```json
[
  {
    "fridgeId": 5,
    "products": [
      { "productId": 301, "productName": "Buddha Bowl Vegano", "quantity": 3 },
      { "productId": 302, "productName": "Salteado de Tofu", "quantity": 2 },
      { "productId": 303, "productName": "Curry de Garbanzos", "quantity": 3 }
    ]
  },
  {
    "fridgeId": 6,
    "products": [
      { "productId": 301, "productName": "Buddha Bowl Vegano", "quantity": 2 },
      { "productId": 302, "productName": "Salteado de Tofu", "quantity": 1 },
      { "productId": 303, "productName": "Curry de Garbanzos", "quantity": 2 }
    ]
  }
]
```

**Cálculo de plan diario:** remanentes totales = Buddha Bowl 5, Salteado Tofu 3, Curry 5

## Notas

- kitchen-service usa estos datos para calcular el remanente total de productos en heladeras y descontarlo de la cantidad sugerida en el plan diario
- La fórmula del plan diario es: `sugerido = ceil(totalVendido / 7) - remanenteTotal` (solo si > 0)
- Si fridge-service no está disponible, el perfil `dev` usa `FridgeClientMockImpl` con datos diferenciados por cocina
- Las cocinas disponibles son: `COCINA-DULCE` (postres), `COCINA-CELIACA` (sin gluten), `COCINA-VEGANA` (vegana)