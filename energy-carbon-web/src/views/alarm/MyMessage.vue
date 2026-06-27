<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">我的消息</span>
          <el-button type="primary" @click="handleMarkAllRead">全部已读</el-button>
        </div>
      </template>
      <el-form :inline="true" class="search-form">
        <el-form-item label="消息标题">
          <el-input v-model="searchForm.title" placeholder="请输入消息标题" clearable />
        </el-form-item>
        <el-form-item label="阅读状态">
          <el-select v-model="searchForm.readStatus" placeholder="请选择" clearable style="width: 140px">
            <el-option label="未读" :value="0" />
            <el-option label="已读" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="title" label="消息标题" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <span :class="{ 'unread-title': row.readStatus === 0 }">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="noticeType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.noticeType === 1 ? 'primary' : 'warning'" size="small">
              {{ row.noticeType === 1 ? '通知' : '公告' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="readStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.readStatus === 1 ? 'success' : 'danger'" size="small">
              {{ row.readStatus === 1 ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="接收时间" width="180" />
        <el-table-column prop="readTime" label="阅读时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
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

    <NoticeDetailDialog
      v-model="detailVisible"
      :itemId="detailId"
      mode="message"
      :messageList="tableData"
      @read="handleMessageRead"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMessageList, markMessageRead } from '@/api/message'
import NoticeDetailDialog from '@/components/NoticeDetailDialog.vue'

const loading = ref(false)
const tableData = ref([])
const searchForm = reactive({ title: '', readStatus: undefined as number | undefined })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const detailVisible = ref(false)
const detailId = ref<number | undefined>(undefined)

async function fetchList() {
  loading.value = true
  try {
    const res = await getMessageList({
      current: pagination.pageNum, size: pagination.pageSize,
      title: searchForm.title, readStatus: searchForm.readStatus,
    })
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

function handleSearch() { pagination.pageNum = 1; fetchList() }
function handleReset() { searchForm.title = ''; searchForm.readStatus = undefined; handleSearch() }
function handleSizeChange(val: number) { pagination.pageSize = val; fetchList() }
function handleCurrentChange(val: number) { pagination.pageNum = val; fetchList() }

function handleView(row: any) {
  detailId.value = row.id
  detailVisible.value = true
}

function handleMessageRead(messageId: number) {
  const idx = tableData.value.findIndex((m: any) => m.id === messageId)
  if (idx >= 0) {
    tableData.value[idx] = { ...tableData.value[idx], readStatus: 1, readTime: new Date().toLocaleString() }
  }
}

async function handleMarkAllRead() {
  try {
    for (const msg of tableData.value) {
      if ((msg as any).readStatus === 0) {
        await markMessageRead((msg as any).id).catch(() => {})
      }
    }
    ElMessage.success('已全部标记为已读')
    fetchList()
  } catch (e) { console.error(e) }
}

onMounted(fetchList)
</script>

<style scoped lang="scss">
.page-container { padding: 10px; }
.search-form { margin-bottom: 20px; }
.pagination { margin-top: 20px; justify-content: flex-end; }
.unread-title { font-weight: bold; color: #303133; }
</style>
