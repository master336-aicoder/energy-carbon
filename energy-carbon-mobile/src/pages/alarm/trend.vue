<template>
  <view class="trend-page">
    <nav-bar title="告警趋势分析" showBack></nav-bar>
    <view class="trend-content" :style="{ paddingTop: safeAreaTop + 'px' }">
      <!-- 时间维度切换 -->
      <view class="time-tabs">
        <view
          v-for="t in timeTabs"
          :key="t.value"
          class="time-tab"
          :class="{ active: activeTab === t.value }"
          @click="switchTab(t.value)"
        >
          <text>{{ t.label }}</text>
        </view>
      </view>

      <!-- 告警数量趋势折线图 -->
      <view class="chart-card">
        <text class="chart-card-title">告警数量趋势</text>
        <canvas
          canvas-id="trendLine"
          id="trendLine"
          class="trend-canvas"
          :style="{ width: canvasW + 'px', height: '220px' }"
        ></canvas>
      </view>

      <!-- 告警类型分布饼图 -->
      <view class="chart-card">
        <text class="chart-card-title">告警类型分布</text>
        <view class="pie-wrapper">
          <canvas
            canvas-id="pieChart"
            id="pieChart"
            class="pie-canvas"
            :style="{ width: '200px', height: '200px' }"
          ></canvas>
          <view class="pie-legend">
            <view v-for="(item, idx) in typeDistribution" :key="idx" class="legend-item">
              <view class="legend-dot" :style="{ background: pieColors[idx % pieColors.length] }"></view>
              <text class="legend-name">{{ item.name }}</text>
              <text class="legend-val">{{ item.count }}次</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 告警设备TOP5 -->
      <view class="top-card">
        <text class="chart-card-title">告警设备TOP5</text>
        <view class="top-list">
          <view v-for="(item, idx) in topDevices" :key="idx" class="top-item">
            <view class="top-rank" :class="'rank-' + (idx + 1)">
              <text>{{ idx + 1 }}</text>
            </view>
            <view class="top-info">
              <text class="top-name">{{ item.deviceName }}</text>
              <view class="top-bar">
                <view class="top-progress" :style="{ width: item.percent + '%' }"></view>
              </view>
            </view>
            <text class="top-count">{{ item.count }}次</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import request from '@/utils/request.js'

const safeAreaTop = ref(uni.getSystemInfoSync().statusBarHeight + 44)
const activeTab = ref(7)
const canvasW = ref(300)

const timeTabs = [
  { label: '7天', value: 7 },
  { label: '30天', value: 30 },
  { label: '90天', value: 90 }
]

const pieColors = ['#1a5f3f', '#2d8b5a', '#faad14', '#f5222d', '#722ed1']

const trendLabels = ref([])
const trendValues = ref([])
const typeDistribution = ref([])
const topDevices = ref([])

const demoTrendData = {
  7: {
    labels: ['06-20', '06-21', '06-22', '06-23', '06-24', '06-25', '06-26'],
    values: [12, 8, 15, 6, 20, 14, 10],
    types: [
      { name: '用电异常', count: 35 },
      { name: '碳排放超标', count: 22 },
      { name: '设备故障', count: 12 },
      { name: '用水异常', count: 8 },
      { name: '通信异常', count: 6 }
    ],
    top5: [
      { deviceName: '1#空压机', count: 18, percent: 100 },
      { deviceName: '2#冷却塔', count: 14, percent: 78 },
      { deviceName: '3#空调机组', count: 11, percent: 61 },
      { deviceName: '1#水泵', count: 9, percent: 50 },
      { deviceName: '2#电表', count: 7, percent: 39 }
    ]
  },
  30: {
    labels: ['第1周', '第2周', '第3周', '第4周'],
    values: [68, 55, 72, 48],
    types: [
      { name: '用电异常', count: 120 },
      { name: '碳排放超标', count: 85 },
      { name: '设备故障', count: 45 },
      { name: '用水异常', count: 32 },
      { name: '通信异常', count: 18 }
    ],
    top5: [
      { deviceName: '1#空压机', count: 52, percent: 100 },
      { deviceName: '2#冷却塔', count: 41, percent: 79 },
      { deviceName: '3#空调机组', count: 35, percent: 67 },
      { deviceName: '1#水泵', count: 28, percent: 54 },
      { deviceName: '5#照明', count: 22, percent: 42 }
    ]
  },
  90: {
    labels: ['1月', '2月', '3月'],
    values: [198, 165, 180],
    types: [
      { name: '用电异常', count: 320 },
      { name: '碳排放超标', count: 215 },
      { name: '设备故障', count: 130 },
      { name: '用水异常', count: 85 },
      { name: '通信异常', count: 55 }
    ],
    top5: [
      { deviceName: '1#空压机', count: 145, percent: 100 },
      { deviceName: '2#冷却塔', count: 118, percent: 81 },
      { deviceName: '3#空调机组', count: 96, percent: 66 },
      { deviceName: '1#水泵', count: 78, percent: 54 },
      { deviceName: '2#电表', count: 62, percent: 43 }
    ]
  }
}

