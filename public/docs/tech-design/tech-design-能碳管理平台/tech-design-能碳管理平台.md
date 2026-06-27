# 世通AI+能碳管理平台

技术设计文档（Technical Design Document）

前端：Vue 3 + Element Plus 后端：Spring Boot 2.x + JDK 1.8 编制日期：2026年6月27日

## 目录

  * 1\. 设计目标与约束
  * 2\. 系统整体架构
  * 3\. 前端技术架构
  * 3.1 技术栈选型
  * 3.2 项目目录结构
  * 3.3 路由与菜单设计
  * 3.4 状态管理设计
  * 3.5 组件设计规范
  * 3.6 API封装与请求拦截
  * 4\. 后端技术架构
  * 4.1 技术栈选型
  * 4.2 项目模块划分
  * 4.3 分层架构设计
  * 4.4 核心模块设计
  * 4.5 安全设计
  * 5\. 数据库设计
  * 5.1 数据库选型
  * 5.2 核心实体关系
  * 5.3 核心表结构设计
  * 6\. API接口设计
  * 6.1 接口规范
  * 6.2 认证授权机制
  * 6.3 核心接口清单
  * 7\. 部署架构
  * 8\. 开发规范



## 1\. 设计目标与约束

### 1.1 设计目标

本技术设计文档基于《世通AI+能碳管理平台需求文档》，将业务需求转化为可执行的技术方案。设计目标如下：

  * **高内聚低耦合** ：按业务域划分模块，模块间通过标准接口通信。
  * **可扩展** ：支持未来功能模块的灵活扩展，如新增能源品种、新增报表类型。
  * **高性能** ：时序数据查询秒级响应，报表生成不超过10秒。
  * **易维护** ：代码结构清晰，分层明确，便于团队协作与后续维护。
  * **安全合规** ：满足等保2.0三级要求，数据加密传输与存储。



### 1.2 技术约束

约束项| 要求| 说明  
---|---|---  
JDK版本| 1.8| 企业现有环境限制  
前端框架| Vue 3| 组合式API，更好的TypeScript支持  
后端框架| Spring Boot 2.7.x| 兼容JDK 1.8的稳定版本  
数据库| MySQL 5.7+| 主业务数据库  
时序数据库| InfluxDB 1.8+| 物联网实时数据存储  
缓存| Redis 5.0+| 会话、缓存、分布式锁  
消息队列| RabbitMQ / Redis Stream| 异步任务、数据同步  
  
## 2\. 系统整体架构

### 2.1 架构全景

