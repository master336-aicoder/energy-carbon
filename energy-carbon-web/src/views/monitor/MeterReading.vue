<template>
  <div class="meter-reading">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>计量抄表记录</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd">新增抄表</el-button>
            <el-button type="success" @click="handleBatch">批量录入</el-button>
          </div>
        </div>
      </template>

      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="计量器具">
          <el-select v-model="searchForm.meterId" placeholder="请选择计量器具" clearable filterable style="width: 200px">
            <el-option v-for="m in meterList" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="searchForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="searchForm.endTime"
            type="datetime"
            placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column prop="meterName" label="计量器具" width="140" />
        <el-table-column prop="recordTime" label="抄表时间" width="180" />
        <el-table-column prop="startCode" label="起始表码" width="120">
          <template #default="{ row }">{{ row.startCode ?? '-' }}</template>
        </el-table-column>
        <el-table-column prop="endCode" label="结束表码" width="120" />
        <el-table-column prop="consumption" label="用量" width="120">
          <template #default="{ row }">{{ row.consumption }} {{ row.unit || '' }}</template>
        </el-table-column>
        <el-table-column prop="unitPrice" label="单价" width="100">
          <template #default="{ row }">{{ row.unitPrice ? '¥' + row.unitPrice : '-' }}</template>
        </el-table-column>
        <el-table-column prop="cost" label="费用" width="120">
          <template #default="{ row }">{{ row.cost ? '¥' + row.cost.toFixed(2) : '-' }}</template>
        </el-table-column>
        <el-table-column prop="operator" label="操作人" width="100" />
        <el-table-column prop="remark" label="备注" min-width="140" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close @open="handleDialogOpen">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="110px">
        <el-form-item label="计量器具" prop="meterId">
          <el-select v-model="form.meterId" placeholder="请选择计量器具" style="width: 100%" filterable>
            <el-option v-for="m in meterList" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="抄表时间" prop="recordTime">
              <el-date-picker
                v-model="form.recordTime"
                type="datetime"
                placeholder="选择抄表时间"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DDTHH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作人">
              <el-input v-model="form.operator" placeholder="操作人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="起始表码">
              <el-input-number v-model="form.startCode" :min="0" :precision="2" placeholder="自动带入上次值" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束表码" prop="endCode">
              <el-input-number v-model="form.endCode" :min="0" :precision="2" placeholder="请输入结束表码" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单价">
              <el-input-number v-model="form.unitPrice" :min="0" :precision="4" placeholder="请输入单价" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用量（自动计算）">
              <el-input :model-value="calculatedConsumption" disabled placeholder="自动计算" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="费用（自动计算）">
          <el-input :model-value="calculatedCost" disabled placeholder="自动计算" style="width: 240px" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 批量录入弹窗 -->
    <el-dialog v-model="batchVisible" title="批量录入抄表" width="650px" destroy-on-close>
      <el-table :data="batchList" border stripe style="width: 100%">
        <el-table-column label="计量器具" width="160">
          <template #default="{ $index }">
            <el-select v-model="batchList[$index].meterId" placeholder="选择器具" filterable style="width: 140px">
              <el-option v-for="m in meterList" :key="m.id" :label="m.name" :value="m.id" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="结束表码" width="130">
          <template #default="{ $index }">
            <el-input-number v-model="batchList[$index].endCode" :min="0" :precision="2" style="width: 110px" />
          </template>
        </el-table-column>
        <el-table-column label="单价" width="120">
          <template #default="{ $index }">
            <el-input-number v-model="batchList[$index].unitPrice" :min="0" :precision="4" style="width: 100px" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="{ $index }">
            <el-button type="danger" link @click="batchList.splice($index, 1)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 10px">
        <el-button @click="addBatchRow">添加一行</el-button>
      </div>
      <template #footer>
        <el-button @click="batchVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBatchSubmit" :loading="batchLoading">确定录入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getMeterRecordList, getLastMeterRecord, createMeterRecord, updateMeterRecord, deleteMeterRecord, batchCreateMeterRecord } from '@/api/meterReading'
import { getMeterList } from '@/api/meter'
import type { MeterRecord, Meter } from '@/types'

const loading = ref(false)
const submitLoading = ref(false)
const batchLoading = ref(false)
const dialogVisible = ref(false)
const batchVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const tableData = ref<MeterRecord[]>([])
const meterList = ref<Meter[]>([])

