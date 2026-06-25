import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

/**
 * Vite 配置文件
 * 配置开发服务器、构建选项、路径别名、代理等
 */
export default defineConfig({
  // ==================== 插件配置 ====================
  plugins: [
    // Vue 3 单文件组件支持
    vue(),
  ],

  // ==================== 路径解析配置 ====================
  resolve: {
    alias: {
      // 配置 @ 指向 src 目录，简化模块导入路径
      // 例如：import { useUserStore } from '@/stores/user'
      '@': resolve(__dirname, 'src'),
    },
  },

  // ==================== 开发服务器配置 ====================
  server: {
    // 开发服务器端口
    // 用途：本地开发时前端服务监听的端口
    // 默认值：3000
    port: 3000,
    // 启动时自动打开浏览器
    // 用途：开发时自动打开浏览器访问项目
    open: true,
    // 代理配置
    // 用途：将 /api 开头的请求代理到后端服务，解决跨域问题
    proxy: {
      '/api': {
        // 目标后端服务地址
        // 开发环境代理到本地后端服务
        target: 'http://localhost:8080',
        // 是否改变请求的 origin，设置为 true 后端可以获取到正确的请求来源
        changeOrigin: true,
        // 路径重写：将 /api 前缀去掉后转发到后端
        // 例如：/api/auth/login -> /auth/login
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },

  // ==================== 构建配置 ====================
  build: {
    // 构建输出目录
    // 用途：指定生产构建文件的输出位置
    // 默认值：dist
    outDir: 'dist',
    // 静态资源目录
    // 用途：指定静态资源（图片、字体等）的存放目录
    assetsDir: 'assets',
    // 是否生成 source map
    // 用途：用于生产环境调试，但会增加构建体积
    // 默认值：false
    // 注意事项：生产环境建议关闭以减小体积
    sourcemap: false,
    // 代码压缩配置
    minify: 'esbuild',
    // 分块策略
    // 用途：将代码分割成多个 chunk，优化加载性能
    rollupOptions: {
      output: {
        // 手动分块配置
        // 将第三方库单独打包，利用浏览器缓存
        manualChunks(id: string) {
          if (id.includes('node_modules/element-plus')) return 'element-plus'
          if (id.includes('node_modules/vue') || id.includes('node_modules/vue-router') || id.includes('node_modules/pinia')) return 'vue'
          return null
        },
      },
    },
  },

  // ==================== CSS 配置 ====================
  css: {
    // CSS 预处理器配置
    preprocessorOptions: {
      scss: {
        // 全局引入 SCSS 变量文件
        // 用途：在所有组件中自动可用这些变量，无需重复导入
        additionalData: `@use "@/styles/_variables.scss" as *;`,
      },
    },
  },

  // ==================== 依赖优化配置 ====================
  optimizeDeps: {
    include: ['vue', 'vue-router', 'pinia', 'element-plus', '@element-plus/icons-vue', 'axios', 'nprogress'],
    exclude: ['echarts'],
  },
})
