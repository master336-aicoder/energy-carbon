<template>
  <div class="status-monitor">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="never">
          <div class="stat-item">
            <div class="stat-label">网关总数</div>
            <div class="stat-value">{{ stats.total }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="stat-item">
            <div class="stat-label">在线设备</div>
            <div class="stat-value" style="color: #67c23a">{{ stats.online }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="stat-item">
            <div class="stat-label">离线设备</div>
            <div class="stat-value" style="color: #f56c6c">{{ stats.offline }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="stat-item">
            <div class="stat-label">在线率</div>
            <div class="stat-value" style="color: #409eff">{{ stats.onlineRate.toFixed(1) }}%</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>状态监测列表</span>
          <div>
            <el-select v-model="deviceType" placeholder="设备类型" clearable style="width: 130px; margin-right: 10px" @change="handleSearch">
              <el-option label="网关" value="GATEWAY" />
              <el-option label="计量器具" value="METER" />
            </el-select>
            <el-select v-model="onlineStatus" placeholder="在线状态" clearable style="width: 130px" @change="handleSearch">
              <el-option label="在线" :value="1" />
              <el-option label="离线" :value="0" />
            </el-select>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column prop="deviceId" label="设备ID" width="80" />
        <el-table-column prop="deviceName" label="设备名称" min-width="140">
          <template #default="{ row }">
            {{ row.deviceName || (row.deviceId ? '设备#' + row.deviceId : '-') }}
          </template>
        </el-table-column>
        <el-table-column prop="deviceType" label="设备类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.deviceType === 'GATEWAY' ? 'primary' : 'success'">
              {{ row.deviceType === 'GATEWAY' ? '网关' : '计量器具' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="onlineStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.onlineStatus === 1 ? 'success' : 'danger'">
              {{ row.onlineStatus === 1 ? '在线' : '离线' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="onlineTime" label="上线时间" width="180">
          <template #default="{ row }">{{ row.onlineTime || '-' }}</template>
        </el-table-column>
        <el-table-column prop="offlineTime" label="离线时间" width="180">
          <template #default="{ row }">{{ row.offlineTime || '-' }}</template>
        </el-table-column>
        <el-table-column prop="duration" label="持续时长" width="140">
          <template #default="{ row }">
            {{ row.duration ? formatDuration(row.duration) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="记录时间" width="180" />
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getStatusLogList, getOnlineRate } from '@/api/statusMonitor'
import type { StatusLog, OnlineRate } from '@/types'

const loading = ref(false)
const tableData = ref<StatusLog[]>([])
const deviceType = ref('')
const onlineStatus = ref<number | undefined>(undefined)
const stats = reactive<OnlineRate>({
  total: 0,
  online: 0,
  offline: 0,
  onlineRate: 0
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

function formatDuration(seconds: number): string {
  if (seconds < 60) return seconds + '秒'
  if (seconds < 3600) return Math.floor(seconds / 60) + '分钟'
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  return h + '小时' + m + '分钟'
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getStatusLogList({
      current: pagination.pageNum,
      size: pagination.pageSize,
      deviceType: deviceType.value || undefined,
      onlineStatus: onlineStatus.value
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

async function fetchStats() {
  try {
    const res = await getOnlineRate()
    Object.assign(stats, res.data)
  } catch {
    // ignore
  }
}

function handleSearch() {
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

onMounted(() => {
  fetchList()
  fetchStats()
})
</script>

<style scoped lang="scss">
.status-monitor {
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

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>