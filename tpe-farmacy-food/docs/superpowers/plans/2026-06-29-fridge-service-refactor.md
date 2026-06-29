# Fridge-Service Refactor Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development or superpowers:executing-plans.

**Goal:** Remove `cocinaId` from Heladera, add ManyToMany linking to cocinas, add `cocinaId` and `price` to StockHeladera, align StockCreateDTO with kitchen-service.

**Architecture:** Only fridge-service is modified. New `KitchenClient` (Feign) validates cocina existence. StockHeladera gains `cocinaId`(Long)+`price`(BigDecimal). Heladera uses `@ElementCollection` for cocina IDs.

**Tech Stack:** Java 21, Spring Boot, JPA/Hibernate, PostgreSQL

---

### Task 1: Update Heladera entity — Remove cocinaId, add cocinaIds set

**Files:**
- Modify: `fridge-service/src/main/java/com/farmacyfood/fridge/entity/Heladera.java`

**Changes:**
- Remove `private String cocinaId` field and its column annotation
- Add `@ElementCollection Set<Long> cocinaIds` with `@CollectionTable` for `heladera_cocina`
- Initialize with `new HashSet<>()`

### Task 2: Update all Heladera DTOs

**Files:**
- Modify: `fridge-service/src/main/java/com/farmacyfood/fridge/dto/HeladeraCreateDTO.java`
- Modify: `fridge-service/src/main/java/com/farmacyfood/fridge/dto/HeladeraUpdateDTO.java`
- Modify: `fridge-service/src/main/java/com/farmacyfood/fridge/dto/HeladeraResponseDTO.java`

**Changes:**
- `HeladeraCreateDTO`: remove `cocinaId` field
- `HeladeraUpdateDTO`: remove `cocinaId` field
- `HeladeraResponseDTO`: replace `String cocinaId` with `Set<Long> cocinaIds`

### Task 3: Update StockHeladera entity — Add cocinaId and price

**Files:**
- Modify: `fridge-service/src/main/java/com/farmacyfood/fridge/entity/StockHeladera.java`

**Changes:**
- Add `private Long cocinaId` with `@Column(name = "cocina_id", nullable = false)`
- Add `private BigDecimal price` with `@Column(nullable = false, precision = 10, scale = 2)`

### Task 4: Update all Stock DTOs

**Files:**
- Modify: `fridge-service/src/main/java/com/farmacyfood/fridge/dto/StockCreateDTO.java`
- Modify: `fridge-service/src/main/java/com/farmacyfood/fridge/dto/StockUpdateDTO.java`
- Modify: `fridge-service/src/main/java/com/farmacyfood/fridge/dto/StockResponseDTO.java`
- Create: `fridge-service/src/main/java/com/farmacyfood/fridge/dto/CocinaLinkRequestDTO.java`

**Changes:**
- `StockCreateDTO`: add `@NotNull Long cocinaId`, `@NotNull BigDecimal price`
- `StockUpdateDTO`: add `@NotNull Long cocinaId`, `BigDecimal price`
- `StockResponseDTO`: add `Long cocinaId`, `BigDecimal price`
- `CocinaLinkRequestDTO`: new record with `@NotNull Long cocinaId`

### Task 5: Create KitchenClient (Feign) for cocina validation

**Files:**
- Create: `fridge-service/src/main/java/com/farmacyfood/fridge/client/KitchenClient.java`
- Create: `fridge-service/src/main/java/com/farmacyfood/fridge/client/KitchenClientFeign.java`
- Create: `fridge-service/src/main/java/com/farmacyfood/fridge/client/KitchenClientMockImpl.java`
- Modify: `fridge-service/src/main/resources/application.yml`

**Details:**
- Interface with `boolean cocinaExists(Long cocinaId)`
- Feign impl calls `GET /api/v1/cocina/{cocinaId}`, returns true if 200
- Mock impl returns true for cocinaIds 1, 2, 3
- Remove `@Profile("!dev")` from `NotificacionClient` dependent beans? No, leave notif as is

### Task 6: Update HeladeraService + HeladeraServiceImpl

**Files:**
- Modify: `fridge-service/src/main/java/com/farmacyfood/fridge/service/HeladeraService.java`
- Modify: `fridge-service/src/main/java/com/farmacyfood/fridge/service/HeladeraServiceImpl.java`

**Changes:**
- Add `linkCocina(Long heladeraId, Long cocinaId)` and `unlinkCocina(Long heladeraId, Long cocinaId)` to interface
- In impl: `create()` no longer sets cocinaId. `update()` no longer sets cocinaId. `toDTO()` maps `cocinaIds`.
- `linkCocina()`: find heladera, call `kitchenClient.cocinaExists()`, throw `CocinaNotFoundException` if not, add to set
- `unlinkCocina()`: find heladera, remove from set

### Task 7: Update StockServiceImpl

**Files:**
- Modify: `fridge-service/src/main/java/com/farmacyfood/fridge/service/StockServiceImpl.java`

**Changes:**
- `addStock()`: save cocinaId and price from DTO
- `updateStock()`: find by `(heladeraId, cocinaId, productId)`, update quantity/name/price
- `getRemainderByCocinaId(Long)`: query stock directly by cocinaId, group by heladera, map to `FridgeRemainderDTO`

### Task 8: Update Repositories

**Files:**
- Modify: `fridge-service/src/main/java/com/farmacyfood/fridge/repository/HeladeraRepository.java`
- Modify: `fridge-service/src/main/java/com/farmacyfood/fridge/repository/StockRepository.java`

**Changes:**
- `HeladeraRepository`: remove `findByCocinaId(String)`
- `StockRepository`: add `findByCocinaId(Long)`, add `findByHeladeraIdAndCocinaIdAndProductId(Long, Long, Long)`

### Task 9: Update Controllers

**Files:**
- Modify: `fridge-service/src/main/java/com/farmacyfood/fridge/controller/HeladeraController.java`
- Modify: `fridge-service/src/main/java/com/farmacyfood/fridge/controller/StockController.java`

**Changes:**
- `HeladeraController`: add POST/DELETE for cocina linking, update remainder endpoint (String→Long), update swagger examples
- `StockController`: update swagger examples with cocinaId and price

### Task 10: Update GlobalExceptionHandler

**Files:**
- Modify: `fridge-service/src/main/java/com/farmacyfood/fridge/exception/GlobalExceptionHandler.java`

**Changes:** Add `@ExceptionHandler` for `CocinaNotFoundException` (404) if not already present.

### Task 11: Update init.sql

**Files:**
- Modify: `fridge-service/init.sql`

**Changes:**
- Remove `cocina_id` column from `heladera` table
- Create `heladera_cocina` table
- Add `cocina_id`, `price` columns to `stock_heladera`
- Update unique constraint to `(heladera_id, cocina_id, product_id)`
- Update seed data

### Task 12: Compile and verify

**Run:** `mvn clean compile -pl fridge-service` or `mvn clean compile` from root
