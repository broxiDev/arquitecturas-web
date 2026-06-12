const CATEGORY_COLORS = {
  vegano: 'bg-green-100 text-green-800',
  vegetariano: 'bg-lime-100 text-lime-800',
  'sin gluten': 'bg-amber-100 text-amber-800',
}

function ProductDetail({ product, onBack }) {
  if (!product) return null
  const colorClass = CATEGORY_COLORS[product.dietaryCategory] || 'bg-gray-100 text-gray-800'

  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200">
      <div className="p-4">
        <button
          onClick={onBack}
          className="text-sm text-green-600 hover:text-green-800 mb-3 flex items-center gap-1"
        >
          ← Volver al catalogo
        </button>
        <div className="aspect-[4/3] md:aspect-video bg-gradient-to-br from-green-100 to-green-50 rounded-lg flex items-center justify-center mb-4">
          <span className="text-6xl">🥗</span>
        </div>
        <div className="space-y-3">
          <span className={`text-xs font-medium px-2 py-1 rounded-full ${colorClass}`}>
            {product.dietaryCategory}
          </span>
          <h2 className="text-xl font-bold text-gray-900">{product.name}</h2>
          <p className="text-gray-600 text-sm leading-relaxed">{product.description}</p>
          <div className="flex items-center justify-between pt-2 border-t border-gray-100">
            <span className="text-2xl font-bold text-green-700">
              ${product.price.toLocaleString('es-AR')}
            </span>
            <span className="text-xs text-gray-400">
              Creado: {new Date(product.createdAt).toLocaleDateString('es-AR')}
            </span>
          </div>
        </div>
      </div>
    </div>
  )
}

export default ProductDetail