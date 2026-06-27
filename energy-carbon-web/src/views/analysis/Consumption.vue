<template>
  <div class="analysis-consumption">
    <!-- 筛选栏 -->
    <el-card class="filter-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="用能单元">
          <el-tree-select
            v-model="queryParams.energyUnitId"
            :data="unitTree"
            :props="{ label: 'deptName', value: 'id' }"
            placeholder="选择用能单元"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="统计周期">
          <el-select v-model="queryParams.statPeriod" style="width: 120px">
            <el-option label="日" value="DAY" />
            <el-option label="月" value="MONTH" />
            <el-option label="年" value="YEAR" />
          </el-select>
        </el-form-item>
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
        <el-form-item label="能源品种">
          <el-select v-model="queryParams.energyTypeId" placeholder="全部" clearable style="width: 150px">
            <el-option v-for="t in energyTypes" :key="t.id" :label="t.name" :value="t.id" />
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
          <div class="kpi-compare">
            <span v-if="item.trend !== undefined" :class="item.trend >= 0 ? 'up' : 'down'">
              同比 {{ item.trend >= 0 ? '+' : '' }}{{ item.trend }}%
            </span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="16">
        <el-card>
          <template #header><span>能耗趋势</span></template>
          <div ref="trendChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <el-card>
          <template #header><span>能源品种占比</span></template>
          <div ref="pieChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="table-header">
          <span>能耗明细数据</span>
          <el-button type="primary" size="small" @click="handleExport">导出</el-button>
        </div>
      </template>
      <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="statDate" label="统计日期" width="120" />
        <el-table-column prop="energyUnitName" label="用能单元" min-width="140" />
        <el-table-column prop="energyTypeName" label="能源品种" min-width="120" />
        <el-table-column prop="consumption" label="消耗量" align="right" width="140">
          <template #default="{ row }">
            {{ row.consumption?.toLocaleString() }} {{ row.unit }}
          </template>
        </el-table-column>
        <el-table-column prop="statPeriod" label="周期" width="80" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button text type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          v-model:current="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import echarts from '@/utils/echarts'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getConsumptionList, getConsumptionSummary, getConsumptionTrend, deleteConsumption } from '@/api/analysis'
import type { ConsumptionListParams, AnalysisConsumption } from '@/api/analysis'

const unitTree = ref([
  { id: 1, deptName: '一分厂' },
  { id: 2, deptName: '二分厂' },
  { id: 3, deptName: '三分厂' },
  { id: 4, deptName: '办公区' }
])
const energyTypes = ref([
  { id: 1, name: '电力' },
  { id: 2, name: '天然气' },
  { id: 3, name: '蒸汽' },
  { id: 4, name: '水' }
])

