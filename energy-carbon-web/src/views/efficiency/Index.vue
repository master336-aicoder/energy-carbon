<template>
  <div class="efficiency-index">
    <!-- 筛选栏 -->
    <el-card class="filter-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="用能单元">
          <el-tree-select v-model="queryParams.energyUnitId" :data="unitTree" :props="{ label: 'deptName', value: 'id' }" placeholder="选择用能单元" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="指标类型">
          <el-select v-model="indicatorType" style="width: 150px">
            <el-option label="综合能耗" value="energy" />
            <el-option label="单位产值能耗" value="intensity" />
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

    <!-- 柱状+折线组合图 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card>
          <template #header><span>能耗与产值双轴图</span></template>
          <div ref="dualChartRef" style="height: 380px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 对标结果 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card>
          <template #header><span>对标分析结果</span></template>
          <el-table :data="benchmarkData" border stripe>
            <el-table-column prop="indicator" label="指标" min-width="160" />
            <el-table-column prop="unitValue" label="本单位值" align="right" width="160" />
            <el-table-column prop="benchmarkValue" label="行业基准值" align="right" width="160" />
            <el-table-column prop="gapRate" label="差距率" align="right" width="120">
              <template #default="{ row }">
                <el-tag :type="Number(row.gapRate) <= 0 ? 'success' : 'danger'" size="small">
                  {{ row.gapRate }}%
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" align="center" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === '达标' ? 'success' : 'danger'">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 能效趋势图 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card>
          <template #header><span>能效趋势</span></template>
          <div ref="trendChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import echarts from '@/utils/echarts'
import { getEfficiencyTrend, getEfficiencyIntensity, getBenchmarkAnalysis } from '@/api/analysis'
import { getDeptTree } from '@/api/dept'

const unitTree = ref<any[]>([])

async function loadUnitTree() {
  try {
    const res = await getDeptTree()
    const depts = res.data || []
    unitTree.value = depts.map((d: any) => ({
      id: d.deptId || d.id,
      deptName: d.deptName || d.name,
      children: d.children?.map((c: any) => ({ id: c.deptId || c.id, deptName: c.deptName || c.name })) || []
    }))
  } catch {
    unitTree.value = []
  }
}
const queryParams = ref({ energyUnitId: 1 as number | undefined, startDate: undefined as string | undefined, endDate: undefined as string | undefined })
const dateRange = ref<string[]>([])
const indicatorType = ref('intensity')
const benchmarkData = ref<Array<Record<string, unknown>>>([])

const kpiCards = ref([
  { label: '综合能耗', value: '0', unit: 'kgce', color: '#0d9488' },
  { label: '产出值', value: '0', unit: 't', color: '#3b82f6' },
  { label: '单位产值能耗', value: '0', unit: 'kgce/t', color: '#f59e0b' },
  { label: '对标结果', value: '-', unit: '', color: '#8b5cf6' }
])

const dualChartRef = ref<HTMLDivElement>()
const trendChartRef = ref<HTMLDivElement>()
let dualChart: echarts.ECharts | null = null
let trendChart: echarts.ECharts | null = null

function handleSearch() {
  if (dateRange.value && dateRange.value.length === 2) {
    queryParams.value.startDate = dateRange.value[0]
    queryParams.value.endDate = dateRange.value[1]
  }
  fetchIntensity()
  fetchTrend()
  fetchBenchmark()
}

function handleReset() {
  dateRange.value = []
  queryParams.value = { energyUnitId: 1, startDate: undefined, endDate: undefined }
  handleSearch()
}

async function fetchIntensity() {
  try {
    const res = await getEfficiencyIntensity({
      energyUnitId: queryParams.value.energyUnitId || 1,
      startDate: queryParams.value.startDate,
      endDate: queryParams.value.endDate
    })
    const d = res.data as Record<string, unknown>
    kpiCards.value[0].value = Number(d.totalEnergy || 0).toLocaleString()
    kpiCards.value[1].value = Number(d.totalOutput || 0).toLocaleString()
    kpiCards.value[2].value = Number(d.intensity || 0).toFixed(4)
  } catch { /* empty */ }
}

async function fetchTrend() {
  try {
    const res = await getEfficiencyTrend({
      energyUnitId: queryParams.value.energyUnitId || 1,
      startDate: queryParams.value.startDate,
      endDate: queryParams.value.endDate
    })
    const data = res.data as Array<Record<string, unknown>>
    if (data && data.length) {
      const dates = data.map(d => d.statDate as string)
      const energy = data.map(d => Number(d.energyConsumption))
      const output = data.map(d => Number(d.outputValue))
      renderDualChart(dates, energy, output)
      renderTrendChart(dates, data.map(d => Number(d.intensity)))
    }
  } catch { /* empty */ }
}

async function fetchBenchmark() {
  try {
    const res = await getBenchmarkAnalysis({
      energyUnitId: queryParams.value.energyUnitId || 1,
      industry: '制造业',
      startDate: queryParams.value.startDate,
      endDate: queryParams.value.endDate
    })
    const d = res.data as Record<string, unknown>
    kpiCards.value[3].value = (d.status as string) || '-'
    benchmarkData.value = [{
      indicator: '单位产值能耗',
      unitValue: d.unitIntensity || 0,
      benchmarkValue: d.benchmarkValue || 0,
      gapRate: d.gapRate || 0,
      status: d.status || '未知'
    }]
  } catch { /* empty */ }
}

function renderDualChart(dates: string[], energy: number[], output: number[]) {
  if (!dualChartRef.value) return
  if (!dualChart) dualChart = echarts.init(dualChartRef.value)
  dualChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'cross' } },
    legend: { data: ['能耗(kgce)', '产出'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: dates },
    yAxis: [
      { type: 'value', name: '能耗(kgce)' },
      { type: 'value', name: '产出' }
    ],
    series: [
      { name: '能耗(kgce)', type: 'bar', data: energy, itemStyle: { color: '#0d9488' } },
      { name: '产出', type: 'line', yAxisIndex: 1, data: output, smooth: true, itemStyle: { color: '#f59e0b' }, lineStyle: { width: 3 } }
    ]
  })
}

function renderTrendChart(dates: string[], intensity: number[]) {
  if (!trendChartRef.value) return
  if (!trendChart) trendChart = echarts.init(trendChartRef.value)
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: dates, boundaryGap: false },
    yAxis: { type: 'value', name: 'kgce/t' },
    series: [{ type: 'line', data: intensity, smooth: true, areaStyle: { opacity: 0.3 }, itemStyle: { color: '#8b5cf6' } }]
  })
}

onMounted(() => {
  loadUnitTree()
  fetchIntensity()
  fetchTrend()
  fetchBenchmark()
  window.addEventListener('resize', () => { dualChart?.resize(); trendChart?.resize() })
})
onUnmounted(() => { dualChart?.dispose(); trendChart?.dispose() })
</script>

<style scoped lang="scss">
.efficiency-index {
  .filter-card { margin-bottom: 16px; }
  .kpi-row { margin-bottom: 16px; }
  .kpi-card {
    margin-bottom: 16px;
    .kpi-label { font-size: 14px; color: #6b7280; margin-bottom: 8px; }
    .kpi-value { font-size: 26px; font-weight: bold; margin-bottom: 8px; }
    .kpi-unit { font-size: 14px; font-weight: normal; color: #6b7280; margin-left: 4px; }
  }
  .chart-row { margin-bottom: 16px; }
}
</style>