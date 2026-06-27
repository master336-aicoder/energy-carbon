<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span class="card-title">操作日志</span>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="操作人">
          <el-input v-model="searchForm.operName" placeholder="请输入操作人" clearable />
        </el-form-item>
        <el-form-item label="操作模块">
          <el-input v-model="searchForm.title" placeholder="请输入操作模块" clearable />
        </el-form-item>
        <el-form-item label="业务类型">
          <el-select v-model="searchForm.businessType" placeholder="请选择" clearable style="width: 120px">
            <el-option label="其他" :value="0" />
            <el-option label="新增" :value="1" />
            <el-option label="修改" :value="2" />
            <el-option label="删除" :value="3" />
            <el-option label="查询" :value="4" />
            <el-option label="导出" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="operName" label="操作人" width="120" align="center" />
        <el-table-column prop="title" label="操作模块" width="150" align="center" />
        <el-table-column prop="businessType" label="业务类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="getBusinessTypeType(row.businessType)">
              {{ getBusinessTypeText(row.businessType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="requestMethod" label="请求方式" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="getMethodType(row.requestMethod)">{{ row.requestMethod }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operIp" label="IP地址" width="130" align="center" />
        <el-table-column prop="browser" label="浏览器" width="100" align="center" />
        <el-table-column prop="os" label="操作系统" width="120" align="center" />
        <el-table-column prop="referer" label="来源页面" min-width="150" show-overflow-tooltip align="center" />
        <el-table-column prop="operTime" label="操作时间" width="170" align="center" />
        <el-table-column prop="status" label="操作结果" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="small">
              {{ row.status === 0 ? '成功' : '异常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="costTime" label="耗时(ms)" width="100" align="center">
          <template #default="{ row }">
            <span :style="{ color: getCostTimeColor(row.costTime) }">{{ row.costTime || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="operUrl" label="请求URL" min-width="200" show-overflow-tooltip />
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="page.current"
        v-model:page-size="page.size"
        :total="page.total"
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
import { getLogList } from '@/api/log'
import type { LogApi } from '@/types/api'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref<LogApi.LogItem[]>([])
const dateRange = ref<string[]>([])

const searchForm = reactive({
  operName: '',
  title: '',
  businessType: undefined as number | undefined,
})

const page = reactive({
  current: 1,
  size: 10,
  total: 0,
})

/** 获取日志列表 */
async function fetchData() {
  loading.value = true
  try {
    const params: LogApi.ListParams = {
      current: page.current,
      size: page.size,
      operName: searchForm.operName || undefined,
      title: searchForm.title || undefined,
      businessType: searchForm.businessType,
      startTime: dateRange.value && dateRange.value.length > 0 ? dateRange.value[0] : undefined,
      endTime: dateRange.value && dateRange.value.length > 1 ? dateRange.value[1] : undefined,
    }
    const res = await getLogList(params)
    if (res.code === 200 && res.data) {
      tableData.value = res.data.records || []
      page.total = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取日志失败')
    }
  } catch (error) {
    ElMessage.error('获取日志列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.current = 1
  fetchData()
}

function resetSearch() {
  searchForm.operName = ''
  searchForm.title = ''
  searchForm.businessType = undefined
  dateRange.value = []
  page.current = 1
  fetchData()
}

function handleSizeChange(val: number) {
  page.size = val
  page.current = 1
  fetchData()
}

function handleCurrentChange(val: number) {
  page.current = val
  fetchData()
}

function getBusinessTypeText(type?: number) {
  const map: Record<number, string> = {
    0: '其他',
    1: '新增',
    2: '修改',
    3: '删除',
    4: '查询',
    5: '导出',
  }
  return type !== undefined ? map[type] || '其他' : '其他'
}

function getBusinessTypeType(type?: number): '' | 'success' | 'warning' | 'danger' | 'info' {
  const map: Record<number, '' | 'success' | 'warning' | 'danger' | 'info'> = {
    0: 'info',
    1: 'success',
    2: 'warning',
    3: 'danger',
    4: '',
    5: 'info',
  }
  return type !== undefined ? map[type] || 'info' : 'info'
}

function getMethodType(method?: string): '' | 'success' | 'warning' | 'danger' | 'info' {
  const map: Record<string, '' | 'success' | 'warning' | 'danger' | 'info'> = {
    GET: 'success',
    POST: 'warning',
    PUT: 'info',
    DELETE: 'danger',
  }
  return method ? map[method.toUpperCase()] || '' : ''
}

function getCostTimeColor(costTime?: number) {
  if (!costTime) return '#67C23A'
  if (costTime > 3000) return '#F56C6C'
  if (costTime > 1000) return '#E6A23C'
  return '#67C23A'
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.page-container {
  padding: 10px;
}

.card-title {
  font-size: 16px;
  font-weight: bold;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
