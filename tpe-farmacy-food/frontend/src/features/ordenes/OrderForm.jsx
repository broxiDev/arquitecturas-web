import { useState } from 'react'
import useOrderStore from './orderStore'
import useProductStore from '../catalogo/productStore'
import useFridgeStore from '../heladeras/fridgeStore'
import userService from '../../services/userService'
import productsData from '../../mocks/products.json'
import fridgesData from '../../mocks/fridges.json'

function OrderForm({ onCancel }) {
  const { createOrder, loading, error } = useOrderStore()
  const { fetchOrders } = useOrderStore.getState()

  const [usuarioId, setUsuarioId] = useState('')
  const [heladeraId, setHeladeraId] = useState('')
  const [items, setItems] = useState([])
  const [productoSeleccionado, setProductoSeleccionado] = useState('')
  const [cantidad, setCantidad] = useState(1)

  const usuarios = [
    { id: 'u001', name: 'Maria Gonzalez' },
    { id: 'u002', name: 'Juan Perez' },
    { id: 'u003', name: 'Carolina Ruiz' },
  ]

  const heladerasActivas = fridgesData.filter((f) => f.status === 'active')

  const agregarItem = () => {
    if (!productoSeleccionado || cantidad < 1) return
    const producto = productsData.find((p) => p.id === productoSeleccionado)
    if (!producto) return
    const existente = items.find((i) => i.productId === producto.id)
    if (existente) {
      setItems(items.map((i) =>
        i.productId === producto.id ? { ...i, quantity: i.quantity + cantidad } : i
      ))
    } else {
      setItems([...items, { productId: producto.id, productName: producto.name, quantity: cantidad, unitPrice: producto.price }])
    }
    setProductoSeleccionado('')
    setCantidad(1)
  }

  const eliminarItem = (productId) => {
    setItems(items.filter((i) => i.productId !== productId))
  }

  const total = items.reduce((sum, i) => sum + i.unitPrice * i.quantity, 0)

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!usuarioId || !heladeraId || items.length === 0) return
    await createOrder({
      userId: usuarioId,
      fridgeId: heladeraId,
      items: items.map(({ productId, productName, quantity, unitPrice }) => ({
        productId, productName, quantity, unitPrice,
      })),
      total,
    })
    fetchOrders()
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Usuario</label>
          <select
            value={usuarioId}
            onChange={(e) => setUsuarioId(e.target.value)}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-green-500 focus:border-green-500"
          >
            <option value="">Seleccionar usuario</option>
            {usuarios.map((u) => (
              <option key={u.id} value={u.id}>{u.name}</option>
            ))}
          </select>
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Heladera</label>
          <select
            value={heladeraId}
            onChange={(e) => setHeladeraId(e.target.value)}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-green-500 focus:border-green-500"
          >
            <option value="">Seleccionar heladera</option>
            {heladerasActivas.map((f) => (
              <option key={f.id} value={f.id}>{f.name}</option>
            ))}
          </select>
        </div>
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Agregar productos</label>
        <div className="flex flex-col sm:flex-row gap-2">
          <select
            value={productoSeleccionado}
            onChange={(e) => setProductoSeleccionado(e.target.value)}
            className="flex-1 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-green-500 focus:border-green-500"
          >
            <option value="">Seleccionar producto</option>
            {productsData.map((p) => (
              <option key={p.id} value={p.id}>{p.name} - ${p.price.toLocaleString('es-AR')}</option>
            ))}
          </select>
          <input
            type="number"
            min="1"
            value={cantidad}
            onChange={(e) => setCantidad(parseInt(e.target.value) || 1)}
            className="w-20 border border-gray-300 rounded-lg px-3 py-2 text-sm text-center"
          />
          <button
            type="button"
            onClick={agregarItem}
            className="bg-green-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-green-700 transition-colors"
          >
            Agregar
          </button>
        </div>
      </div>

      {items.length > 0 && (
        <div className="bg-gray-50 rounded-lg p-3 space-y-2">
          {items.map((item) => (
            <div key={item.productId} className="flex items-center justify-between text-sm">
              <div className="flex-1 min-w-0">
                <span className="font-medium text-gray-800">{item.productName}</span>
                <span className="text-gray-500 ml-2">x{item.quantity}</span>
                <span className="text-gray-500 ml-2">${item.unitPrice.toLocaleString('es-AR')} c/u</span>
              </div>
              <div className="flex items-center gap-3 ml-2">
                <span className="font-semibold text-gray-700">${(item.unitPrice * item.quantity).toLocaleString('es-AR')}</span>
                <button
                  type="button"
                  onClick={() => eliminarItem(item.productId)}
                  className="text-red-400 hover:text-red-600 text-xs"
                >
                  ✕
                </button>
              </div>
            </div>
          ))}
          <div className="border-t border-gray-200 pt-2 flex justify-between items-center">
            <span className="font-bold text-gray-900">Total</span>
            <span className="font-bold text-green-700 text-lg">${total.toLocaleString('es-AR')}</span>
          </div>
        </div>
      )}

      {error && (
        <div className="text-sm text-red-500 bg-red-50 rounded-lg p-2">{error}</div>
      )}

      <div className="flex gap-3">
        <button
          type="submit"
          disabled={!usuarioId || !heladeraId || items.length === 0 || loading}
          className="flex-1 bg-green-600 text-white py-2.5 rounded-lg text-sm font-medium hover:bg-green-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {loading ? 'Creando...' : 'Crear Orden'}
        </button>
        <button
          type="button"
          onClick={onCancel}
          className="px-4 py-2.5 border border-gray-300 rounded-lg text-sm text-gray-600 hover:bg-gray-50"
        >
          Cancelar
        </button>
      </div>
    </form>
  )
}

export default OrderForm