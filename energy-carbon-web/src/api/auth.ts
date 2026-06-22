import request from '@/utils/request'

export function login(data: { username: string; password: string; captcha?: string }) {
  return request.post('/auth/login', data)
}

export function logout() {
  return request.post('/auth/logout')
}

export function getUserInfo() {
  return request.get('/auth/userinfo')
}

export function refreshToken(refreshToken: string) {
  return request.post('/auth/refresh', { refreshToken })
}

export function getCaptcha() {
  return request.get('/auth/captcha')
}
