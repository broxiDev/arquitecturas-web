# Plan: US-3 — Registrar Producto en Catálogo de Cocina Fantasma

## Phase 1: Modelo de datos y persistencia
- [x] Task 1: Crear entidad `Catalogo` con campos `id`, `cocinaId` y relación OneToMany con `Product`
- [x] Task 2: Agregar campos `nutritionalInfo` (String) y `conservacionTemperature` (BigDecimal) a la entidad `Product`
- [x] Task 3: Crear repository `CatalogoRepository` (JpaRepository)
- [x] Task 4: Modificar `ProductRepository` para queries por cocinaId y nombre
- [x] Task 5: Actualizar schema de la base de datos (verificar `ddl-auto: update`)
- [x] Task: Conductor - User Manual Verification 'Modelo de datos y persistencia' (Protocol in workflow.md)

## Phase 2: Lógica de negocio (Service)
- [x] Task 6: Crear interfaz `CatalogoService` con métodos para crear/actualizar producto en catálogo
- [x] Task 7: Implementar `CatalogoServiceImpl` con lógica de upsert (verificar existencia por cocinaId + nombre)
- [x] Task 8: Modificar `ProductServiceImpl` para usar nuevos campos
- [x] Task 9: Agregar validaciones: campos obligatorios, precio > 0, nombre duplicado por cocina
- [x] Task 10: Crear excepciones (`InvalidProductDataException`, `DuplicateProductException`)
- [x] Task: Conductor - User Manual Verification 'Lógica de negocio' (Protocol in workflow.md)

## Phase 3: API REST y DTOs
- [x] Task 11: Actualizar `ProductRequest` para incluir `nutritionalInfo`, `conservacionTemperature`
- [x] Task 12: Actualizar `ProductResponse` para incluir nuevos campos y `cocinaId`
- [x] Task 13: Crear endpoint `POST /api/v1/productos/cocina/{cocinaId}` para registrar producto en catálogo
- [x] Task 14: Agregar documentación Swagger (`@Operation`, `@Parameter`, `@Schema`) a todos los endpoints
- [x] Task 15: Verificar que los endpoints existentes (GET, PUT, DELETE) siguen funcionando correctamente
- [x] Task: Conductor - User Manual Verification 'API REST y DTOs' (Protocol in workflow.md)

## Phase 4: Datos de prueba y documentación
- [x] Task 16: Modificar `init.sql` para crear tabla `catalogos`
- [x] Task 17: Insertar 2-3 cocinas de ejemplo en `init.sql`
- [x] Task 18: Insertar 2-3 productos por cocina en `init.sql`
- [x] Task 19: Verificar compilación correcta
- [x] Task: Conductor - User Manual Verification 'Datos de prueba y documentación' (Protocol in workflow.md)

## Phase 5: Testing
- [x] Task 20: Crear tests unitarios para `CatalogoService` (upsert, validaciones, duplicados)
- [x] Task 21: Crear tests unitarios para el controller (POST con cocinaId, validación de campos)
- [x] Task 22: Verificar que los tests pasan correctamente
- [x] Task 23: Verificar cobertura (9 tests, 0 failures)
- [x] Task: Conductor - User Manual Verification 'Testing' (Protocol in workflow.md)
