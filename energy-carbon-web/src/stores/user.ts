import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo } from '@/api/auth'
import { local, StorageKeys } from '@/utils/storage'
import { resetRouter } from '@/router'
import type { UserInfo, Menu } from '@/types'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)
  const permissions = ref<string[]>([])
  const menus = ref<Menu[]>([])

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
    const data = res.data
    userInfo.value = {
      id: data.id,
      username: data.username,
      nickname: data.nickname,
      avatar: data.avatar,
      email: data.email,
      phone: data.phone,
      deptName: data.deptName,
      roles: data.roles || [],
      permissions: data.permissions || [],
      menus: data.menus || []
    }
    permissions.value = data.permissions || []
    menus.value = data.menus || []

    // 缓存用户数据
    local.set(StorageKeys.USER_INFO, userInfo.value)
    local.set(StorageKeys.USER_MENUS, menus.value)
    local.set(StorageKeys.USER_PERMISSIONS, permissions.value)

    return data
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    permissions.value = []
    menus.value = []
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    local.remove(StorageKeys.USER_INFO)
    local.remove(StorageKeys.USER_MENUS)
    local.remove(StorageKeys.USER_PERMISSIONS)
    resetRouter()
  }

  function hasPermission(perm: string) {
    return permissions.value.includes(perm)
  }

  function hasRole(role: string) {
    return userInfo.value?.roles.includes(role) || false
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
    hasPermission,
    hasRole
  }
})
