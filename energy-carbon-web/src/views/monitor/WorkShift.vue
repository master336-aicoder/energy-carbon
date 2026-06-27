<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">班组管理</span>
          <el-button type="primary" @click="handleAdd">新增班组</el-button>
        </div>
      </template>
      <el-form :inline="true" class="search-form">
        <el-form-item label="班组名称">
          <el-input v-model="searchForm.shiftName" placeholder="请输入班组名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="shiftName" label="班组名称" width="140" />
        <el-table-column prop="shiftCode" label="班组编号" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="120" />
        <el-table-column prop="endTime" label="结束时间" width="120" />
        <el-table-column prop="leader" label="班组长" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
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
        <el-form-item label="班组名称">
          <el-input v-model="form.shiftName" placeholder="请输入班组名称" />
        </el-form-item>
        <el-form-item label="班组编号">
          <el-input v-model="form.shiftCode" placeholder="请输入班组编号" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-time-picker v-model="form.startTime" placeholder="选择开始时间" value-format="HH:mm" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-time-picker v-model="form.endTime" placeholder="选择结束时间" value-format="HH:mm" />
        </el-form-item>
        <el-form-item label="班组长">
          <el-input v-model="form.leader" placeholder="请输入班组长姓名" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
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
import { getWorkShiftList, createWorkShift, updateWorkShift, deleteWorkShift } from '@/api/monitor'

const loading = ref(false)
const tableData = ref([])

const searchForm = reactive({ shiftName: '', status: undefined as number | undefined })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const dialogTitle = ref('新增班组')
const form = reactive({
  id: undefined as number | undefined,
  shiftName: '',
  shiftCode: '',
  startTime: '',
  endTime: '',
  leader: '',
  status: 1
})

async function fetchList() {
  loading.value = true
  try {
    const res: any = await getWorkShiftList({
      current: pagination.pageNum,
      size: pagination.pageSize,
      shiftName: searchForm.shiftName,
      status: searchForm.status,
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
function handleReset() { searchForm.shiftName = ''; searchForm.status = undefined; handleSearch() }
function handleSizeChange(val: number) { pagination.pageSize = val; fetchList() }
function handleCurrentChange(val: number) { pagination.pageNum = val; fetchList() }

function handleAdd() {
  dialogTitle.value = '新增班组'
  Object.assign(form, { id: undefined, shiftName: '', shiftCode: '', startTime: '', endTime: '', leader: '', status: 1 })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  dialogTitle.value = '编辑班组'
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  try {
    if (form.id) {
      await updateWorkShift(form.id, JSON.parse(JSON.stringify(form)))
    } else {
      await createWorkShift(JSON.parse(JSON.stringify(form)))
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
      await deleteWorkShift(row.id)
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
