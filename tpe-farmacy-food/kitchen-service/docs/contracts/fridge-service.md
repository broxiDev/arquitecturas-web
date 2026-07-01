# Spec para fridge-service (Ale)

## Contexto

Se hizo un refactor completo en kitchen-service que afecta la comunicacion con fridge-service. Los cambios principales:

1. **Se cambio el tipo de `cocinaId`** de `String` a `Long` — ahora es el ID auto-increment de la entidad `Cocina` en kitchen-service.
2. **Nueva entidad `Cocina`** en kitchen-service con `id` (Long), `nombre` (String) y `usuarioId` (Long, unique). El `id` de la cocina **es** el `cocinaId`.
3. **Se agrego `price` (BigDecimal)** al stock de heladeras. El precio **no** esta en el catalogo de kitchen — se asigna al momento de cargar productos en la heladera.
4. **Nueva operacion de carga de stock** — kitchen envia productos a fridge via `POST /api/v1/heladeras/{heladeraId}/stock`.

El catalogo local de kitchen solo tiene `productId` y `productName` (sin precio). El `price` lo manda el usuario de kitchen en la request de carga-heladeras, y kitchen lo reenvia a fridge tal cual.

Kitchen valida su catalogo local antes de enviar. Fridge **no valida** contra kitchen — confia en que kitchen ya valido.

---

## Cambios necesarios

### 1. `Heladera.java` — cambiar tipo de `cocinaId` (String → Long)

**Archivo:** `src/main/java/com/frarmacyfood/fridge/entity/Heladera.java`

El campo actual:
```java
@Column(name = "cocina_id", nullable = false, length = 50)
@NotBlank
private String cocinaId;
```

Debe quedar:
```java
@Column(name = "cocina_id", nullable = false)
@NotNull
private Long cocinaId;
```

Agregar import: `import jakarta.validation.constraints.NotNull;`

### 2. `HeladeraRepository.java` — cambiar query

**Archivo:** `src/main/java/com/frarmacyfood/fridge/repository/HeladeraRepository.java`

```java
// antes
List<Heladera> findByCocinaId(String cocinaId);

// ahora
List<Heladera> findByCocinaId(Long cocinaId);
```

### 3. `HeladeraController.java` — cambiar endpoint de remanente

**Archivo:** `src/main/java/com/frarmacyfood/fridge/controller/HeladeraController.java`

```java
// antes
@GetMapping("/cocina/{cocinaId}/remanente")
public ResponseEntity<List<FridgeRemainderDTO>> getRemainderByCocina(@PathVariable String cocinaId) {

// ahora
@GetMapping("/cocina/{cocinaId}/remanente")
public ResponseEntity<List<FridgeRemainderDTO>> getRemainderByCocina(@PathVariable Long cocinaId) {
```

### 4. `StockService.java` + `StockServiceImpl.java` — actualizar metodo de remanente

**Archivo:** `src/main/java/com/frarmacyfood/fridge/service/StockService.java`
**Archivo:** `src/main/java/com/frarmacyfood/fridge/service/StockServiceImpl.java`

```java
// antes
List<FridgeRemainderDTO> getRemainderByCocinaId(String cocinaId);
public List<FridgeRemainderDTO> getRemainderByCocinaId(String cocinaId) {
    List<Heladera> heladeras = heladeraRepository.findByCocinaId(cocinaId);

// ahora
List<FridgeRemainderDTO> getRemainderByCocinaId(Long cocinaId);
public List<FridgeRemainderDTO> getRemainderByCocinaId(Long cocinaId) {
    List<Heladera> heladeras = heladeraRepository.findByCocinaId(cocinaId);
```

### 5. `StockHeladera.java` — agregar 2 campos

**Archivo:** `src/main/java/com/frarmacyfood/fridge/entity/StockHeladera.java`

Agregar despues de `quantity`:
```java
@Column(name = "cocina_id", nullable = false)
@NotNull
private Long cocinaId;

@Column(name = "price", nullable = false, precision = 10, scale = 2)
@NotNull
private BigDecimal price;
```

Agregar import: `import java.math.BigDecimal;`

### 6. `StockCreateDTO.java` — agregar 2 campos

