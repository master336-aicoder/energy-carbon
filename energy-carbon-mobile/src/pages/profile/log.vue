<template>
  <view class="log-page">
    <!-- 筛选栏 -->
    <view class="filter-row">
      <view class="filter-date" @click="showDateStart = true">
        <uni-icons type="calendar" size="14" color="#1a5f3f"></uni-icons>
        <text class="filter-date-text">{{ dateStart || '开始日期' }}</text>
      </view>
      <text class="filter-sep">至</text>
      <view class="filter-date" @click="showDateEnd = true">
        <uni-icons type="calendar" size="14" color="#1a5f3f"></uni-icons>
        <text class="filter-date-text">{{ dateEnd || '结束日期' }}</text>
      </view>
    </view>

    <view class="type-filter-row">
      <picker :range="typeOptions" range-key="label" @change="onTypeChange">
        <view class="type-picker">
          <text>{{ selectedTypeLabel }}</text>
          <uni-icons type="bottom" size="14" color="#999"></uni-icons>
        </view>
      </picker>
      <view class="reset-btn" @click="resetFilter">
        <text>重置</text>
      </view>
    </view>

    <!-- 日志列表 -->
    <scroll-view
      scroll-y
      class="log-scroll"
      @scrolltolower="loadMore"
    >
      <view
        v-for="(item, index) in filteredList"
        :key="item.id"
        class="log-item"
      >
        <view class="log-left">
          <view class="log-dot" :style="{ background: getTypeColor(item.type) }"></view>
          <view v-if="index < filteredList.length - 1" class="log-line"></view>
        </view>
        <view class="log-right">
          <view class="log-header">
            <view class="log-type-tag" :style="{ background: getTypeBg(item.type), color: getTypeColor(item.type) }">
              <text>{{ item.typeName }}</text>
            </view>
            <text class="log-time">{{ item.time }}</text>
          </view>
          <text class="log-content">{{ item.content }}</text>
          <text class="log-ip">IP: {{ item.ip }}</text>
        </view>
      </view>

      <view v-if="filteredList.length === 0" class="empty-state">
        <uni-icons type="info" size="48" color="#cccccc"></uni-icons>
        <text class="empty-text">暂无操作日志</text>
      </view>

      <uni-load-more
        v-if="filteredList.length > 0"
        :status="loadStatus"
      ></uni-load-more>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const dateStart = ref('')
const dateEnd = ref('')
const selectedType = ref('')
const showDateStart = ref(false)
const showDateEnd = ref(false)

const typeOptions = [
  { label: '全部类型', value: '' },
  { label: '登录', value: 'login' },
  { label: '数据录入', value: 'entry' },
  { label: '告警处理', value: 'alarm' },
  { label: '报表导出', value: 'export' },
  { label: '设备操作', value: 'device' },
  { label: '系统设置', value: 'settings' }
]

const selectedTypeLabel = computed(() => {
  const opt = typeOptions.find(t => t.value === selectedType.value)
  return opt ? opt.label : '全部类型'
})

const logList = ref([])
const pageSize = 20
const loadStatus = ref('noMore')

const demoLogs = [
  { id: 1, type: 'login', typeName: '登录', content: '用户登录系统', time: '2026-06-26 09:00:12', ip: '192.168.1.100' },
  { id: 2, type: 'entry', typeName: '数据录入', content: '录入1#空压机电表数据：1256 kWh', time: '2026-06-26 09:15:30', ip: '192.168.1.100' },
  { id: 3, type: 'alarm', typeName: '告警处理', content: '处理温度过高告警 #20260625001，已恢复正常', time: '2026-06-26 09:32:45', ip: '192.168.1.100' },
  { id: 4, type: 'export', typeName: '报表导出', content: '导出6月份能耗统计报表（PDF）', time: '2026-06-25 16:20:18', ip: '192.168.1.100' },
  { id: 5, type: 'device', typeName: '设备操作', content: '远程重启3#空调机组', time: '2026-06-25 15:10:22', ip: '192.168.1.100' },
  { id: 6, type: 'entry', typeName: '数据录入', content: '录入碳排放因子数据：天然气 2.16 kgCO2/m3', time: '2026-06-25 14:30:55', ip: '192.168.1.100' },
  { id: 7, type: 'settings', typeName: '系统设置', content: '修改告警订阅设置：开启短信推送', time: '2026-06-25 11:00:08', ip: '192.168.1.100' },
  { id: 8, type: 'login', typeName: '登录', content: '用户登录系统', time: '2026-06-25 08:55:30', ip: '192.168.1.101' },
  { id: 9, type: 'alarm', typeName: '告警处理', content: '处理能耗异常告警 #20260624003，已调整阈值', time: '2026-06-24 17:45:12', ip: '192.168.1.100' },
  { id: 10, type: 'entry', typeName: '数据录入', content: '批量录入2#冷却塔运行数据，共12条', time: '2026-06-24 14:20:38', ip: '192.168.1.100' },
  { id: 11, type: 'device', typeName: '设备操作', content: '修改1#空压机运行参数：功率上限 85%', time: '2026-06-24 10:15:20', ip: '192.168.1.100' },
  { id: 12, type: 'export', typeName: '报表导出', content: '导出Q2碳排放趋势报表（Excel）', time: '2026-06-23 16:30:00', ip: '192.168.1.100' },
  { id: 13, type: 'login', typeName: '登录', content: '用户登录系统', time: '2026-06-23 09:02:15', ip: '192.168.1.100' },
  { id: 14, type: 'settings', typeName: '系统设置', content: '修改密码', time: '2026-06-22 15:45:30', ip: '192.168.1.100' },
  { id: 15, type: 'alarm', typeName: '告警处理', content: '处理设备离线告警 #20260622005，已恢复通信', time: '2026-06-22 11:20:45', ip: '192.168.1.100' }
]

