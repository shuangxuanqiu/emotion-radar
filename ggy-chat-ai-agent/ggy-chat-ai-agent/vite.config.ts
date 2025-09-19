import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(), vueDevTools()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    proxy: {
      '/api': {
        // target: 'http://localhost:8123',
        target: 'http://ddns.6010.top:12844',
        changeOrigin: true,
        secure: false,
      },
    },
  },
  define: {
    __IMAGE_DISPLAY_PREFIX__: JSON.stringify('/api/imageAnalysis/display'),
  },
})
