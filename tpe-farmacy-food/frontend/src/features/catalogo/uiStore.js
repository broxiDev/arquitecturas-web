import { create } from 'zustand'

const useUiStore = create((set) => ({
  sidebarOpen: false,
  globalLoading: false,
  filters: { category: null, searchQuery: '' },

  toggleSidebar: () => {
    set((state) => ({ sidebarOpen: !state.sidebarOpen }))
  },

  setLoading: (loading) => {
    set({ globalLoading: loading })
  },

  setFilter: (key, value) => {
    set((state) => ({
      filters: { ...state.filters, [key]: value },
    }))
  },
}))

export default useUiStore
