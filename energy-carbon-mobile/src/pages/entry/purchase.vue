<template>
  <view class="purchase-page">
    <nav-bar title="能源采购录入" showBack></nav-bar>
    <!-- 表单 -->
    <view class="form-card">
      <view class="form-title">
        <text>能源采购录入</text>
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
        <text class="form-label">采购量 <text class="required">*</text></text>
        <input class="form-input" v-model="form.quantity" type="digit" placeholder="请输入采购量" @input="calcTotal" />
      </view>

      <view class="form-group">
        <text class="form-label">单价（元） <text class="required">*</text></text>
        <input class="form-input" v-model="form.unitPrice" type="digit" placeholder="请输入单价" @input="calcTotal" />
      </view>

      <view class="form-group">
        <text class="form-label">总金额（元）</text>
        <view class="total-display">
          <text class="total-value">{{ form.totalAmount || '0.00' }}</text>
        </view>
      </view>

      <view class="form-group">
        <text class="form-label">供应商</text>
        <input class="form-input" v-model="form.supplier" placeholder="请输入供应商名称" />
      </view>

      <view class="form-group">
        <text class="form-label">采购日期 <text class="required">*</text></text>
        <picker mode="date" :value="form.purchaseDate" @change="onDateChange">
          <view class="picker-view">
            <text :class="{ placeholder: !form.purchaseDate }">{{ form.purchaseDate || '请选择采购日期' }}</text>
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
        {{ loading ? '提交中...' : '提交采购记录' }}
      </button>
      <button class="reset-btn" @click="handleReset">重置</button>
    </view>

    <!-- 历史采购记录 -->
    <view class="history-section">
      <view class="section-header">
        <text class="section-title">近期采购记录</text>
      </view>
      <view class="record-list">
        <view v-for="item in purchaseList" :key="item.id" class="record-card">
          <view class="record-header">
            <text class="record-type">{{ item.energyLabel }}</text>
            <text class="record-date">{{ item.purchaseDate }}</text>
          </view>
          <view class="record-body">
            <view class="record-info">
              <text class="record-label">采购量</text>
              <text class="record-val">{{ item.quantity }} {{ item.unit }}</text>
            </view>
            <view class="record-info">
              <text class="record-label">总金额</text>
              <text class="record-val price">{{ item.totalAmount }} 元</text>
            </view>
          </view>
          <view v-if="item.supplier" class="record-footer">
            <text class="record-supplier">供应商：{{ item.supplier }}</text>
          </view>
        </view>

        <view v-if="purchaseList.length === 0" class="empty-state">
          <text class="empty-text">暂无采购记录</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import request from '@/utils/request.js'

const loading = ref(false)
const energyIndex = ref(-1)
const energyTypes = [
  { value: 'electricity', label: '电', unit: 'kWh' },
  { value: 'gas', label: '天然气', unit: 'm3' },
  { value: 'water', label: '水', unit: '吨' },
  { value: 'steam', label: '蒸汽', unit: '吨' }
]

const form = reactive({
  energyType: '',
  quantity: '',
  unitPrice: '',
  totalAmount: '',
  supplier: '',
  purchaseDate: '',
  remark: ''
})

const purchaseList = ref([])

const onEnergyChange = (e) => {
  energyIndex.value = e.detail.value
  form.energyType = energyTypes[e.detail.value].value
}

const onDateChange = (e) => {
  form.purchaseDate = e.detail.value
}

const calcTotal = () => {
  const q = parseFloat(form.quantity) || 0
  const p = parseFloat(form.unitPrice) || 0
  form.totalAmount = (q * p).toFixed(2)
}

