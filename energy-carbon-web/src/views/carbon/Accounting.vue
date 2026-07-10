<template>
  <div class="carbon-accounting">
    <!-- 顶部筛选与操作 -->
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="queryParams" class="filter-form">
        <el-form-item label="年度">
          <el-date-picker v-model="queryParams.accountingYear" type="year" placeholder="选择年份" value-format="YYYY" style="width:120px" />
        </el-form-item>
        <el-form-item label="用能单元">
          <el-tree-select
            v-model="queryParams.energyUnitId"
            :data="deptTree"
            :props="{ label: 'deptName', value: 'id', children: 'children' }"
            placeholder="请选择"
            clearable
            style="width:200px"
          />
        </el-form-item>
        <el-form-item label="选择模型">
          <el-select v-model="queryParams.modelId" placeholder="请选择" clearable style="width:140px">
            <el-option label="标准核算模型" value="standard" />
            <el-option label="行业核算模型" value="industry" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Refresh" @click="handleQuery">刷新</el-button>
          <el-button :icon="Document" @click="handleReport">碳排放报告</el-button>
          <el-button :icon="Download" @click="handleImportTemplate">导入模板</el-button>
          <el-button :icon="Upload" @click="handleImport">导入</el-button>
          <el-button type="warning" :icon="MagicStick" @click="handleAIAnalysis">AI 智能分析</el-button>
          <el-button type="primary" :icon="Plus" @click="handleAddActivity">新增活动</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 指标/图表 -->
    <el-row :gutter="16" class="dashboard-row">
      <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
        <el-card shadow="never" class="summary-card">
          <template #header>
            <span class="card-title">排放指标</span>
          </template>
          <el-table :data="summaryData" size="small" :show-header="false" class="summary-table">
            <el-table-column prop="label" />
            <el-table-column prop="value" align="right" width="120">
              <template #default="{ row }">
                <span class="summary-value">{{ row.value }}</span>
                <span class="summary-unit">tCO₂e</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="9" :lg="9" :xl="9">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <span class="card-title">碳排放分布</span>
          </template>
          <div ref="scopePieRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="9" :lg="9" :xl="9">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <span class="card-title">碳排放总量</span>
          </template>
          <div ref="monthBarRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 活动数据明细 -->
    <div v-for="group in groupedActivityData" :key="group.category" class="activity-card">
      <el-card shadow="never">
        <template #header>
          <div class="header-row">
            <span class="title">{{ group.category || '未分类' }}</span>
            <el-button type="primary" size="small" :icon="Plus" @click="handleAddActivity">新增活动</el-button>
          </div>
        </template>
        <div class="table-wrap">
          <el-table :data="group.items" border stripe size="small" v-loading="loading">
            <el-table-column type="index" label="序号" width="55" />
            <el-table-column prop="category" label="活动" width="150" show-overflow-tooltip />
            <el-table-column prop="emissionSource" label="排放源" width="120" show-overflow-tooltip />
            <el-table-column prop="activityName" label="活动名称" min-width="140" show-overflow-tooltip />
            <el-table-column prop="dataSource" label="数据来源" width="100" />
            <el-table-column prop="operator" label="运算符" width="80" />
            <el-table-column prop="relatedIndicator" label="关联指标" width="120" show-overflow-tooltip />
            <el-table-column prop="activityData" label="活动水平(数量)" width="130" align="right" />
            <el-table-column prop="activityUnit" label="计量单位" width="90" />
            <el-table-column prop="emission" label="排放量(tCO₂e)" width="140" align="right" />
            <el-table-column label="操作" width="140" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleEditActivity(row)">编辑</el-button>
                <el-button link type="danger" @click="handleDeleteActivity(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-card>
    </div>

    <!-- 新增/编辑活动弹窗 -->
    <el-dialog v-model="activityVisible" :title="activityFormIndex > -1 ? '编辑活动' : '新增活动'" width="650px">
      <el-form :model="activityForm" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="排放类别">
              <el-select v-model="activityForm.category" placeholder="请选择" style="width:100%">
                <el-option label="类别一：直接温室气体排放" value="类别一：直接温室气体排放" />
                <el-option label="类别二：输入能源间接排放" value="类别二：输入能源间接排放" />
                <el-option label="类别三：其他间接排放" value="类别三：其他间接排放" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排放源">
              <el-input v-model="activityForm.emissionSource" placeholder="如固定燃烧、移动燃烧" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="活动名称">
              <el-input v-model="activityForm.activityName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数据来源">
              <el-select v-model="activityForm.dataSource" placeholder="请选择" style="width:100%">
                <el-option label="录入" value="录入" />
                <el-option label="自动采集" value="自动采集" />
                <el-option label="系统计算" value="系统计算" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="运算符">
              <el-select v-model="activityForm.operator" placeholder="请选择" style="width:100%">
                <el-option label="+" value="+" />
                <el-option label="-" value="-" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关联指标">
              <el-input v-model="activityForm.relatedIndicator" placeholder="如能耗指标、产量指标" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="活动水平">
              <el-input-number v-model="activityForm.activityData" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计量单位">
              <el-select v-model="activityForm.activityUnit" placeholder="请选择" style="width:100%">
                <el-option label="t" value="t" />
                <el-option label="kWh" value="kWh" />
                <el-option label="m³" value="m³" />
                <el-option label="GJ" value="GJ" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="能源类型">
              <el-select v-model="activityForm.energyTypeId" placeholder="请选择" style="width:100%" clearable>
                <el-option v-for="et in energyTypes" :key="et.id" :label="et.typeName" :value="et.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排放因子">
              <el-input-number v-model="activityForm.emissionFactor" :min="0" :precision="6" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="activityVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveActivity">保存</el-button>
      </template>
    </el-dialog>

    <!-- 排放报告弹窗 -->
    <el-dialog v-model="reportVisible" title="碳排放报告" width="700px">
      <template v-if="reportData">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="核算名称">{{ reportData.accountingName }}</el-descriptions-item>
          <el-descriptions-item label="核算年份">{{ reportData.accountingYear }}</el-descriptions-item>
          <el-descriptions-item label="总排放量">{{ reportData.totalEmission }} {{ reportData.emissionUnit }}</el-descriptions-item>
          <el-descriptions-item label="期次">{{ reportData.accountingPeriod }}</el-descriptions-item>
          <el-descriptions-item label="范围一排放">{{ reportData.scope1Emission }} ({{ reportData.scope1Percent }}%)</el-descriptions-item>
          <el-descriptions-item label="范围二排放">{{ reportData.scope2Emission }} ({{ reportData.scope2Percent }}%)</el-descriptions-item>
          <el-descriptions-item label="范围三排放">{{ reportData.scope3Emission }} ({{ reportData.scope3Percent }}%)</el-descriptions-item>
        </el-descriptions>
        <div ref="reportChartRef" style="height:300px;margin-top:16px"></div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Document, Download, Upload, MagicStick, Plus } from '@element-plus/icons-vue'
