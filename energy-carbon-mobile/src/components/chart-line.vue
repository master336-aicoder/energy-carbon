<template>
  <view class="chart-container">
    <view class="chart-header">
      <text class="chart-title">{{ title }}</text>
    </view>
    <view class="chart-body">
      <canvas canvas-id="lineChart" id="lineChart" class="charts" :style="{ width: cWidth + 'px', height: cHeight + 'px' }"
        @touchstart="touchChart" @touchmove="moveChart" @touchend="endChart"></canvas>
    </view>
  </view>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'

const props = defineProps({
  title: { type: String, default: '趋势图' },
  categories: { type: Array, default: () => [] },
  series: { type: Array, default: () => [] }
})

const cWidth = ref(300)
const cHeight = ref(200)
const ctx = ref(null)

onMounted(() => {
  const sysInfo = uni.getSystemInfoSync()
  cWidth.value = sysInfo.windowWidth - 60
  cHeight.value = 200
  setTimeout(() => drawChart(), 100)
})

watch(() => [props.categories, props.series], () => {
  setTimeout(() => drawChart(), 50)
}, { deep: true })

const drawChart = () => {
  if (!props.categories.length || !props.series.length) return
  try {
    ctx.value = uni.createCanvasContext('lineChart')
    const padding = { top: 20, right: 20, bottom: 30, left: 40 }
    const w = cWidth.value - padding.left - padding.right
    const h = cHeight.value - padding.top - padding.bottom

    // 找最大值
    let maxVal = 0
    props.series.forEach(s => {
      s.data.forEach(v => { if (v > maxVal) maxVal = v })
    })
    maxVal = maxVal * 1.2 || 100

    // 绘制网格
    const ctxC = ctx.value
    ctxC.setStrokeStyle('#eeeeee')
    ctxC.setLineWidth(0.5)
    for (let i = 0; i <= 4; i++) {
      const y = padding.top + (h / 4) * i
      ctxC.beginPath()
      ctxC.moveTo(padding.left, y)
      ctxC.lineTo(cWidth.value - padding.right, y)
      ctxC.stroke()
      // Y轴标签
      const val = Math.round(maxVal - (maxVal / 4) * i)
      ctxC.setFillStyle('#999999')
      ctxC.setFontSize(10)
      ctxC.fillText(val.toString(), 4, y + 4)
    }

    // X轴标签
    const xStep = props.categories.length > 1 ? w / (props.categories.length - 1) : w
    ctxC.setFillStyle('#999999')
    ctxC.setFontSize(10)
    ctxC.setTextAlign('center')
    props.categories.forEach((cat, i) => {
      const x = padding.left + i * xStep
      ctxC.fillText(cat, x, cHeight.value - 8)
    })

    // 绘制折线
    const colors = ['#1a5f3f', '#2d8b5a', '#faad14', '#f5222d']
    props.series.forEach((s, si) => {
      ctxC.setStrokeStyle(colors[si % colors.length])
      ctxC.setLineWidth(2)
      ctxC.beginPath()
      s.data.forEach((v, i) => {
        const x = padding.left + i * xStep
        const y = padding.top + h - (v / maxVal) * h
        if (i === 0) ctxC.moveTo(x, y)
        else ctxC.lineTo(x, y)
      })
      ctxC.stroke()

      // 绘制点
      s.data.forEach((v, i) => {
        const x = padding.left + i * xStep
        const y = padding.top + h - (v / maxVal) * h
        ctxC.beginPath()
        ctxC.arc(x, y, 3, 0, 2 * Math.PI)
        ctxC.setFillStyle(colors[si % colors.length])
        ctxC.fill()
      })
    })

    ctxC.draw()
  } catch (e) {
    console.error('Chart draw error:', e)
  }
}

const touchChart = (e) => {}
const moveChart = (e) => {}
const endChart = (e) => {}
</script>

<style scoped>
.chart-container {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
  margin-bottom: 24rpx;
}
.chart-header {
  margin-bottom: 16rpx;
}
.chart-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333333;
}
.charts {
  width: 100%;
  height: 200px;
}
</style>
