<template>
  <div class="report-cost">
    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <span class="title">用能成本统计</span>
          <div>
            <el-button :type="reportType === 'DAILY' ? 'primary' : ''" size="small" @click="switchType('DAILY')">日报</el-button>
            <el-button :type="reportType === 'MONTHLY' ? 'primary' : ''" size="small" @click="switchType('MONTHLY')">月报</el-button>
            <el-button :type="reportType === 'YEARLY' ? 'primary' : ''" size="small" @click="switchType('YEARLY')">年报</el-button>
          </div>
        </div>
      </template>

      <el-form :inline="true" class="search-form">
        <el-form-item label="开始日期">
          <el-date-picker v-model="startDate" type="date" placeholder="开始日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="endDate" type="date" placeholder="结束日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="能源类型ID">
          <el-input-number v-model="energyTypeId" :min="0" placeholder="全部" style="width:140px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
        </el-form-item>
      </el-form>

      <!-- 成本趋势图 -->
      <div ref="chartRef" style="height:320px;margin-bottom:16px"></div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="statDate" label="统计日期" width="120" />
        <el-table-column prop="reportType" label="报表类型" width="80">
          <template #default="{ row }">
            <el-tag size="small">{{ row.reportType === 'DAILY' ? '日报' : row.reportType === 'MONTHLY' ? '月报' : '年报' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="energyTypeId" label="能源类型ID" width="120" />
        <el-table-column prop="cost" label="成本" width="150">
          <template #default="{ row }">
            <span style="font-weight:bold;color:#F56C6C">{{ row.cost != null ? row.cost : '-' }}</span>
          </template>
        </el-table-column>
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
import * as echarts from 'echarts'
import { getCostStatList, getCostStatByRange } from '@/api/report'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({ current: 1, size: 10, reportType: 'MONTHLY', statDate: '' })
const reportType = ref('MONTHLY')
const startDate = ref('')
const endDate = ref('')
const energyTypeId = ref<number | null>(null)
const chartRef = ref<HTMLElement | null>(null)

onMounted(() => { getList() })

async function getList() {
  loading.value = true
  try {
    const params: any = { current: queryParams.current, size: queryParams.size, reportType: reportType.value }
    if (energyTypeId.value) params.energyTypeId = energyTypeId.value
    const res = await getCostStatList(params)
    tableData.value = res.data.records
    total.value = res.data.total
    await nextTick()
    renderChart(res.data.records)
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
      const res = await getCostStatByRange(reportType.value, startDate.value, endDate.value)
      tableData.value = res.data
      total.value = res.data.length
      await nextTick()
      renderChart(res.data)
    } catch (e: any) { console.error(e) } finally { loading.value = false }
  } else {
    getList()
  }
}

function renderChart(data: any[]) {
  if (!chartRef.value || data.length === 0) return
  const chart = echarts.init(chartRef.value)
  chart.setOption({
    title: { text: '成本趋势', left: 'center' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: data.map(d => d.statDate) },
    yAxis: { type: 'value', name: '成本(元)' },
    series: [{
      type: 'line',
      data: data.map(d => d.cost || 0),
      areaStyle: { color: 'rgba(245,108,108,0.15)' },
      lineStyle: { color: '#F56C6C', width: 2 },
      itemStyle: { color: '#F56C6C' }
    }]
  })
  window.addEventListener('resize', () => chart.resize())
}
</script>

<style scoped>
.report-cost { padding: 16px; }
.header-row { display: flex; justify-content: space-between; align-items: center; }
.title { font-size: 16px; font-weight: 600; }
.search-form { margin-bottom: 10px; }
.pagination-row { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>