import echarts from '@/utils/echarts'
import { getAccountingList, getAccountingDetail, getEmissionReportById, calculateEmission, deleteAccounting } from '@/api/carbon'
import { getDeptTree } from '@/api/dept'
import { getAllEnergyTypes } from '@/api/energyType'

const loading = ref(false)
const deptTree = ref<any[]>([])
const energyTypes = ref<any[]>([])
const currentAccounting = ref<any>(null)
const reportVisible = ref(false)
const reportData = ref<any>(null)
const reportChartRef = ref<HTMLElement | null>(null)
let reportChart: echarts.ECharts | null = null

const queryParams = reactive({
  accountingYear: new Date().getFullYear().toString(),
  energyUnitId: null as number | null,
  modelId: 'standard'
})

const summaryData = computed(() => {
  const details = currentAccounting.value?.details || []
  let total = 0, scope1 = 0, scope2 = 0, scope3 = 0
  details.forEach((d: any) => {
    const emission = Number(d.emission || 0)
    total += emission
    const category = d.category || ''
    if (category.includes('类别二')) scope2 += emission
    else if (category.includes('类别三')) scope3 += emission
    else scope1 += emission
  })
  return [
    { label: '碳排放总量', value: total.toFixed(2) },
    { label: '范围一', value: scope1.toFixed(2) },
    { label: '范围二', value: scope2.toFixed(2) },
    { label: '范围三', value: scope3.toFixed(2) }
  ]
})

const activityData = computed(() => currentAccounting.value?.details || [])

const groupedActivityData = computed(() => {
  const map: Record<string, any[]> = {}
  activityData.value.forEach((item: any) => {
    const key = item.category || '未分类'
    if (!map[key]) map[key] = []
    map[key].push(item)
  })
  return Object.keys(map).map(key => ({ category: key, items: map[key] }))
})

const scopePieRef = ref<HTMLElement | null>(null)
const monthBarRef = ref<HTMLElement | null>(null)
let scopePieChart: echarts.ECharts | null = null
let monthBarChart: echarts.ECharts | null = null

