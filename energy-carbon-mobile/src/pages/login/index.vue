<template>
  <view class="login-page">
    <view class="login-bg">
      <view class="login-header">
        <image class="logo" src="/static/logo.png" mode="aspectFit"></image>
        <text class="app-name">能碳数智化</text>
        <text class="app-slogan">智慧能源 · 绿色未来</text>
      </view>
    </view>

    <view class="login-form">
      <view class="form-item">
        <uni-icons type="person" size="20" color="#999999"></uni-icons>
        <input
          class="form-input"
          v-model="form.username"
          placeholder="请输入用户名"
          placeholder-class="placeholder"
        />
      </view>

      <view class="form-item">
        <uni-icons type="locked" size="20" color="#999999"></uni-icons>
        <input
          class="form-input"
          v-model="form.password"
          type="password"
          placeholder="请输入密码"
          placeholder-class="placeholder"
          @confirm="handleLogin"
        />
      </view>

      <button
        class="login-btn"
        :loading="loading"
        :disabled="loading"
        @click="handleLogin"
      >
        {{ loading ? '登录中...' : '登 录' }}
      </button>

      <view class="login-tips">
        <text>默认账号：admin / 123456</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { login, setToken, getUserInfo } from '@/utils/auth.js'

const form = reactive({
  username: '',
  password: ''
})

const loading = ref(false)

const handleLogin = async () => {
  if (!form.username.trim()) {
    uni.showToast({ title: '请输入用户名', icon: 'none' })
    return
  }
  if (!form.password.trim()) {
    uni.showToast({ title: '请输入密码', icon: 'none' })
    return
  }

  loading.value = true
  try {
    const res = await login(form.username, form.password)
    if (res.code === 200 && res.data) {
      setToken(res.data.accessToken, res.data.expiresIn || 7200)
      // 获取用户信息
      const userRes = await getUserInfo()
      if (userRes.code === 200 && userRes.data) {
        uni.setStorageSync('userInfo', JSON.stringify(userRes.data))
      }
      uni.showToast({ title: '登录成功', icon: 'success' })
      setTimeout(() => {
        uni.switchTab({ url: '/pages/home/index' })
      }, 800)
    } else {
      uni.showToast({ title: res.message || '登录失败', icon: 'none' })
    }
  } catch (e) {
    console.error(e)
    uni.showToast({ title: '登录失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: #f5f7fa;
}
.login-bg {
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  padding: 120rpx 0 100rpx;
  border-radius: 0 0 60rpx 60rpx;
}
.login-header {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.logo {
  width: 160rpx;
  height: 160rpx;
  border-radius: 24rpx;
  background: #ffffff;
  margin-bottom: 24rpx;
}
.app-name {
  color: #ffffff;
  font-size: 48rpx;
  font-weight: 700;
}
.app-slogan {
  color: rgba(255, 255, 255, 0.8);
  font-size: 28rpx;
  margin-top: 12rpx;
}
.login-form {
  margin: -40rpx 40rpx 0;
  background: #ffffff;
  border-radius: 24rpx;
  padding: 60rpx 40rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
}
.form-item {
  display: flex;
  align-items: center;
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 20rpx 24rpx;
  margin-bottom: 24rpx;
}
.form-input {
  flex: 1;
  margin-left: 16rpx;
  font-size: 30rpx;
  color: #333333;
}
.placeholder {
  color: #bbbbbb;
}
.login-btn {
  margin-top: 40rpx;
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  color: #ffffff;
  font-size: 34rpx;
  height: 96rpx;
  line-height: 96rpx;
  border-radius: 48rpx;
  border: none;
}
.login-btn::after {
  border: none;
}
.login-tips {
  text-align: center;
  margin-top: 32rpx;
  color: #999999;
  font-size: 24rpx;
}
</style>
