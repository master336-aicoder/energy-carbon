<template>
  <view class="profile-page">
    <nav-bar :title="'个人中心'"></nav-bar>
    <view class="profile-content" :style="{ paddingTop: safeAreaTop + 'px' }">
      <!-- 用户信息卡片 -->
      <view class="user-card">
        <view class="user-avatar">
          <uni-icons type="person-filled" size="60" color="#1a5f3f"></uni-icons>
        </view>
        <view class="user-info">
          <text class="user-name">{{ userInfo.nickname || userInfo.username || '管理员' }}</text>
          <text class="user-dept">{{ userInfo.deptName || '能碳管理部' }}</text>
        </view>
      </view>

      <!-- 功能菜单 -->
      <view class="menu-list">
        <view class="menu-group">
          <view class="menu-item" @click="changePassword">
            <view class="menu-left">
              <view class="menu-icon lock">
                <uni-icons type="locked" size="22" color="#ffffff"></uni-icons>
              </view>
              <text class="menu-title">修改密码</text>
            </view>
            <uni-icons type="right" size="16" color="#cccccc"></uni-icons>
          </view>

          <view class="menu-item" @click="clearCache">
            <view class="menu-left">
              <view class="menu-icon clear">
                <uni-icons type="trash" size="22" color="#ffffff"></uni-icons>
              </view>
              <text class="menu-title">清除缓存</text>
            </view>
            <view class="menu-right">
              <text class="cache-size">{{ cacheSize }}</text>
              <uni-icons type="right" size="16" color="#cccccc"></uni-icons>
            </view>
          </view>

          <view class="menu-item" @click="showAbout">
            <view class="menu-left">
              <view class="menu-icon about">
                <uni-icons type="info" size="22" color="#ffffff"></uni-icons>
              </view>
              <text class="menu-title">关于我们</text>
            </view>
            <uni-icons type="right" size="16" color="#cccccc"></uni-icons>
          </view>
        </view>

        <view class="menu-group">
          <view class="menu-item logout" @click="confirmLogout">
            <view class="menu-left">
              <view class="menu-icon logout-icon">
                <uni-icons type="undo" size="22" color="#ffffff"></uni-icons>
              </view>
              <text class="menu-title logout-text">退出登录</text>
            </view>
            <uni-icons type="right" size="16" color="#cccccc"></uni-icons>
          </view>
        </view>
      </view>
    </view>

    <!-- 关于弹窗 -->
    <uni-popup ref="aboutPopup" type="center">
      <view class="about-popup">
        <image class="about-logo" src="/static/logo.png" mode="aspectFit"></image>
        <text class="about-name">能碳数智化</text>
        <text class="about-version">版本 1.0.0</text>
        <text class="about-desc">智慧能源管理平台移动端\n助力企业绿色低碳转型</text>
        <button class="about-close" @click="closeAbout">关闭</button>
      </view>
    </uni-popup>

    <!-- 修改密码弹窗 -->
    <uni-popup ref="pwdPopup" type="center">
      <view class="pwd-popup">
        <text class="pwd-title">修改密码</text>
        <input class="pwd-input" v-model="pwdForm.oldPassword" type="password" placeholder="原密码" />
        <input class="pwd-input" v-model="pwdForm.newPassword" type="password" placeholder="新密码" />
        <input class="pwd-input" v-model="pwdForm.confirmPassword" type="password" placeholder="确认新密码" />
        <view class="pwd-actions">
          <button class="pwd-btn cancel" @click="closePwd">取消</button>
          <button class="pwd-btn confirm" @click="doChangePassword">确认</button>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { logout, clearAuth } from '@/utils/auth.js'

const safeAreaTop = ref(uni.getSystemInfoSync().statusBarHeight + 44)
const userInfo = ref({})
const cacheSize = ref('0 MB')
const aboutPopup = ref(null)
const pwdPopup = ref(null)

const pwdForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const loadUserInfo = () => {
  const info = uni.getStorageSync('userInfo')
  if (info) {
    try {
      userInfo.value = JSON.parse(info)
    } catch (e) {
      userInfo.value = {}
    }
  }
}

const getCacheSize = () => {
  // 简易估算
  const keys = uni.getStorageInfoSync().keys || []
  let size = 0
  keys.forEach(key => {
    const v = uni.getStorageSync(key)
    size += JSON.stringify(v).length
  })
  cacheSize.value = (size / 1024 / 1024).toFixed(2) + ' MB'
}

