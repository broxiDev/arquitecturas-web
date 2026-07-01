# kitchen-service

## Cómo probar `POST /api/v1/cocina`

El endpoint no recibe `usuarioId` por body: lo resuelve del usuario autenticado (header `X-User` que inyecta el api-gateway a partir del JWT). Por eso hay que pasar siempre por el gateway, no pegarle directo a kitchen-service.

Con todo el stack levantado vía Docker (`docker compose up -d --build` desde la raíz del repo), el gateway queda en `http://localhost:7080`.

### Flujo

**1. Registrar un usuario en auth-service, con un `username` que ya exista como `authUsername` en user-service** (el join se hace por ese valor). User-service tiene seed data con usuarios como `auth_juan`, `auth_caro`, `auth_nahuel`, etc.

```bash
curl -X POST http://localhost:7080/api/v1/auth/registrar \
  -H "Content-Type: application/json" \
  -d '{"username":"auth_juan","password":"test1234","rol":"cocina","name":"Juan Perez","email":"juan@test.com"}'
```
(`rol` solo acepta `cliente` o `cocina`.)

**2. Login → JWT**

```bash
curl -X POST http://localhost:7080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"auth_juan","password":"test1234"}'
```

**3. Crear la cocina (con el token del paso 2)**

```bash
curl -X POST http://localhost:7080/api/v1/cocina \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{"nombre": "La mejor cocina veggy de Rosi"}'
```

kitchen-service toma el `X-User` (`auth_juan`) del contexto de seguridad, le pregunta a user-service `GET /api/v1/usuarios/auth-username/auth_juan` para sacar el `id` numérico, y ese es el `usuarioId` que queda guardado — no se puede pasar por body.

Respuesta esperada (`201`):
```json
{"id":1,"nombre":"La mejor cocina veggy de Rosi","usuarioId":2,"createdAt":"2026-07-01T05:43:10.263655689"}
```
