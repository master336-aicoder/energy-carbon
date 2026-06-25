<template>
  <div class="carbon-supply-chain">
    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <span class="title">供应链碳足迹</span>
          <div>
            <el-button type="primary" @click="handleAdd">新增记录</el-button>
            <el-button type="success" @click="handleExport">导出</el-button>
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="供应商名称">
          <el-input v-model="queryParams.supplierName" placeholder="请输入" clearable style="width:180px" />
        </el-form-item>
        <el-form-item label="产品类别">
          <el-select v-model="queryParams.productCategory" placeholder="全部" clearable style="width:180px">
            <el-option label="原材料" value="RAW_MATERIAL" />
            <el-option label="零部件" value="COMPONENT" />
            <el-option label="半成品" value="SEMI_FINISHED" />
            <el-option label="成品" value="FINISHED" />
            <el-option label="包装材料" value="PACKAGING" />
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
        <el-table-column prop="supplierName" label="供应商名称" min-width="160" />
        <el-table-column prop="productName" label="产品名称" min-width="140" />
        <el-table-column prop="carbonFootprintValue" label="碳排放量(tCO2)" width="150" />
        <el-table-column label="碳足迹强度" width="130">
          <template #default="{ row }">
            <span>{{ row.unit ? `${row.carbonFootprintValue} ${row.unit}` : `${row.carbonFootprintValue} tCO2` }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="validDate" label="核算日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'warning' : 'info'" size="small">
              {{ row.status === 1 ? '有效' : row.status === 2 ? '已过期' : '待审核' }}
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑供应链碳足迹' : '新增供应链碳足迹'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="供应商名称" prop="supplierName">
          <el-input v-model="form.supplierName" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="供应商编码">
          <el-input v-model="form.supplierCode" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="产品名称" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="产品类别">
          <el-select v-model="form.productCategory" placeholder="请选择" style="width:100%">
            <el-option label="原材料" value="RAW_MATERIAL" />
            <el-option label="零部件" value="COMPONENT" />
            <el-option label="半成品" value="SEMI_FINISHED" />
            <el-option label="成品" value="FINISHED" />
            <el-option label="包装材料" value="PACKAGING" />
          </el-select>
        </el-form-item>
        <el-form-item label="碳排放量" prop="carbonFootprintValue">
          <el-input-number v-model="form.carbonFootprintValue" :min="0" :precision="4" style="width:100%" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" placeholder="如: tCO2" />
        </el-form-item>
        <el-form-item label="认证类型">
          <el-input v-model="form.certType" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="认证编号">
          <el-input v-model="form.certNumber" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="有效期至">
          <el-date-picker v-model="form.validDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="数据来源">
          <el-input v-model="form.dataSource" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择" style="width:100%">
            <el-option label="待审核" :value="0" />
            <el-option label="有效" :value="1" />
            <el-option label="已过期" :value="2" />
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
import { getSupplyChainList, createSupplyChain, updateSupplyChain, deleteSupplyChain } from '@/api/carbon'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({ current: 1, size: 10, supplierName: '', productCategory: '', year: null })

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(0)
const form = reactive({
  supplierName: '',
  supplierCode: '',
  productName: '',
  productCategory: '',
  carbonFootprintValue: null as number | null,
  unit: 'tCO2',
  certType: '',
  certNumber: '',
  validDate: '',
  dataSource: '',
  status: 1 as number,
  remark: ''
})
const rules = {
  supplierName: [{ required: true, message: '请输入供应商名称', trigger: 'blur' }],
  productName: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  carbonFootprintValue: [{ required: true, message: '请输入碳排放量', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

onMounted(() => { getList() })

async function getList() {
  loading.value = true
  try {
    const params: any = { current: queryParams.current, size: queryParams.size }
    if (queryParams.supplierName) params.supplierName = queryParams.supplierName
    if (queryParams.productCategory) params.productCategory = queryParams.productCategory
    if (queryParams.year) params.year = parseInt(queryParams.year as string)
    const res = await getSupplyChainList(params)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (e: any) { console.error(e) } finally { loading.value = false }
}

function handleQuery() { queryParams.current = 1; getList() }
function handleReset() { Object.assign(queryParams, { current: 1, size: 10, supplierName: '', productCategory: '', year: null }); getList() }

function handleAdd() {
  isEdit.value = false; editId.value = 0
  Object.assign(form, {
    supplierName: '', supplierCode: '', productName: '', productCategory: '',
    carbonFootprintValue: null, unit: 'tCO2', certType: '', certNumber: '',
    validDate: '', dataSource: '', status: 1, remark: ''
  })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true; editId.value = row.id
  Object.assign(form, {
    supplierName: row.supplierName,
    supplierCode: row.supplierCode || '',
    productName: row.productName,
    productCategory: row.productCategory || '',
    carbonFootprintValue: row.carbonFootprintValue,
    unit: row.unit || 'tCO2',
    certType: row.certType || '',
    certNumber: row.certNumber || '',
    validDate: row.validDate || '',
    dataSource: row.dataSource || '',
    status: row.status,
    remark: row.remark || ''
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  const data: any = { ...form }
  if (isEdit.value) {
    await updateSupplyChain(editId.value, data)
    ElMessage.success('修改成功')
  } else {
    await createSupplyChain(data)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  getList()
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm('确认删除该供应链碳足迹记录？', '提示')
  await deleteSupplyChain(row.id)
  ElMessage.success('删除成功')
  getList()
}

function handleExport() {
  ElMessage.info('导出功能开发中')
}
</script>

<style scoped>
.carbon-supply-chain { padding: 16px; }
.header-row { display: flex; justify-content: space-between; align-items: center; }
.title { font-size: 16px; font-weight: 600; }
.search-form { margin-bottom: 10px; }
.pagination-row { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
