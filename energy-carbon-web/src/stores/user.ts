import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

/**
 * 用户 Store
 * 使用 Pinia 管理用户登录状态和用户信息
 * 支持 Token 持久化存储（localStorage）
 */
export const useUserStore = defineStore('user', () => {
  // ==================== State ====================

  /**
   * 用户认证 Token
   * 从 localStorage 恢复持久化的 Token
   */
  const token = ref<string>(localStorage.getItem('token') || '')

  /**
   * 用户信息对象
   */
  const userInfo = ref<any>(null)

  // ==================== Getters ====================

  /**
   * 是否已登录
   * 根据 Token 是否存在判断登录状态
   */
  const isLoggedIn = computed(() => !!token.value)

  // ==================== Actions ====================

  /**
   * 设置用户 Token
   * 同时持久化到 localStorage
   *
   * @param newToken 新的 Token 字符串
   */
  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  /**
   * 设置用户信息
   *
   * @param info 用户信息对象
   */
  const setUserInfo = (info: any) => {
    userInfo.value = info
  }

  /**
   * 用户登出
   * 清除 Token 和用户信息，并从 localStorage 移除
   */
  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  // 返回所有状态和操作
  return {
    token,
    userInfo,
    isLoggedIn,
    setToken,
    setUserInfo,
    logout,
  }
})
