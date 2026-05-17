// Vite 构建配置文件
// 导入 Vite 的 defineConfig 工具函数，提供 TypeScript 类型提示
import { defineConfig } from 'vite'
// 导入 Vite 的 Vue 3 插件，支持 .vue 单文件组件
import vue from '@vitejs/plugin-vue'
// 导入 Node.js URL 处理工具：fileURLToPath 将文件 URL 转为路径，URL 用于构造 URL 对象
import { fileURLToPath, URL } from 'node:url'
// 导入 unplugin-auto-import 插件，自动导入 Vue/VueRouter 等常用 API，无需手动 import
import AutoImport from 'unplugin-auto-import/vite'
// 导入 unplugin-vue-components 插件，自动按需导入 Element Plus 组件
import Components from 'unplugin-vue-components/vite'
// 导入 Element Plus 的组件解析器，配合 unplugin-vue-components 使用
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// 使用 defineConfig 导出 Vite 配置（提供更好的 IDE 智能提示）
export default defineConfig({
  // ---------- 插件配置 ----------
  plugins: [
    // 启用 Vue 3 SFC（单文件组件）编译插件
    vue(),
    // 配置自动导入：无需手动 import ref/reactive/useRouter 等
    AutoImport({
      imports: ['vue', 'vue-router'], // 自动导入 vue 和 vue-router 的 API
      resolvers: [ElementPlusResolver()] // 自动导入 Element Plus 组件的样式
    }),
    // 配置组件自动按需导入：模板中使用的 Element Plus 组件会自动注册
    Components({
      resolvers: [ElementPlusResolver()] // 解析 Element Plus 组件
    })
  ],
  // ---------- 路径别名 ----------
  resolve: {
    alias: {
      // 配置 @ 别名指向 src 目录，导入时可用 '@/api/xxx' 代替 '../../api/xxx'
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  // ---------- 构建配置 ----------
  build: {
    rollupOptions: {
      output: {
        // 手动代码分割：将大型依赖拆分成独立的 chunk，减少首屏加载体积
        manualChunks(id) {
          // 将 ECharts 图表库单独打包（体积大，懒加载可优化首屏）
          if (id.includes('node_modules/echarts')) {
            return 'vendor-echarts'
          }
          // 将 Vue 核心库（vue/vue-router/pinia）打包在一起
          if (id.includes('node_modules/vue') || id.includes('node_modules/vue-router') || id.includes('node_modules/pinia')) {
            return 'vendor-vue'
          }
          // 将 lodash/floating-ui 等工具库单独打包
          if (id.includes('node_modules/lodash') || id.includes('node_modules/@floating-ui')) {
            return 'vendor-ui-utils'
          }
        }
      }
    }
  },
  // ---------- 开发服务器配置 ----------
  server: {
    port: 3000, // 开发服务器端口，默认 3000
    proxy: {
      // 配置 API 代理：将以 /api 开头的请求转发到后端 Spring Boot 服务器
      '/api': {
        target: 'http://localhost:8080', // 后端 Spring Boot 地址
        changeOrigin: true // 修改请求头中的 Origin 为目标地址，避免跨域问题
      }
    }
  }
})
