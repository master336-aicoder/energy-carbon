<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">产量报表</span>
          <el-button type="primary" @click="handleAdd">新增记录</el-button>
        </div>
      </template>
      <el-form :inline="true" class="search-form">
        <el-form-item label="产品名称">
          <el-input v-model="searchForm.productName" placeholder="请输入产品名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="productName" label="产品名称" width="160" />
        <el-table-column prop="productCode" label="产品编码" width="120" />
        <el-table-column prop="planQty" label="计划产量" width="120" />
        <el-table-column prop="actualQty" label="实际产量" width="120" />
        <el-table-column prop="unit" label="单位" width="100" />
        <el-table-column prop="productionDate" label="生产日期" width="140" />
        <el-table-column prop="workshop" label="生产车间" width="140" />
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
        <el-form-item label="产品名称">
          <el-input v-model="form.productName" placeholder="请输入产品名称" />
        </el-form-item>
        <el-form-item label="产品编码">
          <el-input v-model="form.productCode" placeholder="请输入产品编码" />
        </el-form-item>
        <el-form-item label="计划产量">
          <el-input-number v-model="form.planQty" :precision="2" />
        </el-form-item>
        <el-form-item label="实际产量">
          <el-input-number v-model="form.actualQty" :precision="2" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="生产日期">
          <el-date-picker v-model="form.productionDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="生产车间">
          <el-input v-model="form.workshop" placeholder="请输入生产车间" />
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
import { getProductionList, createProduction, updateProduction, deleteProduction } from '@/api/report'

const loading = ref(false)
const tableData = ref([])

const searchForm = reactive({ productName: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const dialogTitle = ref('新增记录')
const form = reactive({
  id: undefined as number | undefined,
  productName: '',
  productCode: '',
  planQty: 0,
  actualQty: 0,
  unit: '',
  productionDate: '',
  workshop: ''
})

async function fetchList() {
  loading.value = true
  try {
    const res: any = await getProductionList({
      current: pagination.pageNum,
      size: pagination.pageSize,
      productName: searchForm.productName,
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
function handleReset() { searchForm.productName = ''; handleSearch() }
function handleSizeChange(val: number) { pagination.pageSize = val; fetchList() }
function handleCurrentChange(val: number) { pagination.pageNum = val; fetchList() }

function handleAdd() {
  dialogTitle.value = '新增记录'
  Object.assign(form, { id: undefined, productName: '', productCode: '', planQty: 0, actualQty: 0, unit: '', productionDate: '', workshop: '' })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  dialogTitle.value = '编辑记录'
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  try {
    if (form.id) {
      await updateProduction(form.id, JSON.parse(JSON.stringify(form)))
    } else {
      await createProduction(JSON.parse(JSON.stringify(form)))
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
      await deleteProduction(row.id)
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
