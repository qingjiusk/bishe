import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, register as registerApi, getUserInfo as getUserInfoApi } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUser = (newUser) => {
    user.value = newUser
    localStorage.setItem('user', JSON.stringify(newUser))
  }

  const logout = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  const login = async (username, password) => {
    const res = await loginApi(username, password)
    if (res.code === 200) {
      setToken(res.data.token)
      setUser(res.data.user)
      return true
    }
    return false
  }

  const register = async (userData) => {
    const res = await registerApi(userData)
    if (res.code === 200) {
      setToken(res.data.token)
      setUser(res.data.user)
      return true
    }
    return false
  }

  const getUserInfo = async () => {
    const res = await getUserInfoApi()
    if (res.code === 200) {
      setUser(res.data)
      return res.data
    }
    return null
  }

  return {
    token,
    user,
    setToken,
    setUser,
    logout,
    login,
    register,
    getUserInfo
  }
})