<template>
  <div class="carbon-footprint">
    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <span class="title">产品碳足迹</span>
          <el-button type="primary" @click="handleAdd">新增记录</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="产品ID">
          <el-input-number v-model="queryParams.productId" :min="0" placeholder="产品ID" style="width:160px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="productId" label="产品ID" width="80" />
        <el-table-column prop="lifecycleStage" label="生命周期阶段" width="150" />
        <el-table-column prop="emission" label="排放量" width="150" />
        <el-table-column prop="emissionUnit" label="单位" width="80" />
        <el-table-column prop="calcMethod" label="计算方法" width="150" />
        <el-table-column prop="dataSource" label="数据来源" min-width="180" />
        <el-table-column prop="calcDate" label="计算日期" width="120" />
        <el-table-column label="操作" width="180" fixed="right">
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑碳足迹' : '新增碳足迹'" width="550px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="产品ID" prop="productId">
          <el-input-number v-model="form.productId" :min="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="生命周期阶段" prop="lifecycleStage">
          <el-select v-model="form.lifecycleStage" placeholder="请选择" style="width:100%">
            <el-option label="原材料获取" value="原材料获取" />
            <el-option label="生产制造" value="生产制造" />
            <el-option label="运输分销" value="运输分销" />
            <el-option label="使用阶段" value="使用阶段" />
            <el-option label="废弃回收" value="废弃回收" />
          </el-select>
        </el-form-item>
        <el-form-item label="排放量" prop="emission">
          <el-input-number v-model="form.emission" :min="0" :precision="4" style="width:100%" />
        </el-form-item>
        <el-form-item label="排放单位">
          <el-input v-model="form.emissionUnit" placeholder="如 tCO2e" />
        </el-form-item>
        <el-form-item label="计算方法">
          <el-input v-model="form.calcMethod" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="数据来源">
          <el-input v-model="form.dataSource" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="计算日期">
          <el-date-picker v-model="form.calcDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width:100%" />
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
import { getFootprintList, createFootprint, updateFootprint, deleteFootprint } from '@/api/carbon'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({ current: 1, size: 10, productId: null })

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(0)
const form = reactive({ productId: null, lifecycleStage: '生产制造', emission: null, emissionUnit: 'tCO2e', calcMethod: '', dataSource: '', calcDate: '' })
const rules = { productId: [{ required: true, message: '请输入产品ID', trigger: 'blur' }], lifecycleStage: [{ required: true, message: '请选择生命周期阶段', trigger: 'change' }], emission: [{ required: true, message: '请输入排放量', trigger: 'blur' }] }

onMounted(() => { getList() })

async function getList() {
  loading.value = true
  try {
    const params: any = { current: queryParams.current, size: queryParams.size }
    if (queryParams.productId) params.productId = queryParams.productId
    const res = await getFootprintList(params)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (e: any) { console.error(e) } finally { loading.value = false }
}

function handleQuery() { queryParams.current = 1; getList() }
function handleReset() { Object.assign(queryParams, { current: 1, size: 10, productId: null }); getList() }

function handleAdd() {
  isEdit.value = false; editId.value = 0
  Object.assign(form, { productId: null, lifecycleStage: '生产制造', emission: null, emissionUnit: 'tCO2e', calcMethod: '', dataSource: '', calcDate: '' })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true; editId.value = row.id
  Object.assign(form, { productId: row.productId, lifecycleStage: row.lifecycleStage, emission: row.emission, emissionUnit: row.emissionUnit, calcMethod: row.calcMethod || '', dataSource: row.dataSource || '', calcDate: row.calcDate || '' })
  dialogVisible.value = true
}

async function handleSubmit() {
  if (isEdit.value) {
    await updateFootprint(editId.value, form as any)
    ElMessage.success('修改成功')
  } else {
    await createFootprint(form as any)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  getList()
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm('确认删除该记录？', '提示')
  await deleteFootprint(row.id)
  ElMessage.success('删除成功')
  getList()
}
</script>

<style scoped>
.carbon-footprint { padding: 16px; }
.header-row { display: flex; justify-content: space-between; align-items: center; }
.title { font-size: 16px; font-weight: 600; }
.search-form { margin-bottom: 10px; }
.pagination-row { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>