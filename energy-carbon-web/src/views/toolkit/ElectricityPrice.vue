<template>
  <div class="electricity-price-page">
    <div class="page-header">
      <h2>分时电价管理器</h2>
      <p>分时电价配置与生效管理</p>
    </div>

    <div class="filter-bar">
      <el-button type="primary" @click="showAddDialog">新增电价配置</el-button>
    </div>

    <el-table :data="tableData" border stripe style="width: 100%">
      <el-table-column prop="priceName" label="电价名称" min-width="150" />
      <el-table-column prop="effectiveDate" label="生效日期" width="110" />
      <el-table-column prop="peakPrice" label="峰段电价(元/kWh)" width="150">
        <template #default="{ row }">
          <span style="color: #f56c6c; font-weight: bold">{{ row.peakPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="flatPrice" label="平段电价(元/kWh)" width="150">
        <template #default="{ row }">
          <span style="color: #e6a23c; font-weight: bold">{{ row.flatPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="valleyPrice" label="谷段电价(元/kWh)" width="150">
        <template #default="{ row }">
          <span style="color: #67c23a; font-weight: bold">{{ row.valleyPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="peakHours" label="峰时段" width="200" />
      <el-table-column prop="flatHours" label="平时段" width="200" />
      <el-table-column prop="valleyHours" label="谷时段" width="200" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="editRow(row)">编辑</el-button>
          <el-button link :type="row.status === 1 ? 'warning' : 'success'" size="small" @click="toggleStatus(row)">
            {{ row.status === 1 ? '停用' : '启用' }}
          </el-button>
          <el-button link type="danger" size="small" @click="deleteRow(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:current-page="pagination.current" v-model:page-size="pagination.size" :total="pagination.total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @size-change="loadData" @current-change="loadData" />
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editingRow ? '编辑电价配置' : '新增电价配置'" width="600px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="140px">
        <el-form-item label="电价名称" prop="priceName">
          <el-input v-model="form.priceName" placeholder="请输入电价名称" />
        </el-form-item>
        <el-form-item label="生效日期" prop="effectiveDate">
          <el-date-picker v-model="form.effectiveDate" type="date" placeholder="选择生效日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="峰段电价(元/kWh)" prop="peakPrice">
          <el-input-number v-model="form.peakPrice" :precision="4" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="平段电价(元/kWh)" prop="flatPrice">
          <el-input-number v-model="form.flatPrice" :precision="4" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="谷段电价(元/kWh)" prop="valleyPrice">
          <el-input-number v-model="form.valleyPrice" :precision="4" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="峰时段">
          <el-input v-model="form.peakHours" placeholder="如 08:00-11:00,18:00-21:00" />
        </el-form-item>
        <el-form-item label="平时段">
          <el-input v-model="form.flatHours" placeholder="如 07:00-08:00,11:00-18:00" />
        </el-form-item>
        <el-form-item label="谷时段">
          <el-input v-model="form.valleyHours" placeholder="如 21:00-07:00" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getElectricityPriceList, addElectricityPrice, updateElectricityPrice, setElectricityPriceStatus, deleteElectricityPrice, type ElectricityPrice } from '@/api/toolkit'

const tableData = ref<ElectricityPrice[]>([])
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const editingRow = ref<ElectricityPrice | null>(null)
const pagination = reactive({ current: 1, size: 10, total: 0 })

const form = reactive({ priceName: '', effectiveDate: '', peakPrice: 0, flatPrice: 0, valleyPrice: 0, peakHours: '', flatHours: '', valleyHours: '' })
const rules = {
  priceName: [{ required: true, message: '请输入电价名称', trigger: 'blur' }],
  effectiveDate: [{ required: true, message: '请选择生效日期', trigger: 'change' }],
  peakPrice: [{ required: true, message: '请输入峰段电价', trigger: 'blur' }],
  flatPrice: [{ required: true, message: '请输入平段电价', trigger: 'blur' }],
  valleyPrice: [{ required: true, message: '请输入谷段电价', trigger: 'blur' }]
}

async function loadData() {
  const res = await getElectricityPriceList({ current: pagination.current, size: pagination.size })
  tableData.value = res.data.list
  pagination.total = res.data.total
}

function showAddDialog() {
  editingRow.value = null
  Object.assign(form, { priceName: '', effectiveDate: '', peakPrice: 0, flatPrice: 0, valleyPrice: 0, peakHours: '', flatHours: '', valleyHours: '' })
  dialogVisible.value = true
}

function editRow(row: ElectricityPrice) {
  editingRow.value = row
  Object.assign(form, row)
  dialogVisible.value = true
}

async function submitForm() {
  await formRef.value?.validate()
  if (editingRow.value) {
    await updateElectricityPrice(editingRow.value.id!, form)
    ElMessage.success('修改成功')
  } else {
    await addElectricityPrice(form as ElectricityPrice)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

async function toggleStatus(row: ElectricityPrice) {
  const newStatus = row.status === 1 ? 0 : 1
  await setElectricityPriceStatus(row.id!, newStatus)
  ElMessage.success(newStatus === 1 ? '已启用' : '已停用')
  loadData()
}

async function deleteRow(row: ElectricityPrice) {
  await ElMessageBox.confirm('确定要删除该电价配置吗？', '提示', { type: 'warning' })
  await deleteElectricityPrice(row.id!)
  ElMessage.success('已删除')
  loadData()
}

onMounted(() => { loadData() })
</script>

<style scoped>
.electricity-price-page { padding: 20px; }
.page-header { margin-bottom: 24px; }
.page-header h2 { margin: 0 0 8px; font-size: 24px; }
.page-header p { margin: 0; color: #909399; }
.filter-bar { display: flex; gap: 12px; margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
