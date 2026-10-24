import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    strictPort: true,
    // Aceita qualquer subdomínio de localhost, como imobiliaria-a.localhost
    allowedHosts: ['.localhost'],
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        // Mantém o Host original (com o subdomínio do tenant) ao repassar para o Spring
        changeOrigin: false,
        // Adiciona os cabeçalhos X-Forwarded-*, como o proxy de produção fará
        xfwd: true,
      },
    },
  },
})
