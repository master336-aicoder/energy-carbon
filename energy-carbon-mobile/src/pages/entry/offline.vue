<template>
  <view class="offline-page">
    <nav-bar title="离线数据管理" showBack></nav-bar>
    <!-- 状态栏 -->
    <view class="status-bar">
      <view class="status-item">
        <text class="status-label">本地缓存</text>
        <text class="status-value">{{ cacheCount }} 条</text>
      </view>
      <view class="status-divider"></view>
      <view class="status-item">
        <text class="status-label">同步状态</text>
        <view class="sync-status" :class="syncStatus">
          <text>{{ syncStatusText }}</text>
        </view>
      </view>
    </view>

    <!-- 操作按钮 -->
    <view class="action-bar">
      <button class="action-btn" @click="syncAll" :loading="syncing">
        {{ syncing ? '同步中...' : '全部同步' }}
      </button>
      <button class="action-btn danger" @click="clearCache">清除本地缓存</button>
    </view>

    <!-- Tab -->
    <view class="tab-bar">
      <view class="tab-item" :class="{ active: activeTab === 'pending' }" @click="switchTab('pending')">
        <text>待同步（{{ pendingCount }}）</text>
      </view>
      <view class="tab-item" :class="{ active: activeTab === 'failed' }" @click="switchTab('failed')">
        <text>同步失败（{{ failedCount }}）</text>
      </view>
    </view>

    <!-- 待同步列表 -->
    <view class="data-list" v-if="activeTab === 'pending'">
      <view v-for="item in pendingList" :key="item.id" class="data-card">
        <view class="data-header">
          <text class="data-type">{{ item.typeLabel }}</text>
          <text class="data-time">{{ item.createTime }}</text>
        </view>
        <view class="data-body">
          <text class="data-desc">{{ item.description }}</text>
        </view>
        <view class="data-footer">
          <button class="sync-single-btn" :loading="item.syncing" @click="syncSingle(item)">
            同步
          </button>
        </view>
      </view>
      <view v-if="pendingList.length === 0" class="empty-state">
        <text class="empty-text">暂无待同步数据</text>
      </view>
    </view>

    <!-- 同步失败列表 -->
    <view class="data-list" v-if="activeTab === 'failed'">
      <view v-for="item in failedList" :key="item.id" class="data-card failed">
        <view class="data-header">
          <text class="data-type">{{ item.typeLabel }}</text>
          <text class="data-time">{{ item.createTime }}</text>
        </view>
        <view class="data-body">
          <text class="data-desc">{{ item.description }}</text>
          <text class="error-msg">失败原因：{{ item.errorMsg }}</text>
        </view>
        <view class="data-footer">
          <button class="retry-btn" :loading="item.syncing" @click="retryItem(item)">
            重试
          </button>
          <button class="delete-btn" @click="deleteItem(item)">
            删除
          </button>
        </view>
      </view>
      <view v-if="failedList.length === 0" class="empty-state">
        <text class="empty-text">暂无失败记录</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import request from '@/utils/request.js'

const activeTab = ref('pending')
const syncing = ref(false)
const syncStatus = ref('idle')
const syncStatusText = ref('未同步')

const pendingList = ref([])
const failedList = ref([])

const cacheCount = computed(() => pendingList.value.length + failedList.value.length)
const pendingCount = computed(() => pendingList.value.length)
const failedCount = computed(() => failedList.value.length)

const switchTab = (tab) => {
  activeTab.value = tab
}

