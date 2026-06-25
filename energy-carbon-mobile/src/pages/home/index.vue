<template>
  <view class="home-page">
    <nav-bar :title="'能碳数智化'"></nav-bar>
    <view class="home-content" :style="{ paddingTop: safeAreaTop + 'px' }">
      <!-- 欢迎语 -->
      <view class="welcome-section">
        <view class="welcome-text">
          <text class="welcome-label">欢迎回来，</text>
          <text class="welcome-name">{{ userName }}</text>
        </view>
        <text class="welcome-date">{{ today }}</text>
      </view>

      <!-- KPI 卡片 -->
      <view class="kpi-grid">
        <kpi-card
          icon="fire"
          :value="kpi.energy + ' kWh'"
          label="今日能耗"
          bgColor="linear-gradient(135deg, #1a5f3f, #2d8b5a)"
        ></kpi-card>
        <kpi-card
          icon="cloud"
          :value="kpi.carbon + ' t'"
          label="今日碳排"
          bgColor="linear-gradient(135deg, #2d8b5a, #52c41a)"
        ></kpi-card>
        <kpi-card
          icon="gear"
          :value="kpi.online + ' 台'"
          label="设备在线"
          bgColor="linear-gradient(135deg, #1890ff, #36cfc9)"
        ></kpi-card>
        <kpi-card
          icon="notification"
          :value="kpi.alarms + ' 条'"
          label="未处理告警"
          bgColor="linear-gradient(135deg, #faad14, #ff7a45)"
        ></kpi-card>
      </view>

      <!-- 趋势图 -->
      <chart-line
        title="近7天能耗与碳排趋势"
        :categories="chartCategories"
        :series="chartSeries"
      ></chart-line>

      <!-- 快捷入口 -->
      <view class="quick-actions">
        <text class="section-title">快捷操作</text>
        <view class="action-grid">
          <view class="action-item" @click="goToScan">
            <view class="action-icon scan">
              <uni-icons type="scan" size="32" color="#ffffff"></uni-icons>
            </view>
            <text class="action-label">扫码录入</text>
          </view>
          <view class="action-item" @click="goToEntry">
            <view class="action-icon entry">
              <uni-icons type="compose" size="32" color="#ffffff"></uni-icons>
            </view>
            <text class="action-label">数据录入</text>
          </view>
          <view class="action-item" @click="goToAlarm">
            <view class="action-icon alarm">
              <uni-icons type="notification" size="32" color="#ffffff"></uni-icons>
            </view>
            <text class="action-label">告警查看</text>
          </view>
          <view class="action-item" @click="goToReport">
            <view class="action-icon report">
              <uni-icons type="bars" size="32" color="#ffffff"></uni-icons>
            </view>
            <text class="action-label">报表查看</text>
          </view>
          <view class="action-item" @click="goInspect">
            <view class="action-icon inspect">
              <uni-icons type="flag" size="32" color="#ffffff"></uni-icons>
            </view>
            <text class="action-label">设备巡检</text>
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

const safeAreaTop = ref(uni.getSystemInfoSync().statusBarHeight + 44)
const userName = ref('管理员')
const today = ref('')

const kpi = ref({
  energy: 0,
  carbon: 0,
  online: 0,
  alarms: 0
})

const chartCategories = ref(['周一', '周二', '周三', '周四', '周五', '周六', '周日'])
const chartSeries = ref([
  {
    name: '能耗',
    data: [120, 132, 101, 134, 90, 230, 210]
  },
  {
    name: '碳排',
    data: [80, 92, 71, 94, 60, 150, 140]
  }
])

const getToday = () => {
  const d = new Date()
  const weeks = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 ${weeks[d.getDay()]}`
}

const loadUserInfo = () => {
  const info = uni.getStorageSync('userInfo')
  if (info) {
    try {
      const user = JSON.parse(info)
      userName.value = user.nickname || user.username || '管理员'
    } catch (e) {}
  }
}

const loadKpi = async () => {
  try {
    const res = await request.get('/dashboard/kpi')
    if (res.code === 200 && res.data) {
      kpi.value = {
        energy: res.data.todayEnergy || 0,
        carbon: res.data.todayCarbon || 0,
        online: res.data.onlineDevices || 0,
        alarms: res.data.pendingAlarms || 0
      }
      if (res.data.trendCategories) {
        chartCategories.value = res.data.trendCategories
      }
      if (res.data.trendSeries) {
        chartSeries.value = res.data.trendSeries
      }
    }
  } catch (e) {
    // 使用默认演示数据
    kpi.value = { energy: 1256.8, carbon: 856.2, online: 42, alarms: 3 }
  }
}

const goToScan = () => {
  uni.navigateTo({ url: '/pages/scan/index' })
}

const goToEntry = () => {
  uni.switchTab({ url: '/pages/entry/index' })
}

const goToAlarm = () => {
  uni.switchTab({ url: '/pages/alarm/index' })
}

const goToReport = () => {
  uni.navigateTo({ url: '/pages/report/index' })
}

const goInspect = () => {
  uni.navigateTo({ url: '/pages/inspect/index' })
}

onMounted(() => {
  today.value = getToday()
  loadUserInfo()
  loadKpi()
})

onShow(() => {
  loadUserInfo()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: #f5f7fa;
}
.home-content {
  padding: 24rpx;
}
.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}
.welcome-text {
  display: flex;
  align-items: baseline;
}
.welcome-label {
  font-size: 30rpx;
  color: #666666;
}
.welcome-name {
  font-size: 34rpx;
  font-weight: 700;
  color: #1a5f3f;
}
.welcome-date {
  font-size: 24rpx;
  color: #999999;
}
.kpi-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20rpx;
  margin-bottom: 24rpx;
}
.quick-actions {
  margin-top: 24rpx;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}
.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 24rpx;
  display: block;
}
.action-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20rpx;
}
.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.action-icon {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12rpx;
}
.action-icon.scan {
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
}
.action-icon.entry {
  background: linear-gradient(135deg, #1890ff, #36cfc9);
}
.action-icon.alarm {
  background: linear-gradient(135deg, #faad14, #ff7a45);
}
.action-icon.report {
  background: linear-gradient(135deg, #722ed1, #eb2f96);
}
.action-icon.inspect {
  background: linear-gradient(135deg, #13c2c2, #36cfc9);
}
.action-label {
  font-size: 24rpx;
  color: #666666;
}
</style>
