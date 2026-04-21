/// <reference types="vitest/config" />
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import { TanStackRouterVite } from '@tanstack/router-plugin/vite';

// https://vite.dev/config/
// tag::proxy-config[]
export default defineConfig({
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './src/test/setup.ts',
  },
  server: {
    host: true,
    proxy: {
      '/api': {
        target: process.env.VITE_TESTREPORT_ENDPOINT || 'http://127.0.0.1:8080',
        changeOrigin: true,
        secure: false,
      },
    },
    cors: {
      origin: ['http://127.0.0.1:8080/*'],
    },
  },
  plugins: [TanStackRouterVite(), react()],
});
// end::proxy-config[]