const loadOfflineData = () => {
  // 读取本地缓存
  try {
    const pending = JSON.parse(uni.getStorageSync('offline_pending') || '[]')
    const failed = JSON.parse(uni.getStorageSync('offline_failed') || '[]')
    if (pending.length || failed.length) {
      pendingList.value = pending
      failedList.value = failed
      updateSyncStatus()
      return
    }
  } catch (e) {
    console.error('读取本地缓存失败:', e)
  }
  // 演示数据
  pendingList.value = [
    { id: 1, typeLabel: '抄表记录', description: '1#电表 - 起始表码:12580 终止表码:13200', createTime: '2026-06-26 08:30', syncing: false, url: '/monitor/meter-record' },
    { id: 2, typeLabel: '指标记录', description: '单位产品能耗 - 0.045 tce/件', createTime: '2026-06-26 09:15', syncing: false, url: '/monitor/indicator/entry' },
    { id: 3, typeLabel: '巡检记录', description: '1#空压机巡检 - 8项全部正常', createTime: '2026-06-26 08:45', syncing: false, url: '/monitor/inspect-task/submit' },
    { id: 4, typeLabel: '采购记录', description: '电力采购 - 50000 kWh - 42500元', createTime: '2026-06-25 10:00', syncing: false, url: '/monitor/energy-purchase' },
    { id: 5, typeLabel: '产量记录', description: 'A线产量 - 2800件', createTime: '2026-06-26 18:00', syncing: false, url: '/monitor/output/entry' }
  ]
  failedList.value = [
    { id: 101, typeLabel: '抄表记录', description: '2#水表 - 起始表码:5600 终止表码:5820', createTime: '2026-06-24 16:00', errorMsg: '网络连接超时', syncing: false, url: '/monitor/meter-record' },
    { id: 102, typeLabel: '巡检记录', description: '4#锅炉巡检 - 发现2项异常', createTime: '2026-06-23 14:30', errorMsg: '服务器内部错误(500)', syncing: false, url: '/monitor/inspect-task/submit' }
  ]
  updateSyncStatus()
}

const updateSyncStatus = () => {
  if (pendingList.value.length > 0) {
    syncStatus.value = 'pending'
    syncStatusText.value = '待同步'
  } else if (failedList.value.length > 0) {
    syncStatus.value = 'error'
    syncStatusText.value = '有失败记录'
  } else {
    syncStatus.value = 'synced'
    syncStatusText.value = '已全部同步'
  }
}

const syncSingle = async (item) => {
  item.syncing = true
  try {
    // 模拟同步
    await new Promise(resolve => setTimeout(resolve, 1500))
    pendingList.value = pendingList.value.filter(i => i.id !== item.id)
    savePendingToLocal()
    updateSyncStatus()
    uni.showToast({ title: '同步成功', icon: 'success' })
  } catch (e) {
    item.syncing = false
    uni.showToast({ title: '同步失败', icon: 'none' })
  } finally {
    item.syncing = false
  }
}

const retryItem = async (item) => {
  item.syncing = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1500))
    failedList.value = failedList.value.filter(i => i.id !== item.id)
    saveFailedToLocal()
    updateSyncStatus()
    uni.showToast({ title: '重试成功', icon: 'success' })
  } catch (e) {
    item.syncing = false
    uni.showToast({ title: '重试失败', icon: 'none' })
  } finally {
    item.syncing = false
  }
}

const deleteItem = (item) => {
  uni.showModal({
    title: '确认删除',
    content: '确定删除该失败记录？',
    success: (res) => {
      if (res.confirm) {
        failedList.value = failedList.value.filter(i => i.id !== item.id)
        saveFailedToLocal()
        updateSyncStatus()
        uni.showToast({ title: '已删除', icon: 'success' })
      }
    }
  })
}

const syncAll = async () => {
  if (pendingList.value.length === 0) {
    uni.showToast({ title: '暂无待同步数据', icon: 'none' })
    return
  }
  syncing.value = true
  syncStatus.value = 'syncing'
  syncStatusText.value = '同步中...'

  let successCount = 0
  let failCount = 0

  for (const item of pendingList.value) {
    item.syncing = true
    try {
      await new Promise(resolve => setTimeout(resolve, 800))
      successCount++
    } catch (e) {
      failCount++
      failedList.value.push({
        ...item,
        errorMsg: e.message || '同步失败'
      })
    }
    item.syncing = false
  }

  pendingList.value = pendingList.value.filter(i => !i.syncing)
  savePendingToLocal()
  saveFailedToLocal()
  updateSyncStatus()
  syncing.value = false

  if (failCount === 0) {
    uni.showToast({ title: `全部同步成功（${successCount}条）`, icon: 'success' })
  } else {
    uni.showToast({ title: `成功${successCount}条，失败${failCount}条`, icon: 'none' })
  }
}

const clearCache = () => {
  uni.showModal({
    title: '确认清除',
    content: '确定清除所有本地缓存数据？此操作不可恢复。',
    success: (res) => {
      if (res.confirm) {
        pendingList.value = []
        failedList.value = []
        uni.removeStorageSync('offline_pending')
        uni.removeStorageSync('offline_failed')
        updateSyncStatus()
        uni.showToast({ title: '缓存已清除', icon: 'success' })
      }
    }
  })
}