const filteredList = computed(() => {
  let list = logList.value
  if (selectedType.value) {
    list = list.filter(l => l.type === selectedType.value)
  }
  if (dateStart.value) {
    list = list.filter(l => l.time >= dateStart.value)
  }
  if (dateEnd.value) {
    list = list.filter(l => l.time <= dateEnd.value + ' 23:59:59')
  }
  return list
})

const getTypeColor = (type) => {
  const map = { login: '#1890ff', entry: '#1a5f3f', alarm: '#f5222d', export: '#722ed1', device: '#fa8c16', settings: '#999999' }
  return map[type] || '#999999'
}

const getTypeBg = (type) => {
  const map = { login: '#e6f7ff', entry: '#e8f5e9', alarm: '#fff2f0', export: '#f9f0ff', device: '#fff7e6', settings: '#f5f5f5' }
  return map[type] || '#f5f5f5'
}

const onTypeChange = (e) => {
  selectedType.value = typeOptions[e.detail.value].value
}

const resetFilter = () => {
  dateStart.value = ''
  dateEnd.value = ''
  selectedType.value = ''
}

const loadMore = () => {
  if (loadStatus.value !== 'more') return
  loadStatus.value = 'noMore'
}

const loadLogs = () => {
  const cached = uni.getStorageSync('operationLogs')
  if (cached) {
    try {
      const data = JSON.parse(cached)
      if (data && data.length > 0) {
        logList.value = data
        return
      }
    } catch (e) {}
  }
  logList.value = [...demoLogs]
  uni.setStorageSync('operationLogs', JSON.stringify(demoLogs))
}

onLoad(() => {
  loadLogs()
})
</script>

<style scoped>
.log-page {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}
.filter-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 24rpx 24rpx 12rpx;
}
.filter-date {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8rpx;
  background: #ffffff;
  border-radius: 12rpx;
  padding: 16rpx 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}
.filter-date-text {
  flex: 1;
  font-size: 26rpx;
  color: #333333;
}
.filter-sep {
  font-size: 26rpx;
  color: #999999;
}
.type-filter-row {
  display: flex;
  gap: 16rpx;
  padding: 0 24rpx 16rpx;
}
.type-picker {
  display: flex;
  align-items: center;
  gap: 8rpx;
  background: #ffffff;
  border-radius: 12rpx;
  padding: 16rpx 20rpx;
  font-size: 26rpx;
  color: #333333;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}
.reset-btn {
  padding: 16rpx 32rpx;
  background: #ffffff;
  border-radius: 12rpx;
  font-size: 26rpx;
  color: #f5222d;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}
.log-scroll {
  flex: 1;
  height: 0;
  padding: 0 24rpx 24rpx;
}
.log-item {
  display: flex;
  gap: 20rpx;
  margin-bottom: 0;
}
.log-left {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 24rpx;
  flex-shrink: 0;
}
.log-dot {
  width: 24rpx;
  height: 24rpx;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 16rpx;
}
.log-line {
  width: 2rpx;
  flex: 1;
  background: #e8e8e8;
  min-height: 20rpx;
}
.log-right {
  flex: 1;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
  margin-bottom: 16rpx;
}
.log-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}
.log-type-tag {
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
  font-weight: 500;
}
.log-time {
  font-size: 22rpx;
  color: #999999;
}
.log-content {
  font-size: 28rpx;
  color: #333333;
  line-height: 1.6;
  display: block;
  margin-bottom: 8rpx;
}
.log-ip {
  font-size: 22rpx;
  color: #cccccc;
}
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0;
}
.empty-text {
  color: #999999;
  font-size: 28rpx;
  margin-top: 16rpx;
}
</style>
