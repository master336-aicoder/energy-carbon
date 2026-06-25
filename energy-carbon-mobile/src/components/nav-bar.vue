<template>
  <view class="nav-bar" :style="{ paddingTop: statusBarHeight + 'px' }">
    <view class="nav-content">
      <view v-if="showBack" class="back-btn" @click="goBack">
        <uni-icons type="left" size="20" color="#ffffff"></uni-icons>
      </view>
      <text class="nav-title">{{ title }}</text>
      <view class="nav-right">
        <slot name="right"></slot>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: ''
  },
  showBack: {
    type: Boolean,
    default: false
  }
})

const statusBarHeight = ref(0)
// #ifdef MP-WEIXIN || APP-PLUS || H5
statusBarHeight.value = uni.getSystemInfoSync().statusBarHeight || 0
// #endif

const goBack = () => {
  uni.navigateBack()
}
</script>

<style scoped>
.nav-bar {
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  position: sticky;
  top: 0;
  z-index: 999;
}
.nav-content {
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 0 16rpx;
}
.back-btn {
  position: absolute;
  left: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60rpx;
  height: 60rpx;
}
.nav-title {
  color: #ffffff;
  font-size: 34rpx;
  font-weight: 600;
}
.nav-right {
  position: absolute;
  right: 16rpx;
  display: flex;
  align-items: center;
}
</style>
