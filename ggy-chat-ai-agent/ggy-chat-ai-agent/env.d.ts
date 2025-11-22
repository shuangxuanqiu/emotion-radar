/// <reference types="vite/client" />

declare const __IMAGE_DISPLAY_PREFIX__: string

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}
