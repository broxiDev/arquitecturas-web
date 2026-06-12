const STATUS_CONFIG = {
  pending: { label: 'Pendiente', color: 'bg-yellow-100 text-yellow-800 border-yellow-200', dot: 'bg-yellow-500' },
  paid: { label: 'Pagada', color: 'bg-blue-100 text-blue-800 border-blue-200', dot: 'bg-blue-500' },
  picked_up: { label: 'Retirada', color: 'bg-green-100 text-green-800 border-green-200', dot: 'bg-green-500' },
  cancelled: { label: 'Cancelada', color: 'bg-red-100 text-red-800 border-red-200', dot: 'bg-red-500' },
}

function OrderDetail({ order, onBack, onPay, onCancel }) {
  const status = STATUS_CONFIG[order.status] || STATUS_CONFIG.pending

  const timeline = [
    { label: 'Orden creada', done: true, date: order.createdAt },
    { label: 'Pago realizado', done: ['paid', 'picked_up'].includes(order.status), date: order.status === 'paid' || order.status === 'picked_up' ? 'Completado' : null },
    { label: 'Producto retirado', done: order.status === 'picked_up', date: order.status === 'picked_up' ? 'Completado' : null },
  ]

  return (
    <div className="space-y-4">
      <button
        onClick={onBack}
        className="text-sm text-green-600 hover:text-green-800 flex items-center gap-1"
      >
        ← Volver a ordenes
      </button>

      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
        <div className="flex items-start justify-between gap-3 mb-4">
          <div>
            <h2 className="text-lg font-bold text-gray-900">Orden #{order.id}</h2>
            <p className="text-xs text-gray-500 mt-1">
              {new Date(order.createdAt).toLocaleDateString('es-AR', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' })}
            </p>
          </div>
          <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold border ${status.color}`}>
            <span className={`w-2 h-2 rounded-full ${status.dot}`} />
            {status.label}
          </span>
        </div>

        <div className="text-sm text-gray-600 mb-4">
          <p>Usuario: <span className="font-medium text-gray-800">{order.userId}</span></p>
          <p>Heladera: <span className="font-medium text-gray-800">{order.fridgeId}</span></p>
          {order.paymentId && (
            <p>Pago: <span className="font-mono text-xs text-gray-500">{order.paymentId}</span></p>
          )}
        </div>

        <div className="border-t border-gray-100 pt-3">
          <h3 className="text-sm font-semibold text-gray-700 mb-2">Productos</h3>
          <div className="space-y-2">
            {order.items.map((item, idx) => (
              <div key={idx} className="flex items-center justify-between text-sm">
                <div>
                  <span className="text-gray-800">{item.productName}</span>
                  <span className="text-gray-500 ml-1">x{item.quantity}</span>
                </div>
                <span className="font-medium text-gray-700">
                  ${(item.unitPrice * item.quantity).toLocaleString('es-AR')}
                </span>
              </div>
            ))}
          </div>
          <div className="border-t border-gray-100 mt-3 pt-3 flex justify-between">
            <span className="font-bold text-gray-900">Total</span>
            <span className="font-bold text-green-700 text-lg">${order.total.toLocaleString('es-AR')}</span>
          </div>
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
        <h3 className="text-sm font-semibold text-gray-700 mb-3">Estado de la orden</h3>
        <div className="space-y-3">
          {timeline.map((step, idx) => (
            <div key={idx} className="flex items-center gap-3">
              <div className={`w-3 h-3 rounded-full ${step.done ? 'bg-green-500' : 'bg-gray-200'}`} />
              <div className="flex-1">
                <span className={`text-sm ${step.done ? 'text-gray-800 font-medium' : 'text-gray-400'}`}>
                  {step.label}
                </span>
                {step.date && typeof step.date === 'string' && step.date !== 'Completado' && (
                  <span className="text-xs text-gray-400 ml-2">{step.date}</span>
                )}
              </div>
            </div>
          ))}
        </div>

        <div className="mt-4 flex gap-2">
          {order.status === 'pending' && (
            <>
              <button
                onClick={() => onPay(order.id)}
                className="flex-1 bg-green-600 text-white py-2 rounded-lg text-sm font-medium hover:bg-green-700 transition-colors"
              >
                Pagar orden
              </button>
              <button
                onClick={() => onCancel(order.id)}
                className="px-4 py-2 border border-red-300 text-red-600 rounded-lg text-sm font-medium hover:bg-red-50 transition-colors"
              >
                Cancelar
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  )
}

export default OrderDetail