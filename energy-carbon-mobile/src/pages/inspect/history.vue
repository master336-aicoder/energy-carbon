<template>
  <view class="history-page">
    <nav-bar title="巡检历史" showBack></nav-bar>
    <!-- 日期筛选 -->
    <view class="filter-card">
      <view class="filter-row">
        <view class="filter-item">
          <text class="filter-label">开始日期</text>
          <picker mode="date" :value="startDate" @change="onStartDateChange">
            <view class="picker-box">
              <text :class="{ placeholder: !startDate }">{{ startDate || '选择开始日期' }}</text>
            </view>
          </picker>
        </view>
        <view class="filter-item">
          <text class="filter-label">结束日期</text>
          <picker mode="date" :value="endDate" @change="onEndDateChange">
            <view class="picker-box">
              <text :class="{ placeholder: !endDate }">{{ endDate || '选择结束日期' }}</text>
            </view>
          </picker>
        </view>
        <button class="filter-btn" @click="searchHistory">查询</button>
      </view>
    </view>

    <!-- 历史列表 -->
    <view class="history-list">
      <view
        v-for="item in historyList"
        :key="item.id"
        class="history-card"
        @click="showDetail(item)"
      >
        <view class="history-header">
          <text class="device-name">{{ item.deviceName }}</text>
          <view class="result-tag" :class="item.result === 'normal' ? 'tag-normal' : 'tag-abnormal'">
            <text>{{ item.result === 'normal' ? '正常' : '异常' }}</text>
          </view>
        </view>
        <view class="history-body">
          <view class="info-row">
            <text class="info-label">巡检时间</text>
            <text class="info-value">{{ item.inspectTime }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">巡检人</text>
            <text class="info-value">{{ item.inspector }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">巡检项数</text>
            <text class="info-value">{{ item.totalItems }}项（异常{{ item.abnormalItems }}项）</text>
          </view>
        </view>
      </view>

      <view v-if="historyList.length === 0" class="empty-state">
        <text class="empty-text">暂无巡检记录</text>
      </view>
    </view>

    <!-- 详情弹窗 -->
    <uni-popup ref="detailPopup" type="bottom" :safe-area="true">
      <view class="popup-content">
        <view class="popup-header">
          <text class="popup-title">巡检详情</text>
          <text class="popup-close" @click="closePopup">x</text>
        </view>
        <view class="popup-body" v-if="currentDetail">
          <view class="detail-info">
            <text class="detail-device">{{ currentDetail.deviceName }}</text>
            <text class="detail-time">{{ currentDetail.inspectTime }} | {{ currentDetail.inspector }}</text>
          </view>
          <view class="detail-items">
            <view
              v-for="(item, index) in currentDetail.items"
              :key="index"
              class="detail-item"
              :class="item.result === 'abnormal' ? 'item-abnormal' : 'item-normal'"
            >
              <view class="detail-item-left">
                <text class="detail-item-name">{{ item.itemName }}</text>
                <text v-if="item.description" class="detail-item-desc">{{ item.description }}</text>
              </view>
              <text class="detail-item-result">{{ item.result === 'normal' ? '正常' : '异常' }}</text>
            </view>
          </view>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import request from '@/utils/request.js'

const detailPopup = ref(null)
const historyList = ref([])
const startDate = ref('')
const endDate = ref('')
const currentDetail = ref(null)

const onStartDateChange = (e) => {
  startDate.value = e.detail.value
}

const onEndDateChange = (e) => {
  endDate.value = e.detail.value
}

const searchHistory = () => {
  loadHistory()
}

const loadHistory = async () => {
  try {
    const params = {}
    if (startDate.value) params.startDate = startDate.value
    if (endDate.value) params.endDate = endDate.value
    const res = await request.get('/monitor/inspect-task/history', params)
    if (res.code === 200 && res.data) {
      historyList.value = res.data.records || res.data || []
      return
    }
  } catch (e) {
    console.error('加载巡检历史失败:', e)
  }
  // 演示数据
  historyList.value = [
    { id: 1, deviceName: '1#空压机', inspectTime: '2026-06-26 08:45', inspector: '张三', result: 'normal', totalItems: 8, abnormalItems: 0, items: [{ itemName: '设备外观检查', result: 'normal', description: '' }, { itemName: '运行声音检查', result: 'normal', description: '' }, { itemName: '油位检查', result: 'normal', description: '' }, { itemName: '压力表读数', result: 'normal', description: '' }] },
    { id: 2, deviceName: '2#冷却塔', inspectTime: '2026-06-26 09:20', inspector: '李四', result: 'abnormal', totalItems: 6, abnormalItems: 1, items: [{ itemName: '冷却水水位', result: 'normal', description: '' }, { itemName: '风机运行', result: 'normal', description: '' }, { itemName: '管道阀门', result: 'abnormal', description: '阀门有轻微渗漏' }, { itemName: '水质检查', result: 'normal', description: '' }] },
    { id: 3, deviceName: '3#配电柜', inspectTime: '2026-06-25 14:30', inspector: '王五', result: 'normal', totalItems: 10, abnormalItems: 0, items: [{ itemName: '开关状态', result: 'normal', description: '' }, { itemName: '温度检查', result: 'normal', description: '' }, { itemName: '线缆检查', result: 'normal', description: '' }] },
    { id: 4, deviceName: '4#锅炉', inspectTime: '2026-06-25 10:10', inspector: '赵六', result: 'abnormal', totalItems: 12, abnormalItems: 2, items: [{ itemName: '压力表', result: 'normal', description: '' }, { itemName: '安全阀', result: 'abnormal', description: '安全阀压力偏高' }, { itemName: '燃烧器', result: 'normal', description: '' }, { itemName: '烟道检查', result: 'abnormal', description: '烟道有积碳' }] },
    { id: 5, deviceName: '5#水泵房', inspectTime: '2026-06-24 16:00', inspector: '张三', result: 'normal', totalItems: 7, abnormalItems: 0, items: [{ itemName: '泵体检查', result: 'normal', description: '' }, { itemName: '密封检查', result: 'normal', description: '' }] }
  ]
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
  // 默认加载近30天数据
  const now = new Date()
  const thirtyDaysAgo = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000)
  endDate.value = formatDate(now)
  startDate.value = formatDate(thirtyDaysAgo)
  loadHistory()
})

