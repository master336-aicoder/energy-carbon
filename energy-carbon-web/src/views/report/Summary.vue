<template>
  <div class="report-summary">
    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <span class="title">能耗汇总表</span>
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
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
        </el-form-item>
      </el-form>

      <!-- 饼图：各能源品种占比 -->
      <div style="display:flex;gap:16px;margin-bottom:16px">
        <div ref="pieChartRef" style="height:300px;flex:1"></div>
        <div ref="barChartRef" style="height:300px;flex:1"></div>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="statDate" label="统计日期" width="120" />
        <el-table-column prop="reportType" label="报表类型" width="80">
          <template #default="{ row }">
            <el-tag size="small">{{ row.reportType === 'DAILY' ? '日报' : row.reportType === 'MONTHLY' ? '月报' : '年报' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalConsumption" label="总消耗量" width="140" />
        <el-table-column prop="totalCost" label="总成本(元)" width="140" />
        <el-table-column prop="comparisonPeriod" label="对比期" min-width="120" />
        <el-table-column prop="comparisonChangeRate" label="同比变化率(%)" width="140">
          <template #default="{ row }">
            <span :style="{ color: (row.comparisonChangeRate || 0) > 0 ? '#F56C6C' : (row.comparisonChangeRate || 0) < 0 ? '#67C23A' : '' }">
              {{ row.comparisonChangeRate != null ? row.comparisonChangeRate + '%' : '-' }}
            </span>
          </template>
        </el-table-column>
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
import { getEnergyTotalList, getEnergyTotalByRange } from '@/api/report'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({ current: 1, size: 10, reportType: 'MONTHLY', statDate: '' })
const reportType = ref('MONTHLY')
const startDate = ref('')
const endDate = ref('')
const pieChartRef = ref<HTMLElement | null>(null)
const barChartRef = ref<HTMLElement | null>(null)

onMounted(() => { getList() })

async function getList() {
  loading.value = true
  try {
    const params: any = { current: queryParams.current, size: queryParams.size, reportType: reportType.value }
    const res = await getEnergyTotalList(params)
    tableData.value = res.data.records
    total.value = res.data.total
    await nextTick()
    renderCharts(res.data.records)
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
      const res = await getEnergyTotalByRange(reportType.value, startDate.value, endDate.value)
      tableData.value = res.data
      total.value = res.data.length
      await nextTick()
      renderCharts(res.data)
    } catch (e: any) { console.error(e) } finally { loading.value = false }
  } else {
    getList()
  }
}

function renderCharts(data: any[]) {
  if (!pieChartRef.value || !barChartRef.value || data.length === 0) return

  // 饼图 - 模拟各能源品种占比
  const pieChart = echarts.init(pieChartRef.value)
  pieChart.setOption({
    title: { text: '能耗占比', left: 'center' },
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { name: '总消耗量', value: data.reduce((s: number, d: any) => s + (d.totalConsumption || 0), 0) },
        { name: '总成本(元)', value: data.reduce((s: number, d: any) => s + (d.totalCost || 0), 0) }
      ],
      emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.3)' } }
    }]
  })

  // 柱状图 - 总消耗和总成本趋势
  const barChart = echarts.init(barChartRef.value)
  barChart.setOption({
    title: { text: '能耗趋势', left: 'center' },
    tooltip: { trigger: 'axis' },
    legend: { data: ['总消耗量', '总成本'], bottom: 0 },
    xAxis: { type: 'category', data: data.map(d => d.statDate) },
    yAxis: { type: 'value' },
    series: [
      { name: '总消耗量', type: 'bar', data: data.map(d => d.totalConsumption || 0) },
      { name: '总成本', type: 'line', data: data.map(d => d.totalCost || 0) }
    ]
  })

  window.addEventListener('resize', () => { pieChart.resize(); barChart.resize() })
}
</script>

<style scoped>
.report-summary { padding: 16px; }
.header-row { display: flex; justify-content: space-between; align-items: center; }
.title { font-size: 16px; font-weight: 600; }
.search-form { margin-bottom: 10px; }
.pagination-row { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>