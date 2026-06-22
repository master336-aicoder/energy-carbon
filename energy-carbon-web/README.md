# energy-carbon-web

世通AI+能碳管理平台 - 前端项目

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue.js | 3.x | 渐进式JavaScript框架，采用Composition API |
| TypeScript | ~6.0.2 | 类型安全的JavaScript超集 |
| Vite | ^8.0.12 | 下一代前端构建工具，极速冷启动 |
| Element Plus | ^2.14.2 | 基于Vue 3的桌面端组件库 |
| Pinia | ^3.0.4 | Vue官方推荐的状态管理方案 |
| Vue Router | ^4.6.4 | Vue官方路由管理器 |
| ECharts | ^6.1.0 | 开源可视化库，用于数据图表展示 |
| Axios | ^1.18.0 | HTTP客户端，用于API请求 |
| Sass | ^1.101.0 | CSS预处理器 |

---

## 目录结构

```
energy-carbon-web/
├── public/                      # 静态公共资源
│   ├── favicon.svg
│   └── icons.svg
├── src/
│   ├── api/                     # API接口模块（按业务域划分）
│   │   ├── auth.ts              # 认证相关接口
│   │   ├── realtime.ts          # 实时数据接口
│   │   ├── monitorHistory.ts    # 历史数据接口
│   │   ├── alarm.ts             # 报警管理接口
│   │   ├── analysis.ts          # 用能分析接口
│   │   ├── carbon.ts            # 碳排放管理接口
│   │   ├── report.ts            # 统计报表接口
│   │   ├── toolkit.ts           # 能碳工具包接口
│   │   ├── user.ts              # 用户管理接口
│   │   ├── role.ts              # 角色管理接口
│   │   ├── dept.ts              # 部门/用能单元接口
│   │   ├── device.ts            # 用能设备接口
│   │   ├── meter.ts             # 计量器具接口
│   │   ├── energyType.ts        # 能源品种接口
│   │   ├── entry.ts             # 数据录入接口
│   │   ├── ai.ts                # AI服务接口
│   │   └── ...
│   ├── assets/                  # 静态资源文件
│   ├── layouts/                 # 布局组件
│   │   └── DefaultLayout.vue    # 默认布局（侧边栏+顶部栏+内容区）
│   ├── router/                  # 路由配置
│   │   └── index.ts             # 路由入口，含动态路由加载和路由守卫
│   ├── stores/                  # Pinia状态管理
│   │   ├── user.ts              # 用户状态（登录信息、权限、菜单）
│   │   └── app.ts               # 应用状态（侧边栏折叠、访问标签页）
│   ├── types/                   # TypeScript类型定义
│   │   ├── index.ts             # 通用类型
│   │   └── api.ts               # API相关类型
│   ├── utils/                   # 工具函数
│   │   ├── request.ts           # Axios封装（拦截器、错误处理）
│   │   ├── storage.ts           # 本地存储封装（localStorage/sessionStorage）
│   │   ├── menu.ts              # 菜单/路由处理工具
│   │   ├── echarts.ts           # ECharts配置工具
│   │   ├── validate.ts          # 表单验证工具
│   │   ├── loading.ts           # 加载状态管理
│   │   └── performance.ts       # 性能监控工具
│   ├── views/                   # 页面视图组件（按业务模块划分）
│   │   ├── auth/                # 认证相关
│   │   │   └── Login.vue
│   │   ├── dashboard/           # 首页仪表盘
│   │   │   └── Index.vue
│   │   ├── dualCarbon/          # 双碳考核
│   │   │   └── Index.vue
│   │   ├── monitor/             # 在线监测
│   │   │   ├── RealTime.vue     # 实时数据
│   │   │   ├── History.vue      # 历史数据
│   │   │   ├── DeviceCompare.vue # 设备对标
│   │   │   ├── TimeCompare.vue  # 时间对标
│   │   │   ├── MeterReading.vue # 计量抄表
│   │   │   └── StatusMonitor.vue # 状态监测
│   │   ├── alarm/               # 报警管理
│   │   │   ├── Config.vue       # 报警设置
│   │   │   └── Record.vue       # 报警查询
│   │   ├── analysis/            # 用能分析
│   │   │   ├── Consumption.vue  # 能耗查询
│   │   │   ├── Electricity.vue  # 电费分析
│   │   │   └── Budget.vue       # 能碳预算
│   │   ├── efficiency/          # 能效分析
│   │   │   ├── Index.vue        # 能效分析
│   │   │   └── EnergyFlow.vue   # 能流分析
│   │   ├── carbon/              # 碳排放管理
│   │   │   ├── Accounting.vue   # 碳排放核算
│   │   │   ├── Factor.vue       # 排放因子库
│   │   │   ├── Footprint.vue    # 产品碳足迹
│   │   │   ├── CBAM.vue         # CBAM核算
│   │   │   ├── SupplyChain.vue  # 供应链管理
│   │   │   └── Asset.vue        # 碳资产管理
│   │   ├── report/              # 统计报表
│   │   │   ├── Energy.vue       # 能耗统计表
│   │   │   ├── Summary.vue      # 能耗汇总表
│   │   │   ├── Saving.vue       # 节能量统计表
│   │   │   └── Cost.vue         # 用能成本统计表
│   │   ├── system/              # 系统管理
│   │   │   ├── User.vue         # 用户管理
│   │   │   ├── Role.vue         # 角色管理
│   │   │   ├── Unit.vue         # 用能单元/部门
│   │   │   ├── Device.vue       # 用能设备
│   │   │   └── Meter.vue        # 计量器具
│   │   └── toolkit/             # 能碳工具包
│   │       ├── AiReport.vue     # AI报告生成
│   │       ├── ElectricityPrice.vue # 分时电价管理
│   │       └── Knowledge.vue    # AI知识库
│   ├── App.vue                  # 根组件
│   ├── main.ts                  # 应用入口
│   ├── counter.ts               # 计数器示例（可删除）
│   └── style.css                # 全局样式
├── index.html                   # HTML入口
├── package.json                 # 依赖配置
├── vite.config.ts               # Vite构建配置
├── tsconfig.json                # TypeScript配置
├── .eslintrc.cjs                # ESLint配置
├── .prettierrc                  # Prettier配置
├── .eslintignore                # ESLint忽略文件
└── .gitignore                   # Git忽略文件
```

