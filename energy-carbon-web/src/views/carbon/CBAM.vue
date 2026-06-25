<template>
  <div class="peak-shifting-page">
    <div class="page-header">
      <h2>错峰排产小助手</h2>
      <p>智能排产计划管理与节能优化</p>
    </div>

    <el-tabs v-model="activeTab">
      <!-- 排产计划 -->
      <el-tab-pane label="排产计划" name="plan">
        <div class="filter-bar">
          <el-input v-model="filters.productionLine" placeholder="产线名称" clearable style="width: 150px" @change="loadPlans" />
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" @change="loadPlans" />
          <el-button type="primary" @click="showPlanDialog">新增计划</el-button>
          <el-button type="success" @click="getSuggestion">智能建议</el-button>
        </div>

        <el-table :data="planData" border stripe style="width: 100%">
          <el-table-column prop="planName" label="计划名称" min-width="150" />
          <el-table-column prop="planDate" label="计划日期" width="110" />
          <el-table-column prop="shiftType" label="班次" width="80" />
          <el-table-column prop="productionLine" label="产线" width="120" />
          <el-table-column prop="targetOutput" label="目标产量" width="100" />
          <el-table-column prop="actualOutput" label="实际产量" width="100" />
          <el-table-column prop="energyConsumption" label="能耗(kWh)" width="110" />
          <el-table-column prop="energyCost" label="能源成本(元)" width="120" />
          <el-table-column prop="savingRate" label="节能率(%)" width="100">
            <template #default="{ row }">
              <span :style="{ color: row.savingRate > 0 ? '#67c23a' : '#f56c6c' }">{{ row.savingRate || 0 }}%</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="editPlan(row)">编辑</el-button>
              <el-button link type="danger" size="small" @click="deletePlan(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination v-model:current-page="planPagination.current" v-model:page-size="planPagination.size" :total="planPagination.total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @size-change="loadPlans" @current-change="loadPlans" />
        </div>
      </el-tab-pane>

      <!-- 排产配置 -->
      <el-tab-pane label="排产配置" name="config">
        <div class="filter-bar">
          <el-button type="primary" @click="showConfigDialog">新增配置</el-button>
        </div>

        <el-table :data="configData" border stripe style="width: 100%">
          <el-table-column prop="configName" label="配置名称" min-width="150" />
          <el-table-column label="峰时段" width="180">
            <template #default="{ row }">{{ row.peakHoursStart }} - {{ row.peakHoursEnd }}</template>
          </el-table-column>
          <el-table-column label="谷时段" width="180">
            <template #default="{ row }">{{ row.valleyHoursStart }} - {{ row.valleyHoursEnd }}</template>
          </el-table-column>
          <el-table-column label="平时段" width="180">
            <template #default="{ row }">{{ row.flatHoursStart }} - {{ row.flatHoursEnd }}</template>
          </el-table-column>
          <el-table-column prop="maxPower" label="最大功率(kW)" width="130" />
          <el-table-column prop="priority" label="优先级" width="80" />
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="editConfig(row)">编辑</el-button>
              <el-button link type="danger" size="small" @click="deleteConfig(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 新增/编辑排产计划弹窗 -->
    <el-dialog v-model="planDialogVisible" :title="editingPlan ? '编辑排产计划' : '新增排产计划'" width="550px" destroy-on-close>
      <el-form :model="planForm" :rules="planRules" ref="planFormRef" label-width="100px">
        <el-form-item label="计划名称" prop="planName">
          <el-input v-model="planForm.planName" placeholder="请输入计划名称" />
        </el-form-item>
        <el-form-item label="计划日期" prop="planDate">
          <el-date-picker v-model="planForm.planDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="班次类型">
          <el-select v-model="planForm.shiftType" placeholder="选择班次" style="width: 100%">
            <el-option label="白班" value="DAY" />
            <el-option label="夜班" value="NIGHT" />
            <el-option label="全天" value="ALL_DAY" />
          </el-select>
        </el-form-item>
        <el-form-item label="产线名称">
          <el-input v-model="planForm.productionLine" placeholder="请输入产线名称" />
        </el-form-item>
        <el-form-item label="目标产量">
          <el-input-number v-model="planForm.targetOutput" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="实际产量">
          <el-input-number v-model="planForm.actualOutput" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="能耗(kWh)">
          <el-input-number v-model="planForm.energyConsumption" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="能源成本(元)">
          <el-input-number v-model="planForm.energyCost" :precision="2" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="planDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPlan">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新增/编辑排产配置弹窗 -->
    <el-dialog v-model="configDialogVisible" :title="editingConfig ? '编辑排产配置' : '新增排产配置'" width="550px" destroy-on-close>
      <el-form :model="configForm" :rules="configRules" ref="configFormRef" label-width="100px">
        <el-form-item label="配置名称" prop="configName">
          <el-input v-model="configForm.configName" placeholder="请输入配置名称" />
        </el-form-item>
        <el-form-item label="峰时段开始">
          <el-time-picker v-model="configForm.peakHoursStart" placeholder="峰时段开始" format="HH:mm" value-format="HH:mm" style="width: 100%" />
        </el-form-item>
        <el-form-item label="峰时段结束">
          <el-time-picker v-model="configForm.peakHoursEnd" placeholder="峰时段结束" format="HH:mm" value-format="HH:mm" style="width: 100%" />
        </el-form-item>
        <el-form-item label="谷时段开始">
          <el-time-picker v-model="configForm.valleyHoursStart" placeholder="谷时段开始" format="HH:mm" value-format="HH:mm" style="width: 100%" />
        </el-form-item>
        <el-form-item label="谷时段结束">
          <el-time-picker v-model="configForm.valleyHoursEnd" placeholder="谷时段结束" format="HH:mm" value-format="HH:mm" style="width: 100%" />
        </el-form-item>
        <el-form-item label="平时段开始">
          <el-time-picker v-model="configForm.flatHoursStart" placeholder="平时段开始" format="HH:mm" value-format="HH:mm" style="width: 100%" />
        </el-form-item>
        <el-form-item label="平时段结束">
          <el-time-picker v-model="configForm.flatHoursEnd" placeholder="平时段结束" format="HH:mm" value-format="HH:mm" style="width: 100%" />
        </el-form-item>
        <el-form-item label="最大功率(kW)">
          <el-input-number v-model="configForm.maxPower" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="优先级">
          <el-input-number v-model="configForm.priority" :min="0" :max="100" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="configDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitConfig">确定</el-button>
      </template>
    </el-dialog>

    <!-- 智能建议弹窗 -->
    <el-dialog v-model="suggestionDialogVisible" title="智能排产建议" width="500px">
      <el-result v-if="suggestion" icon="success" :title="suggestion.suggestion as string" sub-title="基于当前排产配置的智能建议">
        <template #extra>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="峰时段">{{ suggestion.peakHoursStart }} - {{ suggestion.peakHoursEnd }}</el-descriptions-item>
            <el-descriptions-item label="谷时段">{{ suggestion.valleyHoursStart }} - {{ suggestion.valleyHoursEnd }}</el-descriptions-item>
            <el-descriptions-item label="最大功率">{{ suggestion.maxPower }} kW</el-descriptions-item>
          </el-descriptions>
        </template>
      </el-result>
      <el-empty v-else description="暂无排产配置，请先配置峰谷时段" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getPeakShiftingList, addPeakShifting, updatePeakShifting, deletePeakShifting, getPeakShiftingConfigList, addPeakShiftingConfig, updatePeakShiftingConfig, deletePeakShiftingConfig, getPeakShiftingSuggestion, type PeakShifting, type PeakShiftingConfig } from '@/api/toolkit'

