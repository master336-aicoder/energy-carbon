<template>
  <div class="system-user">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="handleAdd">新增用户</el-button>
        </div>
      </template>

      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="deptName" label="部门" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="(val) => handleStatusChange(row.id, val)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handleAssignRole(row)">分配角色</el-button>
            <el-button type="primary" link @click="handleResetPwd(row)">重置密码</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!form.id">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="部门">
          <el-tree-select
            v-model="form.deptId"
            :data="deptTree"
            :props="{ label: 'deptName', value: 'id' }"
            placeholder="请选择部门"
            clearable
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="roleDialogVisible" title="分配角色" width="400px">
      <el-checkbox-group v-model="selectedRoleIds">
        <el-checkbox v-for="role in roleList" :key="role.id" :label="role.id">{{ role.roleName }}</el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignRoleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const roleDialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const currentUserId = ref<number | null>(null)

const searchForm = reactive({
  username: '',
  status: null as number | null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const tableData = ref<any[]>([])
const deptTree = ref<any[]>([])
const roleList = ref<any[]>([])
const selectedRoleIds = ref<number[]>([])

const form = reactive({
  id: null as number | null,
  username: '',
  password: '',
  nickname: '',
  phone: '',
  email: '',
  deptId: null as number | null,
  status: 1
})

const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function fetchList() {
  loading.value = true
  try {
    const res: any = await request.get('/system/user/list', {
      params: {
        current: pagination.current,
        size: pagination.size,
        username: searchForm.username || undefined,
        status: searchForm.status
      }
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

async function fetchDeptTree() {
  const res: any = await request.get('/system/dept/tree')
  deptTree.value = res.data
}

async function fetchRoleList() {
  const res: any = await request.get('/system/role/all')
  roleList.value = res.data
}

function handleSearch() {
  pagination.current = 1
  fetchList()
}

function handleReset() {
  searchForm.username = ''
  searchForm.status = null
  pagination.current = 1
  fetchList()
}

function handleSizeChange(val: number) {
  pagination.size = val
  fetchList()
}

function handleCurrentChange(val: number) {
  pagination.current = val
  fetchList()
}

function handleAdd() {
  dialogTitle.value = '新增用户'
  form.id = null
  form.username = ''
  form.password = ''
  form.nickname = ''
  form.phone = ''
  form.email = ''
  form.deptId = null
  form.status = 1
  dialogVisible.value = true
}

function handleEdit(row: any) {
  dialogTitle.value = '编辑用户'
  form.id = row.id
  form.username = row.username
  form.password = ''
  form.nickname = row.nickname || ''
  form.phone = row.phone || ''
  form.email = row.email || ''
  form.deptId = row.deptId || null
  form.status = row.status
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (form.id) {
      await request.put(`/system/user/${form.id}`, form)
      ElMessage.success('修改成功')
    } else {
      await request.post('/system/user', form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchList()
  } finally {
    submitLoading.value = false
  }
}

async function handleStatusChange(id: number, status: number) {
  await request.put(`/system/user/${id}/status`, null, { params: { status } })
  ElMessage.success('状态更新成功')
}

async function handleResetPwd(row: any) {
  await ElMessageBox.confirm(`确定重置用户 "${row.username}" 的密码吗？`, '提示', { type: 'warning' })
  await request.put(`/system/user/${row.id}/resetPassword`)
  ElMessage.success('密码已重置为 123456')
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm(`确定删除用户 "${row.username}" 吗？`, '提示', { type: 'warning' })
  await request.delete(`/system/user/${row.id}`)
  ElMessage.success('删除成功')
  fetchList()
}

async function handleAssignRole(row: any) {
  currentUserId.value = row.id
  const res: any = await request.get('/system/role/all')
  roleList.value = res.data
  const userRoleRes: any = await request.get(`/system/user/${row.id}`)
  selectedRoleIds.value = userRoleRes.data.roles?.map((r: any) => r.id) || []
  roleDialogVisible.value = true
}

async function handleAssignRoleSubmit() {
  submitLoading.value = true
  try {
    await request.post('/system/user/assignRole', {
      userId: currentUserId.value,
      roleIds: selectedRoleIds.value
    })
    ElMessage.success('分配角色成功')
    roleDialogVisible.value = false
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  fetchList()
  fetchDeptTree()
})
</script>

<style scoped lang="scss">
.system-user {
  padding: 20px;
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
