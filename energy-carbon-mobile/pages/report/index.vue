<template>
  <view class="report-page">
    <nav-bar title="报表查看" showBack></nav-bar>
    <view class="report-content" :style="{ paddingTop: safeAreaTop + 'px' }">
      <view class="report-tabs">
        <view
          class="tab-item"
          :class="{ active: activeTab === 'energy' }"
          @click="activeTab = 'energy'"
        >
          <text>能耗报表</text>
        </view>
        <view
          class="tab-item"
          :class="{ active: activeTab === 'carbon' }"
          @click="activeTab = 'carbon'"
        >
          <text>碳排报表</text>
        </view>
        <view
          class="tab-item"
          :class="{ active: activeTab === 'device' }"
          @click="activeTab = 'device'"
        >
          <text>设备报表</text>
        </view>
      </view>

      <view v-if="activeTab === 'energy'" class="report-section">
        <chart-line
          title="本月能耗趋势"
          :categories="energyCategories"
          :series="energySeries"
        ></chart-line>
        <view class="data-card">
          <text class="card-title">能耗统计</text>
          <view class="stat-row">
            <view class="stat-item">
              <text class="stat-value">12,560</text>
              <text class="stat-label">本月总能耗(kWh)</text>
            </view>
            <view class="stat-item">
              <text class="stat-value">8.5%</text>
              <text class="stat-label">同比变化</text>
            </view>
          </view>
        </view>
      </view>

      <view v-if="activeTab === 'carbon'" class="report-section">
        <chart-line
          title="本月碳排趋势"
          :categories="carbonCategories"
          :series="carbonSeries"
        ></chart-line>
        <view class="data-card">
          <text class="card-title">碳排统计</text>
          <view class="stat-row">
            <view class="stat-item">
              <text class="stat-value">8,240</text>
              <text class="stat-label">本月总碳排(t)</text>
            </view>
            <view class="stat-item">
              <text class="stat-value">-3.2%</text>
              <text class="stat-label">同比变化</text>
            </view>
          </view>
        </view>
      </view>

      <view v-if="activeTab === 'device'" class="report-section">
        <view class="data-card">
          <text class="card-title">设备运行统计</text>
          <view class="device-list">
            <view v-for="(item, index) in deviceList" :key="index" class="device-item">
              <view class="device-info">
                <text class="device-name">{{ item.name }}</text>
                <text class="device-status" :class="item.status">{{ item.statusText }}</text>
              </view>
              <view class="device-bar">
                <view class="device-progress" :style="{ width: item.load + '%' }"></view>
              </view>
              <view class="device-meta">
                <text>负载：{{ item.load }}%</text>
                <text>运行：{{ item.runtime }}h</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request.js'

const safeAreaTop = ref(uni.getSystemInfoSync().statusBarHeight + 44)
const activeTab = ref('energy')

const energyCategories = ref(['1日', '5日', '10日', '15日', '20日', '25日', '30日'])
const energySeries = ref([
  { name: '电', data: [420, 450, 380, 460, 410, 480, 440] },
  { name: '水', data: [120, 130, 110, 140, 125, 135, 128] }
])

const carbonCategories = ref(['1日', '5日', '10日', '15日', '20日', '25日', '30日'])
const carbonSeries = ref([
  { name: '直接排放', data: [280, 295, 260, 300, 275, 310, 290] },
  { name: '间接排放', data: [150, 160, 140, 165, 155, 170, 158] }
])

const deviceList = ref([
  { name: '1#空压机', status: 'online', statusText: '运行中', load: 78, runtime: 720 },
  { name: '2#冷却塔', status: 'online', statusText: '运行中', load: 65, runtime: 680 },
  { name: '3#空调机组', status: 'offline', statusText: '停机', load: 0, runtime: 0 },
  { name: '1#水泵', status: 'online', statusText: '运行中', load: 82, runtime: 720 },
  { name: '2#水泵', status: 'online', statusText: '运行中', load: 45, runtime: 680 }
])

const loadDeviceList = async () => {
  try {
    const res = await request.get('/monitor/device/list', { current: 1, size: 20 })
    if (res.code === 200 && res.data && res.data.records) {
      deviceList.value = res.data.records.map(item => ({
        name: item.deviceName || item.name,
        status: item.status === 1 ? 'online' : 'offline',
        statusText: item.status === 1 ? '运行中' : '停机',
        load: item.loadRate || 0,
        runtime: item.runtime || 0
      }))
    }
  } catch (e) {
    // 使用演示数据
  }
}

onMounted(() => {
  loadDeviceList()
})
</script>

<style scoped>
.report-page {
  min-height: 100vh;
  background: #f5f7fa;
}
.report-content {
  padding: 24rpx;
}
.report-tabs {
  display: flex;
  gap: 16rpx;
  margin-bottom: 24rpx;
}
.tab-item {
  flex: 1;
  padding: 20rpx 0;
  background: #ffffff;
  border-radius: 12rpx;
  text-align: center;
  font-size: 28rpx;
  color: #666666;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}
.tab-item.active {
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  color: #ffffff;
}
.report-section {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}
.data-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}
.card-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 24rpx;
  display: block;
}
.stat-row {
  display: flex;
  gap: 24rpx;
}
.stat-item {
  flex: 1;
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 32rpx;
  text-align: center;
}
.stat-value {
  font-size: 40rpx;
  font-weight: 700;
  color: #1a5f3f;
  display: block;
}
.stat-label {
  font-size: 24rpx;
  color: #999999;
  margin-top: 8rpx;
  display: block;
}
.device-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}
.device-item {
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 24rpx;
}
.device-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}
.device-name {
  font-size: 30rpx;
  font-weight: 600;
  color: #333333;
}
.device-status {
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
}
.device-status.online {
  background: #f6ffed;
  color: #52c41a;
}
.device-status.offline {
  background: #f5f5f5;
  color: #999999;
}
.device-bar {
  height: 12rpx;
  background: #e8e8e8;
  border-radius: 6rpx;
  overflow: hidden;
  margin-bottom: 12rpx;
}
.device-progress {
  height: 100%;
  background: linear-gradient(90deg, #1a5f3f, #2d8b5a);
  border-radius: 6rpx;
  transition: width 0.3s;
}
.device-meta {
  display: flex;
  justify-content: space-between;
  font-size: 24rpx;
  color: #999999;
}
</style>
