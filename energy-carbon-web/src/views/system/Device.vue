<template>
  <div class="system-device">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用能设备管理</span>
          <div class="header-actions">
            <el-button type="success" @click="handleImport">导入</el-button>
            <el-button type="warning" @click="handleExport">导出</el-button>
            <el-button type="primary" @click="handleAdd">新增设备</el-button>
          </div>
        </div>
      </template>

      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="设备编码">
          <el-input v-model="searchForm.code" placeholder="请输入设备编码" clearable />
        </el-form-item>
        <el-form-item label="设备名称">
          <el-input v-model="searchForm.name" placeholder="请输入设备名称" clearable />
        </el-form-item>
        <el-form-item label="设备类型">
          <el-select v-model="searchForm.type" placeholder="请选择设备类型" clearable>
            <el-option label="电机" value="motor" />
            <el-option label="锅炉" value="boiler" />
            <el-option label="空压机" value="air_compressor" />
            <el-option label="风机" value="fan" />
            <el-option label="泵" value="pump" />
            <el-option label="照明" value="lighting" />
            <el-option label="空调" value="ac" />
            <el-option label="其他" value="other" />
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
        <el-table-column prop="deviceCode" label="设备编码" min-width="120" />
        <el-table-column prop="deviceName" label="设备名称" min-width="140" />
        <el-table-column prop="deviceType" label="设备类型" min-width="100">
          <template #default="{ row }">
            <el-tag>{{ deviceTypeMap[row.deviceType] || row.deviceType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deptName" label="所属部门" min-width="120" />
        <el-table-column prop="manufacturer" label="制造商" min-width="120" />
        <el-table-column prop="model" label="型号" min-width="100" />
        <el-table-column prop="powerRating" label="功率" min-width="100">
          <template #default="{ row }">
            <span v-if="row.powerRating">{{ row.powerRating }} {{ row.powerUnit || 'kW' }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
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
            <el-form-item label="设备编码" prop="deviceCode">
              <el-input v-model="form.deviceCode" placeholder="请输入设备编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备名称" prop="deviceName">
              <el-input v-model="form.deviceName" placeholder="请输入设备名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="设备类型" prop="deviceType">
              <el-select v-model="form.deviceType" placeholder="请选择设备类型" style="width: 100%">
                <el-option label="电机" value="motor" />
                <el-option label="锅炉" value="boiler" />
                <el-option label="空压机" value="air_compressor" />
                <el-option label="风机" value="fan" />
                <el-option label="泵" value="pump" />
                <el-option label="照明" value="lighting" />
                <el-option label="空调" value="ac" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
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
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="制造商">
              <el-input v-model="form.manufacturer" placeholder="请输入制造商" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="型号">
              <el-input v-model="form.model" placeholder="请输入型号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="功率">
              <el-input-number v-model="form.powerRating" :min="0" :precision="2" placeholder="请输入功率" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="功率单位">
              <el-select v-model="form.powerUnit" placeholder="请选择单位" style="width: 100%">
                <el-option label="kW" value="kW" />
                <el-option label="W" value="W" />
                <el-option label="MW" value="MW" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="额定电压(V)">
              <el-input-number v-model="form.voltage" :min="0" :precision="0" placeholder="请输入额定电压" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="额定电流(A)">
              <el-input-number v-model="form.current" :min="0" :precision="2" placeholder="请输入额定电流" style="width: 100%" />
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
    <el-dialog v-model="detailVisible" title="设备详情" width="600px">
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="设备编码">{{ currentRow.deviceCode }}</el-descriptions-item>
        <el-descriptions-item label="设备名称">{{ currentRow.deviceName }}</el-descriptions-item>
        <el-descriptions-item label="设备类型">{{ deviceTypeMap[currentRow.deviceType] || currentRow.deviceType }}</el-descriptions-item>
        <el-descriptions-item label="所属部门">{{ currentRow.deptName }}</el-descriptions-item>
        <el-descriptions-item label="制造商">{{ currentRow.manufacturer || '-' }}</el-descriptions-item>
        <el-descriptions-item label="型号">{{ currentRow.model || '-' }}</el-descriptions-item>
        <el-descriptions-item label="功率">
          <span v-if="currentRow.powerRating">{{ currentRow.powerRating }} {{ currentRow.powerUnit || 'kW' }}</span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="额定电压">{{ currentRow.voltage ? currentRow.voltage + ' V' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="额定电流">{{ currentRow.current ? currentRow.current + ' A' : '-' }}</el-descriptions-item>
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
    <el-dialog v-model="importVisible" title="导入设备" width="500px">
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
import { getDeviceList, createDevice, updateDevice, deleteDevice, updateDeviceStatus, exportDevice, importDevice, downloadDeviceTemplate } from '@/api/device'
import { getDeptTree } from '@/api/dept'
import type { Device } from '@/types'

const loading = ref(false)
const submitLoading = ref(false)
const importLoading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const importVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const uploadRef = ref<UploadInstance>()
const currentRow = ref<Device | null>(null)
const importFile = ref<File | null>(null)

const deviceTypeMap: Record<string, string> = {
  motor: '电机',
  boiler: '锅炉',
  air_compressor: '空压机',
  fan: '风机',
  pump: '泵',
  lighting: '照明',
  ac: '空调',
  other: '其他'
}

const searchForm = reactive({
  code: '',
  name: '',
  type: '',
  deptId: null as number | null,
  status: null as number | null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref<Device[]>([])
const deptTree = ref<any[]>([])

const form = reactive({
  id: null as number | null,
  deviceCode: '',
  deviceName: '',
  deviceType: '',
  deptId: null as number | null,
  manufacturer: '',
  model: '',
  powerRating: undefined as number | undefined,
  powerUnit: 'kW',
  voltage: undefined as number | undefined,
  current: undefined as number | undefined,
  status: 1,
  remark: ''
})

const formRules = {
  deviceCode: [{ required: true, message: '请输入设备编码', trigger: 'blur' }],
  deviceName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  deviceType: [{ required: true, message: '请选择设备类型', trigger: 'change' }],
  deptId: [{ required: true, message: '请选择所属部门', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getDeviceList({
      current: pagination.pageNum,
      size: pagination.pageSize,
      deviceName: searchForm.name || undefined,
      deviceType: searchForm.type || undefined,
      deptId: searchForm.deptId || undefined,
      status: searchForm.status ?? undefined
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

async function fetchDeptTree() {
  const res = await getDeptTree()
  deptTree.value = res.data
}

function handleSearch() {
  pagination.pageNum = 1
  fetchList()
}

function handleReset() {
  searchForm.code = ''
  searchForm.name = ''
  searchForm.type = ''
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
  form.deviceCode = ''
  form.deviceName = ''
  form.deviceType = ''
  form.deptId = null
  form.manufacturer = ''
  form.model = ''
  form.powerRating = undefined
  form.powerUnit = 'kW'
  form.voltage = undefined
  form.current = undefined
  form.status = 1
  form.remark = ''
}

function handleAdd() {
  dialogTitle.value = '新增设备'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: Device) {
  dialogTitle.value = '编辑设备'
  form.id = row.id
  form.deviceCode = row.deviceCode
  form.deviceName = row.deviceName
  form.deviceType = row.deviceType
  form.deptId = row.deptId
  form.manufacturer = row.manufacturer || ''
  form.model = row.model || ''
  form.powerRating = row.powerRating
  form.powerUnit = row.powerUnit || 'kW'
  form.voltage = row.voltage
  form.current = row.current
  form.status = row.status
  form.remark = row.remark || ''
  dialogVisible.value = true
}

function handleDetail(row: Device) {
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
      await updateDevice(data)
      ElMessage.success('修改成功')
    } else {
      await createDevice(data)
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
    await updateDeviceStatus(id, status)
    ElMessage.success('状态更新成功')
  } catch {
    fetchList()
  }
}

async function handleDelete(row: Device) {
  await ElMessageBox.confirm(`确定删除设备 "${row.deviceName}" 吗？`, '提示', { type: 'warning' })
  await deleteDevice(row.id)
  ElMessage.success('删除成功')
  fetchList()
}

async function handleExport() {
  const res = await exportDevice({
    code: searchForm.code || undefined,
    name: searchForm.name || undefined,
    type: searchForm.type || undefined,
    deptId: searchForm.deptId || undefined,
    status: searchForm.status ?? undefined
  })
  const blob = new Blob([res as unknown as BlobPart], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `设备列表_${new Date().toISOString().slice(0, 10)}.xlsx`
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
    await importDevice(importFile.value)
    ElMessage.success('导入成功')
    importVisible.value = false
    fetchList()
  } finally {
    importLoading.value = false
  }
}

async function handleDownloadTemplate() {
  const res = await downloadDeviceTemplate()
  const blob = new Blob([res as unknown as BlobPart], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = '设备导入模板.xlsx'
  link.click()
  URL.revokeObjectURL(link.href)
}

onMounted(() => {
  fetchList()
  fetchDeptTree()
})
</script>

<style scoped lang="scss">
.system-device {
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
