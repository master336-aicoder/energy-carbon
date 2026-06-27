<template>
  <div class="monitor-history">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>历史数据查询</span>
          <el-button type="primary" @click="handleExport">导出数据</el-button>
        </div>
      </template>

      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="计量器具">
          <el-select v-model="searchForm.meterId" placeholder="请选择计量器具" clearable filterable style="width: 200px">
            <el-option v-for="m in meterList" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="数据类型">
          <el-select v-model="searchForm.dataType" placeholder="请选择数据类型" clearable style="width: 140px">
            <el-option v-for="t in dataTypeList" :key="t" :label="t" :value="t" />
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
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="chart-container" v-loading="chartLoading">
        <div ref="chartRef" class="chart"></div>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe style="margin-top: 20px">
        <el-table-column prop="dataTime" label="数据时间" width="180" />
        <el-table-column prop="meterName" label="计量器具" width="140" />
        <el-table-column prop="dataValue" label="数据值" width="120">
          <template #default="{ row }">
            {{ row.dataValue }} {{ row.unit || '' }}
          </template>
        </el-table-column>
        <el-table-column prop="dataType" label="数据类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.dataType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="记录时间" width="180" />
      </el-table>

      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getMonitorHistoryList, getMonitorHistoryChart, getMonitorDataTypes } from '@/api/monitorHistory'
import { getMeterList } from '@/api/meter'
import type { MonitorHistoryData, Meter } from '@/types'
import echarts from '@/utils/echarts'

const loading = ref(false)
const chartLoading = ref(false)
const tableData = ref<MonitorHistoryData[]>([])
const meterList = ref<Meter[]>([])
const dataTypeList = ref<string[]>([])
const chartRef = ref<HTMLDivElement>()
let chartInstance: echarts.ECharts | null = null

const searchForm = reactive({
  meterId: undefined as number | undefined,
  dataType: undefined as string | undefined,
  startTime: '',
  endTime: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

async function fetchList() {
  if (!searchForm.startTime || !searchForm.endTime) {
    ElMessage.warning('请选择时间范围')
    return
  }
  loading.value = true
  try {
    const res = await getMonitorHistoryList({
      current: pagination.pageNum,
      size: pagination.pageSize,
      meterId: searchForm.meterId,
      dataType: searchForm.dataType,
      startTime: searchForm.startTime,
      endTime: searchForm.endTime
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

async function fetchChart() {
  if (!searchForm.startTime || !searchForm.endTime) {
    return
  }
  chartLoading.value = true
  try {
    const res = await getMonitorHistoryChart({
      meterId: searchForm.meterId || 0,
      dataType: searchForm.dataType,
      startTime: searchForm.startTime,
      endTime: searchForm.endTime
    })
    renderChart(res.data)
  } finally {
    chartLoading.value = false
  }
}

function renderChart(data: MonitorHistoryData[]) {
  if (!chartRef.value) return
  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value)
  }
  const times = data.map(d => d.dataTime.slice(0, 19).replace('T', ' '))
  const values = data.map(d => d.dataValue)
  const option: echarts.EChartsOption = {
    title: { text: '历史数据曲线', left: 'center' },
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        const p = params[0]
        return `${p.axisValue}<br/>${p.seriesName}: ${p.value}`
      }
    },
    toolbox: {
      feature: {
        dataZoom: { show: true },
        restore: { show: true },
        saveAsImage: { show: true }
      }
    },
    dataZoom: [
      { type: 'inside', start: 0, end: 100 },
      { type: 'slider', start: 0, end: 100 }
    ],
    xAxis: {
      type: 'category',
      data: times,
      boundaryGap: false,
      axisLabel: { rotate: 45 }
    },
    yAxis: { type: 'value', name: data.length > 0 ? data[0].unit || '' : '' },
    series: [{
      name: data.length > 0 ? data[0].dataType : '数据值',
      type: 'line',
      data: values,
      smooth: true,
      lineStyle: { width: 2 },
      itemStyle: { color: '#409EFF' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64,158,255,0.3)' },
          { offset: 1, color: 'rgba(64,158,255,0.05)' }
        ])
      }
    }],
    grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true }
  }
  chartInstance.setOption(option, true)
}

async function fetchMeters() {
  const res = await getMeterList({ current: 1, size: 9999 })
  meterList.value = res.data.records
}

async function fetchDataTypes() {
  try {
    const res = await getMonitorDataTypes()
    dataTypeList.value = res.data
  } catch {
    dataTypeList.value = ['电量', '功率', '电压', '电流', '温度', '压力']
  }
}

function handleSearch() {
  pagination.pageNum = 1
  fetchList()
  fetchChart()
}

function handleReset() {
  searchForm.meterId = undefined
  searchForm.dataType = undefined
  searchForm.startTime = ''
  searchForm.endTime = ''
  pagination.pageNum = 1
  tableData.value = []
  if (chartInstance) {
    chartInstance.clear()
  }
}

function handleSizeChange(val: number) {
  pagination.pageSize = val
  fetchList()
}

function handleCurrentChange(val: number) {
  pagination.pageNum = val
  fetchList()
}

function handleExport() {
  ElMessage.success('导出功能开发中')
}

function handleResize() {
  chartInstance?.resize()
}

watch(() => searchForm.meterId, () => {
  if (searchForm.startTime && searchForm.endTime) {
    fetchChart()
  }
})

onMounted(() => {
  fetchMeters()
  fetchDataTypes()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>

<style scoped lang="scss">
.monitor-history {
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
  height: 400px;
  margin-bottom: 20px;
}

.chart {
  width: 100%;
  height: 100%;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>