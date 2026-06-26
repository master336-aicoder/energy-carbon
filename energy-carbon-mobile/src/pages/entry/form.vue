<template>
  <view class="form-page">
    <nav-bar :title="formTitle" :showBack="true"></nav-bar>
    <view class="form-content" :style="{ paddingTop: safeAreaTop + 'px' }">
      <view class="form-body">
        <!-- 抄表录入特有字段 -->
        <template v-if="formType === 'meter'">
          <view class="form-group">
            <text class="form-label">计量器具 <text class="required">*</text></text>
            <picker mode="selector" :range="meterList" range-key="meterName" :value="meterIndex" @change="onMeterChange">
              <view class="picker-view">
                <text :class="{ 'picker-placeholder': meterIndex === -1 }">{{ meterIndex >= 0 ? meterList[meterIndex].meterName : '请选择计量器具' }}</text>
                <uni-icons type="right" size="14" color="#cccccc"></uni-icons>
              </view>
            </picker>
          </view>

          <view class="form-group">
            <text class="form-label">起始表码 <text class="required">*</text></text>
            <input class="form-input" v-model="form.startCode" type="digit" placeholder="请输入起始表码" />
          </view>

          <view class="form-group">
            <text class="form-label">终止表码 <text class="required">*</text></text>
            <input class="form-input" v-model="form.endCode" type="digit" placeholder="请输入终止表码" />
          </view>

          <view class="form-group">
            <text class="form-label">单价 (元)</text>
            <input class="form-input" v-model="form.unitPrice" type="digit" placeholder="请输入单价" />
          </view>
        </template>

        <!-- 指标/产量录入 -->
        <template v-else>
          <view class="form-group">
            <text class="form-label">{{ formType === 'indicator' ? '指标名称' : '产品名称' }} <text class="required">*</text></text>
            <input class="form-input" v-model="form.name" placeholder="请输入名称" />
          </view>

          <view class="form-group">
            <text class="form-label">数值 <text class="required">*</text></text>
            <input class="form-input" v-model="form.value" type="digit" placeholder="请输入数值" />
          </view>

          <view class="form-group">
            <text class="form-label">单位</text>
            <input class="form-input" v-model="form.unit" placeholder="如：kWh、吨、件" />
          </view>
        </template>

        <!-- 公共字段 -->
        <view class="form-group">
          <text class="form-label">日期时间 <text class="required">*</text></text>
          <picker mode="multiSelector" :range="dateRange" :value="dateIndex" @change="onDateTimeChange" @columnchange="onColumnChange">
            <view class="picker-view">
              <text :class="{ 'picker-placeholder': !form.recordTime }">{{ form.recordTime || '请选择日期时间' }}</text>
              <uni-icons type="right" size="14" color="#cccccc"></uni-icons>
            </view>
          </picker>
        </view>

        <view class="form-group">
          <text class="form-label">备注</text>
          <textarea class="form-textarea" v-model="form.remark" placeholder="请输入备注信息" maxlength="200" />
        </view>
      </view>

      <view class="form-actions">
        <button class="submit-btn" :loading="loading" @click="handleSubmit">{{ loading ? '提交中...' : '提 交' }}</button>
        <button class="reset-btn" @click="handleReset">重置</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import request from '@/utils/request.js'

const formType = ref('indicator')
const formTitle = ref('指标录入')
const loading = ref(false)
const safeAreaTop = ref(uni.getSystemInfoSync().statusBarHeight + 44)

const meterList = ref([])
const meterIndex = ref(-1)

const form = reactive({
  name: '',
  value: '',
  unit: '',
  startCode: '',
  endCode: '',
  unitPrice: '',
  recordTime: '',
  remark: ''
})

const typeMap = {
  indicator: { title: '指标录入', url: '/entry/indicator' },
  output: { title: '产量录入', url: '/entry/production' },
  meter: { title: '抄表录入', url: '/monitor/meter-record' }
}

// 日期选择器数据
const years = Array.from({length: 5}, (_, i) => (2024 + i) + '年')
const months = Array.from({length: 12}, (_, i) => String(i + 1).padStart(2, '0') + '月')
const days = Array.from({length: 31}, (_, i) => String(i + 1).padStart(2, '0') + '日')
const hours = Array.from({length: 24}, (_, i) => String(i).padStart(2, '0') + '时')
const minutes = Array.from({length: 60}, (_, i) => String(i).padStart(2, '0') + '分')
const dateRange = ref([years, months, days, hours, minutes])
const dateIndex = ref([2, 5, 24, 8, 0])

onLoad((options) => {
  if (options.type && typeMap[options.type]) {
    formType.value = options.type
    formTitle.value = typeMap[options.type].title
  }
  if (formType.value === 'meter') {
    loadMeters()
  }
  // 默认今天
  const d = new Date()
  form.recordTime = `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}:00`
  dateIndex.value = [
    d.getFullYear() - 2024,
    d.getMonth(),
    d.getDate() - 1,
    d.getHours(),
    d.getMinutes()
  ]
})

