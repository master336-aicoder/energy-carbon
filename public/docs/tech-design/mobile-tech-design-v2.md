# 能碳数智化平台移动端二期技术设计文档

## 文档信息

| 项目 | 内容 |
|------|------|
| 文档名称 | 能碳数智化平台移动端二期技术设计文档 |
| 版本号 | v2.0.0 |
| 编制日期 | 2026-06-26 |
| 目标平台 | iOS / Android / 微信小程序 / H5 |
| 基于版本 | v1.0.0（一期技术设计文档） |
| 关联文档 | 《mobile-tech-design.md》《mobile-dev-plan-v2.md》 |

---

## 目录

1. [概述](#1-概述)
2. [设备巡检模块](#2-设备巡检模块)
3. [报表增强](#3-报表增强)
4. [消息中心](#4-消息中心)
5. [离线功能增强](#5-离线功能增强)
6. [主题切换](#6-主题切换)
7. [图片处理](#7-图片处理)
8. [二期新增目录结构](#8-二期新增目录结构)
9. [二期新增API接口设计](#9-二期新增api接口设计)

---

## 1. 概述

### 1.1 一期回顾

一期已基于 **UniApp + Vue 3 + uview-plus + Pinia + uni.request封装** 完成基础功能，包括：

- 首页仪表盘（KPI卡片、趋势图）
- 数据录入（抄表录入、碳排放录入）
- 告警中心（列表、详情、处理）
- 报表查看（能耗/碳排放图表）
- 个人中心（用户信息、设置）
- 扫码功能（设备/计量器具扫码）
- 基础离线方案（本地SQLite + 同步队列）

### 1.2 二期新增功能总览

| 模块 | 新增功能 | 优先级 |
|------|----------|--------|
| 设备巡检 | 巡检任务管理、巡检执行、GPS定位、拍照上传、异常上报 | P0 |
| 报表增强 | 多维度时间切换、同比/环比计算、PDF/Excel导出、混合图表 | P0 |
| 消息中心 | 实时推送（WebSocket+极光）、本地缓存、已读/未读管理 | P1 |
| 离线增强 | 队列优化、冲突检测与合并、存储容量管理 | P1 |
| 主题切换 | CSS变量主题、暗色模式适配 | P2 |
| 图片处理 | 压缩方案、上传进度、预览组件 | P1 |

### 1.3 技术栈扩展

一期技术栈基础上，二期新增以下依赖：

| 新增依赖 | 版本 | 用途 | 适用平台 |
|----------|------|------|----------|
| jsPDF | 2.5+ | H5端PDF生成 | H5 |
| SheetJS (xlsx) | 0.18+ | H5端Excel生成 | H5 |
| @nicepkg/gql2flow | - | （可选）云函数Excel/PDF生成 | 小程序 |
| jpush-uniplugin | 1.x | 极光推送App端插件 | App |

---

## 2. 设备巡检模块

### 2.1 巡检任务数据模型

#### 2.1.1 TypeScript类型定义

```typescript
// types/entity.ts - 二期新增

/** 巡检任务 */
export interface InspectionTask {
  taskId: string              // 任务ID（UUID）
  deviceId: number            // 设备ID
  deviceName: string          // 设备名称
  deviceCode: string          // 设备编码
  templateId: number          // 巡检模板ID
  templateName: string        // 巡检模板名称
  items: InspectionItem[]     // 巡检项列表
  status: InspectionStatus    // 任务状态
  assigneeId: number          // 巡检人ID
  assigneeName: string        // 巡检人姓名
  scheduledTime: string       // 计划巡检时间（ISO 8601）
  startTime?: string          // 实际开始时间
  endTime?: string             // 实际完成时间
  gpsLocation?: GPSLocation   // GPS定位信息
  photos: InspectionPhoto[]   // 巡检照片
  remark?: string              // 备注
  createdAt: string           // 创建时间
  updatedAt: string           // 更新时间
}

/** 巡检项 */
export interface InspectionItem {
  itemId: string              // 巡检项ID
  itemName: string            // 巡检项名称
  itemType: 'boolean' | 'numeric' | 'text' | 'select'  // 巡检项类型
  standard: string            // 检查标准/正常值描述
  threshold?: { min: number; max: number }  // 数值型阈值
  options?: string[]          // 选择型选项
  required: boolean           // 是否必填
  sortOrder: number           // 排序序号
  result?: string             // 巡检结果（用户填写）
  isAbnormal: boolean         // 是否异常
  abnormalDesc?: string        // 异常描述
  photos: string[]            // 巡检项照片（本地路径/远程URL）
}

/** 巡检状态枚举 */
export type InspectionStatus =
  | 'pending'       // 待执行
  | 'in_progress'   // 进行中
  | 'completed'     // 已完成
  | 'submitted'     // 已提交
  | 'rejected'      // 已驳回
  | 'cancelled'     // 已取消

/** GPS定位信息 */
export interface GPSLocation {
  latitude: number           // 纬度
  longitude: number          // 经度
  accuracy: number           // 精度（米）
  altitude?: number          // 海拔（可选）
  timestamp: number          // 定位时间戳
}

/** 巡检照片 */
export interface InspectionPhoto {
  localPath: string          // 本地路径
  remoteUrl?: string         // 远程URL（上传后）
  thumbUrl?: string          // 缩略图URL
  uploadStatus: 'pending' | 'uploading' | 'success' | 'failed'
  size: number               // 文件大小（字节）
}
```

#### 2.1.2 本地SQLite存储设计

```sql
-- 巡检任务表（离线支持）
CREATE TABLE IF NOT EXISTS inspection_task (
  task_id TEXT PRIMARY KEY,
  device_id INTEGER NOT NULL,
  device_name TEXT,
  device_code TEXT,
  template_id INTEGER,
  template_name TEXT,
  items_json TEXT,              -- JSON序列化的巡检项数组
  status TEXT DEFAULT 'pending',
  assignee_id INTEGER,
  assignee_name TEXT,
  scheduled_time TEXT,
  start_time TEXT,
  end_time TEXT,
  gps_latitude REAL,
  gps_longitude REAL,
  gps_accuracy REAL,
  gps_timestamp INTEGER,
  photos_json TEXT,              -- JSON序列化的照片数组
  remark TEXT,
  created_at TEXT,
  updated_at TEXT,
  is_offline INTEGER DEFAULT 0   -- 是否离线创建：0=在线拉取，1=离线创建
);

-- 巡检记录表（已提交的巡检历史）
CREATE TABLE IF NOT EXISTS inspection_history (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  task_id TEXT,
  device_id INTEGER,
  device_name TEXT,
  result TEXT,                   -- overall: 'normal' | 'abnormal'
  abnormal_count INTEGER DEFAULT 0,
  inspector_id INTEGER,
  inspector_name TEXT,
  completed_time TEXT,
  create_time INTEGER
);
```

### 2.2 巡检执行流程状态机

#### 2.2.1 状态流转设计

```
                    ┌──────────┐
                    │  pending  │  (任务创建/分配)
                    │  待执行    │
                    └─────┬────┘
                          │ 开始巡检（扫码/手动选择任务）
                          v
                    ┌──────────┐
                    │in_progress│  (逐项检查中)
                    │  进行中    │
                    └──┬───┬───┘
                       │   │
           全部正常且完成 │   │ 发现异常
                       │   v
                       │  ┌──────────┐
                       │  │ abnormal  │  (异常上报)
                       │  │  异常中    │
                       │  └─────┬────┘
                       │        │ 补充说明+照片后提交
                       │        v
                       v  ┌──────────┐
                    ┌──────┐│submitted│  (提交审核)
                    │completed│  已完成  │◄──────┘
                    │  已完成  │
                    └──────┘
                         │
                         │ 管理员驳回
                         v
                    ┌──────────┐
                    │ rejected │───(重新巡检)──► in_progress
                    │  已驳回    │
                    └──────────┘
                         │
              (过期/取消)  │
                    ┌──────────┐
                    │cancelled │
                    │  已取消    │
                    └──────────┘
```

#### 2.2.2 状态机实现

```typescript
// composables/useInspection.ts
import { ref, computed } from 'vue'
import type { InspectionStatus, InspectionTask, InspectionItem } from '@/types/entity'

/** 状态流转规则定义 */
const STATE_TRANSITIONS: Record<InspectionStatus, InspectionStatus[]> = {
  pending:     ['in_progress', 'cancelled'],
  in_progress: ['completed', 'submitted', 'cancelled'],
  submitted:   ['completed', 'rejected'],
  rejected:    ['in_progress', 'cancelled'],
  completed:   [],
  cancelled:   []
}

export function useInspection() {
  const currentTask = ref<InspectionTask | null>(null)
  const loading = ref(false)

  /** 检查状态是否允许流转到目标状态 */
  const canTransition = (from: InspectionStatus, to: InspectionStatus): boolean => {
    return STATE_TRANSITIONS[from]?.includes(to) || false
  }

  /** 检查所有巡检项是否已完成 */
  const isAllItemsCompleted = computed(() => {
    if (!currentTask.value?.items) return false
    return currentTask.value.items.every(
      item => !item.required || (item.result !== undefined && item.result !== '')
    )
  })

  /** 获取异常项数量 */
  const abnormalItemCount = computed(() => {
    if (!currentTask.value?.items) return 0
    return currentTask.value.items.filter(item => item.isAbnormal).length
  })

  /** 开始巡检 */
  const startInspection = async (taskId: string) => {
    loading.value = true
    try {
      // 1. 采集GPS定位
      const location = await getCurrentLocation()
      // 2. 更新任务状态为 in_progress
      currentTask.value = await inspectionApi.startTask(taskId, location)
      // 3. 离线时存入本地
      await saveTaskToLocal(currentTask.value)
    } finally {
      loading.value = false
    }
  }

  /** 更新巡检项结果 */
  const updateItemResult = (itemId: string, result: string, isAbnormal: boolean) => {
    if (!currentTask.value) return
    const item = currentTask.value.items.find(i => i.itemId === itemId)
    if (item) {
      item.result = result
      item.isAbnormal = isAbnormal
      // 自动保存到本地
      saveTaskToLocal(currentTask.value)
    }
  }

  /** 提交巡检结果 */
  const submitInspection = async () => {
    if (!currentTask.value) return
    loading.value = true
    try {
      const task = currentTask.value
      // 1. 上传所有待上传照片
      await uploadAllPhotos(task.photos)
      // 2. 提交巡检结果
      await inspectionApi.submitTask(task.taskId, {
        items: task.items,
        gpsLocation: task.gpsLocation,
        photos: task.photos.map(p => p.remoteUrl).filter(Boolean),
        remark: task.remark
      })
      task.status = 'submitted'
    } finally {
      loading.value = false
    }
  }

  return {
    currentTask,
    loading,
    canTransition,
    isAllItemsCompleted,
    abnormalItemCount,
    startInspection,
    updateItemResult,
    submitInspection
  }
}
```

### 2.3 GPS定位采集方案

```typescript
// utils/location.ts

interface LocationResult {
  latitude: number
  longitude: number
  accuracy: number
  altitude?: number
  timestamp: number
}

/** 获取当前位置（支持App/小程序/H5） */
export function getCurrentLocation(): Promise<LocationResult> {
  return new Promise((resolve, reject) => {
    uni.getLocation({
      type: 'gcj02',           // 国测局坐标，适用于国内地图
      altitude: true,          // 获取海拔信息
      geocode: false,          // 不需要逆地理编码
      accuracy: 'high',        // 高精度模式（App端）
      isHighAccuracy: true,   // 开启高精度（App端）
      highAccuracyExpireTime: 3000,  // 高精度超时3秒
      success: (res) => {
        resolve({
          latitude: res.latitude,
          longitude: res.longitude,
          accuracy: res.accuracy || 0,
          altitude: res.altitude,
          timestamp: Date.now()
        })
      },
      fail: (err) => {
        // 定位失败时使用默认位置（厂区中心坐标）
        console.warn('定位失败，使用默认位置', err)
        resolve({
          latitude: 30.5728,    // 默认纬度（项目配置）
          longitude: 104.0668, // 默认经度（项目配置）
          accuracy: 9999,
          timestamp: Date.now()
        })
      }
    })
  })
}

/** 计算两个坐标之间的距离（米） */
export function calculateDistance(
  lat1: number, lng1: number,
  lat2: number, lng2: number
): number {
  const R = 6371000  // 地球半径（米）
  const dLat = (lat2 - lat1) * Math.PI / 180
  const dLng = (lng2 - lng1) * Math.PI / 180
  const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
    Math.sin(dLng / 2) * Math.sin(dLng / 2)
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
  return R * c
}

/** 持续追踪位置（巡检路线记录） */
export function startLocationTracking(
  onLocationUpdate: (loc: LocationResult) => void,
  interval: number = 10000  // 默认10秒
): () => void {
  const timer = setInterval(() => {
    getCurrentLocation().then(onLocationUpdate)
  }, interval)

  // 返回停止追踪函数
  return () => clearInterval(timer)
}
```

### 2.4 拍照上传方案

#### 2.4.1 图片压缩方案

```typescript
// utils/image-compress.ts

interface CompressOptions {
  maxWidth: number       // 最大宽度（像素）
  maxHeight: number      // 最大高度（像素）
  quality: number        // 压缩质量 0-1
  maxSize: number        // 最大文件大小（字节），超过则进一步降低质量
}

const DEFAULT_COMPRESS_OPTIONS: CompressOptions = {
  maxWidth: 1920,
  maxHeight: 1080,
  quality: 0.8,
  maxSize: 2 * 1024 * 1024  // 2MB
}

/** 压缩图片（自适应质量调整） */
export async function compressImage(
  filePath: string,
  options: Partial<CompressOptions> = {}
): Promise<string> {
  const opts = { ...DEFAULT_COMPRESS_OPTIONS, ...options }

  return new Promise((resolve, reject) => {
    uni.compressImage({
      src: filePath,
      quality: Math.round(opts.quality * 100),
      success: async (res) => {
        let compressedPath = res.tempFilePath

        // 检查压缩后文件大小
        const fileInfo = await getFileInfo(compressedPath)
        if (fileInfo.size > opts.maxSize && opts.quality > 0.3) {
          // 如果仍超过最大限制，进一步降低质量
          const reducedQuality = Math.max(0.3, opts.quality - 0.2)
          compressedPath = await compressImage(filePath, {
            ...opts,
            quality: reducedQuality
          })
        }

        resolve(compressedPath)
      },
      fail: reject
    })
  })
}

/** 获取文件信息 */
function getFileInfo(filePath: string): Promise<{ size: number }> {
  return new Promise((resolve, reject) => {
    uni.getFileInfo({
      filePath,
      success: (res) => resolve({ size: res.size }),
      fail: reject
    })
  })
}
```

#### 2.4.2 批量上传方案

```typescript
// utils/upload.ts
import { compressImage } from './image-compress'

interface UploadProgress {
  total: number          // 总文件数
  completed: number      // 已完成数
  currentFile: string    // 当前上传文件名
  percent: number        // 当前进度百分比 0-100
  failedCount: number    // 失败数
}

interface UploadResult {
  originalPath: string   // 本地原始路径
  remoteUrl: string       // 远程URL
  thumbUrl?: string       // 缩略图URL
  status: 'success' | 'failed'
  error?: string
}

/** 批量上传图片（支持进度回调、断点续传） */
export async function batchUploadImages(
  files: string[],
  onProgress?: (progress: UploadProgress) => void,
  concurrency: number = 2  // 并发上传数
): Promise<UploadResult[]> {
  const results: UploadResult[] = []
  const queue = [...files]
  let completed = 0
  let failedCount = 0

  const uploadSingle = async (filePath: string): Promise<UploadResult> => {
    try {
      // 1. 压缩图片
      const compressedPath = await compressImage(filePath)

      // 2. 上传到服务器
      const remoteUrl = await uploadToServer(compressedPath, (percent) => {
        onProgress?.({
          total: files.length,
          completed: completed + (percent >= 100 ? 1 : 0),
          currentFile: filePath.split('/').pop() || '',
          percent,
          failedCount
        })
      })

      return { originalPath: filePath, remoteUrl, status: 'success' }
    } catch (error) {
      failedCount++
      return {
        originalPath: filePath,
        remoteUrl: '',
        status: 'failed',
        error: String(error)
      }
    } finally {
      completed++
    }
  }

  // 并发控制
  const workers = Array.from(
    { length: Math.min(concurrency, queue.length) },
    async () => {
      while (queue.length > 0) {
        const file = queue.shift()!
        const result = await uploadSingle(file)
        results.push(result)
      }
    }
  )

  await Promise.all(workers)

  onProgress?.({
    total: files.length,
    completed: results.filter(r => r.status === 'success').length,
    currentFile: '',
    percent: 100,
    failedCount: results.filter(r => r.status === 'failed').length
  })

  return results
}

/** 单文件上传（带进度） */
function uploadToServer(
  filePath: string,
  onProgress?: (percent: number) => void
): Promise<string> {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('access_token')
    const uploadTask = uni.uploadFile({
      url: `${import.meta.env.VITE_API_BASE_URL}/mobile/file/upload`,
      filePath,
      name: 'file',
      header: {
        'Authorization': `Bearer ${token}`
      },
      formData: {
        bizType: 'INSPECTION'
      },
      success: (res) => {
        const data = JSON.parse(res.data)
        if (data.code === 200) {
          resolve(data.data.url)
        } else {
          reject(new Error(data.message || '上传失败'))
        }
      },
      fail: reject
    })

    // 监听上传进度
    uploadTask.onProgressUpdate((res) => {
      onProgress?.(res.progress)
    })
  })
}
```

### 2.5 对接后端API方案

```typescript
// api/inspection.ts
import { http } from '@/utils/request'
import type { InspectionTask, InspectionStatus } from '@/types/entity'

export const inspectionApi = {
  /** 获取巡检任务列表 */
  getTaskList(params: {
    status?: InspectionStatus
    deviceId?: number
    page: number
    pageSize: number
  }) {
    return http.get<{ records: InspectionTask[]; total: number }>('/mobile/inspection/task/list', params)
  },

  /** 获取巡检任务详情 */
  getTaskDetail(taskId: string) {
    return http.get<InspectionTask>(`/mobile/inspection/task/${taskId}`)
  },

  /** 开始巡检（更新状态为进行中） */
  startTask(taskId: string, location: { latitude: number; longitude: number; accuracy: number }) {
    return http.put(`/mobile/inspection/task/${taskId}/start`, location)
  },

  /** 提交巡检结果 */
  submitTask(taskId: string, data: {
    items: any[]
    gpsLocation?: { latitude: number; longitude: number; accuracy: number }
    photos?: string[]
    remark?: string
  }) {
    return http.post(`/mobile/inspection/task/${taskId}/submit`, data)
  },

  /** 获取巡检模板详情 */
  getTemplate(templateId: number) {
    return http.get<any>(`/mobile/inspection/template/${templateId}`)
  },

  /** 上报异常 */
  reportAbnormal(taskId: string, data: {
    itemId: string
    abnormalDesc: string
    photos: string[]
    level: 'minor' | 'major' | 'critical'
  }) {
    return http.post(`/mobile/inspection/task/${taskId}/abnormal`, data)
  },

  /** 获取巡检历史 */
  getHistory(params: { deviceId?: number; startTime: string; endTime: string; page: number }) {
    return http.get<any>('/mobile/inspection/history', params)
  }
}
```

---

## 3. 报表增强

### 3.1 时间维度切换组件设计

#### 3.1.1 时间维度选择器组件

```vue
<!-- components/TimeDimensionPicker.vue -->
<template>
  <view class="time-dimension-picker">
    <view class="tab-group">
      <view
        v-for="dim in dimensions"
        :key="dim.value"
        class="tab-item"
        :class="{ active: modelValue === dim.value }"
        @click="selectDimension(dim.value)"
      >
        {{ dim.label }}
      </view>
    </view>

    <view class="date-range">
      <view class="date-item" @click="showCalendar = true">
        <u-icon name="calendar" size="16" />
        <text class="date-text">{{ displayDateRange }}</text>
        <u-icon name="arrow-down" size="14" />
      </view>
    </view>

    <!-- 日期范围快捷选择 -->
    <view class="shortcuts" v-if="modelValue !== 'year'">
      <u-button
        v-for="shortcut in currentShortcuts"
        :key="shortcut.label"
        size="mini"
        :type="isShortcutActive(shortcut) ? 'primary' : 'default'"
        :text="shortcut.label"
        @click="selectShortcut(shortcut)"
      />
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

interface DateRange {
  start: string   // YYYY-MM-DD
  end: string     // YYYY-MM-DD
}

interface Shortcut {
  label: string
  getRange: () => DateRange
}

const props = defineProps<{
  modelValue: 'day' | 'week' | 'month' | 'quarter' | 'year' | 'custom'
  dateRange: DateRange
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
  (e: 'update:dateRange', range: DateRange): void
  (e: 'change', dimension: string, range: DateRange): void
}>()

const dimensions = [
  { label: '日', value: 'day' },
  { label: '周', value: 'week' },
  { label: '月', value: 'month' },
  { label: '季', value: 'quarter' },
  { label: '年', value: 'year' }
]

const dayShortcuts: Shortcut[] = [
  { label: '今日', getRange: () => ({ start: today(), end: today() }) },
  { label: '昨日', getRange: () => ({ start: yesterday(), end: yesterday() }) },
  { label: '近7日', getRange: () => ({ start: daysAgo(6), end: today() }) },
  { label: '近30日', getRange: () => ({ start: daysAgo(29), end: today() }) }
]

const monthShortcuts: Shortcut[] = [
  { label: '本月', getRange: () => ({ start: monthStart(), end: today() }) },
  { label: '上月', getRange: () => ({ start: lastMonthStart(), end: lastMonthEnd() }) },
  { label: '近3月', getRange: () => ({ start: monthsAgo(2, 'start'), end: today() }) },
  { label: '近6月', getRange: () => ({ start: monthsAgo(5, 'start'), end: today() }) }
]

const currentShortcuts = computed(() => {
  if (['day', 'week'].includes(props.modelValue)) return dayShortcuts
  if (['month', 'quarter'].includes(props.modelValue)) return monthShortcuts
  return []
})

const displayDateRange = computed(() => `${props.dateRange.start} ~ ${props.dateRange.end}`)

function today() { return formatDate(new Date()) }
function yesterday() { return formatDate(new Date(Date.now() - 86400000)) }
function daysAgo(n: number) { return formatDate(new Date(Date.now() - n * 86400000)) }
function monthStart() { const d = new Date(); d.setDate(1); return formatDate(d) }
function lastMonthStart() { const d = new Date(); d.setMonth(d.getMonth() - 1); d.setDate(1); return formatDate(d) }
function lastMonthEnd() { const d = new Date(); d.setDate(0); return formatDate(d) }
function monthsAgo(n: number, part: 'start' | 'end' = 'start') {
  const d = new Date(); d.setMonth(d.getMonth() - n); d.setDate(part === 'start' ? 1 : 0); return formatDate(d)
}
function formatDate(d: Date) { return d.toISOString().split('T')[0] }

function selectDimension(value: string) {
  emit('update:modelValue', value)
}

function selectShortcut(shortcut: Shortcut) {
  const range = shortcut.getRange()
  emit('update:dateRange', range)
  emit('change', props.modelValue, range)
}

function isShortcutActive(shortcut: Shortcut) {
  const range = shortcut.getRange()
  return props.dateRange.start === range.start && props.dateRange.end === range.end
}
</script>

<style lang="scss" scoped>
.time-dimension-picker {
  .tab-group {
    display: flex;
    background: $bg-color;
    border-radius: 8rpx;
    padding: 4rpx;
    .tab-item {
      flex: 1;
      text-align: center;
      padding: 12rpx 0;
      font-size: 24rpx;
      border-radius: 6rpx;
      color: $text-regular;
      &.active {
        background: $primary;
        color: #fff;
        font-weight: 500;
      }
    }
  }
  .date-range { margin-top: 16rpx; }
  .date-item {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8rpx;
    padding: 16rpx;
    background: #fff;
    border-radius: 8rpx;
    .date-text { font-size: 26rpx; color: $text-primary; }
  }
  .shortcuts {
    display: flex;
    flex-wrap: wrap;
    gap: 16rpx;
    margin-top: 16rpx;
  }
}
</style>
```

### 3.2 同比/环比图表数据计算方案

```typescript
// composables/useReport.ts
import { ref, computed, watch } from 'vue'
import type { DateRange } from '@/types/entity'

interface ReportDataPoint {
  date: string        // 日期标签
  value: number       // 当前值
  yoyValue?: number    // 同比值（去年同期）
  qoqValue?: number    // 环比值（上期）
  yoyRate?: number     // 同比增长率（%）
  qoqRate?: number     // 环比增长率（%）
}

export function useReport(dataType: 'energy' | 'carbon') {
  const currentData = ref<ReportDataPoint[]>([])
  const compareData = ref<ReportDataPoint[]>([])
  const dimension = ref<'day' | 'week' | 'month' | 'quarter' | 'year'>('month')
  const dateRange = ref<DateRange>({ start: '', end: '' })
  const loading = ref(false)

  /** 计算同比/环比 */
  const processedData = computed(() => {
    return currentData.value.map((item, index) => {
      // 同比：与去年同期对比
      const yoyItem = compareData.value[index]
      const yoyValue = yoyItem?.value
      const yoyRate = yoyValue !== undefined && yoyValue !== 0
        ? ((item.value - yoyValue) / Math.abs(yoyValue) * 100)
        : undefined

      // 环比：与上期对比
      const prevItem = currentData.value[index - 1]
      const qoqValue = prevItem?.value
      const qoqRate = qoqValue !== undefined && qoqValue !== 0
        ? ((item.value - qoqValue) / Math.abs(qoqValue) * 100)
        : undefined

      return {
        ...item,
        yoyValue,
        qoqValue,
        yoyRate: yoyRate !== undefined ? Math.round(yoyRate * 100) / 100 : undefined,
        qoqRate: qoqRate !== undefined ? Math.round(qoqRate * 100) / 100 : undefined
      }
    })
  })

  /** 获取对比时间范围 */
  const getCompareRange = (range: DateRange, dim: string): DateRange => {
    const start = new Date(range.start)
    const end = new Date(range.end)
    const diffMs = end.getTime() - start.getTime()
    const diffDays = diffMs / 86400000

    switch (dim) {
      case 'day':
      case 'week':
        // 同比偏移365天
        return {
          start: formatDate(new Date(start.getTime() - 365 * 86400000)),
          end: formatDate(new Date(end.getTime() - 365 * 86400000))
        }
      case 'month':
      case 'quarter':
      case 'year':
        // 同比偏移1年
        return {
          start: `${new Date(start).getFullYear() - 1}-${range.start.slice(5)}`,
          end: `${new Date(end).getFullYear() - 1}-${range.end.slice(5)}`
        }
      default:
        return range
    }
  }

  /** 加载报表数据 */
  const loadReportData = async () => {
    loading.value = true
    try {
      const compareRange = getCompareRange(dateRange.value, dimension.value)

      // 并行请求当前数据和对比数据
      const [current, compare] = await Promise.all([
        http.get(`/report/${dataType}/detail`, {
          dimension: dimension.value,
          startDate: dateRange.value.start,
          endDate: dateRange.value.end
        }),
        http.get(`/report/${dataType}/detail`, {
          dimension: dimension.value,
          startDate: compareRange.start,
          endDate: compareRange.end
        })
      ])

      currentData.value = current
      compareData.value = compare
    } finally {
      loading.value = false
    }
  }

  /** 生成ECharts图表配置 */
  const chartOption = computed(() => {
    const categories = processedData.value.map(d => d.date)
    return {
      legend: { data: ['本期', '同期'], bottom: 0 },
      tooltip: {
        trigger: 'axis',
        formatter: (params: any[]) => {
          const date = params[0]?.axisValue
          let html = `<div style="font-weight:bold">${date}</div>`
          params.forEach((p: any) => {
            html += `<div>${p.marker} ${p.seriesName}: ${p.value}</div>`
          })
          // 附加同比/环比信息
          const item = processedData.value[params[0]?.dataIndex]
          if (item?.yoyRate !== undefined) {
            const color = item.yoyRate >= 0 ? '#F56C6C' : '#67C23A'
            html += `<div style="color:${color}">同比: ${item.yoyRate > 0 ? '+' : ''}${item.yoyRate}%</div>`
          }
          return html
        }
      },
      xAxis: { type: 'category', data: categories },
      yAxis: [
        { type: 'value', name: dataType === 'energy' ? '能耗(kWh)' : '碳排放(tCO2e)' },
        { type: 'value', name: '增长率(%)', splitLine: { show: false } }
      ],
      series: [
        { name: '本期', type: 'bar', data: processedData.value.map(d => d.value) },
        { name: '同期', type: 'line', yAxisIndex: 0, data: processedData.value.map(d => d.yoyValue) },
        {
          name: '同比率',
          type: 'line',
          yAxisIndex: 1,
          data: processedData.value.map(d => d.yoyRate),
          lineStyle: { type: 'dashed', color: '#E6A23C' }
        }
      ]
    }
  })

  return {
    currentData,
    compareData,
    processedData,
    dimension,
    dateRange,
    loading,
    loadReportData,
    chartOption
  }
}
```

### 3.3 PDF/Excel导出方案

#### 3.3.1 H5端（jsPDF + SheetJS）

```typescript
// utils/export.ts
import { http } from '@/utils/request'

#ifdef H5
import jsPDF from 'jspdf'
import 'jspdf-autotable'
import * as XLSX from 'xlsx'
#endif

/** 导出PDF（H5端） */
export async function exportPDF(params: {
  title: string
  type: 'energy' | 'carbon'
  dimension: string
  dateRange: { start: string; end: string }
  columns: { key: string; title: string; width?: number }[]
}): Promise<Blob> {
  #ifdef H5
  // 1. 获取报表数据
  const data = await http.get<any[]>(`/report/${params.type}/export`, {
    dimension: params.dimension,
    startDate: params.dateRange.start,
    endDate: params.dateRange.end
  })

  // 2. 生成PDF
  const doc = new jsPDF({ orientation: 'landscape', unit: 'mm', format: 'a4' })

  // 标题
  doc.setFontSize(16)
  doc.text(params.title, 14, 20)

  // 副标题（日期范围）
  doc.setFontSize(10)
  doc.setTextColor(150)
  doc.text(
    `${params.dateRange.start} 至 ${params.dateRange.end}    导出时间：${new Date().toLocaleString()}`,
    14, 28
  )

  // 数据表格
  doc.autoTable({
    startY: 35,
    head: [params.columns.map(c => c.title)],
    body: data.map(row => params.columns.map(c => row[c.key])),
    styles: { fontSize: 8, cellPadding: 2 },
    headStyles: { fillColor: [64, 158, 255] },
    alternateRowStyles: { fillColor: [245, 247, 250] },
    margin: { left: 14, right: 14 }
  })

  return doc.output('blob')
  #endif

  // 非H5端：走后端生成
  #ifndef H5
  return await http.post('/report/export/pdf', params, {
    responseType: 'arraybuffer'
  })
  #endif
}

/** 导出Excel（H5端） */
export async function exportExcel(params: {
  title: string
  type: 'energy' | 'carbon'
  dimension: string
  dateRange: { start: string; end: string }
  columns: { key: string; title: string }[]
}): Promise<Blob> {
  #ifdef H5
  // 1. 获取报表数据
  const data = await http.get<any[]>(`/report/${params.type}/export`, {
    dimension: params.dimension,
    startDate: params.dateRange.start,
    endDate: params.dateRange.end
  })

  // 2. 构建工作表数据
  const wsData = [
    [params.title],
    [`时间范围: ${params.dateRange.start} ~ ${params.dateRange.end}`],
    [],  // 空行
    params.columns.map(c => c.title),  // 表头
    ...data.map(row => params.columns.map(c => row[c.key]))
  ]

  // 3. 创建工作簿并导出
  const ws = XLSX.utils.aoa_to_sheet(wsData)
  ws['!cols'] = params.columns.map((c, i) => ({
    wch: c.width || (c.title.length * 2 + 4)
  }))

  // 合并标题行
  ws['!merges'] = [
    { s: { r: 0, c: 0 }, e: { r: 0, c: params.columns.length - 1 } },
    { s: { r: 1, c: 0 }, e: { r: 1, c: params.columns.length - 1 } }
  ]

  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, params.title)

  // 导出为ArrayBuffer
  const wbOut = XLSX.write(wb, { bookType: 'xlsx', type: 'array' })
  return new Blob([wbOut], { type: 'application/octet-stream' })
  #endif

  // 非H5端：走后端生成
  #ifndef H5
  return await http.post('/report/export/excel', params, {
    responseType: 'arraybuffer'
  })
  #endif
}

/** 下载文件（通用） */
export function downloadFile(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  a.click()
  URL.revokeObjectURL(url)
}
```

#### 3.3.2 小程序端（云函数/后端生成）

```typescript
// 小程序端导出方案：通过后端生成文件，返回下载链接

// api/export.ts
export const exportApi = {
  /** 请求后端生成PDF/Excel，返回文件URL */
  requestExport(params: {
    type: 'pdf' | 'excel'
    reportType: 'energy' | 'carbon'
    dimension: string
    dateRange: { start: string; end: string }
  }) {
    return http.post<{ fileUrl: string; fileName: string }>('/report/export/request', params)
  },

  /** 小程序端下载文件 */
  downloadFile(fileUrl: string, fileName: string) {
    return new Promise((resolve, reject) => {
      uni.downloadFile({
        url: fileUrl,
        success: (res) => {
          if (res.statusCode === 200) {
            // 打开文件（预览）
            uni.openDocument({
              filePath: res.tempFilePath,
              fileType: fileName.endsWith('.pdf') ? 'pdf' : 'xlsx',
              showMenu: true,  // 允许转发/保存
              success: resolve,
              fail: reject
            })
          }
        },
        fail: reject
      })
    })
  }
}
```

### 3.4 图表组件增强

#### 3.4.1 增强版图表组件

```vue
<!-- components/EnhancedChart.vue -->
<template>
  <view class="enhanced-chart">
    <!-- 图表类型切换 -->
    <view class="chart-type-switch" v-if="enableTypeSwitch">
      <view
        v-for="type in chartTypes"
        :key="type.value"
        class="type-item"
        :class="{ active: currentType === type.value }"
        @click="switchChartType(type.value)"
      >
        <u-icon :name="type.icon" size="16" />
        <text>{{ type.label }}</text>
      </view>
    </view>

    <!-- 图表容器 -->
    <view class="chart-wrapper" :style="{ height: height + 'rpx' }">
      <!-- #ifdef APP-PLUS || H5 -->
      <canvas
        :canvas-id="canvasId"
        :id="canvasId"
        class="chart-canvas"
        @touchstart="onTouchStart"
        @touchmove="onTouchMove"
        @touchend="onTouchEnd"
      />
      <!-- #endif -->

      <!-- #ifdef MP-WEIXIN -->
      <ec-canvas :canvas-id="canvasId" :ec="ec" class="chart-canvas" />
      <!-- #endif -->
    </view>

    <!-- 数据摘要 -->
    <view class="chart-summary" v-if="summaryData.length > 0">
      <view v-for="item in summaryData" :key="item.label" class="summary-item">
        <text class="summary-label">{{ item.label }}</text>
        <text class="summary-value" :style="{ color: item.color }">{{ item.value }}</text>
        <text class="summary-change" :class="item.change >= 0 ? 'up' : 'down'">
          {{ item.change >= 0 ? '+' : '' }}{{ item.change }}%
        </text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

interface ChartType {
  value: 'bar' | 'line' | 'pie' | 'mixed'
  label: string
  icon: string
}

interface SummaryItem {
  label: string
  value: string
  color: string
  change: number
}

const props = withDefaults(defineProps<{
  option: any
  height?: number
  enableTypeSwitch?: boolean
  defaultType?: 'bar' | 'line' | 'pie' | 'mixed'
  summaryData?: SummaryItem[]
}>(), {
  height: 500,
  enableTypeSwitch: false,
  defaultType: 'bar',
  summaryData: () => []
})

const emit = defineEmits<{
  (e: 'typeChange', type: string): void
}>()

const chartTypes: ChartType[] = [
  { value: 'bar', label: '柱状图', icon: 'bar-chart' },
  { value: 'line', label: '折线图', icon: 'line-chart' },
  { value: 'pie', label: '饼图', icon: 'pie-chart' },
  { value: 'mixed', label: '混合图', icon: 'mix-chart' }
]

const currentType = ref(props.defaultType)
const canvasId = ref(`chart_${Date.now()}`)
let chartInstance: echarts.ECharts | null = null

/** 初始化图表 */
const initChart = () => {
  const canvasDom = document.getElementById(canvasId.value) as HTMLCanvasElement
  if (canvasDom) {
    chartInstance = echarts.init(canvasDom)
    chartInstance.setOption(props.option)
  }
}

/** 切换图表类型 */
const switchChartType = (type: string) => {
  currentType.value = type as any
  emit('typeChange', type)
  updateChartOption()
}

/** 根据类型更新图表配置 */
const updateChartOption = () => {
  if (!chartInstance) return
  const opt = { ...props.option }

  switch (currentType.value) {
    case 'bar':
      opt.series = opt.series.map((s: any) => ({ ...s, type: 'bar' }))
      break
    case 'line':
      opt.series = opt.series.map((s: any) => ({ ...s, type: 'line', smooth: true }))
      break
    case 'pie':
      opt.series = [{
        type: 'pie',
        radius: ['40%', '70%'],
        data: opt.series[0]?.data || []
      }]
      break
    case 'mixed':
      // 柱状图 + 折线图混合
      opt.series = opt.series.map((s: any, i: number) => ({
        ...s,
        type: i === 0 ? 'bar' : 'line'
      }))
      break
  }

  chartInstance?.setOption(opt, { notMerge: true })
}

onMounted(() => {
  nextTick(initChart)
})

watch(() => props.option, () => {
  chartInstance?.setOption(props.option)
}, { deep: true })

// 触摸事件处理（移动端图表交互）
const onTouchStart = (e: TouchEvent) => chartInstance?.dispatchAction({ type: 'highlight' })
const onTouchMove = (e: TouchEvent) => chartInstance?.dispatchAction({ type: 'showTip' })
const onTouchEnd = () => chartInstance?.dispatchAction({ type: 'downplay' })
</script>
```

---

## 4. 消息中心

### 4.1 消息推送方案

#### 4.1.1 双通道推送架构

```
┌─────────────────────────────────────────────────────┐
│                    消息推送架构                        │
│                                                      │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐       │
│  │ 告警触发  │───>│ 后端消息  │───>│ 消息分发  │       │
│  │ 审批通知  │    │ 处理服务  │    │   路由    │       │
│  │ 系统公告  │    │          │    │          │       │
│  └──────────┘    └──────────┘    └─────┬────┘       │
│                                       │             │
│                          ┌────────────┴─────────┐  │
│                          v                      v  │
│                   ┌──────────┐          ┌──────────┐│
│                   │WebSocket │          │ 极光推送  ││
│                   │ 实时通道   │          │ App原生   ││
│                   └─────┬────┘          └─────┬────┘│
│                         │                    │     │
│            ┌────────────┼────────────┐        │     │
│            v            v            v        v     │
│          H5端      小程序端      App前台   App后台   │
│       (WebSocket)  (轮询/订阅)  (WS+展示)  (通知栏)  │
└─────────────────────────────────────────────────────┘
```

#### 4.1.2 WebSocket实时推送

```typescript
// utils/websocket.ts

interface WSMessage {
  type: 'alarm' | 'approval' | 'announcement' | 'system'
  title: string
  content: string
  data?: any
  timestamp: number
  id: string
}

class WebSocketManager {
  private ws: UniApp.SocketTask | null = null
  private reconnectTimer: ReturnType<typeof setTimeout> | null = null
  private heartbeatTimer: ReturnType<typeof setInterval> | null = null
  private reconnectCount = 0
  private maxReconnectCount = 5
  private listeners: Map<string, ((msg: WSMessage) => void)[]> = new Map()
  private url = ''

  /** 建立WebSocket连接 */
  connect(token: string) {
    const baseUrl = import.meta.env.VITE_WS_BASE_URL
    this.url = `${baseUrl}/ws/message?token=${token}`

    #ifdef H5
    this.connectH5(this.url)
    #endif

    #ifndef H5
    this.connectUni(this.url)
    #endif
  }

  /** H5端WebSocket连接 */
  #ifdef H5
  private wsNative: WebSocket | null = null
  private connectH5(url: string) {
    this.wsNative = new WebSocket(url)

    this.wsNative.onopen = () => {
      console.log('[WS] 已连接')
      this.reconnectCount = 0
      this.startHeartbeat()
    }

    this.wsNative.onmessage = (event) => {
      const msg: WSMessage = JSON.parse(event.data)
      this.handleMessage(msg)
    }

    this.wsNative.onclose = () => {
      this.stopHeartbeat()
      this.autoReconnect()
    }

    this.wsNative.onerror = (error) => {
      console.error('[WS] 连接错误', error)
    }
  }
  #endif

  /** UniApp端Socket连接 */
  #ifndef H5
  private connectUni(url: string) {
    this.ws = uni.connectSocket({
      url,
      complete: () => {}
    })

    this.ws.onOpen(() => {
      console.log('[WS] 已连接')
      this.reconnectCount = 0
      this.startHeartbeat()
    })

    this.ws.onMessage((res) => {
      const msg: WSMessage = JSON.parse(res.data as string)
      this.handleMessage(msg)
    })

    this.ws.onClose(() => {
      this.stopHeartbeat()
      this.autoReconnect()
    })

    this.ws.onError((err) => {
      console.error('[WS] 错误', err)
    })
  }
  #endif

  /** 消息处理 */
  private handleMessage(msg: WSMessage) {
    // 1. 存入本地消息缓存
    this.saveToLocal(msg)
    // 2. 分发给监听器
    const handlers = this.listeners.get(msg.type) || []
    const allHandlers = this.listeners.get('*') || []
    ;[...handlers, ...allHandlers].forEach(fn => fn(msg))
    // 3. 更新未读数
    this.updateUnreadCount()
  }

  /** 注册消息监听 */
  on(type: string | '*', handler: (msg: WSMessage) => void) {
    if (!this.listeners.has(type)) {
      this.listeners.set(type, [])
    }
    this.listeners.get(type)!.push(handler)
  }

  /** 移除消息监听 */
  off(type: string | '*', handler: (msg: WSMessage) => void) {
    const handlers = this.listeners.get(type)
    if (handlers) {
      const idx = handlers.indexOf(handler)
      if (idx > -1) handlers.splice(idx, 1)
    }
  }

  /** 保存消息到本地 */
  private saveToLocal(msg: WSMessage) {
    const messages = JSON.parse(uni.getStorageSync('messages') || '[]')
    messages.unshift(msg)
    // 最多保留200条本地消息
    if (messages.length > 200) messages.length = 200
    uni.setStorageSync('messages', JSON.stringify(messages))
  }

  /** 更新未读数 */
  private updateUnreadCount() {
    const messages = JSON.parse(uni.getStorageSync('messages') || '[]')
    const unread = messages.filter((m: WSMessage) => !m.data?.isRead).length
    uni.setStorageSync('unread_message_count', unread)
    // 更新TabBar徽标
    if (unread > 0) {
      uni.setTabBarBadge({ index: 3, text: String(unread > 99 ? '99+' : unread) })
    } else {
      uni.removeTabBarBadge({ index: 3 })
    }
  }

  /** 心跳检测 */
  private startHeartbeat() {
    this.heartbeatTimer = setInterval(() => {
      this.send({ type: 'heartbeat', content: '', timestamp: Date.now(), id: '' } as WSMessage)
    }, 30000)
  }

  private stopHeartbeat() {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer)
      this.heartbeatTimer = null
    }
  }

  /** 发送消息 */
  send(data: WSMessage) {
    const json = JSON.stringify(data)
    #ifdef H5
    this.wsNative?.send(json)
    #endif
    #ifndef H5
    this.ws?.send({ data: json })
    #endif
  }

  /** 自动重连 */
  private autoReconnect() {
    if (this.reconnectCount >= this.maxReconnectCount) {
      console.warn('[WS] 超过最大重连次数，停止重连')
      return
    }
    const delay = Math.min(1000 * Math.pow(2, this.reconnectCount), 30000)
    this.reconnectTimer = setTimeout(() => {
      this.reconnectCount++
      const token = uni.getStorageSync('access_token')
      if (token) this.connect(token)
    }, delay)
  }

  /** 断开连接 */
  disconnect() {
    this.stopHeartbeat()
    if (this.reconnectTimer) clearTimeout(this.reconnectTimer)
    #ifdef H5
    this.wsNative?.close()
    this.wsNative = null
    #endif
    #ifndef H5
    this.ws?.close({})
    this.ws = null
    #endif
  }
}

export const wsManager = new WebSocketManager()
```

#### 4.1.3 App端极光推送集成

```typescript
// utils/jpush.ts

#ifdef APP-PLUS
/** 极光推送初始化 */
export function initJPush() {
  const jpush = uni.requireNativePlugin('JG-JPush')

  // 初始化极光SDK
  jpush.init()

  // 获取注册ID
  jpush.getRegistrationID((result: any) => {
    if (result.registrationID) {
      // 上报到后端
      http.post('/mobile/push/register', {
        clientId: result.registrationID,
        platform: plus.os.name.toLowerCase() === 'ios' ? 'ios' : 'android',
        deviceToken: result.registrationID
      })
    }
  })

  // 监听推送通知点击
  plus.push.addEventListener('click', (msg: any) => {
    // 通知点击事件
    const payload = JSON.parse(msg.payload || '{}')
    navigateToMessageDetail(payload)
  })

  // 监听推送消息接收（前台）
  plus.push.addEventListener('receive', (msg: any) => {
    const payload = JSON.parse(msg.payload || '{}')
    // 前台收到推送，同步到消息列表
    wsManager.saveToLocal({
      type: payload.type || 'system',
      title: msg.title,
      content: msg.content,
      data: payload,
      timestamp: Date.now(),
      id: payload.id || String(Date.now())
    })
    wsManager.updateUnreadCount()
  })
}

/** 跳转到消息详情 */
function navigateToMessageDetail(payload: any) {
  switch (payload.type) {
    case 'alarm':
      uni.navigateTo({ url: `/pages/alarm/detail?id=${payload.alarmId}` })
      break
    case 'inspection':
      uni.navigateTo({ url: `/pages/inspection/detail?taskId=${payload.taskId}` })
      break
    case 'approval':
      uni.navigateTo({ url: `/pages/approval/detail?id=${payload.approvalId}` })
      break
    default:
      uni.navigateTo({ url: '/pages/message/list' })
  }
}
#endif
```

### 4.2 本地消息缓存方案

```typescript
// stores/message.ts
import { defineStore } from 'pinia'
import type { WSMessage } from '@/utils/websocket'

interface MessageState {
  messages: WSMessage[]
  unreadCount: number
  loading: boolean
}

interface MessageFilter {
  type?: 'alarm' | 'approval' | 'announcement' | 'system'
  readStatus?: 'all' | 'read' | 'unread'
  startTime?: string
  endTime?: string
}

export const useMessageStore = defineStore('message', {
  state: (): MessageState => ({
    messages: [],
    unreadCount: 0,
    loading: false
  }),

  getters: {
    /** 按类型分组的消息 */
    groupedMessages: (state) => {
      const groups: Record<string, WSMessage[]> = {}
      state.messages.forEach(msg => {
        if (!groups[msg.type]) groups[msg.type] = []
        groups[msg.type].push(msg)
      })
      return groups
    },

    /** 最新未读消息 */
    latestUnread: (state) => {
      return state.messages.find(m => !m.data?.isRead)
    }
  },

  actions: {
    /** 加载本地缓存消息 */
    loadCachedMessages() {
      const cached = uni.getStorageSync('messages')
      if (cached) {
        this.messages = JSON.parse(cached)
        this.updateUnreadCount()
      }
    },

    /** 接收新消息 */
    receiveMessage(msg: WSMessage) {
      this.messages.unshift(msg)
      // 限制本地缓存数量
      if (this.messages.length > 200) {
        this.messages = this.messages.slice(0, 200)
      }
      this.persistMessages()
      this.updateUnreadCount()
    },

    /** 标记消息已读 */
    markAsRead(messageId: string) {
      const msg = this.messages.find(m => m.id === messageId)
      if (msg) {
        if (!msg.data) msg.data = {}
        msg.data.isRead = true
        this.persistMessages()
        this.updateUnreadCount()
      }
    },

    /** 标记全部已读 */
    markAllAsRead() {
      this.messages.forEach(msg => {
        if (!msg.data) msg.data = {}
        msg.data.isRead = true
      })
      this.persistMessages()
      this.updateUnreadCount()
      // 同步到后端
      http.put('/mobile/message/read-all')
    },

    /** 删除消息 */
    deleteMessage(messageId: string) {
      const idx = this.messages.findIndex(m => m.id === messageId)
      if (idx > -1) {
        this.messages.splice(idx, 1)
        this.persistMessages()
        this.updateUnreadCount()
      }
    },

    /** 清空所有消息 */
    clearAll() {
      this.messages = []
      this.persistMessages()
      this.updateUnreadCount()
    },

    /** 过滤消息 */
    getFilteredMessages(filter: MessageFilter): WSMessage[] {
      return this.messages.filter(msg => {
        if (filter.type && msg.type !== filter.type) return false
        if (filter.readStatus === 'read' && !msg.data?.isRead) return false
        if (filter.readStatus === 'unread' && msg.data?.isRead) return false
        return true
      })
    },

    /** 更新未读数 */
    updateUnreadCount() {
      this.unreadCount = this.messages.filter(m => !m.data?.isRead).length
      // 同步到TabBar徽标（消息中心Tab index = 3）
      if (this.unreadCount > 0) {
        uni.setTabBarBadge({ index: 3, text: String(this.unreadCount > 99 ? '99+' : this.unreadCount) })
      } else {
        uni.removeTabBarBadge({ index: 3 })
      }
    },

    /** 持久化消息 */
    persistMessages() {
      uni.setStorageSync('messages', JSON.stringify(this.messages))
    }
  }
})
```

### 4.3 消息已读/未读状态管理

```
┌────────────────────────────────────────────┐
│             消息已读/未读状态管理              │
│                                             │
│  ┌───────────┐     ┌───────────────┐       │
│  │ 消息到达    │────>│ 本地标记未读   │       │
│  │ (WS/推送)   │     │ 更新未读计数   │       │
│  └───────────┘     └───────┬───────┘       │
│                            │                │
│              ┌─────────────┼───────────┐    │
│              v             v           v    │
│       ┌──────────┐  ┌──────────┐  ┌──────┐  │
│       │ 用户点击  │  │ 全部已读  │  │ 滑动  │  │
│       │ 单条消息  │  │ 批量标记  │  │ 列表  │  │
│       └────┬─────┘  └────┬─────┘  └──┬───┘  │
│            v             v           v      │
│       ┌─────────────────────────────┐     │
│       │ 本地更新已读状态 + 持久化      │     │
│       │ 后端同步 (PUT /message/read)  │     │
│       │ 更新TabBar徽标               │     │
│       └─────────────────────────────┘     │
└────────────────────────────────────────────┘
```

---

## 5. 离线功能增强

### 5.1 离线数据队列优化

#### 5.1.1 增强版同步队列

```typescript
// stores/sync-v2.ts
import { defineStore } from 'pinia'

interface EnhancedSyncTask {
  id: string
  api: string
  method: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data: any
  timestamp: number
  retryCount: number
  maxRetry: number
  status: 'pending' | 'syncing' | 'success' | 'failed' | 'conflict'
  priority: number            // 优先级（数字越小优先级越高）
  bizType: string             // 业务类型（meter_reading / inspection / carbon_entry）
  localRecordId?: string       // 关联的本地记录ID
  conflictInfo?: {            // 冲突信息
    serverData: any
    localData: any
    conflictField: string
  }
}

interface SyncStatistics {
  total: number
  pending: number
  syncing: number
  success: number
  failed: number
  conflict: number
}

export const useSyncStoreV2 = defineStore('sync-v2', {
  state: () => ({
    queue: [] as EnhancedSyncTask[],
    isSyncing: false,
    lastSyncTime: uni.getStorageSync('last_sync_time') || 0,
    syncStats: { total: 0, pending: 0, syncing: 0, success: 0, failed: 0, conflict: 0 } as SyncStatistics
  }),

  getters: {
    pendingCount: (state) => state.queue.filter(t => t.status === 'pending').length,
    failedCount: (state) => state.queue.filter(t => t.status === 'failed').length,
    conflictCount: (state) => state.queue.filter(t => t.status === 'conflict').length,
    /** 按优先级排序的待处理任务 */
    sortedPendingTasks: (state) => {
      return state.queue
        .filter(t => t.status === 'pending')
        .sort((a, b) => a.priority - b.priority)
    }
  },

  actions: {
    /** 添加同步任务（增强版） */
    addTask(task: {
      api: string
      method: string
      data: any
      priority?: number
      bizType: string
      localRecordId?: string
      maxRetry?: number
    }) {
      const newTask: EnhancedSyncTask = {
        id: `sync_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
        api: task.api,
        method: task.method as any,
        data: task.data,
        timestamp: Date.now(),
        retryCount: 0,
        maxRetry: task.maxRetry || 3,
        status: 'pending',
        priority: task.priority ?? 5,
        bizType: task.bizType,
        localRecordId: task.localRecordId
      }
      this.queue.push(newTask)
      this.updateStats()
      this.persistQueue()

      // 触发自动同步
      if (this.isOnline) {
        this.processQueue()
      }
    },

    /** 处理同步队列（增强版 - 支持冲突检测） */
    async processQueue() {
      if (this.isSyncing) return
      this.isSyncing = true

      const tasks = this.sortedPendingTasks

      for (const task of tasks) {
        try {
          task.status = 'syncing'
          this.updateStats()

          // 发送请求（带冲突检测头）
          const response = await uni.request({
            url: task.api,
            method: task.method,
            data: {
              ...task.data,
              _clientTimestamp: task.timestamp,
              _localRecordId: task.localRecordId
            },
            header: {
              'X-Sync-Mode': 'offline-sync',
              'X-Local-Timestamp': String(task.timestamp)
            }
          })

          const data = response.data as any

          // 检查冲突
          if (data.code === 409) {
            // 数据冲突
            task.status = 'conflict'
            task.conflictInfo = {
              serverData: data.data.serverData,
              localData: data.data.localData,
              conflictField: data.data.conflictField
            }
          } else if (data.code === 200) {
            task.status = 'success'
            // 从队列中移除成功任务
            this.removeTask(task.id)
          } else {
            throw new Error(data.message)
          }
        } catch (e) {
          task.retryCount++
          if (task.retryCount >= task.maxRetry) {
            task.status = 'failed'
          } else {
            task.status = 'pending'
          }
        }
      }

      this.lastSyncTime = Date.now()
      uni.setStorageSync('last_sync_time', this.lastSyncTime)
      this.isSyncing = false
      this.updateStats()
      this.persistQueue()
    },

    /** 解决冲突 */
    resolveConflict(taskId: string, resolution: 'useLocal' | 'useServer' | 'merge') {
      const task = this.queue.find(t => t.id === taskId)
      if (!task || !task.conflictInfo) return

      switch (resolution) {
        case 'useLocal':
          // 使用本地数据覆盖服务端，重新提交
          task.status = 'pending'
          task.retryCount = 0
          break
        case 'useServer':
          // 放弃本地数据，使用服务端数据
          this.removeTask(task.id)
          break
        case 'merge':
          // 合并数据（由业务层处理合并逻辑）
          task.data = this.mergeData(task.conflictInfo.serverData, task.conflictInfo.localData)
          task.status = 'pending'
          task.retryCount = 0
          break
      }

      this.updateStats()
      this.persistQueue()
    },

    /** 数据合并（通用策略） */
    mergeData(serverData: any, localData: any): any {
      const merged = { ...serverData }
      for (const key of Object.keys(localData)) {
        // 本地数据的时间戳更新，则使用本地值
        if (localData._updatedAt > (serverData._updatedAt || 0)) {
          merged[key] = localData[key]
        }
      }
      return merged
    },

    /** 移除任务 */
    removeTask(taskId: string) {
      const idx = this.queue.findIndex(t => t.id === taskId)
      if (idx > -1) this.queue.splice(idx, 1)
    },

    /** 更新统计信息 */
    updateStats() {
      this.syncStats = {
        total: this.queue.length,
        pending: this.queue.filter(t => t.status === 'pending').length,
        syncing: this.queue.filter(t => t.status === 'syncing').length,
        success: 0, // 成功的已移除
        failed: this.queue.filter(t => t.status === 'failed').length,
        conflict: this.queue.filter(t => t.status === 'conflict').length
      }
    },

    /** 持久化队列 */
    persistQueue() {
      uni.setStorageSync('sync_queue_v2', JSON.stringify(this.queue))
    },

    /** 初始化队列（从本地恢复） */
    initQueue() {
      const cached = uni.getStorageSync('sync_queue_v2')
      if (cached) {
        this.queue = JSON.parse(cached)
        this.updateStats()
      }
    }
  }
})
```

### 5.2 数据冲突检测与合并策略

| 冲突类型 | 检测方式 | 合并策略 | 用户交互 |
|----------|----------|----------|----------|
| **时间戳冲突** | 服务端数据修改时间 > 离线创建时间 | 服务端优先，提示用户数据已更新 | 弹窗提示，用户选择"查看最新"或"覆盖" |
| **唯一性冲突** | 同一设备+同一日期重复录入 | 拒绝提交，提示重复 | 弹窗提示，用户确认是否覆盖 |
| **删除冲突** | 离线修改的记录已被服务端删除 | 放弃离线修改，标记为失败 | 列表标记红色，提示"记录已被删除" |
| **字段级冲突** | 同一记录不同字段被修改 | 字段级合并（各取最新） | 自动合并，差异高亮显示 |
| **多人冲突** | 多人同时修改同一记录 | 乐观锁（version字段） | 弹窗展示差异，用户选择保留版本 |

### 5.3 存储容量管理

```typescript
// utils/storage-manager.ts

interface StorageInfo {
  totalUsed: number          // 已使用容量（字节）
  limit: number              // 容量上限（字节）
  usagePercent: number        // 使用百分比
  breakdown: {
    syncQueue: number         // 同步队列占用
    messages: number         // 消息缓存占用
    images: number            // 图片缓存占用
    sqlite: number            // SQLite数据库占用
    other: number             // 其他缓存
  }
}

/** 存储容量管理器 */
export const storageManager = {
  /** 获取存储使用情况 */
  getStorageInfo(): StorageInfo {
    const info = uni.getStorageInfoSync()
    return {
      totalUsed: info.currentSize * 1024,  // KB -> Bytes
      limit: info.limitSize * 1024,
      usagePercent: (info.currentSize / info.limitSize) * 100,
      breakdown: {
        syncQueue: this.getStorageSize('sync_queue_v2'),
        messages: this.getStorageSize('messages'),
        images: this.calculateImageCacheSize(),
        sqlite: this.getSQLiteSize(),
        other: 0  // 计算得出
      }
    }
  },

  /** 清理策略（按优先级从低到高清理） */
  cleanup(lowMemory: boolean = false) {
    // 1. 清理过期图片缓存（超过7天未使用）
    this.cleanExpiredImageCache(7)

    if (lowMemory) {
      // 2. 清理已完成/已失败的同步记录（保留最近10条）
      this.cleanOldSyncRecords(10)

      // 3. 清理已读消息（保留最近50条）
      this.cleanReadMessages(50)

      // 4. 清理过期仪表盘缓存（超过1小时）
      this.cleanExpiredCache('dashboard_summary', 3600000)
    }
  },

  /** 自动存储监控 */
  startMonitoring(checkInterval: number = 300000) {
    setInterval(() => {
      const info = this.getStorageInfo()

      // 超过80%触发清理
      if (info.usagePercent > 80) {
        this.cleanup(true)
        uni.showToast({
          title: '存储空间不足，已自动清理部分缓存',
          icon: 'none'
        })
      }

      // 超过95%警告
      if (info.usagePercent > 95) {
        uni.showModal({
          title: '存储空间严重不足',
          content: '请清理应用数据或释放设备存储空间',
          showCancel: false
        })
      }
    }, checkInterval)
  },

  // ... 辅助方法省略
}
```

---

## 6. 主题切换

### 6.1 CSS变量主题方案

#### 6.1.1 主题变量定义

```scss
// uni.scss - 主题变量定义（使用CSS自定义属性）

:root,
page {
  /* 主色调 */
  --primary-color: #409EFF;
  --primary-light-color: #ECF5FF;
  --primary-dark-color: #337ECC;

  /* 功能色 */
  --success-color: #67C23A;
  --warning-color: #E6A23C;
  --danger-color: #F56C6C;
  --info-color: #909399;

  /* 背景色 */
  --bg-color: #F5F7FA;
  --card-bg-color: #FFFFFF;
  --page-bg-color: #F5F7FA;

  /* 文本色 */
  --text-primary-color: #303133;
  --text-regular-color: #606266;
  --text-secondary-color: #909399;
  --text-placeholder-color: #C0C4CC;

  /* 边框色 */
  --border-color: #DCDFE6;
  --divider-color: #EBEEF5;

  /* 阴影 */
  --shadow-color: rgba(0, 0, 0, 0.06);
  --shadow-hover: rgba(0, 0, 0, 0.12);

  /* 圆角 */
  --card-radius: 16rpx;
  --button-radius: 8rpx;

  /* 暗色模式变量覆盖 */
  &[data-theme="dark"] {
    --primary-color: #4A8FE7;
    --primary-light-color: #1A2A3A;
    --primary-dark-color: #5A9FF7;

    --success-color: #5DAF34;
    --warning-color: #D4932C;
    --danger-color: #E25A5A;
    --info-color: #7A7D82;

    --bg-color: #141414;
    --card-bg-color: #1E1E1E;
    --page-bg-color: #0A0A0A;

    --text-primary-color: #E5EAF3;
    --text-regular-color: #CFD3DC;
    --text-secondary-color: #8F95A2;
    --text-placeholder-color: #5A6070;

    --border-color: #2C2C2C;
    --divider-color: #252525;

    --shadow-color: rgba(0, 0, 0, 0.3);
    --shadow-hover: rgba(0, 0, 0, 0.5);
  }
}
```

#### 6.1.2 主题切换Store

```typescript
// stores/theme.ts
import { defineStore } from 'pinia'

export type ThemeMode = 'light' | 'dark' | 'auto'

export const useThemeStore = defineStore('theme', {
  state: () => ({
    mode: (uni.getStorageSync('theme_mode') || 'light') as ThemeMode,
    isDark: false
  }),

  actions: {
    /** 设置主题 */
    setTheme(mode: ThemeMode) {
      this.mode = mode
      uni.setStorageSync('theme_mode', mode)
      this.applyTheme()
    },

    /** 应用主题 */
    applyTheme() {
      let dark = false

      switch (this.mode) {
        case 'light':
          dark = false
          break
        case 'dark':
          dark = true
          break
        case 'auto':
          // 跟随系统
          const systemInfo = uni.getSystemInfoSync()
          dark = systemInfo.osTheme === 'dark'
          break
      }

      this.isDark = dark

      // 设置CSS变量（通过修改page的data-theme属性）
      const themeValue = dark ? 'dark' : 'light'
      // #ifdef H5
      document.documentElement.setAttribute('data-theme', themeValue)
      // #endif

      // #ifdef APP-PLUS
      plus.navigator.setStatusBarStyle(dark ? 'light' : 'dark')
      // #endif

      // #ifdef MP-WEIXIN
      wx.setNavigationBarColor({
        frontColor: dark ? '#ffffff' : '#000000',
        backgroundColor: dark ? '#1E1E1E' : '#FFFFFF'
      })
      // #endif

      // 通知所有组件更新
      uni.$emit('theme-changed', { mode: this.mode, isDark: dark })
    },

    /** 跟随系统主题变化 */
    listenSystemTheme() {
      if (this.mode === 'auto') {
        // #ifdef APP-PLUS
        plus.globalEvent.addEventListener('themeChanged', () => {
          this.applyTheme()
        })
        // #endif

        // #ifdef H5
        const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
        mediaQuery.addEventListener('change', () => {
          this.applyTheme()
        })
        // #endif
      }
    },

    /** 初始化主题 */
    initTheme() {
      this.applyTheme()
      this.listenSystemTheme()
    }
  }
})
```

### 6.2 暗色模式适配策略

#### 6.2.1 适配原则

| 原则 | 说明 |
|------|------|
| **语义化颜色** | 所有颜色使用CSS变量，不硬编码色值 |
| **层级区分** | 暗色模式下通过不同灰度层级区分背景层级（而非阴影） |
| **对比度保证** | 文本与背景对比度不低于 4.5:1（WCAG AA标准） |
| **图片处理** | 暗色模式下图片降低亮度，避免刺眼 |
| **图表适配** | 图表背景色、网格线、文字颜色随主题切换 |

#### 6.2.2 组件暗色适配示例

```scss
// 适配暗色模式的通用样式

// 卡片组件
.stat-card {
  background: var(--card-bg-color);
  box-shadow: 0 4rpx 16rpx var(--shadow-color);
  border-radius: var(--card-radius);

  // 暗色模式下增加边框来区分层级
  [data-theme="dark"] & {
    border: 1rpx solid var(--border-color);
  }
}

// 表单输入框
.u-input,
.u-textarea {
  background: var(--card-bg-color);
  color: var(--text-primary-color);
  border: 1rpx solid var(--border-color);

  &[data-theme="dark"] & {
    border-color: var(--border-color);
  }
}

// 暗色模式下图片降低亮度
.dark-mode-image {
  [data-theme="dark"] & {
    filter: brightness(0.85);
  }
}

// 图表暗色适配
.chart-container {
  [data-theme="dark"] & {
    .chart-title { color: var(--text-primary-color); }
    .chart-legend { color: var(--text-secondary-color); }
  }
}
```

#### 6.2.3 ECharts暗色主题配置

```typescript
// config/theme-chart.ts

export const chartLightTheme = {
  backgroundColor: 'transparent',
  textStyle: { color: '#303133' },
  splitLine: { lineStyle: { color: '#EBEEF5' } },
  axisLine: { lineStyle: { color: '#DCDFE6' } }
}

export const chartDarkTheme = {
  backgroundColor: 'transparent',
  textStyle: { color: '#E5EAF3' },
  splitLine: { lineStyle: { color: '#2C2C2C' } },
  axisLine: { lineStyle: { color: '#3C3C3C' } },
  legend: { textStyle: { color: '#8F95A2' } }
}

/** 获取当前主题的图表配置 */
export function getChartTheme(): typeof chartLightTheme {
  const themeStore = useThemeStore()
  return themeStore.isDark ? chartDarkTheme : chartLightTheme
}
```

---

## 7. 图片处理

### 7.1 图片压缩方案

```typescript
// utils/image-process.ts

/** 图片压缩策略（根据场景自动选择） */
export const IMAGE_COMPRESS_STRATEGY = {
  /** 头像上传 */
  avatar: {
    maxWidth: 400,
    maxHeight: 400,
    quality: 0.8,
    maxSize: 500 * 1024  // 500KB
  },
  /** 巡检照片 */
  inspection: {
    maxWidth: 1920,
    maxHeight: 1440,
    quality: 0.75,
    maxSize: 2 * 1024 * 1024  // 2MB
  },
  /** 抄表照片 */
  meterReading: {
    maxWidth: 1600,
    maxHeight: 1200,
    quality: 0.8,
    maxSize: 1.5 * 1024 * 1024  // 1.5MB
  },
  /** 聊天/消息图片 */
  chat: {
    maxWidth: 1280,
    maxHeight: 960,
    quality: 0.7,
    maxSize: 1 * 1024 * 1024  // 1MB
  },
  /** 缩略图 */
  thumbnail: {
    maxWidth: 300,
    maxHeight: 300,
    quality: 0.6,
    maxSize: 100 * 1024  // 100KB
  }
}

/**
 * 选择/拍摄图片
 * @param count 最大选择数量
 * @param sourceType 来源 ['album', 'camera']
 * @param compressStrategy 压缩策略
 */
export function pickImage(
  count: number = 1,
  sourceType: ('album' | 'camera')[] = ['album', 'camera'],
  compressStrategy: keyof typeof IMAGE_COMPRESS_STRATEGY = 'inspection'
): Promise<{ originalPath: string; compressedPath: string; size: number }[]> {
  return new Promise((resolve, reject) => {
    uni.chooseImage({
      count,
      sizeType: ['original', 'compressed'],
      sourceType,
      success: async (res) => {
        const strategy = IMAGE_COMPRESS_STRATEGY[compressStrategy]
        const results = []

        for (const tempPath of res.tempFilePaths) {
          try {
            // 获取原始文件大小
            const origInfo = await getFileInfo(tempPath)

            // 判断是否需要压缩
            if (origInfo.size > strategy.maxSize * 1.5) {
              // 需要压缩
              const compressedPath = await compressImage(tempPath, strategy)
              const compressedInfo = await getFileInfo(compressedPath)
              results.push({
                originalPath: tempPath,
                compressedPath,
                size: compressedInfo.size
              })
            } else {
              // 不需要压缩
              results.push({
                originalPath: tempPath,
                compressedPath: tempPath,
                size: origInfo.size
              })
            }
          } catch (e) {
            console.error('图片处理失败:', e)
          }
        }

        resolve(results)
      },
      fail: reject
    })
  })
}
```

### 7.2 图片上传进度显示

```vue
<!-- components/ImageUploader.vue -->
<template>
  <view class="image-uploader">
    <!-- 已上传/待上传图片列表 -->
    <view class="image-list">
      <view v-for="(img, index) in imageList" :key="img.id" class="image-item">
        <!-- 图片缩略图 -->
        <image
          :src="img.thumbPath || img.localPath"
          mode="aspectFill"
          class="image-thumb"
          @click="previewImage(index)"
        />

        <!-- 上传进度遮罩 -->
        <view v-if="img.status === 'uploading'" class="upload-overlay">
          <view class="progress-ring">
            <text class="progress-text">{{ img.progress }}%</text>
          </view>
        </view>

        <!-- 上传失败标记 -->
        <view v-if="img.status === 'failed'" class="failed-overlay" @click="retryUpload(index)">
          <u-icon name="close-circle" color="#fff" size="24" />
          <text class="retry-text">点击重试</text>
        </view>

        <!-- 删除按钮 -->
        <view class="delete-btn" @click="removeImage(index)" v-if="img.status !== 'uploading'">
          <u-icon name="close" color="#fff" size="16" />
        </view>
      </view>

      <!-- 添加图片按钮 -->
      <view
        v-if="imageList.length < maxCount"
        class="add-image-btn"
        @click="onAddImage"
      >
        <u-icon name="plus" size="32" color="#C0C4CC" />
        <text class="add-text">{{ imageList.length }}/{{ maxCount }}</text>
      </view>
    </view>

    <!-- 总体上传进度条 -->
    <view class="total-progress" v-if="isUploading">
      <text>上传中 {{ uploadedCount }}/{{ imageList.length }}</text>
      <u-line-progress :percent="totalProgress" activeColor="#409EFF" />
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { pickImage, compressImage } from '@/utils/image-process'
import { batchUploadImages } from '@/utils/upload'

interface ImageItem {
  id: string
  localPath: string
  compressedPath?: string
  thumbPath?: string
  remoteUrl?: string
  status: 'pending' | 'uploading' | 'success' | 'failed'
  progress: number
  size: number
}

const props = withDefaults(defineProps<{
  maxCount?: number
  compressStrategy?: 'avatar' | 'inspection' | 'meterReading' | 'chat' | 'thumbnail'
  autoUpload?: boolean
}>(), {
  maxCount: 9,
  compressStrategy: 'inspection',
  autoUpload: true
})

const emit = defineEmits<{
  (e: 'update:urls', urls: string[]): void
  (e: 'uploadComplete', results: any[]): void
}>()

const imageList = ref<ImageItem[]>([])
const isUploading = ref(false)

const uploadedCount = computed(() => imageList.value.filter(i => i.status === 'success').length)
const totalProgress = computed(() => {
  if (imageList.value.length === 0) return 0
  const total = imageList.value.reduce((sum, img) => sum + img.progress, 0)
  return Math.round(total / imageList.value.length)
})

/** 添加图片 */
const onAddImage = async () => {
  const remaining = props.maxCount - imageList.value.length
  const picked = await pickImage(remaining, ['album', 'camera'], props.compressStrategy)

  picked.forEach(item => {
    imageList.value.push({
      id: `img_${Date.now()}_${Math.random().toString(36).substr(2, 6)}`,
      localPath: item.originalPath,
      compressedPath: item.compressedPath,
      status: 'pending',
      progress: 0,
      size: item.size
    })
  })

  if (props.autoUpload) {
    startUpload()
  }
}

/** 开始上传 */
const startUpload = async () => {
  isUploading.value = true
  const pendingImages = imageList.value.filter(i => i.status === 'pending' || i.status === 'failed')
  const filePaths = pendingImages.map(i => i.compressedPath || i.localPath)

  await batchUploadImages(
    filePaths,
    (progress) => {
      // 更新当前上传图片的进度
      const currentIdx = pendingImages.findIndex(
        i => i.localPath === progress.currentFile || i.compressedPath === progress.currentFile
      )
      if (currentIdx > -1) {
        pendingImages[currentIdx].progress = progress.percent
        pendingImages[currentIdx].status = progress.percent >= 100 ? 'success' : 'uploading'
      }
    }
  ).then(results => {
    results.forEach((result, index) => {
      const img = pendingImages[index]
      if (result.status === 'success') {
        img.remoteUrl = result.remoteUrl
        img.status = 'success'
        img.progress = 100
      } else {
        img.status = 'failed'
      }
    })
  })

  isUploading.value = false

  // 通知父组件上传完成
  const urls = imageList.value.filter(i => i.status === 'success').map(i => i.remoteUrl!)
  emit('update:urls', urls)
  emit('uploadComplete', imageList.value)
}

/** 重试上传 */
const retryUpload = (index: number) => {
  imageList.value[index].status = 'pending'
  imageList.value[index].progress = 0
  startUpload()
}

/** 删除图片 */
const removeImage = (index: number) => {
  imageList.value.splice(index, 1)
  const urls = imageList.value.filter(i => i.status === 'success').map(i => i.remoteUrl!)
  emit('update:urls', urls)
}

/** 预览图片 */
const previewImage = (index: number) => {
  const urls = imageList.value
    .filter(i => i.remoteUrl || i.compressedPath || i.localPath)
    .map(i => i.remoteUrl || i.compressedPath || i.localPath)
  uni.previewImage({ current: index, urls })
}
</script>
```

### 7.3 图片预览组件

```vue
<!-- components/ImagePreview.vue -->
<template>
  <view class="image-preview" v-if="visible" @click="onClose">
    <!-- 当前图片 -->
    <swiper
      class="preview-swiper"
      :current="currentIndex"
      :circular="false"
      @change="onSwiperChange"
    >
      <swiper-item v-for="(url, index) in urls" :key="index">
        <view class="image-wrapper" @click.stop>
          <image
            :src="url"
            mode="aspectFit"
            class="preview-image"
            :style="imageTransform"
            @touchstart="onTouchStart"
            @touchmove="onTouchMove"
            @touchend="onTouchEnd"
          />
        </view>
      </swiper-item>
    </swiper>

    <!-- 页码指示器 -->
    <view class="indicator">
      {{ currentIndex + 1 }} / {{ urls.length }}
    </view>

    <!-- 底部操作栏 -->
    <view class="action-bar">
      <view class="action-item" @click.stop="onSave">
        <u-icon name="download" size="20" color="#fff" />
        <text class="action-text">保存</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const props = defineProps<{
  urls: string[]
  visible: boolean
  initialIndex?: number
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'save', url: string): void
}>()

const currentIndex = ref(props.initialIndex || 0)
const scale = ref(1)
const lastTouchDistance = ref(0)

const imageTransform = computed(() => ({
  transform: `scale(${scale.value})`,
  transition: 'transform 0.2s ease'
}))

const onSwiperChange = (e: any) => {
  currentIndex.value = e.detail.current
  scale.value = 1  // 切换时重置缩放
}

const onClose = () => {
  emit('close')
}

const onSave = () => {
  emit('save', props.urls[currentIndex.value])
}

// 双指缩放手势
const onTouchStart = (e: TouchEvent) => {
  if (e.touches.length === 2) {
    lastTouchDistance.value = getTouchDistance(e.touches)
  }
}

const onTouchMove = (e: TouchEvent) => {
  if (e.touches.length === 2) {
    const distance = getTouchDistance(e.touches)
    const delta = distance / lastTouchDistance.value
    scale.value = Math.min(Math.max(scale.value * delta, 0.5), 3)
    lastTouchDistance.value = distance
  }
}

const onTouchEnd = () => {
  // 如果缩放比小于1，恢复到1
  if (scale.value < 1) {
    scale.value = 1
  }
}

function getTouchDistance(touches: TouchList): number {
  const dx = touches[0].clientX - touches[1].clientX
  const dy = touches[0].clientY - touches[1].clientY
  return Math.sqrt(dx * dx + dy * dy)
}
</script>
```

---

## 8. 二期新增目录结构

在一期项目结构基础上，二期新增以下文件和目录：

```
energy-carbon-mobile/src/
├── api/
│   ├── inspection.ts              # [新增] 巡检模块API
│   ├── message.ts                # [新增] 消息中心API
│   └── export.ts                 # [新增] 报表导出API
├── components/
│   ├── TimeDimensionPicker.vue   # [新增] 时间维度选择器
│   ├── EnhancedChart.vue         # [新增] 增强版图表组件
│   ├── ImageUploader.vue         # [新增] 图片上传组件
│   ├── ImagePreview.vue          # [新增] 图片预览组件
│   ├── InspectionItem.vue        # [新增] 巡检项组件
│   ├── MessageItem.vue           # [新增] 消息列表项组件
│   ├── ConflictDialog.vue        # [新增] 数据冲突处理弹窗
│   ├── ThemeSwitch.vue           # [新增] 主题切换按钮
│   └── SyncStatusPanel.vue      # [新增] 同步状态面板
├── composables/
│   ├── useInspection.ts          # [新增] 巡检业务逻辑
│   ├── useReport.ts              # [新增] 报表增强逻辑
│   ├── useMessage.ts             # [新增] 消息中心逻辑
│   └── useImageProcess.ts        # [新增] 图片处理逻辑
├── config/
│   └── theme-chart.ts            # [新增] 图表主题配置
├── pages/
│   ├── inspection/                # [新增] 巡检模块
│   │   ├── list.vue              # 巡检任务列表
│   │   ├── detail.vue            # 巡检任务详情
│   │   ├── execute.vue           # 巡检执行页
│   │   ├── abnormal.vue          # 异常上报页
│   │   └── history.vue           # 巡检历史
│   ├── message/                  # [新增] 消息中心
│   │   ├── list.vue              # 消息列表
│   │   └── detail.vue            # 消息详情
│   ├── report/                   # [增强] 报表页面
│   │   ├── energy.vue            # 能耗报表（增加多维切换+导出）
│   │   └── carbon.vue            # 碳排放报表（增加多维切换+导出）
│   └── profile/                  # [增强] 个人中心
│       └── index.vue             # 增加主题切换、数据统计
├── stores/
│   ├── theme.ts                  # [新增] 主题状态管理
│   └── message.ts                # [新增] 消息状态管理
├── types/
│   └── entity.ts                 # [增强] 新增巡检、消息等类型
├── utils/
│   ├── location.ts               # [新增] GPS定位工具
│   ├── image-compress.ts         # [新增] 图片压缩工具
│   ├── image-process.ts          # [新增] 图片处理工具
│   ├── upload.ts                 # [新增] 批量上传工具
│   ├── websocket.ts              # [新增] WebSocket管理
│   ├── jpush.ts                  # [新增] 极光推送封装
│   ├── export.ts                 # [新增] PDF/Excel导出
│   └── storage-manager.ts        # [新增] 存储容量管理
```

---

## 9. 二期新增API接口设计

### 9.1 设备巡检接口

```yaml
# 巡检任务
GET  /mobile/inspection/task/list     # 获取巡检任务列表
GET  /mobile/inspection/task/{id}      # 获取任务详情
PUT  /mobile/inspection/task/{id}/start    # 开始巡检
POST /mobile/inspection/task/{id}/submit # 提交巡检结果
POST /mobile/inspection/task/{id}/abnormal # 上报异常
GET  /mobile/inspection/history        # 巡检历史
GET  /mobile/inspection/template/{id}  # 巡检模板详情
```

### 9.2 消息中心接口

```yaml
# 消息
GET  /mobile/message/list             # 消息列表（支持分页、类型筛选）
GET  /mobile/message/{id}              # 消息详情
PUT  /mobile/message/{id}/read         # 标记已读
PUT  /mobile/message/read-all         # 全部已读
DELETE /mobile/message/{id}           # 删除消息
DELETE /mobile/message/clear           # 清空消息
GET  /mobile/message/unread-count     # 未读数
WS   /ws/message                       # WebSocket消息通道
```

### 9.3 报表导出接口

```yaml
# 报表导出
POST /report/export/pdf               # 生成PDF（后端）
POST /report/export/excel             # 生成Excel（后端）
POST /report/export/request           # 请求导出（返回文件URL）
GET  /report/{type}/detail             # 报表详情（支持dimension参数）
```

### 9.4 离线同步接口

```yaml
# 离线同步（增强版）
POST /mobile/sync/batch-v2            # 批量同步（支持冲突检测）
  Header:
    X-Sync-Mode: offline-sync
    X-Local-Timestamp: {timestamp}
  Response:
    code: 200 | 409 (冲突)
    conflictInfo: { serverData, localData, conflictField }

POST /mobile/sync/resolve             # 解决冲突
  Body: { taskId, resolution: 'useLocal'|'useServer'|'merge', mergedData }
```

### 9.5 文件上传接口

```yaml
# 文件上传
POST /mobile/file/upload              # 上传文件（支持巡检照片、抄表照片）
POST /mobile/file/batch-upload        # 批量上传
GET  /mobile/file/{id}/thumbnail       # 获取缩略图
```

---

## 附录

### A. 二期技术栈版本锁定

| 依赖 | 版本 | 说明 |
|------|------|------|
| @dcloudio/uni-app | 3.99+ | UniApp Vue 3版本 |
| vue | 3.4+ | 响应式框架 |
| typescript | 5.0+ | 类型系统 |
| uview-plus | 3.2+ | UI组件库 |
| pinia | 2.1+ | 状态管理 |
| echarts | 5.4+ | 图表库 |
| ucharts | 2.5+ | 轻量图表库（App端备选） |
| jsPDF | 2.5+ | PDF生成（H5端） |
| xlsx (SheetJS) | 0.18+ | Excel生成（H5端） |

### B. 二期编码规范补充

| 规范 | 说明 |
|------|------|
| 巡检相关组件 | 以 `Inspection` 前缀命名 |
| 图片相关组件 | 以 `Image` 前缀命名 |
| 消息相关Store | `useMessageStore` |
| 主题相关Store | `useThemeStore` |
| WebSocket工具 | 单例模式，全局唯一实例 |
| 离线同步 | V2版本Store与V1共存，渐进迁移 |

### C. 与一期兼容性说明

二期完全兼容一期架构：
- 一期Store（user、app、alarm、sync）保持不变
- 二期新增Store（message、theme）独立运行
- syncStore V2继承V1数据格式，支持自动迁移
- 一期API封装不受影响，二期新增API独立文件
- 一期组件可被二期页面直接引用
- 二期新增组件均放在 `components/` 目录下，一期组件保持不变

---

**文档说明**

本文档为能碳数智化平台移动端二期技术设计文档，在一期技术栈（UniApp + Vue 3 + uview-plus + Pinia）基础上新增设备巡检、报表增强、消息中心、离线增强、主题切换、图片处理六大模块的技术方案。本文档与一期技术设计文档共同构成移动端完整技术方案。
