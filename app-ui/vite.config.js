import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig(({command, mode, ssrBuild}) => {

  let base;
  switch (mode) {
    case "production":
      base = "/apl-event-finder/";
      break;
    case "development":
      base = "/";
      break;
    default:
      throw new Error("unknown mode: " + mode);
  }

  return {
    plugins: [
      vue(),
    ],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      }
    },
    build: {
      outDir: "build/dist"
    },
    base: base
  }
});