---

## 开发环境搭建

### 环境要求

- Node.js >= 18.x
- npm >= 9.x 或 pnpm >= 8.x

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

服务默认运行在 `http://localhost:3000`，会自动代理 `/api` 请求到 `http://localhost:8080`。

### 构建生产环境

```bash
npm run build
```

构建产物输出到 `dist/` 目录。

### 预览生产构建

```bash
npm run preview
```

---

## 常用命令

| 命令 | 说明 |
|------|------|
| `npm run dev` | 启动开发服务器 |
| `npm run build` | 构建生产环境（含TypeScript类型检查） |
| `npm run preview` | 预览生产构建 |
| `npm run lint` | 运行ESLint并自动修复问题 |
| `npm run lint:check` | 运行ESLint仅检查不修复 |
| `npm run format` | 运行Prettier格式化代码 |
| `npm run format:check` | 检查代码格式是否符合Prettier规范 |

---

## 代码规范

### ESLint 配置

项目使用 `.eslintrc.cjs` 配置，继承以下规则集：

- `eslint:recommended` - ESLint推荐规则
- `plugin:@typescript-eslint/recommended` - TypeScript推荐规则
- `plugin:vue/vue3-recommended` - Vue 3推荐规则
- `plugin:prettier/recommended` - Prettier集成规则

**主要自定义规则**：

