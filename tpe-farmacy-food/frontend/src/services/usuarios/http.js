import httpClient from '../../lib/httpClient'

const userService = {
  getAll: async () => {
    // GET /api/v1/usuarios
    const response = await httpClient.get('/usuarios')
    return response.data
  },

  getById: async (id) => {
    // GET /api/v1/usuarios/{id}
    const response = await httpClient.get(`/usuarios/${id}`)
    return response.data
  },
}

export default userService
