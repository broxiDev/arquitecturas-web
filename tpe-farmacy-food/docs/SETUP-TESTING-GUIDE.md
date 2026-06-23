# Guía de Levantamiento y Pruebas — FarmacyFood

Guía paso a paso para poner en marcha el sistema FarmacyFood y verificar las 16 user stories implementadas.

---

## Índice

1. [Requisitos previos](#1-requisitos-previos)
2. [Levantamiento del sistema](#2-levantamiento-del-sistema)
3. [Datos semilla (seed data)](#3-datos-semilla-seed-data)
4. [Tabla de endpoints por User Story](#4-tabla-de-endpoints-por-user-story)
5. [Flujos de prueba](#5-flujos-de-prueba)

---

## 1. Requisitos previos

| Herramienta | Versión | Verificación |
|---|---|---|
| Java JDK | 21+ | `java -version` |
| Maven | 3.9+ | `mvn -version` |
| Docker Compose | v2.24+ | `docker compose version` |
| Git | cualquiera | `git --version` |
| curl | cualquiera | `curl --version` |

> **Windows:** Se recomienda usar Git Bash, WSL2 o PowerShell. Los scripts `.sh` requieren un entorno Unix-like.  
> **Docker Desktop** debe estar instalado y corriendo.

---

## 2. Levantamiento del sistema

### 2.1 Clonar repositorio

```bash
git clone <repo-url>
cd tpe-farmacy-food
```

### 2.2 Levantar bases de datos con Docker

```bash
docker compose up -d
```

Esto inicia 7 contenedores con sus respectivos seed data automáticos:

| Contenedor | Puerto | Motor | Base de datos |
|---|---|---|---|
| `product-postgres` | 5437 | PostgreSQL 16 | `product_db` |
| `fridge-postgres` | 5433 | PostgreSQL 16 | `fridge_db` |
| `fridge-mongo` | 27018 | MongoDB 7 | `fridge_db` (eventos) |
| `order-postgres` | 5434 | PostgreSQL 16 | `order_db` |
| `kitchen-postgres` | 5432 | PostgreSQL 16 | `kitchen_db` |
| `user-postgres` | 5435 | PostgreSQL 16 | `user_db` |
| `notification-mongo` | 27019 | MongoDB 7 | `notification_db` |

Verificar que todos los contenedores estén activos:

```bash
docker compose ps
```

Para reiniciar las bases desde cero (borra todos los datos):

```bash
docker compose down -v && docker compose up -d
```

### 2.3 Iniciar Eureka (discovery-service)

```bash
mvn spring-boot:run -pl discovery-service
```

Esperar 10-15 segundos y verificar: [http://localhost:8761](http://localhost:8761) — Debería verse la consola de Eureka.

### 2.4 Iniciar API Gateway

```bash
mvn spring-boot:run -pl api-gateway
```

Verificar: `curl http://localhost:8080/actuator/health`

### 2.5 Iniciar los 7 microservicios

Abrir **una terminal por servicio** o ejecutar en background:

```bash
# Terminal 1
mvn spring-boot:run -pl product-service

# Terminal 2
mvn spring-boot:run -pl fridge-service

# Terminal 3
mvn spring-boot:run -pl order-service

# Terminal 4
mvn spring-boot:run -pl kitchen-service

# Terminal 5
mvn spring-boot:run -pl user-service

# Terminal 6
mvn spring-boot:run -pl recommendation-service

# Terminal 7
mvn spring-boot:run -pl notification-service
```

### 2.6 Verificar que todo está funcionando

```bash
# Health checks individuales
curl http://localhost:8081/api/v1/productos/health
curl http://localhost:8082/api/v1/heladeras/health
curl http://localhost:8083/api/v1/ordenes/health
curl http://localhost:8084/api/v1/cocina/health
curl http://localhost:8085/api/v1/recomendaciones/health
curl http://localhost:8086/api/v1/usuarios/health
curl http://localhost:8087/api/v1/notificaciones/health

# Eureka — deben figurar todos los servicios como UP
http://localhost:8761

# Gateway — Swagger centralizado
http://localhost:8080/swagger-ui.html
```

### 2.7 Script automatizado

Existe un script que automatiza todo el proceso:

```bash
./scripts/start.sh
```

Detener todo:

```bash
./scripts/stop.sh
```

> Los scripts requieren entorno Unix (Git Bash en Windows). Dejan logs en `/tmp/*.log` y archivos PID en `/tmp/farmacyfood-pids/`.

### 2.8 Iniciar Frontend Mock

Existe un frontend de prueba en `frontend/` que se conecta al Gateway en `localhost:8080`.
Actualmente es un **mock visual** que no implementa los servicios reales: las operaciones
(listar productos, crear órdenes, etc.) se simulan localmente con datos ficticios.

```bash
cd frontend && npm run dev
```

Abrir en [http://localhost:5173](http://localhost:5173)

---

## 3. Datos semilla (seed data)

Las bases de datos se cargan automáticamente al iniciar Docker con los siguientes datos. También pueden usarse para pruebas manuales vía API.

### 3.1 Catálogos y Productos (product-service)

**3 catálogos** (uno por cocina fantasma):

| ID Catálogo | Cocina |
|---|---|
| 1 | `COCINA-DULCE` |
| 2 | `COCINA-CELIACA` |
| 3 | `COCINA-VEGANA` |

**9 productos:**

| ID | Nombre | Categoría | Precio | Catálogo |
|---|---|---|---|---|
| 101 | Brownie de Chocolate | DULCE | $7.500 | COCINA-DULCE |
| 102 | Cheesecake | DULCE | $9.500 | COCINA-DULCE |
| 103 | Tiramisú | DULCE | $8.800 | COCINA-DULCE |
| 201 | Tostada de Palta Sin Gluten | SIN_GLUTEN | $7.200 | COCINA-CELIACA |
| 202 | Bowl de Quinoa Sin Gluten | SIN_GLUTEN | $9.800 | COCINA-CELIACA |
| 203 | Rolls de Primavera de Arroz | SIN_GLUTEN | $6.500 | COCINA-CELIACA |
| 301 | Buddha Bowl Vegano | VEGANO | $8.500 | COCINA-VEGANA |
| 302 | Salteado de Tofu | VEGANO | $7.800 | COCINA-VEGANA |
| 303 | Curry de Garbanzos | VEGANO | $9.200 | COCINA-VEGANA |

### 3.2 Heladeras y Stock (fridge-service)

**6 heladeras** (2 por cocina, ubicadas en CABA):

| ID | Nombre | Latitud | Longitud | Dirección | Cocina |
|---|---|---|---|---|---|
| 1 | Heladera Palermo Dulce | -34.5883 | -58.4223 | Av. Santa Fe 1234, Palermo | COCINA-DULCE |
| 2 | Heladera Centro Dulce | -34.6033 | -58.3817 | Av. Corrientes 567, Microcentro | COCINA-DULCE |
| 3 | Heladera Belgrano Celiaca | -34.5632 | -58.4512 | Av. Cabildo 890, Belgrano | COCINA-CELIACA |
| 4 | Heladera Devoto Celiaca | -34.6034 | -58.5123 | Av. Lincoln 345, Villa Devoto | COCINA-CELIACA |
| 5 | Heladera Palermo Vegana | -34.5856 | -58.4301 | Av. Scalabrini Ortiz 1234, Palermo | COCINA-VEGANA |
| 6 | Heladera Villa Crespo Vegana | -34.5956 | -58.4401 | Av. Corrientes 4567, Villa Crespo | COCINA-VEGANA |

**Stock inicial por heladera:**

| Heladera | Producto | Cantidad |
|---|---|---|
| 1 (Palermo Dulce) | Brownie de Chocolate (101) | 3 |
| 1 (Palermo Dulce) | Cheesecake (102) | 2 |
| 1 (Palermo Dulce) | Tiramisú (103) | 2 |
| 2 (Centro Dulce) | Brownie de Chocolate (101) | 2 |
| 2 (Centro Dulce) | Cheesecake (102) | 1 |
| 2 (Centro Dulce) | Tiramisú (103) | 3 |
| 3 (Belgrano Celiaca) | Tostada de Palta Sin Gluten (201) | 3 |
| 3 (Belgrano Celiaca) | Bowl de Quinoa Sin Gluten (202) | 2 |
| 3 (Belgrano Celiaca) | Rolls de Primavera de Arroz (203) | 2 |
| 4 (Devoto Celiaca) | Tostada de Palta Sin Gluten (201) | 1 |
| 4 (Devoto Celiaca) | Bowl de Quinoa Sin Gluten (202) | 2 |
| 4 (Devoto Celiaca) | Rolls de Primavera de Arroz (203) | 3 |
| 5 (Palermo Vegana) | Buddha Bowl Vegano (301) | 3 |
| 5 (Palermo Vegana) | Salteado de Tofu (302) | 2 |
| 5 (Palermo Vegana) | Curry de Garbanzos (303) | 3 |
| 6 (Villa Crespo Vegana) | Buddha Bowl Vegano (301) | 2 |
| 6 (Villa Crespo Vegana) | Salteado de Tofu (302) | 1 |
| 6 (Villa Crespo Vegana) | Curry de Garbanzos (303) | 2 |

### 3.3 Usuarios (user-service)

| ID | Nombre | Email | Preferencias dietarias |
|---|---|---|---|
| 1 | Quique TDV | maria@test.com | vegano |
| 2 | Juan Pérez | juan@test.com | sin gluten |
| 3 | Carolina Ruiz | caro@test.com | vegetariano, sin gluten |
| 4 | Pedro Martínez | pedro@test.com | vegano, sin gluten |
| 5 | Ana López | ana@test.com | vegetariano |
| 6 | Matías Bordonaro | matias@test.com | vegano, sin gluten |
| 7 | Fiorella Di Fiore | fiorella@test.com | vegetariano |
| 8 | Nahuel Di Fiore | nahuel@test.com | sin gluten |
| 9 | Gabriel Marrero | gabriel@test.com | vegano, vegetariano |
| 10 | Ale Machado | ale@test.com | sin gluten, vegetariano |

### 3.4 Órdenes precargadas (order-service)

16 órdenes con status `PAID` o `PICKED_UP` distribuidas en los últimos 12 días. Cubren las 3 cocinas y las 6 heladeras.

IDs de órdenes: 1 al 16.

### 3.5 Planes diarios precargados (kitchen-service)

3 planes (uno por cocina) para la fecha actual:

| Cocina | Producto | Cantidad sugerida |
|---|---|---|
| COCINA-DULCE | Brownie de Chocolate | 5 |
| COCINA-DULCE | Cheesecake | 4 |
| COCINA-DULCE | Tiramisú | 3 |
| COCINA-CELIACA | Tostada de Palta Sin Gluten | 8 |
| COCINA-CELIACA | Bowl de Quinoa Sin Gluten | 4 |
| COCINA-CELIACA | Rolls de Primavera de Arroz | 5 |
| COCINA-VEGANA | Buddha Bowl Vegano | 7 |
| COCINA-VEGANA | Salteado de Tofu | 4 |
| COCINA-VEGANA | Curry de Garbanzos | 4 |

### 3.6 Perfiles de recomendación precargados (recommendation-service)

| User ID | Preferencias |
|---|---|
| 1 | VEGANO, SIN_GLUTEN |
| 2 | VEGETARIANO |

---

## 4. Tabla de endpoints por User Story

| US | Descripción | Método | Endpoint | Ejemplo | Servicio (puerto) |
|---|---|---|---|---|---|
| US-01 | Listar productos | GET | `/api/v1/productos` | — | product (8081) |
| US-02 | Filtrar por categoría | GET | `/api/v1/productos?category={cat}` | `category=VEGANO` | product (8081) |
| US-03 | Registrar producto en catálogo | POST | `/api/v1/productos/cocina/{cocinaId}` | `cocina/COCINA-DULCE` | product (8081) |
| US-04 | Heladeras cercanas | GET | `/api/v1/heladeras?lat={lat}&lng={lng}&radius={km}` | `lat=-34.5883&lng=-58.4223&radius=5` | fridge (8082) |
| US-05 | Stock de una heladera | GET | `/api/v1/heladeras/{id}/stock` | `heladeras/1/stock` | fridge (8082) |
| US-06 | Actualizar stock | PUT | `/api/v1/heladeras/{heladeraId}/stock` | `heladeras/1/stock` | fridge (8082) |
| US-07 | Alerta de cambio de estado | — | Automático al cambiar status de heladera | — | fridge (8082) |
| US-08 | Crear orden | POST | `/api/v1/ordenes` | — | order (8083) |
| US-09 | Pagar orden | POST | `/api/v1/ordenes/{id}/pagar` | `ordenes/17/pagar` | order (8083) |
| US-10 | Confirmar retiro | POST | `/api/v1/ordenes/{id}/confirmar-retiro` | `ordenes/17/confirmar-retiro` | order (8083) |
| US-11 | Ver detalle de órdenes | GET | `/api/v1/ordenes` / `/api/v1/ordenes/{id}` | `ordenes/17` | order (8083) |
| US-12 | Generar/consultar plan diario | POST/GET | `/api/v1/cocina/plan-diario?cocinaId={id}&fecha={date}` | `cocinaId=COCINA-DULCE&fecha=2026-06-23` | kitchen (8084) |
| US-13 | Historial de ventas | GET | `/api/v1/cocina/historial-ventas` | — | kitchen (8084) |
| US-13 | Ventas agregadas por cocina | GET | `/api/v1/ordenes/historial-ventas/cocina/{cocinaId}` | `cocina/COCINA-DULCE` | order (8083) |
| US-14 | Registrar usuario | POST | `/api/v1/usuarios/registrar` | — | user (8086) |
| US-15 | Recomendaciones | GET | `/api/v1/recomendaciones/{userId}` | `recomendaciones/1` | recommendation (8085) |
| US-16 | Suscribir a notificaciones | POST | `/api/v1/notificaciones/suscribir` | — | notification (8087) |
| US-16 | Notificar disponibilidad | POST | `/api/v1/notificaciones/notificar-disponibilidad` | — | notification (8087) |
| US-16 | Ver suscripción de usuario | GET | `/api/v1/notificaciones/suscribir/{userId}` | `suscribir/1` | notification (8087) |
| US-16 | Ver notificaciones de usuario | GET | `/api/v1/notificaciones/usuario/{userId}` | `usuario/1` | notification (8087) |
| US-16 | Ver notificaciones no leídas | GET | `/api/v1/notificaciones/usuario/{userId}/no-leidas` | `usuario/1/no-leidas` | notification (8087) |
| US-16 | Marcar notificación como leída | PUT | `/api/v1/notificaciones/{id}/leer` | `notificaciones/1/leer` | notification (8087) |

---

## 5. Flujos de prueba

Todos los ejemplos usan el puerto del Gateway (`8080`) para simular el acceso real. También pueden probarse directamente contra cada servicio usando su puerto individual.

---

### Flujo A: Catálogo de Productos (US-01, US-02, US-03)

#### A.1 Listar todos los productos (US-01)

```bash
curl -s 'http://localhost:8080/api/v1/productos' | jq
```

Respuesta esperada: array JSON con los 9 productos (IDs 101-303).

#### A.2 Filtrar por categoría dietaria (US-02)

```bash
curl -s 'http://localhost:8080/api/v1/productos?category=VEGANO' | jq
```

Respuesta esperada: 3 productos veganos (IDs 301-303).

Otras categorías válidas: `DULCE`, `SIN_GLUTEN`.

#### A.3 Registrar un nuevo producto en un catálogo (US-03)

```bash
curl -s -X POST 'http://localhost:8080/api/v1/productos/cocina/1' \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "Mousse de Maracuyá",
    "description": "Mousse cremoso de maracuyá con base de galleta",
    "dietaryCategory": "DULCE",
    "price": 8200.00,
    "imageUrl": "/images/mousse-maracuya.jpg",
    "nutritionalInfo": "Calorías: 350kcal, Proteínas: 6g, Carbohidratos: 40g, Grasas: 18g",
    "conservacionTemperature": 4.0
  }' | jq
```

> Nota: `cocinaId=1` corresponde a `COCINA-DULCE`. Usar 2 para `COCINA-CELIACA` y 3 para `COCINA-VEGANA`.

---

### Flujo B: Gestión de Heladeras (US-04, US-05, US-06, US-07)

#### B.1 Listar heladeras cercanas (US-04)

```bash
# Heladeras cerca de Palermo (radio 5 km)
curl -s 'http://localhost:8080/api/v1/heladeras?lat=-34.5883&lng=-58.4223&radius=5' | jq
```

Respuesta esperada: heladeras 1 (Palermo Dulce) y 5 (Palermo Vegana).

```bash
# Heladeras sin filtro (todas)
curl -s 'http://localhost:8080/api/v1/heladeras' | jq
```

```bash
# Filtrar por estado
curl -s 'http://localhost:8080/api/v1/heladeras?status=ACTIVE' | jq
```

#### B.2 Ver stock de una heladera (US-05)

```bash
curl -s 'http://localhost:8080/api/v1/heladeras/1/stock' | jq
```

Respuesta esperada: stock de la Heladera Palermo Dulce (Brownie ×3, Cheesecake ×2, Tiramisú ×2).

#### B.3 Actualizar stock automáticamente (US-06)

```bash
# Actualizar cantidad de un producto en una heladera
curl -s -X PUT 'http://localhost:8080/api/v1/heladeras/1/stock' \
  -H 'Content-Type: application/json' \
  -d '{"productId": 101, "quantity": 5}' | jq
```

Verificar el cambio:

```bash
curl -s 'http://localhost:8080/api/v1/heladeras/1/stock' | jq
```

#### B.4 Alerta por cambio de estado (US-07)

Al actualizar el estado de una heladera, se genera un evento en MongoDB y se notifica a notification-service.

```bash
# Cambiar estado de heladera 1 a "OUT_OF_SERVICE"
curl -s -X PUT 'http://localhost:8080/api/v1/heladeras/1' \
  -H 'Content-Type: application/json' \
  -d '{"status": "OUT_OF_SERVICE"}' | jq
```

Verificar que se generó la notificación:

```bash
curl -s 'http://localhost:8080/api/v1/notificaciones/usuario/1' | jq
```

Restaurar el estado:

```bash
curl -s -X PUT 'http://localhost:8080/api/v1/heladeras/1' \
  -H 'Content-Type: application/json' \
  -d '{"status": "ACTIVE"}' | jq
```

---

### Flujo C: Órdenes y Pagos (US-08, US-09, US-10, US-11)

> Este flujo requiere que **product-service**, **fridge-service** y **order-service** estén corriendo.
>
> **Nota:** `pagar` y `confirmar-retiro` usan `POST` (acciones sobre el recurso), mientras que `cancelar` usa `PUT` (actualización del recurso).

#### C.1 Crear una orden (US-08)

```bash
curl -s -X POST 'http://localhost:8080/api/v1/ordenes' \
  -H 'Content-Type: application/json' \
  -d '{
    "userId": 1,
    "fridgeId": 1,
    "items": [
      {"productId": 101, "productName": "Brownie de Chocolate", "quantity": 2, "unitPrice": 7500.00},
      {"productId": 102, "productName": "Cheesecake", "quantity": 1, "unitPrice": 9500.00}
    ]
  }' | jq
```

Guardar el `orderId` devuelto (ej: `17`). La orden se crea con status `PENDING`.

#### C.2 Pagar la orden (US-09)

```bash
curl -s -X POST 'http://localhost:8080/api/v1/ordenes/17/pagar' | jq
```

> Reemplazar `17` con el ID real de la orden creada.  
> El payment gateway es un mock; no requiere datos de tarjeta reales.

Respuesta esperada: status cambia a `PAID`, se asigna un `paymentId` (ej: `pay_mock_xxx`).

#### C.3 Confirmar retiro de productos (US-10)

```bash
curl -s -X POST 'http://localhost:8080/api/v1/ordenes/17/confirmar-retiro' | jq
```

Respuesta esperada: status cambia a `PICKED_UP`. El stock de la heladera se descuenta automáticamente.

#### C.4 Ver detalle de órdenes (US-11)

```bash
# Ver una orden específica
curl -s 'http://localhost:8080/api/v1/ordenes/17' | jq

# Ver todas las órdenes
curl -s 'http://localhost:8080/api/v1/ordenes' | jq

# Ver órdenes de un usuario
curl -s 'http://localhost:8080/api/v1/ordenes/usuario/1' | jq
```

##### Datos mock para crear órdenes adicionales

```bash
# Orden de COCINA-CELIACA (fridge 3)
curl -s -X POST 'http://localhost:8080/api/v1/ordenes' \
  -H 'Content-Type: application/json' \
  -d '{
    "userId": 2,
    "fridgeId": 3,
    "items": [
      {"productId": 201, "productName": "Tostada de Palta Sin Gluten", "quantity": 2, "unitPrice": 7200.00}
    ]
  }' | jq

# Orden de COCINA-VEGANA (fridge 5)
curl -s -X POST 'http://localhost:8080/api/v1/ordenes' \
  -H 'Content-Type: application/json' \
  -d '{
    "userId": 1,
    "fridgeId": 5,
    "items": [
      {"productId": 301, "productName": "Buddha Bowl Vegano", "quantity": 1, "unitPrice": 8500.00},
      {"productId": 303, "productName": "Curry de Garbanzos", "quantity": 2, "unitPrice": 9200.00}
    ]
  }' | jq
```

#### C.5 Cancelar una orden

Se puede cancelar una orden si está en estado `PENDING` o `PAID`.
Si estaba `PAID`, se libera el stock automáticamente.

```bash
curl -s -X PUT 'http://localhost:8080/api/v1/ordenes/17/cancelar' \
  -H 'Content-Type: application/json' \
  -d '{"motivo": "Cambié de opinión"}' | jq
```

> El campo `motivo` es opcional. Si la orden está `PENDING`, no se necesita body.

---

### Flujo D: Planificación de Cocina (US-12, US-13)

#### D.1 Ver historial de ventas (US-13)

```bash
# Todas las ventas
curl -s 'http://localhost:8080/api/v1/cocina/historial-ventas' | jq

# Por rango de fechas (ajustar según fecha actual)
curl -s 'http://localhost:8080/api/v1/cocina/historial-ventas?from=2026-06-01&to=2026-06-22' | jq

# Por producto
curl -s 'http://localhost:8080/api/v1/cocina/historial-ventas?productId=101' | jq

# Por heladera
curl -s 'http://localhost:8080/api/v1/cocina/historial-ventas?fridgeId=1' | jq
```

#### D.2 Generar plan diario (US-12)

> Requiere que **order-service**, **product-service** y **fridge-service** estén corriendo.  
> El cálculo sugerido = `ceil(ventas_promedio_7días)` - `stock_remanente`.

```bash
# Generar plan para COCINA-DULCE
curl -s -X POST 'http://localhost:8080/api/v1/cocina/plan-diario?cocinaId=COCINA-DULCE&fecha=2026-06-23' | jq

# Consultar el plan generado
curl -s 'http://localhost:8080/api/v1/cocina/plan-diario?cocinaId=COCINA-DULCE&fecha=2026-06-23' | jq
```

IDs de cocina disponibles: `COCINA-DULCE`, `COCINA-CELIACA`, `COCINA-VEGANA`.

---

### Flujo E: Preferencias y Recomendaciones (US-14, US-15)

#### E.1 Registrar un nuevo usuario con preferencias (US-14)

```bash
curl -s -X POST 'http://localhost:8080/api/v1/usuarios/registrar' \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "password123",
    "dietaryPreferences": ["VEGANO", "SIN_GLUTEN"]
  }' | jq
```

Guardar el `id` devuelto para usarlo en recomendaciones.

Preferencias válidas: `VEGANO`, `SIN_GLUTEN`, `VEGETARIANO`, `DULCE`.

#### E.2 Obtener recomendaciones para un usuario (US-15)

```bash
# Usuario 1 (preferencias: vegano, sin gluten)
curl -s 'http://localhost:8080/api/v1/recomendaciones/1' | jq

# Usuario 2 (preferencias: vegetariano)
curl -s 'http://localhost:8080/api/v1/recomendaciones/2' | jq

# Usuario recién creado (sin historial de compras)
curl -s 'http://localhost:8080/api/v1/recomendaciones/11' | jq
```

Respuesta esperada: top 5 productos que coinciden con las preferencias del usuario, excluyendo los ya comprados, ordenados por popularidad.

> **Nota:** recommendation-service usa mocks en profile `dev` (default). Los datos de preferencias e historial son simulados localmente, no se consultan los otros servicios reales. Para usar datos reales, cambiar a `spring.profiles.active=!dev`.

---

### Flujo F: Notificaciones (US-16)

#### F.1 Suscribir un usuario para recibir notificaciones

```bash
curl -s -X POST 'http://localhost:8080/api/v1/notificaciones/suscribir' \
  -H 'Content-Type: application/json' \
  -d '{
    "userId": 1,
    "deviceToken": "device-token-001",
    "productPreferences": [101, 301],
    "heladeraIds": [1, 5]
  }' | jq
```

#### F.2 Ver suscripción de un usuario

```bash
curl -s 'http://localhost:8080/api/v1/notificaciones/suscribir/1' | jq
```

#### F.3 Notificar disponibilidad de producto

```bash
curl -s -X POST 'http://localhost:8080/api/v1/notificaciones/notificar-disponibilidad' \
  -H 'Content-Type: application/json' \
  -d '{
    "productId": 101,
    "productName": "Brownie de Chocolate",
    "fridgeId": 1,
    "heladeraName": "Heladera Palermo Dulce"
  }' | jq
```

#### F.4 Ver notificaciones de un usuario

```bash
curl -s 'http://localhost:8080/api/v1/notificaciones/usuario/1' | jq

# Solo no leídas
curl -s 'http://localhost:8080/api/v1/notificaciones/usuario/1/no-leidas' | jq
```

#### F.5 Marcar notificación como leída

```bash
curl -s -X PUT 'http://localhost:8080/api/v1/notificaciones/{id}/leer' | jq
```

---

### Flujo G: Endpoints administrativos varios

#### Obtener productos por cocina

```bash
curl -s 'http://localhost:8080/api/v1/productos/cocina/1' | jq
```

#### Stock remanente por cocina

```bash
curl -s 'http://localhost:8080/api/v1/heladeras/cocina/COCINA-DULCE/remanente' | jq
```

#### Ventas agregadas por cocina

```bash
curl -s 'http://localhost:8080/api/v1/ordenes/historial-ventas/cocina/COCINA-DULCE' | jq
```

#### Cancelar orden

Ver [C.5 Cancelar una orden](#c5-cancelar-una-orden) en el Flujo C.

#### Actualizar preferencias de usuario

```bash
curl -s -X PUT 'http://localhost:8080/api/v1/usuarios/1/preferencias' \
  -H 'Content-Type: application/json' \
  -d '{
    "dietaryPreferences": ["VEGANO", "SIN_GLUTEN", "VEGETARIANO"]
  }' | jq
```

---

### Resumen visual de datos mock por flujo

| Flujo | Productos | Heladeras | Usuarios | IDs clave |
|---|---|---|---|---|
| A: Catálogo | 101-303 | — | — | cocinaIds: 1, 2, 3 |
| B: Heladeras | 101-303 | 1-6 | — | lat=-34.5883, lng=-58.4223 |
| C: Órdenes | 101, 102, 301, 303 | 1, 3, 5 | 1, 2 | userId=1, fridgeId=1 |
| D: Cocina | 101-303 | — | — | COCINA-DULCE, COCINA-CELIACA, COCINA-VEGANA |
| E: Recomendaciones | — | — | 1, 2 | userId=1 (vegano), userId=2 (vegetariano) |
| F: Notificaciones | 101, 301 | 1, 5 | 1 | userId=1, productId=101 |

---