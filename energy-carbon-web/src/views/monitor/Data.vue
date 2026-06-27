<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">监测数据</span>
          <el-button type="primary" @click="handleAdd">新增数据</el-button>
        </div>
      </template>
      <el-form :inline="true" class="search-form">
        <el-form-item label="计量器具">
          <el-select v-model="searchForm.meterId" placeholder="请选择计量器具" clearable>
            <el-option v-for="m in meterList" :key="m.id" :label="m.meterName" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="数据类型">
          <el-select v-model="searchForm.dataType" placeholder="请选择数据类型" clearable>
            <el-option v-for="t in dataTypeOptions" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="meterName" label="计量器具" width="140" />
        <el-table-column prop="dataType" label="数据类型" width="120" />
        <el-table-column prop="dataValue" label="数据值" width="120">
          <template #default="{ row }">{{ row.dataValue }} {{ row.unit }}</template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="100" />
        <el-table-column prop="collectTime" label="采集时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '异常' }}</el-tag>
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
        <el-form-item label="计量器具">
          <el-select v-model="form.meterId" placeholder="请选择计量器具">
            <el-option v-for="m in meterList" :key="m.id" :label="m.meterName" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="数据值">
          <el-input-number v-model="form.dataValue" :precision="2" />
        </el-form-item>
        <el-form-item label="数据类型">
          <el-select v-model="form.dataType" placeholder="请选择数据类型">
            <el-option v-for="t in dataTypeOptions" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMonitorDataList, createMonitorData, updateMonitorData, deleteMonitorData } from '@/api/monitor'
import { getMeterList } from '@/api/meter'

const loading = ref(false)
const tableData = ref([])
const meterList = ref([])
const dataTypeOptions = ref([
  { label: '电流', value: 'current' },
  { label: '电压', value: 'voltage' },
  { label: '功率', value: 'power' },
  { label: '电量', value: 'energy' },
])

const searchForm = reactive({ meterId: undefined as number | undefined, dataType: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const dialogTitle = ref('新增数据')
const form = reactive({ id: undefined as number | undefined, meterId: undefined as number | undefined, dataValue: 0, dataType: '', status: 1 })

async function fetchList() {
  loading.value = true
  try {
    const res: any = await getMonitorDataList({
      current: pagination.pageNum,
      size: pagination.pageSize,
      meterId: searchForm.meterId,
      dataType: searchForm.dataType,
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

async function fetchMeterList() {
  try {
    const res: any = await getMeterList({ current: 1, size: 999 })
    meterList.value = res?.data?.records || []
  } catch (error) {
    ElMessage.error('获取计量器具失败')
  }
}

function handleSearch() { pagination.pageNum = 1; fetchList() }
function handleReset() { searchForm.meterId = undefined; searchForm.dataType = ''; handleSearch() }
function handleSizeChange(val: number) { pagination.pageSize = val; fetchList() }
function handleCurrentChange(val: number) { pagination.pageNum = val; fetchList() }

function handleAdd() {
  dialogTitle.value = '新增数据'
  Object.assign(form, { id: undefined, meterId: undefined, dataValue: 0, dataType: '', status: 1 })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  dialogTitle.value = '编辑数据'
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  try {
    if (form.id) {
      await updateMonitorData(form.id, JSON.parse(JSON.stringify(form)))
    } else {
      await createMonitorData(JSON.parse(JSON.stringify(form)))
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
      await deleteMonitorData(row.id)
      ElMessage.success('删除成功')
      fetchList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  fetchList()
  fetchMeterList()
})
</script>

<style scoped lang="scss">
.page-container { padding: 10px; }
.search-form { margin-bottom: 20px; }
.pagination { margin-top: 20px; justify-content: flex-end; }
</style>
