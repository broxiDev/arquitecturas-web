const CATEGORIAS = [
  { value: null, label: 'Todas' },
  { value: 'vegano', label: 'Vegano' },
  { value: 'vegetariano', label: 'Vegetariano' },
  { value: 'sin gluten', label: 'Sin Gluten' },
]

const CATEGORY_COLORS = {
  vegano: 'bg-green-100 text-green-800',
  vegetariano: 'bg-lime-100 text-lime-800',
  'sin gluten': 'bg-amber-100 text-amber-800',
}

function ProductCard({ product, onSelect }) {
  const colorClass = CATEGORY_COLORS[product.dietaryCategory] || 'bg-gray-100 text-gray-800'

  return (
    <button
      onClick={() => onSelect(product)}
      className="flex flex-col rounded-lg border border-gray-200 bg-white shadow-sm hover:shadow-md transition-shadow text-left w-full"
    >
      <div className="aspect-[4/3] bg-gradient-to-br from-green-100 to-green-50 rounded-t-lg flex items-center justify-center">
        <span className="text-4xl">🥗</span>
      </div>
      <div className="p-3 flex flex-col gap-1.5">
        <span className={`text-xs font-medium px-2 py-0.5 rounded-full w-fit ${colorClass}`}>
          {product.dietaryCategory}
        </span>
        <h3 className="font-semibold text-gray-900 text-sm leading-tight">{product.name}</h3>
        <p className="text-xs text-gray-500 line-clamp-2">{product.description}</p>
        <span className="text-green-700 font-bold text-lg mt-1">
          ${product.price.toLocaleString('es-AR')}
        </span>
      </div>
    </button>
  )
}

export default ProductCard