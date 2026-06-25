<template>
  <view class="stats-page">
    <!-- 统计卡片 -->
    <view class="stats-grid">
      <view class="stats-card">
        <text class="stats-value">{{ monthData.total }}</text>
        <text class="stats-label">本月巡检总数</text>
      </view>
      <view class="stats-card">
        <text class="stats-value green">{{ monthData.completionRate }}%</text>
        <text class="stats-label">完成率</text>
      </view>
      <view class="stats-card">
        <text class="stats-value red">{{ monthData.abnormalCount }}</text>
        <text class="stats-label">异常数</text>
      </view>
      <view class="stats-card">
        <text class="stats-value">{{ monthData.avgDuration }}min</text>
        <text class="stats-label">平均耗时</text>
      </view>
    </view>

    <!-- 趋势图 -->
    <view class="chart-card">
      <view class="card-header">
        <text class="card-title">近30天巡检趋势</text>
      </view>
      <canvas canvas-id="trendChart" id="trendChart" class="trend-canvas" @touchstart="onTouchChart"></canvas>
    </view>

    <!-- 设备排名 -->
    <view class="rank-card">
      <view class="card-header">
        <text class="card-title">设备异常排名</text>
      </view>
      <view class="rank-list">
        <view
          v-for="(item, index) in rankList"
          :key="index"
          class="rank-item"
        >
          <view class="rank-left">
            <text class="rank-badge" :class="'rank-' + (index + 1)">{{ index + 1 }}</text>
            <text class="rank-name">{{ item.deviceName }}</text>
          </view>
          <view class="rank-right">
            <text class="rank-count">{{ item.abnormalCount }}次</text>
            <view class="rank-bar">
              <view class="rank-bar-fill" :style="{ width: item.percent + '%' }"></view>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import request from '@/utils/request.js'

const monthData = reactive({
  total: 0,
  completionRate: 0,
  abnormalCount: 0,
  avgDuration: 0
})
const trendData = ref([])
const rankList = ref([])

const loadStats = async () => {
  try {
    const res = await request.get('/monitor/inspect-task/stats')
    if (res.code === 200 && res.data) {
      monthData.total = res.data.total || 0
      monthData.completionRate = res.data.completionRate || 0
      monthData.abnormalCount = res.data.abnormalCount || 0
      monthData.avgDuration = res.data.avgDuration || 0
      trendData.value = res.data.trend || []
      rankList.value = res.data.rank || []
      nextTick(() => drawChart())
      return
    }
  } catch (e) {
    console.error('加载巡检统计失败:', e)
  }
  // 演示数据
  monthData.total = 156
  monthData.completionRate = 94.2
  monthData.abnormalCount = 8
  monthData.avgDuration = 25
  trendData.value = [3, 5, 4, 6, 8, 5, 7, 9, 6, 4, 5, 8, 7, 3, 6, 9, 10, 7, 5, 4, 8, 6, 5, 7, 9, 11, 8, 6, 7, 5]
  rankList.value = [
    { deviceName: '2#冷却塔', abnormalCount: 5, percent: 100 },
    { deviceName: '4#锅炉', abnormalCount: 4, percent: 80 },
    { deviceName: '1#空压机', abnormalCount: 3, percent: 60 },
    { deviceName: '6#空调机组', abnormalCount: 2, percent: 40 },
    { deviceName: '3#配电柜', abnormalCount: 1, percent: 20 },
    { deviceName: '5#水泵房', abnormalCount: 1, percent: 20 }
  ]
  nextTick(() => drawChart())
}

