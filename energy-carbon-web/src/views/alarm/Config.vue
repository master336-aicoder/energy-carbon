<template>
  <div class="alarm-config">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>报警阈值配置</span>
          <el-button type="primary" @click="handleAdd">新增阈值</el-button>
        </div>
      </template>

      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="阈值名称">
          <el-input v-model="searchForm.thresholdName" placeholder="请输入阈值名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="指标类型">
          <el-select v-model="searchForm.metricType" placeholder="请选择指标类型" clearable style="width: 140px">
            <el-option label="电量" value="电量" />
            <el-option label="功率" value="功率" />
            <el-option label="电压" value="电压" />
            <el-option label="电流" value="电流" />
            <el-option label="温度" value="温度" />
            <el-option label="压力" value="压力" />
          </el-select>
        </el-form-item>
        <el-form-item label="报警级别">
          <el-select v-model="searchForm.alarmLevel" placeholder="请选择报警级别" clearable style="width: 140px">
            <el-option label="一般" value="INFO" />
            <el-option label="警告" value="WARN" />
            <el-option label="严重" value="CRITICAL" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.enabled" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="thresholdName" label="阈值名称" min-width="140" />
        <el-table-column prop="metricType" label="指标类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.metricType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="compareType" label="比较方式" width="110">
          <template #default="{ row }">
            <el-tag>{{ compareTypeMap[row.compareType] || row.compareType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="thresholdValue" label="阈值" width="120" />
        <el-table-column prop="alarmLevel" label="报警级别" width="100">
          <template #default="{ row }">
            <el-tag :type="row.alarmLevel === 'CRITICAL' ? 'danger' : row.alarmLevel === 'WARN' ? 'warning' : 'info'">
              {{ levelMap[row.alarmLevel] || row.alarmLevel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="enabled" label="状态" width="80">
          <template #default="{ row }">
            <el-switch
              v-model="row.enabled"
              :active-value="1"
              :inactive-value="0"
              @change="(val) => handleEnabledChange(row.id!, val)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="notifyMethod" label="通知方式" width="130">
          <template #default="{ row }">
            {{ row.notifyMethod || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="140" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="140" fixed="right">
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="650px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="110px">
        <el-form-item label="阈值名称" prop="thresholdName">
          <el-input v-model="form.thresholdName" placeholder="请输入阈值名称" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用能单元">
              <el-tree-select
                v-model="form.energyUnitId"
                :data="deptTree"
                :props="{ label: 'name', value: 'id', children: 'children' }"
                placeholder="请选择用能单元"
                style="width: 100%"
                clearable
                check-strictly
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="指标类型" prop="metricType">
              <el-select v-model="form.metricType" placeholder="请选择指标类型" style="width: 100%">
                <el-option label="电量" value="电量" />
                <el-option label="功率" value="功率" />
                <el-option label="电压" value="电压" />
                <el-option label="电流" value="电流" />
                <el-option label="温度" value="温度" />
                <el-option label="压力" value="压力" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="比较方式" prop="compareType">
              <el-select v-model="form.compareType" placeholder="请选择比较方式" style="width: 100%">
                <el-option label="大于 (GT)" value="GT" />
                <el-option label="小于 (LT)" value="LT" />
                <el-option label="大于等于 (GTE)" value="GTE" />
                <el-option label="小于等于 (LTE)" value="LTE" />
                <el-option label="等于 (EQ)" value="EQ" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="阈值" prop="thresholdValue">
              <el-input-number v-model="form.thresholdValue" :min="0" :precision="4" placeholder="请输入阈值" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="报警级别" prop="alarmLevel">
              <el-select v-model="form.alarmLevel" placeholder="请选择报警级别" style="width: 100%">
                <el-option label="一般 (INFO)" value="INFO" />
                <el-option label="警告 (WARN)" value="WARN" />
                <el-option label="严重 (CRITICAL)" value="CRITICAL" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="通知方式">
              <el-checkbox-group v-model="notifyMethods">
                <el-checkbox label="SYSTEM">系统消息</el-checkbox>
                <el-checkbox label="SMS">短信</el-checkbox>
                <el-checkbox label="EMAIL">邮件</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态">
          <el-radio-group v-model="form.enabled">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getAlarmThresholdList, createAlarmThreshold, updateAlarmThreshold, setAlarmThresholdEnabled, deleteAlarmThreshold } from '@/api/alarm'
import { getDeptTree } from '@/api/dept'
import type { AlarmThreshold } from '@/types'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const tableData = ref<AlarmThreshold[]>([])
const deptTree = ref<any[]>([])

const compareTypeMap: Record<string, string> = {
  GT: '大于',
  LT: '小于',
  GTE: '大于等于',
  LTE: '小于等于',
  EQ: '等于'
}

const levelMap: Record<string, string> = {
  INFO: '一般',
  WARN: '警告',
  CRITICAL: '严重'
}

const searchForm = reactive({
  thresholdName: '',
  metricType: '',
  alarmLevel: '',
  enabled: null as number | null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null as number | null,
  thresholdName: '',
  energyUnitId: undefined as number | undefined,
  metricType: '',
  compareType: '',
  thresholdValue: undefined as number | undefined,
  alarmLevel: '',
  enabled: 1,
  notifyMethod: '',
  remark: ''
})

const notifyMethods = ref<string[]>([])

watch(notifyMethods, (val) => {
  form.notifyMethod = val.join(',')
})

const formRules = {
  thresholdName: [{ required: true, message: '请输入阈值名称', trigger: 'blur' }],
  metricType: [{ required: true, message: '请选择指标类型', trigger: 'change' }],
  compareType: [{ required: true, message: '请选择比较方式', trigger: 'change' }],
  thresholdValue: [{ required: true, message: '请输入阈值', trigger: 'blur' }],
  alarmLevel: [{ required: true, message: '请选择报警级别', trigger: 'change' }]
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getAlarmThresholdList({
      current: pagination.pageNum,
      size: pagination.pageSize,
      thresholdName: searchForm.thresholdName || undefined,
      metricType: searchForm.metricType || undefined,
      alarmLevel: searchForm.alarmLevel || undefined,
      enabled: searchForm.enabled ?? undefined
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

async function fetchDeptTree() {
  try {
    const res = await getDeptTree()
    deptTree.value = res.data
  } catch {
    // ignore
  }
}

function handleSearch() {
  pagination.pageNum = 1
  fetchList()
}

function handleReset() {
  searchForm.thresholdName = ''
  searchForm.metricType = ''
  searchForm.alarmLevel = ''
  searchForm.enabled = null
  pagination.pageNum = 1
  fetchList()
}

function handleSizeChange(val: number) {
  pagination.pageSize = val
  fetchList()
}

function handleCurrentChange(val: number) {
  pagination.pageNum = val
  fetchList()
}

function resetForm() {
  form.id = null
  form.thresholdName = ''
  form.energyUnitId = undefined
  form.metricType = ''
  form.compareType = ''
  form.thresholdValue = undefined
  form.alarmLevel = ''
  form.enabled = 1
  form.notifyMethod = ''
  form.remark = ''
  notifyMethods.value = []
}

function handleAdd() {
  dialogTitle.value = '新增阈值'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: AlarmThreshold) {
  dialogTitle.value = '编辑阈值'
  form.id = row.id || null
  form.thresholdName = row.thresholdName
  form.energyUnitId = row.energyUnitId
  form.metricType = row.metricType
  form.compareType = row.compareType
  form.thresholdValue = row.thresholdValue
  form.alarmLevel = row.alarmLevel
  form.enabled = row.enabled
  form.notifyMethod = row.notifyMethod || ''
  form.remark = row.remark || ''
  notifyMethods.value = row.notifyMethod ? row.notifyMethod.split(',').filter(Boolean) : []
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    const data: any = {
      thresholdName: form.thresholdName,
      energyUnitId: form.energyUnitId,
      metricType: form.metricType,
      compareType: form.compareType,
      thresholdValue: form.thresholdValue,
      alarmLevel: form.alarmLevel,
      enabled: form.enabled,
      notifyMethod: form.notifyMethod || undefined,
      remark: form.remark || undefined
    }
    if (form.id) {
      await updateAlarmThreshold(form.id, data)
      ElMessage.success('修改成功')
    } else {
      await createAlarmThreshold(data)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchList()
  } finally {
    submitLoading.value = false
  }
}

async function handleEnabledChange(id: number, enabled: number) {
  try {
    await setAlarmThresholdEnabled(id, enabled)
    ElMessage.success(enabled === 1 ? '已启用' : '已禁用')
  } catch {
    fetchList()
  }
}

async function handleDelete(row: AlarmThreshold) {
  await ElMessageBox.confirm(`确定删除阈值 "${row.thresholdName}" 吗？`, '提示', { type: 'warning' })
  await deleteAlarmThreshold(row.id!)
  ElMessage.success('删除成功')
  fetchList()
}

onMounted(() => {
  fetchList()
  fetchDeptTree()
})
</script>

<style scoped lang="scss">
.alarm-config {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>