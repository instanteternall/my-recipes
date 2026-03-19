import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { User, LoginRequest, RegisterRequest } from '@/types/auth'
import { login as loginApi, register as registerApi, getCurrentUser as getCurrentUserApi } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const user = ref<User | null>(null)

  const login = async (data: LoginRequest) => {
    const res = await loginApi(data)
    token.value = res.token
    user.value = res.user
    localStorage.setItem('token', res.token)
    return res
  }

  const register = async (data: RegisterRequest) => {
    const res = await registerApi(data)
    return res
  }

  const getCurrentUser = async () => {
    const res = await getCurrentUserApi()
    user.value = res
    return res
  }

  const logout = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
  }

  const isAuthenticated = () => {
    return !!token.value
  }

  return {
    token,
    user,
    login,
    register,
    getCurrentUser,
    logout,
    isAuthenticated
  }
})