**Archivo:** `src/main/java/com/frarmacyfood/fridge/dto/StockCreateDTO.java`

```java
public record StockCreateDTO(
    @NotNull Long productId,
    @NotBlank String productName,
    @NotNull @Min(0) Integer quantity,

    @NotNull Long cocinaId,
    @NotNull BigDecimal price
) {}
```

### 7. `StockResponseDTO.java` — agregar 2 campos

Agregar `cocinaId` (Long) y `price` (BigDecimal) al record.

### 8. `StockUpdateDTO.java` — agregar 2 campos

Agregar `cocinaId` (Long, opcional) y `price` (BigDecimal, opcional).

### 9. `StockServiceImpl.addStock()` — setear campos nuevos

```java
StockHeladera stock = StockHeladera.builder()
    .heladera(heladera)
    .productId(dto.productId())
    .productName(dto.productName())
    .quantity(dto.quantity())
    .cocinaId(dto.cocinaId())
    .price(dto.price())
    .build();
```

### 10. `StockServiceImpl.toDTO()` — incluir campos nuevos

```java
private StockResponseDTO toDTO(StockHeladera stock) {
    return new StockResponseDTO(
        stock.getId(),
        stock.getHeladera().getId(),
        stock.getProductId(),
        stock.getProductName(),
        stock.getQuantity(),
        stock.getCocinaId(),
        stock.getPrice(),
        stock.getUpdatedAt()
    );
}
```

### 11. `StockServiceImpl.updateStock()` — actualizar campos si vienen

Agregar despues del update de `productName`:
```java
if (dto.cocinaId() != null) stock.setCocinaId(dto.cocinaId());
if (dto.price() != null) stock.setPrice(dto.price());
```

### 12. `HeladeraCreateDTO` / `HeladeraUpdateDTO` / `HeladeraResponseDTO`

Cambiar el tipo de `cocinaId` de `String` a `Long` en los tres DTOs.

---

## Lo que NO se toca

- No se crean endpoints nuevos. El endpoint `POST /api/v1/heladeras/{heladeraId}/stock` ya existe.
- `StockRepository.java` — sin cambios (ya tiene `findByHeladeraIdAndProductId`).
- No se agrega validacion contra kitchen-service. Fridge confia en que kitchen ya valido.

---

## Endpoints que kitchen-service llama a fridge-service

| Metodo | Path | Descripcion |
|---|---|---|
| `GET` | `/api/v1/heladeras/cocina/{cocinaId}/remanente` | Obtener remanente de stock por cocina |
| `POST` | `/api/v1/heladeras/{heladeraId}/stock` | Cargar un producto en una heladera (con cocinaId + price) |

Kitchen envia un POST por producto. Si carga 2 productos en heladera 1, hace 2 POSTs secuenciales.

## Ejemplo de request que kitchen envia a fridge

```
POST /api/v1/heladeras/1/stock
Content-Type: application/json

{
  "productId": 101,
  "productName": "Lechuga y tomate",
  "quantity": 10,
  "cocinaId": 1,
  "price": 10.00
}
```

---

## Mientras tanto

Mientras Ale implementa, kitchen-service usa `FridgeClientMockImpl` (profile `dev`) que loguea los items y no hace llamadas reales. No hay bloqueo.

---

## Endpoints de kitchen-service (para info)

| Metodo | Path | Descripcion |
|---|---|---|
| `POST` | `/api/v1/cocina` | Crear cocina (valida usuario en user-service, 1 usuario = 1 cocina) |
| `GET` | `/api/v1/cocina/{cocinaId}` | Buscar cocina por ID |
| `POST` | `/api/v1/cocina/productos` | Agregar producto a la cocina del usuario autenticado (X-User) |
| `GET` | `/api/v1/cocina/productos` | Listar productos de la cocina del usuario autenticado (X-User) |
| `POST` | `/api/v1/cocina/carga-heladeras` | Cargar productos en heladera (valida catálogo, envía a fridge) |
| `GET` | `/api/v1/cocina/plan-diario?cocinaId=1` | Obtener plan diario |
| `POST` | `/api/v1/cocina/plan-diario?cocinaId=1` | Generar plan diario |
