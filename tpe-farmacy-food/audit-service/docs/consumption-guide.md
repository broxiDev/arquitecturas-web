# Guía de Consumo — Audit Service

## Índice

1. [Introducción](#1-introducción)
2. [Arquitectura](#2-arquitectura)
3. [Setup rápido (3 pasos)](#3-setup-rápido-3-pasos)
4. [Uso del AuditLogger](#4-uso-del-auditlogger)
5. [Constantes de mensajes](#5-constantes-de-mensajes)
6. [Estructura del evento](#6-estructura-del-evento)
7. [Buenas prácticas](#7-buenas-prácticas)

---

## 1. Introducción

El **Audit Service** (`audit-service`, puerto `8088`) permite a cualquier microservicio del ecosistema FarmacyFood registrar eventos de auditoría con un patrón **fire-and-forget**.

Cada servicio utiliza la librería compartida **`audit-client`** que provee:
- `AuditLogger` — helper con métodos `success()`, `error()`, `info()`, `pending()`
- `AuditClientFeign` — implementación Feign real (perfiles no-dev)
- `AuditClientMockImpl` — mock para desarrollo (perfil `dev`)

---

## 2. Arquitectura

```
┌───────────────┐             POST /api/v1/auditoria/eventos (fire & forget)         ┌───────────────┐
│ order-service │ ─── usa AuditLogger ──► audit-client ──► Feign ──────────────────► │ audit-service  │
│ user-service  │ ─── usa AuditLogger ──► audit-client ──► Feign ──────────────────► │  (PostgreSQL)  │
│ auth-service  │ ─── usa AuditLogger ──► audit-client ──► Feign ──────────────────► └───────────────┘
│ ...           │                                                                     
└───────────────┘
       ▲
       │
  audit-client (librería compartida)
  ┌────────────────────────────────────┐
  │ AuditLogger.java                   │
  │ AuditClient.java (interface)       │
  │ AuditClientFeign.java (Feign)      │
  │ AuditClientMockImpl.java (mock)    │
  │ AuditEventRequestDTO.java          │
  │ EventType.java (enum)              │
  └────────────────────────────────────┘
```

---

## 3. Setup rápido (3 pasos)

### Paso 1: Agregar dependencias

En tu `pom.xml`:

```xml
<!-- Si no tenés OpenFeign aún -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>

<!-- Librería compartida de auditoría -->
<dependency>
    <groupId>com.farmacyfood</groupId>
    <artifactId>audit-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Paso 2: Habilitar Feign Clients

En tu clase `@SpringBootApplication`, agregá:

```java
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.farmacyfood.audit.client")
public class MyServiceApplication { ... }
```

### Paso 3: Configurar el nombre del servicio

En tu `application.yml`:

```yaml
audit:
  service-name: MI-SERVICIO   # Reemplazá con el nombre de tu servicio
```

---

## 4. Uso del AuditLogger

### 4.1 Inyección

```java
import com.farmacyfood.audit.client.AuditLogger;

@Service
public class MiServiceImpl implements MiService {

    private final AuditLogger auditLogger;

    public MiServiceImpl(AuditLogger auditLogger) {
        this.auditLogger = auditLogger;
    }
    // ...
}
```

Si usás `@Autowired` por campo:

```java
@Autowired
private AuditLogger auditLogger;
```

### 4.2 Métodos disponibles

| Método | Tipo de evento | Uso típico |
|--------|---------------|------------|
| `auditLogger.success(action, message, detail)` | `SUCCESS` | Operación exitosa |
| `auditLogger.error(action, message, detail)` | `ERROR` | Operación fallida |
| `auditLogger.info(action, message, detail)` | `INFO` | Evento informativo |
| `auditLogger.pending(action, message, detail)` | `PENDING` | Operación en proceso |

### 4.3 Parámetros

| Parámetro | Tipo | Descripción | Ejemplo |
|-----------|------|-------------|---------|
| `action` | `String` | Nombre de la acción | `"CREATE_ORDER"` |
| `message` | `String` | Texto legible del evento | `"Orden creada exitosamente"` |
| `detail` | `Object` | Detalle adicional (se serializa a JSON). Si es String se usa literal. | DTO, Map, String |

### 4.4 Ejemplo completo

```java
public OrderResponseDTO crearOrden(OrderCreateDTO dto) {
    try {
        // Lógica de negocio...
        OrderResponseDTO response = orderRepository.save(order);
        
        auditLogger.success("CREATE_ORDER", AuditMessages.ORDER_CREATED, response);
        return response;
    } catch (OutOfStockException e) {
        auditLogger.error("CREATE_ORDER", AuditMessages.INSUFFICIENT_STOCK, e.getMessage());
        throw e;
    } catch (Exception e) {
        auditLogger.error("CREATE_ORDER", "Error inesperado", e.getMessage());
        throw e;
    }
}
```

---

## 5. Constantes de mensajes

Cada servicio define su propia clase `AuditMessages` en `com.farmacyfood.<servicio>.constants.AuditMessages`:

```java
package com.farmacyfood.miservicio.constants;

public final class AuditMessages {
    private AuditMessages() {}

    public static final String ENTITY_CREATED = "Entidad creada exitosamente";
    public static final String ENTITY_UPDATED = "Entidad actualizada exitosamente";
    public static final String ENTITY_DELETED = "Entidad eliminada exitosamente";
    public static final String ENTITY_NOT_FOUND = "Entidad no encontrada";
    // ...
}
```

**Convención:** los nombres de las constantes van en `MAYUSCULAS_CON_SNAKE_CASE` y sus valores son frases en español.

---

## 6. Estructura del evento

Cuando se registra un evento, el audit-service lo persiste con esta estructura:

```json
{
  "id": 1,
  "serviceName": "ORDER-SERVICE",
  "eventType": "SUCCESS",
  "action": "CREATE_ORDER",
  "message": "Orden creada exitosamente",
  "request": "{\"method\":\"POST\",\"path\":\"/api/v1/ordenes\",...}",
  "response": "{\"orderId\":1,\"total\":8200.00,\"status\":\"CREATED\"}",
  "timestamp": "2026-07-01T12:00:00"
}
```

| Campo | Descripción |
|-------|-------------|
| `serviceName` | Nombre del servicio que originó el evento |
| `eventType` | `SUCCESS`, `ERROR`, `PENDING` o `INFO` |
| `action` | Acción concreta (ej: `CREATE_ORDER`) |
| `message` | Texto legible del evento |
| `request` | JSON original de la solicitud (cuando aplica) |
| `response` | JSON del detalle enviado vía `AuditLogger` |
| `timestamp` | Fecha/hora del evento |

---

## 7. Buenas prácticas

### 7.1 ¿Cuándo auditar?

| Acción | ¿Auditar? | Tipo |
|--------|-----------|------|
| Creación de entidad | **Sí** | `SUCCESS` / `ERROR` |
| Actualización de datos sensibles | **Sí** | `SUCCESS` / `ERROR` |
| Eliminación | **Sí** | `SUCCESS` / `ERROR` |
| Cambio de estado crítico | **Sí** | `SUCCESS` / `ERROR` |
| Lecturas (GET) | **No** | — |
| Consultas internas | **No** | — |
| Procesos batch/programados | **Sí** | `INFO` / `ERROR` |

### 7.2 Fire-and-forget

El `AuditLogger` ya maneja el patrón fire-and-forget automáticamente: envuelve la llamada Feign en un `try-catch` y solo loguea un warning si falla. **No bloquea el flujo principal.**

### 7.3 No auditar en cascada

Si el servicio A llama al servicio B, y B ya audita su propia acción, **no audites la misma acción desde A**. Cada servicio es responsable de auditar sus propias operaciones.

### 7.4 El detail se serializa a JSON

El parámetro `detail` de `AuditLogger` se serializa automáticamente con Jackson. Si pasás un String, se usa literal. Si pasás un objeto/DTO/Map, se convierte a JSON.

### 7.5 Perfiles

- **Perfil `dev`**: los eventos solo se loguean por consola (`AuditClientMockImpl`)
- **Perfil `!dev`** (prod, test, etc.): los eventos se envían realmente al audit-service vía Feign
