<template>
  <view class="message-page">
    <!-- 顶部统计 -->
    <view class="msg-header">
      <view class="unread-badge">
        <text class="unread-num">{{ unreadCount }}</text>
        <text class="unread-label">未读消息</text>
      </view>
      <view class="mark-all-btn" @click="markAllRead" v-if="unreadCount > 0">
        <uni-icons type="checkmarkempty" size="16" color="#fff"></uni-icons>
        <text>全部已读</text>
      </view>
    </view>

    <!-- Tab切换 -->
    <view class="msg-tabs">
      <view
        v-for="t in tabs"
        :key="t.value"
        class="msg-tab"
        :class="{ active: activeTab === t.value }"
        @click="switchTab(t.value)"
      >
        <text>{{ t.label }}</text>
        <view v-if="getTabBadge(t.value) > 0" class="tab-badge">
          <text>{{ getTabBadge(t.value) }}</text>
        </view>
      </view>
    </view>

    <!-- 消息列表 -->
    <scroll-view
      scroll-y
      class="msg-scroll"
      @scrolltolower="loadMore"
    >
      <view
        v-for="(item, index) in filteredList"
        :key="item.id"
        class="msg-item"
        :class="{ unread: !item.read }"
        @click="readAndGo(item)"
        @touchstart="touchStart($event, index)"
        @touchmove="touchMove($event, index)"
        @touchend="touchEnd(index)"
        :style="{ transform: 'translateX(' + (item._offsetX || 0) + 'rpx)' }"
      >
        <view class="msg-content-row">
          <view class="msg-icon-wrap" :style="{ background: getIconBg(item.type) }">
            <uni-icons :type="getIconType(item.type)" size="20" :color="getIconColor(item.type)"></uni-icons>
          </view>
          <view class="msg-body">
            <view class="msg-title-row">
              <text class="msg-title" :class="{ bold: !item.read }">{{ item.title }}</text>
              <text class="msg-time">{{ item.time }}</text>
            </view>
            <text class="msg-summary">{{ item.summary }}</text>
          </view>
        </view>
        <view class="msg-delete-btn" @click.stop="deleteMsg(index)">
          <text>删除</text>
        </view>
      </view>

      <view v-if="filteredList.length === 0" class="empty-state">
        <uni-icons type="info" size="48" color="#cccccc"></uni-icons>
        <text class="empty-text">暂无消息</text>
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
import { onLoad, onShow } from '@dcloudio/uni-app'
import request from '@/utils/request.js'

const activeTab = ref('all')
const loadStatus = ref('noMore')

const tabs = [
  { label: '全部', value: 'all' },
  { label: '告警', value: 'alarm' },
  { label: '通知', value: 'notice' },
  { label: '任务', value: 'task' }
]

// 模拟消息数据
const messageList = ref([])

const demoMessages = [
  { id: 1, type: 'alarm', title: '温度过高告警', summary: '1#车间温度超过设定阈值35°C，请及时处理', time: '10:30', read: false, url: '/pages/alarm/detail?id=1' },
  { id: 2, type: 'alarm', title: '能耗异常告警', summary: '2#电表读数异常波动，超出正常范围20%', time: '09:15', read: false, url: '/pages/alarm/detail?id=2' },
  { id: 3, type: 'notice', title: '系统维护通知', summary: '系统将于今晚22:00-次日06:00进行升级维护', time: '昨天', read: false, url: '' },
  { id: 4, type: 'task', title: '巡检任务待完成', summary: '您有一个待执行的设备巡检任务，请尽快处理', time: '昨天', read: true, url: '/pages/inspect/index' },
  { id: 5, type: 'notice', title: '月度报表已生成', summary: '2026年6月能耗月报已生成，请查阅', time: '06-24', read: true, url: '/pages/report/detail?type=energy' },
  { id: 6, type: 'alarm', title: '碳排放超标提醒', summary: '本月碳排放已达配额的85%，请注意控制', time: '06-23', read: true, url: '/pages/alarm/detail?id=5' },
  { id: 7, type: 'task', title: '数据录入提醒', summary: '本周抄表数据尚未录入，请及时补充', time: '06-22', read: true, url: '/pages/entry/index' },
  { id: 8, type: 'notice', title: '设备维保通知', summary: '1#空压机即将到达维保周期，请安排维保', time: '06-20', read: true, url: '' }
]

const unreadCount = computed(() => {
  return messageList.value.filter(m => !m.read).length
})

const filteredList = computed(() => {
  if (activeTab.value === 'all') return messageList.value
  return messageList.value.filter(m => m.type === activeTab.value)
})

const getTabBadge = (tab) => {
  if (tab === 'all') return unreadCount.value
  return messageList.value.filter(m => m.type === tab && !m.read).length
}

const getIconType = (type) => {
  const map = { alarm: 'warn', notice: 'notification', task: 'compose' }
  return map[type] || 'info'
}
const getIconBg = (type) => {
  const map = { alarm: '#fff2f0', notice: '#e6f7ff', task: '#f6ffed' }
  return map[type] || '#f5f5f5'
}
const getIconColor = (type) => {
  const map = { alarm: '#f5222d', notice: '#1890ff', task: '#52c41a' }
  return map[type] || '#999999'
}

const switchTab = (val) => {
  activeTab.value = val
}

