<template>
  <div class="system-meter">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>计量器具管理</span>
          <div class="header-actions">
            <el-button type="success" @click="handleImport">导入</el-button>
            <el-button type="warning" @click="handleExport">导出</el-button>
            <el-button type="primary" @click="handleAdd">新增器具</el-button>
          </div>
        </div>
      </template>

      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="器具编码">
          <el-input v-model="searchForm.code" placeholder="请输入器具编码" clearable />
        </el-form-item>
        <el-form-item label="器具名称">
          <el-input v-model="searchForm.name" placeholder="请输入器具名称" clearable />
        </el-form-item>
        <el-form-item label="器具类型">
          <el-select v-model="searchForm.type" placeholder="请选择器具类型" clearable>
            <el-option label="电能表" value="electric_meter" />
            <el-option label="水表" value="water_meter" />
            <el-option label="燃气表" value="gas_meter" />
            <el-option label="热量表" value="heat_meter" />
            <el-option label="蒸汽表" value="steam_meter" />
            <el-option label="流量计" value="flow_meter" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联设备">
          <el-select v-model="searchForm.deviceId" placeholder="请选择关联设备" clearable filterable>
            <el-option
              v-for="device in deviceList"
              :key="device.id"
              :label="device.name"
              :value="device.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属部门">
          <el-tree-select
            v-model="searchForm.deptId"
            :data="deptTree"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            placeholder="请选择所属部门"
            clearable
            check-strictly
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
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
        <el-table-column prop="meterCode" label="器具编码" min-width="120" />
        <el-table-column prop="meterName" label="器具名称" min-width="140" />
        <el-table-column prop="meterType" label="器具类型" min-width="100">
          <template #default="{ row }">
            <el-tag>{{ meterTypeMap[row.meterType] || row.meterType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="accuracyClass" label="精度等级" min-width="100">
          <template #default="{ row }">
            <span>{{ row.accuracyClass || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="量程" min-width="120">
          <template #default="{ row }">
            <span v-if="row.rangeMin !== undefined && row.rangeMax !== undefined">
              {{ row.rangeMin }} ~ {{ row.rangeMax }}
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="installDate" label="安装日期" min-width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="(val) => handleStatusChange(row.id, val)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="器具编码" prop="meterCode">
              <el-input v-model="form.meterCode" placeholder="请输入器具编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="器具名称" prop="meterName">
              <el-input v-model="form.meterName" placeholder="请输入器具名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="器具类型" prop="meterType">
              <el-select v-model="form.meterType" placeholder="请选择器具类型" style="width: 100%">
                <el-option label="电能表" value="electric_meter" />
                <el-option label="水表" value="water_meter" />
                <el-option label="燃气表" value="gas_meter" />
                <el-option label="热量表" value="heat_meter" />
                <el-option label="蒸汽表" value="steam_meter" />
                <el-option label="流量计" value="flow_meter" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关联设备">
              <el-select v-model="form.deviceId" placeholder="请选择关联设备" style="width: 100%" clearable filterable>
                <el-option
                  v-for="device in deviceList"
                  :key="device.id"
                  :label="device.name"
                  :value="device.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属部门" prop="deptId">
              <el-tree-select
                v-model="form.deptId"
                :data="deptTree"
                :props="{ label: 'name', value: 'id', children: 'children' }"
                placeholder="请选择所属部门"
                style="width: 100%"
                check-strictly
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="精度等级">
              <el-input v-model="form.accuracyClass" placeholder="如: 0.5级" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="量程下限">
              <el-input-number v-model="form.rangeMin" :min="0" :precision="2" placeholder="下限" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="量程上限">
              <el-input-number v-model="form.rangeMax" :min="0" :precision="2" placeholder="上限" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="量程单位">
              <el-input v-model="form.rangeUnit" placeholder="单位" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
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

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="器具详情" width="600px">
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="器具编码">{{ currentRow.code }}</el-descriptions-item>
        <el-descriptions-item label="器具名称">{{ currentRow.name }}</el-descriptions-item>
        <el-descriptions-item label="器具类型">{{ meterTypeMap[currentRow.type] || currentRow.type }}</el-descriptions-item>
        <el-descriptions-item label="关联设备">{{ currentRow.deviceName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="所属部门">{{ currentRow.deptName }}</el-descriptions-item>
        <el-descriptions-item label="精度等级">{{ currentRow.accuracy || '-' }}</el-descriptions-item>
        <el-descriptions-item label="量程">
          <span v-if="currentRow.rangeMin !== undefined && currentRow.rangeMax !== undefined">
            {{ currentRow.rangeMin }} ~ {{ currentRow.rangeMax }} {{ currentRow.rangeUnit || '' }}
          </span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentRow.status === 1 ? 'success' : 'danger'">
            {{ currentRow.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ currentRow.createTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentRow.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 导入弹窗 -->
    <el-dialog v-model="importVisible" title="导入计量器具" width="500px">
      <el-upload
        ref="uploadRef"
        action="#"
        :auto-upload="false"
        :limit="1"
        :on-change="handleFileChange"
        accept=".xlsx,.xls"
      >
        <template #trigger>
          <el-button type="primary">选择文件</el-button>
        </template>
        <template #tip>
          <div class="upload-tip">
            <p>仅支持 .xlsx 或 .xls 格式文件</p>
            <el-button type="link" @click="handleDownloadTemplate">下载导入模板</el-button>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="importVisible = false">取消</el-button>
        <el-button type="primary" @click="handleImportSubmit" :loading="importLoading">确定导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, UploadInstance } from 'element-plus'
import { getMeterList, createMeter, updateMeter, deleteMeter, updateMeterStatus, exportMeter, importMeter, downloadMeterTemplate } from '@/api/meter'
import { getDeptTree } from '@/api/dept'
import { getDeviceList } from '@/api/device'
import type { Meter, Device } from '@/types'

const loading = ref(false)
const submitLoading = ref(false)
const importLoading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const importVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const uploadRef = ref<UploadInstance>()
const currentRow = ref<Meter | null>(null)
const importFile = ref<File | null>(null)

const meterTypeMap: Record<string, string> = {
  electric_meter: '电能表',
  water_meter: '水表',
  gas_meter: '燃气表',
  heat_meter: '热量表',
  steam_meter: '蒸汽表',
  flow_meter: '流量计',
  other: '其他'
}

const searchForm = reactive({
  code: '',
  name: '',
  type: '',
  deviceId: null as number | null,
  deptId: null as number | null,
  status: null as number | null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref<Meter[]>([])
const deptTree = ref<any[]>([])
const deviceList = ref<Device[]>([])

const form = reactive({
  id: null as number | null,
  code: '',
  name: '',
  type: '',
  deviceId: null as number | null,
  deptId: null as number | null,
  accuracy: '',
  rangeMin: undefined as number | undefined,
  rangeMax: undefined as number | undefined,
  rangeUnit: '',
  status: 1,
  remark: ''
})

const formRules = {
  meterCode: [{ required: true, message: '请输入器具编码', trigger: 'blur' }],
  meterName: [{ required: true, message: '请输入器具名称', trigger: 'blur' }],
  meterType: [{ required: true, message: '请选择器具类型', trigger: 'change' }],
  deptId: [{ required: true, message: '请选择所属部门', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getMeterList({
      current: pagination.pageNum,
      size: pagination.pageSize,
      meterName: searchForm.name || undefined,
      meterType: searchForm.type || undefined,
      status: searchForm.status ?? undefined
    })
    tableData.value = res.data.records || []
    pagination.total = res.data.total || 0
  } finally {
    loading.value = false
  }
}

async function fetchDeptTree() {
  const res = await getDeptTree()
  deptTree.value = res.data
}

async function fetchDeviceList() {
  const res = await getDeviceList({ pageNum: 1, pageSize: 9999 })
  deviceList.value = res.data.records
}

function handleSearch() {
  pagination.pageNum = 1
  fetchList()
}

function handleReset() {
  searchForm.code = ''
  searchForm.name = ''
  searchForm.type = ''
  searchForm.deviceId = null
  searchForm.deptId = null
  searchForm.status = null
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
  form.code = ''
  form.name = ''
  form.type = ''
  form.deviceId = null
  form.deptId = null
  form.accuracy = ''
  form.rangeMin = undefined
  form.rangeMax = undefined
  form.rangeUnit = ''
  form.status = 1
  form.remark = ''
}

function handleAdd() {
  dialogTitle.value = '新增器具'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: Meter) {
  dialogTitle.value = '编辑器具'
  form.id = row.id
  form.code = row.code
  form.name = row.name
  form.type = row.type
  form.deviceId = row.deviceId || null
  form.deptId = row.deptId
  form.accuracy = row.accuracy || ''
  form.rangeMin = row.rangeMin
  form.rangeMax = row.rangeMax
  form.rangeUnit = row.rangeUnit || ''
  form.status = row.status
  form.remark = row.remark || ''
  dialogVisible.value = true
}

function handleDetail(row: Meter) {
  currentRow.value = row
  detailVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    const data = {
      ...form,
      deptId: form.deptId!
    }
    if (form.id) {
      await updateMeter(data)
      ElMessage.success('修改成功')
    } else {
      await createMeter(data)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchList()
  } finally {
    submitLoading.value = false
  }
}

async function handleStatusChange(id: number, status: number) {
  try {
    await updateMeterStatus(id, status)
    ElMessage.success('状态更新成功')
  } catch {
    fetchList()
  }
}

async function handleDelete(row: Meter) {
  await ElMessageBox.confirm(`确定删除计量器具 "${row.name}" 吗？`, '提示', { type: 'warning' })
  await deleteMeter(row.id)
  ElMessage.success('删除成功')
  fetchList()
}

async function handleExport() {
  const res = await exportMeter({
    code: searchForm.code || undefined,
    name: searchForm.name || undefined,
    type: searchForm.type || undefined,
    deviceId: searchForm.deviceId || undefined,
    deptId: searchForm.deptId || undefined,
    status: searchForm.status ?? undefined
  })
  const blob = new Blob([res as unknown as BlobPart], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `计量器具列表_${new Date().toISOString().slice(0, 10)}.xlsx`
  link.click()
  URL.revokeObjectURL(link.href)
  ElMessage.success('导出成功')
}

function handleImport() {
  importVisible.value = true
  importFile.value = null
}

function handleFileChange(file: any) {
  importFile.value = file.raw
}

async function handleImportSubmit() {
  if (!importFile.value) {
    ElMessage.warning('请选择文件')
    return
  }
  importLoading.value = true
  try {
    await importMeter(importFile.value)
    ElMessage.success('导入成功')
    importVisible.value = false
    fetchList()
  } finally {
    importLoading.value = false
  }
}

async function handleDownloadTemplate() {
  const res = await downloadMeterTemplate()
  const blob = new Blob([res as unknown as BlobPart], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = '计量器具导入模板.xlsx'
  link.click()
  URL.revokeObjectURL(link.href)
}

onMounted(() => {
  fetchList()
  fetchDeptTree()
  fetchDeviceList()
})
</script>

<style scoped lang="scss">
.system-meter {
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

.upload-tip {
  margin-top: 10px;
  color: #666;
  font-size: 12px;
}
</style>
