<template>
  <view class="entry-page">
    <view class="entry-list">
      <view class="entry-card" @click="goToForm('indicator')">
        <view class="entry-icon indicator">
          <uni-icons type="bars" size="40" color="#ffffff"></uni-icons>
        </view>
        <view class="entry-info">
          <text class="entry-title">指标录入</text>
          <text class="entry-desc">录入能耗、碳排等关键指标数据</text>
        </view>
        <uni-icons type="right" size="18" color="#cccccc"></uni-icons>
      </view>

      <view class="entry-card" @click="goToForm('output')">
        <view class="entry-icon output">
          <uni-icons type="upload" size="40" color="#ffffff"></uni-icons>
        </view>
        <view class="entry-info">
          <text class="entry-title">产量录入</text>
          <text class="entry-desc">录入生产产量、产值等数据</text>
        </view>
        <uni-icons type="right" size="18" color="#cccccc"></uni-icons>
      </view>

      <view class="entry-card" @click="goToForm('meter')">
        <view class="entry-icon meter">
          <uni-icons type="compose" size="40" color="#ffffff"></uni-icons>
        </view>
        <view class="entry-info">
          <text class="entry-title">抄表录入</text>
          <text class="entry-desc">录入水电气表读数数据</text>
        </view>
        <uni-icons type="right" size="18" color="#cccccc"></uni-icons>
      </view>

      <view class="entry-card" @click="goPage('/pages/entry/purchase')">
        <view class="entry-icon purchase">
          <uni-icons type="cart" size="40" color="#ffffff"></uni-icons>
        </view>
        <view class="entry-info">
          <text class="entry-title">能源采购</text>
          <text class="entry-desc">录入电/气/水/蒸汽等采购数据</text>
        </view>
        <uni-icons type="right" size="18" color="#cccccc"></uni-icons>
      </view>

      <view class="entry-card" @click="goPage('/pages/entry/carbon-factor')">
        <view class="entry-icon carbon">
          <uni-icons type="cloud" size="40" color="#ffffff"></uni-icons>
        </view>
        <view class="entry-info">
          <text class="entry-title">排放因子</text>
          <text class="entry-desc">录入不同能源碳排放因子值</text>
        </view>
        <uni-icons type="right" size="18" color="#cccccc"></uni-icons>
      </view>
    </view>

    <view class="quick-actions">
      <view class="action-item" @click="goPage('/pages/entry/records')">
        <uni-icons type="list" size="20" color="#1a5f3f"></uni-icons>
        <text>录入记录</text>
      </view>
      <view class="action-item" @click="goPage('/pages/entry/offline')">
        <uni-icons type="download" size="20" color="#1a5f3f"></uni-icons>
        <text>离线数据</text>
      </view>
      <view class="action-item" @click="goPage('/pages/inspect/index')">
        <uni-icons type="flag" size="20" color="#1a5f3f"></uni-icons>
        <text>设备巡检</text>
      </view>
    </view>

    <view class="recent-section">
      <text class="section-title">最近抄表记录</text>
      <view v-if="recentList.length === 0" class="empty-state">
        <uni-icons type="info" size="40" color="#cccccc"></uni-icons>
        <text>暂无记录</text>
      </view>
      <view v-else class="record-list">
        <view v-for="(item, index) in recentList" :key="index" class="record-item">
          <view class="record-left">
            <text class="record-name">{{ item.deviceName || item.meterName }}</text>
            <text class="record-time">{{ item.readingTime || item.createTime }}</text>
          </view>
          <view class="record-right">
            <text class="record-value">{{ item.readingValue || item.value }}</text>
            <text class="record-unit">{{ item.unit || 'kWh' }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import request from '@/utils/request.js'

const recentList = ref([])

const loadRecent = async () => {
  try {
    const res = await request.get('/monitor/meter-record/list', { current: 1, size: 5 })
    if (res.code === 200 && res.data) {
      recentList.value = res.data.records || []
    }
  } catch (e) {
    // 演示数据
    recentList.value = [
      { deviceName: '1#电表', readingValue: 12560.5, unit: 'kWh', readingTime: '2026-06-25 08:00' },
      { deviceName: '2#电表', readingValue: 8920.3, unit: 'kWh', readingTime: '2026-06-25 08:00' },
      { deviceName: '总水表', readingValue: 3450.2, unit: 'm³', readingTime: '2026-06-25 08:00' }
    ]
  }
}

const goToForm = (type) => {
  uni.navigateTo({ url: '/pages/entry/form?type=' + type })
}

const goPage = (url) => {
  uni.navigateTo({ url })
}

onMounted(() => {
  loadRecent()
})

onShow(() => {
  loadRecent()
})
</script>

<style scoped>
.entry-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24rpx;
}
.entry-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 32rpx;
  display: flex;
  align-items: center;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}
.entry-icon {
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
  flex-shrink: 0;
}
.entry-icon.indicator {
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
}
.entry-icon.output {
  background: linear-gradient(135deg, #1890ff, #36cfc9);
}
.entry-icon.meter {
  background: linear-gradient(135deg, #faad14, #ff7a45);
}
.entry-icon.purchase {
  background: linear-gradient(135deg, #722ed1, #b37feb);
}
.entry-icon.carbon {
  background: linear-gradient(135deg, #13c2c2, #36cfc9);
}
.quick-actions {
  display: flex;
  gap: 16rpx;
  margin: 24rpx 0;
}
.action-item {
  flex: 1;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 20rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
  font-size: 24rpx;
  color: #333;
}
.entry-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.entry-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
}
.entry-desc {
  font-size: 24rpx;
  color: #999999;
  margin-top: 6rpx;
}
.recent-section {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}
.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 20rpx;
  display: block;
}
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 0;
  color: #999999;
  font-size: 26rpx;
}
.record-list {
  display: flex;
  flex-direction: column;
}
.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}
.record-item:last-child {
  border-bottom: none;
}
.record-left {
  display: flex;
  flex-direction: column;
}
.record-name {
  font-size: 28rpx;
  color: #333333;
}
.record-time {
  font-size: 22rpx;
  color: #999999;
  margin-top: 4rpx;
}
.record-right {
  display: flex;
  align-items: baseline;
}
.record-value {
  font-size: 32rpx;
  font-weight: 700;
  color: #1a5f3f;
}
.record-unit {
  font-size: 22rpx;
  color: #999999;
  margin-left: 6rpx;
}
</style>