const savePendingToLocal = () => {
  uni.setStorageSync('offline_pending', JSON.stringify(pendingList.value.map(i => ({ ...i, syncing: false }))))
}

const saveFailedToLocal = () => {
  uni.setStorageSync('offline_failed', JSON.stringify(failedList.value.map(i => ({ ...i, syncing: false }))))
}

onShow(() => {
  loadOfflineData()
})
</script>

<style scoped>
.offline-page {
  min-height: 100vh;
  background: #f5f7fa;
}
.status-bar {
  margin: 24rpx;
  padding: 28rpx;
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: space-around;
}
.status-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.status-label {
  font-size: 24rpx;
  color: rgba(255,255,255,0.8);
  margin-bottom: 8rpx;
}
.status-value {
  font-size: 40rpx;
  font-weight: 700;
  color: #ffffff;
}
.sync-status {
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
  color: #ffffff;
}
.sync-status.idle {
  background: rgba(255,255,255,0.2);
}
.sync-status.pending {
  background: rgba(250,173,20,0.8);
}
.sync-status.syncing {
  background: rgba(24,144,255,0.8);
}
.sync-status.synced {
  background: rgba(82,196,26,0.8);
}
.sync-status.error {
  background: rgba(245,34,45,0.8);
}
.status-divider {
  width: 1rpx;
  height: 60rpx;
  background: rgba(255,255,255,0.3);
}
.action-bar {
  display: flex;
  gap: 16rpx;
  padding: 0 24rpx 20rpx;
}
.action-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  color: #ffffff;
  font-size: 28rpx;
  border-radius: 40rpx;
  border: none;
}
.action-btn::after {
  border: none;
}
.action-btn.danger {
  background: #f5f5f5;
  color: #f5222d;
  border: 2rpx solid #f5222d;
}
.tab-bar {
  display: flex;
  gap: 16rpx;
  margin: 0 24rpx 20rpx;
}
.tab-item {
  flex: 1;
  text-align: center;
  padding: 16rpx 0;
  background: #ffffff;
  border-radius: 12rpx;
  font-size: 26rpx;
  color: #666;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
}
.tab-item.active {
  background: #ffffff;
  color: #1a5f3f;
  font-weight: 600;
  border-bottom: 4rpx solid #1a5f3f;
}
.data-list {
  padding: 0 24rpx;
}
.data-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06);
}
.data-card.failed {
  border-left: 6rpx solid #f5222d;
}
.data-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}
.data-type {
  font-size: 28rpx;
  font-weight: 600;
  color: #1a5f3f;
}
.data-time {
  font-size: 22rpx;
  color: #999;
}
.data-body {
  margin-bottom: 16rpx;
}
.data-desc {
  font-size: 26rpx;
  color: #333;
  display: block;
  margin-bottom: 8rpx;
}
.error-msg {
  font-size: 24rpx;
  color: #f5222d;
  display: block;
}
.data-footer {
  display: flex;
  justify-content: flex-end;
  gap: 16rpx;
  border-top: 1rpx solid #f0f0f0;
  padding-top: 16rpx;
}
.sync-single-btn {
  padding: 8rpx 32rpx;
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  color: #ffffff;
  font-size: 24rpx;
  border-radius: 20rpx;
  border: none;
  height: 56rpx;
  line-height: 40rpx;
}
.sync-single-btn::after {
  border: none;
}
.retry-btn {
  padding: 8rpx 32rpx;
  background: #e6f7ff;
  color: #1890ff;
  font-size: 24rpx;
  border-radius: 20rpx;
  border: none;
  height: 56rpx;
  line-height: 40rpx;
}
.retry-btn::after {
  border: none;
}
.delete-btn {
  padding: 8rpx 32rpx;
  background: #fff2f0;
  color: #f5222d;
  font-size: 24rpx;
  border-radius: 20rpx;
  border: none;
  height: 56rpx;
  line-height: 40rpx;
}
.delete-btn::after {
  border: none;
}
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0;
}
.empty-text {
  font-size: 28rpx;
  color: #999;
}
</style>