const switchTab = (val) => {
  activeTab.value = val
  loadData()
}

const loadData = async () => {
  try {
    const res = await request.get('/alarm/record/stats', { days: activeTab.value })
    if (res.code === 200 && res.data) {
      trendLabels.value = res.data.trendLabels || []
      trendValues.value = res.data.trendValues || []
      typeDistribution.value = res.data.typeDistribution || []
      topDevices.value = (res.data.topDevices || []).map(d => ({
        ...d,
        percent: res.data.topDevices.length > 0 ? Math.round((d.count / res.data.topDevices[0].count) * 100) : 0
      }))
      nextTick(() => { drawLineChart(); drawPieChart() })
      return
    }
  } catch (e) {
    // 使用演示数据
  }
  applyDemoData()
  nextTick(() => { drawLineChart(); drawPieChart() })
}

const applyDemoData = () => {
  const data = demoTrendData[activeTab.value] || demoTrendData[7]
  trendLabels.value = data.labels
  trendValues.value = data.values
  typeDistribution.value = data.types
  topDevices.value = data.top5
}

const drawLineChart = () => {
  if (!trendLabels.value.length || !trendValues.value.length) return
  try {
    const ctx = uni.createCanvasContext('trendLine')
    const padding = { top: 20, right: 20, bottom: 36, left: 50 }
    const w = canvasW.value - padding.left - padding.right
    const h = 180

    let maxVal = 0
    trendValues.value.forEach(v => { if (v > maxVal) maxVal = v })
    maxVal = maxVal * 1.2 || 100

    // 网格
    ctx.setStrokeStyle('#eeeeee')
    ctx.setLineWidth(0.5)
    for (let i = 0; i <= 4; i++) {
      const y = padding.top + (h / 4) * i
      ctx.beginPath()
      ctx.moveTo(padding.left, y)
      ctx.lineTo(canvasW.value - padding.right, y)
      ctx.stroke()
      ctx.setFillStyle('#999999')
      ctx.setFontSize(10)
      ctx.setTextAlign('right')
      ctx.fillText(Math.round(maxVal - (maxVal / 4) * i).toString(), padding.left - 6, y + 4)
    }

    const xStep = trendLabels.value.length > 1 ? w / (trendLabels.value.length - 1) : w
    ctx.setFillStyle('#999999')
    ctx.setFontSize(10)
    ctx.setTextAlign('center')
    trendLabels.value.forEach((label, i) => {
      ctx.fillText(label, padding.left + i * xStep, 256)
    })

    // 面积 + 折线
    ctx.beginPath()
    trendValues.value.forEach((v, i) => {
      const x = padding.left + i * xStep
      const y = padding.top + h - (v / maxVal) * h
      if (i === 0) ctx.moveTo(x, y)
      else ctx.lineTo(x, y)
    })
    const lastX = padding.left + (trendValues.value.length - 1) * xStep
    ctx.lineTo(lastX, padding.top + h)
    ctx.lineTo(padding.left, padding.top + h)
    ctx.closePath()
    ctx.setFillStyle('rgba(26, 95, 63, 0.1)')
    ctx.fill()

    ctx.setStrokeStyle('#1a5f3f')
    ctx.setLineWidth(2.5)
    ctx.beginPath()
    trendValues.value.forEach((v, i) => {
      const x = padding.left + i * xStep
      const y = padding.top + h - (v / maxVal) * h
      if (i === 0) ctx.moveTo(x, y)
      else ctx.lineTo(x, y)
    })
    ctx.stroke()

    // 数据点
    trendValues.value.forEach((v, i) => {
      const x = padding.left + i * xStep
      const y = padding.top + h - (v / maxVal) * h
      ctx.beginPath()
      ctx.arc(x, y, 4, 0, 2 * Math.PI)
      ctx.setFillStyle('#ffffff')
      ctx.fill()
      ctx.setStrokeStyle('#1a5f3f')
      ctx.setLineWidth(2)
      ctx.stroke()
      // 数值标签
      ctx.setFillStyle('#333333')
      ctx.setFontSize(10)
      ctx.setTextAlign('center')
      ctx.fillText(v.toString(), x, y - 10)
    })

    ctx.draw()
  } catch (e) {
    console.error('Line chart draw error:', e)
  }
}

