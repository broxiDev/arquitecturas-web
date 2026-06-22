import { create } from 'zustand'
import notificationService from '../../services/notificaciones/index.js'

const useNotificationStore = create((set, get) => ({
  notifications: [],
  subscriptions: [],
  unreadCount: 0,
  loading: false,
  error: null,

  fetchNotifications: async (userId) => {
    set({ loading: true, error: null })
    try {
      const notifications = await notificationService.getNotifications(userId)
      const unread = notifications.filter((n) => !n.read).length
      set({ notifications, unreadCount: unread, loading: false })
    } catch (error) {
      set({ error: error.message, loading: false })
    }
  },

  markAsRead: async (id) => {
    try {
      const updated = await notificationService.markAsRead(id)
      set((state) => ({
        notifications: state.notifications.map((n) => (n.id === id ? updated : n)),
        unreadCount: Math.max(0, state.unreadCount - 1),
      }))
    } catch (error) {
      set({ error: error.message })
    }
  },

  subscribe: async (userId, data) => {
    set({ loading: true, error: null })
    try {
      const subscription = await notificationService.subscribe(userId, data)
      set((state) => ({
        subscriptions: [...state.subscriptions, subscription],
        loading: false,
      }))
    } catch (error) {
      set({ error: error.message, loading: false })
    }
  },

  unsubscribe: async (userId, productId) => {
    try {
      await notificationService.unsubscribe(userId, productId)
      set((state) => ({
        subscriptions: state.subscriptions.filter((s) => s.productPreferences?.[0] !== productId),
      }))
    } catch (error) {
      set({ error: error.message })
    }
  },
}))

export default useNotificationStore