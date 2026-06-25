<template>
  <view class="kpi-card" :style="{ background: bgColor }">
    <view class="kpi-icon">
      <uni-icons :type="icon" size="28" color="#ffffff"></uni-icons>
    </view>
    <view class="kpi-info">
      <text class="kpi-value">{{ value }}</text>
      <text class="kpi-label">{{ label }}</text>
    </view>
  </view>
</template>

<script setup>
const props = defineProps({
  icon: {
    type: String,
    default: 'star'
  },
  value: {
    type: [String, Number],
    default: '0'
  },
  label: {
    type: String,
    default: ''
  },
  bgColor: {
    type: String,
    default: 'linear-gradient(135deg, #1a5f3f, #2d8b5a)'
  }
})
</script>

<style scoped>
.kpi-card {
  border-radius: 16rpx;
  padding: 24rpx;
  display: flex;
  align-items: center;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
}
.kpi-icon {
  width: 80rpx;
  height: 80rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
}
.kpi-info {
  display: flex;
  flex-direction: column;
}
.kpi-value {
  color: #ffffff;
  font-size: 40rpx;
  font-weight: 700;
  line-height: 1.2;
}
.kpi-label {
  color: rgba(255, 255, 255, 0.9);
  font-size: 24rpx;
  margin-top: 4rpx;
}
</style>
