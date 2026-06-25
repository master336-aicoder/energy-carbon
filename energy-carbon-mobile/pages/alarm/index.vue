<template>
  <view class="alarm-page">
    <view class="alarm-filter">
      <view
        class="filter-item"
        :class="{ active: status === 'all' }"
        @click="changeStatus('all')"
      >
        <text>全部</text>
      </view>
      <view
        class="filter-item"
        :class="{ active: status === 'pending' }"
        @click="changeStatus('pending')"
      >
        <text>未处理</text>
      </view>
      <view
        class="filter-item"
        :class="{ active: status === 'handled' }"
        @click="changeStatus('handled')"
      >
        <text>已处理</text>
      </view>
    </view>

    <view class="alarm-list">
      <view
        v-for="(item, index) in alarmList"
        :key="index"
        class="alarm-card"
        @click="goToDetail(item)"
      >
        <view class="alarm-header">
          <view class="alarm-level" :class="'level-' + (item.level || 'low')">
            <text>{{ getLevelText(item.level) }}</text>
          </view>
          <text class="alarm-time">{{ item.createTime }}</text>
        </view>
        <view class="alarm-body">
          <text class="alarm-title">{{ item.title || item.alarmName }}</text>
          <text class="alarm-desc">{{ item.content || item.description }}</text>
        </view>
        <view class="alarm-footer">
          <text class="alarm-source">{{ item.source || item.deviceName }}</text>
          <view class="alarm-status" :class="{ handled: item.status === 'handled' || item.status === 1 }">
            <text>{{ item.status === 'handled' || item.status === 1 ? '已处理' : '未处理' }}</text>
          </view>
        </view>
      </view>

      <view v-if="alarmList.length === 0" class="empty-state">
        <uni-icons type="info" size="48" color="#cccccc"></uni-icons>
        <text>暂无告警记录</text>
      </view>
    </view>

    <uni-load-more
      v-if="alarmList.length > 0"
      :status="loadStatus"
      @clickLoadMore="loadMore"
    ></uni-load-more>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import request from '@/utils/request.js'

const status = ref('all')
const alarmList = ref([])
const current = ref(1)
const size = ref(10)
const loadStatus = ref('more')

const getLevelText = (level) => {
  const map = { high: '高', medium: '中', low: '低', critical: '紧急' }
  return map[level] || '低'
}

const loadData = async (refresh = true) => {
  if (refresh) {
    current.value = 1
    alarmList.value = []
  }
  loadStatus.value = 'loading'
  try {
    const params = { current: current.value, size: size.value }
    if (status.value !== 'all') {
      params.status = status.value === 'pending' ? 0 : 1
    }
    const res = await request.get('/alarm/record/list', params)
    if (res.code === 200 && res.data) {
      const records = res.data.records || []
      if (refresh) {
        alarmList.value = records
      } else {
        alarmList.value = alarmList.value.concat(records)
      }
      if (records.length < size.value) {
        loadStatus.value = 'noMore'
      } else {
        loadStatus.value = 'more'
      }
    } else {
      loadStatus.value = 'noMore'
    }
  } catch (e) {
    loadStatus.value = 'more'
    // 演示数据
    alarmList.value = [
      { id: 1, title: '温度过高告警', content: '1#车间温度超过设定阈值35°C', level: 'high', source: '1#温度传感器', status: 0, createTime: '2026-06-25 10:30' },
      { id: 2, title: '能耗异常告警', content: '2#电表读数异常波动', level: 'medium', source: '2#电表', status: 0, createTime: '2026-06-25 09:15' },
      { id: 3, title: '设备离线', content: '3#空调设备通信中断', level: 'low', source: '3#空调', status: 1, createTime: '2026-06-24 18:20' }
    ]
  }
}

const changeStatus = (val) => {
  status.value = val
  loadData(true)
}

const loadMore = () => {
  if (loadStatus.value !== 'more') return
  current.value++
  loadData(false)
}

const goToDetail = (item) => {
  uni.navigateTo({ url: '/pages/alarm/detail?id=' + item.id })
}

onMounted(() => {
  loadData()
})

onShow(() => {
  loadData(true)
})
</script>

<style scoped>
.alarm-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24rpx;
}
.alarm-filter {
  display: flex;
  gap: 16rpx;
  margin-bottom: 24rpx;
}
.filter-item {
  padding: 12rpx 32rpx;
  background: #ffffff;
  border-radius: 32rpx;
  font-size: 26rpx;
  color: #666666;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}
.filter-item.active {
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  color: #ffffff;
}
.alarm-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}
.alarm-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}
.alarm-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}
.alarm-level {
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
  color: #ffffff;
}
.alarm-level.level-high, .alarm-level.level-critical {
  background: #f5222d;
}
.alarm-level.level-medium {
  background: #faad14;
}
.alarm-level.level-low {
  background: #52c41a;
}
.alarm-time {
  font-size: 22rpx;
  color: #999999;
}
.alarm-body {
  margin-bottom: 16rpx;
}
.alarm-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333333;
  display: block;
  margin-bottom: 8rpx;
}
.alarm-desc {
  font-size: 26rpx;
  color: #666666;
}
.alarm-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1rpx solid #f0f0f0;
  padding-top: 16rpx;
}
.alarm-source {
  font-size: 24rpx;
  color: #999999;
}
.alarm-status {
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
  background: #fff2f0;
  color: #f5222d;
}
.alarm-status.handled {
  background: #f6ffed;
  color: #52c41a;
}
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx 0;
  color: #999999;
  font-size: 28rpx;
}
</style>
