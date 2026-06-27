<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">绿色工厂申报助手</span>
          <el-button type="primary" @click="handleAdd">新增申报</el-button>
        </div>
      </template>
      <el-form :inline="true" class="search-form">
        <el-form-item label="工厂名称">
          <el-input v-model="searchForm.factoryName" placeholder="请输入工厂名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="factoryName" label="工厂名称" width="180" />
        <el-table-column prop="assessmentYear" label="评估年度" width="120" />
        <el-table-column prop="score" label="得分" width="100" />
        <el-table-column prop="level" label="等级" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'warning' : 'info'">
              {{ row.status === 1 ? '已通过' : row.status === 2 ? '审核中' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="工厂名称">
          <el-input v-model="form.factoryName" placeholder="请输入工厂名称" />
        </el-form-item>
        <el-form-item label="评估年度">
          <el-input v-model="form.assessmentYear" placeholder="请输入评估年度" />
        </el-form-item>
        <el-form-item label="得分">
          <el-input-number v-model="form.score" :precision="2" />
        </el-form-item>
        <el-form-item label="等级">
          <el-input v-model="form.level" placeholder="请输入等级" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">草稿</el-radio>
            <el-radio :label="2">审核中</el-radio>
            <el-radio :label="1">已通过</el-radio>
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

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getGreenFactoryList, createGreenFactory, updateGreenFactory, deleteGreenFactory } from '@/api/toolkit'

const loading = ref(false)
const tableData = ref([])

const searchForm = reactive({ factoryName: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const dialogTitle = ref('新增申报')
const form = reactive({ id: undefined, factoryName: '', assessmentYear: '', score: 0, level: '', status: 0 })

async function fetchList() {
  loading.value = true
  try {
    const res = await getGreenFactoryList({ current: pagination.pageNum, size: pagination.pageSize, ...searchForm })
    tableData.value = res.data.records || res.data.list || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

function handleSearch() { pagination.pageNum = 1; fetchList() }
function handleReset() { searchForm.factoryName = ''; handleSearch() }
function handleSizeChange(val) { pagination.pageSize = val; fetchList() }
function handleCurrentChange(val) { pagination.pageNum = val; fetchList() }

function handleAdd() {
  dialogTitle.value = '新增申报'
  Object.assign(form, { id: undefined, factoryName: '', assessmentYear: '', score: 0, level: '', status: 0 })
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑申报'
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  try {
    if (form.id) {
      await updateGreenFactory(form.id, JSON.parse(JSON.stringify(form)))
    } else {
      await createGreenFactory(JSON.parse(JSON.stringify(form)))
    }
    ElMessage.success(dialogTitle.value + '成功')
    dialogVisible.value = false
    fetchList()
  } catch (error) {
    console.error(error)
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确认删除该记录？', '提示', { type: 'warning' })
    await deleteGreenFactory(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch (error) {
    // user cancelled or error
  }
}

onMounted(fetchList)
</script>

<style scoped lang="scss">
.page-container { padding: 10px; }
.search-form { margin-bottom: 20px; }
.pagination { margin-top: 20px; justify-content: flex-end; }
</style>
