import { useEffect, useState } from 'react'
import useProductStore from './productStore'
import ProductCard from './ProductCard'
import ProductDetail from './ProductDetail'

const CATEGORIAS = [
  { value: null, label: 'Todas' },
  { value: 'vegano', label: 'Vegano' },
  { value: 'vegetariano', label: 'Vegetariano' },
  { value: 'sin gluten', label: 'Sin Gluten' },
]

function Catalogo() {
  const { products, selectedProduct, filters, loading, error, fetchProducts, fetchProductById, setFilter, clearSelectedProduct } = useProductStore()

  useEffect(() => {
    fetchProducts(filters.category)
  }, [filters.category])

  const handleCategoryFilter = (category) => {
    setFilter(category)
  }

  const handleSelectProduct = (product) => {
    fetchProductById(product.id)
  }

  if (selectedProduct) {
    return (
      <div className="px-2 md:px-0">
        <ProductDetail product={selectedProduct} onBack={clearSelectedProduct} />
      </div>
    )
  }

  const filteredProducts = filters.category
    ? products
    : products

  return (
    <div>
      <div className="flex gap-2 overflow-x-auto pb-2 mb-4 scrollbar-hide">
        {CATEGORIAS.map((cat) => (
          <button
            key={cat.label}
            onClick={() => handleCategoryFilter(cat.value)}
            className={`px-3 py-1.5 rounded-full text-sm font-medium whitespace-nowrap transition-colors ${
              filters.category === cat.value
                ? 'bg-green-600 text-white'
                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
            }`}
          >
            {cat.label}
          </button>
        ))}
      </div>

      {loading && (
        <div className="text-center py-8 text-gray-500">Cargando productos...</div>
      )}

      {error && (
        <div className="text-center py-4 text-red-500 bg-red-50 rounded-lg">{error}</div>
      )}

      {!loading && !error && (
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3 md:gap-4">
          {products.map((product) => (
            <ProductCard key={product.id} product={product} onSelect={handleSelectProduct} />
          ))}
        </div>
      )}

      {!loading && !error && products.length === 0 && (
        <div className="text-center py-8 text-gray-500">
          No se encontraron productos{filters.category ? ` en la categoria "${filters.category}"` : ''}.
        </div>
      )}
    </div>
  )
}

export default Catalogo