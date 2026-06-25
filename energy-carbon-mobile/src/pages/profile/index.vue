<template>
  <view class="profile-page">
    <view class="profile-header">
      <view class="avatar-section">
        <view class="avatar">
          <image class="avatar-img" src="/static/logo.png" mode="aspectFit"></image>
        </view>
        <view class="user-info">
          <text class="user-name">{{ userInfo.username || '用户' }}</text>
          <text class="user-dept">{{ userInfo.deptName || '暂无部门' }}</text>
        </view>
      </view>
    </view>

    <view class="menu-list">
      <view class="menu-item" @click="showPasswordDialog = true">
        <view class="menu-icon">
          <uni-icons type="locked" size="22" color="#1a5f3f"></uni-icons>
        </view>
        <text class="menu-text">修改密码</text>
        <uni-icons type="right" size="16" color="#cccccc"></uni-icons>
      </view>

      <view class="menu-item" @click="handleClearCache">
        <view class="menu-icon">
          <uni-icons type="trash" size="22" color="#1a5f3f"></uni-icons>
        </view>
        <text class="menu-text">清除缓存</text>
        <uni-icons type="right" size="16" color="#cccccc"></uni-icons>
      </view>

      <view class="menu-item">
        <view class="menu-icon">
          <uni-icons type="info" size="22" color="#1a5f3f"></uni-icons>
        </view>
        <text class="menu-text">关于我们</text>
        <text class="menu-extra">v1.0.0</text>
      </view>
    </view>

    <view class="logout-section">
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </view>

    <!-- 修改密码弹窗 -->
    <view v-if="showPasswordDialog" class="modal-overlay" @click="showPasswordDialog = false">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">修改密码</text>
          <view class="modal-close" @click="showPasswordDialog = false">
            <uni-icons type="closeempty" size="18" color="#999"></uni-icons>
          </view>
        </view>
        <view class="modal-body">
          <view class="form-item">
            <text class="form-item-label">旧密码</text>
            <input class="form-item-input" v-model="pwdForm.oldPassword" type="password" password placeholder="请输入旧密码" />
          </view>
          <view class="form-item">
            <text class="form-item-label">新密码</text>
            <input class="form-item-input" v-model="pwdForm.newPassword" type="password" password placeholder="请输入新密码（6位以上）" />
          </view>
          <view class="form-item">
            <text class="form-item-label">确认密码</text>
            <input class="form-item-input" v-model="pwdForm.confirmPassword" type="password" password placeholder="请再次输入新密码" />
          </view>
        </view>
        <view class="modal-footer">
          <button class="btn-cancel" @click="showPasswordDialog = false">取消</button>
          <button class="btn-confirm" :loading="pwdLoading" @click="handleChangePassword">确认</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserInfo, logout } from '@/utils/auth.js'
import request from '@/utils/request.js'

const userInfo = ref({})
const showPasswordDialog = ref(false)
const pwdLoading = ref(false)
const pwdForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

onMounted(() => {
  userInfo.value = getUserInfo() || {}
})

const handleChangePassword = async () => {
  const { oldPassword, newPassword, confirmPassword } = pwdForm.value
  if (!oldPassword) { uni.showToast({ title: '请输入旧密码', icon: 'none' }); return }
  if (!newPassword || newPassword.length < 6) { uni.showToast({ title: '新密码至少6位', icon: 'none' }); return }
  if (newPassword !== confirmPassword) { uni.showToast({ title: '两次密码不一致', icon: 'none' }); return }

  pwdLoading.value = true
  try {
    // 调用后端用户更新接口修改密码
    const user = getUserInfo()
    if (!user || !user.id) {
      uni.showToast({ title: '未获取到用户信息', icon: 'none' })
      return
    }
    const res = await request.put(`/system/user/${user.id}`, {
      id: user.id,
      password: newPassword
    })
    if (res.code === 200) {
      uni.showToast({ title: '修改成功，请重新登录', icon: 'success' })
      showPasswordDialog.value = false
      setTimeout(() => {
        logout()
        uni.reLaunch({ url: '/pages/login/index' })
      }, 1500)
    } else {
      uni.showToast({ title: res.message || '修改失败', icon: 'none' })
    }
  } catch (e) {
    uni.showToast({ title: '修改失败', icon: 'none' })
  } finally {
    pwdLoading.value = false
  }
}

