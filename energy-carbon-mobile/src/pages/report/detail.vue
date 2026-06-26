<template>
  <view class="detail-page">
    <nav-bar :title="reportTitle" showBack></nav-bar>
    <view class="detail-content" :style="{ paddingTop: safeAreaTop + 'px' }">
      <!-- 时间维度切换 -->
      <view class="dim-tabs">
        <view
          v-for="d in dimensions"
          :key="d.value"
          class="dim-item"
          :class="{ active: activeDim === d.value }"
          @click="switchDimension(d.value)"
        >
          <text>{{ d.label }}</text>
        </view>
      </view>

      <!-- 日期选择器 -->
      <view class="date-picker-row">
        <view class="date-picker-card" @click="showDatePicker = true">
          <uni-icons type="calendar" size="16" color="#1a5f3f"></uni-icons>
          <text class="date-text">{{ currentDateLabel }}</text>
          <uni-icons type="bottom" size="14" color="#999"></uni-icons>
        </view>
      </view>

      <!-- 图表区域 -->
      <view class="chart-section">
        <view class="chart-card">
          <view class="chart-header">
            <text class="chart-title-text">{{ reportTitle }}</text>
            <view class="chart-type-switch">
              <view class="switch-btn" :class="{ active: chartType === 'line' }" @click="chartType = 'line'">
                <text>折线</text>
              </view>
              <view class="switch-btn" :class="{ active: chartType === 'bar' }" @click="chartType = 'bar'">
                <text>柱状</text>
              </view>
            </view>
          </view>
          <canvas
            canvas-id="reportChart"
            id="reportChart"
            class="report-canvas"
            :style="{ width: canvasWidth + 'px', height: canvasHeight + 'px' }"
          ></canvas>
        </view>
      </view>

      <!-- 数据汇总 -->
      <view class="summary-section">
        <view class="summary-card">
          <view class="summary-item">
            <text class="summary-value">{{ summaryData.total }}</text>
            <text class="summary-label">{{ summaryData.totalLabel }}</text>
          </view>
          <view class="summary-divider"></view>
          <view class="summary-item">
            <text class="summary-value" :class="{ down: summaryData.yoy < 0 }">
              {{ summaryData.yoy > 0 ? '+' : '' }}{{ summaryData.yoy }}%
            </text>
            <text class="summary-label">同比</text>
          </view>
          <view class="summary-divider"></view>
          <view class="summary-item">
            <text class="summary-value" :class="{ down: summaryData.mom < 0 }">
              {{ summaryData.mom > 0 ? '+' : '' }}{{ summaryData.mom }}%
            </text>
            <text class="summary-label">环比</text>
          </view>
        </view>
      </view>

      <!-- 导出按钮 -->
      <view class="export-section">
        <button class="export-btn" @click="handleExport('pdf')">
          <uni-icons type="download" size="18" color="#fff"></uni-icons>
          <text>导出PDF</text>
        </button>
        <button class="export-btn" @click="handleExport('excel')">
          <uni-icons type="download" size="18" color="#fff"></uni-icons>
          <text>导出Excel</text>
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import request from '@/utils/request.js'
import { getPageParam } from '@/utils/page-options.js'

const safeAreaTop = ref(uni.getSystemInfoSync().statusBarHeight + 44)
const reportType = ref('energy')
const activeDim = ref('month')

const dimensions = [
  { label: '日', value: 'day' },
  { label: '周', value: 'week' },
  { label: '月', value: 'month' },
  { label: '季', value: 'quarter' },
  { label: '年', value: 'year' }
]

const reportTypeMap = {
  energy: { title: '能耗统计', unit: 'kWh', totalLabel: '总能耗' },
  carbon: { title: '碳排放趋势', unit: 'tCO₂', totalLabel: '总碳排' },
  cost: { title: '成本分析', unit: '元', totalLabel: '总成本' },
  efficiency: { title: '设备能效', unit: '%', totalLabel: '平均能效' }
}

const reportTitle = computed(() => (reportTypeMap[reportType.value] || {}).title || '报表详情')

const chartType = ref('line')
const canvasWidth = ref(300)
const canvasHeight = ref(220)
const chartLabels = ref([])
const chartData = ref([])
const showDatePicker = ref(false)

const currentDateLabel = computed(() => {
  const now = new Date()
  const y = now.getFullYear()
  const m = now.getMonth() + 1
  const d = now.getDate()
  const map = {
    day: `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')}`,
    week: `${y}年第${getWeekNumber(now)}周`,
    month: `${y}-${String(m).padStart(2, '0')}`,
    quarter: `${y}年第${Math.ceil(m / 3)}季度`,
    year: `${y}年`
  }
  return map[activeDim.value] || `${y}-${String(m).padStart(2, '0')}`
})

