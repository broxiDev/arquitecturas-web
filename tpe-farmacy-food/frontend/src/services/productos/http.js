import httpClient from '../../lib/httpClient'

const productService = {
  getAll: async (categoria) => {
    // GET /api/v1/productos?categoria=...
    const response = await httpClient.get('/productos', { params: { categoria } })
    return response.data
  },

  getById: async (id) => {
    // GET /api/v1/productos/{id}
    const response = await httpClient.get(`/productos/${id}`)
    return response.data
  },

  create: async (data) => {
    // POST /api/v1/productos
    const response = await httpClient.post('/productos', data)
    return response.data
  },

  update: async (id, data) => {
    // PUT /api/v1/productos/{id}
    const response = await httpClient.put(`/productos/${id}`, data)
    return response.data
  },

  delete: async (id) => {
    // DELETE /api/v1/productos/{id}
    const response = await httpClient.delete(`/productos/${id}`)
    return response.data
  },
}

export default productService
