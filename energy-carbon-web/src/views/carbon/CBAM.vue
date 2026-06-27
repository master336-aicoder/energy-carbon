<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span class="card-title">CBAM 碳边境调节机制申报</span>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="申报编号">
          <el-input v-model="searchForm.declarationNo" placeholder="请输入申报编号" clearable />
        </el-form-item>
        <el-form-item label="申报年份">
          <el-input v-model="searchForm.declarationYear" placeholder="请输入年份" clearable />
        </el-form-item>
        <el-form-item label="申报状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="草稿" :value="0" />
            <el-option label="已提交" :value="1" />
            <el-option label="已通过" :value="2" />
            <el-option label="已驳回" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="declarationNo" label="申报编号" width="150" align="center" />
        <el-table-column prop="declarationYear" label="申报年份" width="100" align="center" />
        <el-table-column prop="importerName" label="进口商名称" min-width="180" align="center" />
        <el-table-column prop="importCountry" label="进口国" width="120" align="center" />
        <el-table-column prop="totalEmbodiedEmissions" label="隐含排放总量(tCO₂)" width="160" align="center" />
        <el-table-column prop="submissionDeadline" label="申报截止日期" width="140" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" align="center" />
      </el-table>

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
import { getCbamList } from '@/api/carbon'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref<any[]>([])

const searchForm = reactive({
  declarationNo: '',
  declarationYear: '',
  status: undefined as number | undefined,
})

const page = reactive({
  current: 1,
  size: 10,
  total: 0,
})

async function fetchData() {
  loading.value = true
  try {
    const res = await getCbamList({
      current: page.current,
      size: page.size,
      ...searchForm,
    })
    if (res.code === 200 && res.data) {
      tableData.value = res.data.records || []
      page.total = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取CBAM列表失败')
    }
  } catch (error) {
    ElMessage.error('获取CBAM列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

function handleSearch() { page.current = 1; fetchData() }
function resetSearch() {
  searchForm.declarationNo = ''
  searchForm.declarationYear = ''
  searchForm.status = undefined
  page.current = 1
  fetchData()
}
function handleSizeChange(val: number) { page.size = val; page.current = 1; fetchData() }
function handleCurrentChange(val: number) { page.current = val; fetchData() }

function getStatusText(status?: number) {
  const map: Record<number, string> = { 0: '草稿', 1: '已提交', 2: '已通过', 3: '已驳回' }
  return status !== undefined ? map[status] || '未知' : '未知'
}
function getStatusType(status?: number): '' | 'success' | 'warning' | 'danger' | 'info' {
  const map: Record<number, '' | 'success' | 'warning' | 'danger' | 'info'> = {
    0: 'info', 1: 'warning', 2: 'success', 3: 'danger',
  }
  return status !== undefined ? map[status] || 'info' : 'info'
}

onMounted(() => { fetchData() })
</script>

<style scoped lang="scss">
.page-container { padding: 10px; }
.card-title { font-size: 16px; font-weight: bold; }
.search-form { margin-bottom: 20px; }
.pagination { margin-top: 20px; justify-content: flex-end; }
</style>
