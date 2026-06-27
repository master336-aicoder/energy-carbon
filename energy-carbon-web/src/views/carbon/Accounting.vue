<template>
  <div class="carbon-accounting">
    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <span class="title">碳排放核算</span>
          <el-button type="primary" @click="handleAdd">新增核算</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="核算名称">
          <el-input v-model="queryParams.accountingName" placeholder="请输入" clearable style="width:180px" />
        </el-form-item>
        <el-form-item label="核算年份">
          <el-date-picker v-model="queryParams.accountingYear" type="year" placeholder="选择年份" value-format="YYYY" style="width:140px" />
        </el-form-item>
        <el-form-item label="核算期次">
          <el-select v-model="queryParams.accountingPeriod" placeholder="全部" clearable style="width:120px">
            <el-option label="年度" value="YEARLY" />
            <el-option label="季度" value="QUARTERLY" />
            <el-option label="月度" value="MONTHLY" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:120px">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已提交" value="SUBMITTED" />
            <el-option label="已审核" value="APPROVED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="accountingName" label="核算名称" min-width="160" />
        <el-table-column prop="accountingYear" label="核算年份" width="100" />
        <el-table-column prop="accountingPeriod" label="期次" width="80">
          <template #default="{ row }">
            <el-tag :type="row.accountingPeriod === 'YEARLY' ? '' : row.accountingPeriod === 'QUARTERLY' ? 'warning' : 'info'" size="small">
              {{ row.accountingPeriod === 'YEARLY' ? '年度' : row.accountingPeriod === 'QUARTERLY' ? '季度' : '月度' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalEmission" label="总排放量(tCO2e)" width="150" />
        <el-table-column prop="scopeType" label="核算范围" width="120">
          <template #default="{ row }">
            {{ row.scopeType === 'SCOPE1' ? '范围一' : row.scopeType === 'SCOPE2' ? '范围二' : row.scopeType === 'SCOPE3' ? '范围三' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'APPROVED' ? 'success' : row.status === 'SUBMITTED' ? 'warning' : 'info'" size="small">
              {{ row.status === 'DRAFT' ? '草稿' : row.status === 'SUBMITTED' ? '已提交' : '已审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
            <el-button link type="primary" @click="handleReport(row)">报告</el-button>
            <el-button link type="primary" v-if="row.status === 'DRAFT'" @click="handleSubmit(row)">提交</el-button>
            <el-button link type="primary" v-if="row.status === 'SUBMITTED'" @click="handleApprove(row)">审核</el-button>
            <el-button link type="danger" v-if="row.status === 'DRAFT'" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增核算弹窗 -->
    <el-dialog v-model="addVisible" title="新增核算" width="500px">
      <el-form ref="addFormRef" :model="addForm" :rules="rules" label-width="100px">
        <el-form-item label="核算名称" prop="accountingName">
          <el-input v-model="addForm.accountingName" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="核算年份" prop="accountingYear">
          <el-date-picker v-model="addForm.accountingYear" type="year" placeholder="选择年份" value-format="YYYY" style="width:100%" />
        </el-form-item>
        <el-form-item label="核算期次" prop="accountingPeriod">
          <el-select v-model="addForm.accountingPeriod" placeholder="请选择" style="width:100%">
            <el-option label="年度" value="YEARLY" />
            <el-option label="季度" value="QUARTERLY" />
            <el-option label="月度" value="MONTHLY" />
          </el-select>
        </el-form-item>
        <el-form-item label="核算范围" prop="scopeType">
          <el-select v-model="addForm.scopeType" placeholder="请选择" style="width:100%">
            <el-option label="范围一（直接排放）" value="SCOPE1" />
            <el-option label="范围二（间接排放）" value="SCOPE2" />
            <el-option label="范围三（其他排放）" value="SCOPE3" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="addForm.remark" type="textarea" :rows="3" placeholder="请输入" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 核算详情弹窗 -->
    <el-dialog v-model="detailVisible" title="核算详情" width="900px">
      <template v-if="detailData">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="核算名称">{{ detailData.accountingName }}</el-descriptions-item>
          <el-descriptions-item label="核算年份">{{ detailData.accountingYear }}</el-descriptions-item>
          <el-descriptions-item label="核算期次">{{ detailData.accountingPeriod === 'YEARLY' ? '年度' : detailData.accountingPeriod === 'QUARTERLY' ? '季度' : '月度' }}</el-descriptions-item>
          <el-descriptions-item label="总排放量">{{ detailData.totalEmission || 0 }} tCO2e</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="detailData.status === 'APPROVED' ? 'success' : detailData.status === 'SUBMITTED' ? 'warning' : 'info'" size="small">
              {{ detailData.status === 'DRAFT' ? '草稿' : detailData.status === 'SUBMITTED' ? '已提交' : '已审核' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="备注">{{ detailData.remark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <!-- 活动数据录入 -->
        <h4 style="margin:16px 0 8px">活动数据</h4>
        <el-table :data="activityData" border stripe size="small" style="width: 100%">
          <el-table-column type="index" label="序号" width="50" />
          <el-table-column prop="activityName" label="活动名称" min-width="120">
            <template #default="{ row, $index }">
              <el-input v-model="row.activityName" placeholder="请输入" size="small" />
            </template>
          </el-table-column>
          <el-table-column prop="activityData" label="活动数据" width="140">
            <template #default="{ row }">
              <el-input-number v-model="row.activityData" :min="0" :precision="2" size="small" style="width:120px" />
            </template>
          </el-table-column>
          <el-table-column prop="activityUnit" label="单位" width="80">
            <template #default="{ row }">
              <el-select v-model="row.activityUnit" placeholder="单位" size="small" style="width:70px">
                <el-option label="t" value="t" />
                <el-option label="kWh" value="kWh" />
                <el-option label="m3" value="m3" />
                <el-option label="GJ" value="GJ" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="emissionFactor" label="排放因子" width="140">
            <template #default="{ row }">
              <el-input-number v-model="row.emissionFactor" :min="0" :precision="6" size="small" style="width:120px" />
            </template>
          </el-table-column>
          <el-table-column prop="emission" label="排放量(tCO2e)" width="140">
            <template #default="{ row }">
              <span>{{ (row.activityData || 0) * (row.emissionFactor || 0) | round(4) }}</span>
            </template>
          </el-table-column>
          <el-table-column width="60">
            <template #default="{ $index }">
              <el-button link type="danger" @click="activityData.splice($index, 1)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" class="add-row-btn" @click="addActivityRow">添加活动</el-button>
        <el-button type="success" @click="handleCalculate">计算排放</el-button>
      </template>
    </el-dialog>

    <!-- 排放报告弹窗 -->
    <el-dialog v-model="reportVisible" title="排放报告" width="700px">
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
        <!-- 排放对比图 -->
        <div ref="reportChartRef" style="height:300px;margin-top:16px"></div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import echarts from '@/utils/echarts'
import {
  getAccountingList,
  createAccounting,
  deleteAccounting,
  submitAccounting,
  approveAccounting,
  getAccountingDetail,
  getEmissionReport,
  calculateEmission
} from '@/api/carbon'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({ current: 1, size: 10, accountingName: '', accountingYear: null, accountingPeriod: '', status: '' })

const addVisible = ref(false)
const addForm = reactive({ accountingName: '', accountingYear: '', accountingPeriod: 'YEARLY', scopeType: 'SCOPE1', remark: '' })
const rules = { accountingName: [{ required: true, message: '请输入核算名称', trigger: 'blur' }], accountingYear: [{ required: true, message: '请选择核算年份', trigger: 'change' }], accountingPeriod: [{ required: true, message: '请选择核算期次', trigger: 'change' }] }

const detailVisible = ref(false)
const detailData = ref<any>(null)
const activityData = ref<any[]>([])
const currentAccountingId = ref<number>(0)

const reportVisible = ref(false)
const reportData = ref<any>(null)
const reportChartRef = ref<HTMLElement | null>(null)

onMounted(() => { getList() })

async function getList() {
  loading.value = true
  try {
    const params: any = { current: queryParams.current, size: queryParams.size }
    if (queryParams.accountingName) params.accountingName = queryParams.accountingName
    if (queryParams.accountingYear) params.accountingYear = parseInt(queryParams.accountingYear as string)
    if (queryParams.accountingPeriod) params.accountingPeriod = queryParams.accountingPeriod
    if (queryParams.status) params.status = queryParams.status
    const res = await getAccountingList(params)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (e: any) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function handleQuery() { queryParams.current = 1; getList() }
function handleReset() { Object.assign(queryParams, { current: 1, size: 10, accountingName: '', accountingYear: null, accountingPeriod: '', status: '' }); getList() }

function handleAdd() {
  Object.assign(addForm, { accountingName: '', accountingYear: '', accountingPeriod: 'YEARLY', scopeType: 'SCOPE1', remark: '' })
  addVisible.value = true
}

async function handleAddSubmit() {
  const data: any = { ...addForm }
  if (data.accountingYear) data.accountingYear = parseInt(data.accountingYear)
  await createAccounting(data)
  ElMessage.success('新增成功')
  addVisible.value = false
  getList()
}

async function handleDetail(row: any) {
  try {
    const res = await getAccountingDetail(row.id)
    detailData.value = res.data
    currentAccountingId.value = row.id
    activityData.value = (res.data.details || []).map((d: any) => ({ ...d }))
    if (activityData.value.length === 0) addActivityRow()
    detailVisible.value = true
  } catch (e) { console.error(e) }
}

function addActivityRow() {
  activityData.value.push({ activityName: '', activityData: null, activityUnit: 't', emissionFactor: null })
}

function addFormRef() {}
async function handleCalculate() {
  if (!currentAccountingId.value) return
  const activities = activityData.value.map(a => ({
    activityName: a.activityName || '未命名',
    energyTypeId: 0,
    activityData: a.activityData || 0,
    activityUnit: a.activityUnit || 't',
    factorId: null,
    sourceDescription: ''
  }))
  await calculateEmission({ accountingId: currentAccountingId.value, activities })
  ElMessage.success('计算完成')
  getList()
}

async function handleSubmit(row: any) {
  await submitAccounting(row.id)
  ElMessage.success('已提交')
  getList()
}
async function handleApprove(row: any) {
  await approveAccounting(row.id)
  ElMessage.success('已审核')
  getList()
}
async function handleDelete(row: any) {
  await ElMessageBox.confirm('确认删除该核算记录？', '提示')
  await deleteAccounting(row.id)
  ElMessage.success('删除成功')
  getList()
}

async function handleReport(row: any) {
  try {
    const res = await getEmissionReport(row.id)
    reportData.value = res.data
    reportVisible.value = true
    await nextTick()
    renderReportChart()
  } catch (e) { console.error(e) }
}

function renderReportChart() {
  if (!reportChartRef.value || !reportData.value) return
  const chart = echarts.init(reportChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['范围一', '范围二', '范围三'] },
    yAxis: { type: 'value', name: 'tCO2e' },
    series: [{
      type: 'bar',
      data: [reportData.value.scope1Emission, reportData.value.scope2Emission, reportData.value.scope3Emission],
      itemStyle: {
        color: (params: any) => {
          const colors = ['#409EFF', '#67C23A', '#E6A23C']
          return colors[params.dataIndex]
        }
      }
    }]
  })
  window.addEventListener('resize', () => chart.resize())
}
</script>

<style scoped>
.carbon-accounting { padding: 16px; }
.header-row { display: flex; justify-content: space-between; align-items: center; }
.title { font-size: 16px; font-weight: 600; }
.search-form { margin-bottom: 10px; }
.pagination-row { margin-top: 16px; display: flex; justify-content: flex-end; }
.add-row-btn { margin: 8px 8px 8px 0; }
</style>