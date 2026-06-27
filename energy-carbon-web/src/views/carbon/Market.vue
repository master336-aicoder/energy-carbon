<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">碳资产市场</span>
          <el-button type="primary" @click="handleAdd">新增交易</el-button>
        </div>
      </template>
      <el-form :inline="true" class="search-form">
        <el-form-item label="交易类型">
          <el-select v-model="searchForm.tradeType" placeholder="请选择交易类型" clearable>
            <el-option label="碳配额" value="quota" />
            <el-option label="CCER" value="ccer" />
            <el-option label="绿证" value="green" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="tradeType" label="交易类型" width="120" />
        <el-table-column prop="tradeDate" label="交易日期" width="140" />
        <el-table-column prop="tradeVolume" label="交易量" width="120" />
        <el-table-column prop="tradePrice" label="交易价格" width="120" />
        <el-table-column prop="totalAmount" label="交易总额" width="140" />
        <el-table-column prop="buyer" label="买方" width="140" />
        <el-table-column prop="seller" label="卖方" width="140" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '停用' }}</el-tag>
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
        <el-form-item label="交易类型">
          <el-select v-model="form.tradeType" placeholder="请选择交易类型">
            <el-option label="碳配额" value="quota" />
            <el-option label="CCER" value="ccer" />
            <el-option label="绿证" value="green" />
          </el-select>
        </el-form-item>
        <el-form-item label="交易日期">
          <el-date-picker v-model="form.tradeDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="交易量">
          <el-input-number v-model="form.tradeVolume" :precision="2" />
        </el-form-item>
        <el-form-item label="交易价格">
          <el-input-number v-model="form.tradePrice" :precision="2" />
        </el-form-item>
        <el-form-item label="买方">
          <el-input v-model="form.buyer" placeholder="请输入买方" />
        </el-form-item>
        <el-form-item label="卖方">
          <el-input v-model="form.seller" placeholder="请输入卖方" />
        </el-form-item>
        <el-form-item label="状态">
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
import { getMarketList, createMarket, updateMarket, deleteMarket } from '@/api/carbon'

const loading = ref(false)
const tableData = ref([])

const searchForm = reactive({ tradeType: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const dialogTitle = ref('新增交易')
const form = reactive({
  id: undefined as number | undefined,
  tradeType: '',
  tradeDate: '',
  tradeVolume: 0,
  tradePrice: 0,
  buyer: '',
  seller: '',
  status: 1
})

async function fetchList() {
  loading.value = true
  try {
    const res: any = await getMarketList({
      current: pagination.pageNum,
      size: pagination.pageSize,
      tradeType: searchForm.tradeType,
    })
    const data = res.data || {}
    tableData.value = data.records || []
    pagination.total = data.total || 0
  } catch (error) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() { pagination.pageNum = 1; fetchList() }
function handleReset() { searchForm.tradeType = ''; handleSearch() }
function handleSizeChange(val: number) { pagination.pageSize = val; fetchList() }
function handleCurrentChange(val: number) { pagination.pageNum = val; fetchList() }

function handleAdd() {
  dialogTitle.value = '新增交易'
  Object.assign(form, { id: undefined, tradeType: '', tradeDate: '', tradeVolume: 0, tradePrice: 0, buyer: '', seller: '', status: 1 })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  dialogTitle.value = '编辑交易'
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  try {
    if (form.id) {
      await updateMarket(form.id, JSON.parse(JSON.stringify(form)))
    } else {
      await createMarket(JSON.parse(JSON.stringify(form)))
    }
    ElMessage.success(dialogTitle.value + '成功')
    dialogVisible.value = false
    fetchList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

function handleDelete(row: any) {
  ElMessageBox.confirm('确认删除该记录？', '提示', { type: 'warning' }).then(async () => {
    try {
      await deleteMarket(row.id)
      ElMessage.success('删除成功')
      fetchList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(fetchList)
</script>

<style scoped lang="scss">
.page-container { padding: 10px; }
.search-form { margin-bottom: 20px; }
.pagination { margin-top: 20px; justify-content: flex-end; }
</style>
