<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">指标报表</span>
          <el-button type="primary" @click="handleAdd">新增指标</el-button>
        </div>
      </template>
      <el-form :inline="true" class="search-form">
        <el-form-item label="指标名称">
          <el-input v-model="searchForm.indicatorName" placeholder="请输入指标名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="indicatorName" label="指标名称" width="160" />
        <el-table-column prop="indicatorCode" label="指标编码" width="120" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="unit" label="单位" width="100" />
        <el-table-column prop="targetValue" label="目标值" width="120" />
        <el-table-column prop="actualValue" label="实际值" width="120" />
        <el-table-column prop="period" label="统计周期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '停用' }}</el-tag>
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
        <el-form-item label="指标名称">
          <el-input v-model="form.indicatorName" placeholder="请输入指标名称" />
        </el-form-item>
        <el-form-item label="指标编码">
          <el-input v-model="form.indicatorCode" placeholder="请输入指标编码" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="form.category" placeholder="请输入分类" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="目标值">
          <el-input-number v-model="form.targetValue" :precision="2" />
        </el-form-item>
        <el-form-item label="实际值">
          <el-input-number v-model="form.actualValue" :precision="2" />
        </el-form-item>
        <el-form-item label="统计周期">
          <el-input v-model="form.period" placeholder="请输入统计周期" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
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
import { getIndicatorList, createIndicator, updateIndicator, deleteIndicator } from '@/api/report'

const loading = ref(false)
const tableData = ref([])

const searchForm = reactive({ indicatorName: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const dialogTitle = ref('新增指标')
const form = reactive({
  id: undefined as number | undefined,
  indicatorName: '',
  indicatorCode: '',
  category: '',
  unit: '',
  targetValue: 0,
  actualValue: 0,
  period: '',
  status: 1
})

async function fetchList() {
  loading.value = true
  try {
    const res: any = await getIndicatorList({
      current: pagination.pageNum,
      size: pagination.pageSize,
      indicatorName: searchForm.indicatorName,
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
function handleReset() { searchForm.indicatorName = ''; handleSearch() }
function handleSizeChange(val: number) { pagination.pageSize = val; fetchList() }
function handleCurrentChange(val: number) { pagination.pageNum = val; fetchList() }

function handleAdd() {
  dialogTitle.value = '新增指标'
  Object.assign(form, { id: undefined, indicatorName: '', indicatorCode: '', category: '', unit: '', targetValue: 0, actualValue: 0, period: '', status: 1 })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  dialogTitle.value = '编辑指标'
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  try {
    if (form.id) {
      await updateIndicator(form.id, JSON.parse(JSON.stringify(form)))
    } else {
      await createIndicator(JSON.parse(JSON.stringify(form)))
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
      await deleteIndicator(row.id)
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
