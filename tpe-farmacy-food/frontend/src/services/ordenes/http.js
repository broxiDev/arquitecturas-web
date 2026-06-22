import httpClient from '../../lib/httpClient'

const orderService = {
  create: async (data) => {
    // POST /api/v1/ordenes
    const response = await httpClient.post('/ordenes', data)
    return response.data
  },

  getAll: async () => {
    // GET /api/v1/ordenes
    const response = await httpClient.get('/ordenes')
    return response.data
  },

  getById: async (id) => {
    // GET /api/v1/ordenes/{id}
    const response = await httpClient.get(`/ordenes/${id}`)
    return response.data
  },

  getUserOrders: async (userId) => {
    // GET /api/v1/ordenes?userId=...
    const response = await httpClient.get('/ordenes', { params: { userId } })
    return response.data
  },

  cancel: async (id) => {
    // PUT /api/v1/ordenes/{id}/cancelar
    const response = await httpClient.put(`/ordenes/${id}/cancelar`)
    return response.data
  },

  pay: async (id) => {
    // PUT /api/v1/ordenes/{id}/pagar
    const response = await httpClient.put(`/ordenes/${id}/pagar`)
    return response.data
  },

  confirmPickup: async (id) => {
    // PUT /api/v1/ordenes/{id}/retirar
    const response = await httpClient.put(`/ordenes/${id}/retirar`)
    return response.data
  },
}

export default orderService
