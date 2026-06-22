import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<any>(null)
  const permissions = ref<string[]>([])
  const menus = ref<any[]>([])

  const isLoggedIn = computed(() => !!token.value)

  async function login(credentials: { username: string; password: string; captcha?: string }) {
    const res = await loginApi(credentials)
    token.value = res.data.accessToken
    localStorage.setItem('token', res.data.accessToken)
    localStorage.setItem('refreshToken', res.data.refreshToken)
    return res
  }

  async function fetchUserInfo() {
    const res = await getUserInfo()
    userInfo.value = res.data
    permissions.value = res.data.permissions || []
    menus.value = res.data.menus || []
    return res.data
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    permissions.value = []
    menus.value = []
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
  }

  function hasPermission(perm: string) {
    return permissions.value.includes(perm)
  }

  return {
    token,
    userInfo,
    permissions,
    menus,
    isLoggedIn,
    login,
    fetchUserInfo,
    logout,
    hasPermission
  }
})
