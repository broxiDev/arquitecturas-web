# Task Summary: Refactor Plan Diario con Integraciones (F4 - US-12)

**Fecha:** 2026-06-20
**Track:** refactor_plan_diario_20260620

## Resumen
Se refactorizó el kitchen-service para que el Plan Diario sea scopeado por `kitchenId`, integrando datos de stock remanente desde fridge-service y ventas filtradas por cocina desde order-service.

## Cambios realizados

### Phase 1: Entidad y Repositorio
- `DailyPlan.java`: Agregado campo `kitchenId` (String, `@NotBlank`, `@Column(name="kitchen_id", nullable=false)`). Unique constraint cambiado a `(date, kitchen_id)`.
- `PlanDiarioRepository.java`: Cambiado `findByDate` a `findByDateAndKitchenId(LocalDate, String)`.
- `init.sql`: Agregada columna `kitchen_id`, unique constraint `(date, kitchen_id)`, actualizado seed data con `KITCHEN-001`.

### Phase 2: DTOs y Clientes de Integración
- Creado `FridgeRemainderDTO` (record: fridgeId, List<ProductRemainderDTO>).
- Creado `ProductRemainderDTO` (record: productId, productName, quantity).
- Actualizado `PlanDiarioResponseDTO`: agregado campo `kitchenId`.
- Creado `FridgeClient` interface con `getRemainderByKitchen(String kitchenId)`.
- Creado `FridgeClientFeign` (@Profile("!dev")): `GET /api/v1/heladeras/cocina/{kitchenId}/remanente`.
- Creado `FridgeClientMockImpl` (@Profile("dev")): 2 heladeras con 3 productos cada una.
- Actualizado `OrdenClient`: agregado `getSalesByKitchen(String kitchenId, LocalDate from, LocalDate to)`.
- Actualizado `OrdenClientFeign`: `GET /api/v1/ordenes/historial-ventas/cocina/{kitchenId}?from=...&to=...`.
- Actualizado `OrdenClientMockImpl`: datos hardcodeados de ventas por cocina.

### Phase 3: Clase Utilitaria
- Creado `PlanCalculatorUtils` con métodos estáticos:
  - `calculateDailyAverage(List<ProductoVentaDTO>, int historyDays)` → Map<Long, Integer>
  - `calculateTotalRemainder(List<FridgeRemainderDTO>)` → Map<Long, Integer>
  - `calculateSuggestedQuantities(Map<Long, Integer>, Map<Long, Integer>)` → Map<Long, Integer> (solo > 0)

### Phase 4: Servicios Refactorizados
- `PlanDiarioService` interface: agregado `kitchenId` a `getPlanByDate` y `generarPlan`.
- `PlanDiarioServiceImpl`: inyectado `FridgeClient`, usa `findByDateAndKitchenId`, `getSalesByKitchen`, `getRemainderByKitchen`, delega cálculos a `PlanCalculatorUtils`.

### Phase 5: Controllers y Contratos
- `PlanDiarioController`: agregado `@RequestParam @NotBlank String kitchenId` (required) a GET y POST.
- Creado `docs/contracts/fridge-service.md`: contrato para `GET /api/v1/heladeras/cocina/{kitchenId}/remanente`.
- Actualizado `docs/contracts/order-service.md`: agregado endpoint `GET /api/v1/ordenes/historial-ventas/cocina/{kitchenId}`.

### Tests actualizados
- `PlanDiarioServiceImplTest`: agregado `@Mock FridgeClient`, actualizado mocks para `findByDateAndKitchenId` y `getSalesByKitchen`.
- `PlanDiarioControllerTest`: actualizado DTOs con `kitchenId`, agregado `param("kitchenId", ...)` a requests.

## Decisiones
- `ProductoVentaDTO` (record existente) se reutilizó tal cual para el método `getSalesByKitchen`.
- Sin programación funcional: todos los cálculos usan bucles for tradicionales.
- Comentarios en español.
- Atributos y nombres en inglés.
- Los URL paths permanecen en español per product guidelines.

## Bloqueos
Ninguno. Compila sin errores (`mvn clean compile` y `mvn test-compile` ambos exitosos).