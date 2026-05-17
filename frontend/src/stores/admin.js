// Pinia 状态管理 - 管理员认证状态
// 导入 Pinia 的 defineStore 函数，用于定义状态仓库
import { defineStore } from 'pinia'
// 导入 Vue 3 的 ref 响应式API
import { ref } from 'vue'
// 导入管理员认证 API 接口模块
import { adminAuthApi } from '@/api/admin'

/**
 * 管理员状态仓库
 * 管理管理员登录后的 Token 和用户名状态
 * 使用 Pinia 的 Composition API 风格（类似 Vue 3 setup）
 */
export const useAdminStore = defineStore('admin', () => {
  // 管理员 JWT Token：从 localStorage 初始化（刷新页面后保持登录状态）
  const token = ref(localStorage.getItem('admin_token') || '')
  // 管理员用户名
  const username = ref('')

  /**
   * 管理员登录
   * 调用后端登录 API，获取 Token 并保存到状态和 localStorage
   * @param {Object} data - 登录表单数据 { username, password }
   */
  const login = async (data) => {
    // 调用管理后台登录 API
    const res = await adminAuthApi.login(data)
    token.value = res.token // 将 Token 保存到响应式状态
    username.value = res.username // 保存用户名到状态
    // 持久化存储 Token 到浏览器 localStorage，刷新页面不丢失登录状态
    localStorage.setItem('admin_token', res.token)
  }

  /**
   * 管理员退出登录
   * 清空状态和 localStorage 中的 Token
   */
  const logout = () => {
    token.value = '' // 清空 Token 状态
    username.value = '' // 清空用户名
    // 移除 localStorage 中保存的 Token
    localStorage.removeItem('admin_token')
  }

  // 导出对外暴露的状态和方法
  return { token, username, login, logout }
})
