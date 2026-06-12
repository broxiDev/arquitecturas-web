# Implementation Plan

## Phase 1: Setup del Proyecto Frontend

- [x] Task: Crear proyecto React con Vite y configurar Tailwind CSS
    - [x] Crear proyecto con `npm create vite@latest frontend -- --template react` dentro de `tpe-farmacy-food/`
    - [x] Instalar dependencias: `npm install`
    - [x] Instalar y configurar Tailwind CSS v3 con `tailwind.config.js` y `postcss.config.js`
    - [x] Agregar directivas de Tailwind en `src/index.css`
    - [x] Instalar Zustand: `npm install zustand`
    - [x] Agregar `dev` script en `package.json` si no existe

- [x] Task: Definir estructura de carpetas del proyecto
    - [x] Crear `src/services/` (capa de API)
    - [x] Crear `src/mocks/` (datos JSON estáticos)
    - [x] Crear `src/features/catalogo/`, `src/features/heladeras/`, `src/features/ordenes/` (una carpeta por feature)
    - [x] Crear `src/components/` (componentes compartidos)
    - [x] Crear `src/stores/` (stores Zustand compartidas si las hay)

- [x] Task: Conductor - User Manual Verification 'Setup del Proyecto Frontend' (Protocol in workflow.md)

## Phase 2: Mock Data y Capa de Servicios API

- [x] Task: Crear archivos de datos mock
    - [x] Crear `src/mocks/products.json` con 10 productos (id, name, description, dietaryCategory, price, imageUrl, createdAt)
    - [x] Crear `src/mocks/fridges.json` con 6 heladeras (id, name, location con lat/lng, status, stock con productId+quantity)
    - [x] Crear `src/mocks/orders.json` con 4 órdenes de ejemplo (id, userId, fridgeId, items[], total, status, paymentId, createdAt)
    - [x] Crear `src/mocks/users.json` con 3 usuarios mock para el formulario de órdenes

- [x] Task: Implementar capa de servicios API (productService, fridgeService, orderService)
    - [x] Crear `src/services/productService.js` con: getAll(categoria?), getById(id), create(data), update(id, data), delete(id)
    - [x] Crear `src/services/fridgeService.js` con: getAll(lat?, lng?), getById(id), getStock(fridgeId), updateStock(fridgeId, data)
    - [x] Crear `src/services/orderService.js` con: create(data), getAll(), getById(id), getUserOrders(userId), cancel(id), pay(id), confirmPickup(id)
    - [x] Crear `src/services/userService.js` con: getAll(), getById(id)

- [x] Task: Conductor - User Manual Verification 'Mock Data y Capa de Servicios API' (Protocol in workflow.md)

## Phase 3: F1 — Catálogo de Productos

- [x] Task: Crear store Zustand para catálogo
    - [x] Crear store `src/features/catalogo/productStore.js` con estado: products[], selectedProduct, filters, loading
    - [x] Acciones: fetchProducts(filtros?), fetchProductById(id)

- [x] Task: Implementar pantalla de catálogo
    - [x] Crear `src/features/catalogo/ProductList.jsx` (integrado en Catalogo.jsx) — grid de cards con imagen, nombre, categoría, precio
    - [x] Crear `src/features/catalogo/ProductCard.jsx` — componente card individual con badge de categoría
    - [x] Crear `src/features/catalogo/ProductDetail.jsx` — vista detalle con todos los campos del producto
    - [x] Implementar filtrado por categoría dietaria con chips/botones (vegano, sin gluten, vegetariano, etc.)
    - [x] Conectar store con componentes (fetchProducts al montar, filtrar via service)

- [x] Task: Conductor - User Manual Verification 'F1 — Catálogo de Productos' (Protocol in workflow.md)

## Phase 4: F2 — Gestión de Heladeras

- [x] Task: Crear store Zustand para heladeras
    - [x] Crear store `src/features/heladeras/fridgeStore.js` con estado: fridges[], selectedFridge, loading
    - [x] Acciones: fetchFridges(), fetchFridgeById(id), fetchStock(fridgeId)

- [x] Task: Implementar pantalla de heladeras
    - [x] Crear `src/features/heladeras/Heladeras.jsx` (lista + detalle integrados)
    - [x] Crear `src/features/heladeras/FridgeCard.jsx` — card con indicador visual: verde (active), amarillo (maintenance), rojo (out_of_service)
    - [x] Crear `src/features/heladeras/FridgeDetail.jsx` — vista detalle que incluye tabla de stock (producto + cantidad)
    - [x] Conectar store con componentes

- [x] Task: Conductor - User Manual Verification 'F2 — Gestión de Heladeras' (Protocol in workflow.md)

## Phase 5: F3 — Órdenes

- [x] Task: Crear store Zustand para órdenes
    - [x] Crear store `src/features/ordenes/orderStore.js` con estado: orders[], selectedOrder, loading
    - [x] Acciones: fetchOrders(), fetchOrderById(id), createOrder(data), cancelOrder(id), payOrder(id)

- [x] Task: Implementar pantalla de órdenes
    - [x] Crear `src/features/ordenes/OrderList.jsx` — lista de órdenes con id, estado, total
    - [x] Crear `src/features/ordenes/OrderDetail.jsx` — detalle con items, total, estado y timeline de acciones
    - [x] Crear `src/features/ordenes/OrderForm.jsx` — formulario para crear orden: seleccionar usuario mock, heladera, productos y cantidades -> calcular total automáticamente
    - [x] Conectar store con componentes
    - [x] App.jsx con navegación mobile-first (bottom tabs en mobile, top tabs en desktop)

- [x] Task: Conductor - User Manual Verification 'F3 — Órdenes' (Protocol in workflow.md)