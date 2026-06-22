import httpClient from '../../lib/httpClient'

const kitchenService = {
  getDailyPlan: async (date) => {
    // GET /api/v1/cocina/plan-diario?date=...
    const response = await httpClient.get('/cocina/plan-diario', { params: { date } })
    return response.data
  },

  getSalesHistory: async (from, to) => {
    // GET /api/v1/cocina/historial-ventas?from=...&to=...
    const response = await httpClient.get('/cocina/historial-ventas', { params: { from, to } })
    return response.data
  },
}

export default kitchenService