const formatDate = (d) => {
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}
</script>

<style scoped>
.history-page {
  min-height: 100vh;
  background: #f5f7fa;
}
.filter-card {
  margin: 24rpx;
  padding: 24rpx;
  background: #ffffff;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06);
}
.filter-row {
  display: flex;
  align-items: flex-end;
  gap: 16rpx;
}
.filter-item {
  flex: 1;
}
.filter-label {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 8rpx;
  display: block;
}
.picker-box {
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 16rpx 20rpx;
  font-size: 26rpx;
  color: #333;
}
.picker-box .placeholder {
  color: #bbb;
}
.filter-btn {
  width: 120rpx;
  height: 72rpx;
  line-height: 72rpx;
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  color: #ffffff;
  font-size: 26rpx;
  border-radius: 12rpx;
  border: none;
  padding: 0;
}
.filter-btn::after {
  border: none;
}
.history-list {
  padding: 0 24rpx;
}
.history-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06);
}
.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}
.device-name {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
}
.result-tag {
  padding: 6rpx 20rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
}
.tag-normal {
  background: #f6ffed;
  color: #52c41a;
}
.tag-abnormal {
  background: #fff2f0;
  color: #f5222d;
}
.history-body {
  border-top: 1rpx solid #f0f0f0;
  padding-top: 16rpx;
}
.info-row {
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
.detail-info {
  margin-bottom: 24rpx;
}
.detail-device {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 8rpx;
}
.detail-time {
  font-size: 24rpx;
  color: #999;
}
.detail-items {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}
.detail-item {
  padding: 20rpx 24rpx;
  border-radius: 12rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.item-normal {
  background: #f6ffed;
}
.item-abnormal {
  background: #fff2f0;
}
.detail-item-name {
  font-size: 28rpx;
  color: #333;
  display: block;
}
.detail-item-desc {
  font-size: 24rpx;
  color: #999;
  display: block;
  margin-top: 4rpx;
}
.detail-item-result {
  font-size: 24rpx;
  font-weight: 500;
}
.item-normal .detail-item-result {
  color: #52c41a;
}
.item-abnormal .detail-item-result {
  color: #f5222d;
}
</style>
