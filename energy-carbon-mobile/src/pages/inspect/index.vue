<template>
  <view class="inspect-page">
    <nav-bar title="设备巡检" showBack></nav-bar>
    <!-- 统计卡片 -->
    <view class="stat-card">
      <view class="stat-item">
        <text class="stat-num">{{ stats.pending }}</text>
        <text class="stat-label">今日待巡检</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <text class="stat-num stat-done">{{ stats.completed }}</text>
        <text class="stat-label">已完成</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <text class="stat-num stat-error">{{ stats.abnormal }}</text>
        <text class="stat-label">异常数</text>
      </view>
    </view>

    <!-- Tab切换 -->
    <view class="tab-bar">
      <view
        class="tab-item"
        :class="{ active: activeTab === 'today' }"
        @click="switchTab('today')"
      >
        <text>今日</text>
      </view>
      <view
        class="tab-item"
        :class="{ active: activeTab === 'week' }"
        @click="switchTab('week')"
      >
        <text>本周</text>
      </view>
      <view
        class="tab-item"
        :class="{ active: activeTab === 'all' }"
        @click="switchTab('all')"
      >
        <text>全部</text>
      </view>
    </view>

    <!-- 任务列表 -->
    <view class="task-list">
      <view
        v-for="item in taskList"
        :key="item.id"
        class="task-card"
        @click="goExecute(item)"
      >
        <view class="task-header">
          <text class="task-device">{{ item.deviceName }}</text>
          <view class="task-status" :class="'status-' + item.status">
            <text>{{ getStatusText(item.status) }}</text>
          </view>
        </view>
        <view class="task-body">
          <view class="task-info">
            <text class="info-label">巡检模板</text>
            <text class="info-value">{{ item.templateName }}</text>
          </view>
          <view class="task-info">
            <text class="info-label">计划时间</text>
            <text class="info-value">{{ item.planTime }}</text>
          </view>
        </view>
        <view class="task-footer">
          <text class="task-operator">负责人：{{ item.operator }}</text>
          <text class="task-arrow">></text>
        </view>
      </view>

      <view v-if="taskList.length === 0" class="empty-state">
        <text class="empty-icon">&#x1f50d;</text>
        <text class="empty-text">暂无巡检任务</text>
      </view>
    </view>

    <!-- 底部按钮 -->
    <view class="bottom-actions">
      <button class="action-btn secondary" @click="goHistory">巡检历史</button>
      <button class="action-btn primary" @click="goStats">巡检统计</button>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import request from '@/utils/request.js'

const activeTab = ref('today')
const taskList = ref([])
const stats = reactive({
  pending: 0,
  completed: 0,
  abnormal: 0
})

const getStatusText = (status) => {
  const map = { pending: '待巡检', doing: '进行中', completed: '已完成', overdue: '已逾期' }
  return map[status] || '待巡检'
}

const switchTab = (tab) => {
  activeTab.value = tab
  loadTasks()
}

const loadTasks = async () => {
  try {
    const params = { tab: activeTab.value }
    const res = await request.get('/monitor/inspect-task/list', params)
    if (res.code === 200 && res.data) {
      taskList.value = res.data.records || res.data || []
      calcStats()
      return
    }
  } catch (e) {
    console.error('加载巡检任务失败:', e)
  }
  // 演示数据
  taskList.value = [
    { id: 1, deviceName: '1#空压机', templateName: '空压机日常巡检', planTime: '2026-06-26 08:30', status: 'pending', operator: '张三' },
    { id: 2, deviceName: '2#冷却塔', templateName: '冷却塔巡检标准', planTime: '2026-06-26 09:00', status: 'doing', operator: '李四' },
    { id: 3, deviceName: '3#配电柜', templateName: '配电设备巡检', planTime: '2026-06-26 10:00', status: 'completed', operator: '王五' },
    { id: 4, deviceName: '4#锅炉', templateName: '锅炉安全巡检', planTime: '2026-06-25 14:00', status: 'overdue', operator: '赵六' },
    { id: 5, deviceName: '5#水泵房', templateName: '水泵日常巡检', planTime: '2026-06-26 11:00', status: 'pending', operator: '张三' },
    { id: 6, deviceName: '6#空调机组', templateName: '空调设备巡检', planTime: '2026-06-26 13:00', status: 'pending', operator: '李四' }
  ]
  calcStats()
}

const calcStats = () => {
  const todayList = taskList.value
  stats.pending = todayList.filter(i => i.status === 'pending' || i.status === 'overdue').length
  stats.completed = todayList.filter(i => i.status === 'completed').length
  stats.abnormal = todayList.filter(i => i.status === 'overdue').length
}

const goExecute = (item) => {
  uni.navigateTo({ url: '/pages/inspect/execute?id=' + item.id })
}

const goHistory = () => {
  uni.navigateTo({ url: '/pages/inspect/history' })
}

const goStats = () => {
  uni.navigateTo({ url: '/pages/inspect/stats' })
}

onShow(() => {
  loadTasks()
})
</script>

<style scoped>
.inspect-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 160rpx;
}
.stat-card {
  margin: 24rpx;
  padding: 32rpx 0;
  background: #ffffff;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06);
  display: flex;
  align-items: center;
  justify-content: space-around;
}
.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.stat-num {
  font-size: 48rpx;
  font-weight: 700;
  color: #1a5f3f;
}
.stat-num.stat-done {
  color: #52c41a;
}
.stat-num.stat-error {
  color: #f5222d;
}
.stat-label {
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
}
.stat-divider {
  width: 1rpx;
  height: 60rpx;
  background: #e8e8e8;
}
.tab-bar {
  display: flex;
  gap: 16rpx;
  margin: 0 24rpx 24rpx;
}
.tab-item {
  flex: 1;
  text-align: center;
  padding: 16rpx 0;
  background: #ffffff;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #666;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
}
.tab-item.active {
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  color: #ffffff;
}
.task-list {
  padding: 0 24rpx;
}
.task-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06);
}
.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}
.task-device {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
}
.task-status {
  padding: 6rpx 20rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
}
.task-status.status-pending {
  background: #fff7e6;
  color: #faad14;
}
.task-status.status-doing {
  background: #e6f7ff;
  color: #1890ff;
}
.task-status.status-completed {
  background: #f6ffed;
  color: #52c41a;
}
.task-status.status-overdue {
  background: #fff2f0;
  color: #f5222d;
}
.task-body {
  margin-bottom: 16rpx;
}
.task-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8rpx;
}
.info-label {
  font-size: 26rpx;
  color: #999;
}
.info-value {
  font-size: 26rpx;
  color: #333;
}
.task-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1rpx solid #f0f0f0;
  padding-top: 16rpx;
}
.task-operator {
  font-size: 24rpx;
  color: #999;
}
.task-arrow {
  font-size: 28rpx;
  color: #ccc;
}
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0;
}
.empty-icon {
  font-size: 80rpx;
  margin-bottom: 16rpx;
}
.empty-text {
  font-size: 28rpx;
  color: #999;
}
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx 24rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  background: #ffffff;
  box-shadow: 0 -4rpx 16rpx rgba(0,0,0,0.06);
  display: flex;
  gap: 20rpx;
}
.action-btn {
  flex: 1;
  height: 88rpx;
  line-height: 88rpx;
  text-align: center;
  border-radius: 44rpx;
  font-size: 30rpx;
  border: none;
}
.action-btn::after {
  border: none;
}
.action-btn.primary {
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  color: #ffffff;
}
.action-btn.secondary {
  background: #f5f7fa;
  color: #1a5f3f;
  border: 2rpx solid #1a5f3f;
}
</style>
