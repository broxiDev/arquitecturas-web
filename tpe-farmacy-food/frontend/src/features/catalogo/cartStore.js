import { create } from 'zustand'

const useCartStore = create((set, get) => ({
  items: [],
  total: 0,
  itemCount: 0,

  addItem: (product, quantity = 1) => {
    set((state) => {
      const existing = state.items.find((i) => i.product.id === product.id)
      let items
      if (existing) {
        items = state.items.map((i) =>
          i.product.id === product.id
            ? { ...i, quantity: i.quantity + quantity }
            : i,
        )
      } else {
        items = [...state.items, { product, quantity }]
      }
      const total = items.reduce((sum, i) => sum + i.product.price * i.quantity, 0)
      const itemCount = items.reduce((sum, i) => sum + i.quantity, 0)
      return { items, total, itemCount }
    })
  },

  removeItem: (productId) => {
    set((state) => {
      const items = state.items.filter((i) => i.product.id !== productId)
      const total = items.reduce((sum, i) => sum + i.product.price * i.quantity, 0)
      const itemCount = items.reduce((sum, i) => sum + i.quantity, 0)
      return { items, total, itemCount }
    })
  },

  updateQuantity: (productId, quantity) => {
    set((state) => {
      if (quantity <= 0) {
        const items = state.items.filter((i) => i.product.id !== productId)
        const total = items.reduce((sum, i) => sum + i.product.price * i.quantity, 0)
        const itemCount = items.reduce((sum, i) => sum + i.quantity, 0)
        return { items, total, itemCount }
      }
      const items = state.items.map((i) =>
        i.product.id === productId ? { ...i, quantity } : i,
      )
      const total = items.reduce((sum, i) => sum + i.product.price * i.quantity, 0)
      const itemCount = items.reduce((sum, i) => sum + i.quantity, 0)
      return { items, total, itemCount }
    })
  },

  clearCart: () => {
    set({ items: [], total: 0, itemCount: 0 })
  },
}))

export default useCartStore
