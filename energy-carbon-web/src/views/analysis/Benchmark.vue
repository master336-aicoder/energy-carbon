<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">能效对标</span>
          <el-button type="primary" @click="handleAdd">新增对标</el-button>
        </div>
      </template>
      <el-form :inline="true" class="search-form">
        <el-form-item label="对标名称">
          <el-input v-model="searchForm.benchmarkName" placeholder="请输入对标名称" clearable />
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
        <el-col :xs="24" :lg="12">
          <el-card>
            <template #header><span>对标雷达图</span></template>
            <div ref="radarChartRef" style="height: 350px"></div>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="12">
          <el-card>
            <template #header><span>差距分析</span></template>
            <div ref="gapChartRef" style="height: 350px"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="benchmarkName" label="对标名称" width="160" />
        <el-table-column prop="industry" label="行业" width="140" />
        <el-table-column prop="unitName" label="用能单元" width="140" />
        <el-table-column prop="benchmarkValue" label="对标值" width="120" />
        <el-table-column prop="unit" label="单位" width="100" />
        <el-table-column prop="ourValue" label="我方值" width="120" />
        <el-table-column prop="gap" label="差距" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '异常' }}</el-tag>
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
        <el-form-item label="对标名称">
          <el-input v-model="form.benchmarkName" placeholder="请输入对标名称" />
        </el-form-item>
        <el-form-item label="行业">
          <el-input v-model="form.industry" placeholder="请输入行业" />
        </el-form-item>
        <el-form-item label="用能单元">
          <el-select v-model="form.unitId" placeholder="请选择用能单元">
            <el-option v-for="u in unitList" :key="u.id" :label="u.unitName" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="对标值">
          <el-input-number v-model="form.benchmarkValue" :precision="2" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="我方值">
          <el-input-number v-model="form.ourValue" :precision="2" />
        </el-form-item>
        <el-form-item label="差距">
          <el-input-number v-model="form.gap" :precision="2" />
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
import { getBenchmarkList, createBenchmark, updateBenchmark, deleteBenchmark } from '@/api/efficiency'
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

const searchForm = reactive({ benchmarkName: '', unitId: undefined })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const dialogTitle = ref('新增对标')
const form = reactive({ id: undefined, benchmarkName: '', industry: '', unitId: undefined, benchmarkValue: 0, unit: '', ourValue: 0, gap: 0, status: 1 })

const radarChartRef = ref<HTMLDivElement>()
const gapChartRef = ref<HTMLDivElement>()
let radarChart: echarts.ECharts | null = null
let gapChart: echarts.ECharts | null = null

async function fetchList() {
  loading.value = true
  try {
    const res = await getBenchmarkList({ current: pagination.pageNum, size: pagination.pageSize, ...searchForm })
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
    const res = await getBenchmarkList({ current: 1, size: 9999, ...searchForm })
    const list = (res.data.records || res.data.list || []) as Array<{
      benchmarkName: string
      industry: string
      benchmarkValue: number
      ourValue: number
      gap: number
    }>
    initRadarChart(list)
    initGapChart(list)
  } catch (error) {
    console.error(error)
  }
}

function initRadarChart(list: Array<{ benchmarkName: string; industry: string; benchmarkValue: number; ourValue: number }>) {
  if (!radarChartRef.value) return
  if (!radarChart) radarChart = echarts.init(radarChartRef.value)
  const indicator = list.map(item => ({ name: item.benchmarkName, max: Math.max(item.benchmarkValue, item.ourValue) * 1.2 || 1 }))
  radarChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { data: ['对标值', '我方值'], bottom: 0 },
    radar: {
      indicator,
      radius: '65%',
      center: ['50%', '45%']
    },
    series: [{
      type: 'radar',
      data: [
        { value: list.map(item => item.benchmarkValue), name: '对标值', itemStyle: { color: '#3b82f6' }, areaStyle: { opacity: 0.2 } },
        { value: list.map(item => item.ourValue), name: '我方值', itemStyle: { color: '#10b981' }, areaStyle: { opacity: 0.2 } }
      ]
    }]
  })
}

function initGapChart(list: Array<{ benchmarkName: string; gap: number }>) {
  if (!gapChartRef.value) return
  if (!gapChart) gapChart = echarts.init(gapChartRef.value)
  const names = list.map(item => item.benchmarkName)
  const values = list.map(item => Number(item.gap) || 0)
  gapChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: names, axisLabel: { interval: 0, rotate: 30 } },
    yAxis: { type: 'value' },
    series: [{
      type: 'bar',
      data: values,
      itemStyle: {
        color: (params: any) => Number(params.value) >= 0 ? '#ef4444' : '#10b981',
        borderRadius: [4, 4, 0, 0]
      }
    }]
  })
}

function handleSearch() { pagination.pageNum = 1; fetchList(); fetchChartData() }
function handleReset() { searchForm.benchmarkName = ''; searchForm.unitId = undefined; handleSearch() }
function handleSizeChange(val) { pagination.pageSize = val; fetchList() }
function handleCurrentChange(val) { pagination.pageNum = val; fetchList() }

function handleAdd() {
  dialogTitle.value = '新增对标'
  Object.assign(form, { id: undefined, benchmarkName: '', industry: '', unitId: undefined, benchmarkValue: 0, unit: '', ourValue: 0, gap: 0, status: 1 })
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑对标'
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  try {
    if (form.id) {
      await updateBenchmark(form.id, JSON.parse(JSON.stringify(form)))
    } else {
      await createBenchmark(JSON.parse(JSON.stringify(form)))
    }
    ElMessage.success(dialogTitle.value + '成功')
    dialogVisible.value = false
    fetchList()
  } catch (error) {
    console.error(error)
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确认删除该记录？', '提示', { type: 'warning' })
    await deleteBenchmark(row.id)
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
    radarChart?.resize()
    gapChart?.resize()
  })
})

onUnmounted(() => {
  radarChart?.dispose()
  gapChart?.dispose()
})
</script>

<style scoped lang="scss">
.page-container { padding: 10px; }
.search-form { margin-bottom: 20px; }
.chart-row { margin-bottom: 20px; }
.pagination { margin-top: 20px; justify-content: flex-end; }
</style>
