<template>
  <div class="analysis-electricity">
    <!-- 筛选栏 -->
    <el-card class="filter-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="统计粒度">
          <el-select v-model="statGranularity" style="width: 120px">
            <el-option label="日" value="DAY" />
            <el-option label="月" value="MONTH" />
            <el-option label="年" value="YEAR" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 汇总卡片 -->
    <el-row :gutter="20" class="kpi-row">
      <el-col :xs="24" :sm="12" :md="6" v-for="item in kpiCards" :key="item.label">
        <el-card shadow="hover" class="kpi-card">
          <div class="kpi-label">{{ item.label }}</div>
          <div class="kpi-value" :style="{ color: item.color }">
            {{ item.value }}
            <span class="kpi-unit">{{ item.unit }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 堆叠柱状图 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card>
          <template #header><span>峰/谷/平 电量分布</span></template>
          <div ref="stackChartRef" style="height: 380px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 分时电价明细表 -->
    <el-card class="table-card">
      <template #header>
        <div class="table-header">
          <span>分时电价明细</span>
          <el-button type="primary" size="small" @click="handleExport">导出</el-button>
        </div>
      </template>
      <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="statDate" label="日期" width="120" />
        <el-table-column prop="peakPower" label="峰段电量(kWh)" align="right" width="140">
          <template #default="{ row }">{{ Number(row.peakPower).toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="valleyPower" label="谷段电量(kWh)" align="right" width="140">
          <template #default="{ row }">{{ Number(row.valleyPower).toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="flatPower" label="平段电量(kWh)" align="right" width="140">
          <template #default="{ row }">{{ Number(row.flatPower).toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="peakCost" label="峰段电费(元)" align="right" width="130">
          <template #default="{ row }">{{ Number(row.peakCost).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="valleyCost" label="谷段电费(元)" align="right" width="130">
          <template #default="{ row }">{{ Number(row.valleyCost).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="flatCost" label="平段电费(元)" align="right" width="130">
          <template #default="{ row }">{{ Number(row.flatCost).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="totalCost" label="总电费(元)" align="right" width="130">
          <template #default="{ row }">{{ Number(row.totalCost).toFixed(2) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import echarts from '@/utils/echarts'
import { ElMessage } from 'element-plus'
import { getElectricitySummary, getElectricityDetail } from '@/api/analysis'

const dateRange = ref<string[]>([])
const statGranularity = ref('MONTH')
const loading = ref(false)
const queryParams = ref({ startDate: undefined as string | undefined, endDate: undefined as string | undefined, meterId: undefined })
const tableData = ref<Array<Record<string, unknown>>>([])

const kpiCards = ref([
  { label: '总电费', value: '0', unit: '元', color: '#dc2626' },
  { label: '峰段电费', value: '0', unit: '元', color: '#f59e0b' },
  { label: '谷段电费', value: '0', unit: '元', color: '#10b981' },
  { label: '总用电量', value: '0', unit: 'kWh', color: '#3b82f6' }
])

const stackChartRef = ref<HTMLDivElement>()
let stackChart: echarts.ECharts | null = null

function handleSearch() {
  if (dateRange.value && dateRange.value.length === 2) {
    queryParams.value.startDate = dateRange.value[0]
    queryParams.value.endDate = dateRange.value[1]
  } else {
    queryParams.value.startDate = undefined
    queryParams.value.endDate = undefined
  }
  fetchSummary()
  fetchDetail()
}

function handleReset() {
  dateRange.value = []
  queryParams.value = { startDate: undefined, endDate: undefined, meterId: undefined }
  handleSearch()
}

async function fetchSummary() {
  try {
    const res = await getElectricitySummary({
      startDate: queryParams.value.startDate,
      endDate: queryParams.value.endDate
    })
    const d = res.data as Record<string, unknown>
    kpiCards.value[0].value = Number(d.totalCost || 0).toFixed(2)
    kpiCards.value[1].value = Number(d.peakCost || 0).toFixed(2)
    kpiCards.value[2].value = Number(d.valleyCost || 0).toFixed(2)
    kpiCards.value[3].value = Number(d.totalPower || 0).toLocaleString()
  } catch { /* empty */ }
}

async function fetchDetail() {
  loading.value = true
  try {
    const res = await getElectricityDetail({
      startDate: queryParams.value.startDate,
      endDate: queryParams.value.endDate
    })
    tableData.value = res.data as Array<Record<string, unknown>>
    renderStackChart(tableData.value)
  } catch { /* empty */ } finally { loading.value = false }
}

function renderStackChart(data: Array<Record<string, unknown>>) {
  if (!stackChartRef.value || !data.length) return
  if (!stackChart) stackChart = echarts.init(stackChartRef.value)
  const dates = data.map(d => d.statDate as string)
  stackChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['峰段电量', '谷段电量', '平段电量'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: dates },
    yAxis: { type: 'value', name: 'kWh' },
    series: [
      { name: '峰段电量', type: 'bar', stack: 'total', data: data.map(d => Number(d.peakPower)), itemStyle: { color: '#dc2626' } },
      { name: '谷段电量', type: 'bar', stack: 'total', data: data.map(d => Number(d.valleyPower)), itemStyle: { color: '#10b981' } },
      { name: '平段电量', type: 'bar', stack: 'total', data: data.map(d => Number(d.flatPower)), itemStyle: { color: '#f59e0b' } }
    ]
  })
}

function handleExport() { ElMessage.info('导出功能待实现') }

onMounted(() => {
  fetchSummary()
  fetchDetail()
  window.addEventListener('resize', () => stackChart?.resize())
})

onUnmounted(() => { stackChart?.dispose() })
</script>

<style scoped lang="scss">
.analysis-electricity {
  .filter-card { margin-bottom: 16px; }
  .kpi-row { margin-bottom: 16px; }
  .kpi-card {
    margin-bottom: 16px;
    .kpi-label { font-size: 14px; color: #6b7280; margin-bottom: 8px; }
    .kpi-value { font-size: 26px; font-weight: bold; margin-bottom: 8px; }
    .kpi-unit { font-size: 14px; font-weight: normal; color: #6b7280; margin-left: 4px; }
  }
  .chart-row { margin-bottom: 16px; }
  .table-card { margin-top: 16px; }
  .table-header { display: flex; justify-content: space-between; align-items: center; }
}
</style>