const drawPieChart = () => {
  if (!typeDistribution.value.length) return
  try {
    const ctx = uni.createCanvasContext('pieChart')
    const cx = 100
    const cy = 100
    const radius = 70
    const total = typeDistribution.value.reduce((s, t) => s + t.count, 0)

    let startAngle = -Math.PI / 2
    typeDistribution.value.forEach((item, idx) => {
      const sliceAngle = (item.count / total) * 2 * Math.PI
      const endAngle = startAngle + sliceAngle

      ctx.beginPath()
      ctx.moveTo(cx, cy)
      ctx.arc(cx, cy, radius, startAngle, endAngle)
      ctx.closePath()
      ctx.setFillStyle(pieColors[idx % pieColors.length])
      ctx.fill()

      // 扇形分隔线
      ctx.setStrokeStyle('#ffffff')
      ctx.setLineWidth(2)
      ctx.stroke()

      startAngle = endAngle
    })

    // 中心圆（做成环形饼图）
    ctx.beginPath()
    ctx.arc(cx, cy, 40, 0, 2 * Math.PI)
    ctx.setFillStyle('#ffffff')
    ctx.fill()

    // 中心文字
    ctx.setFillStyle('#333333')
    ctx.setFontSize(14)
    ctx.setTextAlign('center')
    ctx.fillText(total.toString(), cx, cy - 2)
    ctx.setFontSize(10)
    ctx.setFillStyle('#999999')
    ctx.fillText('总计', cx, cy + 14)

    ctx.draw()
  } catch (e) {
    console.error('Pie chart draw error:', e)
  }
}

onLoad(() => {
  const sysInfo = uni.getSystemInfoSync()
  canvasW.value = sysInfo.windowWidth - 48
  loadData()
})
</script>

<style scoped>
.trend-page {
  min-height: 100vh;
  background: #f5f7fa;
}
.trend-content {
  padding: 24rpx;
}
.time-tabs {
  display: flex;
  gap: 16rpx;
  margin-bottom: 24rpx;
}
.time-tab {
  padding: 12rpx 36rpx;
  background: #ffffff;
  border-radius: 32rpx;
  font-size: 26rpx;
  color: #666666;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}
.time-tab.active {
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  color: #ffffff;
}
.chart-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
  margin-bottom: 24rpx;
}
.chart-card-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333333;
  display: block;
  margin-bottom: 20rpx;
}
.trend-canvas {
  width: 100%;
  height: 220px;
}
.pie-wrapper {
  display: flex;
  align-items: center;
  gap: 24rpx;
}
.pie-canvas {
  width: 200px;
  height: 200px;
  flex-shrink: 0;
}
.pie-legend {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}
.legend-item {
  display: flex;
  align-items: center;
  gap: 12rpx;
}
.legend-dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  flex-shrink: 0;
}
.legend-name {
  flex: 1;
  font-size: 24rpx;
  color: #666666;
}
.legend-val {
  font-size: 24rpx;
  font-weight: 600;
  color: #333333;
}
.top-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
  margin-bottom: 24rpx;
}
.top-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}
.top-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
}
.top-rank {
  width: 48rpx;
  height: 48rpx;
  border-radius: 12rpx;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  font-weight: 700;
  color: #999999;
  flex-shrink: 0;
}
.top-rank.rank-1 {
  background: linear-gradient(135deg, #ff8c00, #ffb347);
  color: #ffffff;
}
.top-rank.rank-2 {
  background: linear-gradient(135deg, #a0a0a0, #c0c0c0);
  color: #ffffff;
}
.top-rank.rank-3 {
  background: linear-gradient(135deg, #cd7f32, #e8a862);
  color: #ffffff;
}
.top-info {
  flex: 1;
}
.top-name {
  font-size: 28rpx;
  font-weight: 500;
  color: #333333;
  display: block;
  margin-bottom: 8rpx;
}
.top-bar {
  height: 8rpx;
  background: #f0f0f0;
  border-radius: 4rpx;
  overflow: hidden;
}
.top-progress {
  height: 100%;
  background: linear-gradient(90deg, #1a5f3f, #2d8b5a);
  border-radius: 4rpx;
  transition: width 0.3s;
}
.top-count {
  font-size: 28rpx;
  font-weight: 700;
  color: #1a5f3f;
  flex-shrink: 0;
}
</style>
