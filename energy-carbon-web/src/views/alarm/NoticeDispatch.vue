<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">通知下发记录</span>
        </div>
      </template>
      <el-form :inline="true" class="search-form">
        <el-form-item label="公告标题">
          <el-input v-model="searchForm.noticeTitle" placeholder="请输入公告标题" clearable />
        </el-form-item>
        <el-form-item label="下发类型">
          <el-select v-model="searchForm.dispatchType" placeholder="请选择" clearable style="width: 140px">
            <el-option label="全部" :value="1" />
            <el-option label="机构" :value="2" />
            <el-option label="角色" :value="3" />
            <el-option label="用户" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="noticeTitle" label="公告标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="dispatchType" label="下发类型" width="100">
          <template #default="{ row }">
            <el-tag :type="dispatchTypeMap[row.dispatchType]?.type">{{ dispatchTypeMap[row.dispatchType]?.label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetName" label="下发范围" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createBy" label="操作人" width="100" />
        <el-table-column prop="createTime" label="下发时间" width="180" />
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
import { getDispatchList } from '@/api/notice'

const dispatchTypeMap: Record<number, { label: string; type: string }> = {
  1: { label: '全部', type: 'primary' },
  2: { label: '机构', type: 'success' },
  3: { label: '角色', type: 'warning' },
  4: { label: '用户', type: 'info' },
}

const loading = ref(false)
const tableData = ref([])
const searchForm = reactive({ noticeTitle: '', dispatchType: undefined as number | undefined })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

async function fetchList() {
  loading.value = true
  try {
    const res = await getDispatchList({
      current: pagination.pageNum, size: pagination.pageSize,
      noticeTitle: searchForm.noticeTitle, dispatchType: searchForm.dispatchType,
    })
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

function handleSearch() { pagination.pageNum = 1; fetchList() }
function handleReset() { searchForm.noticeTitle = ''; searchForm.dispatchType = undefined; handleSearch() }
function handleSizeChange(val: number) { pagination.pageSize = val; fetchList() }
function handleCurrentChange(val: number) { pagination.pageNum = val; fetchList() }

onMounted(fetchList)
</script>

<style scoped lang="scss">
.page-container { padding: 10px; }
.search-form { margin-bottom: 20px; }
.pagination { margin-top: 20px; justify-content: flex-end; }
</style>
