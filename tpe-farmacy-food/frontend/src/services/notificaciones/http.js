import httpClient from '../../lib/httpClient'

const notificationService = {
  getNotifications: async (userId) => {
    // GET /api/v1/notificaciones?userId=...
    const response = await httpClient.get('/notificaciones', { params: { userId } })
    return response.data
  },

  markAsRead: async (id) => {
    // PUT /api/v1/notificaciones/{id}/leer
    const response = await httpClient.put(`/notificaciones/${id}/leer`)
    return response.data
  },

  subscribe: async (userId, data) => {
    // POST /api/v1/notificaciones/suscribir
    const response = await httpClient.post('/notificaciones/suscribir', { userId, ...data })
    return response.data
  },

  unsubscribe: async (userId, productId) => {
    // DELETE /api/v1/notificaciones/suscribir?userId=...&productId=...
    const response = await httpClient.delete('/notificaciones/suscribir', { params: { userId, productId } })
    return response.data
  },
}

export default notificationService
