# Implementation Plan: Refactor Plan Diario con Integraciones (F4 - US-12)

## Phase 1: Entidad y Repositorio

- [x] Task: Agregar campo `kitchenId` a entidad `DailyPlan`
    - [x] Agregar campo `kitchenId` (String, `@NotBlank`, `@Column(nullable = false)`) a `DailyPlan.java`
    - [x] Cambiar unique constraint de `date` a `(date, kitchenId)` via `@Table(uniqueConstraints = ...)`
    - [x] Actualizar `@Builder` para incluir `kitchenId`
- [x] Task: Actualizar `PlanDiarioRepository`
    - [x] Cambiar `findByDate(LocalDate)` a `findByDateAndKitchenId(LocalDate, String)`
- [x] Task: Actualizar `init.sql`
    - [x] Agregar columna `kitchen_id` (VARCHAR, NOT NULL) a tabla `daily_plan`
    - [x] Actualizar unique constraint a `(date, kitchen_id)`
    - [x] Actualizar datos de ejemplo con un `kitchen_id`
- [x] Task: Conductor - User Manual Verification 'Entidad y Repositorio' (Protocol in workflow.md)

## Phase 2: DTOs y Clientes de Integración

- [x] Task: Crear DTOs para fridge-service
    - [x] Crear record `FridgeRemainderDTO(Long fridgeId, List<ProductRemainderDTO> products)`
    - [x] Crear record `ProductRemainderDTO(Long productId, String productName, Integer quantity)`
- [x] Task: Actualizar `PlanDiarioResponseDTO`
    - [x] Agregar campo `kitchenId` (String) al record
- [x] Task: Crear `FridgeClient` interface
    - [x] Crear interface `FridgeClient` con método `getRemainderByKitchen(String kitchenId)`
- [x] Task: Crear `FridgeClientFeign` (`@Profile("!dev")`)
    - [x] Sub-interface de `FridgeClient` con `@FeignClient(name = "fridge-service", url = "${clients.fridge-service.url:http://localhost:8082}")`
    - [x] Mapear `GET /api/v1/heladeras/cocina/{kitchenId}/remanente`
- [x] Task: Crear `FridgeClientMockImpl` (`@Profile("dev")`)
    - [x] Implementar con datos hardcodeados: 2 heladeras con 3 productos cada una
- [x] Task: Actualizar `OrdenClient` interface
    - [x] Agregar método `getSalesByKitchen(String kitchenId, LocalDate from, LocalDate to)`
- [x] Task: Actualizar `OrdenClientFeign`
    - [x] Agregar mapeo `GET /api/v1/ordenes/historial-ventas/cocina/{kitchenId}?from=...&to=...`
- [x] Task: Actualizar `OrdenClientMockImpl`
    - [x] Agregar implementación mock de `getSalesByKitchen` con datos hardcodeados
- [x] Task: Conductor - User Manual Verification 'DTOs y Clientes de Integración' (Protocol in workflow.md)

## Phase 3: Clase Utilitaria

- [x] Task: Crear `PlanCalculatorUtils`
    - [x] Implementar `calculateDailyAverage(List<ProductSaleDTO>, int historyDays)` → `Map<Long, Integer>`
    - [x] Implementar `calculateTotalRemainder(List<FridgeRemainderDTO>)` → `Map<Long, Integer>`
    - [x] Implementar `calculateSuggestedQuantities(Map<Long, Integer> averages, Map<Long, Integer> remainders)` → `Map<Long, Integer>` (solo > 0)
- [x] Task: Conductor - User Manual Verification 'Clase Utilitaria' (Protocol in workflow.md)

## Phase 4: Servicios Refactorizados

- [x] Task: Refactorizar `PlanDiarioServiceImpl.generarPlan`
    - [x] Agregar parámetro `kitchenId` al método
    - [x] Cambiar validación a `findByDateAndKitchenId`
    - [x] Llamar `OrdenClient.getSalesByKitchen(kitchenId, from, to)` en vez de `getVentasRecientes`
    - [x] Llamar `FridgeClient.getRemainderByKitchen(kitchenId)`
    - [x] Delegar cálculos a `PlanCalculatorUtils`
    - [x] Setear `kitchenId` en el `DailyPlan` antes de persistir
- [x] Task: Refactorizar `PlanDiarioServiceImpl.getPlanByDate`
    - [x] Agregar parámetro `kitchenId` al método
    - [x] Cambiar lookup a `findByDateAndKitchenId`
- [x] Task: Actualizar interface `PlanDiarioService`
    - [x] Actualizar firmas de `generarPlan` y `getPlanByDate` con `kitchenId`
- [x] Task: Actualizar `PlanDiarioResponseDTO` en mapeo entidad→DTO
    - [x] Incluir `kitchenId` en la conversión
- [x] Task: Conductor - User Manual Verification 'Servicios Refactorizados' (Protocol in workflow.md)

## Phase 5: Controllers y Contratos

- [x] Task: Refactorizar `PlanDiarioController`
    - [x] Agregar `@RequestParam String kitchenId` (required) a `getPlan` y `generarPlan`
    - [x] Pasar `kitchenId` a los métodos de servicio
- [x] Task: Documentar contrato `docs/contracts/fridge-service.md`
    - [x] Crear archivo con endpoint, parámetros, response DTO y ejemplo
- [x] Task: Actualizar contrato `docs/contracts/order-service.md`
    - [x] Agregar nuevo endpoint `GET /api/v1/ordenes/historial-ventas/cocina/{kitchenId}`
- [x] Task: Verificar compilación (`mvn clean compile`)
- [x] Task: Conductor - User Manual Verification 'Controllers y Contratos' (Protocol in workflow.md)