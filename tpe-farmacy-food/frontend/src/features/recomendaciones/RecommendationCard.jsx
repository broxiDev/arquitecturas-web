const REASON_ICONS = {
  'Basado en tu historial': '\u{1F4CA}',
  'Usuarios similares': '\u{1F465}',
  'Tendencia de la semana': '\u{1F525}',
}

const CATEGORY_COLORS = {
  vegano: 'bg-green-100 text-green-800',
  vegetariano: 'bg-lime-100 text-lime-800',
  'sin gluten': 'bg-amber-100 text-amber-800',
}

function RecommendationCard({ recommendation, onViewProduct }) {
  const icon = REASON_ICONS[recommendation.reason] || '\u{1F4A1}'
  const colorClass = CATEGORY_COLORS[recommendation.dietaryCategory] || 'bg-gray-100 text-gray-800'

  return (
    <div className="bg-white rounded-xl border border-gray-200 shadow-sm p-4">
      <div className="flex items-start justify-between gap-3">
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 mb-1.5">
            <span className="text-lg">{icon}</span>
            <span className={`text-xs font-medium px-2 py-0.5 rounded-full ${colorClass}`}>
              {recommendation.dietaryCategory}
            </span>
          </div>
          <h3 className="font-semibold text-gray-900 text-sm">{recommendation.productName}</h3>
          <p className="text-xs text-gray-500 mt-0.5">{recommendation.reason}</p>
          <span className="text-green-700 font-bold text-sm mt-1 block">
            ${recommendation.price.toLocaleString('es-AR')}
          </span>
        </div>
        {onViewProduct && (
          <button
            onClick={() => onViewProduct(recommendation)}
            className="text-xs text-green-600 hover:text-green-800 font-medium whitespace-nowrap"
          >
            Ver en catalogo
          </button>
        )}
      </div>
    </div>
  )
}

export default RecommendationCard