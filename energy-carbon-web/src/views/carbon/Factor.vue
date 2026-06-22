<template>
  <div class="carbon-factor">
    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <span class="title">排放因子库</span>
          <el-button type="primary" @click="handleAdd">新增因子</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="因子名称">
          <el-input v-model="queryParams.factorName" placeholder="请输入" clearable style="width:180px" />
        </el-form-item>
        <el-form-item label="因子类别">
          <el-select v-model="queryParams.factorCategory" placeholder="全部" clearable style="width:180px">
            <el-option label="范围一-燃料排放" value="SCOPE1_FUEL" />
            <el-option label="范围二-电力排放" value="SCOPE2_GRID" />
            <el-option label="范围三-其他排放" value="SCOPE3_OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="生效年份">
          <el-date-picker v-model="queryParams.effectiveYear" type="year" placeholder="选择年份" value-format="YYYY" style="width:140px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="factorName" label="因子名称" min-width="180" />
        <el-table-column prop="factorCategory" label="因子类别" width="160">
          <template #default="{ row }">
            <el-tag :type="row.factorCategory === 'SCOPE1_FUEL' ? 'danger' : row.factorCategory === 'SCOPE2_GRID' ? 'warning' : 'info'" size="small">
              {{ row.factorCategory === 'SCOPE1_FUEL' ? '燃料排放' : row.factorCategory === 'SCOPE2_GRID' ? '电力排放' : '其他排放' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="factorValue" label="因子值" width="120" />
        <el-table-column prop="factorUnit" label="单位" width="120" />
        <el-table-column prop="source" label="数据来源" min-width="180" />
        <el-table-column prop="effectiveYear" label="生效年份" width="100" />
        <el-table-column label="默认" width="80">
          <template #default="{ row }">
            <el-tag :type="row.defaultFlag === 1 ? 'success' : 'info'" size="small">{{ row.defaultFlag === 1 ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" v-if="!row.defaultFlag" @click="handleSetDefault(row)">设默认</el-button>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑因子' : '新增因子'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="因子名称" prop="factorName">
          <el-input v-model="form.factorName" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="因子类别" prop="factorCategory">
          <el-select v-model="form.factorCategory" placeholder="请选择" style="width:100%">
            <el-option label="范围一-燃料排放" value="SCOPE1_FUEL" />
            <el-option label="范围二-电力排放" value="SCOPE2_GRID" />
            <el-option label="范围三-其他排放" value="SCOPE3_OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联能源类型">
          <el-input-number v-model="form.energyTypeId" :min="0" style="width:100%" placeholder="能源类型ID" />
        </el-form-item>
        <el-form-item label="因子值" prop="factorValue">
          <el-input-number v-model="form.factorValue" :min="0" :precision="8" style="width:100%" />
        </el-form-item>
        <el-form-item label="单位" prop="factorUnit">
          <el-input v-model="form.factorUnit" placeholder="如: tCO2/t, kgCO2/kWh" />
        </el-form-item>
        <el-form-item label="数据来源">
          <el-input v-model="form.source" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="生效年份">
          <el-date-picker v-model="form.effectiveYear" type="year" placeholder="选择年份" value-format="YYYY" style="width:100%" />
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
import { getFactorList, createFactor, updateFactor, deleteFactor, setDefaultFactor } from '@/api/carbon'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({ current: 1, size: 10, factorName: '', factorCategory: '', effectiveYear: null })

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(0)
const form = reactive({ factorName: '', factorCategory: 'SCOPE1_FUEL', energyTypeId: 0, factorValue: null, factorUnit: '', source: '', effectiveYear: null, remark: '' })
const rules = { factorName: [{ required: true, message: '请输入因子名称', trigger: 'blur' }], factorCategory: [{ required: true, message: '请选择因子类别', trigger: 'change' }], factorValue: [{ required: true, message: '请输入因子值', trigger: 'blur' }], factorUnit: [{ required: true, message: '请输入单位', trigger: 'blur' }] }

onMounted(() => { getList() })

async function getList() {
  loading.value = true
  try {
    const params: any = { current: queryParams.current, size: queryParams.size }
    if (queryParams.factorName) params.factorName = queryParams.factorName
    if (queryParams.factorCategory) params.factorCategory = queryParams.factorCategory
    if (queryParams.effectiveYear) params.effectiveYear = parseInt(queryParams.effectiveYear as string)
    const res = await getFactorList(params)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (e: any) { console.error(e) } finally { loading.value = false }
}

function handleQuery() { queryParams.current = 1; getList() }
function handleReset() { Object.assign(queryParams, { current: 1, size: 10, factorName: '', factorCategory: '', effectiveYear: null }); getList() }

function handleAdd() {
  isEdit.value = false; editId.value = 0
  Object.assign(form, { factorName: '', factorCategory: 'SCOPE1_FUEL', energyTypeId: 0, factorValue: null, factorUnit: '', source: '', effectiveYear: null, remark: '' })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true; editId.value = row.id
  Object.assign(form, { factorName: row.factorName, factorCategory: row.factorCategory, energyTypeId: row.energyTypeId || 0, factorValue: row.factorValue, factorUnit: row.factorUnit, source: row.source || '', effectiveYear: row.effectiveYear || null, remark: row.remark || '' })
  dialogVisible.value = true
}

async function handleSubmit() {
  const data: any = { ...form }
  if (data.effectiveYear) data.effectiveYear = parseInt(data.effectiveYear)
  if (isEdit.value) {
    await updateFactor(editId.value, data)
    ElMessage.success('修改成功')
  } else {
    await createFactor(data)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  getList()
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm('确认删除该因子？', '提示')
  await deleteFactor(row.id)
  ElMessage.success('删除成功')
  getList()
}

async function handleSetDefault(row: any) {
  await setDefaultFactor(row.id)
  ElMessage.success('已设为默认因子')
  getList()
}
</script>

<style scoped>
.carbon-factor { padding: 16px; }
.header-row { display: flex; justify-content: space-between; align-items: center; }
.title { font-size: 16px; font-weight: 600; }
.search-form { margin-bottom: 10px; }
.pagination-row { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>