# Specification: Refactor Plan Diario con Integraciones (F4 - US-12)

## Overview
Refactorizar los endpoints de Plan Diario del kitchen-service para integrar datos reales de stock remanente en heladeras (fridge-service) y ventas filtradas por cocina (order-service). El plan diario ahora es scopeado por `kitchenId`, permitiendo generar reportes de producción independientes por cocina fantasma.

## User Stories
- **US-12**: Como admin de cocina fantasma, quiero recibir diariamente un reporte de qué productos y en qué cantidades debo preparar para el día siguiente, basado en ventas históricas y stock remanente en heladeras.

## Restricciones de Código
- **Sin programación funcional**: No usar streams, lambdas, method references ni Optional chaining. Usar bucles for/while tradicionales e if/else explícito.
- **Servicios simples**: Los métodos de servicio deben ser simples. Lógica extra (cálculos, agrupaciones) va en una clase utilitaria (`PlanCalculatorUtils` o similar).
- **MVP**: Implementación mínima viable. Complejidad adicional se agrega en tracks futuros.
- **Sin tests**: Por ahora solo se requiere que compile y funcione en pruebas locales.
- **Comentarios en español**.
- **Atributos y nombres en inglés**: Todos los nombres de clases, métodos, parámetros, campos de DTOs y entidades deben estar en inglés.

## Functional Requirements

### FR-1: Entidad DailyPlan con kitchenId
- Agregar campo `kitchenId` (String, `@NotBlank`, not null) a `DailyPlan`.
- Unique constraint cambia de `(date)` a `(date, kitchenId)`.
- `PlanDiarioRepository.findByDateAndKitchenId(LocalDate date, String kitchenId)`.
- Actualizar `init.sql` con la nueva columna y constraint.

### FR-2: DTOs actualizados
- `PlanDiarioResponseDTO`: agregar campo `kitchenId` (String).
- Nuevos DTOs para integración con fridge-service:
  - `FridgeRemainderDTO(Long fridgeId, List<ProductRemainderDTO> products)`
  - `ProductRemainderDTO(Long productId, String productName, Integer quantity)`
- DTO para integración con order-service (nuevo método):
  - Reutilizar `ProductSaleDTO(Long productId, String productName, Integer totalSold)` o crear uno similar si es necesario.

### FR-3: Nuevo FridgeClient (patrón Interface + Feign + Mock)
- **Interface** `FridgeClient`:
  - `List<FridgeRemainderDTO> getRemainderByKitchen(String kitchenId)`
- **Feign impl** `FridgeClientFeign` (`@Profile("!dev")`):
  - `@FeignClient(name = "fridge-service", url = "${clients.fridge-service.url:http://localhost:8082}")`
  - `GET /api/v1/heladeras/cocina/{kitchenId}/remanente`
- **Mock impl** `FridgeClientMockImpl` (`@Profile("dev")`):
  - Retorna 2 heladeras con 3 productos hardcodeados cada una.
- **Contrato**: documentar en `docs/contracts/fridge-service.md`.

### FR-4: OrdenClient actualizado
- **Nuevo método** en interface `OrdenClient`:
  - `List<ProductSaleDTO> getSalesByKitchen(String kitchenId, LocalDate from, LocalDate to)`
- **Feign impl** (`@Profile("!dev")`):
  - `GET /api/v1/ordenes/historial-ventas/cocina/{kitchenId}?from=...&to=...`
- **Mock impl** (`@Profile("dev")`):
  - Retorna datos hardcodeados de ventas para una cocina.
- **Contrato**: actualizar `docs/contracts/order-service.md` con el nuevo endpoint.

### FR-5: Endpoints refactorizados
| Método | Path | Params | Descripción |
|--------|------|--------|-------------|
| GET | `/api/v1/cocina/plan-diario` | `kitchenId` (req), `fecha` (opt, default hoy) | Obtener plan diario de una cocina |
| POST | `/api/v1/cocina/plan-diario` | `kitchenId` (req), `fecha` (opt, default hoy) | Generar plan diario para una cocina |

### FR-6: Algoritmo de generación (refactorizado)
1. Validar que no exista plan para `(fecha, kitchenId)` → 409 si existe.
2. Obtener ventas últimos 7 días via `OrdenClient.getSalesByKitchen(kitchenId, from, to)`.
3. Calcular `ceiling(promedio diario)` por producto (lógica en utils).
4. Obtener remanente de heladeras via `FridgeClient.getRemainderByKitchen(kitchenId)`.
5. Calcular stock remanente total por producto (suma de todas las heladeras, lógica en utils).
6. `suggestedQuantity = ceiling(average) - totalRemainder`
7. Solo incluir productos con `suggestedQuantity > 0`.
8. Persistir `DailyPlan` con `kitchenId` y sus `PlanItem`.

### FR-7: Clase utilitaria
- `PlanCalculatorUtils` (o nombre similar):
  - `calculateDailyAverage(List<ProductSaleDTO> sales, int historyDays)` → `Map<Long, Integer>` (productId → ceiling average)
  - `calculateTotalRemainder(List<FridgeRemainderDTO> fridges)` → `Map<Long, Integer>` (productId → total remainder stock)
  - `calculateSuggestedQuantities(Map<Long, Integer> averages, Map<Long, Integer> remainders)` → `Map<Long, Integer>` (productId → suggested quantity, only > 0)

## Non-Functional Requirements
- **NFR-1**: Código sin streams/lambdas, con comentarios en español.
- **NFR-2**: Swagger/OpenAPI accesible.
- **NFR-3**: Compila sin errores (`mvn clean compile`).
- **NFR-4**: Funciona en profile `dev` sin depender de otros servicios.
- **NFR-5**: Todos los atributos, nombres de clases, métodos y parámetros en inglés.

## Acceptance Criteria
- [ ] `DailyPlan` tiene campo `kitchenId` y unique constraint `(date, kitchenId)`
- [ ] `POST /api/v1/cocina/plan-diario?kitchenId=xxx` genera plan con datos de ventas y remanente
- [ ] `GET /api/v1/cocina/plan-diario?kitchenId=xxx` retorna plan de esa cocina
- [ ] `FridgeClient` mock funciona en profile `dev`
- [ ] `OrdenClient.getSalesByKitchen` mock funciona en profile `dev`
- [ ] Algoritmo descuenta stock remanente de la cantidad sugerida
- [ ] Si cantidad sugerida ≤ 0, el producto no aparece en el plan
- [ ] 409 si ya existe plan para esa fecha+cocina
- [ ] `docs/contracts/fridge-service.md` documentado
- [ ] `docs/contracts/order-service.md` actualizado
- [ ] Servicio compila sin errores

## Out of Scope
- Implementación real del endpoint en order-service o fridge-service
- Tests unitarios o de integración (se agregan en track futuro)
- Algoritmos avanzados (tendencias, ponderación, estacionalidad)
- Regeneración/actualización de planes existentes
- Frontend integration