const queryParams = ref<ConsumptionListParams>({
  statPeriod: 'MONTH',
  energyUnitId: undefined,
  energyTypeId: undefined,
  startDate: undefined,
  endDate: undefined
})
const dateRange = ref<string[]>([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref<AnalysisConsumption[]>([])

const kpiCards = ref([
  { label: '总能耗', value: '0', unit: 'kgce', trend: undefined, color: '#0d9488' },
  { label: '同比变化', value: '-', unit: '%', trend: undefined, color: '#3b82f6' },
  { label: '环比变化', value: '-', unit: '%', trend: undefined, color: '#f59e0b' },
  { label: '电力占比', value: '-', unit: '%', trend: undefined, color: '#8b5cf6' }
])

const trendChartRef = ref<HTMLDivElement>()
const pieChartRef = ref<HTMLDivElement>()
let trendChart: echarts.ECharts | null = null
let pieChart: echarts.ECharts | null = null

function handleSearch() {
  if (dateRange.value && dateRange.value.length === 2) {
    queryParams.value.startDate = dateRange.value[0]
    queryParams.value.endDate = dateRange.value[1]
  } else {
    queryParams.value.startDate = undefined
    queryParams.value.endDate = undefined
  }
  pageNum.value = 1
  fetchData()
  fetchSummary()
  fetchTrend()
}

function handleReset() {
  queryParams.value = { statPeriod: 'MONTH' }
  dateRange.value = []
  handleSearch()
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getConsumptionList({
      ...queryParams.value,
      current: pageNum.value,
      size: pageSize.value
    })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch { /* empty */ } finally {
    loading.value = false
  }
}

async function fetchSummary() {
  try {
    const res = await getConsumptionSummary({
      energyUnitId: queryParams.value.energyUnitId,
      statPeriod: queryParams.value.statPeriod || 'MONTH',
      startDate: queryParams.value.startDate,
      endDate: queryParams.value.endDate,
      energyTypeId: queryParams.value.energyTypeId
    })
    const d = res.data as Record<string, unknown>
    kpiCards.value[0].value = (d.totalConsumption as number)?.toLocaleString() || '0'
    kpiCards.value[1].value = d.yoyRate !== null ? Number(d.yoyRate).toFixed(2) : '-'
    kpiCards.value[1].trend = d.yoyRate !== null ? Number(d.yoyRate) : undefined
    kpiCards.value[2].value = d.momRate !== null ? Number(d.momRate).toFixed(2) : '-'
    kpiCards.value[2].trend = d.momRate !== null ? Number(d.momRate) : undefined
    // 计算电力占比
    const typeRatio = d.typeRatio as Array<{ energyTypeId: string; total: string }> || []
    const electric = typeRatio.find((t: { energyTypeId: string; total: string }) => Number(t.energyTypeId) === 1)
    if (electric) {
      const totalSum = typeRatio.reduce((s: number, t: { total: string }) => s + Number(t.total), 0)
      kpiCards.value[3].value = totalSum > 0 ? ((Number(electric.total) / totalSum) * 100).toFixed(1) : '0'
    }
  } catch { /* empty */ }
}

async function fetchTrend() {
  try {
    const res = await getConsumptionTrend({
      energyUnitId: queryParams.value.energyUnitId,
      statPeriod: queryParams.value.statPeriod || 'MONTH',
      startDate: queryParams.value.startDate,
      endDate: queryParams.value.endDate,
      energyTypeId: queryParams.value.energyTypeId
    })
    const data = res.data as Array<{ statDate: string; total: string }>
    const dates = data.map(d => d.statDate)
    const values = data.map(d => Number(d.total))
    initTrendChart(dates, values)

    // 饼图
    const summaryRes = await getConsumptionSummary({
      energyUnitId: queryParams.value.energyUnitId,
      statPeriod: queryParams.value.statPeriod || 'MONTH',
      startDate: queryParams.value.startDate,
      endDate: queryParams.value.endDate
    })
    const typeRatio = (summaryRes.data as Record<string, unknown>).typeRatio as Array<{ energyTypeId: string; total: string }> || []
    const pieData = typeRatio.map((t: { energyTypeId: string; total: string }) => {
      const et = energyTypes.value.find(e => e.id === Number(t.energyTypeId))
      return { name: et?.name || '其他', value: Number(t.total) }
    })
    initPieChart(pieData)
  } catch { /* empty */ }
}

function initTrendChart(dates: string[], values: number[]) {
  if (!trendChartRef.value) return
  if (!trendChart) trendChart = echarts.init(trendChartRef.value)
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: dates, boundaryGap: false },
    yAxis: { type: 'value' },
    series: [{ type: 'line', data: values, smooth: true, areaStyle: { opacity: 0.3 }, itemStyle: { color: '#0d9488' } }]
  })
}

function initPieChart(data: Array<{ name: string; value: number }>) {
  if (!pieChartRef.value) return
  if (!pieChart) pieChart = echarts.init(pieChartRef.value)
  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left' },
    series: [{
      type: 'pie', radius: ['40%', '70%'],
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
      data
    }]
  })
}

async function handleDelete(row: AnalysisConsumption) {
  try {
    await ElMessageBox.confirm('确认删除该条数据？', '提示')
    await deleteConsumption(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch { /* empty */ }
}

function handleExport() {
  ElMessage.info('导出功能待实现')
}

onMounted(() => {
  fetchData()
  fetchSummary()
  fetchTrend()
  window.addEventListener('resize', () => {
    trendChart?.resize()
    pieChart?.resize()
  })
})

onUnmounted(() => {
  trendChart?.dispose()
  pieChart?.dispose()
})

watch(() => queryParams.value.statPeriod, () => { handleSearch() })
</script>

<style scoped lang="scss">
.analysis-consumption {
  .filter-card { margin-bottom: 16px; }
  .kpi-row { margin-bottom: 16px; }
  .kpi-card {
    margin-bottom: 16px;
    .kpi-label { font-size: 14px; color: #6b7280; margin-bottom: 8px; }
    .kpi-value { font-size: 26px; font-weight: bold; margin-bottom: 8px; }
    .kpi-unit { font-size: 14px; font-weight: normal; color: #6b7280; margin-left: 4px; }
    .kpi-compare { font-size: 13px; }
    .up { color: #dc2626; }
    .down { color: #10b981; }
  }
  .chart-row { margin-bottom: 16px; }
  .table-card { margin-top: 16px; }
  .table-header { display: flex; justify-content: space-between; align-items: center; }
  .pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
}
</style>