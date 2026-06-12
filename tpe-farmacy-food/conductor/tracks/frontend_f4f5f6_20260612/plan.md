# Implementation Plan

## Phase 1: Mock Data y Servicios API para F4, F5, F6

- [ ] Task: Crear archivos de datos mock
    - [ ] Crear `src/mocks/kitchen.json` con 3-5 planes diarios (date, items: [{productId, productName, suggestedQuantity}])
    - [ ] Crear `src/mocks/recommendations.json` con recomendaciones por usuario (userId, products: [{productId, productName, reason, dietaryCategory, price}])
    - [ ] Crear `src/mocks/notifications.json` con notificaciones mock (id, userId, message, productId, productName, read, createdAt)

- [ ] Task: Implementar capa de servicios API (kitchenService, recommendationService, notificationService)
    - [ ] Crear `src/services/kitchenService.js` con: getDailyPlan(date?), getSalesHistory(from, to) — importa kitchen.json, usa Promises con setTimeout
    - [ ] Crear `src/services/recommendationService.js` con: getRecommendations(userId) — importa recommendations.json
    - [ ] Crear `src/services/notificationService.js` con: getNotifications(userId), subscribe(userId, data), unsubscribe(userId, productId) — importa notifications.json

- [ ] Task: Conductor - User Manual Verification 'Mock Data y Servicios API para F4, F5, F6' (Protocol in workflow.md)

## Phase 2: F4 — Reporte Diario de Cocina

- [ ] Task: Crear store Zustand para cocina
    - [ ] Crear store `src/features/cocina/kitchenStore.js` con estado: dailyPlan, selectedDate, loading
    - [ ] Acciones: fetchDailyPlan(date?), setDate(date)

- [ ] Task: Implementar pantalla de cocina
    - [ ] Crear `src/features/cocina/Cocina.jsx` — pantalla principal con selector de fecha y tabla de producción
    - [ ] Crear `src/features/cocina/DailyPlanTable.jsx` — tabla con producto y cantidad sugerida, card resumen con total
    - [ ] Crear `src/features/cocina/DatePicker.jsx` — selector de fecha simple para consultar planes de días anteriores
    - [ ] Conectar store con componentes (fetchDailyPlan al montar y al cambiar fecha)

- [ ] Task: Conductor - User Manual Verification 'F4 — Reporte Diario de Cocina' (Protocol in workflow.md)

## Phase 3: F5 — Recomendaciones

- [ ] Task: Crear store Zustand para recomendaciones
    - [ ] Crear store `src/features/recomendaciones/recommendationStore.js` con estado: recommendations, selectedUser, loading
    - [ ] Acciones: fetchRecommendations(userId), setSelectedUser(userId)

- [ ] Task: Implementar pantalla de recomendaciones
    - [ ] Crear `src/features/recomendaciones/Recomendaciones.jsx` — pantalla principal con selector de usuario y lista de recomendaciones
    - [ ] Crear `src/features/recomendaciones/RecommendationCard.jsx` — card con producto, razón y badge de categoría dietaria
    - [ ] Crear `src/features/recomendaciones/UserSelector.jsx` — selector simple de usuario mock (3 usuarios)
    - [ ] Conectar store con componentes

- [ ] Task: Conductor - User Manual Verification 'F5 — Recomendaciones' (Protocol in workflow.md)

## Phase 4: F6 — Notificaciones + Suscripción

- [ ] Task: Crear store Zustand para notificaciones
    - [ ] Crear store `src/features/notificaciones/notificationStore.js` con estado: notifications, subscriptions, unreadCount, loading
    - [ ] Acciones: fetchNotifications(userId), markAsRead(id), subscribe(userId, data), unsubscribe(userId, productId)

- [ ] Task: Implementar pantalla de notificaciones
    - [ ] Crear `src/features/notificaciones/Notificaciones.jsx` — pantalla con tabs internos (Notificaciones / Suscribirse)
    - [ ] Crear `src/features/notificaciones/NotificationList.jsx` — lista de notificaciones con badge de leído/no leído, mensaje, nombre de producto y fecha
    - [ ] Crear `src/features/notificaciones/SubscriptionForm.jsx` — formulario para suscribirse a notificaciones de un producto (selección de producto + device token mock)
    - [ ] Conectar store con componentes

- [ ] Task: Conductor - User Manual Verification 'F6 — Notificaciones + Suscripción' (Protocol in workflow.md)

## Phase 5: Navegación extendida a 6 tabs

- [ ] Task: Actualizar App.jsx con navegación de 6 tabs
    - [ ] Agregar las 3 nuevas pantallas (Cocina, Recomendaciones, Notificaciones) al array PANTALLAS en App.jsx
    - [ ] Actualizar bottom-nav y top-nav con 6 tabs: Catálogo, Heladeras, Órdenes, Cocina, Recomendaciones, Notificaciones
    - [ ] Agregar iconos representativos para cada tab
    - [ ] Verificar que `npm run dev` compila y las 6 pantallas son navegables

- [ ] Task: Conductor - User Manual Verification 'Navegación extendida a 6 tabs' (Protocol in workflow.md)