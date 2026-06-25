import request from './request.js'

export const login = (username, password) => {
  return request.post('/auth/login', { username, password })
}

export const getUserInfo = () => {
  return request.get('/auth/userinfo')
}

export const logout = () => {
  return request.post('/auth/logout')
}

export const setToken = (token, expiresIn) => {
  uni.setStorageSync('token', token)
  const expireTime = Date.now() + expiresIn * 1000
  uni.setStorageSync('expiresIn', expireTime.toString())
}

export const clearAuth = () => {
  uni.removeStorageSync('token')
  uni.removeStorageSync('expiresIn')
  uni.removeStorageSync('userInfo')
}

export const isLoggedIn = () => {
  const token = uni.getStorageSync('token')
  const expiresIn = uni.getStorageSync('expiresIn')
  if (!token || !expiresIn) return false
  return Date.now() < parseInt(expiresIn)
}
