<template>
  <div class="calculator-page">
    <div class="page-header">
      <h2>能碳数据计算器</h2>
      <p>碳排放/电量/成本快速计算工具</p>
    </div>

    <el-tabs v-model="activeTab">
      <!-- 碳排放计算 -->
      <el-tab-pane label="碳排放计算" name="carbon">
        <div class="calc-card">
          <h4>碳排放量计算</h4>
          <p class="calc-desc">公式: 碳排放量 = 活动数据 x 排放因子</p>
          <el-form :model="carbonForm" label-width="120px" style="max-width: 500px; margin-top: 20px">
            <el-form-item label="活动数据">
              <el-input-number v-model="carbonForm.activityData" :precision="4" :min="0" placeholder="活动数据" style="width: 100%" />
            </el-form-item>
            <el-form-item label="排放因子">
              <el-input-number v-model="carbonForm.emissionFactor" :precision="6" :min="0" placeholder="排放因子" style="width: 100%" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="doCarbonCalc">计算</el-button>
              <el-button @click="resetCarbon">重置</el-button>
            </el-form-item>
          </el-form>
          <div v-if="carbonResult" class="calc-result">
            <el-alert :title="`碳排放量: ${carbonResult.emission} ${carbonResult.unit}`" type="success" :closable="false" show-icon />
          </div>
        </div>
      </el-tab-pane>

      <!-- 电量计算 -->
      <el-tab-pane label="电量计算" name="electricity">
        <div class="calc-card">
          <h4>用电量计算</h4>
          <p class="calc-desc">公式: 用电量 = 功率 x 使用时间</p>
          <el-form :model="electricityForm" label-width="120px" style="max-width: 500px; margin-top: 20px">
            <el-form-item label="功率(kW)">
              <el-input-number v-model="electricityForm.power" :precision="2" :min="0" placeholder="功率" style="width: 100%" />
            </el-form-item>
            <el-form-item label="使用时间(h)">
              <el-input-number v-model="electricityForm.hours" :precision="2" :min="0" placeholder="使用时间" style="width: 100%" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="doElectricityCalc">计算</el-button>
              <el-button @click="resetElectricity">重置</el-button>
            </el-form-item>
          </el-form>
          <div v-if="electricityResult" class="calc-result">
            <el-alert :title="`用电量: ${electricityResult.electricity} ${electricityResult.unit}`" type="success" :closable="false" show-icon />
          </div>
        </div>
      </el-tab-pane>

      <!-- 成本计算 -->
      <el-tab-pane label="成本计算" name="cost">
        <div class="calc-card">
          <h4>用能成本计算</h4>
          <p class="calc-desc">公式: 总成本 = 用电量 x (峰比例 x 峰价 + 平比例 x 平价 + 谷比例 x 谷价)</p>
          <el-form :model="costForm" label-width="140px" style="max-width: 600px; margin-top: 20px">
            <el-form-item label="用电量(kWh)">
              <el-input-number v-model="costForm.consumption" :precision="2" :min="0" style="width: 100%" />
            </el-form-item>
            <el-form-item label="峰段电价(元/kWh)">
              <el-input-number v-model="costForm.peakPrice" :precision="4" :min="0" style="width: 100%" />
            </el-form-item>
            <el-form-item label="平段电价(元/kWh)">
              <el-input-number v-model="costForm.flatPrice" :precision="4" :min="0" style="width: 100%" />
            </el-form-item>
            <el-form-item label="谷段电价(元/kWh)">
              <el-input-number v-model="costForm.valleyPrice" :precision="4" :min="0" style="width: 100%" />
            </el-form-item>
            <el-form-item label="峰段比例(%)">
              <el-input-number v-model="costForm.peakRatio" :min="0" :max="100" style="width: 100%" />
            </el-form-item>
            <el-form-item label="平段比例(%)">
              <el-input-number v-model="costForm.flatRatio" :min="0" :max="100" style="width: 100%" />
            </el-form-item>
            <el-form-item label="谷段比例(%)">
              <el-input-number v-model="costForm.valleyRatio" :min="0" :max="100" style="width: 100%" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="doCostCalc">计算</el-button>
              <el-button @click="resetCost">重置</el-button>
            </el-form-item>
          </el-form>
          <div v-if="costResult" class="calc-result">
            <el-descriptions :column="2" border size="small">
              <el-descriptions-item label="峰段电费">{{ costResult.peakCost }} 元</el-descriptions-item>
              <el-descriptions-item label="平段电费">{{ costResult.flatCost }} 元</el-descriptions-item>
              <el-descriptions-item label="谷段电费">{{ costResult.valleyCost }} 元</el-descriptions-item>
              <el-descriptions-item label="总电费">
                <span style="color: #e6a23c; font-weight: bold">{{ costResult.totalCost }} 元</span>
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </div>
      </el-tab-pane>

      <!-- 历史记录 -->
      <el-tab-pane label="历史记录" name="history">
        <div class="filter-bar">
          <el-select v-model="historyFilter.calcType" placeholder="计算类型" clearable style="width: 150px" @change="loadHistory">
            <el-option label="碳排放计算" value="CARBON" />
            <el-option label="电量计算" value="ELECTRICITY" />
            <el-option label="成本计算" value="COST" />
          </el-select>
        </div>
        <el-table :data="historyData" border stripe style="width: 100%">
          <el-table-column prop="calcName" label="计算名称" width="140" />
          <el-table-column prop="calcType" label="计算类型" width="120">
            <template #default="{ row }">
              <el-tag size="small">{{ calcTypeMap[row.calcType] || row.calcType }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="inputParamsJson" label="输入参数" min-width="300">
            <template #default="{ row }">
              <span class="json-text">{{ formatJson(row.inputParamsJson) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="resultJson" label="计算结果" min-width="250">
            <template #default="{ row }">
              <span class="json-text">{{ formatJson(row.resultJson) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="计算时间" width="170" />
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="{ row }">
              <el-button link type="danger" size="small" @click="deleteHistory(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination">
          <el-pagination v-model:current-page="historyPagination.current" v-model:page-size="historyPagination.size" :total="historyPagination.total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @size-change="loadHistory" @current-change="loadHistory" />
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { calcCarbon, calcElectricity, calcCost, getCalculatorHistory, deleteCalculatorRecord, type CalculatorRecord } from '@/api/toolkit'

const activeTab = ref('carbon')
const carbonResult = ref<Record<string, unknown> | null>(null)
const electricityResult = ref<Record<string, unknown> | null>(null)
const costResult = ref<Record<string, unknown> | null>(null)

const carbonForm = reactive({ activityData: 0, emissionFactor: 0 })
const electricityForm = reactive({ power: 0, hours: 0 })
const costForm = reactive({ consumption: 0, peakPrice: 0, flatPrice: 0, valleyPrice: 0, peakRatio: 30, flatRatio: 40, valleyRatio: 30 })

const historyData = ref<CalculatorRecord[]>([])
const historyFilter = reactive({ calcType: '' })
const historyPagination = reactive({ current: 1, size: 10, total: 0 })

const calcTypeMap: Record<string, string> = { CARBON: '碳排放计算', ELECTRICITY: '电量计算', COST: '成本计算' }

function formatJson(str: string | undefined) {
  if (!str) return '-'
  try {
    const obj = JSON.parse(str)
    return JSON.stringify(obj, null, 2)
  } catch { return str }
}

async function doCarbonCalc() {
  const res = await calcCarbon(carbonForm)
  carbonResult.value = res.data
  ElMessage.success('计算完成')
}

function resetCarbon() {
  carbonForm.activityData = 0
  carbonForm.emissionFactor = 0
  carbonResult.value = null
}

async function doElectricityCalc() {
  const res = await calcElectricity(electricityForm)
  electricityResult.value = res.data
  ElMessage.success('计算完成')
}

function resetElectricity() {
  electricityForm.power = 0
  electricityForm.hours = 0
  electricityResult.value = null
}

async function doCostCalc() {
  const res = await calcCost({ ...costForm, peakRatio: costForm.peakRatio / 100, flatRatio: costForm.flatRatio / 100, valleyRatio: costForm.valleyRatio / 100 })
  costResult.value = res.data
  ElMessage.success('计算完成')
}

function resetCost() {
  Object.assign(costForm, { consumption: 0, peakPrice: 0, flatPrice: 0, valleyPrice: 0, peakRatio: 30, flatRatio: 40, valleyRatio: 30 })
  costResult.value = null
}

async function loadHistory() {
  const res = await getCalculatorHistory({ current: historyPagination.current, size: historyPagination.size, calcType: historyFilter.calcType || undefined })
  historyData.value = res.data.list
  historyPagination.total = res.data.total
}

async function deleteHistory(row: CalculatorRecord) {
  await ElMessageBox.confirm('确定要删除该记录吗？', '提示', { type: 'warning' })
  await deleteCalculatorRecord(row.id!)
  ElMessage.success('已删除')
  loadHistory()
}

onMounted(() => { loadHistory() })
</script>

<style scoped>
.calculator-page { padding: 20px; }
.page-header { margin-bottom: 24px; }
.page-header h2 { margin: 0 0 8px; font-size: 24px; }
.page-header p { margin: 0; color: #909399; }
.calc-card { background: #fff; border-radius: 8px; padding: 24px; box-shadow: 0 2px 12px rgba(0,0,0,0.06); }
.calc-card h4 { margin: 0 0 8px; font-size: 18px; }
.calc-desc { margin: 0; color: #909399; font-size: 14px; }
.calc-result { margin-top: 20px; }
.filter-bar { display: flex; gap: 12px; margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
.json-text { font-size: 12px; color: #606266; white-space: pre-wrap; }
</style>
