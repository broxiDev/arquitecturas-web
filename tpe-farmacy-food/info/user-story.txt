# FarmacyFood — User Stories

## Feature 1: Catálogo de Productos

**US-01** Como **cliente**, quiero ver el listado de productos disponibles con su nombre, descripción, categoría (vegano, sin gluten, etc.) y precio, para decidir qué quiero comer antes de ir a la heladera.

**US-02(Opcional)** Como **cliente**, quiero filtrar productos por categoría dietaria (vegetariano, vegano, sin gluten), para encontrar rápido opciones que se ajusten a mis necesidades.

**US-03** Como **cocina fantasma**, quiero registrar un nuevo producto con todos sus atributos (ID, nombre, descripción, categoría, precio, info nutricional, temperatura de conservación), para mantener el catálogo actualizado.
Nota: 
- Cada cocina fantasma tiene su propio catalogo. 
- Es el catalogo, no el producto disponible en la heladera.

---

## Feature 2: Gestión de Heladeras

**US-04** Como **cliente**, quiero ver en un mapa las heladeras cercanas a mi ubicación con su estado (operativa/fuera de servicio), para saber adónde ir a buscar mi comida.

**US-05** Como **cliente**, quiero ver el stock disponible en una heladera específica antes de ir, para no hacer el viaje en vano.

**US-06** Como **admin de heladera**, quiero que el sistema actualice automáticamente el stock cuando la heladera inteligente reporta que un producto fue retirado o devuelto, para tener inventario en tiempo real.

**US-07** Como **admin de heladera**, quiero recibir una alerta cuando una heladera pasa a estado "fuera de servicio" o "en mantenimiento", para poder actuar rápido.

---

## Feature 3: Órdenes y Pagos

**US-08** Como **cliente**, quiero seleccionar uno o más productos de una heladera específica y generar una orden, para formalizar mi compra.

**US-09** Como **cliente**, quiero pagar mi orden a través de un gateway de pago (PayPal u otro), para completar la transacción sin efectivo.

**US-10** Como **cliente**, quiero que el sistema autorice la apertura de la heladera una vez confirmado el pago, para retirar mis productos.
Nota:
- Aca asumimos buena fe de los consumidores.

**US-11** Como **admin de heladera**, quiero ver el detalle de cada orden procesada en mis heladeras (productos, cantidades, monto total, medio de pago y timestamp), para auditar las ventas y detectar anomalías.

---

## Feature 4: Planificación de Cocina

**US-12** Como **admin de cocina fantasma**, quiero recibir diariamente un reporte de qué productos y en qué cantidades debo preparar para el día siguiente, basado en ventas históricas, stock remanente y tendencias, para optimizar la producción y evitar desperdicios.
Nota:
- Por ej un mail, mensaje, de lo que pasó el día anterior

**US-13** Como **admin de cocina**, quiero ver el historial de ventas por producto y por heladera, para identificar tendencias y ajustar la producción manualmente si es necesario.
Notas:
- Por ej: Ver en la app, un historial de que se vendio en que dia/heladera


---

## Feature 5: Preferencias y Recomendaciones

**US-14** Como **cliente**, quiero registrar mis preferencias dietarias (tipo de dieta, alergias, productos favoritos), para recibir sugerencias personalizadas.
Nota:
- Puede ser al registrarse
- Para la primera entrega asumimos que esto solo se podra haer al registrarse.

**US-15** Como **cliente**, quiero ver sugerencias de productos basadas en mi historial de compras y en lo que compraron usuarios similares, para descubrir opciones nuevas que probablemente me gusten.


---

## Feature 6: Notificaciones

**US-16** Como **cliente**, quiero recibir una notificación push cuando un producto que marqué como favorito vuelve a estar disponible en una heladera cercana a mí, para no perdérmelo.

---

## Distribución sugerida por integrante

| Integrante | Features                           | Microservicio principal                  |
| ---------- | ---------------------------------- | ---------------------------------------- |
| 1          | F1 — Catálogo                      | Product Service                          |
| 2          | F2 — Heladeras                     | Fridge Service                           |
| 3          | F3 — Órdenes y Pagos               | Order Service                            |
| 4          | F4 + F5 — Cocina + Recomendaciones | Kitchen Service + Recommendation Service |
| 5          | F6 + transversal                   | Notification Service + API Gateway + JWT |
