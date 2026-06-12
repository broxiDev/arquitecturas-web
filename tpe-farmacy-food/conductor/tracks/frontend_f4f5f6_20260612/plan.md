# Implementation Plan

## Phase 1: Mock Data y Servicios API para F4, F5, F6

- [x] Task: Crear archivos de datos mock
    - [x] Crear `src/mocks/kitchen.json` con 3-5 planes diarios (date, items: [{productId, productName, suggestedQuantity}])
    - [x] Crear `src/mocks/recommendations.json` con recomendaciones por usuario (userId, products: [{productId, productName, reason, dietaryCategory, price}])
    - [x] Crear `src/mocks/notifications.json` con notificaciones mock (id, userId, message, productId, productName, read, createdAt)

- [x] Task: Implementar capa de servicios API (kitchenService, recommendationService, notificationService)
    - [x] Crear `src/services/kitchenService.js` con: getDailyPlan(date?), getSalesHistory(from, to) — importa kitchen.json, usa Promises con setTimeout
    - [x] Crear `src/services/recommendationService.js` con: getRecommendations(userId) — importa recommendations.json
    - [x] Crear `src/services/notificationService.js` con: getNotifications(userId), subscribe(userId, data), unsubscribe(userId, productId) — importa notifications.json

- [x] Task: Conductor - User Manual Verification 'Mock Data y Servicios API para F4, F5, F6' (Protocol in workflow.md)

## Phase 2: F4 — Reporte Diario de Cocina

- [x] Task: Crear store Zustand para cocina
    - [x] Crear store `src/features/cocina/kitchenStore.js` con estado: dailyPlan, selectedDate, loading
    - [x] Acciones: fetchDailyPlan(date?), setDate(date), fetchAvailableDates()

- [x] Task: Implementar pantalla de cocina
    - [x] Crear `src/features/cocina/Cocina.jsx` — pantalla principal con selector de fecha y tabla de producción
    - [x] Crear `src/features/cocina/DailyPlanTable.jsx` — tabla con producto y cantidad sugerida, card resumen con total
    - [x] Crear `src/features/cocina/DatePicker.jsx` — selector de fecha simple para consultar planes de días anteriores
    - [x] Conectar store con componentes (fetchDailyPlan al montar y al cambiar fecha)

- [x] Task: Conductor - User Manual Verification 'F4 — Reporte Diario de Cocina' (Protocol in workflow.md)

## Phase 3: F5 — Recomendaciones

- [x] Task: Crear store Zustand para recomendaciones
    - [x] Crear store `src/features/recomendaciones/recommendationStore.js` con estado: recommendations, selectedUser, loading
    - [x] Acciones: fetchRecommendations(userId), setSelectedUser(userId)

- [x] Task: Implementar pantalla de recomendaciones
    - [x] Crear `src/features/recomendaciones/Recomendaciones.jsx` — pantalla principal con selector de usuario y lista de recomendaciones
    - [x] Crear `src/features/recomendaciones/RecommendationCard.jsx` — card con producto, razón y badge de categoría dietaria
    - [x] Crear `src/features/recomendaciones/UserSelector.jsx` — selector simple de usuario mock (3 usuarios)
    - [x] Conectar store con componentes

- [x] Task: Conductor - User Manual Verification 'F5 — Recomendaciones' (Protocol in workflow.md)

## Phase 4: F6 — Notificaciones + Suscripción

- [x] Task: Crear store Zustand para notificaciones
    - [x] Crear store `src/features/notificaciones/notificationStore.js` con estado: notifications, subscriptions, unreadCount, loading
    - [x] Acciones: fetchNotifications(userId), markAsRead(id), subscribe(userId, data), unsubscribe(userId, productId)

- [x] Task: Implementar pantalla de notificaciones
    - [x] Crear `src/features/notificaciones/Notificaciones.jsx` — pantalla con tabs internos (Notificaciones / Suscribirse)
    - [x] Crear `src/features/notificaciones/NotificationList.jsx` — lista de notificaciones con badge de leído/no leído, mensaje, nombre de producto y fecha
    - [x] Crear `src/features/notificaciones/SubscriptionForm.jsx` — formulario para suscribirse a notificaciones de un producto (selección de producto + device token mock)
    - [x] Conectar store con componentes

- [x] Task: Conductor - User Manual Verification 'F6 — Notificaciones + Suscripción' (Protocol in workflow.md)

## Phase 5: Navegación extendida a 6 tabs

- [x] Task: Actualizar App.jsx con navegación de 6 tabs
    - [x] Agregar las 3 nuevas pantallas (Cocina, Recomendaciones, Notificaciones) al array PANTALLAS en App.jsx
    - [x] Actualizar bottom-nav y top-nav con 6 tabs: Catálogo, Heladeras, Órdenes, Cocina, Recomendaciones, Notificaciones
    - [x] Agregar iconos representativos para cada tab
    - [x] Verificar que `npm run dev` compila y las 6 pantallas son navegables
    - [x] Agregar badge de notificaciones no leídas en tab de Notificaciones
    - [x] Recomendaciones: pasar onNavigateToCatalogo prop para navegación cruzada

- [x] Task: Conductor - User Manual Verification 'Navegación extendida a 6 tabs' (Protocol in workflow.md)