const activeTab = ref('plan')
const planData = ref<PeakShifting[]>([])
const configData = ref<PeakShiftingConfig[]>([])
const dateRange = ref<string[]>([])
const filters = reactive({ productionLine: '' })
const planPagination = reactive({ current: 1, size: 10, total: 0 })

const planDialogVisible = ref(false)
const configDialogVisible = ref(false)
const suggestionDialogVisible = ref(false)
const planFormRef = ref<FormInstance>()
const configFormRef = ref<FormInstance>()
const editingPlan = ref<PeakShifting | null>(null)
const editingConfig = ref<PeakShiftingConfig | null>(null)
const suggestion = ref<Record<string, unknown> | null>(null)

const planForm = reactive({ planName: '', planDate: '', shiftType: '', productionLine: '', targetOutput: 0, actualOutput: 0, energyConsumption: 0, energyCost: 0 })
const planRules = { planName: [{ required: true, message: '请输入计划名称', trigger: 'blur' }], planDate: [{ required: true, message: '请选择日期', trigger: 'change' }] }

const configForm = reactive({ configName: '', peakHoursStart: '', peakHoursEnd: '', valleyHoursStart: '', valleyHoursEnd: '', flatHoursStart: '', flatHoursEnd: '', maxPower: 0, priority: 0 })
const configRules = { configName: [{ required: true, message: '请输入配置名称', trigger: 'blur' }] }

