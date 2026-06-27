<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">能碳数据计算器</span>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form label-width="120px">
            <el-form-item label="电力消费量(kWh)">
              <el-input-number v-model="calcForm.electricity" :precision="2" :min="0" style="width: 200px" />
            </el-form-item>
            <el-form-item label="天然气(m³)">
              <el-input-number v-model="calcForm.gas" :precision="2" :min="0" style="width: 200px" />
            </el-form-item>
            <el-form-item label="自来水(t)">
              <el-input-number v-model="calcForm.water" :precision="2" :min="0" style="width: 200px" />
            </el-form-item>
            <el-form-item label="蒸汽(t)">
              <el-input-number v-model="calcForm.steam" :precision="2" :min="0" style="width: 200px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleCalculate">计算</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-col>
        <el-col :span="12">
          <el-card shadow="never">
            <template #header>
              <span>计算结果</span>
            </template>
            <div class="result-item">
              <span class="result-label">折标准煤(kgce):</span>
              <span class="result-value">{{ result.coal }}</span>
            </div>
            <div class="result-item">
              <span class="result-label">碳排放量(tCO2e):</span>
              <span class="result-value">{{ result.carbon }}</span>
            </div>
            <div class="result-item">
              <span class="result-label">用能成本(元):</span>
              <span class="result-value">{{ result.cost }}</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const calcForm = reactive({ electricity: 0, gas: 0, water: 0, steam: 0 })
const result = reactive({ coal: 0, carbon: 0, cost: 0 })

function handleCalculate() {
  const { electricity, gas, water, steam } = calcForm
  // 折标煤系数：电 0.1229 kgce/kWh，天然气 1.2143 kgce/m³，水 0.0857 kgce/t，蒸汽 88 kgce/t
  result.coal = (electricity * 0.1229 + gas * 1.2143 + water * 0.0857 + steam * 88).toFixed(2)
  // 碳排放系数：电 0.5703 kgCO2/kWh，天然气 2.162 kgCO2/m³，水 0.091 kgCO2/t，蒸汽 110 kgCO2/t
  result.carbon = (electricity * 0.5703 / 1000 + gas * 2.162 / 1000 + water * 0.091 / 1000 + steam * 110 / 1000).toFixed(4)
  // 用能成本：电 0.8 元/kWh，天然气 3.5 元/m³，水 4 元/t，蒸汽 200 元/t
  result.cost = (electricity * 0.8 + gas * 3.5 + water * 4 + steam * 200).toFixed(2)
  ElMessage.success('计算完成')
}

function handleReset() {
  calcForm.electricity = 0
  calcForm.gas = 0
  calcForm.water = 0
  calcForm.steam = 0
  result.coal = 0
  result.carbon = 0
  result.cost = 0
}
</script>

<style scoped lang="scss">
.page-container { padding: 10px; }
.result-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
  .result-label { color: #666; }
  .result-value { font-weight: bold; color: #0d9488; font-size: 18px; }
}
</style>
