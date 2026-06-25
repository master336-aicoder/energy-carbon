<template>
  <div class="ai-report-page">
    <div class="page-header">
      <h2>AI报告生成</h2>
      <p>基于模板的智能报告生成与管理</p>
    </div>

    <el-tabs v-model="activeTab">
      <!-- 创建报告 -->
      <el-tab-pane label="创建报告" name="create">
        <div class="report-create">
          <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="120px" style="max-width: 600px">
            <el-form-item label="报告名称" prop="reportName">
              <el-input v-model="createForm.reportName" placeholder="请输入报告名称" />
            </el-form-item>
            <el-form-item label="报告类型" prop="reportType">
              <el-select v-model="createForm.reportType" placeholder="请选择报告类型" style="width: 100%" @change="loadTemplates">
                <el-option label="绿色工厂评价报告" value="GREEN_FACTORY" />
                <el-option label="碳足迹报告" value="CARBON_FOOTPRINT" />
                <el-option label="核查报告" value="VERIFICATION" />
                <el-option label="能源审计报告" value="ENERGY_AUDIT" />
              </el-select>
            </el-form-item>
            <el-form-item label="选择模板" prop="templateId">
              <el-select v-model="createForm.templateId" placeholder="请选择模板" style="width: 100%">
                <el-option v-for="t in templates" :key="t.id" :label="t.templateName" :value="t.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="报告参数">
              <el-input v-model="createForm.paramsJson" type="textarea" :rows="6" placeholder='请输入JSON格式参数，如: {"year": "2024", "enterprise": "XX公司"}' />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="createReport">生成报告</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>

      <!-- 任务列表 -->
      <el-tab-pane label="任务列表" name="list">
        <div class="filter-bar">
          <el-select v-model="listFilter.reportType" placeholder="报告类型" clearable style="width: 150px" @change="loadTasks">
            <el-option label="绿色工厂评价报告" value="GREEN_FACTORY" />
            <el-option label="碳足迹报告" value="CARBON_FOOTPRINT" />
            <el-option label="核查报告" value="VERIFICATION" />
            <el-option label="能源审计报告" value="ENERGY_AUDIT" />
          </el-select>
          <el-select v-model="listFilter.status" placeholder="状态" clearable style="width: 150px" @change="loadTasks">
            <el-option label="待处理" value="PENDING" />
            <el-option label="生成中" value="GENERATING" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="失败" value="FAILED" />
          </el-select>
        </div>

        <el-table :data="taskData" border stripe style="width: 100%">
          <el-table-column prop="reportName" label="报告名称" min-width="180" />
          <el-table-column prop="reportType" label="报告类型" width="150">
            <template #default="{ row }">
              <el-tag size="small">{{ reportTypeMap[row.reportType] || row.reportType }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getTaskStatusTag(row.status)" size="small">{{ getTaskStatusName(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="progress" label="进度" width="150">
            <template #default="{ row }">
              <el-progress :percentage="row.progress || 0" :status="row.status === 'COMPLETED' ? 'success' : row.status === 'FAILED' ? 'exception' : undefined" :stroke-width="14" :text-inside="true" />
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="170" />
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="{ row }">
              <el-button v-if="row.status === 'COMPLETED'" link type="success" size="small" @click="downloadReport(row)">下载</el-button>
              <el-button v-if="row.status === 'PENDING'" link type="warning" size="small" @click="cancelTask(row)">取消</el-button>
              <el-button link type="danger" size="small" @click="deleteTask(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination v-model:current-page="taskPagination.current" v-model:page-size="taskPagination.size" :total="taskPagination.total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @size-change="loadTasks" @current-change="loadTasks" />
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { createAiReport, getAiReportList, cancelAiReport, deleteAiReport, getAllAiReportTemplates, type AiReportTask, type AiReportTemplate } from '@/api/ai'

const activeTab = ref('create')
const templates = ref<AiReportTemplate[]>([])
const taskData = ref<AiReportTask[]>([])
const createFormRef = ref<FormInstance>()

const createForm = reactive({ reportName: '', reportType: '', templateId: undefined as number | undefined, paramsJson: '' })
const createRules = {
  reportName: [{ required: true, message: '请输入报告名称', trigger: 'blur' }],
  reportType: [{ required: true, message: '请选择报告类型', trigger: 'change' }]
}

const listFilter = reactive({ reportType: '', status: '' })
const taskPagination = reactive({ current: 1, size: 10, total: 0 })

const reportTypeMap: Record<string, string> = { GREEN_FACTORY: '绿色工厂评价报告', CARBON_FOOTPRINT: '碳足迹报告', VERIFICATION: '核查报告', ENERGY_AUDIT: '能源审计报告' }
const taskStatusMap: Record<string, string> = { PENDING: '待处理', GENERATING: '生成中', COMPLETED: '已完成', FAILED: '失败' }

function getTaskStatusName(status: string) { return taskStatusMap[status] || status }
function getTaskStatusTag(status: string) {
  const map: Record<string, string> = { PENDING: 'warning', GENERATING: 'primary', COMPLETED: 'success', FAILED: 'danger' }
  return map[status] || 'info'
}

async function loadTemplates() {
  const res = await getAllAiReportTemplates(createForm.reportType)
  templates.value = res.data
}

async function createReport() {
  await createFormRef.value?.validate()
  await createAiReport(createForm)
  ElMessage.success('报告任务已创建，请到任务列表查看进度')
  activeTab.value = 'list'
  loadTasks()
}

async function loadTasks() {
  const res = await getAiReportList({ current: taskPagination.current, size: taskPagination.size, reportType: listFilter.reportType || undefined, status: listFilter.status || undefined })
  taskData.value = res.data.records
  taskPagination.total = res.data.total
}

function downloadReport(row: AiReportTask) {
  ElMessage.info('报告下载: ' + (row.resultPath || '文件路径未配置'))
}

async function cancelTask(row: AiReportTask) {
  await ElMessageBox.confirm('确定要取消该任务吗？', '提示', { type: 'warning' })
  await cancelAiReport(row.id!)
  ElMessage.success('已取消')
  loadTasks()
}

async function deleteTask(row: AiReportTask) {
  await ElMessageBox.confirm('确定要删除该任务吗？', '提示', { type: 'warning' })
  await deleteAiReport(row.id!)
  ElMessage.success('已删除')
  loadTasks()
}

onMounted(() => { loadTasks() })
</script>

<style scoped>
.ai-report-page { padding: 20px; }
.page-header { margin-bottom: 24px; }
.page-header h2 { margin: 0 0 8px; font-size: 24px; }
.page-header p { margin: 0; color: #909399; }
.report-create { background: #fff; border-radius: 8px; padding: 24px; box-shadow: 0 2px 12px rgba(0,0,0,0.06); }
.filter-bar { display: flex; gap: 12px; margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