const readAndGo = (item) => {
  item.read = true
  saveToLocal()
  if (item.url) {
    // 判断是否是TabBar页面
    const tabPages = ['pages/home/index', 'pages/entry/index', 'pages/alarm/index', 'pages/message/index', 'pages/profile/index']
    const isTab = tabPages.some(p => item.url.indexOf(p) === 0 || item.url === '/' + p)
    if (isTab) {
      uni.switchTab({ url: item.url })
    } else {
      uni.navigateTo({ url: item.url })
    }
  }
}

const markAllRead = () => {
  messageList.value.forEach(m => { m.read = true })
  saveToLocal()
  uni.showToast({ title: '已全部标记已读', icon: 'success' })
}

const deleteMsg = (index) => {
  messageList.value.splice(index, 1)
  saveToLocal()
  uni.showToast({ title: '已删除', icon: 'success' })
}

const loadMore = () => {
  if (loadStatus.value !== 'more') return
  loadStatus.value = 'noMore'
}

// 右滑删除
const touchStartX = ref(0)
const touchStartY = ref(0)
const touchStart = (e, index) => {
  touchStartX.value = e.touches[0].clientX
  touchStartY.value = e.touches[0].clientY
}
const touchMove = (e, index) => {
  const dx = e.touches[0].clientX - touchStartX.value
  const dy = Math.abs(e.touches[0].clientY - touchStartY.value)
  if (dy > 30) return // 竖向滑动不处理
  if (dx < -10) {
    messageList.value[index]._offsetX = Math.max(dx * 2, -160)
  } else if (dx > 0) {
    messageList.value[index]._offsetX = Math.min(0, dx * 2)
  }
}
const touchEnd = (index) => {
  if (messageList.value[index]._offsetX < -80) {
    messageList.value[index]._offsetX = -160
  } else {
    messageList.value[index]._offsetX = 0
  }
}

const saveToLocal = () => {
  const data = messageList.value.map(m => {
    const copy = { ...m }
    delete copy._offsetX
    return copy
  })
  uni.setStorageSync('messageList', JSON.stringify(data))
}

const loadData = async () => {
  // 尝试从API获取
  try {
    const res = await request.get('/system/message/list', { current: 1, size: 50 })
    if (res.code === 200 && res.data && res.data.records && res.data.records.length > 0) {
      messageList.value = res.data.records.map(item => ({
        id: item.id,
        type: item.type || 'notice',
        title: item.title,
        summary: item.content || item.summary || '',
        time: item.createTime || '',
        read: item.readStatus === 1,
        url: item.url || ''
      }))
      saveToLocal()
      return
    }
  } catch (e) {
    // API不可用
  }

  // 从本地缓存获取
  const cached = uni.getStorageSync('messageList')
  if (cached) {
    try {
      messageList.value = JSON.parse(cached)
      return
    } catch (e) {}
  }
  // 使用演示数据
  messageList.value = [...demoMessages]
  saveToLocal()
}

onLoad(() => {
  loadData()
})

onShow(() => {
  loadData()
})
</script>

<style scoped>
.message-page {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}
.msg-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 24rpx 0;
}
.unread-badge {
  display: flex;
  align-items: baseline;
  gap: 8rpx;
}
.unread-num {
  font-size: 48rpx;
  font-weight: 700;
  color: #1a5f3f;
}
.unread-label {
  font-size: 26rpx;
  color: #999999;
}
.mark-all-btn {
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 12rpx 24rpx;
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  border-radius: 32rpx;
  font-size: 24rpx;
  color: #ffffff;
}
.msg-tabs {
  display: flex;
  gap: 16rpx;
  padding: 24rpx;
}
.msg-tab {
  position: relative;
  padding: 12rpx 28rpx;
  background: #ffffff;
  border-radius: 32rpx;
  font-size: 26rpx;
  color: #666666;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}
.msg-tab.active {
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  color: #ffffff;
}
.tab-badge {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  min-width: 32rpx;
  height: 32rpx;
  line-height: 32rpx;
  background: #f5222d;
  border-radius: 16rpx;
  font-size: 20rpx;
  color: #ffffff;
  text-align: center;
  padding: 0 8rpx;
}
.msg-scroll {
  flex: 1;
  height: 0;
  padding: 0 24rpx;
}
.msg-item {
  position: relative;
  overflow: hidden;
  margin-bottom: 16rpx;
  transition: transform 0.2s ease;
}
.msg-content-row {
  display: flex;
  align-items: flex-start;
  gap: 20rpx;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
  position: relative;
  z-index: 2;
}
.msg-item.unread .msg-content-row {
  border-left: 6rpx solid #1a5f3f;
}
.msg-icon-wrap {
  width: 72rpx;
  height: 72rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.msg-body {
  flex: 1;
  min-width: 0;
}
.msg-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8rpx;
}
.msg-title {
  font-size: 28rpx;
  color: #333333;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.msg-title.bold {
  font-weight: 600;
}
.msg-time {
  font-size: 22rpx;
  color: #999999;
  flex-shrink: 0;
  margin-left: 12rpx;
}
.msg-summary {
  font-size: 24rpx;
  color: #999999;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.msg-delete-btn {
  position: absolute;
  right: 0;
  top: 0;
  bottom: 0;
  width: 160rpx;
  background: #f5222d;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-size: 28rpx;
  z-index: 1;
  border-radius: 16rpx 0 0 16rpx;
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
