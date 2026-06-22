import { create } from 'zustand'
import fridgeService from '../../services/heladeras/index.js'

const useFridgeStore = create((set) => ({
  fridges: [],
  selectedFridge: null,
  loading: false,
  error: null,

  fetchFridges: async (lat, lng) => {
    set({ loading: true, error: null })
    try {
      const fridges = await fridgeService.getAll(lat, lng)
      set({ fridges, loading: false })
    } catch (error) {
      set({ error: error.message, loading: false })
    }
  },

  fetchFridgeById: async (id) => {
    set({ loading: true, error: null })
    try {
      const fridge = await fridgeService.getById(id)
      set({ selectedFridge: fridge, loading: false })
    } catch (error) {
      set({ error: error.message, loading: false })
    }
  },

  fetchStock: async (fridgeId) => {
    set({ loading: true, error: null })
    try {
      const stock = await fridgeService.getStock(fridgeId)
      set((state) => ({
        selectedFridge: state.selectedFridge
          ? { ...state.selectedFridge, stock }
          : null,
        loading: false,
      }))
    } catch (error) {
      set({ error: error.message, loading: false })
    }
  },

  clearSelectedFridge: () => {
    set({ selectedFridge: null })
  },
}))

export default useFridgeStore