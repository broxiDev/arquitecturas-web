import { BrowserRouter, Routes, Route, NavLink, useNavigate } from 'react-router-dom'
import Catalogo from './features/catalogo/Catalogo'
import Heladeras from './features/heladeras/Heladeras'
import Ordenes from './features/ordenes/Ordenes'
import Cocina from './features/cocina/Cocina'
import Recomendaciones from './features/recomendaciones/Recomendaciones'
import Notificaciones from './features/notificaciones/Notificaciones'
import useNotificationStore from './features/notificaciones/notificationStore'

const PANTALLAS = [
  { path: '/', label: 'Catalogo', icon: '\u{1F96D}', Component: Catalogo },
  { path: '/heladeras', label: 'Heladeras', icon: '\u{1F9CA}', Component: Heladeras },
  { path: '/ordenes', label: 'Ordenes', icon: '\u{1F6D2}', Component: Ordenes },
  { path: '/cocina', label: 'Cocina', icon: '\u{1F525}', Component: Cocina },
  { path: '/recomendaciones', label: 'Recom.', icon: '\u{2B50}', Component: Recomendaciones },
  { path: '/notificaciones', label: 'Notif.', icon: '\u{1F514}', Component: Notificaciones },
]

function AppContent() {
  const unreadCount = useNotificationStore((s) => s.unreadCount)
  const navigate = useNavigate()

  const handleNavigateToCatalogo = () => {
    navigate('/')
  }

  return (
    <div className="min-h-screen bg-gray-50 pb-16 md:pb-0">
      <header className="bg-white shadow-sm border-b border-green-100">
        <div className="max-w-3xl mx-auto px-4 py-3 md:py-4">
          <h1 className="text-xl md:text-2xl font-bold text-green-700">FarmacyFood</h1>
          <nav className="hidden md:flex gap-2 mt-3 flex-wrap">
            {PANTALLAS.map(({ path, label, icon }) => (
              <NavLink
                key={path}
                to={path}
                end={path === '/'}
                className={({ isActive }) =>
                  `px-3 py-2 rounded-lg text-sm font-medium transition-colors relative ${
                    isActive
                      ? 'bg-green-600 text-white'
                      : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                  }`
                }
              >
                <span className="mr-1">{icon}</span>
                {label}
                {path === '/notificaciones' && unreadCount > 0 && (
                  <span className="absolute -top-1 -right-1 inline-flex items-center justify-center bg-red-500 text-white text-xs font-bold rounded-full w-4 h-4">
                    {unreadCount}
                  </span>
                )}
              </NavLink>
            ))}
          </nav>
        </div>
      </header>

      <main className="max-w-3xl mx-auto px-3 md:px-4 py-4 md:py-6">
        <Routes>
          {PANTALLAS.map(({ path, Component }) => (
            <Route
              key={path}
              path={path}
              element={path === '/recomendaciones'
                ? <Recomendaciones onNavigateToCatalogo={handleNavigateToCatalogo} />
                : <Component />
              }
            />
          ))}
          <Route path="/login" element={<div className="text-center text-gray-500 py-12">Login (proximamente)</div>} />
        </Routes>
      </main>

      <nav className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 md:hidden z-10">
        <div className="flex justify-around py-1.5">
          {PANTALLAS.map(({ path, label, icon }) => (
            <NavLink
              key={path}
              to={path}
              end={path === '/'}
              className={({ isActive }) =>
                `flex flex-col items-center gap-0.5 px-2 py-1 text-xs font-medium transition-colors relative ${
                  isActive ? 'text-green-600' : 'text-gray-400'
                }`
              }
            >
              <span className="text-base">{icon}</span>
              <span className="text-[10px] leading-tight">{label}</span>
              {path === '/notificaciones' && unreadCount > 0 && (
                <span className="absolute -top-0.5 -right-0.5 inline-flex items-center justify-center bg-red-500 text-white text-[8px] font-bold rounded-full w-3.5 h-3.5">
                  {unreadCount > 9 ? '9+' : unreadCount}
                </span>
              )}
            </NavLink>
          ))}
        </div>
      </nav>
    </div>
  )
}

function App() {
  return (
    <BrowserRouter>
      <AppContent />
    </BrowserRouter>
  )
}

export default App
