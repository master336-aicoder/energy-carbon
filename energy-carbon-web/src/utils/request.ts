import axios from 'axios'
import type { AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

/**
 * 请求响应数据结构
 * 与后端 Result 类对应
 */
interface ResponseData<T = any> {
  /** 响应状态码，200 表示成功 */
  code: number
  /** 响应消息 */
  message: string
  /** 响应数据 */
  data: T
}

/**
 * 创建 Axios 实例
 * 配置基础URL、超时时间、请求头等通用参数
 */
const request = axios.create({
  // 基础URL，所有请求都会自动拼接此前缀
  // 开发环境通过 Vite 代理转发到后端，生产环境应配置为实际后端地址
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  // 请求超时时间（毫秒）
  timeout: 10000,
  // 请求头配置
  headers: {
    'Content-Type': 'application/json',
  },
})

/**
 * 请求拦截器
 * 在请求发送前执行，用于添加认证Token等通用处理
 */
request.interceptors.request.use(
  (config) => {
    // 从 Pinia store 获取用户 Token
    const userStore = useUserStore()
    const token = userStore.token
    // 如果存在 Token，添加到请求头中
    if (token) {
      config.headers = config.headers || {}
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error: AxiosError) => {
    // 请求发送失败（如网络断开）
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 * 在收到响应后执行，用于统一处理响应数据和错误
 * 注意：此处不解包 response.data，API 层通过泛型获取完整 ResponseData
 */
request.interceptors.response.use(
  (response: AxiosResponse<ResponseData>) => {
    const res = response.data
    // 判断业务状态码
    if (res.code !== 200) {
      // 业务错误，显示错误消息
      ElMessage.error(res.message || '请求失败')
      // 401：未授权，清除登录状态并跳转登录页
      if (res.code === 401) {
        const userStore = useUserStore()
        userStore.logout()
        window.location.href = '/login'
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    // 返回完整的 ResponseData，API 层通过泛型获取
    return response.data as any
  },
  (error: AxiosError) => {
    // HTTP 错误（如 404、500、网络超时等）
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
