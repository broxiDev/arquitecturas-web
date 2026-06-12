import { useState } from 'react'
import Catalogo from './features/catalogo/Catalogo'
import Heladeras from './features/heladeras/Heladeras'
import Ordenes from './features/ordenes/Ordenes'

const PANTALLAS = [
  { key: 'catalogo', label: 'Catalogo', icon: '🥗', Component: Catalogo },
  { key: 'heladeras', label: 'Heladeras', icon: '🧊', Component: Heladeras },
  { key: 'ordenes', label: 'Ordenes', icon: '🛒', Component: Ordenes },
]

function App() {
  const [pantallaActiva, setPantallaActiva] = useState('catalogo')

  const PantallaActiva = PANTALLAS.find(p => p.key === pantallaActiva)?.Component || Catalogo

  return (
    <div className="min-h-screen bg-gray-50 pb-20 md:pb-0">
      <header className="bg-white shadow-sm border-b border-green-100">
        <div className="max-w-3xl mx-auto px-4 py-3 md:py-4">
          <h1 className="text-xl md:text-2xl font-bold text-green-700">FarmacyFood</h1>
          <nav className="hidden md:flex gap-2 mt-3">
            {PANTALLAS.map(({ key, label, icon }) => (
              <button
                key={key}
                onClick={() => setPantallaActiva(key)}
                className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                  pantallaActiva === key
                    ? 'bg-green-600 text-white'
                    : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                }`}
              >
                <span className="mr-1.5">{icon}</span>
                {label}
              </button>
            ))}
          </nav>
        </div>
      </header>

      <main className="max-w-3xl mx-auto px-3 md:px-4 py-4 md:py-6">
        <PantallaActiva />
      </main>

      <nav className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 md:hidden z-10">
        <div className="flex justify-around py-2">
          {PANTALLAS.map(({ key, label, icon }) => (
            <button
              key={key}
              onClick={() => setPantallaActiva(key)}
              className={`flex flex-col items-center gap-0.5 px-3 py-1 text-xs font-medium transition-colors ${
                pantallaActiva === key
                  ? 'text-green-600'
                  : 'text-gray-400'
              }`}
            >
              <span className="text-lg">{icon}</span>
              <span>{label}</span>
            </button>
          ))}
        </div>
      </nav>
    </div>
  )
}

export default App