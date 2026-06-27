<template>
  <div class="analysis-budget">
    <!-- 操作栏 -->
    <el-card class="filter-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="预算年度">
          <el-date-picker v-model="queryParams.budgetYear" type="year" placeholder="选择年度" value-format="YYYY" style="width: 150px" />
        </el-form-item>
        <el-form-item label="预算类型">
          <el-select v-model="queryParams.budgetType" placeholder="全部" clearable style="width: 150px">
            <el-option label="能耗预算" value="ENERGY" />
            <el-option label="碳排放预算" value="CARBON" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button type="success" @click="showAddDialog">新增预算</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 预算执行汇总卡片 -->
    <el-row :gutter="20" class="summary-row">
      <el-col :xs="24" :sm="12" v-for="item in budgetSummary" :key="item.budgetYear + item.budgetType">
        <el-card shadow="hover">
          <div class="summary-header">
            <span class="summary-title">{{ item.budgetYear }}年 {{ item.budgetType === 'ENERGY' ? '能耗' : '碳排放' }}</span>
            <el-tag :type="Number(item.completionRate) >= 100 ? 'danger' : 'success'" size="small">
              完成率 {{ item.completionRate }}%
            </el-tag>
          </div>
          <div class="summary-body">
            <el-progress :percentage="Math.min(Number(item.completionRate), 100)" :color="Number(item.completionRate) >= 100 ? '#dc2626' : '#10b981'" :stroke-width="20" />
            <div class="summary-values">
              <span>目标: {{ Number(item.targetValue).toLocaleString() }}</span>
              <span>当前: {{ Number(item.currentValue).toLocaleString() }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 预算列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="table-header">
          <span>预算明细列表</span>
        </div>
      </template>
      <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="energyUnitName" label="用能单元" min-width="140" />
        <el-table-column prop="budgetYear" label="年度" width="80" />
        <el-table-column prop="budgetTypeName" label="预算类型" width="120" />
        <el-table-column prop="targetValue" label="目标值" align="right" width="140" />
        <el-table-column prop="targetUnit" label="单位" width="80" />
        <el-table-column prop="currentValue" label="当前值" align="right" width="140" />
        <el-table-column prop="completionRate" label="完成率(%)" align="right" width="120">
          <template #default="{ row }">
            <el-progress :percentage="Number(row.completionRate)" :status="Number(row.completionRate) >= 100 ? 'exception' : 'success'" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="showEditDialog(row)">编辑</el-button>
            <el-button text type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current="pageNum" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增预算' : '编辑预算'" width="600px">
      <el-form :model="formData" label-width="120px" ref="formRef" :rules="formRules">
        <el-form-item label="预算年度" prop="budgetYear">
          <el-date-picker v-model="formData.budgetYear" type="year" placeholder="选择年度" value-format="YYYY" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预算类型" prop="budgetType">
          <el-select v-model="formData.budgetType" style="width: 100%">
            <el-option label="能耗预算" value="ENERGY" />
            <el-option label="碳排放预算" value="CARBON" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标值" prop="targetValue">
          <el-input-number v-model="formData.targetValue" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="目标单位" prop="targetUnit">
          <el-input v-model="formData.targetUnit" placeholder="如 kgce, tCO2" />
        </el-form-item>
        <el-form-item label="当前值" prop="currentValue">
          <el-input-number v-model="formData.currentValue" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="formData.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="停用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBudgetList, createBudget, updateBudget, deleteBudget, getBudgetSummaryByYear } from '@/api/analysis'
import type { BudgetListParams, AnalysisEnergyBudget } from '@/api/analysis'

const queryParams = ref<BudgetListParams>({ energyUnitId: 1 })
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref<AnalysisEnergyBudget[]>([])
const budgetSummary = ref<Array<Record<string, unknown>>>([])
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const formRef = ref()
const formData = ref({
  id: undefined as number | undefined,
  energyUnitId: 1,
  budgetYear: '',
  budgetType: 'ENERGY',
  targetValue: 0,
  targetUnit: 'kgce',
  currentValue: 0,
  status: 1
})
const formRules = {
  budgetYear: [{ required: true, message: '请选择预算年度', trigger: 'change' }],
  budgetType: [{ required: true, message: '请选择预算类型', trigger: 'change' }],
  targetValue: [{ required: true, message: '请输入目标值', trigger: 'blur' }],
  targetUnit: [{ required: true, message: '请输入目标单位', trigger: 'blur' }]
}

function handleSearch() {
  pageNum.value = 1
  fetchData()
  fetchSummary()
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getBudgetList({ ...queryParams.value, current: pageNum.value, size: pageSize.value })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch { /* empty */ } finally { loading.value = false }
}

async function fetchSummary() {
  try {
    const res = await getBudgetSummaryByYear(1)
    budgetSummary.value = res.data as Array<Record<string, unknown>>
  } catch { /* empty */ }
}

function showAddDialog() {
  dialogType.value = 'add'
  formData.value = { id: undefined, energyUnitId: 1, budgetYear: '', budgetType: 'ENERGY', targetValue: 0, targetUnit: 'kgce', currentValue: 0, status: 1 }
  dialogVisible.value = true
}

function showEditDialog(row: AnalysisEnergyBudget) {
  dialogType.value = 'edit'
  formData.value = {
    id: row.id,
    energyUnitId: row.energyUnitId,
    budgetYear: String(row.budgetYear),
    budgetType: row.budgetType,
    targetValue: row.targetValue,
    targetUnit: row.targetUnit,
    currentValue: row.currentValue,
    status: row.status
  }
  dialogVisible.value = true
}

async function handleSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (dialogType.value === 'add') {
      await createBudget(formData.value as unknown as AnalysisEnergyBudget)
      ElMessage.success('新增成功')
    } else {
      await updateBudget(formData.value.id!, formData.value as unknown as AnalysisEnergyBudget)
      ElMessage.success('修改成功')
    }
    dialogVisible.value = false
    fetchData()
    fetchSummary()
  } catch { /* empty */ }
}

async function handleDelete(row: AnalysisEnergyBudget) {
  try {
    await ElMessageBox.confirm('确认删除该预算？', '提示')
    await deleteBudget(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch { /* empty */ }
}

onMounted(() => {
  fetchData()
  fetchSummary()
})
</script>

<style scoped lang="scss">
.analysis-budget {
  .filter-card { margin-bottom: 16px; }
  .summary-row { margin-bottom: 16px; }
  .summary-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
  .summary-title { font-size: 16px; font-weight: bold; }
  .summary-body { .summary-values { display: flex; justify-content: space-between; margin-top: 8px; font-size: 13px; color: #6b7280; } }
  .table-card { margin-top: 16px; }
  .table-header { display: flex; justify-content: space-between; align-items: center; }
  .pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
}
</style>