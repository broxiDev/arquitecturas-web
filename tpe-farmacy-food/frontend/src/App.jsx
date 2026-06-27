import { BrowserRouter, Routes, Route, NavLink, useNavigate } from 'react-router-dom'
import Catalogo from './features/catalogo/Catalogo'
import Heladeras from './features/heladeras/Heladeras'
import Ordenes from './features/ordenes/Ordenes'
import Cocina from './features/cocina/Cocina'
import Recomendaciones from './features/recomendaciones/Recomendaciones'
import Notificaciones from './features/notificaciones/Notificaciones'
import useNotificationStore from './features/notificaciones/notificationStore'
import logo from './assets/icons/farmacyfoodIcon.png'
import { useState } from 'react'


const PANTALLAS = [
  { path: '/', label: 'Catálogo', Component: Catalogo },
  { path: '/heladeras', label: 'Heladeras', Component: Heladeras },
  { path: '/órdenes', label: 'Órdenes', Component: Ordenes },
  { path: '/cocina', label: 'Cocina', Component: Cocina },
  { path: '/recomendaciones', label: 'Recom.', Component: Recomendaciones },
  { path: '/notificaciones', label: 'Notif.', Component: Notificaciones },
]

function AppContent() {
  const unreadCount = useNotificationStore((s) => s.unreadCount)
  const navigate = useNavigate()
    const [menuOpen, setMenuOpen] = useState(false)
    const handleNavigateToCatalogo = () => {
    navigate('/')
  }

  return (
    <div className="min-h-screen bg-gray-50 pb-16 md:pb-0">
      <header className="bg-green-900 shadow-sm border-b border-green-100">

        <div className="flex flex-row justify-between max-w-3xl mx-auto py-3 md:py-4">
            <img src={logo} alt="FarmacyFood" className="h-12 md:h-20"  />
            <div className="flex items-center justify-between">



          <nav className="hidden md:flex gap-2 flex-wrap">
            {PANTALLAS.map(({ path, label, icon }) => (
              <NavLink
                key={path}
                to={path}
                end={path === '/'}
                className={({ isActive }) =>
                    `px-3 py-2 text-sm font-medium transition-colors relative ${
                        isActive
                            ? 'text-white border-b-2 border-white'
                            : 'text-white/80 hover:text-white'
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
            {/* Hamburger button */}
            <button
                onClick={() => setMenuOpen(!menuOpen)}
                className="md:hidden p-2 text-white"
            >
                {menuOpen ? (
                    <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                    </svg>
                ) : (
                    <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
                    </svg>
                )}
            </button>

          {/* Mobile dropdown */}
          {menuOpen && (
              <div className="md:hidden mt-3 border-t border-green-700 pt-3 space-y-1">
                  {PANTALLAS.map(({ path, label, icon }) => (
                      <NavLink
                          key={path}
                          to={path}
                          end={path === '/'}
                          onClick={() => setMenuOpen(false)}
                          className={({ isActive }) =>
                              `block px-3 py-2 rounded text-sm font-medium ${
                                  isActive ? 'text-white border-l-2 border-white' : 'text-white/80 hover:text-white'
                              }`
                          }
                      >
                          {icon} {label}
                      </NavLink>
                  ))}
              </div>
          )}
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
