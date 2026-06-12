# Spec: Frontend React Mock para Simulación de Features Core

## Overview
Agregar un frontend en React 18+ al proyecto FarmacyFood para simular las features core del MVP (F1, F2, F3) con datos mock locales. El objetivo es tener pantallas funcionales que permitan visualizar y validar los flujos de usuario, mientras se define y estructura la capa de servicios/API que consumirán los microservicios reales cuando estén implementados.

## Functional Requirements

### FR-1: Estructura del Proyecto Frontend
- Proyecto React 18+ creado con Vite dentro de `tpe-farmacy-food/frontend/`
- Tailwind CSS configurado para estilos
- Zustand para estado local de cada feature
- Estructura de carpetas:
  - `src/services/` — Capa de API con funciones que definen los contratos REST (usan mocks por ahora)
  - `src/mocks/` — Datos JSON estáticos (`products.json`, `fridges.json`, `orders.json`)
  - `src/features/` — Una carpeta por feature (`catalogo/`, `heladeras/`, `ordenes/`), cada una con sus componentes, store (Zustand) y service
  - `src/components/` — Componentes compartidos (Layout, Navbar opcional, Cards, etc.)

### FR-2: Capa de Servicios/API Preparada
- Cada feature debe tener su archivo de servicio (`productService.js`, `fridgeService.js`, `orderService.js`)
- Funciones con firma real de API:
  - `productService`: getAll(categoria?), getById(id), create(data), update(id, data), delete(id)
  - `fridgeService`: getAll(lat?, lng?), getById(id), getStock(fridgeId), updateStock(fridgeId, data)
  - `orderService`: create(data), getAll(), getById(id), getUserOrders(userId), cancel(id), pay(id), confirmPickup(id)
- En modo mock: importan datos de los archivos JSON y devuelven Promises (con `setTimeout` simulado)
- Fácil de reemplazar por fetch/axios real cuando el backend esté listo

### FR-3: Mock Data
- `products.json`: array de 8-12 productos con id, name, description, dietaryCategory, price, imageUrl, createdAt
- `fridges.json`: array de 5-8 heladeras con id, name, location (lat/lng), status, stock (array de {productId, quantity})
- `orders.json`: array de órdenes de ejemplo con id, userId, fridgeId, items, total, status, paymentId, createdAt

### FR-4: Pantalla F1 — Catálogo de Productos
- Visualización de productos en grid de cards (imagen, nombre, categoría dietaria, precio)
- Filtrado por categoría dietaria (vegano, sin gluten, vegetariano, etc.) con botones/chips
- Vista de detalle al clickear un producto

### FR-5: Pantalla F2 — Gestión de Heladeras
- Visualización de heladeras en lista de cards (nombre, dirección/ubicación, estado con badge de color)
- Indicador visual de estado: verde (active), amarillo (maintenance), rojo (out_of_service)
- Vista de detalle de heladera con su stock (producto + cantidad disponible)

### FR-6: Pantalla F3 — Órdenes
- Listado de órdenes existentes con estado (pending, paid, picked_up, cancelled)
- Formulario para crear nueva orden: seleccionar usuario (mock), heladera, productos y cantidades -> calcular total
- Vista de detalle de orden con items, total, estado y timeline de acciones (creada -> pagada -> retirada)

### FR-7: Estado con Zustand
- Cada feature tiene su propio store Zustand con acciones asíncronas (invocan al service layer)
- Ejemplo: `useProductStore` con `products`, `selectedProduct`, `filters`, `fetchProducts()`, `fetchById(id)`

## Non-Functional Requirements
- La app debe compilar y ejecutarse con `npm run dev` en localhost:5173
- No se requiere test coverage en esta fase mock
- Código en español para nomenclatura de funciones y stores

## Acceptance Criteria
- [ ] El proyecto frontend compila y levanta correctamente con `npm run dev`
- [ ] Las 3 pantallas (catálogo, heladeras, órdenes) se renderizan con datos mock
- [ ] Los servicios API están definidos con las firmas correctas para cada feature
- [ ] Cada pantalla tiene su store Zustand funcional
- [ ] El catálogo permite filtrar por categoría dietaria
- [ ] Las heladeras muestran su estado con indicador visual
- [ ] Se puede crear una orden mock y ver sus detalles

## Out of Scope
- F4 (Planificación de Cocina), F5 (Preferencias), F6 (Notificaciones)
- Autenticación real o login simulado
- Router global (React Router)
- Conexión a backend real
- Tests unitarios o de integración
- Responsive design completo (solo desktop por ahora)
