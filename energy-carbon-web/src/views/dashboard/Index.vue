<template>
  <div class="dashboard">
    <!-- 通知公告滚动横幅 -->
    <div v-if="noticeList.length > 0" class="notice-banner">
      <el-icon class="notice-icon"><Bell /></el-icon>
      <div class="notice-scroll-wrapper">
        <div class="notice-scroll-content" :style="scrollStyle">
          <div v-for="(notice, index) in noticeList" :key="index" class="notice-item" @click="handleNoticeClick(notice)">
            <el-tag size="small" :type="notice.noticeType === 1 ? 'primary' : 'warning'" class="notice-tag">
              {{ notice.noticeType === 1 ? '通知' : '公告' }}
            </el-tag>
            <span class="notice-title">{{ notice.title }}</span>
            <span class="notice-time">{{ notice.createTime }}</span>
          </div>
        </div>
      </div>
    </div>
    <NoticeDetailDialog
      v-model="noticeDialogVisible"
      :itemId="noticeDialogId"
      mode="message"
      :messageList="noticeList"
      @read="handleNoticeRead"
    />

    <!-- KPI卡片 -->
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :md="8" :lg="4" v-for="item in kpiList" :key="item.title">
        <el-card class="kpi-card" shadow="hover">
          <div class="kpi-title">{{ item.title }}</div>
          <div class="kpi-value" :style="{ color: item.color }">
            {{ item.value }}
            <span class="kpi-unit">{{ item.unit }}</span>
          </div>
          <div class="kpi-trend">
            <span :class="item.trend >= 0 ? 'up' : 'down'">
              {{ item.trend >= 0 ? '+' : '' }}{{ item.trend }}%
            </span>
            <span class="kpi-compare">同比</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>能碳趋势</span>
              <el-radio-group v-model="trendPeriod" size="small">
                <el-radio-button label="day">日</el-radio-button>
                <el-radio-button label="month">月</el-radio-button>
                <el-radio-button label="year">年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="trendChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>碳排放分布</span>
            </div>
          </template>
          <div ref="pieChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 用能统计表 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>用能统计</span>
          <el-button type="primary" size="small" @click="exportData">导出</el-button>
        </div>
      </template>
      <el-table :data="energyData" border stripe style="width: 100%">
        <el-table-column prop="type" label="能源类型" min-width="150" />
        <el-table-column prop="value" label="用量" align="right" />
        <el-table-column prop="ratio" label="占比(%)" align="right" />
        <el-table-column prop="yoy" label="同比(%)" align="right">
          <template #default="{ row }">
            <span :class="row.yoy >= 0 ? 'up' : 'down'">
              {{ row.yoy >= 0 ? '+' : '' }}{{ row.yoy }}%
            </span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { Bell } from '@element-plus/icons-vue'
import echarts from '@/utils/echarts'
import { getMessageList, markMessageRead } from '@/api/message'
import { getDashboardStats } from '@/api/dashboard'
import NoticeDetailDialog from '@/components/NoticeDetailDialog.vue'

const trendPeriod = ref('day')
const trendChartRef = ref()
const pieChartRef = ref()
let trendChart = null
let pieChart = null

// 我的消息（首页滚动横幅）
const noticeList = ref([])
const scrollStyle = ref({})
const noticeDialogVisible = ref(false)
const noticeDialogId = ref(undefined)
let scrollInterval = null

async function loadNotices() {
  try {
    const res = await getMessageList({ current: 1, size: 10, readStatus: 0 })
    noticeList.value = res.data?.records || []
    startScroll()
  } catch (e) {
    // 静默失败
  }
}

function startScroll() {
  if (scrollInterval) clearInterval(scrollInterval)
  if (noticeList.value.length <= 1) return
  let index = 0
  scrollInterval = setInterval(() => {
    index = (index + 1) % noticeList.value.length
    scrollStyle.value = { transform: `translateY(-${index * 40}px)`, transition: 'transform 0.5s ease' }
  }, 3000)
}

function handleNoticeClick(notice) {
  noticeDialogId.value = notice.id
  noticeDialogVisible.value = true
}

function handleNoticeRead(noticeId) {
  // 从列表中移除已读的通知
  const idx = noticeList.value.findIndex((n: any) => n.id === noticeId)
  if (idx >= 0) {
    noticeList.value.splice(idx, 1)
  }
  // 如果没有未读通知了，停止滚动
  if (noticeList.value.length <= 1 && scrollInterval) {
    clearInterval(scrollInterval)
    scrollInterval = null
  }
}

const kpiList = ref([])
const energyData = ref([])

