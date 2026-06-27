<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">设备管理</span>
          <el-button type="primary" @click="handleAdd">新增设备</el-button>
        </div>
      </template>
      <el-form :inline="true" class="search-form">
        <el-form-item label="设备名称">
          <el-input v-model="searchForm.deviceName" placeholder="请输入设备名称" clearable />
        </el-form-item>
        <el-form-item label="设备类型">
          <el-select v-model="searchForm.deviceType" placeholder="请选择设备类型" clearable>
            <el-option v-for="t in deviceTypeOptions" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="deviceName" label="设备名称" width="140" />
        <el-table-column prop="deviceType" label="设备类型" width="120" />
        <el-table-column prop="deviceCode" label="设备编号" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '运行' : '停机' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="安装位置" width="140" />
        <el-table-column prop="installTime" label="安装时间" width="180" />
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
      <el-form :model="form" :rules="rules" label-width="100px">
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="form.deviceName" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="设备编号" prop="deviceCode">
          <el-input v-model="form.deviceCode" placeholder="请输入设备编号" />
        </el-form-item>
        <el-form-item label="设备类型" prop="deviceType">
          <el-select v-model="form.deviceType" placeholder="请选择设备类型">
            <el-option v-for="t in deviceTypeOptions" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="安装位置">
          <el-input v-model="form.location" placeholder="请输入安装位置" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">运行</el-radio>
            <el-radio :label="0">停机</el-radio>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDeviceList, createDevice, updateDevice, deleteDevice } from '@/api/monitor'

const loading = ref(false)
const tableData = ref([])
const deviceTypeOptions = ref([
  { label: '变压器', value: 'transformer' },
  { label: '空压机', value: 'compressor' },
  { label: '锅炉', value: 'boiler' },
  { label: '制冷机', value: 'chiller' },
])

const searchForm = reactive({ deviceName: '', deviceType: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const dialogTitle = ref('新增设备')
const form = reactive({ id: undefined as number | undefined, deviceName: '', deviceCode: '', deviceType: '', location: '', status: 1 })
const rules = {
  deviceName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  deviceCode: [{ required: true, message: '请输入设备编号', trigger: 'blur' }],
  deviceType: [{ required: true, message: '请选择设备类型', trigger: 'change' }],
}

async function fetchList() {
  loading.value = true
  try {
    const res: any = await getDeviceList({
      current: pagination.pageNum,
      size: pagination.pageSize,
      deviceName: searchForm.deviceName,
      deviceType: searchForm.deviceType,
    })
    const data = res.data || {}
    tableData.value = data.records || []
    pagination.total = data.total || 0
  } catch (error) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() { pagination.pageNum = 1; fetchList() }
function handleReset() { searchForm.deviceName = ''; searchForm.deviceType = ''; handleSearch() }
function handleSizeChange(val: number) { pagination.pageSize = val; fetchList() }
function handleCurrentChange(val: number) { pagination.pageNum = val; fetchList() }

function handleAdd() {
  dialogTitle.value = '新增设备'
  Object.assign(form, { id: undefined, deviceName: '', deviceCode: '', deviceType: '', location: '', status: 1 })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  dialogTitle.value = '编辑设备'
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  try {
    if (form.id) {
      await updateDevice(form.id, JSON.parse(JSON.stringify(form)))
    } else {
      await createDevice(JSON.parse(JSON.stringify(form)))
    }
    ElMessage.success(dialogTitle.value + '成功')
    dialogVisible.value = false
    fetchList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

function handleDelete(row: any) {
  ElMessageBox.confirm('确认删除该记录？', '提示', { type: 'warning' }).then(async () => {
    try {
      await deleteDevice(row.id)
      ElMessage.success('删除成功')
      fetchList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(fetchList)
</script>

<style scoped lang="scss">
.page-container { padding: 10px; }
.search-form { margin-bottom: 20px; }
.pagination { margin-top: 20px; justify-content: flex-end; }
</style>
