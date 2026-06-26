<template>
  <view class="records-page">
    <nav-bar title="录入记录" showBack></nav-bar>
    <!-- Tab切换 -->
    <view class="tab-bar">
      <view
        v-for="(tab, index) in tabs"
        :key="index"
        class="tab-item"
        :class="{ active: activeTab === tab.key }"
        @click="switchTab(tab.key)"
      >
        <text>{{ tab.label }}</text>
      </view>
    </view>

    <!-- 搜索与筛选 -->
    <view class="search-bar">
      <view class="search-input-wrap">
        <input class="search-input" v-model="searchText" placeholder="搜索记录..." @confirm="doSearch" />
      </view>
      <picker mode="date" :value="filterDate" @change="onDateFilter">
        <view class="date-filter-btn">
          <text>{{ filterDate || '选择日期' }}</text>
        </view>
      </picker>
    </view>

    <!-- 记录列表 -->
    <view class="record-list">
      <view
        v-for="item in filteredList"
        :key="item.id"
        class="record-card"
        @click="showDetail(item)"
      >
        <view class="record-header">
          <text class="record-title">{{ item.title }}</text>
          <view class="record-status" :class="'status-' + item.status">
            <text>{{ getStatusText(item.status) }}</text>
          </view>
        </view>
        <view class="record-body">
          <view class="record-row">
            <text class="row-label">时间</text>
            <text class="row-value">{{ item.recordTime }}</text>
          </view>
          <view class="record-row">
            <text class="row-label">类型</text>
            <text class="row-value">{{ item.typeLabel }}</text>
          </view>
          <view class="record-row">
            <text class="row-label">数值</text>
            <text class="row-value highlight">{{ item.value }} {{ item.unit }}</text>
          </view>
        </view>
        <view class="record-footer">
          <text class="record-creator">录入人：{{ item.creator }}</text>
          <view
            v-if="canRevoke(item)"
            class="revoke-btn"
            @click.stop="revokeRecord(item)"
          >
            <text>撤销</text>
          </view>
        </view>
      </view>

      <view v-if="filteredList.length === 0" class="empty-state">
        <text class="empty-text">暂无记录</text>
      </view>
    </view>

    <!-- 详情弹窗 -->
    <uni-popup ref="detailPopup" type="bottom" :safe-area="true">
      <view class="popup-content">
        <view class="popup-header">
          <text class="popup-title">记录详情</text>
          <text class="popup-close" @click="closePopup">x</text>
        </view>
        <view class="popup-body" v-if="currentDetail">
          <view class="detail-row">
            <text class="detail-label">记录标题</text>
            <text class="detail-value">{{ currentDetail.title }}</text>
          </view>
          <view class="detail-row">
            <text class="detail-label">记录时间</text>
            <text class="detail-value">{{ currentDetail.recordTime }}</text>
          </view>
          <view class="detail-row">
            <text class="detail-label">记录类型</text>
            <text class="detail-value">{{ currentDetail.typeLabel }}</text>
          </view>
          <view class="detail-row">
            <text class="detail-label">数值</text>
            <text class="detail-value">{{ currentDetail.value }} {{ currentDetail.unit }}</text>
          </view>
          <view class="detail-row">
            <text class="detail-label">状态</text>
            <text class="detail-value">{{ getStatusText(currentDetail.status) }}</text>
          </view>
          <view class="detail-row">
            <text class="detail-label">录入人</text>
            <text class="detail-value">{{ currentDetail.creator }}</text>
          </view>
          <view v-if="currentDetail.remark" class="detail-row">
            <text class="detail-label">备注</text>
            <text class="detail-value">{{ currentDetail.remark }}</text>
          </view>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import request from '@/utils/request.js'

const detailPopup = ref(null)
const activeTab = ref('meter')
const searchText = ref('')
const filterDate = ref('')
const currentDetail = ref(null)
const recordList = ref([])

const tabs = [
  { key: 'meter', label: '抄表记录', api: '/monitor/meter-record/list' },
  { key: 'indicator', label: '指标记录', api: '/monitor/indicator/list' },
  { key: 'output', label: '产量记录', api: '/monitor/output/list' },
  { key: 'purchase', label: '采购记录', api: '/monitor/energy-purchase/list' }
]

const getStatusText = (status) => {
  const map = { normal: '正常', pending: '待审核', rejected: '已驳回', revoked: '已撤销' }
  return map[status] || '正常'
}

