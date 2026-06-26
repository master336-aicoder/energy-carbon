<template>
  <view class="stats-page">
    <nav-bar title="数据统计" showBack></nav-bar>
    <!-- 本月录入统计 -->
    <view class="section-card">
      <text class="section-title">本月录入统计</text>
      <view class="stat-grid">
        <view class="stat-cell">
          <text class="stat-num">{{ entryStats.meter }}</text>
          <text class="stat-text">抄表次数</text>
        </view>
        <view class="stat-cell">
          <text class="stat-num">{{ entryStats.indicator }}</text>
          <text class="stat-text">指标录入</text>
        </view>
        <view class="stat-cell">
          <text class="stat-num">{{ entryStats.production }}</text>
          <text class="stat-text">产量录入</text>
        </view>
        <view class="stat-cell">
          <text class="stat-num">{{ entryStats.purchase }}</text>
          <text class="stat-text">采购录入</text>
        </view>
      </view>
    </view>

    <!-- 巡检统计 -->
    <view class="section-card">
      <text class="section-title">巡检统计</text>
      <view class="inspect-row">
        <view class="inspect-item">
          <view class="inspect-icon-wrap" style="background: #e8f5e9;">
            <uni-icons type="search" size="20" color="#1a5f3f"></uni-icons>
          </view>
          <view class="inspect-info">
            <text class="inspect-num">{{ inspectStats.count }}</text>
            <text class="inspect-label">巡检次数</text>
          </view>
        </view>
        <view class="inspect-item">
          <view class="inspect-icon-wrap" style="background: #fff2f0;">
            <uni-icons type="warn" size="20" color="#f5222d"></uni-icons>
          </view>
          <view class="inspect-info">
            <text class="inspect-num">{{ inspectStats.anomaly }}</text>
            <text class="inspect-label">异常发现</text>
          </view>
        </view>
        <view class="inspect-item">
          <view class="inspect-icon-wrap" style="background: #e6f7ff;">
            <uni-icons type="checkmarkempty" size="20" color="#1890ff"></uni-icons>
          </view>
          <view class="inspect-info">
            <text class="inspect-num">{{ alarmStats.handled }}</text>
            <text class="inspect-label">处理告警</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 柱状图 -->
    <view class="chart-card">
      <text class="section-title">近6个月录入趋势</text>
      <canvas
        canvas-id="barChart"
        id="barChart"
        class="bar-canvas"
        :style="{ width: canvasW + 'px', height: '220px' }"
      ></canvas>
    </view>
  </view>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const canvasW = ref(300)

const entryStats = ref({
  meter: 0,
  indicator: 0,
  production: 0,
  purchase: 0
})

const inspectStats = ref({
  count: 0,
  anomaly: 0
})

const alarmStats = ref({
  handled: 0
})

const barLabels = ref([])
const barData = ref([])

const demoEntryStats = { meter: 86, indicator: 42, production: 28, purchase: 15 }
const demoInspectStats = { count: 32, anomaly: 8 }
const demoAlarmStats = { handled: 24 }
const demoBarLabels = ['1月', '2月', '3月', '4月', '5月', '6月']
const demoBarData = [156, 132, 148, 125, 138, 171]

const loadStats = () => {
  // 尝试从本地缓存读取
  const cached = uni.getStorageSync('userStats')
  if (cached) {
    try {
      const data = JSON.parse(cached)
      if (data) {
        entryStats.value = data.entryStats || { meter: 0, indicator: 0, production: 0, purchase: 0 }
        inspectStats.value = data.inspectStats || { count: 0, anomaly: 0 }
        alarmStats.value = data.alarmStats || { handled: 0 }
      }
    } catch (e) {}
  }

  // 如果缓存无数据，使用演示数据
  if (entryStats.value.meter === 0) {
    entryStats.value = { ...demoEntryStats }
    inspectStats.value = { ...demoInspectStats }
    alarmStats.value = { ...demoAlarmStats }
  }

  barLabels.value = demoBarLabels
  barData.value = demoBarData
  nextTick(() => drawBarChart())
}

const drawBarChart = () => {
  if (!barLabels.value.length || !barData.value.length) return
  try {
    const ctx = uni.createCanvasContext('barChart')
    const padding = { top: 20, right: 20, bottom: 36, left: 50 }
    const w = canvasW.value - padding.left - padding.right
    const h = 180

    let maxVal = 0
    barData.value.forEach(v => { if (v > maxVal) maxVal = v })
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

    const groupWidth = w / barLabels.value.length
    const barWidth = Math.max(16, groupWidth * 0.5)

    barData.value.forEach((v, i) => {
      const cx = padding.left + groupWidth * i + groupWidth / 2
      const x = cx - barWidth / 2
      const barH = (v / maxVal) * h
      const y = padding.top + h - barH

      const gradient = ctx.createLinearGradient(x, y, x, padding.top + h)
      gradient.addColorStop(0, '#1a5f3f')
      gradient.addColorStop(1, '#2d8b5a')
      ctx.setFillStyle(gradient)

      // 圆角柱子
      const r = Math.min(6, barWidth / 2)
      ctx.beginPath()
      ctx.moveTo(x, padding.top + h)
      ctx.lineTo(x, y + r)
      ctx.arc(x + r, y + r, r, Math.PI, Math.PI * 1.5)
      ctx.lineTo(x + barWidth - r, y)
      ctx.arc(x + barWidth - r, y + r, r, Math.PI * 1.5, 0)
      ctx.lineTo(x + barWidth, padding.top + h)
      ctx.closePath()
      ctx.fill()

      // 数值标签
      ctx.setFillStyle('#333333')
      ctx.setFontSize(10)
      ctx.setTextAlign('center')
      ctx.fillText(v.toString(), cx, y - 6)

      // X轴标签
      ctx.setFillStyle('#999999')
      ctx.fillText(barLabels.value[i], cx, padding.top + h + 20)
    })

    ctx.draw()
  } catch (e) {
    console.error('Bar chart draw error:', e)
  }
}

onLoad(() => {
  const sysInfo = uni.getSystemInfoSync()
  canvasW.value = sysInfo.windowWidth - 48
  loadStats()
})
</script>

<style scoped>
.stats-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24rpx;
}
.section-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
  margin-bottom: 24rpx;
}
.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333333;
  display: block;
  margin-bottom: 24rpx;
}
.stat-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20rpx;
}
.stat-cell {
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 28rpx;
  text-align: center;
}
.stat-num {
  font-size: 40rpx;
  font-weight: 700;
  color: #1a5f3f;
  display: block;
}
.stat-text {
  font-size: 24rpx;
  color: #999999;
  margin-top: 8rpx;
  display: block;
}
.inspect-row {
  display: flex;
  gap: 20rpx;
}
.inspect-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16rpx;
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 24rpx;
}
.inspect-icon-wrap {
  width: 64rpx;
  height: 64rpx;
  border-radius: 14rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.inspect-info {
  flex: 1;
}
.inspect-num {
  font-size: 36rpx;
  font-weight: 700;
  color: #333333;
  display: block;
}
.inspect-label {
  font-size: 22rpx;
  color: #999999;
  margin-top: 4rpx;
  display: block;
}
.chart-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}
.bar-canvas {
  width: 100%;
  height: 220px;
}
</style>
