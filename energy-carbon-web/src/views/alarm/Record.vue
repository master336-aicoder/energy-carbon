<template>
  <div class="alarm-record">
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="6">
        <el-card shadow="never">
          <div class="stat-item">
            <div class="stat-label">报警总数</div>
            <div class="stat-value">{{ stats.total }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="stat-item">
            <div class="stat-label">未处理</div>
            <div class="stat-value" style="color: #f56c6c">{{ stats.unhandled }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="stat-item">
            <div class="stat-label">严重报警</div>
            <div class="stat-value" style="color: #e6a23c">{{ stats.critical }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="stat-item">
            <div class="stat-label">警告报警</div>
            <div class="stat-value" style="color: #409eff">{{ stats.warn }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>报警记录列表</span>
          <div>
            <el-button type="primary" @click="handleBatchHandle" :disabled="selectedIds.length === 0">批量处理</el-button>
          </div>
        </div>
      </template>

      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="报警级别">
          <el-select v-model="searchForm.alarmLevel" placeholder="请选择报警级别" clearable style="width: 130px">
            <el-option label="一般" value="INFO" />
            <el-option label="警告" value="WARN" />
            <el-option label="严重" value="CRITICAL" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select v-model="searchForm.handleStatus" placeholder="请选择处理状态" clearable style="width: 130px">
            <el-option label="未处理" value="UNHANDLED" />
            <el-option label="已处理" value="HANDLED" />
            <el-option label="已忽略" value="IGNORED" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="searchForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 190px"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="searchForm.endTime"
            type="datetime"
            placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 190px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table
        :data="tableData"
        v-loading="loading"
        border stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="alarmTime" label="报警时间" width="180" />
        <el-table-column prop="alarmType" label="报警类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.alarmType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="alarmLevel" label="级别" width="80">
          <template #default="{ row }">
            <el-tag
              :type="row.alarmLevel === 'CRITICAL' ? 'danger' : row.alarmLevel === 'WARN' ? 'warning' : 'info'"
              size="small"
            >
              {{ levelMap[row.alarmLevel] || row.alarmLevel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="thresholdName" label="阈值" width="120" />
        <el-table-column prop="alarmValue" label="实际值" width="100" />
        <el-table-column prop="thresholdValue" label="阈值设定值" width="110" />
        <el-table-column prop="meterName" label="关联器具" width="120" />
        <el-table-column prop="handleStatus" label="处理状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="row.handleStatus === 'HANDLED' ? 'success' : row.handleStatus === 'IGNORED' ? 'info' : 'danger'"
              size="small"
            >
              {{ statusMap[row.handleStatus] || row.handleStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handler" label="处理人" width="80">
          <template #default="{ row }">{{ row.handler || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <template v-if="row.handleStatus === 'UNHANDLED'">
              <el-button type="success" link @click="handleProcess(row)">处理</el-button>
              <el-button type="info" link @click="handleIgnore(row)">忽略</el-button>
            </template>
            <span v-else style="color: #999">已处理</span>
            <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 处理弹窗 -->
    <el-dialog v-model="processVisible" title="处理报警" width="500px">
      <el-form ref="processFormRef" :model="processForm" :rules="processRules" label-width="100px">
        <el-form-item label="处理方式" prop="handleStatus">
          <el-radio-group v-model="processForm.handleStatus">
            <el-radio label="HANDLED">已处理</el-radio>
            <el-radio label="IGNORED">已忽略</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理人">
          <el-input v-model="processForm.handler" placeholder="请输入处理人" />
        </el-form-item>
        <el-form-item label="处理备注">
          <el-input v-model="processForm.handleRemark" type="textarea" :rows="3" placeholder="请输入处理备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="processVisible = false">取消</el-button>
        <el-button type="primary" @click="handleProcessSubmit" :loading="processLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="报警详情" width="600px">
      <el-descriptions :column="2" border v-if="detailRow">
        <el-descriptions-item label="报警类型">{{ detailRow.alarmType }}</el-descriptions-item>
        <el-descriptions-item label="报警级别">
          <el-tag
            :type="detailRow.alarmLevel === 'CRITICAL' ? 'danger' : detailRow.alarmLevel === 'WARN' ? 'warning' : 'info'"
          >
            {{ levelMap[detailRow.alarmLevel] || detailRow.alarmLevel }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="报警时间">{{ detailRow.alarmTime }}</el-descriptions-item>
        <el-descriptions-item label="关联器具">{{ detailRow.meterName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="实际值">{{ detailRow.alarmValue }}</el-descriptions-item>
        <el-descriptions-item label="阈值设定值">{{ detailRow.thresholdValue }}</el-descriptions-item>
        <el-descriptions-item label="阈值名称">{{ detailRow.thresholdName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag
            :type="detailRow.handleStatus === 'HANDLED' ? 'success' : detailRow.handleStatus === 'IGNORED' ? 'info' : 'danger'"
          >
            {{ statusMap[detailRow.handleStatus] || detailRow.handleStatus }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处理人">{{ detailRow.handler || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ detailRow.handleTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理备注" :span="2">{{ detailRow.handleRemark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getAlarmRecordList, handleAlarmRecord, batchHandleAlarmRecord, getAlarmStatistics } from '@/api/alarm'
import type { AlarmRecord, AlarmStatistics } from '@/types'

const loading = ref(false)
const processLoading = ref(false)
const processVisible = ref(false)
const detailVisible = ref(false)
const processFormRef = ref<FormInstance>()
const tableData = ref<AlarmRecord[]>([])
const selectedIds = ref<number[]>([])
const detailRow = ref<AlarmRecord | null>(null)

const levelMap: Record<string, string> = {
  INFO: '一般',
  WARN: '警告',
  CRITICAL: '严重'
}

const statusMap: Record<string, string> = {
  UNHANDLED: '未处理',
  HANDLED: '已处理',
  IGNORED: '已忽略'
}

const stats = reactive<AlarmStatistics>({
  total: 0,
  unhandled: 0,
  handled: 0,
  ignored: 0,
  critical: 0,
  warn: 0,
  info: 0
})

const searchForm = reactive({
  alarmLevel: '',
  handleStatus: '',
  alarmType: '',
  startTime: '',
  endTime: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const processForm = reactive({
  handleStatus: 'HANDLED',
  handler: '',
  handleRemark: '',
  id: 0
})

const processRules = {
  handleStatus: [{ required: true, message: '请选择处理方式', trigger: 'change' }]
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getAlarmRecordList({
      current: pagination.pageNum,
      size: pagination.pageSize,
      alarmLevel: searchForm.alarmLevel || undefined,
      handleStatus: searchForm.handleStatus || undefined,
      alarmType: searchForm.alarmType || undefined,
      startTime: searchForm.startTime || undefined,
      endTime: searchForm.endTime || undefined
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

async function fetchStats() {
  try {
    const res = await getAlarmStatistics()
    Object.assign(stats, res.data)
  } catch {
    // ignore
  }
}

function handleSearch() {
  pagination.pageNum = 1
  fetchList()
}

function handleReset() {
  searchForm.alarmLevel = ''
  searchForm.handleStatus = ''
  searchForm.alarmType = ''
  searchForm.startTime = ''
  searchForm.endTime = ''
  pagination.pageNum = 1
  fetchList()
}

function handleSizeChange(val: number) {
  pagination.pageSize = val
  fetchList()
}

function handleCurrentChange(val: number) {
  pagination.pageNum = val
  fetchList()
}

function handleSelectionChange(rows: AlarmRecord[]) {
  selectedIds.value = rows.map(r => r.id!).filter(Boolean)
}

function handleProcess(row: AlarmRecord) {
  processForm.id = row.id!
  processForm.handleStatus = 'HANDLED'
  processForm.handler = ''
  processForm.handleRemark = ''
  processVisible.value = true
}

function handleIgnore(row: AlarmRecord) {
  processForm.id = row.id!
  processForm.handleStatus = 'IGNORED'
  processForm.handler = ''
  processForm.handleRemark = ''
  processVisible.value = true
}

async function handleProcessSubmit() {
  const valid = await processFormRef.value?.validate().catch(() => false)
  if (!valid) return
  processLoading.value = true
  try {
    if (processForm.id === 0) {
      // 批量处理
      await batchHandleAlarmRecord({
        ids: selectedIds.value,
        handleStatus: processForm.handleStatus,
        handler: processForm.handler || undefined,
        handleRemark: processForm.handleRemark || undefined
      })
      ElMessage.success('批量处理成功')
      selectedIds.value = []
    } else {
      // 单条处理
      await handleAlarmRecord(processForm.id, {
        handleStatus: processForm.handleStatus,
        handler: processForm.handler || undefined,
        handleRemark: processForm.handleRemark || undefined
      })
      ElMessage.success('处理成功')
    }
    processVisible.value = false
    fetchList()
    fetchStats()
  } finally {
    processLoading.value = false
  }
}

function handleBatchHandle() {
  if (selectedIds.value.length === 0) return
  processForm.id = 0
  processForm.handleStatus = 'HANDLED'
  processForm.handler = ''
  processForm.handleRemark = ''
  processVisible.value = true
}

function handleDetail(row: AlarmRecord) {
  detailRow.value = row
  detailVisible.value = true
}

onMounted(() => {
  fetchList()
  fetchStats()
})
</script>

<style scoped lang="scss">
.alarm-record {
  padding: 20px;
}

.stat-item {
  text-align: center;
  padding: 10px 0;

  .stat-label {
    font-size: 14px;
    color: #909399;
    margin-bottom: 8px;
  }

  .stat-value {
    font-size: 28px;
    font-weight: bold;
    color: #303133;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>