| 规则 | 配置 | 说明 |
|------|------|------|
| `vue/multi-word-component-names` | `off` | 允许单单词组件名 |
| `vue/no-v-html` | `off` | 允许使用v-html（富文本场景） |
| `vue/require-default-prop` | `off` | 不强制要求默认prop |
| `@typescript-eslint/no-explicit-any` | `warn` | 显式使用any时警告 |
| `@typescript-eslint/no-unused-vars` | `warn` | 未使用变量警告（忽略下划线前缀参数） |
| `no-console` | `warn`(生产) / `off`(开发) | 生产环境警告console |
| `no-debugger` | `warn`(生产) / `off`(开发) | 生产环境警告debugger |
| `no-var` | `error` | 禁止使用var |
| `prefer-const` | `warn` | 建议使用const |

### Prettier 配置

项目使用 `.prettierrc` 配置：

```json
{
  "semi": false,           // 不使用分号
  "singleQuote": true,     // 使用单引号
  "tabWidth": 2,           // 缩进2个空格
  "useTabs": false,        // 使用空格而非Tab
  "printWidth": 120,       // 每行最大120字符
  "trailingComma": "none", // 不使用尾随逗号
  "bracketSpacing": true,  // 对象括号间保留空格
  "arrowParens": "avoid",  // 箭头函数单参数省略括号
  "endOfLine": "auto",     // 自动处理换行符
  "vueIndentScriptAndStyle": false,  // Vue文件不缩进script/style
  "htmlWhitespaceSensitivity": "strict"  // HTML空白敏感模式
}
```

### 提交前检查

建议在提交代码前运行以下命令确保代码质量：

```bash
npm run lint
npm run format
```

---

## 组件开发规范

### 文件命名

- 页面组件：使用大驼峰命名，如 `UserManagement.vue`
- 通用组件：使用大驼峰命名，如 `DataTable.vue`
- 组合式函数：使用 `use` 前缀，如 `usePagination.ts`

### 组件结构

推荐按照以下顺序组织组件代码：

```vue
<script setup lang="ts">
// 1. 类型导入
import type { UserInfo } from '@/types'

// 2. Vue/库导入
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

// 3. 组件导入
import DataTable from '@/components/DataTable.vue'

// 4. 类型定义
interface Props {
  title: string
}

// 5. Props/Emits定义
const props = defineProps<Props>()
const emit = defineEmits<{
  (e: 'submit', data: UserInfo): void
}>()

// 6. 状态定义
const loading = ref(false)
const list = ref<UserInfo[]>([])

// 7. 计算属性
const totalCount = computed(() => list.value.length)

// 8. 方法定义
async function fetchData() {
  loading.value = true
  try {
    // ...
  } finally {
    loading.value = false
  }
}

// 9. 生命周期钩子
onMounted(() => {
  fetchData()
})
</script>

<template>
  <!-- 模板内容 -->
</template>

<style scoped lang="scss">
/* 样式内容 */
</style>
```

### 组件设计原则

1. **单一职责**：每个组件只负责一个功能点
2. **Props向下传递**：数据通过props自上而下传递
3. **Events向上通知**：事件通过emit自下而上通知
4. **避免深层嵌套**：超过3层的组件嵌套考虑重构
5. **复用提取**：出现2次以上的相同UI逻辑提取为公共组件

---

## API调用规范

### 请求封装

项目使用 `src/utils/request.ts` 封装 Axios，已配置：

- 基础URL：`/api`（开发环境通过Vite代理到后端）
- 超时时间：30秒
- 请求拦截器：自动附加 JWT Token
- 响应拦截器：统一处理错误码和消息提示

### 接口定义规范

在 `src/api/` 目录下按业务模块定义接口：

```typescript
// src/api/user.ts
import request from '@/utils/request'
import type { UserInfo, PageResult } from '@/types'

export function getUserList(params: {
  pageNum: number
  pageSize: number
  keyword?: string
}) {
  return request.get<PageResult<UserInfo>>('/system/user/list', { params })
}

export function createUser(data: Partial<UserInfo>) {
  return request.post('/system/user', data)
}

export function updateUser(id: number, data: Partial<UserInfo>) {
  return request.put(`/system/user/${id}`, data)
}

export function deleteUser(id: number) {
  return request.delete(`/system/user/${id}`)
}
```

