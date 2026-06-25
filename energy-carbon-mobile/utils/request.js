const getBaseURL = () => {
  return uni.getStorageSync('apiBase') || '/api'
}

const request = (options) => {
  return new Promise((resolve, reject) => {
    uni.request({
      url: getBaseURL() + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + uni.getStorageSync('token')
      },
      success: (res) => {
        if (res.statusCode === 200) {
          if (res.data.code === 401 || res.data.code === 403) {
            uni.removeStorageSync('token')
            uni.removeStorageSync('expiresIn')
            uni.removeStorageSync('userInfo')
            uni.showToast({ title: '登录已过期，请重新登录', icon: 'none' })
            setTimeout(() => {
              uni.redirectTo({ url: '/pages/login/index' })
            }, 1500)
            reject(new Error('Unauthorized'))
          } else {
            resolve(res.data)
          }
        } else if (res.statusCode === 401) {
          uni.removeStorageSync('token')
          uni.removeStorageSync('expiresIn')
          uni.removeStorageSync('userInfo')
          uni.showToast({ title: '登录已过期，请重新登录', icon: 'none' })
          setTimeout(() => {
            uni.redirectTo({ url: '/pages/login/index' })
          }, 1500)
          reject(new Error('Unauthorized'))
        } else {
          uni.showToast({ title: res.data?.message || '请求失败', icon: 'none' })
          reject(new Error(res.data?.message || 'Request failed'))
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络请求失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

request.get = (url, params) => request({ url, method: 'GET', data: params })
request.post = (url, data) => request({ url, method: 'POST', data })
request.put = (url, data) => request({ url, method: 'PUT', data })
request.delete = (url, data) => request({ url, method: 'DELETE', data })

export default request
