<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">错峰排产小助手</span>
          <el-button type="primary" @click="handleAdd">新增方案</el-button>
        </div>
      </template>
      <el-form :inline="true" class="search-form">
        <el-form-item label="方案名称">
          <el-input v-model="searchForm.planName" placeholder="请输入方案名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="planName" label="方案名称" width="180" />
        <el-table-column prop="planDate" label="方案日期" width="140" />
        <el-table-column prop="shiftType" label="错峰类型" width="120" />
        <el-table-column prop="peakHours" label="高峰时段" width="140" />
        <el-table-column prop="offPeakHours" label="低谷时段" width="140" />
        <el-table-column prop="expectedSaving" label="预计节省" width="140" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '已启用' : '未启用' }}</el-tag>
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
        <el-form-item label="方案名称">
          <el-input v-model="form.planName" placeholder="请输入方案名称" />
        </el-form-item>
        <el-form-item label="方案日期">
          <el-input v-model="form.planDate" placeholder="请输入方案日期" />
        </el-form-item>
        <el-form-item label="错峰类型">
          <el-input v-model="form.shiftType" placeholder="请输入错峰类型" />
        </el-form-item>
        <el-form-item label="高峰时段">
          <el-input v-model="form.peakHours" placeholder="请输入高峰时段" />
        </el-form-item>
        <el-form-item label="低谷时段">
          <el-input v-model="form.offPeakHours" placeholder="请输入低谷时段" />
        </el-form-item>
        <el-form-item label="预计节省">
          <el-input-number v-model="form.expectedSaving" :precision="2" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">已启用</el-radio>
            <el-radio :label="0">未启用</el-radio>
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

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPeakShiftingList, createPeakShifting, updatePeakShifting, deletePeakShifting } from '@/api/toolkit'

const loading = ref(false)
const tableData = ref([])

const searchForm = reactive({ planName: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const dialogTitle = ref('新增方案')
const form = reactive({ id: undefined, planName: '', planDate: '', shiftType: '', peakHours: '', offPeakHours: '', expectedSaving: 0, status: 0 })

async function fetchList() {
  loading.value = true
  try {
    const res = await getPeakShiftingList({ current: pagination.pageNum, size: pagination.pageSize, ...searchForm })
    tableData.value = res.data.records || res.data.list || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

function handleSearch() { pagination.pageNum = 1; fetchList() }
function handleReset() { searchForm.planName = ''; handleSearch() }
function handleSizeChange(val) { pagination.pageSize = val; fetchList() }
function handleCurrentChange(val) { pagination.pageNum = val; fetchList() }

function handleAdd() {
  dialogTitle.value = '新增方案'
  Object.assign(form, { id: undefined, planName: '', planDate: '', shiftType: '', peakHours: '', offPeakHours: '', expectedSaving: 0, status: 0 })
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑方案'
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  try {
    if (form.id) {
      await updatePeakShifting(form.id, JSON.parse(JSON.stringify(form)))
    } else {
      await createPeakShifting(JSON.parse(JSON.stringify(form)))
    }
    ElMessage.success(dialogTitle.value + '成功')
    dialogVisible.value = false
    fetchList()
  } catch (error) {
    console.error(error)
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确认删除该记录？', '提示', { type: 'warning' })
    await deletePeakShifting(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch (error) {
    // user cancelled or error
  }
}

onMounted(fetchList)
</script>

<style scoped lang="scss">
.page-container { padding: 10px; }
.search-form { margin-bottom: 20px; }
.pagination { margin-top: 20px; justify-content: flex-end; }
</style>
