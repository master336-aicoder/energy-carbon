<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">能效分析</span>
          <el-button type="primary" @click="handleAdd">新增记录</el-button>
        </div>
      </template>
      <el-form :inline="true" class="search-form">
        <el-form-item label="指标名称">
          <el-input v-model="searchForm.analysisName" placeholder="请输入指标名称" clearable />
        </el-form-item>
        <el-form-item label="用能单元">
          <el-select v-model="searchForm.unitId" placeholder="请选择用能单元" clearable>
            <el-option v-for="u in unitList" :key="u.id" :label="u.unitName" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 图表看板 -->
      <el-row :gutter="20" class="chart-row">
        <el-col :xs="24" :lg="16">
          <el-card>
            <template #header><span>能效值 vs 标准值</span></template>
            <div ref="barChartRef" style="height: 350px"></div>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="8">
          <el-card>
            <template #header><span>状态分布</span></template>
            <div ref="pieChartRef" style="height: 350px"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="analysisName" label="分析名称" width="140" />
        <el-table-column prop="deviceName" label="设备名称" width="140" />
        <el-table-column prop="efficiencyValue" label="能效值" width="120" />
        <el-table-column prop="unit" label="单位" width="100" />
        <el-table-column prop="standardValue" label="标准值" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '异常' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="分析名称">
          <el-input v-model="form.analysisName" placeholder="请输入分析名称" />
        </el-form-item>
        <el-form-item label="设备名称">
          <el-input v-model="form.deviceName" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="能效值">
          <el-input-number v-model="form.efficiencyValue" :precision="2" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="标准值">
          <el-input-number v-model="form.standardValue" :precision="2" />
        </el-form-item>
        <el-form-item label="用能单元">
          <el-select v-model="form.unitId" placeholder="请选择用能单元">
            <el-option v-for="u in unitList" :key="u.id" :label="u.unitName" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">异常</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import echarts from '@/utils/echarts'
import { getEfficiencyAnalysisList, createEfficiencyAnalysis, updateEfficiencyAnalysis, deleteEfficiencyAnalysis } from '@/api/efficiency'
import { getDeptList } from '@/api/dept'

const loading = ref(false)
const tableData = ref([])
const unitList = ref<{ id: number; unitName: string }[]>([])

async function loadUnitList() {
  try {
    const res = await getDeptList()
    const depts = res.data || []
    unitList.value = depts.map((d: any) => ({ id: d.deptId || d.id, unitName: d.deptName || d.name }))
  } catch {
    unitList.value = []
  }
}

const searchForm = reactive({ analysisName: '', unitId: undefined as number | undefined })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const dialogTitle = ref('新增记录')
const form = reactive({ id: undefined as number | undefined, analysisName: '', deviceName: '', efficiencyValue: 0, unit: '', standardValue: 0, unitId: undefined as number | undefined, status: 1 })

const barChartRef = ref<HTMLDivElement>()
const pieChartRef = ref<HTMLDivElement>()
let barChart: echarts.ECharts | null = null
let pieChart: echarts.ECharts | null = null

async function fetchList() {
  loading.value = true
  try {
    const res = await getEfficiencyAnalysisList({ current: pagination.pageNum, size: pagination.pageSize, ...searchForm })
    tableData.value = res.data.records || res.data.list || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

async function fetchChartData() {
  try {
    const res = await getEfficiencyAnalysisList({ current: 1, size: 9999, ...searchForm })
    const list = (res.data.records || res.data.list || []) as Array<{
      analysisName: string
      efficiencyValue: number
      standardValue: number
      status: number
    }>
    initBarChart(list)
    initPieChart(list)
  } catch (error) {
    console.error(error)
  }
}

function initBarChart(list: Array<{ analysisName: string; efficiencyValue: number; standardValue: number }>) {
  if (!barChartRef.value) return
  if (!barChart) barChart = echarts.init(barChartRef.value)
  const names = list.map(item => item.analysisName)
  barChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: { data: ['能效值', '标准值'], bottom: 0 },
    grid: { left: '3%', right: '4%', bottom: '10%', containLabel: true },
    xAxis: { type: 'category', data: names, axisLabel: { interval: 0, rotate: 30 } },
    yAxis: { type: 'value' },
    series: [
      { type: 'bar', name: '能效值', data: list.map(item => item.efficiencyValue), itemStyle: { color: '#3b82f6', borderRadius: [4, 4, 0, 0] } },
      { type: 'bar', name: '标准值', data: list.map(item => item.standardValue), itemStyle: { color: '#10b981', borderRadius: [4, 4, 0, 0] } }
    ]
  })
}

function initPieChart(list: Array<{ status: number }>) {
  if (!pieChartRef.value) return
  if (!pieChart) pieChart = echarts.init(pieChartRef.value)
  const normal = list.filter(item => item.status === 1).length
  const abnormal = list.length - normal
  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
      data: [
        { name: '正常', value: normal, itemStyle: { color: '#10b981' } },
        { name: '异常', value: abnormal, itemStyle: { color: '#ef4444' } }
      ]
    }]
  })
}

function handleSearch() { pagination.pageNum = 1; fetchList(); fetchChartData() }
function handleReset() { searchForm.analysisName = ''; searchForm.unitId = undefined; handleSearch() }
function handleSizeChange(val: number) { pagination.pageSize = val; fetchList() }
function handleCurrentChange(val: number) { pagination.pageNum = val; fetchList() }

function handleAdd() {
  dialogTitle.value = '新增记录'
  Object.assign(form, { id: undefined, analysisName: '', deviceName: '', efficiencyValue: 0, unit: '', standardValue: 0, unitId: undefined, status: 1 })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  dialogTitle.value = '编辑记录'
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  try {
    if (form.id) {
      await updateEfficiencyAnalysis(form.id, JSON.parse(JSON.stringify(form)))
    } else {
      await createEfficiencyAnalysis(JSON.parse(JSON.stringify(form)))
    }
    ElMessage.success(dialogTitle.value + '成功')
    dialogVisible.value = false
    fetchList()
  } catch (error) {
    console.error(error)
  }
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm('确认删除该记录？', '提示', { type: 'warning' })
    await deleteEfficiencyAnalysis(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch (error) {
    // user cancelled or error
  }
}

onMounted(() => {
  loadUnitList()
  fetchList()
  nextTick(() => fetchChartData())
  window.addEventListener('resize', () => {
    barChart?.resize()
    pieChart?.resize()
  })
})

onUnmounted(() => {
  barChart?.dispose()
  pieChart?.dispose()
})
</script>

<style scoped lang="scss">
.page-container { padding: 10px; }
.search-form { margin-bottom: 20px; }
.chart-row { margin-bottom: 20px; }
.pagination { margin-top: 20px; justify-content: flex-end; }
</style>
