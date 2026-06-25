<template>
  <div class="carbon-asset">
    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <span class="title">碳资产管理</span>
          <div>
            <el-button type="primary" @click="handleAdd">新增资产</el-button>
            <el-button type="success" @click="handleExport">导出</el-button>
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="资产类型">
          <el-select v-model="queryParams.assetType" placeholder="全部" clearable style="width:180px">
            <el-option label="配额" value="QUOTA" />
            <el-option label="CCER" value="CCER" />
            <el-option label="碳汇" value="CARBON_SINK" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:140px">
            <el-option label="有效" value="VALID" />
            <el-option label="已使用" value="USED" />
            <el-option label="已过期" value="EXPIRED" />
          </el-select>
        </el-form-item>
        <el-form-item label="年份">
          <el-date-picker v-model="queryParams.year" type="year" placeholder="选择年份" value-format="YYYY" style="width:140px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="assetName" label="资产名称" min-width="160" />
        <el-table-column prop="assetType" label="资产类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.assetType === 'QUOTA' ? '' : row.assetType === 'CCER' ? 'success' : 'warning'" size="small">
              {{ row.assetType === 'QUOTA' ? '配额' : row.assetType === 'CCER' ? 'CCER' : '碳汇' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量(tCO2)" width="130" />
        <el-table-column prop="source" label="来源" min-width="140" />
        <el-table-column prop="acquireDate" label="获取日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'VALID' ? 'success' : row.status === 'USED' ? 'warning' : 'danger'" size="small">
              {{ row.status === 'VALID' ? '有效' : row.status === 'USED' ? '已使用' : '已过期' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-row">
        <el-pagination
          v-model:current="queryParams.current"
          v-model:page-size="queryParams.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="getList"
          @size-change="getList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑碳资产' : '新增碳资产'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="资产名称" prop="assetName">
          <el-input v-model="form.assetName" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="资产类型" prop="assetType">
          <el-select v-model="form.assetType" placeholder="请选择" style="width:100%">
            <el-option label="配额" value="QUOTA" />
            <el-option label="CCER" value="CCER" />
            <el-option label="碳汇" value="CARBON_SINK" />
          </el-select>
        </el-form-item>
        <el-form-item label="资产编码">
          <el-input v-model="form.assetCode" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="数量(tCO2)" prop="quantity">
          <el-input-number v-model="form.quantity" :min="0" :precision="4" style="width:100%" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" placeholder="如: tCO2" />
        </el-form-item>
        <el-form-item label="来源">
          <el-input v-model="form.source" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="获取日期">
          <el-date-picker v-model="form.acquireDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="到期日期">
          <el-date-picker v-model="form.expireDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="成本(元)">
          <el-input-number v-model="form.cost" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择" style="width:100%">
            <el-option label="有效" value="VALID" />
            <el-option label="已使用" value="USED" />
            <el-option label="已过期" value="EXPIRED" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入" />
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
import { getAssetList, createAsset, updateAsset, deleteAsset } from '@/api/carbon'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({ current: 1, size: 10, assetType: '', status: '', year: null })

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(0)
const form = reactive({
  assetName: '',
  assetType: 'QUOTA',
  assetCode: '',
  quantity: null as number | null,
  unit: 'tCO2',
  acquireDate: '',
  expireDate: '',
  source: '',
  cost: null as number | null,
  status: 'VALID',
  remark: ''
})
const rules = {
  assetName: [{ required: true, message: '请输入资产名称', trigger: 'blur' }],
  assetType: [{ required: true, message: '请选择资产类型', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

onMounted(() => { getList() })

async function getList() {
  loading.value = true
  try {
    const params: any = { current: queryParams.current, size: queryParams.size }
    if (queryParams.assetType) params.assetType = queryParams.assetType
    if (queryParams.status) params.status = queryParams.status
    if (queryParams.year) params.year = parseInt(queryParams.year as string)
    const res = await getAssetList(params)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (e: any) { console.error(e) } finally { loading.value = false }
}

function handleQuery() { queryParams.current = 1; getList() }
function handleReset() { Object.assign(queryParams, { current: 1, size: 10, assetType: '', status: '', year: null }); getList() }

function handleAdd() {
  isEdit.value = false; editId.value = 0
  Object.assign(form, { assetName: '', assetType: 'QUOTA', assetCode: '', quantity: null, unit: 'tCO2', acquireDate: '', expireDate: '', source: '', cost: null, status: 'VALID', remark: '' })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true; editId.value = row.id
  Object.assign(form, {
    assetName: row.assetName,
    assetType: row.assetType,
    assetCode: row.assetCode || '',
    quantity: row.quantity,
    unit: row.unit || 'tCO2',
    acquireDate: row.acquireDate || '',
    expireDate: row.expireDate || '',
    source: row.source || '',
    cost: row.cost || null,
    status: row.status,
    remark: row.remark || ''
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  const data: any = { ...form }
  if (isEdit.value) {
    await updateAsset(editId.value, data)
    ElMessage.success('修改成功')
  } else {
    await createAsset(data)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  getList()
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm('确认删除该碳资产？', '提示')
  await deleteAsset(row.id)
  ElMessage.success('删除成功')
  getList()
}

function handleExport() {
  ElMessage.info('导出功能开发中')
}
</script>

<style scoped>
.carbon-asset { padding: 16px; }
.header-row { display: flex; justify-content: space-between; align-items: center; }
.title { font-size: 16px; font-weight: 600; }
.search-form { margin-bottom: 10px; }
.pagination-row { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
