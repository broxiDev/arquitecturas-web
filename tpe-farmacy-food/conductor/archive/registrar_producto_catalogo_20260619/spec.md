# US-03 — Registrar Producto en Catálogo de Cocina Fantasma

## Overview
Como cocina fantasma, quiero registrar un nuevo producto con todos sus atributos en mi catálogo, para mantener el catálogo actualizado. Cada cocina fantasma tiene su propio catálogo y solo un catálogo por cocina. Se genera documentación Swagger y datos de prueba en init.sql.

## Functional Requirements

### F1 — Crear/Actualizar producto en catálogo
- La cocina fantasma envía su `cocinaId` y los datos del producto
- El endpoint `POST /api/v1/productos` recibe `cocinaId` en el body del request
- **Campos obligatorios:** nombre (`name`), categoría (`dietaryCategory`), precio (`price`)
- **Campos opcionales:** descripción (`description`), URL de imagen (`imageUrl`), info nutricional (`nutritionalInfo`), temperatura de conservación (`conservacionTemperature`)
- Si ya existe un producto con el mismo nombre en el catálogo de esa cocina → se actualiza (upsert)
- Si no existe → se crea un nuevo producto vinculado a esa cocina
- Retorna HTTP 201 al crear, HTTP 200 al actualizar

### F2 — Modelo de datos
- Crear entidad `Catalogo`: `id` (Long, auto), `cocinaId` (String), relación OneToMany con `Product`
- Agregar campos a `Product`: `nutritionalInfo` (String), `conservacionTemperature` (BigDecimal)
- Un catálogo pertenece a UNA cocina, una cocina tiene UN catálogo
- Un producto pertenece a UN catálogo

### F3 — Validaciones
- `cocinaId`, `name`, `dietaryCategory`, `price` son obligatorios
- `price` debe ser mayor a 0
- No se permiten productos con nombre duplicado dentro del mismo catálogo (se actualiza)

### F4 — Documentación Swagger
- Documentar el endpoint `POST /api/v1/productos` con anotaciones `@Operation` y `@Parameter`
- Incluir schema del request con cocinaId y campos del producto
- Los endpoints existentes (GET, PUT, DELETE) también deben estar documentados

### F5 — Datos de prueba (init.sql)
- Insertar 2-3 cocinas de ejemplo (ej: "Cocina Sur", "Cocina Norte")
- Cada cocina con 2-3 productos de ejemplo con datos realistas (nombre, categoría, precio, descripción, info nutricional, temperatura)
- Usar INSERTs directos en init.sql

## Non-Functional Requirements
- Compatible con la estructura existente de Spring Boot + JPA + PostgreSQL
- Tests unitarios con cobertura >80%
- Mantener backward compatibility con el endpoint existente
- SpringDoc OpenAPI documentación funcional

## Acceptance Criteria
- [ ] Se puede crear un producto en el catálogo de una cocina enviando cocinaId
- [ ] Se puede actualizar un producto existente (misma cocina + mismo nombre)
- [ ] Los campos obligatorios son validados (400 Bad Request si faltan)
- [ ] Retorna HTTP 201 al crear, HTTP 200 al actualizar
- [ ] El catálogo de cada cocina es independiente
- [ ] Los campos opcionales se persisten correctamente
- [ ] Swagger muestra la documentación del endpoint con schemas correctos
- [ ] init.sql contiene datos de prueba para 2-3 cocinas con productos
- [ ] Tests unitarios pasan con >80% cobertura

## Out of Scope
- Eliminar productos del catálogo (DELETE)
- Listar el catálogo completo de una cocina (GET por cocinaId)
- Gestión de heladeras o stock
- Comunicación entre microservicios (solo product-service)