const searchForm = reactive({
  meterId: undefined as number | undefined,
  startTime: '',
  endTime: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null as number | null,
  meterId: undefined as number | undefined,
  recordTime: '',
  startCode: undefined as number | undefined,
  endCode: undefined as number | undefined,
  unitPrice: undefined as number | undefined,
  operator: '',
  remark: ''
})

const formRules = {
  meterId: [{ required: true, message: '请选择计量器具', trigger: 'change' }],
  recordTime: [{ required: true, message: '请选择抄表时间', trigger: 'change' }],
  endCode: [{ required: true, message: '请输入结束表码', trigger: 'blur' }]
}

const batchList = ref<any[]>([])

const calculatedConsumption = computed(() => {
  if (form.startCode !== undefined && form.endCode !== undefined) {
    return (form.endCode - form.startCode).toFixed(4)
  }
  return '-'
})

const calculatedCost = computed(() => {
  const consumption = form.startCode !== undefined && form.endCode !== undefined ? form.endCode - form.startCode : 0
  if (form.unitPrice !== undefined && consumption > 0) {
    return '¥' + (consumption * form.unitPrice).toFixed(2)
  }
  return '-'
})

async function fetchList() {
  loading.value = true
  try {
    const res = await getMeterRecordList({
      current: pagination.pageNum,
      size: pagination.pageSize,
      meterId: searchForm.meterId,
      startTime: searchForm.startTime || undefined,
      endTime: searchForm.endTime || undefined
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

async function fetchMeters() {
  const res = await getMeterList({ current: 1, size: 9999 })
  meterList.value = (res.data.records || []).map((m: any) => ({
    ...m,
    name: m.meterName || m.name,
    code: m.meterCode || m.code,
    type: m.meterType || m.type
  }))
}

async function handleDialogOpen() {
  if (form.meterId) {
    try {
      const res = await getLastMeterRecord(form.meterId)
      if (res.data && res.data.endCode) {
        form.startCode = res.data.endCode
      } else {
        form.startCode = undefined
      }
    } catch {
      form.startCode = undefined
    }
  }
}

function handleSearch() {
  pagination.pageNum = 1
  fetchList()
}

function handleReset() {
  searchForm.meterId = undefined
  searchForm.startTime = ''
  searchForm.endTime = ''
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
  form.meterId = undefined
  form.recordTime = ''
  form.startCode = undefined
  form.endCode = undefined
  form.unitPrice = undefined
  form.operator = ''
  form.remark = ''
}

function handleAdd() {
  dialogTitle.value = '新增抄表'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: MeterRecord) {
  dialogTitle.value = '编辑抄表'
  form.id = row.id || null
  form.meterId = row.meterId
  form.recordTime = row.recordTime
  form.startCode = row.startCode
  form.endCode = row.endCode
  form.unitPrice = row.unitPrice
  form.operator = row.operator || ''
  form.remark = row.remark || ''
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    const data: any = {
      meterId: form.meterId,
      recordTime: form.recordTime,
      startCode: form.startCode,
      endCode: form.endCode,
      unitPrice: form.unitPrice,
      operator: form.operator || undefined,
      remark: form.remark || undefined
    }
    if (form.id) {
      await updateMeterRecord(form.id, data)
      ElMessage.success('修改成功')
    } else {
      await createMeterRecord(data)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchList()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row: MeterRecord) {
  await ElMessageBox.confirm(`确定删除该抄表记录吗？`, '提示', { type: 'warning' })
  await deleteMeterRecord(row.id!)
  ElMessage.success('删除成功')
  fetchList()
}

function handleBatch() {
  batchList.value = [{ meterId: undefined, endCode: undefined, unitPrice: undefined }]
  batchVisible.value = true
}

function addBatchRow() {
  batchList.value.push({ meterId: undefined, endCode: undefined, unitPrice: undefined })
}

async function handleBatchSubmit() {
  const valid = batchList.value.every((b: any) => b.meterId && b.endCode !== undefined)
  if (!valid) {
    ElMessage.warning('请填写完整的抄表数据')
    return
  }
  batchLoading.value = true
  try {
    await batchCreateMeterRecord(batchList.value.map((b: any) => ({
      meterId: b.meterId,
      recordTime: new Date().toISOString().slice(0, 19),
      endCode: b.endCode,
      unitPrice: b.unitPrice,
      operator: ''
    })))
    ElMessage.success('批量录入成功')
    batchVisible.value = false
    fetchList()
  } finally {
    batchLoading.value = false
  }
}

onMounted(() => {
  fetchList()
  fetchMeters()
})
</script>

<style scoped lang="scss">
.meter-reading {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .header-actions {
    display: flex;
    gap: 10px;
  }
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>