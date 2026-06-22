import usuariosData from '../../mocks/users.json'

const SIMULAR_DELAY = 200

const simularAsync = (data, delay = SIMULAR_DELAY) =>
  new Promise((resolve) => setTimeout(() => resolve(data), delay))

const userService = {
  getAll: async () => {
    return simularAsync([...usuariosData])
  },

  getById: async (id) => {
    const usuario = usuariosData.find((u) => u.id === id)
    if (!usuario) throw new Error(`Usuario ${id} no encontrado`)
    return simularAsync(usuario)
  },
}

export default userService