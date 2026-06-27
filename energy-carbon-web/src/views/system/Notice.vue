<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">{{ pageTitle }}</span>
          <el-button type="primary" @click="handleAdd">新增公告</el-button>
        </div>
      </template>
      <el-form :inline="true" class="search-form">
        <el-form-item label="公告标题">
          <el-input v-model="searchForm.noticeTitle" placeholder="请输入公告标题" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="noticeTitle" label="公告标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="noticeType" label="公告类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.noticeType === 1 ? '' : 'warning'">{{ row.noticeType === 1 ? '通知' : '公告' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="page.current"
        v-model:page-size="page.size"
        :total="page.total"
        layout="total, sizes, prev, pager, next"
        class="pagination"
        @size-change="handleSearch"
        @current-change="handleSearch"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="公告标题" prop="noticeTitle">
          <el-input v-model="form.noticeTitle" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="公告类型" prop="noticeType">
          <el-radio-group v-model="form.noticeType">
            <el-radio :value="1">通知</el-radio>
            <el-radio :value="2">公告</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="公告内容" prop="noticeContent">
          <div style="border: 1px solid #ccc; z-index: 2000;">
            <Toolbar
              style="border-bottom: 1px solid #ccc"
              :editor="editorRef"
              :defaultConfig="toolbarConfig"
              mode="default"
            />
            <Editor
              style="height: 300px; overflow-y: hidden;"
              v-model="form.noticeContent"
              :defaultConfig="editorConfig"
              mode="default"
              @onCreated="handleCreated"
            />
          </div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="公告详情" width="600px">
      <div class="notice-detail">
        <h3>{{ detail.noticeTitle }}</h3>
        <div class="notice-meta">
          <el-tag :type="detail.noticeType === 1 ? '' : 'warning'" size="small">{{ detail.noticeType === 1 ? '通知' : '公告' }}</el-tag>
          <span class="notice-time">{{ detail.createTime }}</span>
        </div>
        <div class="notice-content" v-html="detail.noticeContent"></div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, shallowRef, onBeforeUnmount } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getNoticeList, getNoticeDetail, createNotice, updateNotice, deleteNotice } from '@/api/notice'
import type { Notice } from '@/types'
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

const pageTitle = '通知公告'
const loading = ref(false)
const searchForm = reactive({ noticeTitle: '', status: undefined as number | undefined })
const tableData = ref<Notice[]>([])
const page = reactive({ current: 1, size: 10, total: 0 })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const isEdit = ref(false)

const form = reactive({
  id: undefined as number | undefined,
  noticeTitle: '',
  noticeType: 1 as number,
  noticeContent: '',
  status: 1 as number,
  remark: '',
})

const rules = {
  noticeTitle: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  noticeType: [{ required: true, message: '请选择公告类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
}

// wangEditor
const editorRef = shallowRef()
const toolbarConfig = {}
const editorConfig = { placeholder: '请输入公告内容...' }

function handleCreated(editor: any) {
  editorRef.value = editor
}

onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor) {
    editor.destroy()
  }
})

async function loadData() {
  loading.value = true
  try {
    const res = await getNoticeList({
      current: page.current,
      size: page.size,
      noticeTitle: searchForm.noticeTitle || undefined,
      status: searchForm.status,
    })
    if (res.code === 200 && res.data) {
      tableData.value = res.data.records || []
      page.total = res.data.total || 0
    }
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.current = 1
  loadData()
}

function resetSearch() {
  searchForm.noticeTitle = ''
  searchForm.status = undefined
  handleSearch()
}

function resetForm() {
  form.id = undefined
  form.noticeTitle = ''
  form.noticeType = 1
  form.noticeContent = ''
  form.status = 1
  form.remark = ''
}

function handleAdd() {
  resetForm()
  isEdit.value = false
  dialogTitle.value = '新增公告'
  dialogVisible.value = true
}

async function handleEdit(row: Notice) {
  resetForm()
  isEdit.value = true
  dialogTitle.value = '编辑公告'
  try {
    const res = await getNoticeDetail(row.id)
    if (res.code === 200 && res.data) {
      Object.assign(form, res.data)
      dialogVisible.value = true
    }
  } catch (e) {
    ElMessage.error('获取公告详情失败')
  }
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate()
  if (isEdit.value && form.id) {
    await updateNotice(form.id, { ...form })
    ElMessage.success('编辑成功')
  } else {
    await createNotice({ ...form })
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

function handleDelete(row: Notice) {
  ElMessageBox.confirm('确定删除该公告吗？', '提示', { type: 'warning' }).then(async () => {
    await deleteNotice(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const detailVisible = ref(false)
const detail = reactive<Partial<Notice>>({})

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.page-container { padding: 10px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 16px; font-weight: bold; }
.search-form { margin-bottom: 20px; }
.pagination { margin-top: 20px; justify-content: flex-end; }

.notice-detail {
  h3 {
    margin: 0 0 12px;
    font-size: 18px;
    color: #303133;
  }

  .notice-meta {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid #ebeef5;

    .notice-time {
      font-size: 13px;
      color: #909399;
    }
  }

  .notice-content {
    font-size: 14px;
    line-height: 1.8;
    color: #606266;
  }
}
</style>
