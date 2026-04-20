import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  //base: '/vue3-blog/',
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  //代理配置
  server: {
    proxy: {
      '/uploaded-images': {
        target: 'http://localhost:8008',
        changeOrigin: true,
        secure: false
      }
    }
  }
})
