<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">通知公告管理</span>
          <div>
            <el-button type="primary" @click="handleAdd">发布公告</el-button>
          </div>
        </div>
      </template>
      <el-form :inline="true" class="search-form">
        <el-form-item label="公告标题">
          <el-input v-model="searchForm.noticeTitle" placeholder="请输入公告标题" clearable />
        </el-form-item>
        <el-form-item label="公告类型">
          <el-select v-model="searchForm.noticeType" placeholder="请选择类型" clearable style="width: 140px">
            <el-option label="通知" :value="1" />
            <el-option label="公告" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 140px">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
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
        <el-table-column prop="noticeType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.noticeType === 1 ? 'primary' : 'warning'">{{ row.noticeType === 1 ? '通知' : '公告' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="handleDispatch(row)" v-if="row.status === 1">下发</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <!-- 发布/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px" destroy-on-close>
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="公告标题" prop="noticeTitle">
          <el-input v-model="form.noticeTitle" placeholder="请输入公告标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="公告类型">
          <el-radio-group v-model="form.noticeType">
            <el-radio :label="1">通知</el-radio>
            <el-radio :label="2">公告</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="公告内容" prop="noticeContent">
          <div style="border: 1px solid #ccc; z-index: 100;">
            <Toolbar style="border-bottom: 1px solid #ccc" :editor="editorRef" :defaultConfig="toolbarConfig" mode="default" />
            <Editor style="height: 300px; overflow-y: hidden;" v-model="form.noticeContent" :defaultConfig="editorConfig" mode="default" @onCreated="handleCreated" />
          </div>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 下发弹窗 -->
    <el-dialog v-model="dispatchVisible" title="下发通知公告" width="550px" destroy-on-close>
      <el-form label-width="80px">
        <el-form-item label="下发范围">
          <el-radio-group v-model="dispatchForm.dispatchType">
            <el-radio :label="1">全部</el-radio>
            <el-radio :label="2">部门</el-radio>
            <el-radio :label="3">角色</el-radio>
            <el-radio :label="4">用户</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="dispatchForm.dispatchType === 2" label="选择部门">
          <el-tree-select
            v-model="dispatchForm.targetIds"
            :data="deptTree"
            :props="{ label: 'deptName', value: 'id', children: 'children' }"
            multiple
            placeholder="请选择部门"
            style="width: 100%"
            check-strictly
          />
        </el-form-item>
        <el-form-item v-if="dispatchForm.dispatchType === 3" label="选择角色">
          <el-select v-model="dispatchForm.targetIds" multiple placeholder="请选择角色" style="width: 100%">
            <el-option v-for="item in roleList" :key="item.id" :label="item.roleName || item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="dispatchForm.dispatchType === 4" label="选择用户">
          <el-select v-model="dispatchForm.targetIds" multiple placeholder="请选择用户" style="width: 100%">
            <el-option v-for="item in userList" :key="item.id" :label="item.nickname || item.username" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dispatchVisible = false">取消</el-button>
        <el-button type="primary" @click="handleDispatchSubmit" :loading="dispatchLoading">确认下发</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, shallowRef, onBeforeUnmount } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'
import { getNoticeList, createNotice, updateNotice, deleteNotice, dispatchNotice } from '@/api/notice'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const searchForm = reactive({ noticeTitle: '', noticeType: undefined as number | undefined, status: undefined as number | undefined })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const dialogTitle = ref('发布公告')
const form = reactive({ id: undefined as number | undefined, noticeTitle: '', noticeType: 1, noticeContent: '', status: 1, remark: '' })
const formRef = ref()
const rules = {
  noticeTitle: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  noticeContent: [{ required: true, message: '请输入公告内容', trigger: 'blur' }],
}

const editorRef = shallowRef()
const toolbarConfig = {}
const editorConfig = { placeholder: '请输入公告内容...', MENU_CONF: {} }

function handleCreated(editor: any) { editorRef.value = editor }
onBeforeUnmount(() => { if (editorRef.value) editorRef.value.destroy() })

// 下发相关
const dispatchVisible = ref(false)
const dispatchLoading = ref(false)
const dispatchNoticeId = ref<number>()
const dispatchForm = reactive({ dispatchType: 1, targetIds: [] as number[] })
const deptTree = ref<any[]>([])
const roleList = ref<any[]>([])
const userList = ref<any[]>([])

async function fetchList() {
  loading.value = true
  try {
    const res = await getNoticeList({
      current: pagination.pageNum, size: pagination.pageSize,
      noticeTitle: searchForm.noticeTitle, noticeType: searchForm.noticeType, status: searchForm.status,
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

function handleSearch() { pagination.pageNum = 1; fetchList() }
function handleReset() { searchForm.noticeTitle = ''; searchForm.noticeType = undefined; searchForm.status = undefined; handleSearch() }
function handleSizeChange(val: number) { pagination.pageSize = val; fetchList() }
function handleCurrentChange(val: number) { pagination.pageNum = val; fetchList() }

function handleAdd() {
  dialogTitle.value = '发布公告'
  Object.assign(form, { id: undefined, noticeTitle: '', noticeType: 1, noticeContent: '', status: 1, remark: '' })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  dialogTitle.value = '编辑公告'
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  try {
    if (form.id) {
      await updateNotice(form.id, form)
      ElMessage.success('更新成功')
    } else {
      await createNotice(form)
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    fetchList()
  } catch (e) { console.error(e) }
}

function handleDelete(row: any) {
  ElMessageBox.confirm('确认删除该公告？', '提示', { type: 'warning' }).then(async () => {
    await deleteNotice(row.id)
    ElMessage.success('删除成功')
    fetchList()
  }).catch(() => {})
}

async function handleDispatch(row: any) {
  dispatchNoticeId.value = row.id
  dispatchForm.dispatchType = 1
  dispatchForm.targetIds = []
  dispatchVisible.value = true
  // 加载选择列表
  try {
    const [deptRes, roleRes, userRes] = await Promise.all([
      request.get('/system/dept/tree').catch(() => ({ data: [] })),
      request.get('/system/role/list').catch(() => ({ data: [] })),
      request.get('/system/user/list').catch(() => ({ data: [] })),
    ])
    deptTree.value = deptRes.data || []
    roleList.value = roleRes.data?.records || roleRes.data || []
    userList.value = userRes.data?.records || userRes.data || []
  } catch (e) { console.error(e) }
}

async function handleDispatchSubmit() {
  if (dispatchForm.dispatchType !== 1 && dispatchForm.targetIds.length === 0) {
    ElMessage.warning('请选择下发目标')
    return
  }
  dispatchLoading.value = true
  try {
    await dispatchNotice(dispatchNoticeId.value!, {
      dispatchType: dispatchForm.dispatchType,
      targetIds: dispatchForm.targetIds,
    })
    ElMessage.success('下发成功')
    dispatchVisible.value = false
  } catch (e) {
    ElMessage.error('下发失败')
    console.error(e)
  } finally {
    dispatchLoading.value = false
  }
}

onMounted(fetchList)
</script>

<style scoped lang="scss">
.page-container { padding: 10px; }
.search-form { margin-bottom: 20px; }
.pagination { margin-top: 20px; justify-content: flex-end; }
</style>
