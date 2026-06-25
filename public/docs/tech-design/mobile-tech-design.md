# 能碳数智化平台移动端技术设计文档

## 文档信息

| 项目 | 内容 |
|------|------|
| 文档名称 | 能碳数智化平台移动端技术设计文档 |
| 版本号 | v1.0.0 |
| 编制日期 | 2026-06-25 |
| 目标平台 | iOS / Android / 微信小程序 |
| 关联文档 | 《tech-design-能碳管理平台.md》《API接口说明.md》 |

---

## 目录

1. [概述](#1-概述)
2. [技术选型](#2-技术选型)
3. [移动端架构设计](#3-移动端架构设计)
4. [与现有后端API对接方案](#4-与现有后端api对接方案)
5. [UI/UX设计规范](#5-uiux设计规范)
6. [性能优化方案](#6-性能优化方案)
7. [安全方案](#7-安全方案)
8. [离线方案](#8-离线方案)
9. [项目目录结构](#9-项目目录结构)
10. [部署与发布方案](#10-部署与发布方案)

---

## 1. 概述

### 1.1 背景

能碳数智化平台PC端已基于 Vue 3 + Spring Boot 2.7 构建并投入运行。为覆盖一线运维人员、巡检人员、管理人员的移动办公场景，需建设移动端应用，聚焦**数据录入、关键指标查看、告警推送**三大核心场景。

### 1.2 设计目标

| 目标 | 说明 |
|------|------|
| 快速交付 | 复用现有后端RESTful API，减少重复开发 |
| 跨平台覆盖 | 一套代码覆盖iOS、Android、微信小程序 |
| 体验优先 | 原生级交互体验，首屏加载 < 2s |
| 稳定可靠 | 支持弱网/离线环境下的数据录入与缓存 |
| 安全合规 | 数据传输加密，Token安全存储，满足等保2.0三级要求 |

### 1.3 核心功能模块

| 模块 | 功能描述 | 优先级 |
|------|----------|--------|
| 首页仪表盘 | 能耗/碳排放/告警关键指标卡片、趋势图 | P0 |
| 数据录入 | 抄表录入、设备巡检、碳排放数据录入 | P0 |
| 告警中心 | 实时告警推送、告警处理、历史告警查询 | P0 |
| 报表查看 | 能耗/碳排放图表，适配手机屏幕 | P1 |
| 个人中心 | 用户信息、消息通知、设置 | P1 |
| 扫码功能 | 扫码识别设备/计量器具，快速跳转 | P0 |

### 1.4 约束条件

| 约束项 | 要求 | 说明 |
|--------|------|------|
| 后端兼容 | 复用现有Spring Boot后端 | 不得修改现有数据库结构 |
| 认证方式 | JWT Bearer Token | 与PC端保持一致 |
| API规范 | RESTful JSON | 复用现有接口，按需扩展 |
| 最低系统版本 | Android 8.0 / iOS 12 / 微信客户端8.0 | 覆盖主流设备 |

---

## 2. 技术选型

### 2.1 跨平台方案对比

| 对比维度 | UniApp (Vue 3) | Flutter | React Native | 原生小程序 |
|----------|----------------|---------|--------------|------------|
| **开发语言** | Vue 3 / TypeScript | Dart | JavaScript / TypeScript | WXML / JS |
| **跨平台能力** | iOS / Android / 小程序 / H5 / 快应用 | iOS / Android / Web / 桌面 | iOS / Android / Web (有限) | 仅微信小程序 |
| **学习成本** | 低（团队已有Vue 3经验） | 中（需学习Dart） | 中（需熟悉React生态） | 低（但仅限微信） |
| **性能表现** | 良好（Vue 3运行时 + 原生渲染） | 优秀（自绘引擎，接近原生） | 良好（原生组件桥接） | 一般（WebView渲染） |
| **包体积** | 较小（按需编译） | 较大（需包含Flutter引擎） | 较大（需包含JS引擎） | 最小（依托微信） |
| **原生能力** | 丰富（插件市场成熟） | 丰富（官方插件 + 社区） | 丰富（原生模块桥接） | 受限于微信SDK |
| **扫码能力** | 内置扫码API | 需依赖原生插件 | 需依赖原生模块 | 微信原生支持 |
| **离线存储** | SQLite / 本地缓存 | SQLite / Hive | AsyncStorage / Realm | LocalStorage / 微信缓存 |
| **热更新** | 支持（整包/资源热更新） | 支持（Code Push） | 支持（Code Push） | 不支持（需微信审核） |
| **社区生态** | 活跃（国内DCloud生态） | 活跃（Google支持） | 活跃（Meta支持） | 一般（受限于微信） |
| **图表库** | ECharts / uCharts / F2 | fl_chart / syncfusion_chart | react-native-chart-kit / victory | ECharts / F2 |
| **招聘难度** | 低（Vue生态人才多） | 中（Dart人才较少） | 中（需React + 原生混合） | 低（但仅限小程序） |
| **开发周期** | 短（1套代码多端运行） | 中（需单独适配多端） | 中（需处理平台差异） | 短（仅微信小程序） |

### 2.2 推荐方案：UniApp (Vue 3 + TypeScript)

**选择理由：**

1. **技术栈一致性**：团队PC端已使用Vue 3 + TypeScript + Pinia，移动端采用UniApp可直接复用技术经验，降低学习成本和协作成本。

2. **多端覆盖**：一套代码可同时输出iOS App、Android App、微信小程序，覆盖全部目标用户群体，无需维护多套代码库。

3. **快速交付**：UniApp提供丰富的内置API（扫码、拍照、定位、本地存储）和插件市场，可快速实现数据录入、扫码识别等核心功能。

4. **后端零改动**：通过复用现有RESTful API，无需对Spring Boot后端进行任何改造，降低项目风险。

5. **包体积可控**：UniApp支持条件编译和分包加载，App端包体积可控制在15MB以内，微信小程序主包可控制在2MB以内。

6. **图表兼容**：可直接使用ECharts或uCharts，与PC端图表库保持一致，确保数据可视化风格统一。

7. **热更新支持**：App端支持整包和资源热更新，修复线上问题无需等待应用商店审核。

### 2.3 移动端技术栈总览

| 层级 | 技术选型 | 版本 | 说明 |
|------|----------|------|------|
| 跨平台框架 | UniApp | 3.99+ | Vue 3版本，支持编译到多端 |
| 开发语言 | TypeScript | 5.0+ | 类型安全，与PC端一致 |
| UI组件库 | uview-plus | 3.2+ | 基于UniApp的Vue 3组件库 |
| 状态管理 | Pinia | 2.1+ | Vue官方推荐，与PC端一致 |
| HTTP客户端 | uni.request + 自定义封装 | - | 适配UniApp运行时 |
| 图表库 | ECharts (小程序) / uCharts (App) | 5.4+ / 2.5+ | 根据平台选择最优方案 |
| 本地存储 | uni.storage + SQLite | - | 键值对 + 结构化离线数据 |
| 扫码 | uni.scanCode | - | 跨平台统一API |
| 推送 | UniPush (个推) | - | 统一推送服务 |
| 构建工具 | HBuilder X / CLI | 4.0+ | 官方IDE + 命令行工具 |

---

## 3. 移动端架构设计

### 3.1 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                     表现层 (Presentation)                     │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│  │  首页仪表盘 │ │  数据录入  │ │  告警中心  │ │  个人中心  │       │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘       │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐                     │
│  │  报表查看  │ │  扫码功能  │ │  消息通知  │                     │
│  └──────────┘ └──────────┘ └──────────┘                     │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                     业务逻辑层 (Business)                     │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐         │
│  │   Composables  │ │   Services     │ │   Utils        │         │
│  │  (useDashboard)│ │ (AlarmService) │ │(format/date)  │         │
│  └──────────────┘ └──────────────┘ └──────────────┘         │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                      状态管理层 (State)                       │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│  │ userStore │ │ appStore  │ │ alarmStore│ │ cacheStore│       │
│  │(用户/权限) │ │(全局状态)  │ │(告警数据)  │ │(离线缓存)  │       │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘       │
│                      Pinia 状态管理                           │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                      网络请求层 (Network)                     │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              request.ts (uni.request封装)              │   │
│  │  • 请求拦截 (添加Token/设备标识/平台标识)              │   │
│  │  • 响应拦截 (统一错误码处理/Token刷新/数据解密)        │   │
│  │  • 离线队列 (网络异常时入队，恢复后自动重试)           │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                      数据持久层 (Storage)                     │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│  │ uni.setStorage│ │  SQLite   │ │  图片缓存  │ │  日志文件  │       │
│  │ (Token/配置)  │ │(离线表单)  │ │(本地缩略图)│ │(操作审计)  │       │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘       │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                      原生能力层 (Native)                      │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│  │ 扫码(scancode)│ │ 拍照/相册  │ │ 定位定位   │ │ 推送通知   │       │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘       │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐                     │
│  │ 网络状态   │ │ 生物识别   │ │ 震动反馈   │                     │
│  └──────────┘ └──────────┘ └──────────┘                     │
└─────────────────────────────────────────────────────────────┘
```

### 3.2 项目结构

```
energy-carbon-mobile/
├── src/
│   ├── api/                          # API接口封装（按模块组织）
│   │   ├── auth.ts                   # 认证相关
│   │   ├── dashboard.ts              # 仪表盘数据
│   │   ├── monitor.ts                # 在线监测/抄表
│   │   ├── alarm.ts                  # 告警管理
│   │   ├── carbon.ts                 # 碳排放管理
│   │   ├── report.ts                 # 报表数据
│   │   ├── entry.ts                  # 数据录入
│   │   └── system.ts                 # 系统管理
│   ├── components/                   # 公共组件
│   │   ├── AppNavbar.vue             # 底部导航栏
│   │   ├── StatCard.vue              # 指标统计卡片
│   │   ├── TrendChart.vue            # 趋势图组件
│   │   ├── AlarmItem.vue             # 告警列表项
│   │   ├── DataForm.vue              # 数据录入表单
│   │   ├── ScanButton.vue            # 扫码按钮
│   │   ├── EmptyState.vue            # 空状态占位
│   │   └── PullRefresh.vue           # 下拉刷新容器
│   ├── composables/                  # 组合式函数
│   │   ├── useAuth.ts                # 认证逻辑
│   │   ├── useDashboard.ts           # 仪表盘数据获取
│   │   ├── useAlarm.ts               # 告警管理逻辑
│   │   ├── useOfflineSync.ts         # 离线同步逻辑
│   │   ├── useScan.ts                # 扫码逻辑
│   │   └── useChart.ts               # 图表配置生成
│   ├── config/                       # 全局配置
│   │   ├── index.ts                  # 环境配置
│   │   ├── api.config.ts             # API接口配置
│   │   ├── theme.config.ts           # 主题配置
│   │   └── constants.ts              # 常量定义
│   ├── pages/                        # 页面（按模块组织）
│   │   ├── index/
│   │   │   └── index.vue             # 首页仪表盘
│   │   ├── entry/
│   │   │   ├── meter-reading.vue     # 抄表录入
│   │   │   ├── inspection.vue        # 设备巡检
│   │   │   └── carbon-input.vue      # 碳排放录入
│   │   ├── alarm/
│   │   │   ├── list.vue              # 告警列表
│   │   │   ├── detail.vue            # 告警详情
│   │   │   └── handle.vue            # 告警处理
│   │   ├── report/
│   │   │   ├── energy.vue            # 能耗报表
│   │   │   └── carbon.vue            # 碳排放报表
│   │   ├── profile/
│   │   │   └── index.vue             # 个人中心
│   │   └── login/
│   │       └── index.vue             # 登录页
│   ├── static/                       # 静态资源
│   │   ├── images/
│   │   ├── icons/
│   │   └── tabbar/                   # 底部Tab图标
│   ├── stores/                       # Pinia状态管理
│   │   ├── user.ts                   # 用户状态
│   │   ├── app.ts                    # 应用状态
│   │   ├── alarm.ts                  # 告警状态
│   │   └── sync.ts                   # 离线同步状态
│   ├── types/                        # TypeScript类型定义
│   │   ├── api.ts                    # API响应类型
│   │   ├── entity.ts                 # 业务实体类型
│   │   └── index.ts                  # 类型导出
│   ├── utils/                        # 工具函数
│   │   ├── request.ts                # HTTP请求封装
│   │   ├── storage.ts                # 本地存储封装
│   │   ├── crypto.ts                 # 加密工具
│   │   ├── validate.ts               # 表单校验
│   │   ├── format.ts                 # 数据格式化
│   │   └── date.ts                   # 日期处理
│   ├── App.vue                       # 应用根组件
│   ├── main.ts                       # 应用入口
│   ├── manifest.json                 # 应用配置
│   └── pages.json                    # 页面路由配置
├── index.html                        # H5入口
├── package.json
├── tsconfig.json
├── vite.config.ts
└── uni.scss                          # UniApp全局样式变量
```

### 3.3 状态管理设计

#### 3.3.1 userStore（用户状态）

```typescript
// stores/user.ts
import { defineStore } from 'pinia'

interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar: string
  phone: string
  deptId: number
  roles: string[]
  permissions: string[]
}

export const useUserStore = defineStore('user', {
  state: () => ({
    token: uni.getStorageSync('access_token') || '',
    refreshToken: uni.getStorageSync('refresh_token') || '',
    userInfo: null as UserInfo | null,
    isLogin: false
  }),

  getters: {
    hasPermission: (state) => (perm: string) => {
      return state.userInfo?.permissions?.includes(perm) || false
    }
  },

  actions: {
    async login(credentials: { username: string; password: string }) {
      const res = await apiAuth.login(credentials)
      this.token = res.token
      this.refreshToken = res.refreshToken
      uni.setStorageSync('access_token', res.token)
      uni.setStorageSync('refresh_token', res.refreshToken)
      await this.fetchUserInfo()
      this.isLogin = true
    },

    async fetchUserInfo() {
      const res = await apiAuth.getUserInfo()
      this.userInfo = res
      uni.setStorageSync('user_info', JSON.stringify(res))
    },

    async refreshAccessToken() {
      const res = await apiAuth.refresh(this.refreshToken)
      this.token = res.token
      uni.setStorageSync('access_token', res.token)
    },

    logout() {
      this.token = ''
      this.refreshToken = ''
      this.userInfo = null
      this.isLogin = false
      uni.removeStorageSync('access_token')
      uni.removeStorageSync('refresh_token')
      uni.removeStorageSync('user_info')
      uni.reLaunch({ url: '/pages/login/index' })
    }
  }
})
```

#### 3.3.2 appStore（应用状态）

```typescript
// stores/app.ts
export const useAppStore = defineStore('app', {
  state: () => ({
    theme: uni.getStorageSync('app_theme') || 'light',
    networkStatus: 'unknown' as 'wifi' | '4g' | '3g' | '2g' | 'unknown' | 'none',
    isOnline: true,
    systemInfo: null as UniApp.GetSystemInfoResult | null,
    unreadAlarmCount: 0
  }),

  actions: {
    setNetworkStatus(status: string) {
      this.networkStatus = status as any
      this.isOnline = status !== 'none'
    },

    async initSystemInfo() {
      const info = await uni.getSystemInfo()
      this.systemInfo = info
    },

    setUnreadAlarmCount(count: number) {
      this.unreadAlarmCount = count
      // 同步到TabBar徽标
      if (count > 0) {
        uni.setTabBarBadge({ index: 2, text: String(count > 99 ? '99+' : count) })
      } else {
        uni.removeTabBarBadge({ index: 2 })
      }
    }
  }
})
```

#### 3.3.3 syncStore（离线同步状态）

```typescript
// stores/sync.ts
interface SyncTask {
  id: string
  api: string
  method: string
  data: any
  timestamp: number
  retryCount: number
  status: 'pending' | 'syncing' | 'failed'
}

export const useSyncStore = defineStore('sync', {
  state: () => ({
    queue: [] as SyncTask[],
    isSyncing: false,
    lastSyncTime: uni.getStorageSync('last_sync_time') || 0
  }),

  getters: {
    pendingCount: (state) => state.queue.filter(t => t.status === 'pending').length
  },

  actions: {
    addTask(task: Omit<SyncTask, 'id' | 'timestamp' | 'retryCount' | 'status'>) {
      const newTask: SyncTask = {
        ...task,
        id: `${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
        timestamp: Date.now(),
        retryCount: 0,
        status: 'pending'
      }
      this.queue.push(newTask)
      this.persistQueue()
    },

    removeTask(taskId: string) {
      const idx = this.queue.findIndex(t => t.id === taskId)
      if (idx > -1) {
        this.queue.splice(idx, 1)
        this.persistQueue()
      }
    },

    persistQueue() {
      uni.setStorageSync('sync_queue', JSON.stringify(this.queue))
    },

    async processQueue() {
      if (this.isSyncing || this.queue.length === 0) return
      this.isSyncing = true

      const pendingTasks = this.queue.filter(t => t.status === 'pending')
      for (const task of pendingTasks) {
        try {
          task.status = 'syncing'
          await uni.request({ url: task.api, method: task.method as any, data: task.data })
          task.status = 'synced'
          this.removeTask(task.id)
        } catch (e) {
          task.retryCount++
          if (task.retryCount >= 3) {
            task.status = 'failed'
          } else {
            task.status = 'pending'
          }
        }
      }

      this.lastSyncTime = Date.now()
      uni.setStorageSync('last_sync_time', this.lastSyncTime)
      this.isSyncing = false
      this.persistQueue()
    }
  }
})
```

### 3.4 网络请求层设计

```typescript
// utils/request.ts
import { useUserStore } from '@/stores/user'
import { useSyncStore } from '@/stores/sync'

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'https://api.energy-carbon.com'
const TIMEOUT = 30000

interface RequestOptions {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: any
  params?: any
  header?: Record<string, string>
  needAuth?: boolean
  enableOffline?: boolean     // 是否支持离线缓存
  offlineKey?: string         // 离线缓存键名
}

export function request<T = any>(options: RequestOptions): Promise<T> {
  return new Promise((resolve, reject) => {
    const userStore = useUserStore()
    const syncStore = useSyncStore()

    // 检查网络状态
    const appStore = useAppStore()
    if (!appStore.isOnline && options.enableOffline && options.method === 'GET') {
      // 离线读操作：返回缓存数据
      const cached = uni.getStorageSync(`cache_${options.offlineKey || options.url}`)
      if (cached) {
        resolve(JSON.parse(cached))
        return
      }
    }

    if (!appStore.isOnline && ['POST', 'PUT', 'DELETE'].includes(options.method || 'GET')) {
      // 离线写操作：加入同步队列
      if (options.enableOffline) {
        syncStore.addTask({
          api: `${BASE_URL}${options.url}`,
          method: options.method || 'POST',
          data: options.data
        })
        resolve({ code: 200, message: '已加入离线队列', data: null } as any)
        return
      }
      reject(new Error('网络不可用，请检查网络连接'))
      return
    }

    const header: Record<string, string> = {
      'Content-Type': 'application/json',
      'X-Client-Platform': uni.getSystemInfoSync().platform,
      'X-Client-Version': uni.getAppBaseInfo().appVersion || '1.0.0',
      ...options.header
    }

    if (options.needAuth !== false && userStore.token) {
      header['Authorization'] = `Bearer ${userStore.token}`
    }

    uni.request({
      url: `${BASE_URL}${options.url}`,
      method: options.method || 'GET',
      data: options.data || options.params,
      header,
      timeout: TIMEOUT,
      success: (res) => {
        const data = res.data as any

        if (res.statusCode === 401) {
          // Token过期，尝试刷新
          userStore.refreshAccessToken().then(() => {
            // 重试原请求
            request(options).then(resolve).catch(reject)
          }).catch(() => {
            userStore.logout()
            reject(new Error('登录已过期，请重新登录'))
          })
          return
        }

        if (data.code !== 200) {
          uni.showToast({ title: data.message || '请求失败', icon: 'none' })
          reject(new Error(data.message))
          return
        }

        // 缓存GET请求结果
        if (options.method === 'GET' && options.enableOffline && options.offlineKey) {
          uni.setStorageSync(`cache_${options.offlineKey}`, JSON.stringify(data.data))
        }

        resolve(data.data)
      },
      fail: (err) => {
        // 网络错误处理
        if (options.enableOffline && ['POST', 'PUT', 'DELETE'].includes(options.method || '')) {
          syncStore.addTask({
            api: `${BASE_URL}${options.url}`,
            method: options.method || 'POST',
            data: options.data
          })
          resolve({ code: 200, message: '已加入离线队列', data: null } as any)
        } else {
          uni.showToast({ title: '网络请求失败', icon: 'none' })
          reject(err)
        }
      }
    })
  })
}

// 快捷方法
export const http = {
  get: <T>(url: string, params?: any, opts?: Partial<RequestOptions>) =>
    request<T>({ url, method: 'GET', params, ...opts }),
  post: <T>(url: string, data?: any, opts?: Partial<RequestOptions>) =>
    request<T>({ url, method: 'POST', data, ...opts }),
  put: <T>(url: string, data?: any, opts?: Partial<RequestOptions>) =>
    request<T>({ url, method: 'PUT', data, ...opts }),
  del: <T>(url: string, data?: any, opts?: Partial<RequestOptions>) =>
    request<T>({ url, method: 'DELETE', data, ...opts })
}
```

### 3.5 存储层设计

| 存储方式 | 用途 | 容量限制 | 数据类型 |
|----------|------|----------|----------|
| `uni.setStorageSync` | Token、用户配置、页面缓存 | 约10MB（小程序）/ 无限制（App） | JSON字符串 |
| `plus.sqlite` (App) | 离线表单数据、日志、告警历史 | 取决于磁盘空间 | 结构化数据 |
| 文件系统 (App) | 图片缓存、离线附件 | 取决于磁盘空间 | 二进制文件 |
| `localStorage` (H5) | 同StorageSync | 约5MB | JSON字符串 |

```typescript
// utils/storage.ts
const SQLite = {
  db: null as any,

  async open() {
    if (this.db) return this.db
    return new Promise((resolve, reject) => {
      plus.sqlite.openDatabase({
        name: 'energy_carbon_mobile',
        path: '_doc/energy_carbon.db',
        success: (e: any) => { this.db = e; resolve(e) },
        fail: reject
      })
    })
  },

  async exec(sql: string) {
    await this.open()
    return new Promise((resolve, reject) => {
      plus.sqlite.executeSql({
        name: 'energy_carbon_mobile',
        sql: [sql],
        success: resolve,
        fail: reject
      })
    })
  },

  async query(sql: string) {
    await this.open()
    return new Promise((resolve, reject) => {
      plus.sqlite.selectSql({
        name: 'energy_carbon_mobile',
        sql,
        success: resolve,
        fail: reject
      })
    })
  }
}

// 初始化离线数据表
export async function initOfflineTables() {
  await SQLite.exec(`
    CREATE TABLE IF NOT EXISTS meter_reading (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      meter_id INTEGER NOT NULL,
      reading_value DECIMAL(18,4) NOT NULL,
      reading_date TEXT NOT NULL,
      recorder_id INTEGER,
      recorder_name TEXT,
      photo_path TEXT,
      status TEXT DEFAULT 'pending',
      sync_id TEXT,
      create_time INTEGER
    )
  `)

  await SQLite.exec(`
    CREATE TABLE IF NOT EXISTS inspection_record (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      device_id INTEGER NOT NULL,
      device_name TEXT,
      inspection_items TEXT,
      result TEXT,
      abnormal_desc TEXT,
      photo_paths TEXT,
      inspector_id INTEGER,
      inspector_name TEXT,
      status TEXT DEFAULT 'pending',
      sync_id TEXT,
      create_time INTEGER
    )
  `)
}
```

---

## 4. 与现有后端API对接方案

### 4.1 对接原则

移动端**完全复用**现有后端RESTful API，遵循以下原则：

1. **零后端改造**：不修改现有Spring Boot代码、数据库结构和接口定义。
2. **按需扩展**：仅在现有接口无法满足移动端特殊场景时，新增独立接口。
3. **统一认证**：使用相同的JWT Token体系，与PC端共享登录态。
4. **字段裁剪**：移动端无需的字段在UI层忽略，不调取额外接口。

### 4.2 接口复用清单

| 移动端功能 | 复用PC端API | 新增API | 说明 |
|------------|-------------|---------|------|
| 用户登录 | `POST /auth/login` | - | 完全一致 |
| 获取用户信息 | `GET /auth/info` | - | 完全一致 |
| Token刷新 | `POST /auth/refresh` | - | 完全一致 |
| 仪表盘指标 | `GET /monitor/analysis/consumption/summary` | `GET /mobile/dashboard/summary` | 移动端聚合接口（可选） |
| 实时告警 | `GET /alarm/record/list` | - | 增加 `unhandled=1` 参数 |
| 告警处理 | `PUT /alarm/record/{id}/handle` | - | 完全一致 |
| 抄表记录查询 | `GET /monitor/reading/list` | - | 增加分页大小限制 |
| 抄表录入 | `POST /monitor/reading` | - | 完全一致 |
| 设备列表 | `GET /device/list` | - | 增加 `keyword` 模糊查询 |
| 设备树 | `GET /monitor/device/device-tree` | - | 完全一致 |
| 碳排放核算 | `GET /carbon/accounting/list` | - | 完全一致 |
| 碳排放录入 | `POST /carbon/accounting` | - | 完全一致 |
| 能耗趋势 | `GET /monitor/analysis/consumption/trend` | - | 完全一致 |
| 电费趋势 | `GET /monitor/analysis/electricity/trend` | - | 完全一致 |
| 用能单元树 | `GET /system/unit/tree` | - | 完全一致 |
| 字典数据 | `GET /system/dict/data/type/{dictType}` | - | 完全一致 |

### 4.3 移动端专用扩展API（建议后端新增）

```yaml
# 建议后端新增的移动端专用接口

# 1. 移动端仪表盘聚合接口
GET /mobile/dashboard/summary
  返回:
    - 今日能耗 (kWh)
    - 本月碳排放 (tCO2e)
    - 未处理告警数
    - 设备在线率
    - 环比/同比数据
  用途: 减少首页多次请求，一次加载全部关键指标

# 2. 扫码识别接口
GET /mobile/scan/resolve
  参数:
    - code: 扫码内容（设备二维码/条形码）
  返回:
    - type: 'DEVICE' | 'METER' | 'UNKNOWN'
    - entity: 设备/计量器具详情
  用途: 扫码后快速获取设备信息

# 3. 离线数据批量提交接口
POST /mobile/sync/batch
  参数:
    - entries: 离线录入数据数组
    - type: 'METER_READING' | 'INSPECTION' | 'CARBON_ENTRY'
  返回:
    - successIds: 成功入库的ID列表
    - failedItems: 失败项及原因
  用途: 网络恢复后批量同步离线数据

# 4. 推送设备注册接口
POST /mobile/push/register
  参数:
    - clientId: 推送客户端ID
    - platform: 'ios' | 'android' | 'wechat'
    - deviceToken: 设备令牌
  用途: 绑定用户与推送设备，实现告警推送

# 5. 告警未读数接口
GET /alarm/record/unhandled/count
  返回:
    - count: 未处理告警数量
    - latestAlarm: 最新一条告警摘要
  用途: TabBar徽标数字展示，轮询刷新
```

### 4.4 API适配层封装

```typescript
// api/dashboard.ts
import { http } from '@/utils/request'

export const dashboardApi = {
  // 聚合仪表盘数据（优先使用移动端专用接口，回退到PC端接口）
  getSummary() {
    return http.get<any>('/mobile/dashboard/summary').catch(() => {
      // 回退：并发请求多个PC端接口
      return Promise.all([
        http.get('/monitor/analysis/consumption/summary'),
        http.get('/alarm/record/unhandled'),
        http.get('/monitor/status/online-rate')
      ]).then(([energy, alarm, device]) => ({
        energyToday: energy.today,
        carbonMonth: energy.carbonMonth,
        unhandledAlarm: alarm,
        onlineRate: device.onlineRate
      }))
    })
  }
}

// api/scan.ts
export const scanApi = {
  resolveCode(code: string) {
    return http.get<any>('/mobile/scan/resolve', { code })
      .catch(() => {
        // 回退：分别查询设备和计量器具
        return http.get('/device/list', { keyword: code, pageSize: 1 })
          .then((res: any) => {
            if (res.records?.length > 0) {
              return { type: 'DEVICE', entity: res.records[0] }
            }
            return http.get('/meter/list', { keyword: code, pageSize: 1 })
          })
          .then((res: any) => {
            if (res.records?.length > 0) {
              return { type: 'METER', entity: res.records[0] }
            }
            throw new Error('未找到匹配的设备或计量器具')
          })
      })
  }
}

// api/sync.ts
export const syncApi = {
  batchSubmit(entries: any[], type: string) {
    return http.post('/mobile/sync/batch', { entries, type })
  }
}
```

---

## 5. UI/UX设计规范

### 5.1 设计系统

#### 5.1.1 色彩规范

```scss
// uni.scss
/* 主色调 - 与PC端Element Plus保持一致 */
$primary: #409EFF;           // 主题蓝
$primary-light: #ECF5FF;     // 浅蓝背景
$primary-dark: #337ECC;      // 深蓝（按下状态）

/* 功能色 */
$success: #67C23A;
$warning: #E6A23C;
$danger: #F56C6C;
$info: #909399;

/* 碳主题色 */
$carbon-primary: #52C41A;    // 绿色（低碳/节能）
$carbon-warning: #FAAD14;    // 橙色（预警）
$carbon-danger: #FF4D4F;     // 红色（高排放/严重告警）

/* 中性色 */
$text-primary: #303133;      // 主文本
$text-regular: #606266;      // 常规文本
$text-secondary: #909399;    // 次要文本
$text-placeholder: #C0C4CC;  // 占位文本
$border-color: #DCDFE6;      // 边框
$bg-color: #F5F7FA;          // 页面背景
$card-bg: #FFFFFF;           // 卡片背景
```

#### 5.1.2 字体规范

| 层级 | 字号 | 字重 | 用途 |
|------|------|------|------|
| 页面标题 | 18px | 600 | 导航栏标题 |
| 模块标题 | 16px | 500 | 卡片标题、分区标题 |
| 正文 | 14px | 400 | 列表内容、表单文字 |
| 辅助文字 | 12px | 400 | 时间、说明、标签 |
| 数据指标 | 24px | 600 | 仪表盘关键数字 |
| 数据单位 | 12px | 400 | 指标单位 |

#### 5.1.3 间距规范

| 变量 | 值 | 用途 |
|------|-----|------|
| $space-xs | 4px | 图标与文字间距 |
| $space-sm | 8px | 紧凑元素间距 |
| $space-md | 16px | 常规元素间距 |
| $space-lg | 24px | 模块间距 |
| $space-xl | 32px | 页面边距 |
| $card-radius | 8px | 卡片圆角 |
| $button-radius | 4px | 按钮圆角 |

### 5.2 组件库选择

**主选：uview-plus 3.x**

选择理由：
- 专为UniApp Vue 3版本设计，组件丰富（Button、Form、List、Card、Tag、Badge等）
- 支持自定义主题，可配置为与Element Plus一致的视觉风格
- 提供移动端专用组件（SwipeAction、IndexList、Calendar、NumberBox等）
- 文档完善，社区活跃

```typescript
// main.ts
import { createSSRApp } from 'vue'
import uviewPlus from 'uview-plus'
import App from './App.vue'

export function createApp() {
  const app = createSSRApp(App)
  app.use(uviewPlus)
  return { app }
}
```

### 5.3 页面布局规范

#### 5.3.1 安全区适配

```vue
<!-- App.vue -->
<template>
  <view class="app-container" :style="{ paddingTop: safeAreaTop + 'px' }">
    <slot />
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
const safeAreaTop = ref(0)
const systemInfo = uni.getSystemInfoSync()
safeAreaTop.value = systemInfo.statusBarHeight || 0
</script>
```

#### 5.3.2 底部TabBar设计

| Tab | 图标 | 页面 | 徽标 |
|-----|------|------|------|
| 首页 | home-filled | /pages/index/index | - |
| 录入 | edit-pen-filled | /pages/entry/index | - |
| 告警 | bell-filled | /pages/alarm/list | 未处理告警数 |
| 我的 | account-filled | /pages/profile/index | - |

```json
// pages.json
{
  "tabBar": {
    "color": "#909399",
    "selectedColor": "#409EFF",
    "backgroundColor": "#FFFFFF",
    "borderStyle": "black",
    "list": [
      { "pagePath": "pages/index/index", "text": "首页", "iconPath": "static/tabbar/home.png", "selectedIconPath": "static/tabbar/home-active.png" },
      { "pagePath": "pages/entry/index", "text": "录入", "iconPath": "static/tabbar/entry.png", "selectedIconPath": "static/tabbar/entry-active.png" },
      { "pagePath": "pages/alarm/list", "text": "告警", "iconPath": "static/tabbar/alarm.png", "selectedIconPath": "static/tabbar/alarm-active.png" },
      { "pagePath": "pages/profile/index", "text": "我的", "iconPath": "static/tabbar/profile.png", "selectedIconPath": "static/tabbar/profile-active.png" }
    ]
  }
}
```

### 5.4 响应式适配策略

移动端为单一竖屏场景，主要适配不同屏幕尺寸：

```scss
// 使用rpx单位（1rpx = 屏幕宽度/750）
// 设计稿基准宽度：375px（iPhone标准宽度）

// 通用容器
.container {
  padding: 0 32rpx;
  box-sizing: border-box;
}

// 卡片布局
.stat-card {
  width: 100%;
  padding: 32rpx;
  border-radius: 16rpx;
  background: #FFFFFF;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06);
}

// 两列网格
.grid-2 {
  display: flex;
  flex-wrap: wrap;
  gap: 24rpx;
  .grid-item {
    width: calc(50% - 12rpx);
  }
}

// 图表容器高度适配
.chart-container {
  width: 100%;
  height: 400rpx;
}
```

### 5.5 交互规范

| 交互 | 实现方式 | 说明 |
|------|----------|------|
| 下拉刷新 | `onPullDownRefresh` + `uni.startPullDownRefresh` | 列表页统一支持下拉刷新 |
| 上拉加载 | `onReachBottom` + 分页加载 | 自动加载下一页 |
| 左滑操作 | `u-swipe-action` | 告警列表左滑标记已处理 |
| 扫码入口 | 悬浮按钮 + 页面内按钮 | 首页右下角悬浮扫码按钮 |
| 拍照上传 | `uni.chooseImage` + `uni.uploadFile` | 抄表/巡检时拍摄照片 |
| 表单校验 | `u-form` + `rules` | 实时校验，提交前二次确认 |

---

## 6. 性能优化方案

### 6.1 包体积优化

| 优化策略 | 具体措施 | 预期效果 |
|----------|----------|----------|
| 条件编译 | 使用 `#ifdef APP-PLUS`、`#ifdef MP-WEIXIN` 区分平台代码 | 移除不需要的平台依赖 |
| 组件按需加载 | `uview-plus` 组件按需引入，非TabBar页面使用分包 | 减少主包体积 |
| 图片压缩 | 静态图片使用WebP格式，图标使用SVG/字体图标 | 图片体积减少60%+ |
| 分包加载 | 报表页面、个人中心等放入分包 | 主包 < 2MB（小程序） |
| 移除调试代码 | 生产环境移除console.log和调试代码 | 减少无用代码 |
| 依赖裁剪 | 仅引入必要的ECharts模块或改用uCharts | 图表库体积减少50% |

```json
// pages.json 分包配置
{
  "pages": [
    { "path": "pages/index/index" },
    { "path": "pages/entry/index" },
    { "path": "pages/alarm/list" },
    { "path": "pages/profile/index" }
  ],
  "subPackages": [
    {
      "root": "pages/report",
      "pages": [
        { "path": "energy" },
        { "path": "carbon" }
      ]
    },
    {
      "root": "pages/entry-sub",
      "pages": [
        { "path": "meter-reading" },
        { "path": "inspection" },
        { "path": "carbon-input" }
      ]
    }
  ],
  "preloadRule": {
    "pages/entry/index": { "network": "all", "packages": ["pages/entry-sub"] }
  }
}
```

### 6.2 首屏加载优化

| 优化策略 | 具体措施 |
|----------|----------|
| 骨架屏 | 首页、列表页使用骨架屏占位，数据加载后替换 |
| 数据预加载 | 登录成功后预加载首页数据并缓存 |
| 接口聚合 | 使用 `/mobile/dashboard/summary` 聚合接口，减少请求数 |
| 本地缓存优先 | 首页先展示缓存数据，后台静默刷新 |
| 懒加载 | 非首屏内容（如告警列表、报表图表）延迟加载 |
| 启动图优化 | App启动图使用压缩PNG，控制在100KB以内 |

```typescript
// composables/useDashboard.ts
export function useDashboard() {
  const summary = ref<any>(null)
  const loading = ref(false)

  const loadSummary = async (forceRefresh = false) => {
    loading.value = true

    // 1. 先读取缓存
    if (!forceRefresh) {
      const cached = uni.getStorageSync('dashboard_summary')
      if (cached) {
        summary.value = JSON.parse(cached)
      }
    }

    // 2. 请求最新数据
    try {
      const data = await dashboardApi.getSummary()
      summary.value = data
      uni.setStorageSync('dashboard_summary', JSON.stringify(data))
    } finally {
      loading.value = false
    }
  }

  return { summary, loading, loadSummary }
}
```

### 6.3 图片懒加载与缓存

```vue
<!-- 图片懒加载组件 -->
<template>
  <image
    :src="currentSrc"
    :lazy-load="true"
    mode="aspectFill"
    @error="onError"
    @load="onLoad"
  />
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const props = defineProps<{
  src: string
  placeholder?: string
}>()

const loaded = ref(false)
const currentSrc = computed(() => loaded.value ? props.src : (props.placeholder || '/static/images/placeholder.png'))

const onLoad = () => { loaded.value = true }
const onError = () => { loaded.value = false }
</script>
```

### 6.4 数据缓存策略

| 数据类型 | 缓存方式 | 过期时间 | 更新策略 |
|----------|----------|----------|----------|
| Token | StorageSync | 7天 | 自动刷新 |
| 用户信息 | StorageSync | 会话级 | 登录时更新 |
| 仪表盘数据 | StorageSync | 5分钟 | 每次进入页面刷新 |
| 告警列表 | StorageSync | 2分钟 | 下拉刷新/轮询 |
| 设备树 | StorageSync | 1小时 | 手动刷新 |
| 字典数据 | StorageSync | 24小时 | 每日首次使用时刷新 |
| 报表数据 | SQLite | 无 | 按需加载 |

---

## 7. 安全方案

### 7.1 JWT Token管理

| 安全策略 | 具体措施 |
|----------|----------|
| Token存储 | Access Token存储于内存（Pinia），Refresh Token存储于加密Storage |
| Token传输 | 所有请求通过HTTPS传输，Header中携带 `Authorization: Bearer {token}` |
| Token刷新 | Access Token有效期2小时，过期前5分钟自动刷新；Refresh Token有效期7天 |
| Token失效处理 | 收到401响应后，尝试刷新Token；刷新失败则跳转登录页 |
| 多设备登录 | 后端支持多设备同时登录，各设备Token独立管理 |

```typescript
// utils/crypto.ts - 简单对称加密（App端）
const CRYPTO_KEY = 'EnergyCarbon2024' // 实际应从安全模块获取

export function encrypt(text: string): string {
  // 使用CryptoJS或原生加密模块
  // 示例：Base64 + 简单混淆
  const buffer = uni.arrayBufferToBase64(new TextEncoder().encode(text))
  return buffer.split('').reverse().join('')
}

export function decrypt(text: string): string {
  const reversed = text.split('').reverse().join('')
  const buffer = uni.base64ToArrayBuffer(reversed)
  return new TextDecoder().decode(buffer)
}

// 安全存储Token
export function setSecureStorage(key: string, value: string) {
  if (uni.getSystemInfoSync().platform === 'android' || uni.getSystemInfoSync().platform === 'ios') {
    // App端使用加密存储
    uni.setStorageSync(key, encrypt(value))
  } else {
    uni.setStorageSync(key, value)
  }
}

export function getSecureStorage(key: string): string {
  const value = uni.getStorageSync(key)
  if (!value) return ''
  if (uni.getSystemInfoSync().platform === 'android' || uni.getSystemInfoSync().platform === 'ios') {
    return decrypt(value)
  }
  return value
}
```

### 7.2 HTTPS与证书固定

```typescript
// App端启用证书固定（防止中间人攻击）
// manifest.json -> App模块配置
{
  "app-plus": {
    "ssl": {
      "untrustedca": "reject",
      "certificates": ["server.crt"]  // 内置服务器证书公钥
    }
  }
}
```

### 7.3 请求签名防篡改

```typescript
// 关键接口（数据录入、告警处理）增加请求签名
import CryptoJS from 'crypto-js'

function signRequest(params: Record<string, any>, timestamp: number, nonce: string): string {
  const sortedKeys = Object.keys(params).sort()
  const paramStr = sortedKeys.map(k => `${k}=${params[k]}`).join('&')
  const signStr = `${paramStr}&timestamp=${timestamp}&nonce=${nonce}&key=${API_SECRET_KEY}`
  return CryptoJS.MD5(signStr).toString().toUpperCase()
}

export function createSignedRequest(url: string, data: any) {
  const timestamp = Date.now()
  const nonce = Math.random().toString(36).substr(2, 10)
  const signature = signRequest(data, timestamp, nonce)

  return http.post(url, {
    ...data,
    _timestamp: timestamp,
    _nonce: nonce,
    _signature: signature
  })
}
```

### 7.4 输入安全与XSS防护

- 所有用户输入在提交前进行校验和转义
- 富文本内容使用 `rich-text` 组件渲染，避免直接插入HTML
- 图片上传限制格式（jpg/png）和大小（单张 < 5MB）
- 文件上传后端进行二次校验

### 7.5 代码安全

| 措施 | 说明 |
|------|------|
| 代码混淆 | App端发布时启用JS代码混淆 |
| 反调试 | 生产环境禁用调试工具 |
| 资源加密 | 敏感配置文件加密打包 |
| 防截屏（可选） | 敏感页面（如密码输入）可启用防截屏 |

---

## 8. 离线方案

### 8.1 离线场景分析

| 场景 | 网络状态 | 用户行为 | 解决方案 |
|------|----------|----------|----------|
| 地下配电房巡检 | 无信号 | 填写巡检表单、拍照 | 本地SQLite存储，恢复后同步 |
| 厂区抄表 | 弱网/无网 | 录入抄表读数 | 本地缓存，批量提交 |
| 碳排放现场核查 | 偶尔有网 | 查看历史数据、录入新数据 | 历史数据预缓存 + 离线写入 |
| 告警接收 | 网络波动 | 接收推送、处理告警 | 本地队列 + 重试机制 |

### 8.2 离线数据缓存架构

```
┌────────────────────────────────────────────────────────────┐
│                      在线状态                                │
│  ┌─────────────┐      ┌─────────────┐      ┌─────────────┐│
│  │  读取API数据  │─────>│  展示到页面   │─────>│  缓存到本地   ││
│  └─────────────┘      └─────────────┘      └─────────────┘│
│  ┌─────────────┐      ┌─────────────┐                     │
│  │  提交表单数据  │─────>│  直接调用API  │                     │
│  └─────────────┘      └─────────────┘                     │
└────────────────────────────────────────────────────────────┘
                              │
                    网络断开 / 弱网
                              │
┌────────────────────────────────────────────────────────────┐
│                      离线状态                                │
│  ┌─────────────┐      ┌─────────────┐      ┌─────────────┐│
│  │  读取本地缓存  │─────>│  展示到页面   │<─────│  预加载缓存   ││
│  └─────────────┘      └─────────────┘      └─────────────┘│
│  ┌─────────────┐      ┌─────────────┐      ┌─────────────┐│
│  │  填写表单数据  │─────>│  存入SQLite  │─────>│  加入同步队列  ││
│  └─────────────┘      └─────────────┘      └─────────────┘│
│                              │                             │
│                    网络恢复后自动同步                         │
│                              │                             │
│  ┌─────────────┐      ┌─────────────┐      ┌─────────────┐│
│  │  读取同步队列  │─────>│  批量调用API  │─────>│  标记同步成功  ││
│  └─────────────┘      └─────────────┘      └─────────────┘│
└────────────────────────────────────────────────────────────┘
```

### 8.3 离线同步流程

```typescript
// composables/useOfflineSync.ts
export function useOfflineSync() {
  const syncStore = useSyncStore()
  const appStore = useAppStore()

  // 监听网络状态变化
  const startNetworkListener = () => {
    uni.onNetworkStatusChange((res) => {
      appStore.setNetworkStatus(res.networkType)
      if (res.isConnected) {
        // 网络恢复，触发同步
        syncStore.processQueue()
      }
    })
  }

  // 手动触发同步（用户下拉或点击同步按钮）
  const manualSync = async () => {
    if (!appStore.isOnline) {
      uni.showToast({ title: '当前无网络，无法同步', icon: 'none' })
      return false
    }

    uni.showLoading({ title: '同步中...' })
    try {
      await syncStore.processQueue()
      uni.showToast({ title: '同步完成', icon: 'success' })
      return true
    } catch (e) {
      uni.showToast({ title: '同步失败，请重试', icon: 'none' })
      return false
    } finally {
      uni.hideLoading()
    }
  }

  return { startNetworkListener, manualSync }
}
```

### 8.4 抄表录入离线实现示例

```vue
<!-- pages/entry/meter-reading.vue -->
<template>
  <view class="container">
    <u-form :model="form" :rules="rules" ref="formRef">
      <u-form-item label="计量器具" prop="meterId" required>
        <u-input v-model="form.meterName" placeholder="点击扫码或选择" readonly @click="onSelectMeter">
          <template #suffix>
            <u-icon name="scan" size="20" @click="onScanMeter"></u-icon>
          </template>
        </u-input>
      </u-form-item>

      <u-form-item label="抄表读数" prop="readingValue" required>
        <u-input v-model="form.readingValue" type="digit" placeholder="请输入当前读数" />
      </u-form-item>

      <u-form-item label="抄表日期" prop="readingDate" required>
        <u-datetime-picker v-model="form.readingDate" mode="date" />
      </u-form-item>

      <u-form-item label="现场照片" prop="photos">
        <u-upload :fileList="fileList" @afterRead="afterRead" @delete="deletePic" maxCount="3" />
      </u-form-item>
    </u-form>

    <view class="sync-info" v-if="syncStore.pendingCount > 0">
      <text class="sync-text">待同步记录: {{ syncStore.pendingCount }} 条</text>
      <u-button type="primary" size="small" text="立即同步" @click="onSync"></u-button>
    </view>

    <u-button type="primary" text="保存" @click="onSubmit" :loading="submitting"></u-button>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useSyncStore } from '@/stores/sync'
import { useAppStore } from '@/stores/app'
import { SQLite } from '@/utils/storage'

const syncStore = useSyncStore()
const appStore = useAppStore()
const submitting = ref(false)

const form = reactive({
  meterId: null as number | null,
  meterName: '',
  readingValue: '',
  readingDate: new Date().toISOString().split('T')[0],
  photos: [] as string[]
})

const onSubmit = async () => {
  submitting.value = true
  try {
    const record = {
      meter_id: form.meterId,
      reading_value: parseFloat(form.readingValue),
      reading_date: form.readingDate,
      photo_path: JSON.stringify(form.photos),
      create_time: Date.now()
    }

    if (appStore.isOnline) {
      // 在线：直接提交
      await apiMonitor.addMeterReading(record)
      uni.showToast({ title: '保存成功', icon: 'success' })
    } else {
      // 离线：存入本地SQLite
      await SQLite.exec(`
        INSERT INTO meter_reading (meter_id, reading_value, reading_date, photo_path, status, create_time)
        VALUES (${record.meter_id}, ${record.reading_value}, '${record.reading_date}', '${record.photo_path}', 'pending', ${record.create_time})
      `)

      // 加入同步队列
      syncStore.addTask({
        api: '/api/monitor/reading',
        method: 'POST',
        data: record
      })

      uni.showToast({ title: '已离线保存，联网后自动同步', icon: 'none' })
    }

    // 重置表单
    form.meterId = null
    form.meterName = ''
    form.readingValue = ''
    form.photos = []
  } finally {
    submitting.value = false
  }
}

const onSync = async () => {
  const { manualSync } = useOfflineSync()
  await manualSync()
}

const onScanMeter = () => {
  uni.scanCode({
    success: async (res) => {
      const result = await scanApi.resolveCode(res.result)
      if (result.type === 'METER') {
        form.meterId = result.entity.id
        form.meterName = result.entity.meterName
      }
    }
  })
}
</script>
```

### 8.5 离线数据冲突解决策略

| 冲突场景 | 解决策略 |
|----------|----------|
| 同一抄表记录离线修改多次 | 以最后一次修改为准，同步时覆盖 |
| 离线录入与线上数据重复 | 后端根据设备ID+日期做唯一性校验，返回重复提示 |
| 离线期间设备/计量器具被删除 | 同步时后端返回实体不存在，移动端标记为失败并提示 |
| 多人同时录入同一设备 | 后端乐观锁（version字段），冲突时提示数据已变更 |

---

## 9. 项目目录结构

```
energy-carbon-mobile/
├── src/
│   ├── api/                          # API接口封装
│   │   ├── auth.ts
│   │   ├── dashboard.ts
│   │   ├── monitor.ts
│   │   ├── alarm.ts
│   │   ├── carbon.ts
│   │   ├── report.ts
│   │   ├── entry.ts
│   │   ├── system.ts
│   │   └── scan.ts
│   ├── components/                   # 公共组件
│   │   ├── AppNavbar.vue
│   │   ├── StatCard.vue
│   │   ├── TrendChart.vue
│   │   ├── AlarmItem.vue
│   │   ├── DataForm.vue
│   │   ├── ScanButton.vue
│   │   ├── EmptyState.vue
│   │   ├── PullRefresh.vue
│   │   └── SkeletonScreen.vue
│   ├── composables/                  # 组合式函数
│   │   ├── useAuth.ts
│   │   ├── useDashboard.ts
│   │   ├── useAlarm.ts
│   │   ├── useOfflineSync.ts
│   │   ├── useScan.ts
│   │   └── useChart.ts
│   ├── config/
│   │   ├── index.ts
│   │   ├── api.config.ts
│   │   ├── theme.config.ts
│   │   └── constants.ts
│   ├── pages/                        # 页面
│   │   ├── index/
│   │   │   └── index.vue             # 首页仪表盘
│   │   ├── entry/
│   │   │   ├── index.vue             # 录入首页
│   │   │   ├── meter-reading.vue     # 抄表录入
│   │   │   ├── inspection.vue        # 设备巡检
│   │   │   └── carbon-input.vue      # 碳排放录入
│   │   ├── alarm/
│   │   │   ├── list.vue
│   │   │   ├── detail.vue
│   │   │   └── handle.vue
│   │   ├── report/
│   │   │   ├── energy.vue
│   │   │   └── carbon.vue
│   │   ├── profile/
│   │   │   └── index.vue
│   │   └── login/
│   │       └── index.vue
│   ├── static/
│   │   ├── images/
│   │   ├── icons/
│   │   └── tabbar/
│   ├── stores/
│   │   ├── user.ts
│   │   ├── app.ts
│   │   ├── alarm.ts
│   │   └── sync.ts
│   ├── types/
│   │   ├── api.ts
│   │   ├── entity.ts
│   │   └── index.ts
│   ├── utils/
│   │   ├── request.ts
│   │   ├── storage.ts
│   │   ├── crypto.ts
│   │   ├── validate.ts
│   │   ├── format.ts
│   │   └── date.ts
│   ├── App.vue
│   ├── main.ts
│   ├── manifest.json
│   └── pages.json
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts
├── uni.scss
└── README.md
```

---

## 10. 部署与发布方案

### 10.1 构建配置

```typescript
// vite.config.ts
import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'

export default defineConfig({
  plugins: [uni()],
  build: {
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: true,
        drop_debugger: true
      }
    }
  }
})
```

### 10.2 多环境配置

```typescript
// config/index.ts
const env = import.meta.env.VITE_APP_ENV || 'development'

const config = {
  development: {
    API_BASE_URL: 'http://localhost:8080',
    UPLOAD_URL: 'http://localhost:8080/upload',
    PUSH_APPID: 'push_dev_appid'
  },
  testing: {
    API_BASE_URL: 'https://test-api.energy-carbon.com',
    UPLOAD_URL: 'https://test-api.energy-carbon.com/upload',
    PUSH_APPID: 'push_test_appid'
  },
  production: {
    API_BASE_URL: 'https://api.energy-carbon.com',
    UPLOAD_URL: 'https://api.energy-carbon.com/upload',
    PUSH_APPID: 'push_prod_appid'
  }
}

export default config[env as keyof typeof config]
```

### 10.3 发布渠道

| 平台 | 发布方式 | 审核周期 | 热更新支持 |
|------|----------|----------|------------|
| iOS App | Apple App Store | 1-3天 | 支持（wgt热更新） |
| Android App | 应用宝/华为/小米等应用市场 | 1-7天 | 支持（wgt热更新） |
| Android App | 企业内部分发（APK） | 即时 | 支持 |
| 微信小程序 | 微信公众平台 | 1-3天 | 不支持（需重新提交审核） |

### 10.4 热更新方案（App端）

```typescript
// utils/update.ts
export function checkUpdate() {
  plus.runtime.getProperty(plus.runtime.appid, (widgetInfo) => {
    uni.request({
      url: `${API_BASE_URL}/mobile/update/check`,
      data: { version: widgetInfo.version, platform: plus.os.name },
      success: (res) => {
        const data = res.data as any
        if (data.hasUpdate) {
          uni.showModal({
            title: '发现新版本',
            content: data.updateLog,
            success: (modalRes) => {
              if (modalRes.confirm) {
                downloadAndInstall(data.wgtUrl)
              }
            }
          })
        }
      }
    })
  })
}

function downloadAndInstall(wgtUrl: string) {
  uni.showLoading({ title: '下载中...' })
  uni.downloadFile({
    url: wgtUrl,
    success: (downloadRes) => {
      if (downloadRes.statusCode === 200) {
        plus.runtime.install(downloadRes.tempFilePath, {
          force: false
        }, () => {
          uni.hideLoading()
          uni.showModal({
            title: '更新完成',
            content: '新版本已准备好，是否立即重启？',
            success: (res) => {
              if (res.confirm) {
                plus.runtime.restart()
              }
            }
          })
        })
      }
    }
  })
}
```

### 10.5 监控与埋点

| 监控维度 | 工具/方案 | 说明 |
|----------|-----------|------|
| 崩溃监控 | uni-app统计 / 友盟+ | App崩溃率、错误堆栈 |
| 性能监控 | 自定义上报 | 首屏加载时间、接口耗时 |
| 业务埋点 | 自定义事件 | 功能使用频率、用户路径 |
| 离线统计 | 本地缓存+联网上报 | 离线操作日志同步 |

---

## 附录

### A. 技术栈版本锁定

| 依赖 | 版本 | 说明 |
|------|------|------|
| @dcloudio/uni-app | 3.99+ | UniApp Vue 3版本 |
| vue | 3.4+ | 响应式框架 |
| typescript | 5.0+ | 类型系统 |
| uview-plus | 3.2+ | UI组件库 |
| pinia | 2.1+ | 状态管理 |
| echarts | 5.4+ | 图表库（小程序） |
| ucharts | 2.5+ | 图表库（App，更轻量） |

### B. 接口版本兼容性

移动端API复用PC端 `/api/v1/` 版本接口。新增移动端专用接口以 `/mobile/` 为前缀，独立版本控制，避免影响PC端稳定性。

### C. 开发规范

- 组件名使用大驼峰（`StatCard.vue`）
- 组合式函数以 `use` 开头（`useDashboard.ts`）
- API模块以 `api` 后缀命名（`dashboardApi`）
- Store模块以 `Store` 后缀命名（`useUserStore`）
- 页面目录名小写，页面文件与目录同名（`pages/alarm/list.vue`）
- 所有异步操作使用 `async/await`，错误统一捕获

---

**文档说明**

本文档为能碳数智化平台移动端的技术设计文档，基于UniApp (Vue 3) 跨平台方案。文档涵盖技术选型、架构设计、API对接、UI规范、性能优化、安全方案、离线方案及部署方案，可作为移动端开发团队的实施依据。实际开发中可根据需求变化进行迭代调整。