const handleSubmit = async () => {
  if (energyIndex.value < 0) {
    uni.showToast({ title: '请选择能源类型', icon: 'none' })
    return
  }
  if (!form.quantity) {
    uni.showToast({ title: '请输入采购量', icon: 'none' })
    return
  }
  if (!form.unitPrice) {
    uni.showToast({ title: '请输入单价', icon: 'none' })
    return
  }
  if (!form.purchaseDate) {
    uni.showToast({ title: '请选择采购日期', icon: 'none' })
    return
  }

  loading.value = true
  try {
    const payload = {
      energyType: form.energyType,
      quantity: parseFloat(form.quantity),
      unitPrice: parseFloat(form.unitPrice),
      totalAmount: parseFloat(form.totalAmount),
      supplier: form.supplier,
      purchaseDate: form.purchaseDate,
      remark: form.remark
    }
    const res = await request.post('/monitor/energy-purchase', payload)
    if (res.code === 200) {
      uni.showToast({ title: '提交成功', icon: 'success' })
      handleReset()
      loadPurchaseList()
    } else {
      uni.showToast({ title: res.message || '提交失败', icon: 'none' })
    }
  } catch (e) {
    console.error(e)
    // 本地存储
    const list = JSON.parse(uni.getStorageSync('purchase_records') || '[]')
    list.unshift({
      id: Date.now(),
      energyType: form.energyType,
      energyLabel: energyIndex.value >= 0 ? energyTypes[energyIndex.value].label : '',
      unit: energyIndex.value >= 0 ? energyTypes[energyIndex.value].unit : '',
      quantity: form.quantity,
      unitPrice: form.unitPrice,
      totalAmount: form.totalAmount,
      supplier: form.supplier,
      purchaseDate: form.purchaseDate,
      remark: form.remark
    })
    uni.setStorageSync('purchase_records', JSON.stringify(list))
    uni.showToast({ title: '提交成功（本地存储）', icon: 'success' })
    handleReset()
    loadPurchaseList()
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  energyIndex.value = -1
  form.energyType = ''
  form.quantity = ''
  form.unitPrice = ''
  form.totalAmount = ''
  form.supplier = ''
  form.purchaseDate = ''
  form.remark = ''
}

const loadPurchaseList = async () => {
  try {
    const res = await request.get('/monitor/energy-purchase/list', { size: 20 })
    if (res.code === 200 && res.data) {
      purchaseList.value = res.data.records || res.data || []
      return
    }
  } catch (e) {
    console.error('加载采购记录失败:', e)
  }
  // 演示数据
  purchaseList.value = [
    { id: 1, energyType: 'electricity', energyLabel: '电', unit: 'kWh', quantity: '50000', unitPrice: '0.85', totalAmount: '42500.00', supplier: '国家电网', purchaseDate: '2026-06-25', remark: '' },
    { id: 2, energyType: 'gas', energyLabel: '天然气', unit: 'm3', quantity: '10000', unitPrice: '3.20', totalAmount: '32000.00', supplier: '华润燃气', purchaseDate: '2026-06-20', remark: '六月采购' },
    { id: 3, energyType: 'water', energyLabel: '水', unit: '吨', quantity: '2000', unitPrice: '5.60', totalAmount: '11200.00', supplier: '市自来水公司', purchaseDate: '2026-06-18', remark: '' },
    { id: 4, energyType: 'steam', energyLabel: '蒸汽', unit: '吨', quantity: '500', unitPrice: '280.00', totalAmount: '140000.00', supplier: '热力公司', purchaseDate: '2026-06-15', remark: '合同价' }
  ]
}

onShow(() => {
  loadPurchaseList()
})
</script>

<style scoped>
.purchase-page {
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
.total-display {
  background: #f0f5f2;
  border-radius: 12rpx;
  padding: 20rpx 24rpx;
}
.total-value {
  font-size: 36rpx;
  font-weight: 700;
  color: #1a5f3f;
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
.history-section {
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
.record-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}
.record-card {
  border: 1rpx solid #f0f0f0;
  border-radius: 12rpx;
  padding: 20rpx;
}
.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}
.record-type {
  font-size: 28rpx;
  font-weight: 600;
  color: #1a5f3f;
}
.record-date {
  font-size: 24rpx;
  color: #999;
}
.record-body {
  display: flex;
  gap: 32rpx;
}
.record-info {
  display: flex;
  flex-direction: column;
}
.record-label {
  font-size: 22rpx;
  color: #999;
}
.record-val {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
}
.record-val.price {
  color: #f5222d;
}
.record-footer {
  margin-top: 12rpx;
  padding-top: 12rpx;
  border-top: 1rpx solid #f0f0f0;
}
.record-supplier {
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
