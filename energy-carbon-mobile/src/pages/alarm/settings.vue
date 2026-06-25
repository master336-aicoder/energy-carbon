<template>
  <view class="settings-page">
    <nav-bar title="告警订阅设置" showBack></nav-bar>
    <view class="settings-content" :style="{ paddingTop: safeAreaTop + 'px' }">
      <!-- 告警类型开关列表 -->
      <view class="section-card">
        <text class="section-title">告警类型订阅</text>
        <view class="alarm-type-list">
          <view
            v-for="item in alarmTypes"
            :key="item.key"
            class="alarm-type-item"
          >
            <view class="type-left">
              <view class="type-icon" :style="{ background: item.iconBg }">
                <uni-icons :type="item.icon" size="18" :color="item.iconColor"></uni-icons>
              </view>
              <view class="type-info">
                <text class="type-name">{{ item.name }}</text>
                <text class="type-desc">{{ item.desc }}</text>
              </view>
            </view>
            <switch
              :checked="item.enabled"
              color="#1a5f3f"
              @change="toggleAlarmType(item)"
            />
          </view>
        </view>
      </view>

      <!-- 免打扰时段 -->
      <view class="section-card">
        <text class="section-title">免打扰时段</text>
        <view class="disturb-row">
          <view class="disturb-item" @click="showTimePicker('start')">
            <text class="disturb-label">开始时间</text>
            <text class="disturb-value">{{ disturbStart }}</text>
          </view>
          <text class="disturb-sep">至</text>
          <view class="disturb-item" @click="showTimePicker('end')">
            <text class="disturb-label">结束时间</text>
            <text class="disturb-value">{{ disturbEnd }}</text>
          </view>
        </view>
        <view class="disturb-switch-row">
          <text class="disturb-switch-label">启用免打扰</text>
          <switch :checked="disturbEnabled" color="#1a5f3f" @change="disturbEnabled = !disturbEnabled" />
        </view>
      </view>

      <!-- 推送方式 -->
      <view class="section-card">
        <text class="section-title">推送方式</text>
        <view class="push-list">
          <view class="push-item" @click="togglePush('notification')">
            <view class="push-left">
              <uni-icons type="sound" size="20" color="#1a5f3f"></uni-icons>
              <text class="push-name">通知栏推送</text>
            </view>
            <view class="push-check" :class="{ checked: pushMethods.notification }">
              <uni-icons v-if="pushMethods.notification" type="checkmarkempty" size="16" color="#fff"></uni-icons>
            </view>
          </view>
          <view class="push-item" @click="togglePush('sms')">
            <view class="push-left">
              <uni-icons type="chat" size="20" color="#1a5f3f"></uni-icons>
              <text class="push-name">短信推送</text>
            </view>
            <view class="push-check" :class="{ checked: pushMethods.sms }">
              <uni-icons v-if="pushMethods.sms" type="checkmarkempty" size="16" color="#fff"></uni-icons>
            </view>
          </view>
        </view>
      </view>

      <!-- 保存按钮 -->
      <view class="save-section">
        <button class="save-btn" :loading="saving" @click="handleSave">保存设置</button>
      </view>
    </view>

    <!-- 时间选择器弹窗 -->
    <view v-if="timePickerVisible" class="picker-overlay" @click="timePickerVisible = false">
      <view class="picker-content" @click.stop>
        <view class="picker-header">
          <text class="picker-cancel" @click="timePickerVisible = false">取消</text>
          <text class="picker-title">{{ pickerType === 'start' ? '选择开始时间' : '选择结束时间' }}</text>
          <text class="picker-confirm" @click="confirmTime">确定</text>
        </view>
        <picker-view
          :value="pickerValue"
          @change="onPickerChange"
          class="time-picker-view"
          indicator-style="height: 50px;"
        >
          <picker-view-column>
            <view v-for="h in 24" :key="h - 1" class="picker-item">{{ String(h - 1).padStart(2, '0') }}时</view>
          </picker-view-column>
          <picker-view-column>
            <view v-for="m in 60" :key="m - 1" class="picker-item">{{ String(m - 1).padStart(2, '0') }}分</view>
          </picker-view-column>
        </picker-view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import request from '@/utils/request.js'

const safeAreaTop = ref(uni.getSystemInfoSync().statusBarHeight + 44)
const saving = ref(false)

const alarmTypes = ref([
  { key: 'power', name: '用电异常', desc: '用电量超出阈值时告警', icon: 'flashlight', iconBg: '#fff7e6', iconColor: '#fa8c16', enabled: true },
  { key: 'water', name: '用水异常', desc: '用水量超出阈值时告警', icon: 'droplet', iconBg: '#e6f7ff', iconColor: '#1890ff', enabled: true },
  { key: 'carbon', name: '碳排放超标', desc: '碳排放超出配额时告警', icon: 'cloud', iconBg: '#f6ffed', iconColor: '#52c41a', enabled: true },
  { key: 'device', name: '设备故障', desc: '设备运行异常或故障告警', icon: 'settings', iconBg: '#fff2f0', iconColor: '#f5222d', enabled: false },
  { key: 'comm', name: '通信异常', desc: '设备通信中断或超时告警', icon: 'wifi', iconBg: '#f9f0ff', iconColor: '#722ed1', enabled: false }
])

const disturbEnabled = ref(false)
const disturbStart = ref('22:00')
const disturbEnd = ref('08:00')

const pushMethods = ref({
  notification: true,
  sms: false
})

const timePickerVisible = ref(false)
const pickerType = ref('start')
const pickerValue = ref([22, 0])
const pickerHour = ref(22)
const pickerMinute = ref(0)

const toggleAlarmType = (item) => {
  item.enabled = !item.enabled
}

