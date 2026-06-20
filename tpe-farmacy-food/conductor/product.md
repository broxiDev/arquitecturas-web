# Initial Concept

FarmacyFood es un sistema backend de microservicios que conecta una cocina fantasma con heladeras inteligentes distribuidas en comercios, permitiendo a los usuarios descubrir, comprar y recibir comida saludable desde su celular, mientras el sistema aprende sus preferencias y optimiza la producción diaria de forma automática.

# FarmacyFood — Guía de Producto

## Visión General
Sistema de microservicios con frontend web que conecta una cocina fantasma con heladeras inteligentes distribuidas en comercios, permitiendo a los usuarios descubrir, comprar y recibir comida saludable desde su celular.

## Misión
Dar respuesta a las necesidades dietarías de las personas mediante opciones de comidas sabrosas y que promuevan estilos de vida activos.

## Usuarios del Sistema
- **Cliente**: Usuario final que compra comida desde su celular, descubre productos y retira de heladeras.
- **Cocina Fantasma**: Equipo que registra productos y gestiona el catálogo.
- **Admin de Heladera**: Responsable del mantenimiento, stock y monitoreo de heladeras.
- **Admin de Cocina**: Encargado de planificar la producción diaria basada en datos históricos.

## Funcionalidades Principales (MVP)

### F1 — Catálogo de Productos
- Visualización de productos con nombre, descripción, categoría dietaria y precio
- Filtrado por categoría (vegano, sin gluten, vegetariano, etc.)
- Registro de productos por cocina fantasma con atributos completos

### F2 — Gestión de Heladeras
- Mapa de heladeras cercanas con estado operativo
- Stock disponible en tiempo real por heladera
- Actualización automática de stock al retirar/devolver productos
- Alertas por estado "fuera de servicio" o "en mantenimiento"

### F3 — Órdenes y Pagos
- Selección de productos y generación de orden
- Pago mediante gateway (PayPal u otro)
- Autorización de apertura de heladera post-pago
- Detalle de órdenes para auditoría

### F4 — Planificación de Cocina
- Reporte diario de producción sugerida basado en ventas históricas y stock remanente en heladeras
- Plan diario scopeado por cocina fantasma (kitchenId)
- Historial de ventas por producto y heladera

### F5 — Preferencias y Recomendaciones
- Registro de preferencias dietarias al registrarse
- Sugerencias basadas en historial de compras y usuarios similares

### F6 — Notificaciones
- Notificaciones push cuando un producto favorito está disponible

### Cliente Frontend (MVP Mock)
- **React 18 + Vite** — Interfaz web mobile-first para simulación de todas las features MVP (F1-F6)
- Catálogo de productos con filtrado por categoría dietaria
- Gestión de heladeras con indicadores de estado y stock
- Órdenes con creación, detalle, pago y cancelación
- Reporte diario de cocina con selector de fecha y tabla de producción
- Recomendaciones personalizadas por usuario con razones y badges de categoría
- Notificaciones con lista leído/no leído y suscripción a productos favoritos
- Navegación mobile-first con tabs inferiores
- 6 tabs de navegación (Catálogo, Heladeras, Órdenes, Cocina, Recomendaciones, Notificaciones)
- Capa de servicios API preparada para conectar a microservicios reales
- Datos mock en JSON estático, servicios con firmas REST originales

## Microservicios
- **product-service** — Catálogo de productos (F1)
- **fridge-service** — Stock y ubicación de heladeras (F2)
- **order-service** — Órdenes y pagos (F3)
- **user-service** — Perfiles y preferencias dietarias
- **notification-service** — Push alerts (F6)
- **kitchen-service** — Planificación de producción (F4)
- **recommendation-service** — Sugerencias personalizadas (F5)
- **api-gateway** — Entry point, JWT, routing

### Distribución por Integrante
| Integrante | Microservicios |
|---|---|
| 1 | product-service |
| 2 | fridge-service |
| 3 | order-service |
| 4 | kitchen-service + recommendation-service |
| 5 | user-service + notification-service + api-gateway |

