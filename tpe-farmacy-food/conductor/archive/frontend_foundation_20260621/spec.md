# Frontend/Foundation

## Overview
Construir la base del frontend React para integrarse con los microservicios via API Gateway.
Este track configura la infraestructura (cliente HTTP, enrutamiento, variables de entorno,
arquitectura de servicios) sin implementar funcionalidad de negocio.

## Functional Requirements

### FR1 — Cliente HTTP (Axios)
- Agregar `axios` como dependencia.
- Instancia de Axios con base URL desde `VITE_API_BASE_URL`.
- Interceptors preparados para auth futura: estructura de request interceptor que
  inyecte token (pendiente de implementar), y response interceptor que capture 401
  (sin lógica de redirección real aún). No implementar JWT real en este track.

### FR2 — Enrutamiento (react-router-dom)
- Agregar `react-router-dom` como dependencia.
- Migrar `App.jsx` de tab switching con `useState` a `<Routes>` + `<BrowserRouter>`.
- Definir rutas:
  `/` → Catálogo  |  `/heladeras`  |  `/ordenes`  |  `/cocina`  |  `/recomendaciones`  |  `/notificaciones`  |  `/login`
- Mantener navegación mobile-first con tabs inferiores adaptada a rutas.

### FR3 — Servicios por dominio (Mock + HTTP)
- Reestructurar `src/services/` con subcarpetas por dominio:
  ```
  src/services/
  ├── productos/{index, mock, http}.js
  ├── heladeras/{index, mock, http}.js
  ├── ordenes/{index, mock, http}.js
  ├── cocina/{index, mock, http}.js
  ├── recomendaciones/{index, mock, http}.js
  ├── notificaciones/{index, mock, http}.js
  └── usuarios/{index, mock, http}.js
  ```
- Mock y HTTP exportan la **misma interfaz**.
- Cada dominio resuelve mock/HTTP en su propio `index.js` según `VITE_USE_MOCK`.
- No hay factory global único.

### FR4 — Variables de entorno
- Crear `.env`, `.env.development`, `.env.example`.
- Variables:
  - `VITE_API_BASE_URL=http://localhost:8080/api/v1`
  - `VITE_USE_MOCK=true`
  - `VITE_APP_TITLE=FarmacyFood`

### FR5 — Estado global con Zustand
- Crear `cartStore` (items del carrito) y `uiStore` (filtros globales, loading).
- Stores existentes se modifican para importar desde `src/services/<dominio>/index.js`.
- `authStore` queda fuera de este track.

### FR6 — Vite proxy para desarrollo
- Configurar proxy en `vite.config.js` parseando `VITE_API_BASE_URL` con `new URL()`,
  usando `url.pathname` como clave y `url.origin` como target, sin duplicar el prefijo.

## Non-Functional Requirements
- **NFR1:** Cada servicio se migra mock→real individualmente sin afectar otros.
- **NFR2:** Stores no contienen lógica HTTP; solo llaman a services.
- **NFR3:** No se implementan pantallas ni lógica de negocio nuevas.
- **NFR4:** Código, comentarios y mensajes en español.

## Acceptance Criteria
- [ ] Axios instalado con base URL e interceptors (JWT preparado, no implementado).
- [ ] react-router-dom instalado con rutas para todas las vistas.
- [ ] Tabs de navegación funcionan con react-router (mobile y desktop).
- [ ] `src/services/` reestructurado con subcarpetas por dominio, cada una con index/mock/http.
- [ ] `VITE_USE_MOCK=true` → mock data (comportamiento actual).
- [ ] `VITE_USE_MOCK=false` → HTTP skeletons placeholder.
- [ ] `.env`, `.env.development`, `.env.example` creados.
- [ ] Stores refactorizados para usar `src/services/<dominio>/index.js`.
- [ ] Vite proxy redirige `/api/v1/*` parseando `VITE_API_BASE_URL` sin duplicación.
- [ ] `cartStore` y `uiStore` creados (authStore en track separado).
- [ ] `npm run dev` funciona correctamente con mock data.

## Out of Scope
- Pantallas de login/registro reales (solo ruta `/login` definida).
- authStore e integración JWT.
- Lógica de negocio en stores o componentes.
- Integración real con microservicios backend.
- Tests unitarios/de integración.