const togglePush = (method) => {
  pushMethods.value[method] = !pushMethods.value[method]
}

const showTimePicker = (type) => {
  pickerType.value = type
  const timeStr = type === 'start' ? disturbStart.value : disturbEnd.value
  const parts = timeStr.split(':')
  pickerHour.value = parseInt(parts[0]) || 0
  pickerMinute.value = parseInt(parts[1]) || 0
  pickerValue.value = [pickerHour.value, pickerMinute.value]
  timePickerVisible.value = true
}

const onPickerChange = (e) => {
  pickerValue.value = e.detail.value
}

const confirmTime = () => {
  const timeStr = String(pickerValue.value[0]).padStart(2, '0') + ':' + String(pickerValue.value[1]).padStart(2, '0')
  if (pickerType.value === 'start') {
    disturbStart.value = timeStr
  } else {
    disturbEnd.value = timeStr
  }
  timePickerVisible.value = false
}

const handleSave = async () => {
  saving.value = true
  try {
    const payload = {
      alarmTypes: alarmTypes.value.filter(t => t.enabled).map(t => t.key),
      disturbEnabled: disturbEnabled.value,
      disturbStart: disturbStart.value,
      disturbEnd: disturbEnd.value,
      pushMethods: Object.keys(pushMethods.value).filter(k => pushMethods.value[k])
    }
    const res = await request.post('/alarm/settings/save', payload)
    if (res.code === 200) {
      uni.showToast({ title: '保存成功', icon: 'success' })
    } else {
      uni.showToast({ title: res.message || '保存失败', icon: 'none' })
    }
  } catch (e) {
    // 离线保存到本地
    uni.setStorageSync('alarmSettings', JSON.stringify({
      alarmTypes: alarmTypes.value,
      disturbEnabled: disturbEnabled.value,
      disturbStart: disturbStart.value,
      disturbEnd: disturbEnd.value,
      pushMethods: pushMethods.value
    }))
    uni.showToast({ title: '设置已保存到本地', icon: 'success' })
  } finally {
    saving.value = false
  }
}

onLoad(() => {
  // 尝试从本地缓存恢复设置
  const cached = uni.getStorageSync('alarmSettings')
  if (cached) {
    try {
      const data = JSON.parse(cached)
      if (data.alarmTypes) {
        data.alarmTypes.forEach(cached => {
          const target = alarmTypes.value.find(t => t.key === cached.key)
          if (target) target.enabled = cached.enabled
        })
      }
      if (data.disturbEnabled !== undefined) disturbEnabled.value = data.disturbEnabled
      if (data.disturbStart) disturbStart.value = data.disturbStart
      if (data.disturbEnd) disturbEnd.value = data.disturbEnd
      if (data.pushMethods) pushMethods.value = { ...pushMethods.value, ...data.pushMethods }
    } catch (e) {}
  }
})
</script>

<style scoped>
.settings-page {
  min-height: 100vh;
  background: #f5f7fa;
}
.settings-content {
  padding: 24rpx;
}
.section-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
  margin-bottom: 24rpx;
}
.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333333;
  display: block;
  margin-bottom: 24rpx;
}
.alarm-type-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}
.alarm-type-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.type-left {
  display: flex;
  align-items: center;
  flex: 1;
}
.type-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
}
.type-info {
  flex: 1;
}
.type-name {
  font-size: 28rpx;
  font-weight: 500;
  color: #333333;
  display: block;
}
.type-desc {
  font-size: 22rpx;
  color: #999999;
  margin-top: 4rpx;
  display: block;
}
.disturb-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 24rpx;
}
.disturb-item {
  flex: 1;
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 24rpx;
  text-align: center;
}
.disturb-label {
  font-size: 22rpx;
  color: #999999;
  display: block;
  margin-bottom: 8rpx;
}
.disturb-value {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
}
.disturb-sep {
  font-size: 28rpx;
  color: #999999;
}
.disturb-switch-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 16rpx;
  border-top: 1rpx solid #f0f0f0;
}
.disturb-switch-label {
  font-size: 26rpx;
  color: #666666;
}
.push-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}
.push-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16rpx 0;
}
.push-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}
.push-name {
  font-size: 28rpx;
  color: #333333;
}
.push-check {
  width: 44rpx;
  height: 44rpx;
  border-radius: 50%;
  border: 2rpx solid #d9d9d9;
  display: flex;
  align-items: center;
  justify-content: center;
}
.push-check.checked {
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  border-color: #1a5f3f;
}
.save-section {
  margin-bottom: 60rpx;
}
.save-btn {
  width: 100%;
  height: 96rpx;
  line-height: 96rpx;
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  color: #ffffff;
  font-size: 32rpx;
  border-radius: 48rpx;
  border: none;
  box-shadow: 0 4rpx 16rpx rgba(26, 95, 63, 0.3);
}
.save-btn::after {
  border: none;
}
/* 时间选择器 */
.picker-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
  display: flex;
  align-items: flex-end;
}
.picker-content {
  background: #ffffff;
  border-radius: 24rpx 24rpx 0 0;
  width: 100%;
  padding-bottom: env(safe-area-inset-bottom);
}
.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28rpx 32rpx;
  border-bottom: 1rpx solid #f0f0f0;
}
.picker-cancel {
  font-size: 28rpx;
  color: #999999;
}
.picker-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333333;
}
.picker-confirm {
  font-size: 28rpx;
  color: #1a5f3f;
  font-weight: 500;
}
.time-picker-view {
  height: 400rpx;
}
.picker-item {
  height: 50px;
  line-height: 50px;
  text-align: center;
  font-size: 32rpx;
  color: #333333;
}
</style>
