import { create } from 'zustand'
import recommendationService from '../../services/recomendaciones/index.js'

const useRecommendationStore = create((set) => ({
  recommendations: [],
  selectedUser: null,
  loading: false,
  error: null,

  fetchRecommendations: async (userId) => {
    set({ loading: true, error: null })
    try {
      const data = await recommendationService.getRecommendations(userId)
      set({ recommendations: data.recommendations || [], selectedUser: userId, loading: false })
    } catch (error) {
      set({ error: error.message, loading: false })
    }
  },

  setSelectedUser: (userId) => {
    set({ selectedUser: userId })
  },
}))

export default useRecommendationStore