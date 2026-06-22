# Frontend/Foundation — Resumen

**Track:** `frontend_foundation_20260621`
**Fecha:** 2026-06-21
**Estado:** Implementado

## Cambios Realizados

### Phase 1: Setup & Dependencies
- Instalados `axios` y `react-router-dom`
- Creados `.env`, `.env.development`, `.env.example` con `VITE_API_BASE_URL`, `VITE_USE_MOCK`, `VITE_APP_TITLE`
- `.env` y `.env.development` agregados a `.gitignore`
- Vite proxy configurado parseando `VITE_API_BASE_URL` con `new URL()`: `url.pathname` como clave, `url.origin` como target (sin duplicación de prefijo)

### Phase 2: Cliente HTTP (Axios)
- Creado `src/lib/httpClient.js` con instancia Axios y `baseURL` desde `VITE_API_BASE_URL`
- Request interceptor preparado para token JWT futuro (pasa sin modificar si no hay token)
- Response interceptor captura 401 (estructura lista, sin redirección real)

### Phase 3: Enrutamiento
- App migrada de `useState` tab switching a `<BrowserRouter>` + `<Routes>` + `<NavLink>`
- 7 rutas: `/`, `/heladeras`, `/ordenes`, `/cocina`, `/recomendaciones`, `/notificaciones`, `/login`
- `onNavigateToCatalogo` preservado mediante `useNavigate`
- Tabs mobile y nav desktop con `<NavLink>` y `isActive`

### Phase 4: Servicios por dominio
- Creadas 7 subcarpetas con `index.js`, `mock.js`, `http.js`:
  `productos/`, `heladeras/`, `ordenes/`, `cocina/`, `recomendaciones/`, `notificaciones/`, `usuarios/`
- Services existentes movidos a `mock.js` con imports actualizados
- `http.js` skeletons creados con `httpClient` y rutas placeholder (sin asumir endpoints definitivos)
- Cada dominio resuelve mock/HTTP en su propio `index.js` según `VITE_USE_MOCK`
- Sin factory global

### Phase 5: Zustand Stores
- Creado `cartStore`: items, total, itemCount + addItem, removeItem, updateQuantity, clearCart
- Creado `uiStore`: sidebarOpen, globalLoading, filters + toggleSidebar, setLoading, setFilter
- Refactorizados 6 stores existentes para importar desde `src/services/<dominio>/index.js`
- `authStore` queda fuera de este track

### Phase 6: Verificación
- Build exitoso con `npm run build` (148 módulos, 2.92s)
- Dev server arranca correctamente

## Decisiones Técnicas
- Se usó `axios` por simplicidad y documentación disponible (proyecto universitario)
- `react-router-dom` por ser estándar de la industria
- Cada dominio resuelve mock/HTTP internamente en su propio `index.js` (sin factory global)
- Proxy de Vite parsea `VITE_API_BASE_URL` con `new URL()` para evitar duplicación de prefijo

## Próximos Pasos
- Frontend/Auth (authStore, login real, JWT)
- Integración real con microservicios backend
