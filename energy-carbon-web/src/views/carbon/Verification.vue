<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">碳核查支撑</span>
          <el-button type="primary" @click="handleAdd">新增核查</el-button>
        </div>
      </template>
      <el-form :inline="true" class="search-form">
        <el-form-item label="核查年度">
          <el-input v-model="searchForm.verificationYear" placeholder="请输入核查年度" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="verificationName" label="核查名称" width="180" />
        <el-table-column prop="verificationYear" label="核查年度" width="120" />
        <el-table-column prop="orgName" label="组织名称" width="180" />
        <el-table-column prop="verifier" label="核查机构" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '已通过' : '待核查' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="verificationDate" label="核查日期" width="140" />
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
        <el-form-item label="核查名称">
          <el-input v-model="form.verificationName" placeholder="请输入核查名称" />
        </el-form-item>
        <el-form-item label="核查年度">
          <el-input v-model="form.verificationYear" placeholder="请输入核查年度" />
        </el-form-item>
        <el-form-item label="组织名称">
          <el-input v-model="form.orgName" placeholder="请输入组织名称" />
        </el-form-item>
        <el-form-item label="核查机构">
          <el-input v-model="form.verifier" placeholder="请输入核查机构" />
        </el-form-item>
        <el-form-item label="核查日期">
          <el-input v-model="form.verificationDate" placeholder="请输入核查日期" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">已通过</el-radio>
            <el-radio :label="0">待核查</el-radio>
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
import { getVerificationList, createVerification, updateVerification, deleteVerification } from '@/api/carbon'

const loading = ref(false)
const tableData = ref([])

const searchForm = reactive({ verificationYear: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const dialogTitle = ref('新增核查')
const form = reactive({ id: undefined, verificationName: '', verificationYear: '', orgName: '', verifier: '', verificationDate: '', status: 0 })

async function fetchList() {
  loading.value = true
  try {
    const res = await getVerificationList({ current: pagination.pageNum, size: pagination.pageSize, ...searchForm })
    tableData.value = res.data.records || res.data.list || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

function handleSearch() { pagination.pageNum = 1; fetchList() }
function handleReset() { searchForm.verificationYear = ''; handleSearch() }
function handleSizeChange(val) { pagination.pageSize = val; fetchList() }
function handleCurrentChange(val) { pagination.pageNum = val; fetchList() }

function handleAdd() {
  dialogTitle.value = '新增核查'
  Object.assign(form, { id: undefined, verificationName: '', verificationYear: '', orgName: '', verifier: '', verificationDate: '', status: 0 })
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑核查'
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  try {
    if (form.id) {
      await updateVerification(form.id, JSON.parse(JSON.stringify(form)))
    } else {
      await createVerification(JSON.parse(JSON.stringify(form)))
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
    await deleteVerification(row.id)
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
