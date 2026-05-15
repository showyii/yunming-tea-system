import { defineStore } from 'pinia'
import { ref } from 'vue'
import { adminAuthApi } from '@/api/admin'

export const useAdminStore = defineStore('admin', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const username = ref('')

  const login = async (data) => {
    const res = await adminAuthApi.login(data)
    token.value = res.token
    username.value = res.username
    localStorage.setItem('admin_token', res.token)
  }

  const logout = () => {
    token.value = ''
    username.value = ''
    localStorage.removeItem('admin_token')
  }

  return { token, username, login, logout }
})
