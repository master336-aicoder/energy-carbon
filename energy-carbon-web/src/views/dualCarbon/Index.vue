<template>
  <div class="ai-platform">
    <div class="page-header">
      <h2>AI智慧管理平台</h2>
      <p>智能能碳服务预约与管理</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card">
        <div class="stat-value">{{ stats.total }}</div>
        <div class="stat-label">总预约数</div>
      </div>
      <div class="stat-card pending">
        <div class="stat-value">{{ stats.pending }}</div>
        <div class="stat-label">待处理</div>
      </div>
      <div class="stat-card processing">
        <div class="stat-value">{{ stats.processing }}</div>
        <div class="stat-label">进行中</div>
      </div>
      <div class="stat-card completed">
        <div class="stat-value">{{ stats.completed }}</div>
        <div class="stat-label">已完成</div>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-select v-model="filters.status" placeholder="状态筛选" clearable style="width: 150px" @change="loadData">
        <el-option label="待处理" value="PENDING" />
        <el-option label="已批准" value="APPROVED" />
        <el-option label="进行中" value="PROCESSING" />
        <el-option label="已完成" value="COMPLETED" />
        <el-option label="已拒绝" value="REJECTED" />
      </el-select>
      <el-select v-model="filters.serviceType" placeholder="服务类型" clearable style="width: 150px" @change="loadData">
        <el-option label="碳核算咨询" value="CARBON_CONSULTING" />
        <el-option label="绿色工厂评估" value="GREEN_FACTORY" />
        <el-option label="能源审计" value="ENERGY_AUDIT" />
        <el-option label="碳足迹认证" value="FOOTPRINT_CERT" />
        <el-option label="节能诊断" value="ENERGY_DIAGNOSIS" />
      </el-select>
      <el-button type="primary" @click="showAddDialog">新增预约</el-button>
    </div>

    <!-- 预约列表 -->
    <el-table :data="tableData" border stripe style="width: 100%">
      <el-table-column prop="serviceName" label="服务名称" min-width="160" />
      <el-table-column prop="serviceType" label="服务类型" width="140">
        <template #default="{ row }">
          <el-tag :type="getServiceTypeTag(row.serviceType)" size="small">{{ getServiceTypeName(row.serviceType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="applicant" label="申请人" width="100" />
      <el-table-column prop="contact" label="联系方式" width="130" />
      <el-table-column prop="expectedDate" label="期望日期" width="110" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusTag(row.status)" size="small">{{ getStatusName(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="viewDetail(row)">详情</el-button>
          <el-button v-if="row.status === 'PENDING'" link type="warning" size="small" @click="cancelRecord(row)">取消</el-button>
          <el-button link type="danger" size="small" @click="deleteRecord(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
      />
    </div>

    <!-- 新增预约弹窗 -->
    <el-dialog v-model="addDialogVisible" title="新增AI服务预约" width="600px" destroy-on-close>
      <el-form :model="addForm" :rules="addRules" ref="addFormRef" label-width="100px">
        <el-form-item label="服务名称" prop="serviceName">
          <el-input v-model="addForm.serviceName" placeholder="请输入服务名称" />
        </el-form-item>
        <el-form-item label="服务类型" prop="serviceType">
          <el-select v-model="addForm.serviceType" placeholder="请选择服务类型" style="width: 100%">
            <el-option label="碳核算咨询" value="CARBON_CONSULTING" />
            <el-option label="绿色工厂评估" value="GREEN_FACTORY" />
            <el-option label="能源审计" value="ENERGY_AUDIT" />
            <el-option label="碳足迹认证" value="FOOTPRINT_CERT" />
            <el-option label="节能诊断" value="ENERGY_DIAGNOSIS" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请人" prop="applicant">
          <el-input v-model="addForm.applicant" placeholder="请输入申请人" />
        </el-form-item>
        <el-form-item label="联系方式" prop="contact">
          <el-input v-model="addForm.contact" placeholder="请输入联系方式" />
        </el-form-item>
        <el-form-item label="期望日期" prop="expectedDate">
          <el-date-picker v-model="addForm.expectedDate" type="date" placeholder="选择期望日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="服务描述">
          <el-input v-model="addForm.description" type="textarea" :rows="3" placeholder="请描述服务需求" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="addForm.remark" type="textarea" :rows="2" placeholder="备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="预约详情" width="600px">
      <el-descriptions :column="2" border v-if="currentRecord">
        <el-descriptions-item label="服务名称">{{ currentRecord.serviceName }}</el-descriptions-item>
        <el-descriptions-item label="服务类型">{{ getServiceTypeName(currentRecord.serviceType) }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ currentRecord.applicant }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ currentRecord.contact }}</el-descriptions-item>
        <el-descriptions-item label="期望日期">{{ currentRecord.expectedDate }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTag(currentRecord.status)">{{ getStatusName(currentRecord.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="服务描述" :span="2">{{ currentRecord.description || '-' }}</el-descriptions-item>
        <el-descriptions-item label="服务结果" :span="2">{{ currentRecord.result || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentRecord.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ currentRecord.updateTime || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getAiServiceList, addAiService, cancelAiService, deleteAiService, type AiServiceRecord } from '@/api/ai'

const tableData = ref<AiServiceRecord[]>([])
const addDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const addFormRef = ref<FormInstance>()
const currentRecord = ref<AiServiceRecord | null>(null)

const stats = reactive({ total: 0, pending: 0, processing: 0, completed: 0 })
const filters = reactive({ status: '', serviceType: '' })
const pagination = reactive({ current: 1, size: 10, total: 0 })

const addForm = reactive({
  serviceName: '',
  serviceType: '',
  applicant: '',
  contact: '',
  expectedDate: '',
  description: '',
  remark: ''
})

const addRules = {
  serviceName: [{ required: true, message: '请输入服务名称', trigger: 'blur' }],
  serviceType: [{ required: true, message: '请选择服务类型', trigger: 'change' }],
  applicant: [{ required: true, message: '请输入申请人', trigger: 'blur' }],
  contact: [{ required: true, message: '请输入联系方式', trigger: 'blur' }]
}

const statusMap: Record<string, string> = { PENDING: '待处理', APPROVED: '已批准', PROCESSING: '进行中', COMPLETED: '已完成', REJECTED: '已拒绝' }
const serviceTypeMap: Record<string, string> = {
  CARBON_CONSULTING: '碳核算咨询', GREEN_FACTORY: '绿色工厂评估',
  ENERGY_AUDIT: '能源审计', FOOTPRINT_CERT: '碳足迹认证', ENERGY_DIAGNOSIS: '节能诊断'
}

function getStatusName(status: string) { return statusMap[status] || status }
function getServiceTypeName(type: string) { return serviceTypeMap[type] || type }
function getStatusTag(status: string) {
  const map: Record<string, string> = { PENDING: 'warning', APPROVED: 'success', PROCESSING: 'primary', COMPLETED: 'info', REJECTED: 'danger' }
  return map[status] || 'info'
}
function getServiceTypeTag(type: string) {
  const map: Record<string, string> = { CARBON_CONSULTING: 'primary', GREEN_FACTORY: 'success', ENERGY_AUDIT: 'warning', FOOTPRINT_CERT: 'danger', ENERGY_DIAGNOSIS: 'info' }
  return map[type] || 'info'
}

async function loadData() {
  try {
    const res: any = await getAiServiceList({ current: pagination.current, size: pagination.size, status: filters.status || undefined, serviceType: filters.serviceType || undefined })
    const list = res.data?.records || []
    const total = res.data?.total || 0
    tableData.value = list
    pagination.total = total

    // 更新统计
    stats.total = total
    stats.pending = list.filter((r: AiServiceRecord) => r.status === 'PENDING').length
    stats.processing = list.filter((r: AiServiceRecord) => r.status === 'PROCESSING').length
    stats.completed = list.filter((r: AiServiceRecord) => r.status === 'COMPLETED').length
  } catch (error) {
    tableData.value = []
    pagination.total = 0
    stats.total = 0
    stats.pending = 0
    stats.processing = 0
    stats.completed = 0
  }
}

function showAddDialog() {
  Object.assign(addForm, { serviceName: '', serviceType: '', applicant: '', contact: '', expectedDate: '', description: '', remark: '' })
  addDialogVisible.value = true
}

async function submitAdd() {
  await addFormRef.value?.validate()
  await addAiService(addForm)
  ElMessage.success('预约成功')
  addDialogVisible.value = false
  loadData()
}

function viewDetail(row: AiServiceRecord) {
  currentRecord.value = row
  detailDialogVisible.value = true
}

async function cancelRecord(row: AiServiceRecord) {
  await ElMessageBox.confirm('确定要取消该预约吗？', '提示', { type: 'warning' })
  await cancelAiService(row.id!)
  ElMessage.success('已取消')
  loadData()
}

async function deleteRecord(row: AiServiceRecord) {
  await ElMessageBox.confirm('确定要删除该记录吗？', '提示', { type: 'warning' })
  await deleteAiService(row.id!)
  ElMessage.success('已删除')
  loadData()
}

onMounted(() => { loadData() })
</script>

<style scoped>
.ai-platform { padding: 20px; }
.page-header { margin-bottom: 24px; }
.page-header h2 { margin: 0 0 8px; font-size: 24px; }
.page-header p { margin: 0; color: #909399; }
.stat-cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
.stat-card { background: #fff; border-radius: 8px; padding: 20px; text-align: center; box-shadow: 0 2px 12px rgba(0,0,0,0.06); }
.stat-card.pending { border-left: 4px solid #e6a23c; }
.stat-card.processing { border-left: 4px solid #409eff; }
.stat-card.completed { border-left: 4px solid #67c23a; }
.stat-value { font-size: 32px; font-weight: bold; color: #303133; }
.stat-label { font-size: 14px; color: #909399; margin-top: 8px; }
.filter-bar { display: flex; gap: 12px; margin-bottom: 16px; align-items: center; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