系统采用前后端分离架构，前端基于Vue 3构建单页应用，后端基于Spring Boot提供RESTful API服务。数据层采用MySQL存储业务数据、InfluxDB存储时序数据、Redis作为缓存与会话存储。
    
    
    flowchart TB
        subgraph Client["客户端层"]
            PC["PC浏览器 Vue3 SPA"]
            Mobile["移动端 H5"]
        end
    
        subgraph Gateway["网关层"]
            Nginx["Nginx 反向代理/静态资源"]
        end
    
        subgraph Backend["后端服务层"]
            Auth["认证服务  
    Spring Security + JWT"]
            Biz["业务服务  
    Spring Boot"]
            AI["AI服务  
    大模型接口代理"]
            Report["报表服务  
    异步生成"]
        end
    
        subgraph Data["数据层"]
            MySQL[("MySQL 5.7  
    业务数据")]
            Influx[("InfluxDB  
    时序数据")]
            Redis[("Redis  
    缓存/会话")]
            MinIO["MinIO  
    文件存储"]
        end
    
        subgraph IoT["物联网层"]
            GatewayIoT["物联网网关"]
            Device["计量器具/传感器"]
        end
    
        PC --> Nginx
        Mobile --> Nginx
        Nginx --> Auth
        Nginx --> Biz
        Nginx --> AI
        Nginx --> Report
        Biz --> MySQL
        Biz --> Influx
        Biz --> Redis
        Biz --> MinIO
        AI --> Redis
        Report --> MySQL
        Report --> MinIO
        GatewayIoT --> Device
        GatewayIoT --> Biz
      

图1：系统整体架构图

### 2.2 技术栈总览

层级| 技术选型| 版本  
---|---|---  
前端框架| Vue| 3.4+  
前端UI库| Element Plus| 2.5+  
前端路由| Vue Router| 4.2+  
前端状态| Pinia| 2.1+  
前端构建| Vite| 5.0+  
后端框架| Spring Boot| 2.7.x  
ORM框架| MyBatis Plus| 3.5+  
安全框架| Spring Security| 5.7+  
认证方式| JWT| 0.11+  
数据库| MySQL| 5.7+  
时序数据库| InfluxDB| 1.8+  
缓存| Redis| 5.0+  
文件存储| MinIO| RELEASE.2024+  
  
## 3\. 前端技术架构

### 3.1 技术栈选型

**Vue 3 + Composition API**

采用组合式API，逻辑复用更灵活，TypeScript支持更好，性能优于Options API。

**Element Plus**

基于Vue 3的组件库，提供表格、表单、图表、树形控件等丰富组件，与需求文档中的界面风格匹配。

**Pinia**

Vue官方推荐状态管理方案，API简洁，TypeScript友好，替代Vuex。

**Vue Router 4**

支持动态路由、路由守卫、懒加载，适配平台多级菜单结构。

**ECharts 5**

数据可视化库，支持折线图、柱状图、饼图、桑基图等，满足平台图表需求。

**Axios**

HTTP客户端，统一封装请求拦截、响应拦截、错误处理。

### 3.2 项目目录结构
    
    
    energy-carbon-web/
    ├── public/                    # 静态资源
    │   └── favicon.ico
    ├── src/
    │   ├── api/                   # API接口封装（按模块组织）
    │   │   ├── auth.js            # 认证相关
    │   │   ├── monitor.js         # 在线监测
    │   │   ├── alarm.js           # 报警管理
    │   │   ├── analysis.js        # 用能/能效分析
    │   │   ├── carbon.js          # 碳排放管理
    │   │   ├── report.js          # 统计报表
    │   │   ├── tool.js            # 能碳工具包
    │   │   └── system.js          # 系统管理
    │   ├── assets/                # 图片、字体、样式
    │   │   ├── images/
    │   │   └── styles/
    │   │       └── variables.scss # 主题变量
    │   ├── components/            # 公共组件
    │   │   ├── AppHeader.vue      # 顶部导航
    │   │   ├── AppSidebar.vue     # 侧边菜单
    │   │   ├── DataTable.vue      # 封装表格（分页/排序/筛选）
    │   │   ├── SearchForm.vue     # 封装查询表单
    │   │   ├── StatCard.vue       # 统计卡片
    │   │   ├── TrendChart.vue     # 趋势图组件
    │   │   └── PieChart.vue       # 饼图组件
    │   ├── composables/           # 组合式函数
    │   │   ├── useTable.js        # 表格逻辑复用
    │   │   ├── useChart.js        # 图表逻辑复用
    │   │   └── usePermission.js   # 权限判断
    │   ├── layouts/               # 布局组件
    │   │   ├── DefaultLayout.vue  # 默认布局（侧边栏+内容区）
    │   │   └── BlankLayout.vue    # 空白布局（登录页）
    │   ├── router/                # 路由配置
    │   │   ├── index.js           # 路由入口
    │   │   ├── routes.js          # 路由表
    │   │   └── guards.js          # 路由守卫
    │   ├── stores/                # Pinia状态管理
    │   │   ├── user.js            # 用户信息/权限
    │   │   ├── app.js             # 应用状态（菜单/主题）
    │   │   └── tags.js            # 标签页状态
    │   ├── utils/                 # 工具函数
    │   │   ├── request.js         # Axios封装
    │   │   ├── auth.js            # Token操作
    │   │   ├── format.js          # 数据格式化
    │   │   └── validate.js        # 表单校验
    │   ├── views/                 # 页面组件（按模块组织）
    │   │   ├── dashboard/         # 首页仪表盘
    │   │   ├── dualCarbon/        # 双碳考核
    │   │   ├── monitor/           # 在线监测
    │   │   │   ├── RealTime.vue
    │   │   │   ├── History.vue
    │   │   │   ├── DeviceCompare.vue
    │   │   │   ├── TimeCompare.vue
    │   │   │   ├── MeterReading.vue
    │   │   │   └── StatusMonitor.vue
    │   │   ├── alarm/             # 报警管理
    │   │   ├── analysis/          # 用能分析/能效分析
    │   │   ├── carbon/            # 碳排放管理
    │   │   ├── report/            # 统计报表
    │   │   ├── dataEntry/         # 数据录入
    │   │   ├── aiService/         # AI智能服务
    │   │   ├── tool/              # 能碳工具包
    │   │   └── system/            # 基础配置
    │   ├── App.vue
    │   └── main.js
    ├── index.html
    ├── package.json
    ├── vite.config.js
    └── jsconfig.json
    

### 3.3 路由与菜单设计

路由结构对应需求文档中的菜单层级，采用动态路由加载方式，根据用户权限动态生成可访问的菜单。平台共包含 **9个一级菜单、56个功能页面**。

#### 菜单结构总览

| 一级菜单 | 二级菜单数量 | 页面文件 |
|----------|-------------|----------|
| 首页 | 1 | `dashboard/Index.vue` |
| 系统管理 | 11 | `system/User.vue`, `system/Role.vue`, `system/Menu.vue`, `system/Unit.vue`, `system/Dict.vue`, `system/Notice.vue`, `system/Settings.vue`, `system/Log.vue`, `system/Device.vue`, `system/Meter.vue`, `system/Profile.vue` |
| 在线监测 | 9 | `monitor/Data.vue`, `monitor/Device.vue`, `monitor/RealTime.vue`, `monitor/History.vue`, `monitor/DeviceCompare.vue`, `monitor/TimeCompare.vue`, `monitor/MeterReading.vue`, `monitor/StatusMonitor.vue`, `monitor/WorkShift.vue` |
| 报警管理 | 3 | `alarm/Config.vue`, `alarm/Record.vue` |
| 碳排放管理 | 8 | `carbon/Accounting.vue`, `carbon/Factor.vue`, `carbon/Asset.vue`, `carbon/CBAM.vue`, `carbon/Footprint.vue`, `carbon/SupplyChain.vue`, `carbon/Verification.vue`, `carbon/Market.vue` |
| 统计报表 | 7 | `report/Energy.vue`, `report/Summary.vue`, `report/Cost.vue`, `report/Saving.vue`, `report/Baseline.vue`, `report/Indicator.vue`, `report/Production.vue` |
| 分析管理 | 5 | `analysis/Consumption.vue`, `analysis/Electricity.vue`, `analysis/Efficiency.vue`, `analysis/Budget.vue`, `analysis/Benchmark.vue` |
| AI智能服务 | 4 | `ai/Service.vue`, `toolkit/Knowledge.vue`, `toolkit/AiReport.vue`, `ai/ReportTemplate.vue` |
| 能碳工具包 | 4 | `toolkit/ElectricityPrice.vue`, `toolkit/PeakShifting.vue`, `toolkit/GreenFactory.vue`, `toolkit/Calculator.vue` |

#### 路由配置示例

```typescript
const routes = [
  {
    path: '/',
    component: DefaultLayout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', component: () => import('@/views/dashboard/Index.vue'), meta: { title: '首页' } },
      {
        path: 'monitor',
        meta: { title: '在线监测' },
        children: [
          { path: 'data', component: () => import('@/views/monitor/Data.vue'), meta: { title: '监测数据' } },
          { path: 'device', component: () => import('@/views/monitor/Device.vue'), meta: { title: '设备管理' } },
          { path: 'realtime', component: () => import('@/views/monitor/RealTime.vue'), meta: { title: '实时数据' } },
          { path: 'history', component: () => import('@/views/monitor/History.vue'), meta: { title: '历史数据' } },
          { path: 'device-compare', component: () => import('@/views/monitor/DeviceCompare.vue'), meta: { title: '设备对标' } },
          { path: 'time-compare', component: () => import('@/views/monitor/TimeCompare.vue'), meta: { title: '时段对标' } },
          { path: 'meter-reading', component: () => import('@/views/monitor/MeterReading.vue'), meta: { title: '计量抄表' } },
          { path: 'status', component: () => import('@/views/monitor/StatusMonitor.vue'), meta: { title: '状态监测' } },
          { path: 'work-shift', component: () => import('@/views/monitor/WorkShift.vue'), meta: { title: '班组管理' } }
        ]
      },
      // ... 其他模块
    ]
  },
  { path: '/login', component: () => import('@/views/auth/Login.vue'), meta: { public: true } }
]
```

#### 动态菜单与RBAC

- 侧边栏菜单从后端 `/auth/userinfo` 接口获取 `menus` 字段，动态构建菜单树
- 超级管理员（roleId=1）自动拥有全部菜单权限
- 菜单数据存储于 `sys_menu` 表，通过 `sys_role_menu` 关联角色与菜单
- 前端 `DefaultLayout.vue` 的 `buildSidebarMenus` 函数负责将后端菜单数据转换为 `el-menu` 可用的树形结构
    

### 3.4 状态管理设计

使用Pinia按功能域划分Store，避免单一Store过于庞大。
    
    
    // stores/user.js - 用户状态
    import { defineStore } from 'pinia'
    export const useUserStore = defineStore('user', {
      state: () => ({
        token: localStorage.getItem('token'),
        userInfo: null,
        permissions: [],      // 按钮级权限
        menus: []             // 菜单树
      }),
      actions: {
        async login(credentials) { /* 登录获取token */ },
        async fetchUserInfo() { /* 获取用户信息与权限 */ },
        logout() { /* 清除状态 */ },
        hasPermission(perm) { return this.permissions.includes(perm) }
      }
    })
    
    // stores/app.js - 应用状态
    export const useAppStore = defineStore('app', {
      state: () => ({
        sidebarCollapsed: false,
        theme: 'light',
        visitedViews: []      // 已访问的标签页
      })
    })
    

### 3.5 组件设计规范

组件名| 用途| Props  
---|---|---  
DataTable| 通用数据表格| columns, data, loading, pagination, showIndex  
SearchForm| 查询条件表单| fields, modelValue, onSearch, onReset  
StatCard| KPI统计卡片| title, value, unit, trend, color  
TrendChart| 趋势折线图| data, xAxis, series, title  
PieChart| 占比饼图| data, title, legendPosition  
UnitTree| 用能单元树| data, defaultExpanded, onSelect  
DynamicIcon| 动态图标加载| name（支持 Element Plus 图标名或 `custom-*` 自定义 SVG）  
NoticeBanner| 首页通知滚动横幅| noticeList（通知数组）  
  
### 3.6 API封装与请求拦截
    
    
    // utils/request.js - Axios封装
    import axios from 'axios'
    import { useUserStore } from '@/stores/user'
    
    const request = axios.create({
      baseURL: import.meta.env.VITE_API_BASE_URL,
      timeout: 30000
    })
    
    // 请求拦截：添加Token
    request.interceptors.request.use(config => {
      const userStore = useUserStore()
      if (userStore.token) {
        config.headers.Authorization = 'Bearer ' + userStore.token
      }
      return config
    })
    
    // 响应拦截：统一错误处理
    request.interceptors.response.use(
      response => response.data,
      error => {
        if (error.response?.status === 401) {
          // Token过期，跳转登录
          useUserStore().logout()
          window.location.href = '/login'
        }
        return Promise.reject(error)
      }
    )
    
    export default request
    

## 4\. 后端技术架构

### 4.1 技术栈选型

**Spring Boot 2.7.x**

兼容JDK 1.8的稳定版本，自动配置，快速开发，生态成熟。

**MyBatis Plus 3.5+**

增强版ORM框架，提供通用CRUD、分页插件、代码生成器，减少重复代码。

**Spring Security**

认证授权框架，支持JWT无状态认证，RBAC权限控制。

**JWT**

JSON Web Token实现无状态认证，支持Token刷新机制。

**Redis**

缓存热点数据、用户会话、分布式锁、限流计数器。

**InfluxDB**

时序数据库，高效存储和查询物联网设备产生的海量时序数据。

### 4.2 项目模块划分

采用多模块Maven项目结构，按业务域拆分模块，每个模块独立编译部署。
    
    
    energy-carbon-platform/
    ├── energy-carbon-common/          # 公共模块
    │   ├── src/main/java/
    │   │   └── com/seatone/common/
    │   │       ├── constant/          # 常量定义
    │   │       ├── exception/         # 全局异常
    │   │       ├── result/            # 统一响应封装
    │   │       ├── utils/             # 工具类
    │   │       └── base/              # 基础实体/Mapper/Service
    │   └── pom.xml
    ├── energy-carbon-system/          # 系统管理模块
    │   ├── 用户管理、角色管理、菜单管理、部门管理
    │   └── 操作日志、数据字典
    ├── energy-carbon-monitor/         # 在线监测模块
    │   ├── 实时数据、历史数据、设备对标、时间对标
    │   └── 计量抄表、状态监测
    ├── energy-carbon-alarm/           # 报警管理模块
    │   ├── 报警设置、报警查询、报警处理
    ├── energy-carbon-analysis/        # 分析模块
    │   ├── 用能分析、能效分析、能流分析、电费分析
    ├── energy-carbon-carbon/          # 碳排放管理模块
    │   ├── 碳排放核算、产品碳足迹、CBAM核算
    │   ├── 供应链管理、碳资产、碳排放因子
    │   └── 碳核查支撑、碳核算建模、碳足迹建模
    ├── energy-carbon-report/          # 统计报表模块
    │   ├── 能耗统计表、能耗汇总表、节能量统计表、用能成本统计表
    ├── energy-carbon-data/            # 数据录入模块
    │   ├── 指标录入、产量录入
    ├── energy-carbon-ai/              # AI服务模块
    │   ├── AI报告生成、知识库、第三方服务对接
    ├── energy-carbon-tool/            # 能碳工具包模块
    │   ├── 绿色工厂申报、错峰排产、能碳计算器、分时电价
    ├── energy-carbon-gateway/         # 物联网网关接入模块
    │   ├── 设备接入、数据解析、协议适配
    ├── energy-carbon-admin/           # 启动模块
    │   └── 整合所有模块，提供启动入口
    └── pom.xml
    

### 4.3 分层架构设计

每个业务模块内部采用经典分层架构：Controller → Service → Mapper → Entity。
    
    
    flowchart TB
        subgraph Controller["表现层 Controller"]
            C1["@RestController  
    接收请求/参数校验  
    返回统一响应"]
        end
    
        subgraph Service["业务层 Service"]
            S1["@Service  
    业务逻辑编排  
    事务控制"]
            S2["接口定义 + 实现类"]
        end
    
        subgraph Mapper["数据层 Mapper"]
            M1["@Mapper  
    MyBatis Plus  
    数据库操作"]
        end
    
        subgraph Entity["模型层 Entity/VO/DTO"]
            E1["Entity: 数据库实体"]
            E2["DTO: 请求/响应传输对象"]
            E3["VO: 视图对象"]
        end
    
        C1 --> S1
        S1 --> S2
        S2 --> M1
        M1 --> E1
        C1 --> E2
        C1 --> E3
      

图2：后端分层架构

#### 分层职责说明

层级| 职责| 规范  
---|---|---  
Controller| 接收HTTP请求，参数校验，调用Service，返回Result| 不处理业务逻辑  
Service| 业务逻辑编排，事务管理，调用Mapper或其他Service| 接口+实现分离  
Mapper| 数据库CRUD操作，复杂查询使用XML或@Select| 继承BaseMapper  
Entity| 与数据库表一一对应的POJO| 使用@TableName/@TableField  
DTO| 请求参数封装、响应数据封装| 使用@Valid校验  
  
### 4.4 核心模块设计

#### 4.4.1 在线监测模块

该模块处理物联网设备实时数据的接收、存储与查询，是系统的数据入口。
    
    
    // 实时数据接收（MQTT/HTTP）
    @RestController
    @RequestMapping("/api/monitor/realtime")
    public class RealTimeController {
    
        @GetMapping("/list")
        public Result<PageResult<RealTimeDataVO>> list(
                @RequestParam Long unitId,
                @RequestParam String energyType,
                @RequestParam(defaultValue = "1") Integer page,
                @RequestParam(defaultValue = "20") Integer size) {
            return Result.ok(realTimeService.queryList(unitId, energyType, page, size));
        }
    
        @GetMapping("/history")
        public Result<List<HistoryDataVO>> history(
                @RequestParam Long deviceId,
                @RequestParam String metric,
                @RequestParam String startTime,
                @RequestParam String endTime) {
            return Result.ok(realTimeService.queryHistory(deviceId, metric, startTime, endTime));
        }
    }
    
    // Service层：根据查询条件选择数据源
    @Service
    public class RealTimeServiceImpl implements RealTimeService {
    
        @Autowired
        private RealTimeDataMapper realTimeDataMapper;    // MySQL - 最新快照
        @Autowired
        private InfluxDBService influxDBService;          // InfluxDB - 历史时序
    
        @Override
        public PageResult<RealTimeDataVO> queryList(Long unitId, String energyType, int page, int size) {
            // 查询MySQL中的设备最新数据快照
            return realTimeDataMapper.selectPage(unitId, energyType, page, size);
        }
    
        @Override
        public List<HistoryDataVO> queryHistory(Long deviceId, String metric, String start, String end) {
            // 查询InfluxDB时序数据
            String influxQL = String.format(
                "SELECT * FROM \"%s\" WHERE device_id='%s' AND time >= '%s' AND time <= '%s'",
                metric, deviceId, start, end
            );
            return influxDBService.query(influxQL);
        }
    }
    

#### 4.4.2 碳排放核算模块

碳排放核算涉及多表关联计算，需要支持灵活的模型配置和活动数据录入。
    
    
    // 碳排放核算Controller
    @RestController
    @RequestMapping("/api/carbon/accounting")
    public class CarbonAccountingController {
    
        @GetMapping("/summary")
        public Result<CarbonSummaryVO> summary(
                @RequestParam Integer year,
                @RequestParam Long unitId) {
            return Result.ok(accountingService.calculateSummary(year, unitId));
        }
    
        @PostMapping("/activity")
        public Result<Void> addActivity(@RequestBody @Valid CarbonActivityDTO dto) {
            accountingService.addActivity(dto);
            return Result.ok();
        }
    
        @GetMapping("/report")
        public Result<String> generateReport(
                @RequestParam Integer year,
                @RequestParam Long unitId) {
            String reportUrl = reportService.generateCarbonReport(year, unitId);
            return Result.ok(reportUrl);
        }
    }
    
    // 碳排放计算Service
    @Service
    public class CarbonAccountingServiceImpl implements CarbonAccountingService {
    
        @Autowired
        private CarbonActivityMapper activityMapper;
        @Autowired
        private EmissionFactorMapper factorMapper;
    
        @Override
        @Transactional
        public CarbonSummaryVO calculateSummary(Integer year, Long unitId) {
            // 1. 查询该年度所有活动数据
            List<CarbonActivity> activities = activityMapper.selectByYearAndUnit(year, unitId);
    
            // 2. 按范围分组计算排放量
            BigDecimal scope1 = BigDecimal.ZERO;
            BigDecimal scope2 = BigDecimal.ZERO;
            BigDecimal scope3 = BigDecimal.ZERO;
    
            for (CarbonActivity activity : activities) {
                EmissionFactor factor = factorMapper.selectById(activity.getFactorId());
                BigDecimal emission = activity.getActivityLevel()
                    .multiply(factor.getFactorValue())
                    .divide(new BigDecimal("1000"), 4, RoundingMode.HALF_UP); // 转换为tCO2e
    
                switch (activity.getScopeType()) {
                    case "SCOPE_1": scope1 = scope1.add(emission); break;
                    case "SCOPE_2": scope2 = scope2.add(emission); break;
                    case "SCOPE_3": scope3 = scope3.add(emission); break;
                }
            }
    
            // 3. 组装汇总结果
            CarbonSummaryVO vo = new CarbonSummaryVO();
            vo.setScope1(scope1);
            vo.setScope2(scope2);
            vo.setScope3(scope3);
            vo.setTotal(scope1.add(scope2).add(scope3));
            vo.setScope1Ratio(scope1.divide(vo.getTotal(), 4, RoundingMode.HALF_UP));
            return vo;
        }
    }
    

#### 4.4.3 操作日志模块

基于 Spring AOP 实现无侵入式操作日志记录，通过自定义 `@Log` 注解标记需要记录的 Controller 方法。

```java
// 日志注解
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    String title();
    int businessType() default 0; // 0其他 1新增 2修改 3删除 4查询 5导出
}

// AOP 拦截器
@Aspect
@Component
public class LogAspect {
    @Autowired
    private SysLogService sysLogService;

    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();
    private static final ThreadLocal<SysLog> LOG_THREAD_LOCAL = new ThreadLocal<>();

    @Before("@annotation(com.seatone.system.annotation.Log)")
    public void doBefore(JoinPoint joinPoint) {
        START_TIME.set(System.currentTimeMillis());
        SysLog sysLog = new SysLog();
        sysLog.setOperTime(LocalDateTime.now());
        // 记录请求信息、操作人、请求参数...
        LOG_THREAD_LOCAL.set(sysLog);
    }

    @AfterReturning(pointcut = "@annotation(com.seatone.system.annotation.Log)", returning = "result")
    public void doAfterReturning(Object result) {
        SysLog sysLog = LOG_THREAD_LOCAL.get();
        sysLog.setCostTime(System.currentTimeMillis() - START_TIME.get());
        sysLog.setStatus(0);
        sysLog.setJsonResult(JSON.toJSONString(result));
        sysLogService.addLog(sysLog);
        clearThreadLocal();
    }

    @AfterThrowing(pointcut = "@annotation(com.seatone.system.annotation.Log)", throwing = "ex")
    public void doAfterThrowing(Exception ex) {
        SysLog sysLog = LOG_THREAD_LOCAL.get();
        sysLog.setCostTime(System.currentTimeMillis() - START_TIME.get());
        sysLog.setStatus(1);
        sysLog.setErrorMsg(ex.getMessage());
        sysLogService.addLog(sysLog);
        clearThreadLocal();
    }
}
```

#### 4.4.4 通知公告模块

通知公告支持富文本编辑（WangEditor），首页以滚动横幅形式展示未读公告，顶部导航栏显示未读数量角标。

```java
@RestController
@RequestMapping("/api/system/notice")
public class SysNoticeController {

    @GetMapping("/list")
    public Result<PageResult<SysNotice>> list(NoticeQueryDTO query) {
        return Result.ok(noticeService.list(query));
    }

    @GetMapping("/unread")
    public Result<List<SysNotice>> unread() {
        return Result.ok(noticeService.getUnreadList());
    }

    @PostMapping
    public Result<Void> add(@RequestBody @Valid SysNotice notice) {
        noticeService.add(notice);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysNotice notice) {
        noticeService.update(id, notice);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return Result.ok();
    }
}
```

前端首页通知滚动实现：

```vue
<div v-if="noticeList.length > 0" class="notice-banner">
  <el-icon class="notice-icon"><Bell /></el-icon>
  <div class="notice-scroll-wrapper">
    <div class="notice-scroll-content" :style="scrollStyle">
      <div v-for="(notice, index) in noticeList" :key="index" class="notice-item">
        <el-tag size="small" :type="notice.noticeType === 1 ? 'primary' : 'warning'">
          {{ notice.noticeType === 1 ? '通知' : '公告' }}
        </el-tag>
        <span class="notice-title">{{ notice.noticeTitle }}</span>
        <span class="notice-time">{{ notice.createTime }}</span>
      </div>
    </div>
  </div>
</div>
```

#### 4.4.5 AI报告生成模块

AI模块作为代理服务，调用大模型API生成各类报告，需要处理异步生成和结果缓存。
    
    
    // AI报告生成Service
    @Service
    public class AIReportServiceImpl implements AIReportService {
    
        @Autowired
        private RestTemplate restTemplate;
        @Autowired
        private StringRedisTemplate redisTemplate;
        @Autowired
        private ReportTaskMapper reportTaskMapper;
    
        @Value("${ai.api.url}")
        private String aiApiUrl;
        @Value("${ai.api.key}")
        private String aiApiKey;
    
        @Async("taskExecutor")
        @Override
        public void generateReportAsync(Long taskId, ReportTemplate template, Map<String, Object> data) {
            try {
                // 1. 更新任务状态为生成中
                reportTaskMapper.updateStatus(taskId, "GENERATING");
    
                // 2. 构建Prompt
                String prompt = buildPrompt(template, data);
    
                // 3. 调用大模型API
                AIRequest request = new AIRequest(prompt, template.getModelType());
                AIResponse response = restTemplate.postForObject(aiApiUrl, request, AIResponse.class);
    
                // 4. 保存生成结果到MinIO
                String reportUrl = minioService.uploadReport(taskId, response.getContent());
    
                // 5. 更新任务状态为完成
                reportTaskMapper.updateStatus(taskId, "COMPLETED", reportUrl);
            } catch (Exception e) {
                reportTaskMapper.updateStatus(taskId, "FAILED", e.getMessage());
            }
        }
    
        private String buildPrompt(ReportTemplate template, Map<String, Object> data) {
            StringBuilder prompt = new StringBuilder();
            prompt.append("请根据以下数据生成").append(template.getName()).append("：\n\n");
            prompt.append("企业名称：").append(data.get("companyName")).append("\n");
            prompt.append("报告年度：").append(data.get("year")).append("\n");
            prompt.append("碳排放数据：").append(JSON.toJSONString(data.get("emissionData"))).append("\n");
            prompt.append("\n要求：\n");
            prompt.append("1. 按照国家标准格式输出\n");
            prompt.append("2. 包含数据分析和改进建议\n");
            prompt.append("3. 使用专业术语\n");
            return prompt.toString();
        }
    }
    

### 4.5 安全设计

#### 4.5.1 认证流程（JWT）
    
    
    sequenceDiagram
        participant Client as 前端
        participant Gateway as Nginx
        participant Auth as AuthController
        participant User as UserService
        participant Redis as Redis
    
        Client->>Auth: POST /api/auth/login  
    {username, password}
        Auth->>User: 验证用户名密码
        User-->>Auth: 验证通过
        Auth->>Auth: 生成AccessToken + RefreshToken
        Auth->>Redis: 存储RefreshToken (7天过期)
        Auth-->>Client: {accessToken, refreshToken, expiresIn}
    
        Client->>Gateway: 后续请求携带 Authorization: Bearer {accessToken}
        Gateway->>Gateway: 验证Token有效性
        Gateway-->>Client: 返回业务数据
    
        Client->>Auth: POST /api/auth/refresh  
    {refreshToken}
        Auth->>Redis: 验证RefreshToken
        Redis-->>Auth: 有效
        Auth->>Auth: 生成新AccessToken
        Auth-->>Client: {accessToken, expiresIn}
      

图3：JWT认证流程

#### 4.5.2 权限控制（RBAC）
    
    
    // Spring Security配置
    @Configuration
    @EnableWebSecurity
    public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/monitor/realtime/**").hasAuthority("monitor:realtime:view")
                .antMatchers("/api/carbon/accounting/**").hasAuthority("carbon:accounting:view")
                .antMatchers("/api/system/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        }
    }
    
    // JWT过滤器
    @Component
    public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain chain) throws ServletException, IOException {
            String token = resolveToken(request);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            chain.doFilter(request, response);
        }
    }
    

#### 4.5.3 数据权限

通过AOP拦截Service层方法，根据用户所属部门/用能单元动态拼接数据范围条件。
    
    
    // 数据权限注解
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DataScope {
        String deptAlias() default "dept_id";
        String unitAlias() default "unit_id";
    }
    
    // AOP拦截
    @Aspect
    @Component
    public class DataScopeAspect {
    
        @Before("@annotation(dataScope)")
        public void before(JoinPoint point, DataScope dataScope) {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser.isAdmin()) return;
    
            // 获取用户数据权限范围
            List<Long> deptIds = currentUser.getDataScopeDeptIds();
            List<Long> unitIds = currentUser.getDataScopeUnitIds();
    
            // 将条件存入ThreadLocal，供Mapper使用
            DataScopeContext.set(new DataScopeCondition(deptIds, unitIds));
        }
    }
    

## 5\. 数据库设计

### 5.1 数据库选型

数据库| 用途| 数据类型  
---|---|---  
MySQL 5.7+| 主业务数据库| 用户、角色、菜单、设备台账、碳排放活动、报表配置等结构化数据  
InfluxDB 1.8+| 时序数据库| 物联网设备实时采集数据（电流、电压、能耗等）  
Redis 5.0+| 缓存/会话| 用户会话、热点数据缓存、分布式锁、限流计数  
MinIO| 对象存储| 报告文件、凭证附件、导入模板  
  
### 5.2 核心实体关系
    
    
    erDiagram
        SYS_USER ||--o{ SYS_USER_ROLE : has
        SYS_ROLE ||--o{ SYS_USER_ROLE : has
        SYS_ROLE ||--o{ SYS_ROLE_MENU : has
        SYS_MENU ||--o{ SYS_ROLE_MENU : has
        SYS_USER ||--o{ ENERGY_UNIT : manages
        ENERGY_UNIT ||--o{ ENERGY_DEVICE : contains
        ENERGY_DEVICE ||--o{ METER_INSTRUMENT : has
        ENERGY_UNIT ||--o{ CARBON_ACTIVITY : produces
        CARBON_ACTIVITY }o--|| EMISSION_FACTOR : uses
        ENERGY_UNIT ||--o{ ALARM_CONFIG : has
        ALARM_CONFIG ||--o{ ALARM_RECORD : generates
    
        SYS_USER {
            bigint id PK
            varchar username
            varchar password
            varchar nickname
            varchar phone
            bigint dept_id
            int status
            datetime create_time
        }
    
        SYS_ROLE {
            bigint id PK
            varchar role_name
            varchar role_code
            int status
        }
    
        ENERGY_UNIT {
            bigint id PK
            varchar unit_name
            bigint parent_id
            varchar unit_type
            int status
        }
    
        ENERGY_DEVICE {
            bigint id PK
            varchar device_code
            varchar device_name
            bigint unit_id
            varchar energy_type
            decimal power
            int status
        }
    
        METER_INSTRUMENT {
            bigint id PK
            varchar meter_code
            varchar meter_name
            bigint device_id
            varchar meter_level
            int status
        }
    
        CARBON_ACTIVITY {
            bigint id PK
            int year
            bigint unit_id
            varchar scope_type
            varchar activity_name
            varchar emission_source
            decimal activity_level
            varchar unit
            bigint factor_id
            decimal emission
        }
    
        EMISSION_FACTOR {
            bigint id PK
            varchar factor_name
            varchar category
            varchar standard
            decimal factor_value
            varchar factor_unit
        }
    
        ALARM_CONFIG {
            bigint id PK
            bigint unit_id
            varchar metric_name
            varchar metric_unit
            decimal upper_limit_day
            decimal lower_limit_day
            decimal upper_limit_month
            decimal lower_limit_month
        }
    
        ALARM_RECORD {
            bigint id PK
            bigint config_id
            datetime alarm_time
            varchar alarm_type
            decimal alarm_value
            varchar alarm_status
            varchar handler
            varchar handle_method
        }
      

图4：核心实体关系图（ER图）

### 5.3 核心表结构设计

#### 5.3.1 用户权限相关表
    
    
    -- 用户表
    CREATE TABLE sys_user (
        id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
        username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录账号',
        password VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
        nickname VARCHAR(50) COMMENT '昵称',
        phone VARCHAR(20) COMMENT '手机号',
        email VARCHAR(50) COMMENT '邮箱',
        avatar VARCHAR(200) COMMENT '头像URL',
        dept_id BIGINT COMMENT '部门ID',
        status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
        create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
        update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        INDEX idx_dept_id (dept_id)
    ) COMMENT='系统用户表';
    
    -- 角色表
    CREATE TABLE sys_role (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
        role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
        role_sort INT DEFAULT 0 COMMENT '显示顺序',
        status TINYINT DEFAULT 1,
        create_time DATETIME DEFAULT CURRENT_TIMESTAMP
    ) COMMENT='角色表';
    
    -- 用户角色关联表
    CREATE TABLE sys_user_role (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        user_id BIGINT NOT NULL,
        role_id BIGINT NOT NULL,
        UNIQUE KEY uk_user_role (user_id, role_id)
    ) COMMENT='用户角色关联表';
    
    -- 菜单表
    CREATE TABLE sys_menu (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
        parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
        menu_type TINYINT COMMENT '类型：1目录 2菜单 3按钮',
        icon VARCHAR(50) COMMENT '图标',
        path VARCHAR(200) COMMENT '路由路径',
        component VARCHAR(200) COMMENT '组件路径',
        perms VARCHAR(100) COMMENT '权限标识',
        sort_order INT DEFAULT 0,
        status TINYINT DEFAULT 1
    ) COMMENT='菜单权限表';
    
    -- 角色菜单关联表
    CREATE TABLE sys_role_menu (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        role_id BIGINT NOT NULL,
        menu_id BIGINT NOT NULL,
        UNIQUE KEY uk_role_menu (role_id, menu_id)
    ) COMMENT='角色菜单关联表';
    

#### 5.3.2 用能单元与设备表
    
    
    -- 用能单元表（树形结构）
    CREATE TABLE energy_unit (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        unit_name VARCHAR(100) NOT NULL COMMENT '单元名称',
        parent_id BIGINT DEFAULT 0 COMMENT '父单元ID',
        unit_type VARCHAR(20) COMMENT '类型：GROUP/ FACTORY/ WORKSHOP/ LINE',
        unit_level INT COMMENT '层级：1集团 2分厂 3车间 4产线',
        sort_order INT DEFAULT 0,
        status TINYINT DEFAULT 1,
        create_time DATETIME DEFAULT CURRENT_TIMESTAMP
    ) COMMENT='用能单元表';
    
    -- 用能设备表
    CREATE TABLE energy_device (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        device_code VARCHAR(50) NOT NULL UNIQUE COMMENT '设备编号',
        device_name VARCHAR(100) NOT NULL COMMENT '设备名称',
        unit_id BIGINT NOT NULL COMMENT '所属用能单元',
        model_spec VARCHAR(100) COMMENT '型号规格',
        install_location VARCHAR(200) COMMENT '安装地点',
        manufacturer VARCHAR(100) COMMENT '生产厂家',
        energy_type VARCHAR(20) COMMENT '能源品种',
        power DECIMAL(10,2) COMMENT '设备功率',
        power_unit VARCHAR(10) COMMENT '功率单位',
        start_date DATE COMMENT '启用时间',
        status TINYINT DEFAULT 1 COMMENT '状态：0停用 1启用',
        INDEX idx_unit_id (unit_id)
    ) COMMENT='用能设备表';
    
    -- 计量器具表
    CREATE TABLE meter_instrument (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        meter_code VARCHAR(50) NOT NULL UNIQUE COMMENT '器具编号',
        meter_name VARCHAR(100) NOT NULL COMMENT '器具名称',
        device_id BIGINT COMMENT '所属设备',
        unit_id BIGINT NOT NULL COMMENT '所属用能单元',
        model_spec VARCHAR(100) COMMENT '型号规格',
        meter_level VARCHAR(20) COMMENT '计量等级',
        install_location VARCHAR(200) COMMENT '安装位置',
        manufacturer VARCHAR(100) COMMENT '生产厂家',
        energy_type VARCHAR(20) COMMENT '能源品种',
        start_date DATE COMMENT '启用时间',
        status TINYINT DEFAULT 1 COMMENT '0离线 1在线',
        INDEX idx_unit_id (unit_id),
        INDEX idx_device_id (device_id)
    ) COMMENT='计量器具表';
    

#### 5.3.3 碳排放相关表
    
    
    -- 碳排放活动表
    CREATE TABLE carbon_activity (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        year INT NOT NULL COMMENT '核算年度',
        unit_id BIGINT NOT NULL COMMENT '用能单元',
        scope_type VARCHAR(20) NOT NULL COMMENT '范围：SCOPE_1/SCOPE_2/SCOPE_3',
        category VARCHAR(50) COMMENT '类别',
        activity_name VARCHAR(100) NOT NULL COMMENT '活动名称',
        emission_source VARCHAR(50) COMMENT '排放源',
        data_source VARCHAR(20) DEFAULT 'INPUT' COMMENT '数据来源：INPUT录入/CALC计算',
        operator VARCHAR(10) DEFAULT 'ADD' COMMENT '运算符：ADD/SUB',
        related_metric VARCHAR(50) COMMENT '关联指标',
        activity_level DECIMAL(18,4) COMMENT '活动水平（数量）',
        unit VARCHAR(20) COMMENT '计量单位',
        factor_id BIGINT COMMENT '排放因子ID',
        emission DECIMAL(18,4) COMMENT '排放量（tCO2e）',
        create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
        INDEX idx_year_unit (year, unit_id),
        INDEX idx_scope (scope_type)
    ) COMMENT='碳排放活动表';
    
    -- 排放因子表
    CREATE TABLE emission_factor (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        factor_name VARCHAR(100) NOT NULL COMMENT '因子名称',
        category VARCHAR(50) COMMENT '分类：电力/天然气/运输等',
        standard VARCHAR(100) COMMENT '引用标准/文件',
        factor_value DECIMAL(18,6) NOT NULL COMMENT '排放因子值',
        factor_unit VARCHAR(50) COMMENT '因子单位',
        region VARCHAR(50) COMMENT '适用地区',
        year INT COMMENT '因子年度',
        is_default TINYINT DEFAULT 0 COMMENT '是否默认：0否 1是',
        remark VARCHAR(500) COMMENT '备注',
        INDEX idx_category (category),
        INDEX idx_default (is_default)
    ) COMMENT='排放因子表';
    
    -- 碳核算模型表
    CREATE TABLE carbon_model (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        model_name VARCHAR(100) NOT NULL COMMENT '模型名称',
        model_type VARCHAR(20) COMMENT '类型：ACCOUNTING/FOOTPRINT',
        scope_type VARCHAR(20) COMMENT '范围类型',
        status TINYINT DEFAULT 1,
        create_time DATETIME DEFAULT CURRENT_TIMESTAMP
    ) COMMENT='碳核算模型表';
    
    -- 模型活动配置表
    CREATE TABLE carbon_model_activity (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        model_id BIGINT NOT NULL,
        activity_name VARCHAR(100) NOT NULL,
        emission_source VARCHAR(50),
        unit VARCHAR(20),
        energy_type VARCHAR(20),
        factor_name VARCHAR(100),
        factor_unit VARCHAR(50),
        factor_value DECIMAL(18,6),
        sort_order INT DEFAULT 0
    ) COMMENT='模型活动配置表';
    

#### 5.3.4 报警相关表
    
    
    -- 报警配置表
    CREATE TABLE alarm_config (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        unit_id BIGINT NOT NULL COMMENT '用能单元',
        metric_name VARCHAR(50) NOT NULL COMMENT '指标名称',
        metric_unit VARCHAR(20) COMMENT '指标单位',
        upper_limit_day DECIMAL(18,4) COMMENT '日报警上限',
        lower_limit_day DECIMAL(18,4) COMMENT '日报警下限',
        upper_limit_month DECIMAL(18,4) COMMENT '月报警上限',
        lower_limit_month DECIMAL(18,4) COMMENT '月报警下限',
        upper_limit_year DECIMAL(18,4) COMMENT '年报警上限',
        lower_limit_year DECIMAL(18,4) COMMENT '年报警下限',
        status TINYINT DEFAULT 1,
        INDEX idx_unit_metric (unit_id, metric_name)
    ) COMMENT='报警配置表';
    
    -- 报警记录表
    CREATE TABLE alarm_record (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        config_id BIGINT COMMENT '关联配置ID',
        alarm_time DATETIME NOT NULL COMMENT '报警时间',
        alarm_type VARCHAR(20) COMMENT '类型：UPPER_EXCEED/LOWER_EXCEED',
        alarm_unit VARCHAR(100) COMMENT '报警单元',
        alarm_metric VARCHAR(50) COMMENT '报警指标',
        alarm_info VARCHAR(200) COMMENT '报警信息',
        alarm_value DECIMAL(18,4) COMMENT '当前值',
        limit_value DECIMAL(18,4) COMMENT '阈值',
        alarm_status VARCHAR(20) DEFAULT 'UNHANDLED' COMMENT '状态：UNHANDLED/ HANDLED',
        handler VARCHAR(50) COMMENT '处理人',
        handle_method VARCHAR(500) COMMENT '处理方法',
        handle_time DATETIME COMMENT '处理时间',
        INDEX idx_time (alarm_time),
        INDEX idx_status (alarm_status)
    ) COMMENT='报警记录表';
    

#### 5.3.5 系统管理扩展表

```sql
-- 操作日志记录表
CREATE TABLE sys_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    title VARCHAR(255) DEFAULT '' COMMENT '操作模块标题',
    business_type INT DEFAULT 0 COMMENT '业务类型（0其他 1新增 2修改 3删除 4查询 5导出）',
    method VARCHAR(500) DEFAULT '' COMMENT '请求方法',
    request_method VARCHAR(10) DEFAULT '' COMMENT '请求方式（GET/POST/PUT/DELETE）',
    oper_name VARCHAR(50) DEFAULT '' COMMENT '操作人员',
    oper_url VARCHAR(500) DEFAULT '' COMMENT '请求URL',
    oper_ip VARCHAR(128) DEFAULT '' COMMENT '操作IP地址',
    oper_location VARCHAR(255) DEFAULT '' COMMENT '操作地点',
    oper_param TEXT COMMENT '请求参数',
    json_result TEXT COMMENT '返回结果',
    status INT DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
    error_msg TEXT COMMENT '错误消息',
    oper_time DATETIME DEFAULT NULL COMMENT '操作时间',
    cost_time BIGINT DEFAULT 0 COMMENT '消耗时间（毫秒）',
    PRIMARY KEY (id),
    KEY idx_oper_time (oper_time),
    KEY idx_oper_name (oper_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志记录表';

-- 通知公告表
CREATE TABLE sys_notice (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    notice_title VARCHAR(255) NOT NULL COMMENT '公告标题',
    notice_type INT DEFAULT 1 COMMENT '公告类型（1通知 2公告）',
    notice_content TEXT COMMENT '公告内容（富文本HTML）',
    status INT DEFAULT 1 COMMENT '状态（0停用 1启用）',
    create_by BIGINT DEFAULT NULL COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT DEFAULT NULL COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT '' COMMENT '备注',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告表';
```

#### 5.3.6 通知下发与用户消息扩展表

```sql
-- 通知公告下发记录表
CREATE TABLE sys_notice_dispatch (
    id BIGINT NOT NULL AUTO_INCREMENT,
    notice_id BIGINT NOT NULL,
    dispatch_type INT DEFAULT 1 COMMENT '下发类型(1全部 2机构 3角色 4用户)',
    target_name VARCHAR(500) DEFAULT '' COMMENT '下发目标名称',
    create_by BIGINT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_notice_id (notice_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告下发记录表';

-- 用户消息表（个人信箱）
CREATE TABLE sys_user_message (
    id BIGINT NOT NULL AUTO_INCREMENT,
    notice_id BIGINT DEFAULT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) DEFAULT '',
    content TEXT,
    notice_type INT DEFAULT 1,
    read_status INT DEFAULT 0 COMMENT '0未读 1已读',
    read_time DATETIME DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_read_status (read_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户消息表';
```

**下发流程**：
1. 管理员在通知公告管理页点击"下发"，选择下发范围（全部/机构/角色/用户）
2. 后端根据下发类型解析目标用户集合（机构→查 `sys_user.dept_id`；角色→查 `sys_user_role`）
3. 为每个目标用户插入一条 `sys_user_message` 记录（冗余 title/content/noticeType）
4. 同时插入一条 `sys_notice_dispatch` 下发记录
5. 用户在"我的消息"页面或右上角铃铛处看到未读消息，点击查看时自动标记已读

#### 5.3.7 InfluxDB时序数据设计
    
    
    -- InfluxDB Measurement设计
    -- 实时数据采集表
    CREATE MEASUREMENT device_data (
        time TIMESTAMP,
        device_id TAG,
        metric_name TAG,       -- 如：a_phase_current, b_phase_voltage
        metric_value FIELD,    -- 采集值
        calculated_value FIELD -- 计算值
    );
    
    -- 能耗数据表（按小时聚合）
    CREATE MEASUREMENT energy_consumption (
        time TIMESTAMP,
        unit_id TAG,
        energy_type TAG,       -- 电力/天然气/蒸汽等
        consumption FIELD,     -- 消耗量
        cost FIELD,            -- 成本
        carbon_emission FIELD  -- 碳排放
    );
    
    -- 示例查询
    SELECT MEAN(metric_value) FROM device_data
    WHERE device_id = 'DEV001' AND metric_name = 'a_phase_current'
    AND time >= '2026-06-01T00:00:00Z' AND time <= '2026-06-30T23:59:59Z'
    GROUP BY time(1h);
    

## 6\. API接口设计

### 6.1 接口规范

规范项| 说明  
---|---  
协议| HTTPS  
数据格式| JSON  
字符编码| UTF-8  
URL前缀| /api/{module}  
版本控制| URL路径中体现，如 /api/v1/monitor/realtime  
请求方式| GET查询、POST创建、PUT更新、DELETE删除  
响应格式| {"code": 200, "message": "ok", "data": {}}  
  
#### 统一响应结构
    
    
    // 成功响应
    {
      "code": 200,
      "message": "操作成功",
      "data": {
        "total": 100,
        "list": [...]
      }
    }
    
    // 错误响应
    {
      "code": 400,
      "message": "参数校验失败：用户名不能为空",
      "data": null
    }
    
    // 分页响应
    {
      "code": 200,
      "message": "ok",
      "data": {
        "current": 1,
        "size": 20,
        "total": 100,
        "pages": 5,
        "records": [...]
      }
    }
    

### 6.2 认证授权机制

#### 请求头规范
    
    
    Authorization: Bearer {accessToken}
    Content-Type: application/json
    X-Request-Id: {UUID}  // 请求追踪ID
    

#### Token刷新机制
    
    
    POST /api/auth/refresh
    Content-Type: application/json
    
    {
      "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
    }
    
    // 响应
    {
      "code": 200,
      "data": {
        "accessToken": "eyJhbGciOiJIUzI1NiIs...",
        "expiresIn": 7200  // 2小时
      }
    }
    

### 6.3 核心接口清单

#### 认证模块

接口| 方法| 说明  
---|---|---  
/api/auth/login| POST| 用户登录  
/api/auth/logout| POST| 用户登出  
/api/auth/refresh| POST| 刷新Token  
/api/auth/captcha| GET| 获取验证码  
/api/auth/userinfo| GET| 获取当前用户信息  
  
#### 在线监测模块

接口| 方法| 说明  
---|---|---  
/api/monitor/realtime/list| GET| 实时数据列表  
/api/monitor/history/query| GET| 历史数据查询  
/api/monitor/compare/device| GET| 设备对标  
/api/monitor/compare/time| GET| 时间对标  
/api/monitor/meter/reading| GET| 计量抄表记录  
/api/monitor/status/gateway| GET| 网关状态  
/api/monitor/status/meter| GET| 计量器具状态  
  
#### 碳排放管理模块

接口| 方法| 说明  
---|---|---  
/api/carbon/accounting/summary| GET| 碳排放汇总  
/api/carbon/accounting/activities| GET| 排放活动列表  
/api/carbon/accounting/activity| POST| 新增排放活动  
/api/carbon/accounting/activity/{id}| PUT| 修改排放活动  
/api/carbon/accounting/report| GET| 生成碳排放报告  
/api/carbon/footprint/models| GET| 碳足迹模型列表  
/api/carbon/factor/list| GET| 排放因子列表  
/api/carbon/asset/summary| GET| 碳资产概览  
/api/carbon/cbam/list| GET| CBAM核算列表  
  
#### 报警管理模块

接口| 方法| 说明  
---|---|---  
/api/alarm/config/list| GET| 报警配置列表  
/api/alarm/config/{id}| PUT| 修改报警阈值  
/api/alarm/record/list| GET| 报警记录查询  
/api/alarm/record/{id}/handle| PUT| 处理报警  
  
#### 系统管理模块

接口| 方法| 说明  
---|---|---  
/api/system/user/list| GET| 用户列表  
/api/system/user| POST| 新增用户  
/api/system/role/list| GET| 角色列表  
/api/system/role| POST| 新增角色  
/api/system/menu/tree| GET| 菜单树  
/api/system/unit/tree| GET| 用能单元树  
/api/system/device/list| GET| 设备列表  
/api/system/meter/list| GET| 计量器具列表  
/api/system/log/list| GET| 操作日志列表  
/api/system/notice/list| GET| 通知公告列表（分页）
/api/system/notice/{id}| GET| 公告详情
/api/system/notice| POST| 发布公告
/api/system/notice/{id}| PUT| 更新公告
/api/system/notice/{id}| DELETE| 删除公告
/api/system/notice/{id}/dispatch| POST| 下发通知公告
/api/system/notice/dispatch/list| GET| 下发记录列表（分页）

#### 我的消息模块

接口| 方法| 说明
---|---|---
/api/system/message/list| GET| 当前用户消息列表（分页，支持readStatus过滤）
/api/system/message/unread/count| GET| 当前用户未读消息数量
/api/system/message/{id}| GET| 消息详情
/api/system/message/{id}/read| POST| 标记消息已读
  
## 7\. 部署架构

### 7.1 部署拓扑
    
    
    flowchart TB
        subgraph User["用户访问层"]
            Browser["PC浏览器"]
            MobileH5["移动端H5"]
        end
    
        subgraph Edge["边缘层"]
            CDN["CDN静态加速"]
            WAF["WAF防护"]
        end
    
        subgraph App["应用层"]
            Nginx1["Nginx-1  
    负载均衡"]
            Nginx2["Nginx-2  
    负载均衡"]
        end
    
        subgraph Service["服务层"]
            App1["App-1  
    Spring Boot"]
            App2["App-2  
    Spring Boot"]
            App3["App-3  
    Spring Boot"]
        end
    
        subgraph DataLayer["数据层"]
            MySQLMaster[("MySQL  
    主库")]
            MySQLSlave[("MySQL  
    从库")]
            RedisCluster[("Redis  
    集群")]
            InfluxDB[("InfluxDB")]
            MinIO[("MinIO  
    对象存储")]
        end
    
        Browser --> CDN
        MobileH5 --> CDN
        CDN --> WAF
        WAF --> Nginx1
        WAF --> Nginx2
        Nginx1 --> App1
        Nginx1 --> App2
        Nginx2 --> App2
        Nginx2 --> App3
        App1 --> MySQLMaster
        App2 --> MySQLMaster
        App3 --> MySQLMaster
        MySQLMaster --> MySQLSlave
        App1 --> RedisCluster
        App2 --> RedisCluster
        App3 --> RedisCluster
        App1 --> InfluxDB
        App2 --> InfluxDB
        App1 --> MinIO
        App3 --> MinIO
      

图5：生产环境部署拓扑

### 7.2 服务器资源配置

服务| 配置| 数量| 说明  
---|---|---|---  
Nginx| 4核8G| 2| 负载均衡+静态资源  
应用服务| 8核16G| 3| Spring Boot服务  
MySQL| 8核32G| 2| 主从复制  
Redis| 4核16G| 3| 主从+哨兵  
InfluxDB| 8核32G| 1| 时序数据存储  
MinIO| 4核16G| 2| 对象存储集群  
  
### 7.3 Docker部署配置
    
    
    # docker-compose.yml 示例
    version: '3.8'
    services:
      app:
        image: energy-carbon-platform:latest
        ports:
          - "8080:8080"
        environment:
          - SPRING_PROFILES_ACTIVE=prod
          - MYSQL_HOST=mysql
          - REDIS_HOST=redis
          - INFLUXDB_HOST=influxdb
        depends_on:
          - mysql
          - redis
          - influxdb
        deploy:
          replicas: 3
    
      mysql:
        image: mysql:5.7
        environment:
          - MYSQL_ROOT_PASSWORD=xxxx
          - MYSQL_DATABASE=energy_carbon
        volumes:
          - mysql_data:/var/lib/mysql
        ports:
          - "3306:3306"
    
      redis:
        image: redis:5-alpine
        ports:
          - "6379:6379"
        volumes:
          - redis_data:/data
    
      influxdb:
        image: influxdb:1.8
        ports:
          - "8086:8086"
        volumes:
          - influxdb_data:/var/lib/influxdb
    
      minio:
        image: minio/minio:latest
        command: server /data --console-address ":9001"
        ports:
          - "9000:9000"
          - "9001:9001"
        volumes:
          - minio_data:/data
    
    volumes:
      mysql_data:
      redis_data:
      influxdb_data:
      minio_data:
    

## 8\. 开发规范

### 8.1 代码规范

  * **Java** ：遵循阿里巴巴Java开发手册，使用IDEA Alibaba插件自动检查。
  * **Vue** ：遵循Vue官方风格指南，组件名大驼峰，组合式函数以use开头。
  * **命名** ：数据库表名小写下划线（sys_user），类名大驼峰（SysUser），方法名小驼峰（getUserById）。
  * **注释** ：类和方法必须添加JavaDoc注释，复杂逻辑添加行内注释。



### 8.2 Git分支规范
    
    
    main        # 生产分支，只能合并，不能直接提交
    develop     # 开发分支，功能开发完成后合并
    develop
    feature/xxx # 功能分支，从develop检出
    bugfix/xxx  # 缺陷修复分支
    release/v1.x # 发布分支
    

### 8.3 接口版本规范
    
    
    /api/v1/monitor/realtime/list    # 第一版接口
    /api/v2/monitor/realtime/list    # 第二版接口（不兼容变更时升级）
    

### 8.4 日志规范
    
    
    // 使用SLF4J + Logback
    private static final Logger log = LoggerFactory.getLogger(XxxService.class);
    
    // 规范示例
    log.info("用户登录成功, userId={}, ip={}", userId, ip);
    log.error("查询碳排放数据失败, year={}, unitId={}", year, unitId, e);
    

**文档说明**

本文档为世通AI+能碳管理平台的技术设计文档，基于Vue 3 + Spring Boot 2.x + JDK 1.8技术栈。文档涵盖系统架构、前后端设计、数据库模型、API接口及部署方案，可作为开发团队的实施依据。实际开发中可根据需求变化进行迭代调整。