onMounted(() => { initData() })
onUnmounted(() => { disposeCharts() })

function disposeCharts() {
  scopePieChart?.dispose()
  monthBarChart?.dispose()
  reportChart?.dispose()
  scopePieChart = null
  monthBarChart = null
  reportChart = null
}

async function initData() {
  const [deptRes, energyRes] = await Promise.all([getDeptTree(), getAllEnergyTypes()])
  deptTree.value = deptRes.data || []
  energyTypes.value = energyRes.data || []
  await loadAccounting()
}

async function loadAccounting() {
  loading.value = true
  try {
    const params: any = { current: 1, size: 10 }
    if (queryParams.accountingYear) params.accountingYear = parseInt(queryParams.accountingYear)
    if (queryParams.energyUnitId) params.energyUnitId = queryParams.energyUnitId
    const res = await getAccountingList(params)
    const records = res.data.records || []
    if (records.length > 0) {
      const detailRes = await getAccountingDetail(records[0].id)
      currentAccounting.value = detailRes.data
    } else {
      currentAccounting.value = null
    }
    await nextTick()
    renderCharts()
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

function renderCharts() {
  renderScopePie()
  renderMonthBar()
}

function renderScopePie() {
  if (!scopePieRef.value) return
  scopePieChart?.dispose()
  scopePieChart = echarts.init(scopePieRef.value)
  const data = [
    { value: Number(summaryData.value[1].value), name: '范围一' },
    { value: Number(summaryData.value[2].value), name: '范围二' },
    { value: Number(summaryData.value[3].value), name: '范围三' }
  ].filter(d => d.value > 0)
  scopePieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} tCO₂e ({d}%)' },
    legend: { bottom: 0, icon: 'circle' },
    color: ['#0ea5e9', '#10b981', '#f59e0b'],
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['50%', '45%'],
      avoidLabelOverlap: true,
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { show: true, formatter: '{b}\n{d}%' },
      data: data.length ? data : [{ value: 0, name: '暂无数据' }]
    }]
  })
  window.addEventListener('resize', () => scopePieChart?.resize())
}

