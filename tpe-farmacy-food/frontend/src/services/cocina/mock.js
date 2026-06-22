import planesData from '../../mocks/kitchen.json'

const SIMULAR_DELAY = 300

const simularAsync = (data, delay = SIMULAR_DELAY) =>
  new Promise((resolve) => setTimeout(() => resolve(data), delay))

const kitchenService = {
  getDailyPlan: async (date) => {
    if (date) {
      const plan = planesData.find((p) => p.date === date)
      if (!plan) throw new Error(`Plan para ${date} no encontrado`)
      return simularAsync(plan)
    }
    const hoy = new Date().toISOString().split('T')[0]
    const plan = planesData.find((p) => p.date === hoy) || planesData[0]
    return simularAsync(plan)
  },

  getSalesHistory: async (from, to) => {
    let resultado = [...planesData]
    if (from) resultado = resultado.filter((p) => p.date >= from)
    if (to) resultado = resultado.filter((p) => p.date <= to)
    return simularAsync(resultado)
  },
}

export default kitchenService