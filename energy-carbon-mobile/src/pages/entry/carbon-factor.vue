<template>
  <view class="factor-page">
    <!-- 表单 -->
    <view class="form-card">
      <view class="form-title">
        <text>碳排放因子录入</text>
      </view>

      <view class="form-group">
        <text class="form-label">能源类型 <text class="required">*</text></text>
        <picker mode="selector" :range="energyTypes" range-key="label" :value="energyIndex" @change="onEnergyChange">
          <view class="picker-view">
            <text :class="{ placeholder: energyIndex === -1 }">{{ energyIndex >= 0 ? energyTypes[energyIndex].label : '请选择能源类型' }}</text>
            <text class="picker-arrow">></text>
          </view>
        </picker>
      </view>

      <view class="form-group">
        <text class="form-label">排放因子值 <text class="required">*</text></text>
        <input class="form-input" v-model="form.factorValue" type="digit" placeholder="请输入排放因子值" />
      </view>

      <view class="form-group">
        <text class="form-label">单位 <text class="required">*</text></text>
        <input class="form-input" v-model="form.unit" placeholder="如：tCO2/MWh、tCO2/万m3" />
      </view>

      <view class="form-group">
        <text class="form-label">数据来源 <text class="required">*</text></text>
        <picker mode="selector" :range="sourceTypes" :value="sourceIndex" @change="onSourceChange">
          <view class="picker-view">
            <text :class="{ placeholder: sourceIndex === -1 }">{{ sourceIndex >= 0 ? sourceTypes[sourceIndex] : '请选择数据来源' }}</text>
            <text class="picker-arrow">></text>
          </view>
        </picker>
      </view>

      <view class="form-group">
        <text class="form-label">生效日期 <text class="required">*</text></text>
        <picker mode="date" :value="form.effectiveDate" @change="onDateChange">
          <view class="picker-view">
            <text :class="{ placeholder: !form.effectiveDate }">{{ form.effectiveDate || '请选择生效日期' }}</text>
            <text class="picker-arrow">></text>
          </view>
        </picker>
      </view>

      <view class="form-group">
        <text class="form-label">备注</text>
        <textarea class="form-textarea" v-model="form.remark" placeholder="请输入备注信息" maxlength="200" />
      </view>
    </view>

    <!-- 按钮 -->
    <view class="form-actions">
      <button class="submit-btn" :loading="loading" @click="handleSubmit">
        {{ loading ? '提交中...' : '提交排放因子' }}
      </button>
      <button class="reset-btn" @click="handleReset">重置</button>
    </view>

    <!-- 已有排放因子列表 -->
    <view class="factor-section">
      <view class="section-header">
        <text class="section-title">已有排放因子</text>
      </view>
      <view class="factor-list">
        <view v-for="item in factorList" :key="item.id" class="factor-card">
          <view class="factor-header">
            <text class="factor-energy">{{ item.energyLabel }}</text>
            <view class="factor-source-tag">
              <text>{{ item.sourceLabel }}</text>
            </view>
          </view>
          <view class="factor-body">
            <text class="factor-value">{{ item.factorValue }} {{ item.unit }}</text>
          </view>
          <view class="factor-footer">
            <text class="factor-date">生效日期：{{ item.effectiveDate }}</text>
            <text v-if="item.remark" class="factor-remark">{{ item.remark }}</text>
          </view>
        </view>

        <view v-if="factorList.length === 0" class="empty-state">
          <text class="empty-text">暂无排放因子数据</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import request from '@/utils/request.js'

const loading = ref(false)
const energyIndex = ref(-1)
const sourceIndex = ref(-1)
const energyTypes = [
  { value: 'electricity', label: '电力' },
  { value: 'gas', label: '天然气' },
  { value: 'water', label: '水' },
  { value: 'steam', label: '蒸汽' },
  { value: 'coal', label: '煤炭' },
  { value: 'diesel', label: '柴油' },
  { value: 'gasoline', label: '汽油' }
]
const sourceTypes = ['默认值', '实测值', '国家标准']

const form = reactive({
  energyType: '',
  factorValue: '',
  unit: '',
  source: '',
  effectiveDate: '',
  remark: ''
})

const factorList = ref([])

const onEnergyChange = (e) => {
  energyIndex.value = e.detail.value
  form.energyType = energyTypes[e.detail.value].value
}

const onSourceChange = (e) => {
  sourceIndex.value = e.detail.value
  form.source = sourceTypes[e.detail.value]
}

const onDateChange = (e) => {
  form.effectiveDate = e.detail.value
}

