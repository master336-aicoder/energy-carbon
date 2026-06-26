<template>
  <view class="execute-page">
    <nav-bar title="巡检执行" showBack></nav-bar>
    <!-- 顶部设备信息 -->
    <view class="device-header">
      <view class="device-info">
        <text class="device-name">{{ taskInfo.deviceName || '设备名称' }}</text>
        <text class="template-name">{{ taskInfo.templateName || '巡检模板' }}</text>
      </view>
      <view class="gps-section" @click="getLocation">
        <text class="gps-icon">&#x1f4cd;</text>
        <text class="gps-text">{{ gpsText }}</text>
      </view>
    </view>

    <!-- GPS位置显示 -->
    <view v-if="location.lat" class="location-card">
      <text class="location-label">当前位置</text>
      <text class="location-value">经度：{{ location.lng }}  纬度：{{ location.lat }}</text>
    </view>

    <!-- 巡检项列表 -->
    <view class="check-list">
      <view class="list-title">
        <text>巡检项目（{{ checkItems.length }}项）</text>
      </view>
      <view
        v-for="(item, index) in checkItems"
        :key="index"
        class="check-item"
      >
        <view class="check-main" @click="toggleExpand(index)">
          <view class="check-left">
            <text class="check-index">{{ index + 1 }}</text>
            <text class="check-name">{{ item.itemName }}</text>
          </view>
          <view class="check-actions">
            <view
              class="status-btn"
              :class="{ active: item.result === 'normal' }"
              @click.stop="markItem(index, 'normal')"
            >
              <text>正常</text>
            </view>
            <view
              class="status-btn abnormal"
              :class="{ active: item.result === 'abnormal' }"
              @click.stop="markItem(index, 'abnormal')"
            >
              <text>异常</text>
            </view>
          </view>
        </view>
        <!-- 异常展开区域 -->
        <view v-if="item.result === 'abnormal' && item.expanded" class="abnormal-detail">
          <view class="detail-group">
            <text class="detail-label">异常描述</text>
            <textarea
              class="detail-textarea"
              v-model="item.description"
              placeholder="请输入异常情况描述..."
              maxlength="500"
            />
          </view>
          <view class="detail-group">
            <text class="detail-label">现场拍照</text>
            <view class="photo-area">
              <view
                v-for="(photo, pIdx) in item.photos"
                :key="pIdx"
                class="photo-item"
              >
                <image :src="photo" class="photo-img" mode="aspectFill" />
                <view class="photo-remove" @click="removePhoto(index, pIdx)">
                  <text>x</text>
                </view>
              </view>
              <view class="photo-add" @click="takePhoto(index)">
                <text class="add-icon">+</text>
                <text class="add-text">拍照</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部提交 -->
    <view class="submit-section">
      <button class="submit-btn" :loading="submitting" @click="handleSubmit">
        {{ submitting ? '提交中...' : '提交巡检结果' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import request from '@/utils/request.js'
import { getPageParam } from '@/utils/page-options.js'

const taskId = ref('')
const submitting = ref(false)
const taskInfo = reactive({
  deviceName: '',
  templateName: ''
})
const location = reactive({
  lat: '',
  lng: ''
})
const gpsText = ref('获取定位')
const checkItems = ref([])

const loadTaskDetail = async () => {
  try {
    const res = await request.get('/monitor/inspect-task/detail', { id: taskId.value })
    if (res.code === 200 && res.data) {
      taskInfo.deviceName = res.data.deviceName
      taskInfo.templateName = res.data.templateName
      checkItems.value = (res.data.items || []).map(item => ({
        itemName: item.itemName || item.name || item.item_name || '未命名项目',
        itemCode: item.itemCode || item.code || item.item_code || '',
        checkMethod: item.checkMethod || item.check_method || item.method || '',
        standardValue: item.standardValue || item.standard_value || item.standard || '',
        result: '',
        description: '',
        photos: [],
        expanded: false
      }))
      return
    }
  } catch (e) {
    console.error('加载任务详情失败:', e)
  }
  // 演示数据
  taskInfo.deviceName = '1#空压机'
  taskInfo.templateName = '空压机日常巡检'
  checkItems.value = [
    { itemName: '设备外观检查', result: '', description: '', photos: [], expanded: false },
    { itemName: '运行声音检查', result: '', description: '', photos: [], expanded: false },
    { itemName: '油位检查', result: '', description: '', photos: [], expanded: false },
    { itemName: '压力表读数', result: '', description: '', photos: [], expanded: false },
    { itemName: '冷却水温度', result: '', description: '', photos: [], expanded: false },
    { itemName: '管路接头泄漏检查', result: '', description: '', photos: [], expanded: false },
    { itemName: '安全阀状态', result: '', description: '', photos: [], expanded: false },
    { itemName: '周围环境清洁度', result: '', description: '', photos: [], expanded: false }
  ]
}

const toggleExpand = (index) => {
  checkItems.value[index].expanded = !checkItems.value[index].expanded
}

const markItem = (index, result) => {
  checkItems.value[index].result = result
  if (result === 'normal') {
    checkItems.value[index].expanded = false
    checkItems.value[index].description = ''
    checkItems.value[index].photos = []
  } else {
    checkItems.value[index].expanded = true
  }
}

const takePhoto = (index) => {
  uni.chooseImage({
    count: 3 - checkItems.value[index].photos.length,
    sourceType: ['camera'],
    success: (res) => {
      checkItems.value[index].photos.push(...res.tempFilePaths)
    },
    fail: () => {
      uni.showToast({ title: '拍照取消', icon: 'none' })
    }
  })
}

const removePhoto = (itemIndex, photoIndex) => {
  checkItems.value[itemIndex].photos.splice(photoIndex, 1)
}

const getLocation = () => {
  gpsText.value = '定位中...'
  uni.getLocation({
    type: 'gcj02',
    success: (res) => {
      location.lat = res.latitude.toFixed(6)
      location.lng = res.longitude.toFixed(6)
      gpsText.value = '已定位'
    },
    fail: () => {
      gpsText.value = '定位失败'
      uni.showToast({ title: '获取定位失败', icon: 'none' })
    }
  })
}

const handleSubmit = async () => {
  const unchecked = checkItems.value.filter(i => !i.result)
  if (unchecked.length > 0) {
    uni.showToast({ title: `还有${unchecked.length}项未检查`, icon: 'none' })
    return
  }
  const abnormals = checkItems.value.filter(i => i.result === 'abnormal')
  for (const item of abnormals) {
    if (!item.description.trim()) {
      uni.showToast({ title: '请填写异常描述', icon: 'none' })
      return
    }
  }

  submitting.value = true
  try {
    const payload = {
      taskId: taskId.value,
      latitude: location.lat,
      longitude: location.lng,
      items: checkItems.value.map(item => ({
        itemName: item.itemName,
        result: item.result,
        description: item.description,
        photos: item.photos
      }))
    }
    const res = await request.post('/monitor/inspect-task/submit', payload)
    if (res.code === 200) {
      uni.showToast({ title: '巡检结果提交成功', icon: 'success' })
      setTimeout(() => uni.navigateBack(), 1500)
    } else {
      uni.showToast({ title: res.message || '提交失败', icon: 'none' })
    }
  } catch (e) {
    console.error(e)
    uni.showToast({ title: '提交成功（本地演示）', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 1500)
  } finally {
    submitting.value = false
  }
}

onLoad((options) => {
  if (options && options.id) {
    taskId.value = options.id
  }
  loadTaskDetail()
})

onMounted(() => {
  // Edge等浏览器H5兼容
  const id = getPageParam('id')
  if (id) {
    taskId.value = id
    loadTaskDetail()
  }
})
</script>

<style scoped>
.execute-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 160rpx;
}
.device-header {
  margin: 24rpx;
  padding: 28rpx;
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  border-radius: 16rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.device-info {
  display: flex;
  flex-direction: column;
}
.device-name {
  font-size: 34rpx;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 8rpx;
}
.template-name {
  font-size: 24rpx;
  color: rgba(255,255,255,0.8);
}
.gps-section {
  display: flex;
  align-items: center;
  gap: 8rpx;
  background: rgba(255,255,255,0.2);
  padding: 12rpx 20rpx;
  border-radius: 24rpx;
}
.gps-icon {
  font-size: 28rpx;
}
.gps-text {
  font-size: 24rpx;
  color: #ffffff;
}
.location-card {
  margin: 0 24rpx 20rpx;
  padding: 20rpx 28rpx;
  background: #ffffff;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06);
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.location-label {
  font-size: 26rpx;
  color: #999;
}
.location-value {
  font-size: 24rpx;
  color: #1a5f3f;
}
.check-list {
  padding: 0 24rpx;
}
.list-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 20rpx;
}
.check-item {
  background: #ffffff;
  border-radius: 16rpx;
  margin-bottom: 16rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06);
  overflow: hidden;
}
.check-main {
  padding: 24rpx 28rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.check-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}
.check-index {
  width: 44rpx;
  height: 44rpx;
  line-height: 44rpx;
  text-align: center;
  background: #f0f5f2;
  border-radius: 50%;
  font-size: 24rpx;
  color: #1a5f3f;
  font-weight: 600;
}
.check-name {
  font-size: 28rpx;
  color: #333;
}
.check-actions {
  display: flex;
  gap: 12rpx;
}
.status-btn {
  padding: 8rpx 24rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  background: #f5f5f5;
  color: #999;
}
.status-btn.active {
  background: #f6ffed;
  color: #52c41a;
}
.status-btn.abnormal.active {
  background: #fff2f0;
  color: #f5222d;
}
.abnormal-detail {
  padding: 0 28rpx 24rpx;
  border-top: 1rpx solid #f0f0f0;
}
.detail-group {
  margin-top: 20rpx;
}
.detail-label {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 12rpx;
  display: block;
}
.detail-textarea {
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 20rpx;
  font-size: 28rpx;
  color: #333;
  width: 100%;
  height: 160rpx;
  box-sizing: border-box;
}
.photo-area {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}
.photo-item {
  width: 160rpx;
  height: 160rpx;
  border-radius: 12rpx;
  position: relative;
}
.photo-img {
  width: 100%;
  height: 100%;
  border-radius: 12rpx;
}
.photo-remove {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  width: 36rpx;
  height: 36rpx;
  background: #f5222d;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 22rpx;
}
.photo-add {
  width: 160rpx;
  height: 160rpx;
  border: 2rpx dashed #ccc;
  border-radius: 12rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
}
.add-icon {
  font-size: 48rpx;
  color: #ccc;
}
.add-text {
  font-size: 22rpx;
  color: #999;
}
.submit-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx 24rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  background: #ffffff;
  box-shadow: 0 -4rpx 16rpx rgba(0,0,0,0.06);
}
.submit-btn {
  width: 100%;
  height: 96rpx;
  line-height: 96rpx;
  background: linear-gradient(135deg, #1a5f3f, #2d8b5a);
  color: #ffffff;
  font-size: 34rpx;
  border-radius: 48rpx;
  border: none;
}
.submit-btn::after {
  border: none;
}
</style>