### 使用示例

```vue
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getUserList } from '@/api/user'
import type { UserInfo } from '@/types'

const userList = ref<UserInfo[]>([])
const loading = ref(false)

async function loadUsers() {
  loading.value = true
  try {
    const res = await getUserList({ pageNum: 1, pageSize: 10 })
    userList.value = res.data.list
  } finally {
    loading.value = false
  }
}

onMounted(loadUsers)
</script>
```

---

## 路由配置说明

### 路由架构

项目采用 **静态路由 + 动态路由** 的混合模式：

- **静态路由**：无需权限即可访问的页面（登录页、404页、部分工具包页面）
- **动态路由**：根据用户权限从后端获取菜单后动态生成

### 路由配置位置

- 静态路由：`src/router/index.ts` 中的 `staticRoutes`
- 动态路由：由后端 `sys_menu` 表数据生成，通过 `generateAsyncRoutes` 函数转换

### 路由守卫

`src/router/index.ts` 中实现了完整的路由守卫逻辑：

1. **白名单放行**：`/login`、`/404` 等路径无需登录
2. **未登录拦截**：未登录用户访问非公开页面重定向到登录页
3. **动态路由加载**：登录后获取用户菜单并动态添加路由
4. **权限检查**：根据 `meta.permission` 检查按钮级权限
5. **页面加载性能监控**：记录每个页面的加载耗时

### 路由元信息

```typescript
interface RouteMeta {
  title?: string        // 页面标题
  public?: boolean      // 是否公开页面（无需登录）
  permission?: string   // 所需权限标识
  keepAlive?: boolean   // 是否缓存页面
  icon?: string         // 菜单图标
}
```

---

## 状态管理说明

项目使用 **Pinia** 进行状态管理，按功能域划分为多个Store：

### User Store (`src/stores/user.ts`)

管理用户认证状态和权限信息：

| 状态/方法 | 说明 |
|-----------|------|
| `token` | JWT访问令牌 |
| `userInfo` | 用户基本信息 |
| `permissions` | 用户权限列表 |
| `menus` | 用户菜单列表 |
| `login()` | 登录并存储Token |
| `fetchUserInfo()` | 获取用户详情（含权限和菜单） |
| `logout()` | 退出登录并清理状态 |
| `hasPermission()` | 检查是否拥有指定权限 |
| `hasRole()` | 检查是否拥有指定角色 |

### App Store (`src/stores/app.ts`)

管理应用级UI状态：

| 状态/方法 | 说明 |
|-----------|------|
| `sidebarCollapsed` | 侧边栏折叠状态 |
| `visitedViews` | 已访问的页面标签列表 |
| `toggleSidebar()` | 切换侧边栏折叠状态 |
| `addVisitedView()` | 添加访问标签 |
| `removeVisitedView()` | 移除访问标签 |

### 使用示例

```vue
<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'

const userStore = useUserStore()
const appStore = useAppStore()

// 检查权限
const canEdit = userStore.hasPermission('system:user:edit')

// 切换侧边栏
function handleToggle() {
  appStore.toggleSidebar()
}
</script>
```

---

## 开发注意事项

1. **环境变量**：如需配置不同环境的API地址，可在项目根目录创建 `.env.development` 和 `.env.production` 文件，通过 `import.meta.env.VITE_*` 访问
2. **路径别名**：项目配置 `@` 指向 `src` 目录，导入时请使用 `@/api/xxx` 而非相对路径
3. **图标使用**：项目已全局注册 Element Plus 的所有图标组件，直接在模板中使用 `<el-icon><Search /></el-icon>`
4. **图表使用**：ECharts已作为依赖安装，建议在 `src/utils/echarts.ts` 中封装通用配置
5. **类型安全**：所有API接口和组件Props请尽量使用TypeScript类型定义

---

**文档版本**：V1.0  
**最后更新**：2026-06-22
