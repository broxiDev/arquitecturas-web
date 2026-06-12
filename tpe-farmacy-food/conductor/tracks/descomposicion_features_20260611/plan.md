# Implementation Plan

## Phase 1: Feature Decomposition and Task Definition

- [x] Task: Analyze F1 — Catálogo de Productos (Gabi / product-service)
    - [x] Revisar requisitos del product-service: CRUD de productos, filtrado por categoría dietaria
    - [x] Definir entidad Product con todos sus campos (id, name, description, dietaryCategory, price, imageUrl, createdAt, updatedAt)
    - [x] Especificar endpoints: POST /products, GET /products, GET /products/{id}, PUT /products/{id}, DELETE /products/{id}
    - [x] Documentar lógica de filtrado: GET /products?category=vegano
    - [x] Confirmar que product-service es standalone (sin integraciones entrantes ni salientes)

- [x] Task: Analyze F2 — Gestión de Heladeras (Ale / fridge-service)
    - [x] Revisar requisitos del fridge-service: registro de heladeras, stock, alertas de estado
    - [x] Definir entidad Fridge (id, name, location, status, lastMaintenance)
    - [x] Definir entidad FridgeStock (id, fridgeId, productId, quantity)
    - [x] Especificar endpoints: GET /fridges, GET /fridges/{id}, GET /fridges/{id}/stock, PUT /fridges/{id}/stock
    - [x] Documentar integración con order-service (actualización de stock al retirar/devolver)
    - [x] Documentar integración con notification-service (alerta de producto repuesto)

- [x] Task: Analyze F3 — Órdenes y Pagos (Fiore / order-service + Mati / api-gateway)
    - [x] Revisar requisitos del order-service: creación de órdenes, pago, confirmación de retiro
    - [x] Definir entidad Order (id, userId, fridgeId, items, total, status, paymentId, createdAt, updatedAt)
    - [x] Definir entidad OrderItem (productId, productName, quantity, unitPrice)
    - [x] Especificar endpoints: POST /orders, GET /orders, GET /orders/{id}, GET /orders/user/{userId}, PUT /orders/{id}/cancel, POST /orders/{id}/pay, POST /orders/{id}/confirm-pickup
    - [x] Documentar integración con PayPal (gateway de pago externo)
    - [x] Documentar integración con fridge-service (verificar stock, reservar, liberar)
    - [x] Documentar integración con user-service (validar usuario)
    - [x] Definir rutas del api-gateway: /api/orders/** con filtro JWT

- [x] Task: Analyze F4 — Planificación de Cocina (Nahue / kitchen-service)
    - [x] Revisar requisitos del kitchen-service: reporte diario, historial de ventas
    - [x] Definir entidad DailyPlan (id, date, items) y PlanItem (productId, productName, suggestedQuantity)
    - [x] Especificar endpoints: GET /kitchen/daily-plan?date=, GET /kitchen/sales-history?from=&to=
    - [x] Documentar integración con order-service via OpenFeign (consumo de historial de ventas)
    - [x] Definir DB: PostgreSQL para daily-plan, MongoDB para histórico agregado

- [x] Task: Analyze F5 — Preferencias y Recomendaciones (Mati / user-service + Nahue / recommendation-service)
    - [x] Revisar requisitos del user-service: registro, perfil, preferencias dietarias
    - [x] Definir entidad User (id, name, email, passwordHash, dietaryPreferences, createdAt)
    - [x] Especificar endpoints user-service: POST /users/register, GET /users/{id}, PUT /users/{id}/preferences, GET /users/{id}/history
    - [x] Documentar integración user-service → order-service (historial de compras)
    - [x] Revisar requisitos del recommendation-service: sugerencias personalizadas
    - [x] Especificar endpoint: GET /recommendations/{userId}
    - [x] Documentar integración recommendation-service → user-service + order-service via OpenFeign

- [x] Task: Analyze F6 — Notificaciones (Mati / notification-service)
    - [x] Revisar requisitos del notification-service: suscripciones, push notifications
    - [x] Definir entidad Subscription (id, userId, deviceToken, productPreferences)
    - [x] Especificar endpoints: POST /notifications/subscribe, POST /notifications/send
    - [x] Documentar integración con Firebase Cloud Messaging (FCM) para push
    - [x] Documentar escucha de eventos de fridge-service (producto repuesto) para disparar notificaciones

- [x] Task: Conductor - User Manual Verification 'Feature Decomposition and Task Definition' (Protocol in workflow.md)