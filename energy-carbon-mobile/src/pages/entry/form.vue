<template>
  <view class="form-page">
    <view class="form-content">
      <view class="form-header">
        <text class="form-title">{{ formTitle }}</text>
      </view>

      <view class="form-body">
        <view class="form-group">
          <text class="form-label">数值 <text class="required">*</text></text>
          <input
            class="form-input"
            v-model="form.value"
            type="digit"
            placeholder="请输入数值"
            placeholder-class="placeholder"
          />
        </view>

        <view class="form-group">
          <text class="form-label">日期 <text class="required">*</text></text>
          <picker mode="date" :value="form.date" @change="onDateChange">
            <view class="picker-view">
              <text :class="{ 'picker-placeholder': !form.date }">{{ form.date || '请选择日期' }}</text>
              <uni-icons type="right" size="14" color="#cccccc"></uni-icons>
            </view>
          </picker>
        </view>

        <view class="form-group">
          <text class="form-label">时间</text>
          <picker mode="time" :value="form.time" @change="onTimeChange">
            <view class="picker-view">
              <text :class="{ 'picker-placeholder': !form.time }">{{ form.time || '请选择时间' }}</text>
              <uni-icons type="right" size="14" color="#cccccc"></uni-icons>
            </view>
          </picker>
        </view>

        <view class="form-group">
          <text class="form-label">备注</text>
          <textarea
            class="form-textarea"
            v-model="form.remark"
            placeholder="请输入备注信息"
            placeholder-class="placeholder"
            maxlength="200"
          />
        </view>
      </view>

      <view class="form-actions">
        <button class="submit-btn" :loading="loading" @click="handleSubmit">
          {{ loading ? '提交中...' : '提 交' }}
        </button>
        <button class="reset-btn" @click="handleReset">重置</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { reactive, ref, onLoad } from 'vue'
import request from '@/utils/request.js'

const formType = ref('indicator')
const formTitle = ref('指标录入')
const loading = ref(false)
const scanData = ref('')

const form = reactive({
  value: '',
  date: '',
  time: '',
  remark: ''
})

const typeMap = {
  indicator: { title: '指标录入', url: '/monitor/indicator/entry' },
  output: { title: '产量录入', url: '/monitor/output/entry' },
  meter: { title: '抄表录入', url: '/monitor/meter-record' }
}

onLoad((options) => {
  if (options.type && typeMap[options.type]) {
    formType.value = options.type
    formTitle.value = typeMap[options.type].title
  }
  if (options.scanData) {
    scanData.value = decodeURIComponent(options.scanData)
    form.remark = '扫码数据：' + scanData.value
  }
  // 默认今天
  const d = new Date()
  form.date = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
  form.time = `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
})

const onDateChange = (e) => {
  form.date = e.detail.value
}

const onTimeChange = (e) => {
  form.time = e.detail.value
}

const handleSubmit = async () => {
  if (!form.value) {
    uni.showToast({ title: '请输入数值', icon: 'none' })
    return
  }
  if (!form.date) {
    uni.showToast({ title: '请选择日期', icon: 'none' })
    return
  }

  loading.value = true
  try {
    const payload = {
      value: parseFloat(form.value),
      recordDate: form.date + (form.time ? ' ' + form.time : ''),
      remark: form.remark,
      scanData: scanData.value
    }
    const apiUrl = typeMap[formType.value].url
    const res = await request.post(apiUrl, payload)
    if (res.code === 200) {
      uni.showToast({ title: '提交成功', icon: 'success' })
      setTimeout(() => {
        uni.navigateBack()
      }, 1000)
    } else {
      uni.showToast({ title: res.message || '提交失败', icon: 'none' })
    }
  } catch (e) {
    uni.showToast({ title: '提交失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  form.value = ''
  form.remark = scanData.value ? '扫码数据：' + scanData.value : ''
}
</script>

<style scoped>
.form-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24rpx;
}
.form-content {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 40rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}
.form-header {
  margin-bottom: 40rpx;
  text-align: center;
}
.form-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #1a5f3f;
}
.form-group {
  margin-bottom: 32rpx;
}
.form-label {
  font-size: 28rpx;
  color: #333333;
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
  color: #333333;
}
.picker-view {
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 20rpx 24rpx;
  font-size: 30rpx;
  color: #333333;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.picker-placeholder {
  color: #bbbbbb;
}
.form-textarea {
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 20rpx 24rpx;
  font-size: 30rpx;
  color: #333333;
  width: 100%;
  height: 180rpx;
  box-sizing: border-box;
}
.placeholder {
  color: #bbbbbb;
}
.form-actions {
  margin-top: 48rpx;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}
.submit-btn {
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  color: #ffffff;
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
  color: #666666;
  font-size: 32rpx;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  border: none;
}
.reset-btn::after {
  border: none;
}
</style>
