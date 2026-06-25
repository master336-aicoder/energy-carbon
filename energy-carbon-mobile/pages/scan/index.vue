<template>
  <view class="scan-page">
    <nav-bar title="扫码录入" showBack></nav-bar>
    <view class="scan-content" :style="{ paddingTop: safeAreaTop + 'px' }">
      <view class="scan-card">
        <uni-icons type="scan" size="80" color="#1a5f3f"></uni-icons>
        <text class="scan-title">扫码识别</text>
        <text class="scan-desc">点击按钮扫描设备二维码，快速录入数据</text>
        <button class="scan-btn" @click="startScan">
          <uni-icons type="scan" size="20" color="#ffffff"></uni-icons>
          <text>开始扫码</text>
        </button>
      </view>

      <view v-if="scanResult" class="result-card">
        <text class="result-title">扫描结果</text>
        <view class="result-item">
          <text class="result-label">内容：</text>
          <text class="result-value">{{ scanResult }}</text>
        </view>
        <view class="result-actions">
          <button class="action-btn primary" @click="goToEntry">去录入</button>
          <button class="action-btn" @click="scanResult = ''">重新扫描</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const safeAreaTop = ref(uni.getSystemInfoSync().statusBarHeight + 44)
const scanResult = ref('')

const startScan = () => {
  uni.scanCode({
    onlyFromCamera: true,
    scanType: ['qrCode', 'barCode'],
    success: (res) => {
      scanResult.value = res.result
    },
    fail: (err) => {
      if (err.errMsg !== 'scanCode:fail cancel') {
        uni.showToast({ title: '扫码失败', icon: 'none' })
      }
    }
  })
}

const goToEntry = () => {
  uni.navigateTo({
    url: '/pages/entry/form?scanData=' + encodeURIComponent(scanResult.value)
  })
}
</script>

<style scoped>
.scan-page {
  min-height: 100vh;
  background: #f5f7fa;
}
.scan-content {
  padding: 40rpx;
}
.scan-card {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 80rpx 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.06);
}
.scan-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #333333;
  margin-top: 24rpx;
}
.scan-desc {
  font-size: 26rpx;
  color: #999999;
  margin-top: 12rpx;
  text-align: center;
}
.scan-btn {
  margin-top: 48rpx;
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  color: #ffffff;
  font-size: 32rpx;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  padding: 0 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  border: none;
}
.scan-btn::after {
  border: none;
}
.result-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-top: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}
.result-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 20rpx;
  display: block;
}
.result-item {
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
}
.result-label {
  color: #666666;
  font-size: 26rpx;
}
.result-value {
  color: #1a5f3f;
  font-size: 28rpx;
  font-weight: 600;
  word-break: break-all;
}
.result-actions {
  display: flex;
  gap: 20rpx;
}
.action-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 30rpx;
  border-radius: 40rpx;
  background: #f0f0f0;
  color: #666666;
  border: none;
}
.action-btn::after {
  border: none;
}
.action-btn.primary {
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  color: #ffffff;
}
</style>
