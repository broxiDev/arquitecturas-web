import { create } from 'zustand'
import kitchenService from '../../services/cocina/index.js'

const useKitchenStore = create((set) => ({
  dailyPlan: null,
  availableDates: [],
  selectedDate: null,
  loading: false,
  error: null,

  fetchDailyPlan: async (date) => {
    set({ loading: true, error: null })
    try {
      const plan = await kitchenService.getDailyPlan(date)
      set({ dailyPlan: plan, selectedDate: date || plan.date, loading: false })
    } catch (error) {
      set({ error: error.message, loading: false })
    }
  },

  fetchAvailableDates: async () => {
    set({ loading: true, error: null })
    try {
      const history = await kitchenService.getSalesHistory()
      const dates = history.map((h) => h.date)
      set({ availableDates: dates, loading: false })
    } catch (error) {
      set({ error: error.message, loading: false })
    }
  },

  setDate: (date) => {
    set({ selectedDate: date })
  },
}))

export default useKitchenStore