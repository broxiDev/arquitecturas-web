# Spec para order-service (Fio)

## Contexto

Se hizo un refactor completo en kitchen-service que afecta la comunicacion con order-service. Los cambios principales:

1. **Se cambio el tipo de `cocinaId`** de `String` a `Long` — ahora es el ID auto-increment de la entidad `Cocina` en kitchen-service.
2. **Nueva entidad `Cocina`** en kitchen-service con `id` (Long), `nombre` (String) y `usuarioId` (Long, unique). El `id` de la cocina **es** el `cocinaId`.
3. **Se elimino `product-service`** — kitchen-service ahora maneja su propio catalogo local (solo `productId` y `productName`, sin precio).
4. **El precio se asigna al cargar a heladera** — no esta en el catalogo. Kitchen lo reenvia a fridge en la carga de stock.

---

## Cambios necesarios

### 1. Cambiar tipo del path variable en endpoint de ventas por cocina

El path no cambia, solo el tipo del parametro:

```
GET /api/v1/ordenes/historial-ventas/cocina/{cocinaId}
```

En el controller de order-service, cambiar:
```java
// antes
@GetMapping("/historial-ventas/cocina/{cocinaId}")
public ... getSalesByKitchen(@PathVariable String cocinaId, ...) {

// ahora
@GetMapping("/historial-ventas/cocina/{cocinaId}")
public ... getSalesByKitchen(@PathVariable Long cocinaId, ...) {
```

### 2. Migrar referencias internas

Cualquier referencia interna a `cocinaId` (String) en order-service debe cambiar a `cocinaId` (Long). Esto incluye:
- Entidades/DTOs que tengan `cocinaId` (String)
- Repositories con queries por `cocinaId`
- Services que usen `cocinaId`

---

## Lo que NO se toca

- El endpoint `GET /api/v1/ordenes/historial-ventas` (sin filtro por cocina) no cambia.
- El endpoint `PUT /api/v1/heladeras/{heladeraId}/stock` que order-service usa para actualizar stock en fridge sigue igual, pero el body ahora incluye `cocinaId` y `price` (opcionales para el update — eso lo implementa Ale en fridge-service).

---

## Endpoints que kitchen-service llama a order-service

| Metodo | Path | Descripcion |
|---|---|---|
| `GET` | `/api/v1/ordenes/historial-ventas` | Listar ventas historicas (con filtros opcionales: productId, fridgeId, from, to) |
| `GET` | `/api/v1/ordenes/historial-ventas/cocina/{cocinaId}` | Ventas por cocina (con filtros: from, to) |

---

## Mientras tanto

Mientras Fio implementa, kitchen-service usa `OrdenClientMockImpl` (profile `dev`) que retorna datos mockeados diferenciados por cocina. No hay bloqueo.

---

## Endpoints de kitchen-service (para info)

| Metodo | Path | Descripcion |
|---|---|---|
| `POST` | `/api/v1/cocina` | Crear cocina (valida usuario en user-service, 1 usuario = 1 cocina) |
| `GET` | `/api/v1/cocina/{cocinaId}` | Buscar cocina por ID |
| `POST` | `/api/v1/cocina/{cocinaId}/productos` | Agregar producto a una cocina |
| `GET` | `/api/v1/cocina/{cocinaId}/productos` | Listar productos de una cocina |
| `POST` | `/api/v1/cocina/carga-heladeras` | Cargar productos en heladera (valida catálogo, envía a fridge) |
| `GET` | `/api/v1/cocina/plan-diario?cocinaId=1` | Obtener plan diario |
| `POST` | `/api/v1/cocina/plan-diario?cocinaId=1` | Generar plan diario |
