const STATUS_CONFIG = {
  active: { color: 'bg-green-500', label: 'Activa', textClass: 'text-green-700 bg-green-50' },
  maintenance: { color: 'bg-yellow-500', label: 'En mantenimiento', textClass: 'text-yellow-700 bg-yellow-50' },
  out_of_service: { color: 'bg-red-500', label: 'Fuera de servicio', textClass: 'text-red-700 bg-red-50' },
}

function FridgeCard({ fridge, onSelect }) {
  const status = STATUS_CONFIG[fridge.status] || STATUS_CONFIG.active

  return (
    <button
      onClick={() => onSelect(fridge)}
      className="w-full p-4 bg-white rounded-xl border border-gray-200 shadow-sm hover:shadow-md transition-shadow text-left"
    >
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0 flex-1">
          <h3 className="font-semibold text-gray-900 text-sm truncate">{fridge.name}</h3>
          <p className="text-xs text-gray-500 mt-1 truncate">{fridge.location.address}</p>
        </div>
        <span className={`inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-xs font-medium whitespace-nowrap ${status.textClass}`}>
          <span className={`w-2 h-2 rounded-full ${status.color}`} />
          {status.label}
        </span>
      </div>
      <div className="mt-3 flex items-center gap-4 text-xs text-gray-500">
        <span>📦 {fridge.stock.length} productos</span>
        <span>📦 {fridge.stock.reduce((sum, s) => sum + s.quantity, 0)} unidades</span>
      </div>
    </button>
  )
}

export default FridgeCard