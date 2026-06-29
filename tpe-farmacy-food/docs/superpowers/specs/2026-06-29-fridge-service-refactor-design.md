# Heladera: independencia de Cocina + Stock con cocinaId/price

## Contexto

Se eliminó el microservicio `product-service`; kitchen-service ahora maneja
productos con su propio `CatalogoProducto` local. Como consecuencia:
- Heladera no debe requerir `cocinaId` al crearse
- Heladera debe poder vincularse a una o varias cocinas post-creación
- Stock debe registrar de qué cocina proviene cada producto y su precio

## Alcance

**Solo se modifica `fridge-service`.** Ningún otro microservicio se toca.

## Cambios

### 1. Heladera entity

| Campo | Cambio |
|---|---|
| `cocinaId` (String) | Eliminado |
| `cocinaIds` (Set\<Long\>) | Nuevo, `@ElementCollection` con tabla `heladera_cocina` |

### 2. DTOs de Heladera

| DTO | Cambio |
|---|---|
| `HeladeraCreateDTO` | Se elimina `cocinaId` |
| `HeladeraUpdateDTO` | Se elimina `cocinaId` |
| `HeladeraResponseDTO` | `cocinaId` (String) → `cocinaIds` (Set\<Long\>) |

### 3. StockHeladera entity

| Campo | Cambio |
|---|---|
| `cocinaId` (Long) | Nuevo, NOT NULL |
| `price` (BigDecimal) | Nuevo, NOT NULL |

Unique constraint: `(heladera_id, cocina_id, product_id)`.

### 4. DTOs de Stock

| DTO | Campo agregado |
|---|---|
| `StockCreateDTO` | `@NotNull Long cocinaId`, `@NotNull BigDecimal price` |
| `StockUpdateDTO` | `@NotNull Long cocinaId`, `BigDecimal price` (opt) |
| `StockResponseDTO` | `Long cocinaId`, `BigDecimal price` |

### 5. Nuevos endpoints

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/v1/heladeras/{id}/cocinas` | Vincula una cocina a la heladera |
| DELETE | `/api/v1/heladeras/{id}/cocinas/{cocinaId}` | Desvincula una cocina |

El POST valida que la cocina existe llamando a kitchen-service via `KitchenClient`.

### 6. KitchenClient (Feign) — nuevo en fridge-service

- `KitchenClient.java` — interfaz
- `KitchenClientFeign.java` — llama a `GET /api/v1/cocina/{cocinaId}` (profile !dev)
- `KitchenClientMockImpl.java` — mock dev con cocinas 1, 2, 3

### 7. Endpoints modificados

| Ruta | Cambio |
|---|---|
| `GET /api/v1/heladeras/cocina/{cocinaId}/remanente` | `cocinaId` pasa de `String` a `Long`. Internamente consulta `StockRepository.findByCocinaId()` |
| `PUT /api/v1/heladeras/{id}/stock` | Busca stock por `(heladeraId, cocinaId, productId)` |

### 8. Repository

- `HeladeraRepository`: se elimina `findByCocinaId(String)`
- `StockRepository`: se agregan `findByCocinaId(Long)` y `findByHeladeraIdAndCocinaIdAndProductId(...)`

### 9. init.sql

- `heladera`: se elimina columna `cocina_id`
- Nueva tabla `heladera_cocina(heladera_id BIGINT, cocina_id BIGINT, PRIMARY KEY(heladera_id, cocina_id))`
- `stock_heladera`: se agregan `cocina_id BIGINT NOT NULL`, `price DECIMAL(10,2) NOT NULL`
- Unique constraint pasa a `(heladera_id, cocina_id, product_id)`
- Seed data: heladeras sin cocina_id, población de `heladera_cocina`, stock con cocina_id y price

## No se modifica

- kitchen-service, api-gateway, ni ningún otro microservicio
- `FridgeRemainderDTO`, `ProductRemainderDTO` (quedan igual)
- `StockServiceImpl.addStock()` no valida productName (se mantiene decisión de no validar por ahora)