const loadMeters = async () => {
  try {
    const res = await request.get('/monitor/meter/list', { current: 1, size: 100 })
    if (res.code === 200 && res.data) {
      meterList.value = res.data.records || []
    }
  } catch (e) {
    // 演示数据
    meterList.value = [
      { id: 1, meterName: '1#电表', meterCode: 'D001' },
      { id: 2, meterName: '2#电表', meterCode: 'D002' },
      { id: 3, meterName: '总水表', meterCode: 'S001' }
    ]
  }
}

const onMeterChange = (e) => {
  meterIndex.value = e.detail.value
}

const onDateTimeChange = (e) => {
  const v = e.detail.value
  dateIndex.value = v
  const y = years[v[0]].replace('年', '')
  const m = months[v[1]].replace('月', '')
  const d = days[v[2]].replace('日', '')
  const h = hours[v[3]].replace('时', '')
  const min = minutes[v[4]].replace('分', '')
  form.recordTime = `${y}-${m}-${d} ${h}:${min}:00`
}

const onColumnChange = (e) => {
  // 可在这里处理月份天数联动
}

const handleSubmit = async () => {
  if (formType.value === 'meter') {
    if (meterIndex.value < 0) { uni.showToast({ title: '请选择计量器具', icon: 'none' }); return }
    if (!form.startCode) { uni.showToast({ title: '请输入起始表码', icon: 'none' }); return }
    if (!form.endCode) { uni.showToast({ title: '请输入终止表码', icon: 'none' }); return }
  } else {
    if (!form.name) { uni.showToast({ title: '请输入名称', icon: 'none' }); return }
    if (!form.value) { uni.showToast({ title: '请输入数值', icon: 'none' }); return }
  }
  if (!form.recordTime) { uni.showToast({ title: '请选择日期时间', icon: 'none' }); return }

  loading.value = true
  try {
    let payload, apiUrl
    if (formType.value === 'meter') {
      payload = {
        meterId: meterList.value[meterIndex.value].id,
        startCode: parseFloat(form.startCode),
        endCode: parseFloat(form.endCode),
        unitPrice: form.unitPrice ? parseFloat(form.unitPrice) : null,
        recordTime: form.recordTime,
        remark: form.remark
      }
      apiUrl = '/monitor/meter-record'
    } else {
      // 指标/产量录入：后端暂无专用API，使用本地存储+演示
      const key = formType.value === 'indicator' ? 'indicator_records' : 'output_records'
      const list = uni.getStorageSync(key) || '[]'
      const records = JSON.parse(list)
      records.push({
        id: Date.now(),
        name: form.name,
        value: parseFloat(form.value),
        unit: form.unit || '',
        recordTime: form.recordTime,
        remark: form.remark,
        createTime: new Date().toLocaleString()
      })
      uni.setStorageSync(key, JSON.stringify(records))
      uni.showToast({ title: '提交成功（本地存储）', icon: 'success' })
      setTimeout(() => uni.navigateBack(), 1000)
      return
    }

    const res = await request.post(apiUrl, payload)
    if (res.code === 200) {
      uni.showToast({ title: '提交成功', icon: 'success' })
      setTimeout(() => uni.navigateBack(), 1000)
    } else {
      uni.showToast({ title: res.message || '提交失败', icon: 'none' })
    }
  } catch (e) {
    console.error(e)
    uni.showToast({ title: '提交失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  if (formType.value === 'meter') {
    meterIndex.value = -1
    form.startCode = ''
    form.endCode = ''
    form.unitPrice = ''
  } else {
    form.name = ''
    form.value = ''
    form.unit = ''
  }
  form.remark = ''
}
</script>

<style scoped>
.form-page { min-height: 100vh; background: #f5f7fa; }
.form-content { padding: 24rpx; }
.form-body { background: #ffffff; border-radius: 16rpx; padding: 40rpx; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06); }
.form-group { margin-bottom: 32rpx; }
.form-label { font-size: 28rpx; color: #333; margin-bottom: 12rpx; display: block; font-weight: 500; }
.required { color: #f5222d; }
.form-input { background: #f8f9fa; border-radius: 12rpx; padding: 20rpx 24rpx; font-size: 30rpx; color: #333; }
.picker-view { background: #f8f9fa; border-radius: 12rpx; padding: 20rpx 24rpx; font-size: 30rpx; color: #333; display: flex; justify-content: space-between; align-items: center; }
.picker-placeholder { color: #bbb; }
.form-textarea { background: #f8f9fa; border-radius: 12rpx; padding: 20rpx 24rpx; font-size: 30rpx; color: #333; width: 100%; height: 180rpx; box-sizing: border-box; }
.form-actions { margin-top: 48rpx; display: flex; flex-direction: column; gap: 20rpx; }
.submit-btn { background: linear-gradient(135deg, #1a5f3f, #2d8b5a); color: #fff; font-size: 34rpx; height: 96rpx; line-height: 96rpx; border-radius: 48rpx; border: none; }
.submit-btn::after { border: none; }
.reset-btn { background: #f0f0f0; color: #666; font-size: 32rpx; height: 88rpx; line-height: 88rpx; border-radius: 44rpx; border: none; }
.reset-btn::after { border: none; }
</style>
