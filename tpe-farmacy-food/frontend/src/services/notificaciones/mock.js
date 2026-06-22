import notificacionesData from '../../mocks/notifications.json'

const SIMULAR_DELAY = 300

const simularAsync = (data, delay = SIMULAR_DELAY) =>
  new Promise((resolve) => setTimeout(() => resolve(data), delay))

let notificaciones = [...notificacionesData]

const notificationService = {
  getNotifications: async (userId) => {
    if (userId) {
      const userNotifs = notificaciones.filter((n) => n.userId === userId)
      return simularAsync(userNotifs)
    }
    return simularAsync([...notificaciones])
  },

  markAsRead: async (id) => {
    const notif = notificaciones.find((n) => n.id === id)
    if (!notif) throw new Error(`Notificacion ${id} no encontrada`)
    notif.read = true
    return simularAsync(notif)
  },

  subscribe: async (userId, data) => {
    const subscription = {
      id: `sub_${Date.now()}`,
      userId,
      deviceToken: data.deviceToken || 'mock_device_token',
      productPreferences: data.productPreferences || [],
      createdAt: new Date().toISOString(),
    }
    return simularAsync(subscription)
  },

  unsubscribe: async (userId, productId) => {
    return simularAsync({ eliminado: true, userId, productId })
  },
}

export default notificationService