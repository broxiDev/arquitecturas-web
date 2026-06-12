# Spec: Descomposición de Features en Tareas por Microservicio

## Overview
Track de análisis para descomponer las 6 features del MVP de FarmacyFood en tareas concretas por microservicio, asignadas a cada integrante del equipo. El objetivo es definir qué endpoints, entidades e integraciones requiere cada feature para que cada integrante sepa exactamente qué implementar en su microservicio.

## Functional Requirements

### FR-1: Feature → Microservicio Mapping
Cada feature del MVP se descompone en el o los microservicios involucrados, identificando claramente quién es responsable.

### FR-2: Definición de Endpoints
Para cada tarea se especifican los endpoints REST que debe exponer el microservicio.

### FR-3: Definición de Entidades
Para cada tarea se especifican las entidades/base de datos requeridas.

### FR-4: Puntos de Integración
Para cada tarea se especifican las dependencias entre microservicios (via OpenFeign).

## F1 — Catálogo de Productos
**Responsable: Gabi (product-service)**

### Product CRUD
- **Endpoints:** POST /products, GET /products, GET /products/{id}, PUT /products/{id}, DELETE /products/{id}
- **Entidad Product:** id, name, description, dietaryCategory, price, imageUrl, createdAt, updatedAt
- **DB:** PostgreSQL
- **Filtrado:** GET /products?category=vegano

## F2 — Gestión de Heladeras
**Responsable: Ale (fridge-service)**

### Registro y consulta de heladeras
- **Endpoints:** GET /fridges (con coordenadas para cercanía), GET /fridges/{id}
- **Entidad Fridge:** id, name, location (lat/lng), status (active/maintenance/out_of_service), lastMaintenance
- **DB:** PostgreSQL

### Gestión de stock por heladera
- **Endpoints:** GET /fridges/{id}/stock, PUT /fridges/{id}/stock (actualización por order-service al retirar/devolver)
- **Entidad FridgeStock:** id, fridgeId, productId, quantity
- **DB:** PostgreSQL

### Alertas de estado
- Endpoint interno para notificar cambio de estado (fuera de servicio, en mantenimiento)
- Integración saliente: notifica a notification-service cuando un producto vuelve a estar disponible

## F3 — Órdenes y Pagos
**Responsables: Fiore (order-service) + Mati (api-gateway)**

### Creación y gestión de órdenes
- **Endpoints:** POST /orders, GET /orders, GET /orders/{id}, GET /orders/user/{userId}, PUT /orders/{id}/cancel
- **Entidad Order:** id, userId, fridgeId, items (List<OrderItem>), total, status, paymentId, createdAt, updatedAt
- **Entidad OrderItem:** productId, productName, quantity, unitPrice
- **DB:** PostgreSQL

### Pago de órdenes
- **Endpoint:** POST /orders/{id}/pay
- Integración con gateway de pago externo (PayPal)
- Al confirmar pago, notifica a fridge-service para reservar/apertura de heladera

### Confirmación de retiro
- **Endpoint:** POST /orders/{id}/confirm-pickup
- Notifica a fridge-service para liberar stock

### Integraciones order-service
- **fridge-service:** verificar stock disponible, reservar al pagar, liberar al retirar, confirmar heladera
- **user-service:** validar existencia y estado del usuario

### Configuración api-gateway
- **Gateway (Mati):** Rutas a order-service bajo /api/orders/**
- Filtro JWT para endpoints protegidos

## F4 — Planificación de Cocina
**Responsable: Nahue (kitchen-service)**

### Reporte diario de producción
- **Endpoint:** GET /kitchen/daily-plan?date=YYYY-MM-DD
- Consulta historial de ventas desde order-service (OpenFeign) para calcular sugerencias
- **Entidad DailyPlan:** id, date, items (List<PlanItem>)
- **PlanItem:** productId, productName, suggestedQuantity
- **DB:** PostgreSQL

### Historial de ventas
- **Endpoint:** GET /kitchen/sales-history?from=&to=
- Agregación de datos de order-service
- **DB:** MongoDB (para datos históricos agregados)

## F5 — Preferencias y Recomendaciones
**Responsables: Mati (user-service) + Nahue (recommendation-service)**

### Registro y perfil de usuario
- **Endpoints:** POST /users/register, GET /users/{id}, PUT /users/{id}/preferences
- **Entidad User:** id, name, email, passwordHash, dietaryPreferences (List<String>), createdAt
- **DB:** PostgreSQL
- **user-service (Mati):** expone endpoint GET /users/{id}/history → consulta order-service para obtener historial de compras

### Recomendaciones personalizadas
- **Endpoints:** GET /recommendations/{userId}
- Algoritmo: basado en historial de compras (order-service) y preferencias del usuario (user-service) + usuarios similares
- **DB:** MongoDB (perfiles de preferencia, historial de compras cacheado)
- **recommendation-service (Nahue):** consulta user-service y order-service via OpenFeign

## F6 — Notificaciones
**Responsable: Mati (notification-service)**

### Envío de notificaciones push
- **Endpoints:** POST /notifications/subscribe, POST /notifications/send
- **Entidad Subscription:** id, userId, deviceToken, productPreferences (List<productId>)
- **DB:** MongoDB
- Escucha eventos de fridge-service (producto repuesto en heladera cercana) para disparar push
- Integración con Firebase Cloud Messaging (FCM) para push notifications

## Acceptance Criteria
- [ ] Cada feature (F1-F6) tiene al menos una tarea definida con endpoints, entidades e integraciones
- [ ] Cada tarea está asignada a un integrante específico
- [ ] Las integraciones entre microservicios están claramente especificadas (quién consume a quién)
- [ ] Todas las bases de datos (PostgreSQL/MongoDB) están asignadas por microservicio

## Out of Scope
- Implementación de seguridad OAuth2/autenticación avanzada más allá de JWT básico
- CI/CD pipeline
- Frontend o aplicación mobile
- Pruebas de carga/performance
