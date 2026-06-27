<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">供应链管理</span>
          <el-button type="primary" @click="handleAdd">新增供应商</el-button>
        </div>
      </template>
      <el-form :inline="true" class="search-form">
        <el-form-item label="供应商名称">
          <el-input v-model="searchForm.supplierName" placeholder="请输入供应商名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="supplierName" label="供应商名称" width="180" />
        <el-table-column prop="materialName" label="物料名称" width="160" />
        <el-table-column prop="carbonIntensity" label="碳排放强度" width="140" />
        <el-table-column prop="unit" label="单位" width="100" />
        <el-table-column prop="supplyVolume" label="供应量" width="120" />
        <el-table-column prop="reportYear" label="报告年度" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '合作中' : '已终止' }}</el-tag>
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
        <el-form-item label="供应商名称">
          <el-input v-model="form.supplierName" placeholder="请输入供应商名称" />
        </el-form-item>
        <el-form-item label="物料名称">
          <el-input v-model="form.materialName" placeholder="请输入物料名称" />
        </el-form-item>
        <el-form-item label="碳排放强度">
          <el-input-number v-model="form.carbonIntensity" :precision="2" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="供应量">
          <el-input-number v-model="form.supplyVolume" :precision="2" />
        </el-form-item>
        <el-form-item label="报告年度">
          <el-input v-model="form.reportYear" placeholder="请输入报告年度" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">合作中</el-radio>
            <el-radio :label="0">已终止</el-radio>
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
import { getSupplyChainList, createSupplyChain, updateSupplyChain, deleteSupplyChain } from '@/api/carbon'

const loading = ref(false)
const tableData = ref([])

const searchForm = reactive({ supplierName: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const dialogTitle = ref('新增供应商')
const form = reactive({ id: undefined, supplierName: '', materialName: '', carbonIntensity: 0, unit: '', supplyVolume: 0, reportYear: '', status: 1 })

async function fetchList() {
  loading.value = true
  try {
    const res = await getSupplyChainList({ current: pagination.pageNum, size: pagination.pageSize, ...searchForm })
    tableData.value = res.data.records || res.data.list || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

function handleSearch() { pagination.pageNum = 1; fetchList() }
function handleReset() { searchForm.supplierName = ''; handleSearch() }
function handleSizeChange(val) { pagination.pageSize = val; fetchList() }
function handleCurrentChange(val) { pagination.pageNum = val; fetchList() }

function handleAdd() {
  dialogTitle.value = '新增供应商'
  Object.assign(form, { id: undefined, supplierName: '', materialName: '', carbonIntensity: 0, unit: '', supplyVolume: 0, reportYear: '', status: 1 })
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑供应商'
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  try {
    if (form.id) {
      await updateSupplyChain(form.id, JSON.parse(JSON.stringify(form)))
    } else {
      await createSupplyChain(JSON.parse(JSON.stringify(form)))
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
    await deleteSupplyChain(row.id)
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
