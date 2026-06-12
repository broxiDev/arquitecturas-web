import recomendacionesData from '../mocks/recommendations.json'

const SIMULAR_DELAY = 300

const simularAsync = (data, delay = SIMULAR_DELAY) =>
  new Promise((resolve) => setTimeout(() => resolve(data), delay))

const recommendationService = {
  getRecommendations: async (userId) => {
    if (userId) {
      const rec = recomendacionesData.find((r) => r.userId === userId)
      if (!rec) throw new Error(`Recomendaciones para usuario ${userId} no encontradas`)
      return simularAsync(rec)
    }
    return simularAsync(recomendacionesData[0])
  },
}

export default recommendationService