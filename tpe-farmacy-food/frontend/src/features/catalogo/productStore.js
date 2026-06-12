import { create } from 'zustand'
import productService from '../../services/productService'

const useProductStore = create((set) => ({
  products: [],
  selectedProduct: null,
  filters: { category: null },
  loading: false,
  error: null,

  fetchProducts: async (category) => {
    set({ loading: true, error: null })
    try {
      const products = await productService.getAll(category)
      set({ products, loading: false })
    } catch (error) {
      set({ error: error.message, loading: false })
    }
  },

  fetchProductById: async (id) => {
    set({ loading: true, error: null })
    try {
      const product = await productService.getById(id)
      set({ selectedProduct: product, loading: false })
    } catch (error) {
      set({ error: error.message, loading: false })
    }
  },

  setFilter: (category) => {
    set({ filters: { category } })
  },

  clearSelectedProduct: () => {
    set({ selectedProduct: null })
  },
}))

export default useProductStore