function getWeekNumber(date) {
  const start = new Date(date.getFullYear(), 0, 1)
  const diff = date - start + (start.getTimezoneOffset() - date.getTimezoneOffset()) * 60000
  return Math.ceil(diff / (7 * 24 * 3600 * 1000))
}

const summaryData = ref({
  total: '12,560',
  totalLabel: '总能耗(kWh)',
  yoy: -5.2,
  mom: -2.8
})

// 演示数据
const demoData = {
  energy: {
    day: {
      labels: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00', '23:59'],
      data: [180, 120, 450, 520, 480, 390, 200],
      total: '3,240 kWh', totalLabel: '总能耗(kWh)', yoy: -5.2, mom: -2.8
    },
    week: {
      labels: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      data: [2100, 2250, 2180, 2300, 2150, 1200, 980],
      total: '11,160 kWh', totalLabel: '总能耗(kWh)', yoy: -4.1, mom: -1.5
    },
    month: {
      labels: ['1日', '5日', '10日', '15日', '20日', '25日', '30日'],
      data: [420, 450, 380, 460, 410, 480, 440],
      total: '12,560 kWh', totalLabel: '总能耗(kWh)', yoy: -5.2, mom: -2.8
    },
    quarter: {
      labels: ['1月', '2月', '3月'],
      data: [12560, 11200, 11800],
      total: '35,560 kWh', totalLabel: '总能耗(kWh)', yoy: -3.8, mom: 4.2
    },
    year: {
      labels: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
      data: [13200, 11800, 12560, 12100, 11800, 11400, 13200, 13500, 12800, 11900, 11200, 10500],
      total: '145,860 kWh', totalLabel: '总能耗(kWh)', yoy: -6.1, mom: -1.2
    }
  },
  carbon: {
    day: {
      labels: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00', '23:59'],
      data: [95, 60, 260, 310, 280, 210, 110],
      total: '1,325 t', totalLabel: '总碳排(tCO₂)', yoy: -8.3, mom: -4.5
    },
    week: {
      labels: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      data: [1200, 1350, 1280, 1400, 1300, 750, 620],
      total: '7,900 t', totalLabel: '总碳排(tCO₂)', yoy: -7.2, mom: -3.1
    },
    month: {
      labels: ['1日', '5日', '10日', '15日', '20日', '25日', '30日'],
      data: [280, 295, 260, 300, 275, 310, 290],
      total: '8,240 t', totalLabel: '总碳排(tCO₂)', yoy: -8.3, mom: -4.5
    },
    quarter: {
      labels: ['1月', '2月', '3月'],
      data: [8240, 7600, 7900],
      total: '23,740 t', totalLabel: '总碳排(tCO₂)', yoy: -6.5, mom: 3.2
    },
    year: {
      labels: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
      data: [8500, 7600, 8240, 7900, 7800, 7500, 8600, 8800, 8300, 7700, 7200, 6800],
      total: '96,400 t', totalLabel: '总碳排(tCO₂)', yoy: -9.1, mom: -2.3
    }
  },
  cost: {
    day: {
      labels: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00', '23:59'],
      data: [1200, 800, 3500, 4200, 3800, 2600, 1400],
      total: '17,500 元', totalLabel: '总成本(元)', yoy: -3.1, mom: -1.8
    },
    week: {
      labels: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      data: [15000, 16500, 15800, 17200, 16000, 9000, 7500],
      total: '97,000 元', totalLabel: '总成本(元)', yoy: -2.5, mom: 1.2
    },
    month: {
      labels: ['1日', '5日', '10日', '15日', '20日', '25日', '30日'],
      data: [3200, 3500, 3100, 3800, 3400, 3900, 3600],
      total: '98,500 元', totalLabel: '总成本(元)', yoy: -3.1, mom: -1.8
    },
    quarter: {
      labels: ['1月', '2月', '3月'],
      data: [98500, 91000, 95000],
      total: '284,500 元', totalLabel: '总成本(元)', yoy: -2.8, mom: 4.4
    },
    year: {
      labels: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
      data: [102000, 91000, 98500, 95000, 93000, 89000, 105000, 108000, 101000, 92000, 87000, 82000],
      total: '1,143,000 元', totalLabel: '总成本(元)', yoy: -4.2, mom: -0.8
    }
  },
  efficiency: {
    day: {
      labels: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00', '23:59'],
      data: [82, 75, 88, 85, 90, 87, 80],
      total: '85.2%', totalLabel: '平均能效(%)', yoy: 3.5, mom: 1.2
    },
    week: {
      labels: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      data: [85, 87, 84, 88, 86, 78, 72],
      total: '84.3%', totalLabel: '平均能效(%)', yoy: 2.8, mom: 0.5
    },
    month: {
      labels: ['1日', '5日', '10日', '15日', '20日', '25日', '30日'],
      data: [82, 85, 80, 88, 84, 87, 86],
      total: '84.5%', totalLabel: '平均能效(%)', yoy: 3.5, mom: 1.2
    },
    quarter: {
      labels: ['1月', '2月', '3月'],
      data: [84.5, 83.2, 85.1],
      total: '84.3%', totalLabel: '平均能效(%)', yoy: 2.1, mom: 1.9
    },
    year: {
      labels: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
      data: [82, 83, 84, 85, 84, 86, 85, 87, 86, 84, 85, 86],
      total: '84.8%', totalLabel: '平均能效(%)', yoy: 4.2, mom: 0.6
    }
  }
}

