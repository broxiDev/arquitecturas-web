import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const apiUrl = new URL(env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1')

  return {
    plugins: [react()],
    server: {
      proxy: {
        [apiUrl.pathname]: {
          target: apiUrl.origin,
          changeOrigin: true,
          rewrite: (path) => path.replace(apiUrl.pathname, ''),
        },
      },
    },
  }
})
