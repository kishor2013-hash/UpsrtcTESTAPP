import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  // Configured with './' to allow hosting on any root, subfolder or subdomain on Hostinger/Apache
  base: './',
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    sourcemap: false
  },
  server: {
    host: '0.0.0.0',
    port: 3000
  }
});
