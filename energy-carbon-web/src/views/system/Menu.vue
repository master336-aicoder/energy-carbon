<template>
  <div class="menu-page">
    <div class="page-header">
      <h2>菜单管理</h2>
      <p>管理系统菜单及权限配置</p>
    </div>
    <el-card>
      <div style="margin-bottom: 16px;">
        <el-button type="primary" @click="handleAdd">新增菜单</el-button>
      </div>
      <el-table v-loading="loading" :data="menuList" row-key="id" :tree-props="{ children: 'children', hasChildren: 'hasChildren' }" border>
        <el-table-column prop="menuName" label="菜单名称" min-width="160" />
        <el-table-column prop="path" label="路由路径" min-width="160" />
        <el-table-column prop="component" label="组件路径" min-width="200" show-overflow-tooltip />
        <el-table-column prop="permission" label="权限标识" min-width="160" />
        <el-table-column prop="icon" label="图标" width="80" align="center">
          <template #default="{ row }">
            <DynamicIcon v-if="row.icon" :name="row.icon" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="menuType" label="类型" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.menuType === 1" type="primary">目录</el-tag>
            <el-tag v-else-if="row.menuType === 2" type="success">菜单</el-tag>
            <el-tag v-else type="info">按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="70" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上级菜单">
          <el-tree-select v-model="form.parentId" :data="menuTree" :props="{ label: 'menuName', value: 'id' }" clearable placeholder="请选择上级菜单" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="form.menuType">
            <el-radio :label="1">目录</el-radio>
            <el-radio :label="2">菜单</el-radio>
            <el-radio :label="3">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="路由路径" prop="path">
          <el-input v-model="form.path" placeholder="请输入路由路径" />
        </el-form-item>
        <el-form-item label="组件路径" prop="component">
          <el-input v-model="form.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item label="权限标识" prop="permission">
          <el-input v-model="form.permission" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="图标">
          <el-select v-model="form.icon" clearable placeholder="请选择图标" style="width: 100%;">
            <el-option v-for="icon in iconList" :key="icon" :label="icon" :value="icon">
              <DynamicIcon :name="icon" style="margin-right: 8px;" />
              <span>{{ icon }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="显示排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="菜单状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getMenuList, getMenuTree, createMenu, updateMenu, deleteMenu } from '@/api/menu'
import DynamicIcon from '@/components/DynamicIcon.vue'

/** Element Plus 图标列表 */
const iconList = [
  'Odometer', 'Setting', 'User', 'UserFilled', 'Monitor', 'TrendCharts',
  'AlarmClock', 'Document', 'Collection', 'Cpu', 'OfficeBuilding',
  'Edit', 'Delete', 'Plus', 'Search', 'View', 'Lock', 'Unlock',
  'Message', 'Bell', 'Calendar', 'PieChart', 'DataLine', 'Grid',
  'Sunny', 'Cloudy', 'Lightning', 'FirstAidKit', 'ShoppingCart',
  'House', 'MapLocation', 'Histogram', 'Reading', 'Timer',
  'custom-icon-1', 'custom-icon-2', 'custom-icon-3',
]

const loading = ref(false)
const menuList = ref<any[]>([])
const menuTree = ref<any[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增菜单')
const formRef = ref<FormInstance>()

const form = reactive({
  id: undefined as number | undefined,
  parentId: undefined as number | undefined,
  menuName: '',
  menuType: 2 as number,
  path: '',
  component: '',
  permission: '',
  icon: '',
  sort: 0,
  status: 1,
})

const rules: FormRules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }],
  path: [{ required: true, message: '请输入路由路径', trigger: 'blur' }],
  sort: [{ required: true, message: '请输入排序', trigger: 'blur' }],
}

async function loadMenus() {
  loading.value = true
  try {
    const res = await getMenuList()
    menuList.value = res.data || []
    const treeRes = await getMenuTree()
    menuTree.value = [{ id: 0, menuName: '根目录', children: treeRes.data || [] }]
  } finally {
    loading.value = false
  }
}

function resetForm() {
  form.id = undefined
  form.parentId = undefined
  form.menuName = ''
  form.menuType = 2
  form.path = ''
  form.component = ''
  form.permission = ''
  form.icon = ''
  form.sort = 0
  form.status = 1
}

function handleAdd() {
  resetForm()
  dialogTitle.value = '新增菜单'
  dialogVisible.value = true
}

function handleEdit(row: any) {
  resetForm()
  Object.assign(form, row)
  dialogTitle.value = '编辑菜单'
  dialogVisible.value = true
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm('确定要删除该菜单吗？', '提示', { type: 'warning' })
  await deleteMenu(row.id)
  ElMessage.success('删除成功')
  loadMenus()
}

async function handleSubmit() {
  await formRef.value?.validate()
  if (form.id) {
    await updateMenu(form)
    ElMessage.success('更新成功')
  } else {
    await createMenu(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadMenus()
}

onMounted(() => {
  loadMenus()
})
</script>

<style scoped>
.menu-page { padding: 20px; }
.page-header { margin-bottom: 24px; }
.page-header h2 { margin: 0 0 8px; font-size: 24px; }
.page-header p { margin: 0; color: #909399; }
</style>