async function loadDashboardStats() {
  try {
    const res = await getDashboardStats()
    const data = res.data || {}

    // KPI 映射
    const kpi = data.kpi || {}
    kpiList.value = [
      { title: '综合能源消费', value: formatNumber(kpi.totalEnergy), unit: kpi.totalEnergyUnit || 'tce', trend: kpi.energyTrend || 0, color: '#0d9488' },
      { title: '碳排放量', value: formatNumber(kpi.carbonEmission), unit: kpi.carbonEmissionUnit || 'tCO₂', trend: kpi.carbonTrend || 0, color: '#dc2626' },
      { title: '用能成本', value: formatNumber(kpi.totalCost), unit: kpi.totalCostUnit || '万元', trend: kpi.costTrend || 0, color: '#f59e0b' },
      { title: '绿电占比', value: kpi.greenPowerRatio || 0, unit: '%', trend: kpi.greenTrend || 0, color: '#3b82f6' },
      { title: '节能率', value: kpi.energySavingRate || 0, unit: '%', trend: kpi.energySavingRate || 0, color: '#8b5cf6' },
      { title: '设备在线数', value: kpi.deviceCount || 0, unit: '台', trend: kpi.deviceTrend || 0, color: '#10b981' }
    ]

    // 用能统计
    energyData.value = (data.energy || []).map(item => ({
      type: item.type,
      value: item.value,
      ratio: item.ratio,
      yoy: item.yoy
    }))

    // 更新趋势图
    updateTrendChart(data.trend || [])
    // 更新饼图
    updatePieChart(data.pie || {})
  } catch (e) {
    // 静默失败，保持默认空状态
  }
}

function formatNumber(num: number): string {
  if (num === undefined || num === null) return '0'
  return num.toLocaleString('en-US', { maximumFractionDigits: 1 })
}

function initTrendChart() {
  trendChart = echarts.init(trendChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['综合能耗', '碳排放'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: []
    },
    yAxis: { type: 'value' },
    series: [
      { name: '综合能耗', type: 'line', data: [], smooth: true },
      { name: '碳排放', type: 'line', data: [], smooth: true }
    ]
  }
  trendChart.setOption(option)
}

function updateTrendChart(trendData: any[]) {
  if (!trendChart) return
  const dates = trendData.map(d => d.date)
  const energyValues = trendData.map(d => Number(d.energy?.toFixed(2)))
  const carbonValues = trendData.map(d => Number(d.carbon?.toFixed(2)))
  trendChart.setOption({
    xAxis: { data: dates },
    series: [
      { name: '综合能耗', data: energyValues },
      { name: '碳排放', data: carbonValues }
    ]
  })
}

function initPieChart() {
  pieChart = echarts.init(pieChartRef.value)
  const option = {
    tooltip: { trigger: 'item' },
    legend: { orient: 'vertical', left: 'left' },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
        label: { show: false, position: 'center' },
        emphasis: { label: { show: true, fontSize: 20, fontWeight: 'bold' } },
        data: []
      }
    ]
  }
  pieChart.setOption(option)
}

function updatePieChart(pieData: any) {
  if (!pieChart) return
  const data = [
    { value: pieData.electricity || 0, name: '电力' },
    { value: pieData.gas || 0, name: '天然气' },
    { value: pieData.coal || 0, name: '煤炭' },
    { value: pieData.oil || 0, name: '油品' },
    { value: pieData.other || 0, name: '其他' }
  ]
  pieChart.setOption({
    series: [{ data }]
  })
}

function exportData() {
  // 导出逻辑
}

onMounted(() => {
  loadNotices()
  initTrendChart()
  initPieChart()
  loadDashboardStats()
  window.addEventListener('resize', () => {
    trendChart?.resize()
    pieChart?.resize()
  })
})

onUnmounted(() => {
  if (scrollInterval) clearInterval(scrollInterval)
  trendChart?.dispose()
  pieChart?.dispose()
})

watch(trendPeriod, () => {
  // 根据周期切换数据
})
</script>

<style scoped lang="scss">
.dashboard {
  .notice-banner {
    display: flex;
    align-items: center;
    background: #fff;
    border-radius: 4px;
    padding: 0 16px;
    margin-bottom: 16px;
    height: 40px;
    box-shadow: 0 1px 4px rgba(0,0,0,0.08);
    overflow: hidden;

    .notice-icon {
      color: #f59e0b;
      margin-right: 10px;
      flex-shrink: 0;
    }

    .notice-scroll-wrapper {
      flex: 1;
      height: 40px;
      overflow: hidden;
      position: relative;
    }

    .notice-scroll-content {
      position: absolute;
      width: 100%;
    }

    .notice-item {
      display: flex;
      align-items: center;
      height: 40px;
      cursor: pointer;
      gap: 8px;

      .notice-tag {
        flex-shrink: 0;
      }

      .notice-title {
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        font-size: 14px;
        color: #333;
      }

      .notice-time {
        flex-shrink: 0;
        font-size: 12px;
        color: #999;
      }
    }
  }

  .kpi-card {
    margin-bottom: 20px;

    .kpi-title {
      font-size: 14px;
      color: #6b7280;
      margin-bottom: 8px;
    }

    .kpi-value {
      font-size: 28px;
      font-weight: bold;
      margin-bottom: 8px;

      .kpi-unit {
        font-size: 14px;
        font-weight: normal;
        color: #6b7280;
        margin-left: 4px;
      }
    }

    .kpi-trend {
      font-size: 13px;

      .up { color: #10b981; }
      .down { color: #dc2626; }

      .kpi-compare {
        color: #9ca3af;
        margin-left: 8px;
      }
    }
  }

  .chart-row {
    margin-bottom: 20px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .table-card {
    margin-top: 20px;

    .up { color: #10b981; }
    .down { color: #dc2626; }
  }
}
</style>
