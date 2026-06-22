# Plan: Frontend/Foundation

## Phase 1: Setup & Dependencies

- [x] Task: Instalar dependencias
    - [x] `npm install axios`
    - [x] `npm install react-router-dom`
- [x] Task: Crear archivos de entorno
    - [x] Crear `.env` con `VITE_API_BASE_URL=http://localhost:8080/api/v1`, `VITE_USE_MOCK=true`, `VITE_APP_TITLE=FarmacyFood`
    - [x] Crear `.env.development` (sobreescribe para dev si aplica)
    - [x] Crear `.env.example` (mismas vars, valores placeholder)
    - [x] Agregar `.env` y `.env.development` a `.gitignore`
- [x] Task: Configurar proxy de Vite
    - [x] Leer `VITE_API_BASE_URL` y parsear con `new URL()`
    - [x] Usar `url.pathname` como clave del proxy y `url.origin` como target
    - [x] Verificar que NO se duplica el prefijo `/api/v1` en la URL destino
- [x] Task: Conductor - User Manual Verification 'Phase 1: Setup & Dependencies' (Protocol in workflow.md)

## Phase 2: Cliente HTTP (Axios)

- [x] Task: Crear instancia central de Axios
    - [x] Crear `src/lib/httpClient.js`
    - [x] Inicializar con `baseURL` desde `import.meta.env.VITE_API_BASE_URL`
    - [x] Exportar instancia como default
- [x] Task: Implementar interceptors
    - [x] Request interceptor: estructura preparada para inyectar token desde authStore (futuro); si no hay token, pasar sin modificar
    - [x] Response interceptor: capturar error 401 y dejar preparada la estructura para redirigir a login (sin implementar redirección real)
    - [x] En ambos interceptors, propagar el error con `Promise.reject`
- [x] Task: Conductor - User Manual Verification 'Phase 2: Cliente HTTP' (Protocol in workflow.md)

## Phase 3: Enrutamiento con react-router-dom

- [x] Task: Migrar App.jsx a BrowserRouter + Routes
    - [x] Envolver con `<BrowserRouter>`
    - [x] Definir `<Routes>` con `<Route>` para cada vista
    - [x] Rutas: `/` (Catálogo), `/heladeras`, `/ordenes`, `/cocina`, `/recomendaciones`, `/notificaciones`, `/login`
    - [x] Reemplazar lógica de `pantallaActiva` (useState) por navegación con Links
- [x] Task: Migrar navegación a NavLink
    - [x] Tabs mobile (bottom bar): reemplazar botones por `<NavLink>`
    - [x] Nav desktop (header): reemplazar botones por `<NavLink>`
    - [x] Asegurar que la tab activa se resalta con `isActive`
- [x] Task: Conductor - User Manual Verification 'Phase 3: Enrutamiento' (Protocol in workflow.md)

## Phase 4: Scaffolding de servicios por dominio

- [x] Task: Crear estructura de subcarpetas por dominio en `src/services/`
    - [x] Crear subcarpetas: `productos/`, `heladeras/`, `ordenes/`, `cocina/`, `recomendaciones/`, `notificaciones/`, `usuarios/`
    - [x] Mover services existentes a `src/services/<dominio>/mock.js` (conservando lógica actual)
    - [x] Cada dominio tendrá: `index.js` (selección mock/HTTP según env), `mock.js` (impl actual), `http.js` (skeleton Axios)
- [x] Task: Implementar skeletons HTTP por dominio
    - [x] `productos/http.js` — mismas funciones que mock.js, cada una retornando `httpClient.get/post/...` con rutas placeholder comentadas
    - [x] `heladeras/http.js`, `ordenes/http.js`, `cocina/http.js`, `recomendaciones/http.js`, `notificaciones/http.js`, `usuarios/http.js` — idéntico patrón
    - [x] No asumir endpoints definitivos ni implementar integración real
- [x] Task: Implementar index.js por dominio
    - [x] Cada `index.js` lee `import.meta.env.VITE_USE_MOCK`
    - [x] Si `'true'`, re-exporta desde `./mock.js`
    - [x] Si no, re-exporta desde `./http.js`
- [x] Task: Conductor - User Manual Verification 'Phase 4: Servicios por dominio' (Protocol in workflow.md)

## Phase 5: Zustand Stores

- [x] Task: Crear `cartStore`
    - [x] State: `items[]`, `total`, `itemCount`
    - [x] Actions: `addItem(product, quantity)`, `removeItem(productId)`, `updateQuantity(productId, qty)`, `clearCart()`
- [x] Task: Crear `uiStore`
    - [x] State: `sidebarOpen`, `globalLoading`, `filters` (categoría activa, query de búsqueda)
    - [x] Actions: `toggleSidebar()`, `setLoading(bool)`, `setFilter(key, value)`
- [x] Task: Refactorizar stores existentes
    - [x] Modificar imports para que consuman desde `src/services/<dominio>/index.js`
    - [x] Reemplazar acceso directo a JSON mock por llamadas al service correspondiente
    - [x] Mantener misma interfaz pública de cada store (componentes no se modifican)
    - [x] No asumir cantidad fija de stores existentes
- [x] Task: Conductor - User Manual Verification 'Phase 5: Zustand Stores' (Protocol in workflow.md)

## Phase 6: Verificación Final

- [x] Task: Verificar compilación y dev server
    - [x] `npm run build` exitoso (148 módulos, 2.92s)
    - [x] `npm run dev` arranca correctamente en localhost:5173
    - [x] Navegación entre tabs funciona con react-router
    - [x] Proxy de Vite configurado sin duplicación de prefijo
- [x] Task: Verificar modo HTTP (con Gateway opcional)
    - [x] Con `VITE_USE_MOCK=false`, los services skeletons están listos y referenciados
- [x] Task: Crear nota de resumen en `conductor/notes/`
- [x] Task: Conductor - User Manual Verification 'Phase 6: Verificación Final' (Protocol in workflow.md)
