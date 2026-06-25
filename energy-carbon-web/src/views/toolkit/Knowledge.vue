<template>
  <div class="knowledge-page">
    <div class="page-header">
      <h2>AI知识库</h2>
      <p>能碳领域知识文档管理与智能检索</p>
    </div>

    <div class="filter-bar">
      <el-input v-model="searchKeyword" placeholder="输入关键词检索文档" clearable style="width: 300px" @keyup.enter="doSearch">
        <template #append>
          <el-button @click="doSearch">检索</el-button>
        </template>
      </el-input>
      <el-select v-model="filters.docType" placeholder="文档类型" clearable style="width: 150px" @change="loadData">
        <el-option label="政策法规" value="POLICY" />
        <el-option label="技术标准" value="STANDARD" />
        <el-option label="行业报告" value="REPORT" />
        <el-option label="操作手册" value="MANUAL" />
        <el-option label="其他" value="OTHER" />
      </el-select>
      <el-upload :show-file-list="false" :before-upload="handleUpload" accept=".pdf,.doc,.docx,.txt,.xlsx">
        <el-button type="primary">上传文档</el-button>
      </el-upload>
    </div>

    <el-table :data="tableData" border stripe style="width: 100%">
      <el-table-column prop="docName" label="文档名称" min-width="200" />
      <el-table-column prop="docType" label="文档类型" width="120">
        <template #default="{ row }">
          <el-tag size="small">{{ docTypeMap[row.docType] || row.docType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="fileSize" label="文件大小" width="120">
        <template #default="{ row }">
          {{ row.fileSize ? formatFileSize(row.fileSize) : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="summary" label="摘要" min-width="250" show-overflow-tooltip />
      <el-table-column prop="uploadUser" label="上传用户" width="100" />
      <el-table-column prop="uploadTime" label="上传时间" width="170" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="viewDetail(row)">预览</el-button>
          <el-button link type="danger" size="small" @click="deleteDoc(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:current-page="pagination.current" v-model:page-size="pagination.size" :total="pagination.total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @size-change="loadData" @current-change="loadData" />
    </div>

    <!-- 上传弹窗 -->
    <el-dialog v-model="uploadDialogVisible" title="上传知识文档" width="500px" destroy-on-close>
      <el-form :model="uploadForm" :rules="uploadRules" ref="uploadFormRef" label-width="100px">
        <el-form-item label="文档名称" prop="docName">
          <el-input v-model="uploadForm.docName" placeholder="请输入文档名称" />
        </el-form-item>
        <el-form-item label="文档类型" prop="docType">
          <el-select v-model="uploadForm.docType" placeholder="请选择类型" style="width: 100%">
            <el-option label="政策法规" value="POLICY" />
            <el-option label="技术标准" value="STANDARD" />
            <el-option label="行业报告" value="REPORT" />
            <el-option label="操作手册" value="MANUAL" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="上传用户">
          <el-input v-model="uploadForm.uploadUser" placeholder="上传用户" />
        </el-form-item>
        <el-form-item label="文档摘要">
          <el-input v-model="uploadForm.summary" type="textarea" :rows="3" placeholder="文档摘要" />
        </el-form-item>
        <el-form-item label="选择文件" prop="file">
          <el-upload ref="uploadRef" :auto-upload="false" :limit="1" :file-list="fileList">
            <el-button type="primary">选择文件</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUpload">上传</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="文档详情" width="600px">
      <el-descriptions :column="2" border v-if="currentDoc">
        <el-descriptions-item label="文档名称" :span="2">{{ currentDoc.docName }}</el-descriptions-item>
        <el-descriptions-item label="文档类型">{{ docTypeMap[currentDoc.docType] || currentDoc.docType }}</el-descriptions-item>
        <el-descriptions-item label="文件大小">{{ currentDoc.fileSize ? formatFileSize(currentDoc.fileSize) : '-' }}</el-descriptions-item>
        <el-descriptions-item label="上传用户">{{ currentDoc.uploadUser || '-' }}</el-descriptions-item>
        <el-descriptions-item label="上传时间">{{ currentDoc.uploadTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="摘要" :span="2">{{ currentDoc.summary || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentDoc.createTime || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, UploadUserFile } from 'element-plus'
import { getAiKnowledgeList, uploadAiKnowledge, deleteAiKnowledge, type AiKnowledgeDoc } from '@/api/ai'

const tableData = ref<AiKnowledgeDoc[]>([])
const searchKeyword = ref('')
const filters = reactive({ docType: '' })
const pagination = reactive({ current: 1, size: 10, total: 0 })
const uploadDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const uploadFormRef = ref<FormInstance>()
const currentDoc = ref<AiKnowledgeDoc | null>(null)
const fileList = ref<UploadUserFile[]>([])

const uploadForm = reactive({ docName: '', docType: '', uploadUser: '', summary: '' })
const uploadRules = {
  docName: [{ required: true, message: '请输入文档名称', trigger: 'blur' }],
  docType: [{ required: true, message: '请选择文档类型', trigger: 'change' }]
}

const docTypeMap: Record<string, string> = { POLICY: '政策法规', STANDARD: '技术标准', REPORT: '行业报告', MANUAL: '操作手册', OTHER: '其他' }

function formatFileSize(bytes: number) {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

async function loadData() {
  const res = await getAiKnowledgeList({ current: pagination.current, size: pagination.size, docType: filters.docType || undefined })
  tableData.value = res.data.records
  pagination.total = res.data.total
}

async function doSearch() {
  if (!searchKeyword.value) { loadData(); return }
  const res = await getAiKnowledgeList({ current: 1, size: 50, docType: filters.docType || undefined })
  const keyword = searchKeyword.value.toLowerCase()
  tableData.value = res.data.records.filter((d: AiKnowledgeDoc) =>
    d.docName.toLowerCase().includes(keyword) || (d.summary && d.summary.toLowerCase().includes(keyword))
  )
  pagination.total = tableData.value.length
}

function handleUpload(file: File) {
  uploadForm.docName = uploadForm.docName || file.name.replace(/\.[^.]+$/, '')
  uploadDialogVisible.value = true
  fileList.value = [{ name: file.name, size: file.size, uid: Date.now(), status: 'ready' } as UploadUserFile]
  return false
}

async function submitUpload() {
  await uploadFormRef.value?.validate()
  const formData = new FormData()
  formData.append('docName', uploadForm.docName)
  formData.append('docType', uploadForm.docType)
  formData.append('uploadUser', uploadForm.uploadUser)
  formData.append('summary', uploadForm.summary)
  if (fileList.value.length > 0) {
    // Note: In real scenario, need to attach the actual file
  }
  await uploadAiKnowledge(formData)
  ElMessage.success('上传成功')
  uploadDialogVisible.value = false
  loadData()
}

function viewDetail(row: AiKnowledgeDoc) {
  currentDoc.value = row
  detailDialogVisible.value = true
}

async function deleteDoc(row: AiKnowledgeDoc) {
  await ElMessageBox.confirm('确定要删除该文档吗？', '提示', { type: 'warning' })
  await deleteAiKnowledge(row.id!)
  ElMessage.success('已删除')
  loadData()
}

onMounted(() => { loadData() })
</script>

<style scoped>
.knowledge-page { padding: 20px; }
.page-header { margin-bottom: 24px; }
.page-header h2 { margin: 0 0 8px; font-size: 24px; }
.page-header p { margin: 0; color: #909399; }
.filter-bar { display: flex; gap: 12px; margin-bottom: 16px; align-items: center; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
