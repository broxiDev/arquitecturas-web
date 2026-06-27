# Guía de Consumo — Audit Service

## Índice

1. [Introducción](#1-introducción)
2. [Endpoints disponibles](#2-endpoints-disponibles)
3. [Setup del cliente en tu servicio](#3-setup-del-cliente-en-tu-servicio)
4. [Ejemplo práctico: order-service audita una creación de orden](#4-ejemplo-práctico-order-service-audita-una-creación-de-orden)
5. [Buenas prácticas](#5-buenas-prácticas)

---

## 1. Introducción

El **Audit Service** (`audit-service`, puerto `8088`) permite a cualquier microservicio del ecosistema FarmacyFood registrar eventos de auditoría con un patrón **fire-and-forget**: cada servicio envía el **request** que recibió y el **response** que devolvió, y el audit-service lo persiste sin esperar confirmación adicional.

Los eventos se almacenan en **PostgreSQL** (`audit_db`, puerto `5438`) y se accede al endpoint via **API Gateway** (`http://localhost:8080/api/v1/auditoria/eventos`).

```
┌──────────────┐   POST /api/v1/auditoria/eventos (fire & forget)   ┌───────────────┐
│ order-service │ ────────────────────────────────────────────────>  │ audit-service  │
│  (Feign)      │                                                   │  (PostgreSQL)  │
└──────────────┘                                                   └───────────────┘
```

---

## 2. Endpoints disponibles

### 2.1 Registrar un evento (único endpoint)

```
POST /api/v1/auditoria/eventos
```

```json
{
  "serviceName": "ORDER-SERVICE",
  "request": "{\"method\":\"POST\",\"path\":\"/api/v1/ordenes\",\"body\":{\"userId\":3,\"items\":[{\"productId\":1,\"quantity\":2}]}}",
  "response": "{\"status\":201,\"body\":{\"orderId\":1,\"total\":8200.00,\"status\":\"CREATED\"}}"
}
```

| Campo | Tipo | Obligatorio | Descripción |
|---|---|---|---|
| `serviceName` | string | **sí** | Nombre del servicio que llama (ej: `ORDER-SERVICE`, `FRIDGE-SERVICE`) |
| `request` | string | no | JSON string del request recibido por el servicio |
| `response` | string | no | JSON string del response devuelto por el servicio |
| `timestamp` | string | no | ISO-8601. Si se omite, se usa la hora del servidor |

> **Fire-and-forget**: el servicio llama a este endpoint de forma asíncrona y no bloquea su flujo principal ante una eventual falla del audit-service.

---

## 3. Setup del cliente en tu servicio

Crea **3 archivos** dentro de `src/main/java/com/farmacyfood/<tuservicio>/client/`:

### 3.1 Interface del contrato

**`AuditClient.java`**

```java
package com.farmacyfood.<tuservicio>.client;

public interface AuditClient {
    void registrarEvento(String serviceName, String request, String response);
}
```

### 3.2 DTO del request

**`AuditEventRequest.java`**

```java
package com.farmacyfood.<tuservicio>.client;

public record AuditEventRequest(
        String serviceName,
        String request,
        String response
) {}
```

### 3.3 Implementación real (Feign) — activa con perfil `!dev`

**`AuditClientFeign.java`**

```java
package com.farmacyfood.<tuservicio>.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "audit-service", url = "${clients.audit-service.url:http://localhost:8088}")
@Profile("!dev")
public interface AuditClientFeign extends AuditClient {

    @Override
    @PostMapping("/api/v1/auditoria/eventos")
    void registrarEvento(@RequestBody AuditEventRequest request);
}
```

### 3.4 Mock para desarrollo — activa con perfil `dev`

**`AuditClientMockImpl.java`**

```java
package com.farmacyfood.<tuservicio>.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev")
public class AuditClientMockImpl implements AuditClient {

    @Override
    public void registrarEvento(String serviceName, String request, String response) {
        log.info("[MOCK AUDIT] service={} | request={} | response={}", serviceName, request, response);
    }
}
```

---

## 4. Ejemplo práctico: order-service audita una creación de orden

### 4.1 En `OrderServiceImpl.java`, inyectá el `AuditClient`:

```java
@Service
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService {

    // ... otros campos ...

    @Autowired(required = false)
    private AuditClient auditClient;

    @Override
    public OrderResponseDTO crearOrden(OrderCreateDTO dto) {
        String requestJson = String.format(
            "{\"method\":\"POST\",\"path\":\"/api/v1/ordenes\",\"body\":%s}",
            convertirACadenaJson(dto)
        );

        // ... lógica de creación de orden ...
        OrderResponseDTO response = orderRepository.save(order);

        // -- Registrar evento de auditoría (fire-and-forget) --
        String responseJson = String.format(
            "{\"status\":201,\"body\":{\"orderId\":%d,\"total\":%.2f,\"status\":\"CREATED\"}}",
            response.orderId(), response.total()
        );

        if (auditClient != null) {
            auditClient.registrarEvento("ORDER-SERVICE", requestJson, responseJson);
        }

        return response;
    }
}
```

### 4.2 Si querés que Feign esté disponible en tu servicio, agregá en su `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

### 4.3 Configuración de `@EnableFeignClients`

Si usás `@EnableFeignClients`, asegurate de que escanee el paquete del cliente:

```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.farmacyfood.order.client")
public class OrderServiceApplication { ... }
```

Si el Feign Client de audit-service está en un paquete diferente, Spring no lo encontrará. **Solución simple**: en vez de usar Feign discovery automático, definí la URL explícitamente como en el ejemplo del Feign Client arriba.

---

## 5. Buenas prácticas

### 5.1 ¿Cuándo auditar?

| Acción | ¿Auditar? | Ejemplo |
|---|---|---|
| Creación de entidad | **Sí** | `request` / `response` completos |
| Actualización de datos sensibles | **Sí** | Enviar ambos payloads |
| Eliminación | **Sí** | Registrar qué se eliminó |
| Cambio de estado crítico | **Sí** | Incluir estado anterior y nuevo |
| Lecturas (GET) | **No** | — |
| Consultas internas | **No** | — |

### 5.2 Fire-and-forget real

El llamado al audit-service debe ejecutarse de forma asíncrona para no afectar la latencia del servicio principal:

```java
// Opción 1: @Async
@Async
public void auditar(String serviceName, String request, String response) {
    auditClient.registrarEvento("ORDER-SERVICE", request, response);
}

// Opción 2: ExecutorService propio
executor.submit(() -> auditClient.registrarEvento("ORDER-SERVICE", request, response));
```

### 5.3 `required = false` en la inyección

Usá `@Autowired(required = false)` para que el servicio funcione incluso si el `AuditClient` no está configurado (ej: durante tests unitarios).

### 5.4 Serialización de `request` y `response`

Los campos `request` y `response` son strings TEXT. Enviá siempre un **JSON string válido** con los datos relevantes del payload, no los objetos serializados completos si contienen información innecesaria.

### 5.5 No auditar en cascada

Si un servicio A llama a B y B ya audita su propia acción, **no audites la misma acción desde A**. Cada servicio es responsable de auditar sus propias operaciones.