const handleClearCache = () => {
  uni.showModal({
    title: '提示',
    content: '确定清除所有缓存吗？',
    success: (res) => {
      if (res.confirm) {
        uni.clearStorageSync()
        uni.showToast({ title: '缓存已清除', icon: 'success' })
      }
    }
  })
}

const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        logout()
        uni.reLaunch({ url: '/pages/login/index' })
      }
    }
  })
}
</script>

<style scoped>
.profile-page { min-height: 100vh; background: #f5f7fa; padding-bottom: 40rpx; }
.profile-header { background: linear-gradient(135deg, #1a5f3f, #2d8b5a); padding: 60rpx 40rpx 80rpx; }
.avatar-section { display: flex; align-items: center; }
.avatar { width: 120rpx; height: 120rpx; border-radius: 60rpx; background: rgba(255,255,255,0.3); display: flex; align-items: center; justify-content: center; margin-right: 24rpx; overflow: hidden; }
.avatar-img { width: 80rpx; height: 80rpx; }
.user-name { font-size: 36rpx; font-weight: 600; color: #ffffff; display: block; }
.user-dept { font-size: 26rpx; color: rgba(255,255,255,0.8); margin-top: 8rpx; display: block; }
.menu-list { background: #ffffff; border-radius: 24rpx; margin: -40rpx 24rpx 24rpx; padding: 0 32rpx; box-shadow: 0 4rpx 24rpx rgba(0,0,0,0.06); }
.menu-item { display: flex; align-items: center; padding: 32rpx 0; border-bottom: 1rpx solid #f0f0f0; }
.menu-item:last-child { border-bottom: none; }
.menu-icon { width: 48rpx; height: 48rpx; background: #e8f5e9; border-radius: 12rpx; display: flex; align-items: center; justify-content: center; margin-right: 24rpx; }
.menu-text { flex: 1; font-size: 30rpx; color: #333; }
.menu-extra { font-size: 26rpx; color: #999; margin-right: 12rpx; }
.logout-section { padding: 0 24rpx; margin-top: 40rpx; }
.logout-btn { background: #ffffff; color: #f5222d; font-size: 32rpx; height: 96rpx; line-height: 96rpx; border-radius: 48rpx; border: none; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06); }
.logout-btn::after { border: none; }
/* 弹窗 */
.modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); z-index: 1000; display: flex; align-items: center; justify-content: center; }
.modal-content { background: #ffffff; border-radius: 24rpx; width: 80%; max-width: 600rpx; overflow: hidden; }
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: 32rpx; border-bottom: 1rpx solid #f0f0f0; }
.modal-title { font-size: 32rpx; font-weight: 600; color: #333; }
.modal-close { width: 48rpx; height: 48rpx; display: flex; align-items: center; justify-content: center; }
.modal-body { padding: 24rpx 32rpx; }
.form-item { margin-bottom: 24rpx; }
.form-item:last-child { margin-bottom: 0; }
.form-item-label { font-size: 28rpx; color: #333; margin-bottom: 12rpx; display: block; }
.form-item-input { background: #f8f9fa; border-radius: 12rpx; padding: 20rpx 24rpx; font-size: 30rpx; color: #333; }
.modal-footer { display: flex; padding: 24rpx 32rpx; gap: 24rpx; border-top: 1rpx solid #f0f0f0; }
.btn-cancel { flex: 1; background: #f0f0f0; color: #666; font-size: 30rpx; height: 80rpx; line-height: 80rpx; border-radius: 40rpx; border: none; }
.btn-cancel::after { border: none; }
.btn-confirm { flex: 1; background: linear-gradient(135deg, #1a5f3f, #2d8b5a); color: #fff; font-size: 30rpx; height: 80rpx; line-height: 80rpx; border-radius: 40rpx; border: none; }
.btn-confirm::after { border: none; }
</style>