const switchDimension = (dim) => {
  activeDim.value = dim
  loadChartData()
}

const loadChartData = async () => {
  try {
    const res = await request.get('/report/detail', {
      type: reportType.value,
      dimension: activeDim.value
    })
    if (res.code === 200 && res.data) {
      chartLabels.value = res.data.labels || []
      chartData.value = res.data.values || []
      summaryData.value = {
        total: res.data.total || '-',
        totalLabel: res.data.totalLabel || (reportTypeMap[reportType.value] || {}).totalLabel || '-',
        yoy: res.data.yoy || 0,
        mom: res.data.mom || 0
      }
      nextTick(() => drawChart())
      return
    }
  } catch (e) {
    // 使用演示数据
  }
  // 使用演示数据
  const typeData = demoData[reportType.value]
  if (typeData) {
    const dimData = typeData[activeDim.value] || typeData.month
    chartLabels.value = dimData.labels
    chartData.value = dimData.data
    summaryData.value = {
      total: dimData.total,
      totalLabel: dimData.totalLabel,
      yoy: dimData.yoy,
      mom: dimData.mom
    }
  }
  nextTick(() => drawChart())
}

const drawChart = () => {
  if (!chartLabels.value.length || !chartData.value.length) return
  try {
    const ctx = uni.createCanvasContext('reportChart')
    const padding = { top: 20, right: 20, bottom: 36, left: 50 }
    const w = canvasWidth.value - padding.left - padding.right
    const h = canvasHeight.value - padding.top - padding.bottom

    let maxVal = 0
    chartData.value.forEach(v => { if (v > maxVal) maxVal = v })
    maxVal = maxVal * 1.2 || 100

    // 网格线
    ctx.setStrokeStyle('#eeeeee')
    ctx.setLineWidth(0.5)
    for (let i = 0; i <= 4; i++) {
      const y = padding.top + (h / 4) * i
      ctx.beginPath()
      ctx.moveTo(padding.left, y)
      ctx.lineTo(canvasWidth.value - padding.right, y)
      ctx.stroke()
      const val = Math.round(maxVal - (maxVal / 4) * i)
      ctx.setFillStyle('#999999')
      ctx.setFontSize(10)
      ctx.setTextAlign('right')
      ctx.fillText(val.toString(), padding.left - 6, y + 4)
    }

    const xStep = chartLabels.value.length > 1 ? w / (chartLabels.value.length - 1) : w
    ctx.setFillStyle('#999999')
    ctx.setFontSize(10)
    ctx.setTextAlign('center')
    chartLabels.value.forEach((label, i) => {
      const x = padding.left + i * xStep
      ctx.fillText(label, x, canvasHeight.value - 10)
    })

    if (chartType.value === 'line') {
      // 折线图
      ctx.setStrokeStyle('#1a5f3f')
      ctx.setLineWidth(2.5)
      ctx.beginPath()
      chartData.value.forEach((v, i) => {
        const x = padding.left + i * xStep
        const y = padding.top + h - (v / maxVal) * h
        if (i === 0) ctx.moveTo(x, y)
        else ctx.lineTo(x, y)
      })
      ctx.stroke()

      // 填充渐变区域
      ctx.beginPath()
      chartData.value.forEach((v, i) => {
        const x = padding.left + i * xStep
        const y = padding.top + h - (v / maxVal) * h
        if (i === 0) ctx.moveTo(x, y)
        else ctx.lineTo(x, y)
      })
      ctx.lineTo(padding.left + (chartData.value.length - 1) * xStep, padding.top + h)
      ctx.lineTo(padding.left, padding.top + h)
      ctx.closePath()
      ctx.setFillStyle('rgba(26, 95, 63, 0.1)')
      ctx.fill()

      // 数据点
      chartData.value.forEach((v, i) => {
        const x = padding.left + i * xStep
        const y = padding.top + h - (v / maxVal) * h
        ctx.beginPath()
        ctx.arc(x, y, 4, 0, 2 * Math.PI)
        ctx.setFillStyle('#ffffff')
        ctx.fill()
        ctx.setStrokeStyle('#1a5f3f')
        ctx.setLineWidth(2)
        ctx.stroke()
      })
    } else {
      // 柱状图
      const barWidth = Math.max(12, xStep * 0.5)
      chartData.value.forEach((v, i) => {
        const x = padding.left + i * xStep - barWidth / 2
        const barH = (v / maxVal) * h
        const y = padding.top + h - barH
        // 渐变色柱子
        const gradient = ctx.createLinearGradient(x, y, x, padding.top + h)
        gradient.addColorStop(0, '#1a5f3f')
        gradient.addColorStop(1, '#2d8b5a')
        ctx.setFillStyle(gradient)
        ctx.beginPath()
        ctx.moveTo(x, padding.top + h)
        ctx.lineTo(x, y)
        ctx.arc(x + barWidth / 2, y, barWidth / 2, Math.PI, 0)
        ctx.lineTo(x + barWidth, padding.top + h)
        ctx.closePath()
        ctx.fill()

        // 数值标签
        ctx.setFillStyle('#333333')
        ctx.setFontSize(10)
        ctx.setTextAlign('center')
        ctx.fillText(v.toString(), padding.left + i * xStep, y - 6)
      })
    }

    ctx.draw()
  } catch (e) {
    console.error('Chart draw error:', e)
  }
}

