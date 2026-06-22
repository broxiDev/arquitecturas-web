import axios from 'axios'

const httpClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
})

httpClient.interceptors.request.use(
  (config) => {
    const token = null
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

httpClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // preparado para redirigir a /login en authStore futuro
    }
    return Promise.reject(error)
  },
)

export default httpClient
