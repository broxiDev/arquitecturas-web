import heladerasData from '../../mocks/fridges.json'

const SIMULAR_DELAY = 300

const simularAsync = (data, delay = SIMULAR_DELAY) =>
  new Promise((resolve) => setTimeout(() => resolve(data), delay))

const fridgeService = {
  getAll: async (lat, lng) => {
    let resultado = [...heladerasData]
    if (lat && lng) {
      resultado = resultado.map((f) => ({
        ...f,
        distancia: Math.sqrt(
          Math.pow(f.location.lat - lat, 2) + Math.pow(f.location.lng - lng, 2)
        ).toFixed(2),
      }))
      resultado.sort((a, b) => a.distancia - b.distancia)
    }
    return simularAsync(resultado)
  },

  getById: async (id) => {
    const heladera = heladerasData.find((f) => f.id === id)
    if (!heladera) throw new Error(`Heladera ${id} no encontrada`)
    return simularAsync(heladera)
  },

  getStock: async (fridgeId) => {
    const heladera = heladerasData.find((f) => f.id === fridgeId)
    if (!heladera) throw new Error(`Heladera ${fridgeId} no encontrada`)
    return simularAsync(heladera.stock)
  },

  updateStock: async (fridgeId, data) => {
    const heladera = heladerasData.find((f) => f.id === fridgeId)
    if (!heladera) throw new Error(`Heladera ${fridgeId} no encontrada`)
    heladera.stock = data
    return simularAsync(heladera)
  },
}

export default fridgeService