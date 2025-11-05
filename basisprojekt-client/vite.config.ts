import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
// tag::proxy-config[]
export default defineConfig({
  server: {
    host: true,
    proxy: {
      '/api': {
        target:
          process.env.VITE_TESTREPORT_ENDPOINT ||
          'http://127.0.0.1:8080',
        changeOrigin: true,
        secure: false,
      },
    },
    cors: {
      origin: ['http://127.0.0.1:8080/*'],
    },
  },
  plugins: [react()],
})
// end::proxy-config[]
