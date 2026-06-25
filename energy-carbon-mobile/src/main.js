import { createSSRApp } from 'vue'
import App from './App.vue'
import uCharts from '@qiun/ucharts'

export function createApp() {
  const app = createSSRApp(App)
  // 全局挂载 uCharts
  app.config.globalProperties.$uCharts = uCharts
  return {
    app
  }
}
