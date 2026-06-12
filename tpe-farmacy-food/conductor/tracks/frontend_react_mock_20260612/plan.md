# Implementation Plan

## Phase 1: Setup del Proyecto Frontend

- [ ] Task: Crear proyecto React con Vite y configurar Tailwind CSS
    - [ ] Crear proyecto con `npm create vite@latest frontend -- --template react` dentro de `tpe-farmacy-food/`
    - [ ] Instalar dependencias: `npm install`
    - [ ] Instalar y configurar Tailwind CSS v3 con `tailwind.config.js` y `postcss.config.js`
    - [ ] Agregar directivas de Tailwind en `src/index.css`
    - [ ] Instalar Zustand: `npm install zustand`
    - [ ] Agregar `dev` script en `package.json` si no existe

- [ ] Task: Definir estructura de carpetas del proyecto
    - [ ] Crear `src/services/` (capa de API)
    - [ ] Crear `src/mocks/` (datos JSON estáticos)
    - [ ] Crear `src/features/catalogo/`, `src/features/heladeras/`, `src/features/ordenes/` (una carpeta por feature)
    - [ ] Crear `src/components/` (componentes compartidos)
    - [ ] Crear `src/stores/` (stores Zustand compartidas si las hay)

- [ ] Task: Conductor - User Manual Verification 'Setup del Proyecto Frontend' (Protocol in workflow.md)

## Phase 2: Mock Data y Capa de Servicios API

- [ ] Task: Crear archivos de datos mock
    - [ ] Crear `src/mocks/products.json` con 10 productos (id, name, description, dietaryCategory, price, imageUrl, createdAt)
    - [ ] Crear `src/mocks/fridges.json` con 6 heladeras (id, name, location con lat/lng, status, stock con productId+quantity)
    - [ ] Crear `src/mocks/orders.json` con 4 órdenes de ejemplo (id, userId, fridgeId, items[], total, status, paymentId, createdAt)

- [ ] Task: Implementar capa de servicios API (productService, fridgeService, orderService)
    - [ ] Crear `src/services/productService.js` con: getAll(categoria?), getById(id), create(data), update(id, data), delete(id) — importa products.json, usa Promises con setTimeout simulado
    - [ ] Crear `src/services/fridgeService.js` con: getAll(lat?, lng?), getById(id), getStock(fridgeId), updateStock(fridgeId, data) — importa fridges.json
    - [ ] Crear `src/services/orderService.js` con: create(data), getAll(), getById(id), getUserOrders(userId), cancel(id), pay(id), confirmPickup(id) — importa orders.json

- [ ] Task: Conductor - User Manual Verification 'Mock Data y Capa de Servicios API' (Protocol in workflow.md)

## Phase 3: F1 — Catálogo de Productos

- [ ] Task: Crear store Zustand para catálogo
    - [ ] Crear store `src/features/catalogo/productStore.js` con estado: products[], selectedProduct, filters, loading
    - [ ] Acciones: fetchProducts(filtros?), fetchProductById(id)

- [ ] Task: Implementar pantalla de catálogo
    - [ ] Crear `src/features/catalogo/ProductList.jsx` — grid de cards con imagen, nombre, categoría, precio
    - [ ] Crear `src/features/catalogo/ProductCard.jsx` — componente card individual con badge de categoría
    - [ ] Crear `src/features/catalogo/ProductDetail.jsx` — vista detalle con todos los campos del producto
    - [ ] Implementar filtrado por categoría dietaria con chips/botones (vegano, sin gluten, vegetariano, etc.)
    - [ ] Conectar store con componentes (fetchProducts al montar, filtrar localmente o via service)

- [ ] Task: Conductor - User Manual Verification 'F1 — Catálogo de Productos' (Protocol in workflow.md)

## Phase 4: F2 — Gestión de Heladeras

- [ ] Task: Crear store Zustand para heladeras
    - [ ] Crear store `src/features/heladeras/fridgeStore.js` con estado: fridges[], selectedFridge, loading
    - [ ] Acciones: fetchFridges(), fetchFridgeById(id), fetchStock(fridgeId)

- [ ] Task: Implementar pantalla de heladeras
    - [ ] Crear `src/features/heladeras/FridgeList.jsx` — lista de cards con nombre, ubicación, badge de estado
    - [ ] Crear `src/features/heladeras/FridgeCard.jsx` — card con indicador visual: verde (active), amarillo (maintenance), rojo (out_of_service)
    - [ ] Crear `src/features/heladeras/FridgeDetail.jsx` — vista detalle que incluye tabla de stock (producto + cantidad)
    - [ ] Conectar store con componentes

- [ ] Task: Conductor - User Manual Verification 'F2 — Gestión de Heladeras' (Protocol in workflow.md)

## Phase 5: F3 — Órdenes

- [ ] Task: Crear store Zustand para órdenes
    - [ ] Crear store `src/features/ordenes/orderStore.js` con estado: orders[], selectedOrder, loading
    - [ ] Acciones: fetchOrders(), fetchOrderById(id), createOrder(data), cancelOrder(id), payOrder(id)

- [ ] Task: Implementar pantalla de órdenes
    - [ ] Crear `src/features/ordenes/OrderList.jsx` — lista de órdenes con id, estado, total
    - [ ] Crear `src/features/ordenes/OrderDetail.jsx` — detalle con items, total, estado y timeline de acciones
    - [ ] Crear `src/features/ordenes/OrderForm.jsx` — formulario para crear orden: seleccionar usuario mock, heladera, productos y cantidades -> calcular total automáticamente
    - [ ] Conectar store con componentes

- [ ] Task: Conductor - User Manual Verification 'F3 — Órdenes' (Protocol in workflow.md)