function renderMonthBar() {
  if (!monthBarRef.value) return
  monthBarChart?.dispose()
  monthBarChart = echarts.init(monthBarRef.value)

  const details = currentAccounting.value?.details || []
  const monthMap: Record<string, number> = {}
  details.forEach((d: any) => {
    const date = d.createTime ? new Date(d.createTime) : null
    if (date && queryParams.accountingYear && date.getFullYear() === parseInt(queryParams.accountingYear)) {
      const key = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`
      monthMap[key] = (monthMap[key] || 0) + Number(d.emission || 0)
    }
  })
  // 如果没有明细按创建时间月聚合，则展示所选年份 1-12 月均为 0
  const months: string[] = []
  const values: number[] = []
  const year = queryParams.accountingYear || new Date().getFullYear().toString()
  for (let i = 1; i <= 12; i++) {
    const key = `${year}-${String(i).padStart(2, '0')}`
    months.push(`${i}月`)
    values.push(Number(monthMap[key] || 0).toFixed(2) as any)
  }

  monthBarChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
    xAxis: {
      type: 'category',
      data: months,
      axisLine: { lineStyle: { color: '#cbd5e1' } },
      axisLabel: { color: '#64748b' }
    },
    yAxis: {
      type: 'value',
      name: 'tCO₂e',
      nameTextStyle: { color: '#64748b' },
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f1f5f9' } },
      axisLabel: { color: '#64748b' }
    },
    series: [{
      type: 'bar',
      data: values,
      barWidth: '50%',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#34d399' },
          { offset: 1, color: '#059669' }
        ]),
        borderRadius: [4, 4, 0, 0]
      }
    }]
  })
  window.addEventListener('resize', () => monthBarChart?.resize())
}

function handleQuery() { loadAccounting() }

async function handleReport() {
  if (!currentAccounting.value) { ElMessage.warning('请先选择核算记录'); return }
  try {
    const res = await getEmissionReportById(currentAccounting.value.id)
    reportData.value = res.data
    reportVisible.value = true
    await nextTick()
    renderReportChart()
  } catch (e) { console.error(e) }
}

function renderReportChart() {
  if (!reportChartRef.value || !reportData.value) return
  reportChart?.dispose()
  reportChart = echarts.init(reportChartRef.value)
  reportChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['范围一', '范围二', '范围三'] },
    yAxis: { type: 'value', name: 'tCO₂e' },
    series: [{
      type: 'bar',
      data: [reportData.value.scope1Emission, reportData.value.scope2Emission, reportData.value.scope3Emission],
      itemStyle: { color: (params: any) => ['#0ea5e9', '#10b981', '#f59e0b'][params.dataIndex] }
    }]
  })
  window.addEventListener('resize', () => reportChart?.resize())
}

function handleImportTemplate() { ElMessage.info('导入模板功能开发中') }
function handleImport() { ElMessage.info('导入功能开发中') }
function handleAIAnalysis() { ElMessage.info('AI 智能分析功能开发中') }

// 活动新增/编辑
const activityVisible = ref(false)
const activityFormIndex = ref(-1)
const activityForm = reactive({
  category: '类别一：直接温室气体排放',
  emissionSource: '',
  activityName: '',
  dataSource: '录入',
  operator: '+',
  relatedIndicator: '',
  activityData: 0 as number | null,
  activityUnit: 't',
  energyTypeId: null as number | null,
  emissionFactor: null as number | null,
  sourceDescription: ''
})

function resetActivityForm() {
  Object.assign(activityForm, {
    category: '类别一：直接温室气体排放',
    emissionSource: '',
    activityName: '',
    dataSource: '录入',
    operator: '+',
    relatedIndicator: '',
    activityData: 0,
    activityUnit: 't',
    energyTypeId: null,
    emissionFactor: null,
    sourceDescription: ''
  })
  activityFormIndex.value = -1
}

function handleAddActivity() {
  if (!currentAccounting.value) { ElMessage.warning('当前无核算记录，请先新增核算'); return }
  resetActivityForm()
  activityVisible.value = true
}

function handleEditActivity(row: any) {
  Object.assign(activityForm, {
    category: row.category || '类别一：直接温室气体排放',
    emissionSource: row.emissionSource || '',
    activityName: row.activityName || '',
    dataSource: row.dataSource || '录入',
    operator: row.operator || '+',
    relatedIndicator: row.relatedIndicator || '',
    activityData: row.activityData,
    activityUnit: row.activityUnit || 't',
    energyTypeId: row.energyTypeId,
    emissionFactor: row.emissionFactor,
    sourceDescription: row.sourceDescription || ''
  })
  activityFormIndex.value = activityData.value.findIndex((d: any) => d.id === row.id)
  activityVisible.value = true
}

async function handleSaveActivity() {
  if (!currentAccounting.value) return
  const payload = {
    accountingId: currentAccounting.value.id,
    activities: [{
      activityName: activityForm.activityName,
      energyTypeId: activityForm.energyTypeId,
      activityData: activityForm.activityData,
      activityUnit: activityForm.activityUnit,
      factorId: null,
      sourceDescription: activityForm.sourceDescription,
      category: activityForm.category,
      emissionSource: activityForm.emissionSource,
      dataSource: activityForm.dataSource,
      operator: activityForm.operator,
      relatedIndicator: activityForm.relatedIndicator
    }]
  }
  await calculateEmission(payload)
  ElMessage.success('保存成功')
  activityVisible.value = false
  await loadAccounting()
}

async function handleDeleteActivity(row: any) {
  await ElMessageBox.confirm('确认删除该活动数据？', '提示')
  // 通过 detail id 删除（后端暂缺明细删除接口，这里仅做前端过滤提示）
  ElMessage.warning('明细删除接口需后端补充，当前仅做提示')
}

watch(() => currentAccounting.value, () => {
  nextTick(() => renderCharts())
}, { deep: true })
</script>

<style scoped>
.carbon-accounting { padding: 16px; }
.filter-card { margin-bottom: 16px; }
.filter-form :deep(.el-form-item) { margin-bottom: 0; }
.dashboard-row { margin-bottom: 16px; }
.summary-card, .chart-card { height: 100%; }
.summary-card :deep(.el-card__body), .chart-card :deep(.el-card__body) { padding: 16px; height: calc(100% - 50px); box-sizing: border-box; }
.card-title { font-size: 15px; font-weight: 600; color: #1e293b; }
.summary-table { font-size: 14px; }
.summary-value { font-size: 18px; font-weight: 700; color: #059669; }
.summary-unit { font-size: 12px; color: #94a3b8; margin-left: 4px; }
.chart-container { width: 100%; height: 280px; }
.activity-card { margin-bottom: 0; }
.header-row { display: flex; justify-content: space-between; align-items: center; }
.title { font-size: 16px; font-weight: 600; }
.table-wrap { overflow-x: auto; }
</style>
