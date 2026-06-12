import productsData from '../../mocks/products.json'

const STATUS_CONFIG = {
  active: { color: 'bg-green-500', label: 'Activa', textClass: 'text-green-700 bg-green-50 border-green-200' },
  maintenance: { color: 'bg-yellow-500', label: 'En mantenimiento', textClass: 'text-yellow-700 bg-yellow-50 border-yellow-200' },
  out_of_service: { color: 'bg-red-500', label: 'Fuera de servicio', textClass: 'text-red-700 bg-red-50 border-red-200' },
}

function FridgeDetail({ fridge, onBack }) {
  if (!fridge) return null
  const status = STATUS_CONFIG[fridge.status] || STATUS_CONFIG.active

  const productMap = new Map(productsData.map((p) => [p.id, p]))

  return (
    <div className="space-y-4">
      <button
        onClick={onBack}
        className="text-sm text-green-600 hover:text-green-800 flex items-center gap-1"
      >
        ← Volver a heladeras
      </button>

      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
        <div className="flex items-start justify-between gap-3 mb-3">
          <div>
            <h2 className="text-xl font-bold text-gray-900">{fridge.name}</h2>
            <p className="text-sm text-gray-500 mt-1">{fridge.location.address}</p>
          </div>
          <span className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold border ${status.textClass}`}>
            <span className={`w-2.5 h-2.5 rounded-full ${status.color}`} />
            {status.label}
          </span>
        </div>

        <div className="text-xs text-gray-400 mt-2">
          Ultimo mantenimiento: {new Date(fridge.lastMaintenance).toLocaleDateString('es-AR')}
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
        <h3 className="font-semibold text-gray-900 mb-3">Stock disponible</h3>
        {fridge.stock.length === 0 ? (
          <p className="text-sm text-gray-500 text-center py-4">No hay productos disponibles en esta heladera.</p>
        ) : (
          <div className="space-y-2">
            {fridge.stock.map((item) => {
              const product = productMap.get(item.productId)
              return (
                <div key={item.productId} className="flex items-center justify-between py-2 border-b border-gray-50 last:border-0">
                  <div className="min-w-0 flex-1">
                    <span className="text-sm font-medium text-gray-800">
                      {product?.name || item.productId}
                    </span>
                    {product && (
                      <span className={`ml-2 text-xs px-1.5 py-0.5 rounded-full ${
                        product.dietaryCategory === 'vegano' ? 'bg-green-50 text-green-700' :
                        product.dietaryCategory === 'vegetariano' ? 'bg-lime-50 text-lime-700' :
                        'bg-amber-50 text-amber-700'
                      }`}>
                        {product.dietaryCategory}
                      </span>
                    )}
                  </div>
                  <span className="text-sm font-semibold text-gray-700 ml-3">{item.quantity} u.</span>
                </div>
              )
            })}
          </div>
        )}
      </div>
    </div>
  )
}

export default FridgeDetail