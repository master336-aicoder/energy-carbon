<template>
  <div class="carbon-cbam">
    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <span class="title">CBAM碳边境调节机制</span>
          <div>
            <el-button type="primary" @click="handleAdd">新增申报</el-button>
            <el-button type="success" @click="handleExport">导出</el-button>
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="申报状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:140px">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已提交" value="SUBMITTED" />
            <el-option label="已审核" value="APPROVED" />
          </el-select>
        </el-form-item>
        <el-form-item label="年份">
          <el-date-picker v-model="queryParams.year" type="year" placeholder="选择年份" value-format="YYYY" style="width:140px" />
        </el-form-item>
        <el-form-item label="产品类别">
          <el-select v-model="queryParams.productCategory" placeholder="全部" clearable style="width:180px">
            <el-option label="钢铁" value="IRON_STEEL" />
            <el-option label="铝" value="ALUMINIUM" />
            <el-option label="水泥" value="CEMENT" />
            <el-option label="化肥" value="FERTILIZER" />
            <el-option label="电力" value="ELECTRICITY" />
            <el-option label="氢" value="HYDROGEN" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="declarationName" label="申报编号" min-width="160" />
        <el-table-column prop="productName" label="产品类别" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ productCategoryMap[row.productName] || row.productName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="originCountry" label="原产国" width="120" />
        <el-table-column prop="totalEmission" label="排放量(tCO2)" width="130" />
        <el-table-column prop="certificateNumber" label="CBAM证书数量" width="140" />
        <el-table-column prop="status" label="申报状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'APPROVED' ? 'success' : row.status === 'SUBMITTED' ? 'warning' : 'info'" size="small">
              {{ row.status === 'DRAFT' ? '草稿' : row.status === 'SUBMITTED' ? '已提交' : '已审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="declarationPeriod" label="申报日期" width="120" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" v-if="row.status === 'DRAFT'" @click="handleCalculate(row)">计算</el-button>
            <el-button link type="primary" v-if="row.status === 'DRAFT'" @click="handleSubmit(row)">提交</el-button>
            <el-button link type="primary" v-if="row.status === 'SUBMITTED'" @click="handleApprove(row)">审核</el-button>
            <el-button link type="danger" v-if="row.status === 'DRAFT'" @click="handleDelete(row)">删除</el-button>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑CBAM申报' : '新增CBAM申报'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="申报名称" prop="declarationName">
          <el-input v-model="form.declarationName" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="产品名称" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="产品编码">
          <el-input v-model="form.productCode" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="CN编码">
          <el-input v-model="form.cnCode" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="原产国">
          <el-input v-model="form.originCountry" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="进口数量" prop="importQuantity">
          <el-input-number v-model="form.importQuantity" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" placeholder="如: t" />
        </el-form-item>
        <el-form-item label="直接排放(tCO2)">
          <el-input-number v-model="form.directEmission" :min="0" :precision="4" style="width:100%" />
        </el-form-item>
        <el-form-item label="间接排放(tCO2)">
          <el-input-number v-model="form.indirectEmission" :min="0" :precision="4" style="width:100%" />
        </el-form-item>
        <el-form-item label="排放单位">
          <el-input v-model="form.emissionUnit" placeholder="如: tCO2" />
        </el-form-item>
        <el-form-item label="申报期次">
          <el-input v-model="form.declarationPeriod" placeholder="如: 2024-Q1" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择" style="width:100%">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已提交" value="SUBMITTED" />
            <el-option label="已审核" value="APPROVED" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCbamList, createCbam, updateCbam, deleteCbam, calculateCbam, submitCbam, approveCbam } from '@/api/carbon'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({ current: 1, size: 10, status: '', year: null, productCategory: '' })

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(0)
const form = reactive({
  declarationName: '',
  productName: '',
  productCode: '',
  cnCode: '',
  originCountry: '',
  importQuantity: null as number | null,
  unit: 't',
  directEmission: null as number | null,
  indirectEmission: null as number | null,
  totalEmission: null as number | null,
  emissionUnit: 'tCO2',
  certificateNumber: '',
  declarationPeriod: '',
  status: 'DRAFT',
  remark: ''
})
const rules = {
  declarationName: [{ required: true, message: '请输入申报名称', trigger: 'blur' }],
  productName: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  importQuantity: [{ required: true, message: '请输入进口数量', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const productCategoryMap: Record<string, string> = {
  IRON_STEEL: '钢铁',
  ALUMINIUM: '铝',
  CEMENT: '水泥',
  FERTILIZER: '化肥',
  ELECTRICITY: '电力',
  HYDROGEN: '氢'
}

onMounted(() => { getList() })

async function getList() {
  loading.value = true
  try {
    const params: any = { current: queryParams.current, size: queryParams.size }
    if (queryParams.status) params.status = queryParams.status
    if (queryParams.year) params.year = parseInt(queryParams.year as string)
    if (queryParams.productCategory) params.productCategory = queryParams.productCategory
    const res = await getCbamList(params)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (e: any) { console.error(e) } finally { loading.value = false }
}

function handleQuery() { queryParams.current = 1; getList() }
function handleReset() { Object.assign(queryParams, { current: 1, size: 10, status: '', year: null, productCategory: '' }); getList() }

function handleAdd() {
  isEdit.value = false; editId.value = 0
  Object.assign(form, {
    declarationName: '', productName: '', productCode: '', cnCode: '', originCountry: '',
    importQuantity: null, unit: 't', directEmission: null, indirectEmission: null,
    totalEmission: null, emissionUnit: 'tCO2', certificateNumber: '',
    declarationPeriod: '', status: 'DRAFT', remark: ''
  })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true; editId.value = row.id
  Object.assign(form, {
    declarationName: row.declarationName,
    productName: row.productName,
    productCode: row.productCode || '',
    cnCode: row.cnCode || '',
    originCountry: row.originCountry || '',
    importQuantity: row.importQuantity,
    unit: row.unit || 't',
    directEmission: row.directEmission || null,
    indirectEmission: row.indirectEmission || null,
    totalEmission: row.totalEmission || null,
    emissionUnit: row.emissionUnit || 'tCO2',
    certificateNumber: row.certificateNumber || '',
    declarationPeriod: row.declarationPeriod || '',
    status: row.status,
    remark: row.remark || ''
  })
  dialogVisible.value = true
}

async function handleSubmitForm() {
  const data: any = { ...form }
  if (isEdit.value) {
    await updateCbam(editId.value, data)
    ElMessage.success('修改成功')
  } else {
    await createCbam(data)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  getList()
}

async function handleCalculate(row: any) {
  await calculateCbam(row.id)
  ElMessage.success('排放计算完成')
  getList()
}

async function handleSubmit(row: any) {
  await submitCbam(row.id)
  ElMessage.success('已提交')
  getList()
}

async function handleApprove(row: any) {
  await approveCbam(row.id)
  ElMessage.success('已审核')
  getList()
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm('确认删除该CBAM申报？', '提示')
  await deleteCbam(row.id)
  ElMessage.success('删除成功')
  getList()
}

function handleExport() {
  ElMessage.info('导出功能开发中')
}
</script>

<style scoped>
.carbon-cbam { padding: 16px; }
.header-row { display: flex; justify-content: space-between; align-items: center; }
.title { font-size: 16px; font-weight: 600; }
.search-form { margin-bottom: 10px; }
.pagination-row { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
