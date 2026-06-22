<template>
  <div class="report-energy">
    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <span class="title">能耗统计表</span>
          <div>
            <el-button :type="reportType === 'DAILY' ? 'primary' : ''" size="small" @click="switchType('DAILY')">日报</el-button>
            <el-button :type="reportType === 'MONTHLY' ? 'primary' : ''" size="small" @click="switchType('MONTHLY')">月报</el-button>
            <el-button :type="reportType === 'YEARLY' ? 'primary' : ''" size="small" @click="switchType('YEARLY')">年报</el-button>
            <el-button type="success" size="small" style="margin-left:8px" @click="handleExport">导出CSV</el-button>
          </div>
        </div>
      </template>

      <!-- 时间选择 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="开始日期">
          <el-date-picker v-model="startDate" type="date" placeholder="开始日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="endDate" type="date" placeholder="结束日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
        </el-form-item>
      </el-form>

      <!-- 能耗柱状图 -->
      <div ref="chartRef" style="height:320px;margin-bottom:16px"></div>

      <!-- 统计表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="statDate" label="统计日期" width="120" />
        <el-table-column prop="reportType" label="报表类型" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ row.reportType === 'DAILY' ? '日报' : row.reportType === 'MONTHLY' ? '月报' : '年报' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="energyTypeId" label="能源类型ID" width="100" />
        <el-table-column prop="consumption" label="消耗量" width="140" />
        <el-table-column prop="consumptionUnit" label="消耗单位" width="100" />
        <el-table-column prop="cost" label="成本(元)" width="140" />
        <el-table-column prop="costUnit" label="成本单位" width="100" />
      </el-table>
      <div class="pagination-row">
        <el-pagination
          v-model:current="queryParams.current"
          v-model:page-size="queryParams.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="getList"
          @size-change="getList"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import echarts from '@/utils/echarts'
import { getEnergySummaryList, getEnergySummaryByRange, exportEnergySummary } from '@/api/report'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({ current: 1, size: 10, reportType: 'MONTHLY', statDate: '' })
const reportType = ref('MONTHLY')
const startDate = ref('')
const endDate = ref('')
const chartRef = ref<HTMLElement | null>(null)

onMounted(() => { getList() })

async function getList() {
  loading.value = true
  try {
    const params: any = { current: queryParams.current, size: queryParams.size, reportType: reportType.value }
    if (queryParams.statDate) params.statDate = queryParams.statDate
    const res = await getEnergySummaryList(params)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (e: any) { console.error(e) } finally { loading.value = false }
}

function switchType(type: string) {
  reportType.value = type
  queryParams.current = 1
  getList()
}

async function handleQuery() {
  if (startDate.value && endDate.value) {
    loading.value = true
    try {
      const res = await getEnergySummaryByRange(reportType.value, startDate.value, endDate.value)
      tableData.value = res.data
      total.value = res.data.length
      await nextTick()
      renderChart(res.data)
    } catch (e: any) { console.error(e) } finally { loading.value = false }
  } else {
    getList()
  }
}

async function handleExport() {
  try {
    const res = await exportEnergySummary({ reportType: reportType.value, startDate: startDate.value || undefined, endDate: endDate.value || undefined })
    const blob = new Blob([res as any], { type: 'text/csv;charset=UTF-8' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url; a.download = `能耗统计_${reportType.value}_${new Date().toISOString().slice(0,10)}.csv`; a.click()
    URL.revokeObjectURL(url)
  } catch (e) { console.error(e) }
}

function renderChart(data: any[]) {
  if (!chartRef.value) return
  const chart = echarts.init(chartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['消耗量', '成本'] },
    xAxis: { type: 'category', data: data.map(d => d.statDate) },
    yAxis: [{ type: 'value', name: '消耗量' }, { type: 'value', name: '成本(元)' }],
    series: [
      { name: '消耗量', type: 'bar', data: data.map(d => d.consumption || 0) },
      { name: '成本', type: 'line', yAxisIndex: 1, data: data.map(d => d.cost || 0) }
    ]
  })
  window.addEventListener('resize', () => chart.resize())
}

// Watch for chart rendering on list load
const origGetList = getList
getList = async () => {
  await origGetList()
  await nextTick()
  if (tableData.value.length > 0 && chartRef.value) {
    const chart = echarts.init(chartRef.value)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['消耗量', '成本'] },
      xAxis: { type: 'category', data: tableData.value.map(d => d.statDate) },
      yAxis: [{ type: 'value', name: '消耗量' }, { type: 'value', name: '成本(元)' }],
      series: [
        { name: '消耗量', type: 'bar', data: tableData.value.map(d => d.consumption || 0) },
        { name: '成本', type: 'line', yAxisIndex: 1, data: tableData.value.map(d => d.cost || 0) }
      ]
    })
    window.addEventListener('resize', () => chart.resize())
  }
}
</script>

<style scoped>
.report-energy { padding: 16px; }
.header-row { display: flex; justify-content: space-between; align-items: center; }
.title { font-size: 16px; font-weight: 600; }
.search-form { margin-bottom: 10px; }
.pagination-row { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>