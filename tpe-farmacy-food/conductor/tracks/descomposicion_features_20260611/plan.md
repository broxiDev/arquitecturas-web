# Implementation Plan

## Phase 1: Feature Decomposition and Task Definition

- [ ] Task: Analyze F1 — Catálogo de Productos (Gabi / product-service)
    - [ ] Revisar requisitos del product-service: CRUD de productos, filtrado por categoría dietaria
    - [ ] Definir entidad Product con todos sus campos (id, name, description, dietaryCategory, price, imageUrl, createdAt, updatedAt)
    - [ ] Especificar endpoints: POST /products, GET /products, GET /products/{id}, PUT /products/{id}, DELETE /products/{id}
    - [ ] Documentar lógica de filtrado: GET /products?category=vegano
    - [ ] Confirmar que product-service es standalone (sin integraciones entrantes ni salientes)

- [ ] Task: Analyze F2 — Gestión de Heladeras (Ale / fridge-service)
    - [ ] Revisar requisitos del fridge-service: registro de heladeras, stock, alertas de estado
    - [ ] Definir entidad Fridge (id, name, location, status, lastMaintenance)
    - [ ] Definir entidad FridgeStock (id, fridgeId, productId, quantity)
    - [ ] Especificar endpoints: GET /fridges, GET /fridges/{id}, GET /fridges/{id}/stock, PUT /fridges/{id}/stock
    - [ ] Documentar integración con order-service (actualización de stock al retirar/devolver)
    - [ ] Documentar integración con notification-service (alerta de producto repuesto)

- [ ] Task: Analyze F3 — Órdenes y Pagos (Fiore / order-service + Mati / api-gateway)
    - [ ] Revisar requisitos del order-service: creación de órdenes, pago, confirmación de retiro
    - [ ] Definir entidad Order (id, userId, fridgeId, items, total, status, paymentId, createdAt, updatedAt)
    - [ ] Definir entidad OrderItem (productId, productName, quantity, unitPrice)
    - [ ] Especificar endpoints: POST /orders, GET /orders, GET /orders/{id}, GET /orders/user/{userId}, PUT /orders/{id}/cancel, POST /orders/{id}/pay, POST /orders/{id}/confirm-pickup
    - [ ] Documentar integración con PayPal (gateway de pago externo)
    - [ ] Documentar integración con fridge-service (verificar stock, reservar, liberar)
    - [ ] Documentar integración con user-service (validar usuario)
    - [ ] Definir rutas del api-gateway: /api/orders/** con filtro JWT

- [ ] Task: Analyze F4 — Planificación de Cocina (Nahue / kitchen-service)
    - [ ] Revisar requisitos del kitchen-service: reporte diario, historial de ventas
    - [ ] Definir entidad DailyPlan (id, date, items) y PlanItem (productId, productName, suggestedQuantity)
    - [ ] Especificar endpoints: GET /kitchen/daily-plan?date=, GET /kitchen/sales-history?from=&to=
    - [ ] Documentar integración con order-service via OpenFeign (consumo de historial de ventas)
    - [ ] Definir DB: PostgreSQL para daily-plan, MongoDB para histórico agregado

- [ ] Task: Analyze F5 — Preferencias y Recomendaciones (Mati / user-service + Nahue / recommendation-service)
    - [ ] Revisar requisitos del user-service: registro, perfil, preferencias dietarias
    - [ ] Definir entidad User (id, name, email, passwordHash, dietaryPreferences, createdAt)
    - [ ] Especificar endpoints user-service: POST /users/register, GET /users/{id}, PUT /users/{id}/preferences, GET /users/{id}/history
    - [ ] Documentar integración user-service → order-service (historial de compras)
    - [ ] Revisar requisitos del recommendation-service: sugerencias personalizadas
    - [ ] Especificar endpoint: GET /recommendations/{userId}
    - [ ] Documentar integración recommendation-service → user-service + order-service via OpenFeign

- [ ] Task: Analyze F6 — Notificaciones (Mati / notification-service)
    - [ ] Revisar requisitos del notification-service: suscripciones, push notifications
    - [ ] Definir entidad Subscription (id, userId, deviceToken, productPreferences)
    - [ ] Especificar endpoints: POST /notifications/subscribe, POST /notifications/send
    - [ ] Documentar integración con Firebase Cloud Messaging (FCM) para push
    - [ ] Documentar escucha de eventos de fridge-service (producto repuesto) para disparar notificaciones

- [ ] Task: Conductor - User Manual Verification 'Feature Decomposition and Task Definition' (Protocol in workflow.md)
