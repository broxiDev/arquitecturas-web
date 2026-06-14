import { useState } from 'react'
import productsData from '../../mocks/products.json'
import useNotificationStore from './notificationStore'

const USUARIOS = [
  { id: 'u001', name: 'Maria Gonzalez' },
  { id: 'u002', name: 'Juan Perez' },
  { id: 'u003', name: 'Carolina Ruiz' },
]

function SubscriptionForm() {
  const { subscribe, loading, error } = useNotificationStore()
  const [userId, setUserId] = useState('')
  const [selectedProducts, setSelectedProducts] = useState([])
  const [success, setSuccess] = useState(false)

  const toggleProduct = (productId) => {
    setSelectedProducts((prev) =>
      prev.includes(productId) ? prev.filter((id) => id !== productId) : [...prev, productId]
    )
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!userId || selectedProducts.length === 0) return
    await subscribe(userId, {
      deviceToken: 'mock_device_token_' + userId,
      productPreferences: selectedProducts,
    })
    setSuccess(true)
    setSelectedProducts([])
    setTimeout(() => setSuccess(false), 3000)
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Usuario</label>
        <select
          value={userId}
          onChange={(e) => setUserId(e.target.value)}
          className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-green-500 focus:border-green-500"
        >
          <option value="">Seleccionar usuario</option>
          {USUARIOS.map((u) => (
            <option key={u.id} value={u.id}>{u.name}</option>
          ))}
        </select>
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Productos favoritos</label>
        <p className="text-xs text-gray-400 mb-2">Selecciona los productos para recibir notificaciones</p>
        <div className="max-h-48 overflow-y-auto space-y-1.5">
          {productsData.map((product) => (
            <label key={product.id} className="flex items-center gap-2 p-2 rounded-lg hover:bg-gray-50 cursor-pointer">
              <input
                type="checkbox"
                checked={selectedProducts.includes(product.id)}
                onChange={() => toggleProduct(product.id)}
                className="rounded border-gray-300 text-green-600 focus:ring-green-500"
              />
              <span className="text-sm text-gray-800">{product.name}</span>
              <span className="text-xs text-gray-400 ml-auto">${product.price.toLocaleString('es-AR')}</span>
            </label>
          ))}
        </div>
      </div>

      {error && <div className="text-sm text-red-500 bg-red-50 rounded-lg p-2">{error}</div>}
      {success && <div className="text-sm text-green-700 bg-green-50 rounded-lg p-2">Suscripcion creada exitosamente!</div>}

      <button
        type="submit"
        disabled={!userId || selectedProducts.length === 0 || loading}
        className="w-full bg-green-600 text-white py-2.5 rounded-lg text-sm font-medium hover:bg-green-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
      >
        {loading ? 'Suscribiendo...' : 'Suscribirse'}
      </button>
    </form>
  )
}

export default SubscriptionForm