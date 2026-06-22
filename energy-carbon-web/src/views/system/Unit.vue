<template>
  <div class="system-unit">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用能单元 / 部门管理</span>
          <el-button type="primary" @click="handleAdd">新增部门</el-button>
        </div>
      </template>

      <el-table
        :data="deptTree"
        v-loading="loading"
        border
        stripe
        row-key="id"
        :tree-props="{ children: 'children' }"
        default-expand-all
      >
        <el-table-column prop="deptName" label="部门名称" min-width="200" />
        <el-table-column prop="leader" label="负责人" />
        <el-table-column prop="phone" label="联系电话" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleAddChild(row)">新增子部门</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="上级部门">
          <el-tree-select
            v-model="form.parentId"
            :data="deptTree"
            :props="{ label: 'deptName', value: 'id', children: 'children' }"
            placeholder="请选择上级部门"
            clearable
            check-strictly
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="form.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="form.leader" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()

const deptTree = ref<any[]>([])

const form = reactive({
  id: null as number | null,
  parentId: null as number | null,
  deptName: '',
  leader: '',
  phone: '',
  email: '',
  sort: 0,
  status: 1
})

const formRules = {
  deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }]
}

async function fetchDeptTree() {
  loading.value = true
  try {
    const res: any = await request.get('/system/dept/tree')
    deptTree.value = res.data
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  dialogTitle.value = '新增部门'
  form.id = null
  form.parentId = null
  form.deptName = ''
  form.leader = ''
  form.phone = ''
  form.email = ''
  form.sort = 0
  form.status = 1
  dialogVisible.value = true
}

function handleAddChild(row: any) {
  dialogTitle.value = '新增子部门'
  form.id = null
  form.parentId = row.id
  form.deptName = ''
  form.leader = ''
  form.phone = ''
  form.email = ''
  form.sort = 0
  form.status = 1
  dialogVisible.value = true
}

function handleEdit(row: any) {
  dialogTitle.value = '编辑部门'
  form.id = row.id
  form.parentId = row.parentId || null
  form.deptName = row.deptName
  form.leader = row.leader || ''
  form.phone = row.phone || ''
  form.email = row.email || ''
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
      await request.put(`/system/dept/${form.id}`, form)
      ElMessage.success('修改成功')
    } else {
      await request.post('/system/dept', form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchDeptTree()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm(`确定删除部门 "${row.deptName}" 吗？`, '提示', { type: 'warning' })
  await request.delete(`/system/dept/${row.id}`)
  ElMessage.success('删除成功')
  fetchDeptTree()
}

onMounted(() => {
  fetchDeptTree()
})
</script>

<style scoped lang="scss">
.system-unit {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
