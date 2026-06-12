const STATUS_CONFIG = {
  pending: { label: 'Pendiente', color: 'bg-yellow-100 text-yellow-800' },
  paid: { label: 'Pagada', color: 'bg-blue-100 text-blue-800' },
  picked_up: { label: 'Retirada', color: 'bg-green-100 text-green-800' },
  cancelled: { label: 'Cancelada', color: 'bg-red-100 text-red-800' },
}

function OrderList({ orders, onSelect }) {
  return (
    <div className="flex flex-col gap-3">
      {orders.map((order) => {
        const status = STATUS_CONFIG[order.status] || STATUS_CONFIG.pending
        return (
          <button
            key={order.id}
            onClick={() => onSelect(order)}
            className="w-full bg-white rounded-xl border border-gray-200 shadow-sm p-4 text-left hover:shadow-md transition-shadow"
          >
            <div className="flex items-start justify-between gap-3">
              <div className="min-w-0 flex-1">
                <div className="flex items-center gap-2 mb-1">
                  <span className="font-semibold text-gray-900 text-sm">#{order.id}</span>
                  <span className={`text-xs font-medium px-2 py-0.5 rounded-full ${status.color}`}>
                    {status.label}
                  </span>
                </div>
                <div className="text-xs text-gray-500 space-y-0.5">
                  <p>{new Date(order.createdAt).toLocaleDateString('es-AR', { day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit' })}</p>
                  <p>{order.items.length} producto{order.items.length !== 1 ? 's' : ''}</p>
                </div>
              </div>
              <span className="font-bold text-green-700 text-lg whitespace-nowrap">
                ${order.total.toLocaleString('es-AR')}
              </span>
            </div>
          </button>
        )
      })}
    </div>
  )
}

export default OrderList