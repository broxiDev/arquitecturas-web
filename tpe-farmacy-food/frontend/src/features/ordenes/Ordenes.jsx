import { useEffect, useState } from 'react'
import useOrderStore from './orderStore'
import OrderList from './OrderList'
import OrderDetail from './OrderDetail'
import OrderForm from './OrderForm'

function Ordenes() {
  const { orders, selectedOrder, loading, error, fetchOrders, fetchOrderById, cancelOrder, payOrder, clearSelectedOrder } = useOrderStore()
  const [showForm, setShowForm] = useState(false)

  useEffect(() => {
    fetchOrders()
  }, [])

  const handleSelectOrder = (order) => {
    fetchOrderById(order.id)
  }

  const handlePay = async (id) => {
    await payOrder(id)
  }

  const handleCancel = async (id) => {
    await cancelOrder(id)
  }

  if (selectedOrder && !showForm) {
    return (
      <div className="px-2 md:px-0">
        <OrderDetail
          order={selectedOrder}
          onBack={clearSelectedOrder}
          onPay={handlePay}
          onCancel={handleCancel}
        />
      </div>
    )
  }

  if (showForm) {
    return (
      <div className="px-2 md:px-0">
        <button
          onClick={() => setShowForm(false)}
          className="text-sm text-green-600 hover:text-green-800 mb-3 flex items-center gap-1"
        >
          ← Volver a ordenes
        </button>
        <h2 className="text-xl font-semibold text-gray-800 mb-4">Nueva Orden</h2>
        <OrderForm onCancel={() => setShowForm(false)} />
      </div>
    )
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <span className="text-sm text-gray-500">{orders.length} ordenes</span>
        <button
          onClick={() => setShowForm(true)}
          className="bg-green-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-green-700 transition-colors"
        >
          + Nueva Orden
        </button>
      </div>

      {loading && (
        <div className="text-center py-8 text-gray-500">Cargando ordenes...</div>
      )}

      {error && (
        <div className="text-center py-4 text-red-500 bg-red-50 rounded-lg">{error}</div>
      )}

      {!loading && !error && (
        <OrderList orders={orders} onSelect={handleSelectOrder} />
      )}
    </div>
  )
}

export default Ordenes