const clearCache = () => {
  uni.showModal({
    title: '提示',
    content: '确定要清除缓存吗？',
    success: (res) => {
      if (res.confirm) {
        uni.clearStorageSync()
        uni.showToast({ title: '清除成功', icon: 'success' })
        cacheSize.value = '0 MB'
      }
    }
  })
}

const showAbout = () => {
  aboutPopup.value.open()
}

const closeAbout = () => {
  aboutPopup.value.close()
}

const changePassword = () => {
  pwdForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  pwdPopup.value.open()
}

const closePwd = () => {
  pwdPopup.value.close()
}

const doChangePassword = () => {
  if (!pwdForm.value.oldPassword || !pwdForm.value.newPassword || !pwdForm.value.confirmPassword) {
    uni.showToast({ title: '请填写完整', icon: 'none' })
    return
  }
  if (pwdForm.value.newPassword !== pwdForm.value.confirmPassword) {
    uni.showToast({ title: '两次密码不一致', icon: 'none' })
    return
  }
  uni.showToast({ title: '修改成功', icon: 'success' })
  closePwd()
}

const confirmLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await logout()
        } catch (e) {}
        clearAuth()
        uni.showToast({ title: '已退出', icon: 'success' })
        setTimeout(() => {
          uni.redirectTo({ url: '/pages/login/index' })
        }, 800)
      }
    }
  })
}

onMounted(() => {
  loadUserInfo()
  getCacheSize()
})

onShow(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: #f5f7fa;
}
.profile-content {
  padding: 24rpx;
}
.user-card {
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  border-radius: 20rpx;
  padding: 48rpx 40rpx;
  display: flex;
  align-items: center;
  margin-bottom: 32rpx;
  box-shadow: 0 8rpx 32rpx rgba(26, 95, 63, 0.2);
}
.user-avatar {
  width: 120rpx;
  height: 120rpx;
  background: #ffffff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 32rpx;
}
.user-info {
  display: flex;
  flex-direction: column;
}
.user-name {
  color: #ffffff;
  font-size: 38rpx;
  font-weight: 700;
}
.user-dept {
  color: rgba(255, 255, 255, 0.85);
  font-size: 26rpx;
  margin-top: 8rpx;
}
.menu-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}
.menu-group {
  background: #ffffff;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
  overflow: hidden;
}
.menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28rpx 32rpx;
  border-bottom: 1rpx solid #f5f5f5;
}
.menu-item:last-child {
  border-bottom: none;
}
.menu-left {
  display: flex;
  align-items: center;
}
.menu-icon {
  width: 56rpx;
  height: 56rpx;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
}
.menu-icon.lock {
  background: linear-gradient(135deg, #1890ff, #36cfc9);
}
.menu-icon.clear {
  background: linear-gradient(135deg, #faad14, #ff7a45);
}
.menu-icon.about {
  background: linear-gradient(135deg, #722ed1, #eb2f96);
}
.menu-icon.logout-icon {
  background: #f5222d;
}
.menu-title {
  font-size: 30rpx;
  color: #333333;
}
.menu-title.logout-text {
  color: #f5222d;
}
.menu-right {
  display: flex;
  align-items: center;
  gap: 8rpx;
}
.cache-size {
  font-size: 26rpx;
  color: #999999;
}
.about-popup {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 48rpx;
  width: 560rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.about-logo {
  width: 140rpx;
  height: 140rpx;
  margin-bottom: 20rpx;
}
.about-name {
  font-size: 36rpx;
  font-weight: 700;
  color: #333333;
}
.about-version {
  font-size: 26rpx;
  color: #999999;
  margin-top: 8rpx;
}
.about-desc {
  font-size: 26rpx;
  color: #666666;
  margin-top: 24rpx;
  text-align: center;
  line-height: 1.6;
}
.about-close {
  margin-top: 32rpx;
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  color: #ffffff;
  font-size: 30rpx;
  height: 80rpx;
  line-height: 80rpx;
  border-radius: 40rpx;
  width: 200rpx;
  border: none;
}
.about-close::after {
  border: none;
}
.pwd-popup {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 40rpx;
  width: 600rpx;
}
.pwd-title {
  font-size: 34rpx;
  font-weight: 700;
  color: #333333;
  text-align: center;
  display: block;
  margin-bottom: 32rpx;
}
.pwd-input {
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 20rpx 24rpx;
  font-size: 30rpx;
  margin-bottom: 20rpx;
}
.pwd-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 12rpx;
}
.pwd-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 30rpx;
  border-radius: 40rpx;
  border: none;
}
.pwd-btn::after {
  border: none;
}
.pwd-btn.cancel {
  background: #f0f0f0;
  color: #666666;
}
.pwd-btn.confirm {
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  color: #ffffff;
}
</style>
