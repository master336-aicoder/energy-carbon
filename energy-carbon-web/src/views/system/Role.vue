<template>
  <div class="system-role">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="handleAdd">新增角色</el-button>
        </div>
      </template>

      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="角色名称">
          <el-input v-model="searchForm.roleName" placeholder="请输入角色名称" clearable />
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

      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column prop="roleName" label="角色名称" />
        <el-table-column prop="roleCode" label="角色编码" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
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
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handleAssignMenu(row)">分配菜单</el-button>
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
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
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

    <el-dialog v-model="menuDialogVisible" title="分配菜单" width="500px">
      <el-tree
        ref="menuTreeRef"
        :data="menuTree"
        :props="{ label: 'menuName', children: 'children' }"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedMenuIds"
      />
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignMenuSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const menuDialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const menuTreeRef = ref()
const currentRoleId = ref<number | null>(null)

const searchForm = reactive({
  roleName: '',
  status: null as number | null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const tableData = ref<any[]>([])
const menuTree = ref<any[]>([])
const checkedMenuIds = ref<number[]>([])

const form = reactive({
  id: null as number | null,
  roleName: '',
  roleCode: '',
  remark: '',
  sort: 0,
  status: 1
})

const formRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

async function fetchList() {
  loading.value = true
  try {
    const res: any = await request.get('/system/role/list', {
      params: {
        current: pagination.current,
        size: pagination.size,
        roleName: searchForm.roleName || undefined,
        status: searchForm.status
      }
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

async function fetchMenuTree() {
  const res: any = await request.get('/system/menu/tree')
  menuTree.value = res.data
}

function handleSearch() {
  pagination.current = 1
  fetchList()
}

function handleReset() {
  searchForm.roleName = ''
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
  dialogTitle.value = '新增角色'
  form.id = null
  form.roleName = ''
  form.roleCode = ''
  form.remark = ''
  form.sort = 0
  form.status = 1
  dialogVisible.value = true
}

function handleEdit(row: any) {
  dialogTitle.value = '编辑角色'
  form.id = row.id
  form.roleName = row.roleName
  form.roleCode = row.roleCode
  form.remark = row.remark || ''
  form.sort = row.sort || 0
  form.status = row.status
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (form.id) {
      await request.put(`/system/role/${form.id}`, form)
      ElMessage.success('修改成功')
    } else {
      await request.post('/system/role', form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchList()
  } finally {
    submitLoading.value = false
  }
}

async function handleStatusChange(id: number, status: number) {
  await request.put(`/system/role/${id}/status`, null, { params: { status } })
  ElMessage.success('状态更新成功')
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm(`确定删除角色 "${row.roleName}" 吗？`, '提示', { type: 'warning' })
  await request.delete(`/system/role/${row.id}`)
  ElMessage.success('删除成功')
  fetchList()
}

async function handleAssignMenu(row: any) {
  currentRoleId.value = row.id
  menuDialogVisible.value = true
  await nextTick()
  const res: any = await request.get(`/system/role/${row.id}/menuIds`)
  checkedMenuIds.value = res.data
  menuTreeRef.value?.setCheckedKeys(checkedMenuIds.value)
}

async function handleAssignMenuSubmit() {
  submitLoading.value = true
  try {
    const checkedKeys = menuTreeRef.value?.getCheckedKeys() || []
    const halfCheckedKeys = menuTreeRef.value?.getHalfCheckedKeys() || []
    const allKeys = [...checkedKeys, ...halfCheckedKeys]
    await request.post('/system/role/assignMenu', {
      roleId: currentRoleId.value,
      menuIds: allKeys
    })
    ElMessage.success('分配菜单成功')
    menuDialogVisible.value = false
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  fetchList()
  fetchMenuTree()
})
</script>

<style scoped lang="scss">
.system-role {
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