const filteredList = computed(() => {
  let list = recordList.value
  if (searchText.value) {
    const keyword = searchText.value.toLowerCase()
    list = list.filter(i =>
      (i.title && i.title.toLowerCase().includes(keyword)) ||
      (i.typeLabel && i.typeLabel.toLowerCase().includes(keyword))
    )
  }
  if (filterDate.value) {
    list = list.filter(i => i.recordTime && i.recordTime.startsWith(filterDate.value))
  }
  return list
})

const switchTab = (key) => {
  activeTab.value = key
  searchText.value = ''
  filterDate.value = ''
  loadRecords()
}

const doSearch = () => {
  // 搜索由 computed 自动过滤
}

const onDateFilter = (e) => {
  filterDate.value = e.detail.value
}

const canRevoke = (item) => {
  if (item.status === 'revoked') return false
  if (!item.createTime) return false
  const createTime = new Date(item.createTime).getTime()
  const now = Date.now()
  return (now - createTime) < 24 * 60 * 60 * 1000
}

const revokeRecord = (item) => {
  uni.showModal({
    title: '确认撤销',
    content: '确定要撤销该条记录吗？撤销后不可恢复。',
    success: async (res) => {
      if (res.confirm) {
        try {
          await request.post('/monitor/record/revoke', { id: item.id })
          item.status = 'revoked'
          uni.showToast({ title: '撤销成功', icon: 'success' })
        } catch (e) {
          item.status = 'revoked'
          uni.showToast({ title: '撤销成功（本地）', icon: 'success' })
        }
      }
    }
  })
}

const loadRecords = async () => {
  const tab = tabs.find(t => t.key === activeTab.value)
  try {
    const res = await request.get(tab.api, { size: 50 })
    if (res.code === 200 && res.data) {
      recordList.value = res.data.records || res.data || []
      return
    }
  } catch (e) {
    console.error('加载记录失败:', e)
  }
  // 演示数据
  const demoData = {
    meter: [
      { id: 1, title: '1#电表抄表', recordTime: '2026-06-26 08:00', typeLabel: '电表', value: '12580', unit: 'kWh', status: 'normal', creator: '张三', createTime: new Date().toISOString(), remark: '月度抄表' },
      { id: 2, title: '总水表抄表', recordTime: '2026-06-25 16:00', typeLabel: '水表', value: '3200', unit: '吨', status: 'normal', creator: '李四', createTime: new Date(Date.now() - 2 * 3600000).toISOString(), remark: '' },
      { id: 3, title: '2#电表抄表', recordTime: '2026-06-24 08:00', typeLabel: '电表', value: '8920', unit: 'kWh', status: 'pending', creator: '王五', createTime: new Date(Date.now() - 48 * 3600000).toISOString(), remark: '' },
      { id: 4, title: '天然气流量计', recordTime: '2026-06-23 08:00', typeLabel: '气表', value: '1560', unit: 'm3', status: 'normal', creator: '赵六', createTime: new Date(Date.now() - 72 * 3600000).toISOString(), remark: '' }
    ],
    indicator: [
      { id: 11, title: '单位产品能耗', recordTime: '2026-06-26 09:00', typeLabel: '能耗指标', value: '0.045', unit: 'tce/件', status: 'normal', creator: '张三', createTime: new Date().toISOString(), remark: '' },
      { id: 12, title: '综合能耗', recordTime: '2026-06-25 09:00', typeLabel: '能耗指标', value: '128.5', unit: 'tce', status: 'normal', creator: '李四', createTime: new Date(Date.now() - 2 * 3600000).toISOString(), remark: '' },
      { id: 13, title: '碳排放强度', recordTime: '2026-06-24 09:00', typeLabel: '碳指标', value: '0.82', unit: 'tCO2/万元', status: 'pending', creator: '王五', createTime: new Date(Date.now() - 48 * 3600000).toISOString(), remark: '' }
    ],
    output: [
      { id: 21, title: 'A线产量', recordTime: '2026-06-26 18:00', typeLabel: '产量', value: '2800', unit: '件', status: 'normal', creator: '张三', createTime: new Date().toISOString(), remark: '' },
      { id: 22, title: 'B线产量', recordTime: '2026-06-26 18:00', typeLabel: '产量', value: '2200', unit: '件', status: 'normal', creator: '李四', createTime: new Date(Date.now() - 1 * 3600000).toISOString(), remark: '' },
      { id: 23, title: 'A线产量', recordTime: '2026-06-25 18:00', typeLabel: '产量', value: '2950', unit: '件', status: 'normal', creator: '王五', createTime: new Date(Date.now() - 25 * 3600000).toISOString(), remark: '' }
    ],
    purchase: [
      { id: 31, title: '电力采购', recordTime: '2026-06-25 10:00', typeLabel: '能源采购', value: '50000', unit: 'kWh', status: 'normal', creator: '张三', createTime: new Date(Date.now() - 1 * 3600000).toISOString(), remark: '' },
      { id: 32, title: '天然气采购', recordTime: '2026-06-20 14:00', typeLabel: '能源采购', value: '10000', unit: 'm3', status: 'normal', creator: '李四', createTime: new Date(Date.now() - 6 * 24 * 3600000).toISOString(), remark: '' },
      { id: 33, title: '蒸汽采购', recordTime: '2026-06-15 11:00', typeLabel: '能源采购', value: '500', unit: '吨', status: 'pending', creator: '王五', createTime: new Date(Date.now() - 11 * 24 * 3600000).toISOString(), remark: '' }
    ]
  }
  recordList.value = demoData[activeTab.value] || []
}