const handleSubmit = async () => {
  if (energyIndex.value < 0) {
    uni.showToast({ title: '请选择能源类型', icon: 'none' })
    return
  }
  if (!form.factorValue) {
    uni.showToast({ title: '请输入排放因子值', icon: 'none' })
    return
  }
  if (!form.unit) {
    uni.showToast({ title: '请输入单位', icon: 'none' })
    return
  }
  if (sourceIndex.value < 0) {
    uni.showToast({ title: '请选择数据来源', icon: 'none' })
    return
  }
  if (!form.effectiveDate) {
    uni.showToast({ title: '请选择生效日期', icon: 'none' })
    return
  }

  loading.value = true
  try {
    const payload = {
      energyType: form.energyType,
      factorValue: parseFloat(form.factorValue),
      unit: form.unit,
      source: form.source,
      effectiveDate: form.effectiveDate,
      remark: form.remark
    }
    const res = await request.post('/carbon/emission-factor', payload)
    if (res.code === 200) {
      uni.showToast({ title: '提交成功', icon: 'success' })
      handleReset()
      loadFactorList()
    } else {
      uni.showToast({ title: res.message || '提交失败', icon: 'none' })
    }
  } catch (e) {
    console.error(e)
    // 本地存储
    const list = JSON.parse(uni.getStorageSync('carbon_factors') || '[]')
    list.unshift({
      id: Date.now(),
      energyType: form.energyType,
      energyLabel: energyTypes[energyIndex.value].label,
      factorValue: form.factorValue,
      unit: form.unit,
      sourceLabel: form.source,
      effectiveDate: form.effectiveDate,
      remark: form.remark
    })
    uni.setStorageSync('carbon_factors', JSON.stringify(list))
    uni.showToast({ title: '提交成功（本地存储）', icon: 'success' })
    handleReset()
    loadFactorList()
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  energyIndex.value = -1
  sourceIndex.value = -1
  form.energyType = ''
  form.factorValue = ''
  form.unit = ''
  form.source = ''
  form.effectiveDate = ''
  form.remark = ''
}

const loadFactorList = async () => {
  try {
    const res = await request.get('/carbon/emission-factor/list', { size: 50 })
    if (res.code === 200 && res.data) {
      factorList.value = res.data.records || res.data || []
      return
    }
  } catch (e) {
    console.error('加载排放因子失败:', e)
  }
  // 演示数据
  factorList.value = [
    { id: 1, energyType: 'electricity', energyLabel: '电力', factorValue: '0.5810', unit: 'tCO2/MWh', sourceLabel: '国家标准', effectiveDate: '2026-01-01', remark: '2026年电网排放因子' },
    { id: 2, energyType: 'gas', energyLabel: '天然气', factorValue: '2.1622', unit: 'tCO2/万m3', sourceLabel: '国家标准', effectiveDate: '2026-01-01', remark: '' },
    { id: 3, energyType: 'coal', energyLabel: '煤炭', factorValue: '2.6604', unit: 'tCO2/吨', sourceLabel: '国家标准', effectiveDate: '2026-01-01', remark: '标准煤' },
    { id: 4, energyType: 'diesel', energyLabel: '柴油', factorValue: '3.0959', unit: 'tCO2/吨', sourceLabel: '实测值', effectiveDate: '2026-03-01', remark: '实测数据' },
    { id: 5, energyType: 'water', energyLabel: '水', factorValue: '0.0003', unit: 'tCO2/吨', sourceLabel: '默认值', effectiveDate: '2026-01-01', remark: '' }
  ]
}

onShow(() => {
  loadFactorList()
})
</script>

<style scoped>
.factor-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24rpx;
  padding-bottom: 40rpx;
}
.form-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06);
  margin-bottom: 24rpx;
}
.form-title {
  font-size: 34rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 32rpx;
}
.form-group {
  margin-bottom: 28rpx;
}
.form-label {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 12rpx;
  display: block;
  font-weight: 500;
}
.required {
  color: #f5222d;
}
.form-input {
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 20rpx 24rpx;
  font-size: 30rpx;
  color: #333;
}
.picker-view {
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 20rpx 24rpx;
  font-size: 30rpx;
  color: #333;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.picker-view .placeholder {
  color: #bbb;
}
.picker-arrow {
  color: #ccc;
  font-size: 28rpx;
}
.form-textarea {
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 20rpx 24rpx;
  font-size: 30rpx;
  color: #333;
  width: 100%;
  height: 160rpx;
  box-sizing: border-box;
}
.form-actions {
  margin-bottom: 32rpx;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}
.submit-btn {
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  color: #fff;
  font-size: 34rpx;
  height: 96rpx;
  line-height: 96rpx;
  border-radius: 48rpx;
  border: none;
}
.submit-btn::after {
  border: none;
}
.reset-btn {
  background: #f0f0f0;
  color: #666;
  font-size: 32rpx;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  border: none;
}
.reset-btn::after {
  border: none;
}
.factor-section {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06);
}
.section-header {
  margin-bottom: 20rpx;
}
.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
}
.factor-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}
.factor-card {
  border: 1rpx solid #f0f0f0;
  border-radius: 12rpx;
  padding: 20rpx;
}
.factor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}
.factor-energy {
  font-size: 28rpx;
  font-weight: 600;
  color: #1a5f3f;
}
.factor-source-tag {
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
  background: #e6f7ff;
  color: #1890ff;
}
.factor-body {
  margin-bottom: 12rpx;
}
.factor-value {
  font-size: 34rpx;
  font-weight: 700;
  color: #333;
}
.factor-footer {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}
.factor-date {
  font-size: 24rpx;
  color: #999;
}
.factor-remark {
  font-size: 24rpx;
  color: #999;
}
.empty-state {
  display: flex;
  justify-content: center;
  padding: 60rpx 0;
}
.empty-text {
  font-size: 28rpx;
  color: #999;
}
</style>
