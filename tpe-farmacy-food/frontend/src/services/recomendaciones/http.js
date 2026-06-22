import httpClient from '../../lib/httpClient'

const recommendationService = {
  getRecommendations: async (userId) => {
    // GET /api/v1/recomendaciones/{userId}
    const response = await httpClient.get(`/recomendaciones/${userId}`)
    return response.data
  },
}

export default recommendationService
