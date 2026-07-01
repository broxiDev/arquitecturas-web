# TODO: registrar en auth-service no crea el usuario en user-service

## Problema

`POST /api/v1/auth/registrar` (auth-service) da de alta un `AuthUser` (`username`, `password` hasheado, `rol`) y devuelve un JWT válido. Pero no crea nada en `user-service`. El `RegisterRequest` de auth-service ya recibe `name` y `email`, pero `AuthServiceImpl.register()` los descarta — ni los persiste en `auth_users` ni los usa para nada.

Consecuencia: cualquier operación que dependa de resolver el `authUsername` del token contra `user-service` (ej. `kitchen-service` creando una cocina, `order-service` resolviendo el usuario de una orden) falla con "usuario no encontrado", salvo que ese `authUsername` ya exista de casualidad en el seed de `user-service`.

## Causa raíz

No es un bug de lógica rota, es un paso de integración que nunca se conectó:

- `user-service` ya tiene el endpoint receptor: `POST /api/v1/usuarios/registrar`, con `UserRegistrationRequest(authUsername, name, email, dietaryPreferences?)`, que valida `email`/`authUsername` únicos y persiste el `User`. Funciona si se lo llama.
- `docs/auth-integration-guide.md` (raíz del repo, sección 4) documenta el diseño **original** como un flujo de 2 pasos a cargo del **cliente** (frontend/Postman): 1) `POST /api/v1/auth/registrar` → JWT, 2) con ese JWT, `POST /api/v1/usuarios/registrar` (segundo llamado explícito). Ese segundo paso nunca se integró en el frontend ni se documentó en los flujos de prueba manual, por eso nadie lo estaba haciendo y parece "roto".
- `auth-service` no tiene ninguna capacidad de llamar a otros servicios hoy (sin Feign, sin `RestTemplate`/`WebClient`, sin la dependencia en el `pom.xml`).

Hay que decidir explícitamente entre mantener el flujo de 2 pasos (y arreglarlo del lado del cliente) o pasar a un flujo atómico donde `auth-service` orquesta la creación en `user-service` en el mismo request de registro. Este documento asume la segunda opción (orquestación automática), que es la que se charló como fix real.

## Spec de la solución

### 1. Cambios en `auth-service` (contexto, no es el foco de este doc)

- Agregar `spring-cloud-starter-openfeign` al `pom.xml`.
- Crear un `UserServiceClient` (Feign) que llame a `POST /api/v1/usuarios/registrar`.
- En `AuthServiceImpl.register()`, después de guardar el `AuthUser` y antes de devolver la respuesta: llamar a `userServiceClient.registrar(new UserRegistrationRequest(username, name, email))`.
- `name`/`email` en `RegisterRequest` deberían pasar a ser `@NotBlank` (hoy son opcionales pero `user-service` los requiere `NOT NULL`).

### 2. Cambios necesarios en `user-service` (foco de este doc)

**2.1. Decisión de seguridad para el endpoint interno (el problema real a resolver acá)**

`user-service`'s `SecurityConfig` tiene `.anyRequest().authenticated()` sin excepciones — `POST /api/v1/usuarios/registrar` también la exige. Pero en el momento en que `auth-service` llama a este endpoint, el usuario recién se está creando: no hay un `X-User`/`X-Role` legítimo de ese usuario todavía (huevo-gallina, igual que ya se resolvió distinto para `/api/v1/auth/**` en auth-service, que sí está whitelisteado).

Dos caminos, elegir uno:

- **(a) Whitelistear la ruta por path**, igual que se hace con swagger:
  ```java
  .requestMatchers("/api/v1/usuarios/registrar").permitAll()
  ```
  Simple, pero deja ese endpoint público — cualquiera podría crear filas en `users` sin pasar por `auth-service`. Aceptable si el `authUsername` igual queda huérfano sin un `AuthUser` real detrás (no se puede loguear con él), pero ensucia la tabla.

- **(b) Secreto interno servicio-a-servicio** (recomendado): un header tipo `X-Internal-Secret` que solo conocen `auth-service` y `user-service` (via variable de entorno / config, no hardcodeado en el repo). Un filtro o `@PreAuthorize` chequea ese header en vez de `X-User`/`X-Role` para esta ruta puntual. Más prolijo, evita el endpoint abierto, pero es más para implementar (un filtro nuevo + config compartida entre dos servicios).

**2.2. Contrato (ya existe, no requiere cambios de forma)**

```
POST /api/v1/usuarios/registrar
Body: { "authUsername": "auth_juan", "name": "Juan Perez", "email": "juan@test.com" }
201 → UserResponse { id, name, email, dietaryPreferences, createdAt }
400 → si authUsername o email ya existen
```

**2.3. Idempotencia / manejo de fallos**

Si `auth-service` ya creó el `AuthUser` y la llamada a `user-service` falla (timeout, 400 por email duplicado, etc.), hoy no hay transacción distribuida ni compensación — quedaría el mismo bug de hoy pero silencioso. Definir:

- Mínimo viable: si falla la llamada a `user-service`, loguear el error pero no revertir el `AuthUser` ni fallar el registro (el login sigue funcionando; el usuario queda "huérfano" en `user-service` hasta que se resuelva a mano o se reintente).
- Más prolijo: exponer un endpoint idempotente en `user-service` (si el `authUsername` ya existe, devolver 200 con el existente en vez de 400) para poder reintentar el alta sin duplicar, útil si más adelante se agrega un reintento automático o un job de reconciliación.

### 3. Fuera de alcance de este doc

- Reemplazar esto por un patrón async (outbox/eventos) — no hay infraestructura de mensajería en el repo hoy, sería un cambio de arquitectura mucho más grande que este bug puntual.
- Tocar `order-service`/`kitchen-service` — ellos ya consumen `GET /api/v1/usuarios/auth-username/{authUsername}` correctamente, el problema es exclusivamente que no hay usuario para encontrar.
