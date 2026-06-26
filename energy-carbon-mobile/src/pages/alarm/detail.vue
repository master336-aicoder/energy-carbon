<template>
  <view class="detail-page">
    <nav-bar title="告警详情" showBack></nav-bar>
    <view class="detail-content">
      <view class="detail-header">
        <view class="detail-level" :class="'level-' + (alarm.level || 'low')">
          <text>{{ getLevelText(alarm.level) }}</text>
        </view>
        <text class="detail-status" :class="{ handled: isHandled }">{{ isHandled ? '已处理' : '未处理' }}</text>
      </view>

      <view class="detail-title">{{ alarm.title || alarm.alarmName }}</view>
      <view class="detail-desc">{{ alarm.content || alarm.description }}</view>

      <view class="detail-section">
        <view class="detail-row">
          <text class="detail-label">告警来源</text>
          <text class="detail-value">{{ alarm.source || alarm.deviceName }}</text>
        </view>
        <view class="detail-row">
          <text class="detail-label">告警时间</text>
          <text class="detail-value">{{ alarm.createTime }}</text>
        </view>
        <view class="detail-row">
          <text class="detail-label">处理人</text>
          <text class="detail-value">{{ alarm.handler || '-' }}</text>
        </view>
        <view class="detail-row">
          <text class="detail-label">处理时间</text>
          <text class="detail-value">{{ alarm.handleTime || '-' }}</text>
        </view>
      </view>

      <view class="detail-section">
        <text class="section-title">处理备注</text>
        <view v-if="alarm.handleRemark" class="remark-text">{{ alarm.handleRemark }}</view>
        <view v-else class="remark-empty">暂无处理备注</view>
      </view>

      <view v-if="!isHandled" class="detail-actions">
        <button class="handle-btn" @click="showHandleDialog = true">处理告警</button>
      </view>
    </view>

    <uni-popup ref="popup" type="dialog" v-model:show="showHandleDialog">
      <uni-popup-dialog
        mode="input"
        title="处理告警"
        placeholder="请输入处理备注"
        @confirm="handleAlarm"
        @close="showHandleDialog = false"
      ></uni-popup-dialog>
    </uni-popup>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import request from '@/utils/request.js'

const alarm = ref({})
const isHandled = ref(false)
const showHandleDialog = ref(false)
const popup = ref(null)

const getLevelText = (level) => {
  const map = { high: '高', medium: '中', low: '低', critical: '紧急' }
  return map[level] || '低'
}

const loadDetail = async (id) => {
  try {
    const res = await request.get('/alarm/record/' + id)
    if (res.code === 200 && res.data) {
      const typeMap = { THRESHOLD_EXCEED: '阈值超限', EFFICIENCY_LOW: '效率偏低', COMMUNICATION_ERROR: '通信异常', DEVICE_OFFLINE: '设备离线' }
      const levelMap = { HIGH: 'high', MEDIUM: 'medium', LOW: 'low', CRITICAL: 'critical' }
      const item = res.data
      alarm.value = {
        id: item.id,
        title: item.thresholdName || typeMap[item.alarmType] || item.alarmType || '告警',
        content: item.alarmType ? `告警值: ${item.alarmValue} (阈值: ${item.thresholdValue})` : (item.content || ''),
        level: levelMap[item.alarmLevel] || item.level || 'low',
        source: item.meterName || `设备${item.meterId || ''}` || '',
        status: item.handleStatus === 'PROCESSED' || item.handleStatus === 'handled' || item.handleStatus === '1' || item.handleStatus === 1 ? 1 : 0,
        createTime: item.alarmTime || item.createTime || '',
        handler: item.handler || '',
        handleTime: item.handleTime || '',
        handleRemark: item.handleRemark || '',
        alarmValue: item.alarmValue,
        thresholdValue: item.thresholdValue
      }
      isHandled.value = alarm.value.status === 1
    }
  } catch (e) {
    console.error('加载告警详情失败:', e)
    // 演示数据
    alarm.value = { id, title: '温度过高告警', content: '1#车间温度超过设定阈值35°C', level: 'high', source: '1#温度传感器', status: 0, createTime: '2026-06-25 10:30' }
    isHandled.value = false
  }
}

const handleAlarm = async (val) => {
  try {
    const res = await request.put('/alarm/record/' + alarm.value.id + '/handle', { remark: val })
    if (res.code === 200) {
      uni.showToast({ title: '处理成功', icon: 'success' })
      isHandled.value = true
      alarm.value.status = 1
      alarm.value.handleRemark = val
      alarm.value.handler = '当前用户'
      alarm.value.handleTime = new Date().toLocaleString()
    } else {
      uni.showToast({ title: res.message || '处理失败', icon: 'none' })
    }
  } catch (e) {
    uni.showToast({ title: '处理失败', icon: 'none' })
  }
  showHandleDialog.value = false
}

onLoad((options) => {
  if (options.id) {
    loadDetail(options.id)
  }
})
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24rpx;
}
.detail-content {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 40rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}
.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}
.detail-level {
  padding: 6rpx 20rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
  color: #ffffff;
}
.detail-level.level-high, .detail-level.level-critical {
  background: #f5222d;
}
.detail-level.level-medium {
  background: #faad14;
}
.detail-level.level-low {
  background: #52c41a;
}
.detail-status {
  padding: 6rpx 20rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
  background: #fff2f0;
  color: #f5222d;
}
.detail-status.handled {
  background: #f6ffed;
  color: #52c41a;
}
.detail-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #333333;
  margin-bottom: 16rpx;
}
.detail-desc {
  font-size: 28rpx;
  color: #666666;
  margin-bottom: 32rpx;
  line-height: 1.6;
}
.detail-section {
  border-top: 1rpx solid #f0f0f0;
  padding-top: 24rpx;
  margin-bottom: 24rpx;
}
.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
}
.detail-label {
  font-size: 28rpx;
  color: #999999;
}
.detail-value {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
}
.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 16rpx;
  display: block;
}
.remark-text {
  font-size: 28rpx;
  color: #666666;
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 20rpx;
  line-height: 1.6;
}
.remark-empty {
  font-size: 28rpx;
  color: #999999;
  text-align: center;
  padding: 40rpx 0;
}
.detail-actions {
  margin-top: 40rpx;
}
.handle-btn {
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  color: #ffffff;
  font-size: 34rpx;
  height: 96rpx;
  line-height: 96rpx;
  border-radius: 48rpx;
  border: none;
}
.handle-btn::after {
  border: none;
}
</style>