const drawChart = () => {
  const ctx = uni.createCanvasContext('trendChart', undefined)
  const data = trendData.value
  if (!data.length) return

  const w = uni.getSystemInfoSync().windowWidth - 96
  const h = 300
  const padding = { top: 20, bottom: 40, left: 50, right: 20 }
  const chartW = w - padding.left - padding.right
  const chartH = h - padding.top - padding.bottom

  const maxVal = Math.max(...data, 1)
  const minVal = 0
  const stepX = chartW / (data.length - 1 || 1)

  // 背景
  ctx.setFillStyle('#ffffff')
  ctx.fillRect(0, 0, w, h)

  // 网格线
  ctx.setStrokeStyle('#f0f0f0')
  ctx.setLineWidth(1)
  for (let i = 0; i <= 4; i++) {
    const y = padding.top + (chartH / 4) * i
    ctx.beginPath()
    ctx.moveTo(padding.left, y)
    ctx.lineTo(w - padding.right, y)
    ctx.stroke()

    // Y轴标签
    const val = Math.round(maxVal - (maxVal / 4) * i)
    ctx.setFillStyle('#999999')
    ctx.setFontSize(18)
    ctx.fillText(val, 4, y + 6)
  }

  // X轴标签（每隔5天）
  ctx.setFillStyle('#999999')
  ctx.setFontSize(18)
  for (let i = 0; i < data.length; i += 5) {
    const x = padding.left + i * stepX
    const d = new Date()
    d.setDate(d.getDate() - (data.length - 1 - i))
    ctx.fillText((d.getMonth() + 1) + '/' + d.getDate(), x - 16, h - 10)
  }

  // 渐变填充
  const gradient = ctx.createLinearGradient(0, padding.top, 0, h - padding.bottom)
  gradient.addColorStop(0, 'rgba(26, 95, 63, 0.3)')
  gradient.addColorStop(1, 'rgba(26, 95, 63, 0.02)')

  ctx.beginPath()
  ctx.moveTo(padding.left, h - padding.bottom)
  for (let i = 0; i < data.length; i++) {
    const x = padding.left + i * stepX
    const y = padding.top + chartH - ((data[i] - minVal) / (maxVal - minVal || 1)) * chartH
    if (i === 0) ctx.lineTo(x, y)
    else ctx.lineTo(x, y)
  }
  ctx.lineTo(padding.left + (data.length - 1) * stepX, h - padding.bottom)
  ctx.closePath()
  ctx.setFillStyle(gradient)
  ctx.fill()

  // 折线
  ctx.beginPath()
  ctx.setStrokeStyle('#1a5f3f')
  ctx.setLineWidth(3)
  ctx.setLineCap('round')
  ctx.setLineJoin('round')
  for (let i = 0; i < data.length; i++) {
    const x = padding.left + i * stepX
    const y = padding.top + chartH - ((data[i] - minVal) / (maxVal - minVal || 1)) * chartH
    if (i === 0) ctx.moveTo(x, y)
    else ctx.lineTo(x, y)
  }
  ctx.stroke()

  // 数据点
  for (let i = 0; i < data.length; i++) {
    const x = padding.left + i * stepX
    const y = padding.top + chartH - ((data[i] - minVal) / (maxVal - minVal || 1)) * chartH
    ctx.beginPath()
    ctx.arc(x, y, 4, 0, Math.PI * 2)
    ctx.setFillStyle('#1a5f3f')
    ctx.fill()
    ctx.beginPath()
    ctx.arc(x, y, 2, 0, Math.PI * 2)
    ctx.setFillStyle('#ffffff')
    ctx.fill()
  }

  ctx.draw()
}

const onTouchChart = (e) => {
  // 可扩展：显示触摸点数据
}

onShow(() => {
  loadStats()
})
</script>

<style scoped>
.stats-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24rpx;
}
.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20rpx;
  margin-bottom: 24rpx;
}
.stats-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06);
  display: flex;
  flex-direction: column;
  align-items: center;
}
.stats-value {
  font-size: 48rpx;
  font-weight: 700;
  color: #1a5f3f;
}
.stats-value.green {
  color: #52c41a;
}
.stats-value.red {
  color: #f5222d;
}
.stats-label {
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
}
.chart-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06);
}
.card-header {
  margin-bottom: 20rpx;
}
.card-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
}
.trend-canvas {
  width: 100%;
  height: 300rpx;
}
.rank-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06);
}
.rank-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}
.rank-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.rank-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}
.rank-badge {
  width: 44rpx;
  height: 44rpx;
  line-height: 44rpx;
  text-align: center;
  border-radius: 10rpx;
  font-size: 24rpx;
  font-weight: 600;
  background: #f0f0f0;
  color: #999;
}
.rank-badge.rank-1 {
  background: #fff7e6;
  color: #faad14;
}
.rank-badge.rank-2 {
  background: #f0f5f2;
  color: #1a5f3f;
}
.rank-badge.rank-3 {
  background: #e6f7ff;
  color: #1890ff;
}
.rank-name {
  font-size: 28rpx;
  color: #333;
}
.rank-right {
  display: flex;
  align-items: center;
  gap: 12rpx;
}
.rank-count {
  font-size: 26rpx;
  color: #f5222d;
  font-weight: 500;
  min-width: 60rpx;
  text-align: right;
}
.rank-bar {
  width: 120rpx;
  height: 12rpx;
  background: #f0f0f0;
  border-radius: 6rpx;
  overflow: hidden;
}
.rank-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #faad14, #f5222d);
  border-radius: 6rpx;
}
</style>