async function loadPlans() {
  const res = await getPeakShiftingList({
    current: planPagination.current, size: planPagination.size,
    productionLine: filters.productionLine || undefined,
    startDate: dateRange.value?.[0], endDate: dateRange.value?.[1]
  })
  planData.value = res.data.records
  planPagination.total = res.data.total
}

async function loadConfigs() {
  const res = await getPeakShiftingConfigList()
  configData.value = res.data
}

function showPlanDialog() {
  editingPlan.value = null
  Object.assign(planForm, { planName: '', planDate: '', shiftType: '', productionLine: '', targetOutput: 0, actualOutput: 0, energyConsumption: 0, energyCost: 0 })
  planDialogVisible.value = true
}

function editPlan(row: PeakShifting) {
  editingPlan.value = row
  Object.assign(planForm, row)
  planDialogVisible.value = true
}

async function submitPlan() {
  await planFormRef.value?.validate()
  if (editingPlan.value) {
    await updatePeakShifting(editingPlan.value.id!, planForm)
    ElMessage.success('修改成功')
  } else {
    await addPeakShifting(planForm as PeakShifting)
    ElMessage.success('新增成功')
  }
  planDialogVisible.value = false
  loadPlans()
}

async function deletePlan(row: PeakShifting) {
  await ElMessageBox.confirm('确定要删除该排产计划吗？', '提示', { type: 'warning' })
  await deletePeakShifting(row.id!)
  ElMessage.success('已删除')
  loadPlans()
}

function showConfigDialog() {
  editingConfig.value = null
  Object.assign(configForm, { configName: '', peakHoursStart: '', peakHoursEnd: '', valleyHoursStart: '', valleyHoursEnd: '', flatHoursStart: '', flatHoursEnd: '', maxPower: 0, priority: 0 })
  configDialogVisible.value = true
}

function editConfig(row: PeakShiftingConfig) {
  editingConfig.value = row
  Object.assign(configForm, row)
  configDialogVisible.value = true
}

async function submitConfig() {
  await configFormRef.value?.validate()
  if (editingConfig.value) {
    await updatePeakShiftingConfig(editingConfig.value.id!, configForm)
    ElMessage.success('修改成功')
  } else {
    await addPeakShiftingConfig(configForm as PeakShiftingConfig)
    ElMessage.success('新增成功')
  }
  configDialogVisible.value = false
  loadConfigs()
}

async function deleteConfig(row: PeakShiftingConfig) {
  await ElMessageBox.confirm('确定要删除该配置吗？', '提示', { type: 'warning' })
  await deletePeakShiftingConfig(row.id!)
  ElMessage.success('已删除')
  loadConfigs()
}

async function getSuggestion() {
  const line = filters.productionLine || '默认产线'
  const date = new Date().toISOString().split('T')[0]
  const res = await getPeakShiftingSuggestion(line, date)
  suggestion.value = res.data
  suggestionDialogVisible.value = true
}

onMounted(() => { loadPlans(); loadConfigs() })
</script>

<style scoped>
.peak-shifting-page { padding: 20px; }
.page-header { margin-bottom: 24px; }
.page-header h2 { margin: 0 0 8px; font-size: 24px; }
.page-header p { margin: 0; color: #909399; }
.filter-bar { display: flex; gap: 12px; margin-bottom: 16px; align-items: center; flex-wrap: wrap; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
