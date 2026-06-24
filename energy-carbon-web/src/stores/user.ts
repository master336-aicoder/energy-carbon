import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo as getUserInfoApi } from '@/api/auth'
import type { UserInfo } from '@/types'

/**
 * 用户 Store
 * 使用 Pinia 管理用户登录状态和用户信息
 * 支持 Token 持久化存储（localStorage）
 */
export const useUserStore = defineStore('user', () => {
  // ==================== State ====================

  /** 用户认证 Token */
  const token = ref<string>(localStorage.getItem('token') || '')

  /** 用户信息对象 */
  const userInfo = ref<UserInfo | null>(null)

  // ==================== Getters ====================

  /** 是否已登录 */
  const isLoggedIn = computed(() => !!token.value)

  // ==================== Actions ====================

  /**
   * 登录
   * 调用登录接口，保存 Token
   */
  const login = async (loginForm: { username: string; password: string }) => {
    const res: any = await loginApi(loginForm)
    // 后端返回 { code: 200, data: { accessToken: "xxx", refreshToken: "xxx", expiresIn: 7200 } }
    const accessToken = res.data?.accessToken || res.data?.token || res.access_token
    if (accessToken) {
      setToken(accessToken)
    } else if (typeof res.data === 'string') {
      setToken(res.data)
    }
  }

  /**
   * 获取用户信息
   * 后端 /auth/userinfo 返回 { username, nickname, permissions }
   */
  const fetchUserInfo = async () => {
    const res: any = await getUserInfoApi()
    const data = res.data
    if (data) {
      userInfo.value = {
        id: data.id || 0,
        username: data.username || '',
        nickname: data.nickname || data.username || '',
        avatar: data.avatar || '',
        roles: data.roles || [],
        permissions: data.permissions || [],
        menus: data.menus || [],
      }
    }
  }

  /** 设置用户 Token */
  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  /** 设置用户信息 */
  const setUserInfo = (info: UserInfo | null) => {
    userInfo.value = info
  }

  /** 用户登出 */
  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    login,
    fetchUserInfo,
    setToken,
    setUserInfo,
    logout,
  }
})
