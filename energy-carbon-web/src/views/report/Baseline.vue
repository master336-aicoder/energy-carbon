<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">基准分析</span>
          <el-button type="primary" @click="handleAdd">新增基准</el-button>
        </div>
      </template>
      <el-form :inline="true" class="search-form">
        <el-form-item label="基准类型">
          <el-input v-model="searchForm.baselineType" placeholder="请输入基准类型" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="energyUnitId" label="用能单元ID" width="120" />
        <el-table-column prop="startDate" label="开始日期" width="140" />
        <el-table-column prop="endDate" label="结束日期" width="140" />
        <el-table-column prop="baselineValue" label="基准值" width="120" />
        <el-table-column prop="baselineType" label="基准类型" width="140" show-overflow-tooltip />
        <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
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
        <el-form-item label="用能单元ID">
          <el-input-number v-model="form.energyUnitId" :min="1" />
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="form.startDate" type="date" placeholder="选择开始日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="form.endDate" type="date" placeholder="选择结束日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="基准值">
          <el-input-number v-model="form.baselineValue" :precision="2" />
        </el-form-item>
        <el-form-item label="基准类型">
          <el-input v-model="form.baselineType" placeholder="请输入基准类型" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
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
import { getBaselineList, createBaseline, updateBaseline, deleteBaseline } from '@/api/report'

const loading = ref(false)
const tableData = ref([])

const searchForm = reactive({ baselineType: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const dialogTitle = ref('新增基准')
const form = reactive({
  id: undefined as number | undefined,
  energyUnitId: undefined as number | undefined,
  startDate: '',
  endDate: '',
  baselineValue: 0,
  baselineType: '',
  remark: ''
})

async function fetchList() {
  loading.value = true
  try {
    const res: any = await getBaselineList({
      current: pagination.pageNum,
      size: pagination.pageSize,
      baselineType: searchForm.baselineType,
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
function handleReset() { searchForm.baselineType = ''; handleSearch() }
function handleSizeChange(val: number) { pagination.pageSize = val; fetchList() }
function handleCurrentChange(val: number) { pagination.pageNum = val; fetchList() }

function handleAdd() {
  dialogTitle.value = '新增基准'
  Object.assign(form, { id: undefined, energyUnitId: undefined, startDate: '', endDate: '', baselineValue: 0, baselineType: '', remark: '' })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  dialogTitle.value = '编辑基准'
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  try {
    if (form.id) {
      await updateBaseline(form.id, JSON.parse(JSON.stringify(form)))
    } else {
      await createBaseline(JSON.parse(JSON.stringify(form)))
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
      await deleteBaseline(row.id)
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
