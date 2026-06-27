<template>
  <div class="green-factory-page">
    <div class="page-header">
      <h2>绿色工厂申报助手</h2>
      <p>绿色工厂申报资料管理与进度跟踪</p>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-select v-model="filters.status" placeholder="状态筛选" clearable style="width: 150px" @change="loadData">
        <el-option label="草稿" value="DRAFT" />
        <el-option label="已提交" value="SUBMITTED" />
        <el-option label="已通过" value="APPROVED" />
        <el-option label="已驳回" value="REJECTED" />
      </el-select>
      <el-input-number v-model="filters.declarationYear" :min="2020" :max="2030" placeholder="申报年度" style="width: 130px" controls-position="right" @change="loadData" />
      <el-button type="primary" @click="showCreateDialog">创建申报</el-button>
    </div>

    <!-- 申报列表 -->
    <el-table :data="tableData" border stripe style="width: 100%">
      <el-table-column prop="declarationName" label="申报名称" min-width="180" />
      <el-table-column prop="enterpriseName" label="企业名称" width="160" />
      <el-table-column prop="declarationYear" label="申报年度" width="100" />
      <el-table-column prop="progress" label="进度" width="180">
        <template #default="{ row }">
          <el-progress :percentage="row.progress || 0" :status="getProgressStatus(row.status)" :stroke-width="14" :text-inside="true" />
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusTag(row.status)" size="small">{{ getStatusName(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="submitTime" label="提交时间" width="170" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="viewDetail(row)">详情</el-button>
          <el-button v-if="row.status === 'DRAFT'" link type="success" size="small" @click="submitDeclaration(row)">提交审核</el-button>
          <el-button link type="danger" size="small" @click="deleteDeclaration(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination v-model:current-page="pagination.current" v-model:page-size="pagination.size" :total="pagination.total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @size-change="loadData" @current-change="loadData" />
    </div>

    <!-- 创建申报弹窗 -->
    <el-dialog v-model="createDialogVisible" title="创建绿色工厂申报" width="500px" destroy-on-close>
      <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="100px">
        <el-form-item label="申报名称" prop="declarationName">
          <el-input v-model="createForm.declarationName" placeholder="请输入申报名称" />
        </el-form-item>
        <el-form-item label="企业名称" prop="enterpriseName">
          <el-input v-model="createForm.enterpriseName" placeholder="请输入企业名称" />
        </el-form-item>
        <el-form-item label="申报年度" prop="declarationYear">
          <el-input-number v-model="createForm.declarationYear" :min="2020" :max="2030" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="createForm.remark" type="textarea" :rows="2" placeholder="备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="申报详情" width="800px">
      <template v-if="currentRecord">
        <el-descriptions :column="2" border style="margin-bottom: 20px">
          <el-descriptions-item label="申报名称">{{ currentRecord.declarationName }}</el-descriptions-item>
          <el-descriptions-item label="企业名称">{{ currentRecord.enterpriseName }}</el-descriptions-item>
          <el-descriptions-item label="申报年度">{{ currentRecord.declarationYear }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusTag(currentRecord.status)">{{ getStatusName(currentRecord.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="进度" :span="2">
            <el-progress :percentage="currentRecord.progress || 0" :stroke-width="16" :text-inside="true" />
          </el-descriptions-item>
        </el-descriptions>

        <h4>资料清单</h4>
        <el-table :data="currentRecord.items || []" border size="small" style="margin-top: 12px">
          <el-table-column prop="itemName" label="资料名称" min-width="200" />
          <el-table-column prop="itemCategory" label="类别" width="100" />
          <el-table-column prop="required" label="必填" width="70">
            <template #default="{ row }">
              <el-tag :type="row.required === 1 ? 'danger' : 'info'" size="small">{{ row.required === 1 ? '是' : '否' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="uploaded" label="已上传" width="80">
            <template #default="{ row }">
              <el-tag :type="row.uploaded === 1 ? 'success' : 'warning'" size="small">{{ row.uploaded === 1 ? '是' : '否' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="uploadTime" label="上传时间" width="170" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-upload
                :show-file-list="false"
                :before-upload="(file: File) => handleUpload(currentRecord!.id!, row.id!, file)"
                accept=".pdf,.doc,.docx,.jpg,.png"
              >
                <el-button link type="primary" size="small">上传</el-button>
              </el-upload>
            </template>
          </el-table-column>
        </el-table>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getGreenFactoryList, createGreenFactory, getGreenFactoryDetail, uploadGreenFactoryItem, submitGreenFactory, deleteGreenFactory, type GreenFactory } from '@/api/toolkit'

const tableData = ref<GreenFactory[]>([])
const createDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const createFormRef = ref<FormInstance>()
const currentRecord = ref<GreenFactory | null>(null)

const filters = reactive({ status: '', declarationYear: new Date().getFullYear() })
const pagination = reactive({ current: 1, size: 10, total: 0 })

const createForm = reactive({ declarationName: '', enterpriseName: '', declarationYear: new Date().getFullYear(), remark: '' })
const createRules = {
  declarationName: [{ required: true, message: '请输入申报名称', trigger: 'blur' }],
  enterpriseName: [{ required: true, message: '请输入企业名称', trigger: 'blur' }],
  declarationYear: [{ required: true, message: '请选择申报年度', trigger: 'change' }]
}

const statusMap: Record<string, string> = { DRAFT: '草稿', SUBMITTED: '已提交', APPROVED: '已通过', REJECTED: '已驳回' }
function getStatusName(status: string) { return statusMap[status] || status }
function getStatusTag(status: string) {
  const map: Record<string, string> = { DRAFT: 'info', SUBMITTED: 'warning', APPROVED: 'success', REJECTED: 'danger' }
  return map[status] || 'info'
}
function getProgressStatus(status: string) {
  if (status === 'APPROVED') return 'success'
  if (status === 'REJECTED') return 'exception'
  return undefined
}

async function loadData() {
  const res = await getGreenFactoryList({ current: pagination.current, size: pagination.size, status: filters.status || undefined, declarationYear: filters.declarationYear || undefined })
  tableData.value = res.data.list
  pagination.total = res.data.total
}

function showCreateDialog() {
  Object.assign(createForm, { declarationName: '', enterpriseName: '', declarationYear: new Date().getFullYear(), remark: '' })
  createDialogVisible.value = true
}

async function submitCreate() {
  await createFormRef.value?.validate()
  await createGreenFactory(createForm)
  ElMessage.success('创建成功')
  createDialogVisible.value = false
  loadData()
}

async function viewDetail(row: GreenFactory) {
  const res = await getGreenFactoryDetail(row.id!)
  currentRecord.value = res.data
  detailDialogVisible.value = true
}

async function handleUpload(declarationId: number, itemId: number, file: File) {
  await uploadGreenFactoryItem(declarationId, itemId, file)
  ElMessage.success('上传成功')
  // 刷新详情
  const res = await getGreenFactoryDetail(declarationId)
  currentRecord.value = res.data
  loadData()
  return false
}

async function submitDeclaration(row: GreenFactory) {
  await ElMessageBox.confirm('确定要提交审核吗？提交后将无法修改。', '提示', { type: 'warning' })
  await submitGreenFactory(row.id!)
  ElMessage.success('已提交审核')
  loadData()
}

async function deleteDeclaration(row: GreenFactory) {
  await ElMessageBox.confirm('确定要删除该申报吗？', '提示', { type: 'warning' })
  await deleteGreenFactory(row.id!)
  ElMessage.success('已删除')
  loadData()
}

onMounted(() => { loadData() })
</script>

<style scoped>
.green-factory-page { padding: 20px; }
.page-header { margin-bottom: 24px; }
.page-header h2 { margin: 0 0 8px; font-size: 24px; }
.page-header p { margin: 0; color: #909399; }
.filter-bar { display: flex; gap: 12px; margin-bottom: 16px; align-items: center; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