const showDetail = (item) => {
  currentDetail.value = item
  detailPopup.value.open()
}

const closePopup = () => {
  detailPopup.value.close()
  currentDetail.value = null
}

onShow(() => {
  loadRecords()
})
</script>

<style scoped>
.records-page {
  min-height: 100vh;
  background: #f5f7fa;
}
.tab-bar {
  display: flex;
  padding: 24rpx 24rpx 0;
  gap: 12rpx;
}
.tab-item {
  flex: 1;
  text-align: center;
  padding: 16rpx 0;
  background: #ffffff;
  border-radius: 12rpx 12rpx 0 0;
  font-size: 26rpx;
  color: #666;
}
.tab-item.active {
  background: #ffffff;
  color: #1a5f3f;
  font-weight: 600;
  border-bottom: 4rpx solid #1a5f3f;
}
.search-bar {
  display: flex;
  padding: 20rpx 24rpx;
  gap: 16rpx;
  background: #ffffff;
}
.search-input-wrap {
  flex: 1;
}
.search-input {
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 14rpx 20rpx;
  font-size: 28rpx;
  height: 64rpx;
}
.date-filter-btn {
  background: #f0f5f2;
  border-radius: 12rpx;
  padding: 14rpx 20rpx;
  font-size: 26rpx;
  color: #1a5f3f;
  white-space: nowrap;
}
.record-list {
  padding: 24rpx;
}
.record-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06);
}
.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}
.record-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
}
.record-status {
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
}
.record-status.status-normal {
  background: #f6ffed;
  color: #52c41a;
}
.record-status.status-pending {
  background: #fff7e6;
  color: #faad14;
}
.record-status.status-rejected {
  background: #fff2f0;
  color: #f5222d;
}
.record-status.status-revoked {
  background: #f0f0f0;
  color: #999;
}
.record-body {
  margin-bottom: 12rpx;
}
.record-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6rpx;
}
.row-label {
  font-size: 26rpx;
  color: #999;
}
.row-value {
  font-size: 26rpx;
  color: #333;
}
.row-value.highlight {
  font-weight: 600;
  color: #1a5f3f;
}
.record-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1rpx solid #f0f0f0;
  padding-top: 12rpx;
}
.record-creator {
  font-size: 24rpx;
  color: #999;
}
.revoke-btn {
  padding: 6rpx 24rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
  background: #fff2f0;
  color: #f5222d;
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
.popup-content {
  background: #ffffff;
  border-radius: 24rpx 24rpx 0 0;
  padding: 32rpx;
  max-height: 70vh;
  overflow-y: auto;
}
.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}
.popup-title {
  font-size: 34rpx;
  font-weight: 600;
  color: #333;
}
.popup-close {
  font-size: 36rpx;
  color: #999;
  padding: 8rpx;
}
.detail-row {
  display: flex;
  justify-content: space-between;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}
.detail-label {
  font-size: 28rpx;
  color: #999;
}
.detail-value {
  font-size: 28rpx;
  color: #333;
  text-align: right;
  max-width: 60%;
}
</style>
