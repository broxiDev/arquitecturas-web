import httpClient from '../../lib/httpClient'

const fridgeService = {
  getAll: async (lat, lng) => {
    // GET /api/v1/heladeras?lat=...&lng=...
    const response = await httpClient.get('/heladeras', { params: { lat, lng } })
    return response.data
  },

  getById: async (id) => {
    // GET /api/v1/heladeras/{id}
    const response = await httpClient.get(`/heladeras/${id}`)
    return response.data
  },

  getStock: async (fridgeId) => {
    // GET /api/v1/heladeras/{fridgeId}/stock
    const response = await httpClient.get(`/heladeras/${fridgeId}/stock`)
    return response.data
  },

  updateStock: async (fridgeId, data) => {
    // PUT /api/v1/heladeras/{fridgeId}/stock
    const response = await httpClient.put(`/heladeras/${fridgeId}/stock`, data)
    return response.data
  },
}

export default fridgeService
