// Vue 3 应用入口文件
// 导入 Vue 3 的 createApp 函数，用于创建应用实例
import { createApp } from 'vue'
// 导入 Pinia 状态管理库的创建函数
import { createPinia } from 'pinia'
// 导入 Element Plus UI 组件库
import ElementPlus from 'element-plus'
// 导入 Element Plus 图标库的所有图标组件
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
// 导入 Element Plus 的中文语言包
import zhCn from 'element-plus/es/locale/lang/zh-cn'
// 导入根组件 App.vue
import App from './App.vue'
// 导入 Vue Router 路由实例
import router from './router'
// 导入 Element Plus 全局样式
import 'element-plus/dist/index.css'
// 导入项目自定义全局样式
import './styles/global.css'

// 创建 Vue 3 应用实例，传入根组件 App
const app = createApp(App)
// 注册 Pinia 状态管理插件
app.use(createPinia())
// 注册 Element Plus UI 组件库，并设置为中文语言
app.use(ElementPlus, {
  locale: zhCn // 使用中文语言包，组件默认文本将显示为中文
})
// 注册 Vue Router 路由插件
app.use(router)

// 遍历 Element Plus 图标库中的所有图标组件
// 将它们全部注册为全局组件，方便在模板中直接使用
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component) // 以图标名为组件名注册全局组件
}

// 将 Vue 应用挂载到 index.html 中 id="app" 的 DOM 元素上
app.mount('#app')
