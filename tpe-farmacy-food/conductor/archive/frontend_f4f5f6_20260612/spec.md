# Spec: Frontend React — Features F4, F5, F6

## Overview
Extender el frontend existente de FarmacyFood agregando las 3 features restantes del MVP: F4 (Planificación de Cocina), F5 (Recomendaciones) y F6 (Notificaciones), con datos mock locales. Se mantiene el mismo patrón arquitectónico: servicios API con firmas REST reales, stores Zustand, y componentes mobile-first con Tailwind.

## Functional Requirements

### FR-1: Navegación extendida a 6 tabs
- Actualizar el bottom-nav y top-nav de App.jsx para incluir 6 tabs: Catálogo, Heladeras, Órdenes, Cocina, Recomendaciones, Notificaciones
- Cada tab navega a su pantalla correspondiente
- Iconos representativos para cada tab

### FR-2: Capa de Servicios API para F4, F5, F6
- `kitchenService.js`: getDailyPlan(date?), getSalesHistory(from, to)
- `recommendationService.js`: getRecommendations(userId)
- `notificationService.js`: getNotifications(userId), subscribe(userId, data), unsubscribe(userId, productId)
- En modo mock: importan datos de `mocks/kitchen.json`, `mocks/recommendations.json`, `mocks/notifications.json`
- Devuelven Promises con setTimeout simulado

### FR-3: Mock Data para F4, F5, F6
- `kitchen.json`: 3-5 planes diarios con fecha y lista de PlanItem (productId, productName, suggestedQuantity)
- `recommendations.json`: recomendaciones por usuario con productos sugeridos y razón (ej. "Basado en tu historial", "Usuarios similares")
- `notifications.json`: notificaciones mock con id, userId, message, productId, productName, read, createdAt

### FR-4: Pantalla F4 — Reporte Diario de Cocina
- Visualización del plan de producción diario sugerido en tabla (producto, cantidad sugerida)
- Selector de fecha para ver planes de días anteriores
- Card resumen con total de productos sugeridos
- Store Zustand: `kitchenStore` con dailyPlan, selectedDate, loading

### FR-5: Pantalla F5 — Recomendaciones
- Lista de productos recomendados para el usuario seleccionado (mock: mostrar 3 usuarios para seleccionar)
- Cada recomendación muestra producto, razón y badge de categoría dietaria
- Botón para ver detalle del producto recomendado (redirige al catálogo)
- Store Zustand: `recommendationStore` con recommendations, selectedUser, loading

### FR-6: Pantalla F6 — Notificaciones + Suscripción
- Tab 1 (lista): Lista de notificaciones con badge de leído/no leído, mensaje, nombre de producto y fecha
- Tab 2 (suscripción): Formulario para suscribirse a notificaciones de un producto (selección de producto favorito + device token mock)
- Indicador visual de notificaciones no leídas en el tab de navegación
- Store Zustand: `notificationStore` con notifications, subscriptions, unreadCount, loading

## Non-Functional Requirements
- Mobile-first, consistente con el diseño existente de F1-F3
- Services con firmas REST listas para reemplazar por fetch real
- Código en español para nomenclatura

## Acceptance Criteria
- [ ] La app compila y ejecuta correctamente con `npm run dev`
- [ ] Los 6 tabs aparecen en la navegación (mobile bottom-nav, desktop top-nav)
- [ ] La pantalla de Cocina muestra el plan diario y permite cambiar fecha
- [ ] La pantalla de Recomendaciones muestra productos sugeridos por usuario
- [ ] La pantalla de Notificaciones muestra lista de notificaciones y formulario de suscripción
- [ ] Los servicios API están definidos con las firmas correctas
- [ ] Cada pantalla tiene su store Zustand funcional

## Out of Scope
- Conexión a backend real
- Autenticación o login
- Gráficos complejos en F4
- Tests unitarios o de integración
- Pantalla de historial de ventas (F4 — solo reporte diario)