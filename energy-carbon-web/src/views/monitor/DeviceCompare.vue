<template>
  <div class="device-compare">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>设备对标分析</span>
        </div>
      </template>

      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="设备A">
          <el-select v-model="searchForm.meterIdA" placeholder="请选择设备A" clearable filterable style="width: 200px">
            <el-option v-for="m in meterList" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备B">
          <el-select v-model="searchForm.meterIdB" placeholder="请选择设备B" clearable filterable style="width: 200px">
            <el-option v-for="m in meterList" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="数据类型">
          <el-select v-model="searchForm.dataType" placeholder="数据类型" clearable style="width: 130px">
            <el-option label="电量" value="电量" />
            <el-option label="功率" value="功率" />
            <el-option label="电压" value="电压" />
            <el-option label="电流" value="电流" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="searchForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="searchForm.endTime"
            type="datetime"
            placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleCompare">开始对比</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="chart-container" v-loading="loading">
        <div ref="chartRef" class="chart"></div>
      </div>

      <el-card shadow="never" class="stat-card" v-if="difference !== null">
        <template #header>
          <span>差异统计</span>
        </template>
        <el-descriptions :column="3" border>
          <el-descriptions-item label="设备A平均值">{{ avgA?.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="设备B平均值">{{ avgB?.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="差异值">{{ difference.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="设备A最大值">{{ maxA?.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="设备B最大值">{{ maxB?.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="差异率">{{ diffRate.toFixed(2) }}%</el-descriptions-item>
        </el-descriptions>
      </el-card>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getDeviceCompare } from '@/api/deviceCompare'
import { getMeterList } from '@/api/meter'
import type { Meter, CompareDataSeries } from '@/types'
import echarts from '@/utils/echarts'

const loading = ref(false)
const meterList = ref<Meter[]>([])
const chartRef = ref<HTMLDivElement>()
let chartInstance: echarts.ECharts | null = null

const avgA = ref(0)
const avgB = ref(0)
const maxA = ref(0)
const maxB = ref(0)
const difference = ref(0)
const diffRate = ref(0)

const searchForm = reactive({
  meterIdA: undefined as number | undefined,
  meterIdB: undefined as number | undefined,
  dataType: undefined as string | undefined,
  startTime: '',
  endTime: ''
})

async function fetchMeters() {
  const res = await getMeterList({ current: 1, size: 9999 })
  meterList.value = res.data.records
}

function calcStats(seriesA: CompareDataSeries, seriesB: CompareDataSeries) {
  const valsA = seriesA.data.map(d => d.value)
  const valsB = seriesB.data.map(d => d.value)
  avgA.value = valsA.length > 0 ? valsA.reduce((a, b) => a + b, 0) / valsA.length : 0
  avgB.value = valsB.length > 0 ? valsB.reduce((a, b) => a + b, 0) / valsB.length : 0
  maxA.value = valsA.length > 0 ? Math.max(...valsA) : 0
  maxB.value = valsB.length > 0 ? Math.max(...valsB) : 0
  difference.value = Math.abs(avgA.value - avgB.value)
  diffRate.value = avgA.value !== 0 ? (difference.value / avgA.value) * 100 : 0
}

function renderChart(seriesList: CompareDataSeries[]) {
  if (!chartRef.value) return
  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value)
  }
  const allTimes = Array.from(new Set(seriesList.flatMap(s => s.data.map(d => d.time.slice(0, 19).replace('T', ' '))))).sort()

  const series = seriesList.map(s => ({
    name: s.seriesName,
    type: 'line' as const,
    data: s.data.map(d => d.value),
    smooth: true,
    lineStyle: { width: 2, color: s.color },
    itemStyle: { color: s.color }
  }))

  const option: echarts.EChartsOption = {
    title: { text: '设备对标曲线', left: 'center' },
    tooltip: { trigger: 'axis' },
    legend: { data: seriesList.map(s => s.seriesName), bottom: 0 },
    xAxis: { type: 'category', data: allTimes, boundaryGap: false, axisLabel: { rotate: 45 } },
    yAxis: { type: 'value' },
    series,
    grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true }
  }
  chartInstance.setOption(option, true)
}

async function handleCompare() {
  if (!searchForm.meterIdA || !searchForm.meterIdB) {
    ElMessage.warning('请选择两个设备')
    return
  }
  if (!searchForm.startTime || !searchForm.endTime) {
    ElMessage.warning('请选择时间范围')
    return
  }
  loading.value = true
  try {
    const res = await getDeviceCompare({
      meterIdA: searchForm.meterIdA,
      meterIdB: searchForm.meterIdB,
      dataType: searchForm.dataType,
      startTime: searchForm.startTime,
      endTime: searchForm.endTime
    })
    renderChart(res.data)
    if (res.data.length >= 2) {
      calcStats(res.data[0], res.data[1])
    }
  } finally {
    loading.value = false
  }
}

function handleReset() {
  searchForm.meterIdA = undefined
  searchForm.meterIdB = undefined
  searchForm.dataType = undefined
  searchForm.startTime = ''
  searchForm.endTime = ''
  difference.value = 0
  diffRate.value = 0
  chartInstance?.clear()
}

function handleResize() {
  chartInstance?.resize()
}

onMounted(() => {
  fetchMeters()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>

<style scoped lang="scss">
.device-compare {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.chart-container {
  height: 450px;
  margin-bottom: 20px;
}

.chart {
  width: 100%;
  height: 100%;
}

.stat-card {
  margin-top: 20px;
}
</style>