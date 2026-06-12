import { create } from 'zustand'
import orderService from '../../services/orderService'

const useOrderStore = create((set) => ({
  orders: [],
  selectedOrder: null,
  loading: false,
  error: null,

  fetchOrders: async () => {
    set({ loading: true, error: null })
    try {
      const orders = await orderService.getAll()
      set({ orders, loading: false })
    } catch (error) {
      set({ error: error.message, loading: false })
    }
  },

  fetchOrderById: async (id) => {
    set({ loading: true, error: null })
    try {
      const order = await orderService.getById(id)
      set({ selectedOrder: order, loading: false })
    } catch (error) {
      set({ error: error.message, loading: false })
    }
  },

  createOrder: async (data) => {
    set({ loading: true, error: null })
    try {
      const newOrder = await orderService.create(data)
      set((state) => ({
        orders: [...state.orders, newOrder],
        selectedOrder: newOrder,
        loading: false,
      }))
    } catch (error) {
      set({ error: error.message, loading: false })
    }
  },

  cancelOrder: async (id) => {
    set({ loading: true, error: null })
    try {
      const updated = await orderService.cancel(id)
      set((state) => ({
        orders: state.orders.map((o) => (o.id === id ? updated : o)),
        selectedOrder: state.selectedOrder?.id === id ? updated : state.selectedOrder,
        loading: false,
      }))
    } catch (error) {
      set({ error: error.message, loading: false })
    }
  },

  payOrder: async (id) => {
    set({ loading: true, error: null })
    try {
      const updated = await orderService.pay(id)
      set((state) => ({
        orders: state.orders.map((o) => (o.id === id ? updated : o)),
        selectedOrder: state.selectedOrder?.id === id ? updated : state.selectedOrder,
        loading: false,
      }))
    } catch (error) {
      set({ error: error.message, loading: false })
    }
  },

  clearSelectedOrder: () => {
    set({ selectedOrder: null })
  },
}))

export default useOrderStore