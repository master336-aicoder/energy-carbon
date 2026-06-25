<template>
  <view class="favorites-page">
    <!-- Tab切换 -->
    <view class="fav-tabs">
      <view
        v-for="t in tabs"
        :key="t.value"
        class="fav-tab"
        :class="{ active: activeTab === t.value }"
        @click="switchTab(t.value)"
      >
        <text>{{ t.label }}</text>
      </view>
    </view>

    <!-- 收藏列表 -->
    <scroll-view scroll-y class="fav-scroll">
      <view
        v-for="(item, index) in currentList"
        :key="item.id"
        class="fav-item"
      >
        <view class="fav-icon-wrap" :style="{ background: activeTab === 'device' ? '#e8f5e9' : '#e6f7ff' }">
          <uni-icons
            :type="activeTab === 'device' ? 'settings' : 'bar'"
            size="22"
            :color="activeTab === 'device' ? '#1a5f3f' : '#1890ff'"
          ></uni-icons>
        </view>
        <view class="fav-info">
          <text class="fav-name">{{ item.name }}</text>
          <text class="fav-meta">{{ item.typeName }} | 收藏于 {{ item.favTime }}</text>
        </view>
        <view class="fav-remove-btn" @click="removeFav(index)">
          <uni-icons type="close" size="18" color="#f5222d"></uni-icons>
        </view>
      </view>

      <view v-if="currentList.length === 0" class="empty-state">
        <uni-icons type="star" size="56" color="#d9d9d9"></uni-icons>
        <text class="empty-text">暂无收藏内容</text>
        <text class="empty-hint">点击页面上的收藏按钮添加收藏</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const activeTab = ref('device')

const tabs = [
  { label: '设备', value: 'device' },
  { label: '报表', value: 'report' }
]

const favorites = ref([])

const demoDeviceFavs = [
  { id: 1, name: '1#空压机', typeName: '空压机', favTime: '2026-06-25', category: 'device' },
  { id: 2, name: '2#冷却塔', typeName: '冷却设备', favTime: '2026-06-23', category: 'device' },
  { id: 3, name: '1#水泵', typeName: '水泵', favTime: '2026-06-20', category: 'device' },
  { id: 4, name: '3#空调机组', typeName: '空调设备', favTime: '2026-06-18', category: 'device' }
]

const demoReportFavs = [
  { id: 101, name: '本月能耗统计报表', typeName: '能耗报表', favTime: '2026-06-24', category: 'report' },
  { id: 102, name: '碳排放月度趋势', typeName: '碳排报表', favTime: '2026-06-22', category: 'report' },
  { id: 103, name: 'Q2成本分析报告', typeName: '成本报表', favTime: '2026-06-15', category: 'report' }
]

const currentList = computed(() => {
  return favorites.value.filter(f => f.category === activeTab.value)
})

const switchTab = (val) => {
  activeTab.value = val
}

const removeFav = (index) => {
  const item = currentList.value[index]
  const realIndex = favorites.value.findIndex(f => f.id === item.id)
  if (realIndex > -1) {
    favorites.value.splice(realIndex, 1)
    saveToLocal()
    uni.showToast({ title: '已取消收藏', icon: 'success' })
  }
}

const saveToLocal = () => {
  uni.setStorageSync('favorites', JSON.stringify(favorites.value))
}

const loadData = () => {
  const cached = uni.getStorageSync('favorites')
  if (cached) {
    try {
      const data = JSON.parse(cached)
      if (data && data.length > 0) {
        favorites.value = data
        return
      }
    } catch (e) {}
  }
  // 使用演示数据
  favorites.value = [...demoDeviceFavs, ...demoReportFavs]
  saveToLocal()
}

onLoad(() => {
  loadData()
})
</script>

<style scoped>
.favorites-page {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}
.fav-tabs {
  display: flex;
  gap: 16rpx;
  padding: 24rpx;
  background: #ffffff;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}
.fav-tab {
  padding: 12rpx 36rpx;
  background: #f5f7fa;
  border-radius: 32rpx;
  font-size: 28rpx;
  color: #666666;
}
.fav-tab.active {
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  color: #ffffff;
}
.fav-scroll {
  flex: 1;
  height: 0;
  padding: 24rpx;
}
.fav-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
  margin-bottom: 16rpx;
}
.fav-icon-wrap {
  width: 80rpx;
  height: 80rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.fav-info {
  flex: 1;
  min-width: 0;
}
.fav-name {
  font-size: 30rpx;
  font-weight: 500;
  color: #333333;
  display: block;
  margin-bottom: 8rpx;
}
.fav-meta {
  font-size: 24rpx;
  color: #999999;
}
.fav-remove-btn {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  background: #fff2f0;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 160rpx 0;
}
.empty-text {
  color: #999999;
  font-size: 30rpx;
  margin-top: 24rpx;
}
.empty-hint {
  color: #cccccc;
  font-size: 24rpx;
  margin-top: 12rpx;
}
</style>