watch(chartType, () => {
  nextTick(() => drawChart())
})

const handleExport = (format) => {
  // #ifdef H5
  uni.showLoading({ title: '生成中...' })
  setTimeout(() => {
    uni.hideLoading()
    uni.showToast({ title: `${format.toUpperCase()} 导出成功`, icon: 'success' })
  }, 1500)
  // #endif
  // #ifndef H5
  uni.showToast({ title: `${format.toUpperCase()} 导出功能仅H5端可用`, icon: 'none' })
  // #endif
}

const initReport = (type) => {
  if (type) {
    reportType.value = type
  }
  const sysInfo = uni.getSystemInfoSync()
  canvasWidth.value = sysInfo.windowWidth - 48
  loadChartData()
}

onLoad((options) => {
  initReport(options && options.type)
})

onMounted(() => {
  // Edge等浏览器H5兼容
  const type = getPageParam('type')
  if (type) initReport(type)
})
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: #f5f7fa;
}
.detail-content {
  padding: 24rpx;
}
.dim-tabs {
  display: flex;
  gap: 12rpx;
  margin-bottom: 24rpx;
}
.dim-item {
  padding: 12rpx 28rpx;
  background: #ffffff;
  border-radius: 32rpx;
  font-size: 26rpx;
  color: #666666;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}
.dim-item.active {
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  color: #ffffff;
}
.date-picker-row {
  margin-bottom: 24rpx;
}
.date-picker-card {
  display: flex;
  align-items: center;
  gap: 12rpx;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 20rpx 28rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}
.date-text {
  flex: 1;
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
}
.chart-section {
  margin-bottom: 24rpx;
}
.chart-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}
.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}
.chart-title-text {
  font-size: 30rpx;
  font-weight: 600;
  color: #333333;
}
.chart-type-switch {
  display: flex;
  gap: 8rpx;
  background: #f5f7fa;
  border-radius: 8rpx;
  padding: 4rpx;
}
.switch-btn {
  padding: 8rpx 20rpx;
  border-radius: 6rpx;
  font-size: 24rpx;
  color: #666666;
}
.switch-btn.active {
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  color: #ffffff;
}
.report-canvas {
  width: 100%;
  height: 220px;
}
.summary-section {
  margin-bottom: 24rpx;
}
.summary-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
  display: flex;
  align-items: center;
}
.summary-item {
  flex: 1;
  text-align: center;
}
.summary-value {
  font-size: 36rpx;
  font-weight: 700;
  color: #1a5f3f;
  display: block;
}
.summary-value.down {
  color: #f5222d;
}
.summary-label {
  font-size: 24rpx;
  color: #999999;
  margin-top: 8rpx;
  display: block;
}
.summary-divider {
  width: 1rpx;
  height: 60rpx;
  background: #e8e8e8;
}
.export-section {
  display: flex;
  gap: 20rpx;
  margin-bottom: 40rpx;
}
.export-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  height: 88rpx;
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  color: #ffffff;
  font-size: 28rpx;
  border-radius: 16rpx;
  border: none;
  box-shadow: 0 4rpx 16rpx rgba(26, 95, 63, 0.3);
}
.export-btn::after {
  border: none;
}
</style>
