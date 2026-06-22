/**
 * 全局加载状态管理
 * 提供统一的 loading 状态控制
 */

import { ref } from 'vue'

// 全局加载状态
const globalLoading = ref(false)
// 加载计数器（支持多个请求同时loading）
let loadingCount = 0
// 加载提示文本
const loadingText = ref('加载中...')

/**
 * 显示全局加载
 */
export function showLoading(text?: string): void {
  if (text) {
    loadingText.value = text
  }
  loadingCount++
  globalLoading.value = true
}

/**
 * 隐藏全局加载
 */
export function hideLoading(): void {
  loadingCount--
  if (loadingCount <= 0) {
    loadingCount = 0
    globalLoading.value = false
    loadingText.value = '加载中...'
  }
}

/**
 * 重置加载状态
 */
export function resetLoading(): void {
  loadingCount = 0
  globalLoading.value = false
  loadingText.value = '加载中...'
}

/**
 * 以异步方式执行并自动管理loading状态
 */
export async function withLoading<T>(fn: () => Promise<T>, text?: string): Promise<T> {
  showLoading(text)
  try {
    return await fn()
  } finally {
    hideLoading()
  }
}

export function useLoading() {
  return {
    globalLoading,
    loadingText,
    showLoading,
    hideLoading,
    resetLoading,
    withLoading
  }
}
