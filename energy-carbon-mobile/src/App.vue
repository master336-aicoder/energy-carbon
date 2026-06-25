<script setup>
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'

onLaunch(() => {
  console.log('App Launch')
  // 检查登录状态
  const token = uni.getStorageSync('token')
  const expiresIn = uni.getStorageSync('expiresIn')
  if (token && expiresIn) {
    const now = Date.now()
    if (now >= parseInt(expiresIn)) {
      // token 已过期
      uni.removeStorageSync('token')
      uni.removeStorageSync('expiresIn')
      uni.removeStorageSync('userInfo')
      uni.showToast({ title: '登录已过期，请重新登录', icon: 'none' })
      uni.redirectTo({ url: '/pages/login/index' })
    }
  }
})

onShow(() => {
  console.log('App Show')
})

onHide(() => {
  console.log('App Hide')
})
</script>

<style lang="scss">
@import "uni.scss";
page {
  background-color: #f5f7fa;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
}
</style>
