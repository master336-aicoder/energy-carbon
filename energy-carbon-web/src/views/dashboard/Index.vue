<template>
  <div class="dashboard">
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
      <el-table :data="energyData" border stripe>
        <el-table-column prop="unitName" label="用能单元" min-width="150" />
        <el-table-column prop="electricity" label="电力(kWh)" align="right" />
        <el-table-column prop="naturalGas" label="天然气(m³)" align="right" />
        <el-table-column prop="steam" label="蒸汽(GJ)" align="right" />
        <el-table-column prop="water" label="自来水(t)" align="right" />
        <el-table-column prop="coal" label="原煤(t)" align="right" />
        <el-table-column prop="total" label="综合能耗(kgce)" align="right" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'

const trendPeriod = ref('day')
const trendChartRef = ref()
const pieChartRef = ref()
let trendChart = null
let pieChart = null

const kpiList = ref([
  { title: '综合能源消费', value: '12,580', unit: 'kgce', trend: -5.2, color: '#0d9488' },
  { title: '碳排放量', value: '3,260', unit: 'kgCO₂e', trend: -3.8, color: '#dc2626' },
  { title: '能耗成本', value: '86,520', unit: '元', trend: 2.1, color: '#f59e0b' },
  { title: '电力消费', value: '8,420', unit: 'kWh', trend: -1.5, color: '#3b82f6' },
  { title: '电力成本', value: '5,680', unit: '元', trend: 1.2, color: '#8b5cf6' },
  { title: '设备在线率', value: '98.5', unit: '%', trend: 0.3, color: '#10b981' }
])

const energyData = ref([
  { unitName: '全厂', electricity: 8420, naturalGas: 1200, steam: 580, water: 3200, coal: 0, total: 12580 },
  { unitName: '一分厂', electricity: 3200, naturalGas: 500, steam: 200, water: 1200, coal: 0, total: 4800 },
  { unitName: '二分厂', electricity: 2800, naturalGas: 400, steam: 180, water: 1000, coal: 0, total: 4100 },
  { unitName: '三分厂', electricity: 2420, naturalGas: 300, steam: 200, water: 1000, coal: 0, total: 3680 }
])

function initTrendChart() {
  trendChart = echarts.init(trendChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['综合能耗', '碳排放', '能耗成本'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00', '24:00']
    },
    yAxis: { type: 'value' },
    series: [
      { name: '综合能耗', type: 'line', data: [120, 132, 101, 134, 90, 230, 210], smooth: true },
      { name: '碳排放', type: 'line', data: [220, 182, 191, 234, 290, 330, 310], smooth: true },
      { name: '能耗成本', type: 'line', data: [150, 232, 201, 154, 190, 330, 410], smooth: true }
    ]
  }
  trendChart.setOption(option)
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
        data: [
          { value: 1048, name: '一分厂' },
          { value: 735, name: '二分厂' },
          { value: 580, name: '三分厂' },
          { value: 484, name: '四分厂' },
          { value: 300, name: '办公区' }
        ]
      }
    ]
  }
  pieChart.setOption(option)
}

function exportData() {
  // 导出逻辑
}

onMounted(() => {
  initTrendChart()
  initPieChart()
  window.addEventListener('resize', () => {
    trendChart?.resize()
    pieChart?.resize()
  })
})

onUnmounted(() => {
  trendChart?.dispose()
  pieChart?.dispose()
})

watch(trendPeriod, () => {
  // 根据周期切换数据
})
</script>

<style scoped lang="scss">
.dashboard {
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
  }
}
</style>
