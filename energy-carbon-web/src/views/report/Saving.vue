<template>
  <div class="report-saving">
    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <span class="title">节能量统计表</span>
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

      <!-- 柱状对比图：基准值 vs 实际值 -->
      <div ref="chartRef" style="height:320px;margin-bottom:16px"></div>

      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="statDate" label="统计日期" width="120" />
        <el-table-column prop="reportType" label="报表类型" width="80">
          <template #default="{ row }">
            <el-tag size="small">{{ row.reportType === 'DAILY' ? '日报' : row.reportType === 'MONTHLY' ? '月报' : '年报' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="baselineConsumption" label="基准消耗量" width="140" />
        <el-table-column prop="actualConsumption" label="实际消耗量" width="140" />
        <el-table-column prop="savingAmount" label="节能量" width="140">
          <template #default="{ row }">
            <span :style="{ color: (row.savingAmount || 0) >= 0 ? '#67C23A' : '#F56C6C', fontWeight: 'bold' }">
              {{ row.savingAmount != null ? row.savingAmount : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="savingRate" label="节能率(%)" width="120">
          <template #default="{ row }">
            <span :style="{ color: (row.savingRate || 0) >= 0 ? '#67C23A' : '#F56C6C', fontWeight: 'bold' }">
              {{ row.savingRate != null ? row.savingRate + '%' : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="savingAmountUnit" label="单位" width="80" />
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
import { getEnergySavingList, getEnergySavingByRange } from '@/api/report'

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
    const res = await getEnergySavingList(params)
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
      const res = await getEnergySavingByRange(reportType.value, startDate.value, endDate.value)
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
    tooltip: { trigger: 'axis' },
    legend: { data: ['基准消耗量', '实际消耗量', '节能量'] },
    xAxis: { type: 'category', data: data.map(d => d.statDate) },
    yAxis: { type: 'value' },
    series: [
      { name: '基准消耗量', type: 'bar', data: data.map(d => d.baselineConsumption || 0), itemStyle: { color: '#909399' } },
      { name: '实际消耗量', type: 'bar', data: data.map(d => d.actualConsumption || 0), itemStyle: { color: '#409EFF' } },
      { name: '节能量', type: 'line', data: data.map(d => d.savingAmount || 0), itemStyle: { color: '#67C23A' } }
    ]
  })
  window.addEventListener('resize', () => chart.resize())
}
</script>

<style scoped>
.report-saving { padding: 16px; width: 100%; box-sizing: border-box; }
.header-row { display: flex; justify-content: space-between; align-items: center; }
.title { font-size: 16px; font-weight: 600; }
.search-form { margin-bottom: 10px; }
.pagination-row { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>