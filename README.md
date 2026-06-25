# 世通AI+能碳管理平台

## 项目简介

在国家"双碳"战略与绿色制造体系推动下，企业亟需数字化工具实现能源与碳排放的精准管理。**世通AI+能碳管理平台**面向制造型企业及园区，提供从数据采集、监控、分析到碳核算、合规申报的全链路解决方案，是企业绿色低碳工作的技术底座。

### 核心价值
- **全链路覆盖**：覆盖能源管理、碳排放管理、产品碳足迹、绿色/零碳工厂申报、CBAM准备、ESG报告、碳资产管理等场景
- **AI赋能**：集成大模型能力，支持智能报告生成、知识库问答、错峰排产优化
- **合规支撑**：依据《碳达峰碳中和综合评价考核办法》《绿色工厂评价通则》等标准设计
- **灵活部署**：支持SaaS公有云部署或本地私有化部署

### 目标用户
- 能源管理员、碳管理专员
- 绿色工厂/零碳工厂申报负责人
- 企业高管（决策支持）
- 园区管理方、第三方核查机构

---

## 技术架构

### 前后端技术栈

| 层级 | 技术选型 | 版本 |
|------|----------|------|
| 前端框架 | Vue.js | 3.x |
| 前端构建 | Vite | 8.x |
| UI组件库 | Element Plus | 2.14.x |
| 状态管理 | Pinia | 3.x |
| 图表库 | ECharts | 6.x |
| 后端框架 | Spring Boot | 2.7.18 |
| ORM框架 | MyBatis Plus | 3.5.5 |
| 安全认证 | Spring Security + JWT | 0.11.5 |
| 数据库 | MySQL | 8.0.x |
| 缓存 | Redis | 6.0+ |
| 时序数据库 | InfluxDB | 1.8+ |
| 消息传输 | MQTT (EMQX 5.9.2) | 3.1.1/5.0 |
| 接口文档 | Knife4j (OpenAPI 3) | 4.4.0 |

#### 移动端技术栈

| 层级 | 技术选型 | 版本 | 说明 |
|------|----------|------|------|
| 跨端框架 | UniApp | 4.x | 基于Vue3语法，一套代码输出多端（H5/小程序/App） |
| 前端框架 | Vue.js | 3.x | 组合式API（Composition API） |
| 构建工具 | Vite | 5.x | 快速构建与热更新 |
| UI组件库 | uni-ui | 最新 | UniApp官方组件库 |
| 状态管理 | Pinia | 2.x | Vue3官方推荐状态管理方案 |
| 图表库 | uCharts | 2.x | 跨端高性能图表，适配ECharts数据格式 |
| 扫码能力 | uni.scanCode API | — | 调用原生扫码能力 |
| 本地存储 | uni.storage + SQLite | — | Token缓存、离线数据存储 |

### 系统架构图（文字描述）

```
┌────────────────────────────────────────────────────────────────────┐
│                           用户层                                    │
│  PC浏览器(Web)  │  移动端App/小程序/H5  │  大屏展示               │
└──────────────────────┬───────────────────────────────────────────┘
                       │
       ┌───────────────┴───────────────┐
       │                               │
┌──────▼──────────────┐    ┌───────────▼──────────────────────────┐
│      PC前端层        │    │            移动端前端层               │
│  Vue3 + Vite +      │    │  UniApp + Vue3 + uView + uCharts    │
│  Element Plus +     │    │  + Pinia + uni.request              │
│  Pinia + ECharts    │    │                                       │
└──────┬──────────────┘    └───────────┬──────────────────────────┘
       │                               │
       └───────────────┬───────────────┘
                       │ RESTful API / WebSocket
┌──────────────────────▼───────────────────────────────────────────┐
│                       网关/代理层                            │
│  Nginx (反向代理、静态资源、负载均衡)                        │
└────────────────────┬────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────┐
│                       后端服务层                             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │  system     │ │  monitor    │ │  alarm      │           │
│  │  系统管理   │ │  在线监测   │ │  报警管理   │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │  carbon     │ │  report     │ │  common     │           │
│  │  碳排放管理 │ │  统计报表   │ │  公共模块   │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
│  Spring Boot + MyBatis Plus + Spring Security + JWT              │
└──────────────────────┬───────────────────────────────────────────┘
                       │
┌──────────────────────▼───────────────────────────────────────────┐
│                       数据层                                       │
│  MySQL (业务数据)  │  Redis (缓存/会话)  │  InfluxDB(时序)        │
└──────────────────────────────────────────────────────────────────┘
                       │
┌──────────────────────▼───────────────────────────────────────────┐
│                       数据采集层                                   │
│  物联网网关 (Modbus / OPC UA / MQTT / HTTP)                        │
│  MQTT Broker - EMQX 5.9.2 (设备实时数据接入，可选)                     │
└──────────────────────────────────────────────────────────────────┘
```

---

## 功能模块

平台包含14个一级功能模块，约40个功能页面：

| 序号 | 模块名称 | 功能说明 | 实现状态 |
|------|----------|----------|----------|
| 1 | **首页仪表盘** | 核心KPI卡片、能碳趋势图、用能统计、碳排放分布、设备状态、碳资产市场供需 | 开发中 |
| 2 | **双碳考核** | 目标建立、层层分解、过程管控、考核评价 | 开发中 |
| 3 | **在线监测** | 实时数据、历史数据查询、设备对标、时间对标、计量抄表、状态监测 | 开发中 |
| 4 | **报警管理** | 报警阈值设置（按用能单元+指标）、报警记录查询与处理 | 开发中 |
| 5 | **用能分析** | 能耗查询（多维度）、电费分析、能效分析、能流分析（桑基图）、能碳预算 | 开发中 |
| 6 | **碳排放管理** | 碳排放核算（范围一/二/三）、排放因子库、产品碳足迹、CBAM核算、供应链管理、碳资产管理、碳核查支撑、碳核算建模、碳足迹建模 | 开发中 |
| 7 | **统计报表** | 能耗统计表（日报/月报/年报）、能耗汇总表、节能量统计表、用能成本统计表 | 开发中 |
| 8 | **数据录入** | 指标录入（手动+批量导入）、产量录入 | 开发中 |
| 9 | **AI智能服务** | AI智慧管理平台（服务预约）、企业级AI知识库、AI报告生成 | 开发中 |
| 10 | **第三方服务** | 外部认证/核查机构服务对接 | 开发中 |
| 11 | **能碳工具包** | 绿色工厂申报助手、AI报告生成、错峰排产小助手、能碳数据计算器、分时电价管理器 | 开发中 |
| 12 | **基础配置** | 网关管理、能管关系、能源品种、能源品种模板、用能设备、计量器具、班组管理、产品种类、对标库 | 开发中 |
| 13 | **系统管理** | 用户管理、角色管理、菜单管理、部门/用能单元管理、操作日志 | 开发中 |
| 14 | **系统安全** | JWT认证、RBAC权限控制、数据权限、操作审计 | 开发中 |

> **状态说明**：本项目采用三阶段开发计划，预计2026年10月下旬完成全部功能开发。

---

## 移动端开发计划

### 概述

平台同步规划了移动端应用，作为PC Web端的有力补充，面向一线能源管理员、巡检人员和碳管理专员，提供随时随地的数据查看、现场数据录入、告警处理和扫码识别能力。

### 技术方案

- **跨端框架**：UniApp + Vue3 + uni-ui，一套代码输出H5、微信小程序、Android App
- **后端复用**：直接复用现有Spring Boot后端API，无需额外后端开发
- **核心功能**：首页仪表盘、数据录入、告警中心、报表查看、个人中心、扫码功能

### 移动端文档

| 文档 | 路径 | 说明 |
|------|------|------|
| 需求方案 | [mobile-prd.md](./public/docs/prd/mobile-prd.md) | 7大模块28项功能设计 |
| 技术设计 | [mobile-tech-design.md](./public/docs/tech-design/mobile-tech-design.md) | 架构设计、安全方案、离线方案 |
| 开发计划 | [mobile-dev-plan.md](./public/docs/dev-plan/mobile-dev-plan.md) | M1-M5共7周里程碑 |
| 接口文档(MD) | [mobile-api.md](./public/docs/mobile-api.md) | 完整API接口文档 |
| 接口文档(HTML) | [mobile-api.html](./public/docs/mobile-api.html) | 交互式API文档页面 |
| 测试计划 | [mobile-test-plan.md](./public/docs/test-plan/mobile-test-plan.md) | 108个测试用例，覆盖全部页面和按钮，含已修复问题回归验证 |
| 测试报告 | [mobile-test-report.md](./public/docs/test-report/mobile-test-report.md) | V1.1测试报告（通过率98.15%） |

### 里程碑（共7周）

| 里程碑 | 周期 | 核心内容 |
|--------|------|----------|
| M1 | 第1周 | 环境搭建、UI设计、接口联调 |
| M2 | 第2-3周 | 首页仪表盘 + 数据录入 + 扫码功能 |
| M3 | 第4-5周 | 告警中心 + 报表查看 |
| M4 | 第6周 | 个人中心 + 离线缓存 + 性能优化 |
| M5 | 第7周 | 测试修复 + 上线部署 |

### 详细文档

详见：[移动端开发计划](./public/docs/dev-plan/mobile-dev-plan.md)

---

## 项目结构

```
能碳数智化/
├── README.md                          # 项目根说明文档
├── 能碳管理平台需求文档.md             # 详细需求文档
├── 用户使用说明.md                     # 最终用户操作手册
├── dev-plan-能碳管理平台/              # 开发计划文档
│   └── dev-plan-能碳管理平台.html
├── prd-能碳管理平台/                   # 产品原型截图
│   └── assets/
├── energy-carbon-web/                 # 前端项目
│   ├── src/
│   │   ├── api/                       # API接口定义
│   │   ├── assets/                    # 静态资源
│   │   ├── layouts/                   # 布局组件
│   │   ├── router/                    # 路由配置
│   │   ├── stores/                    # Pinia状态管理
│   │   ├── types/                     # TypeScript类型定义
│   │   ├── utils/                     # 工具函数
│   │   └── views/                     # 页面视图组件
│   ├── package.json
│   ├── vite.config.ts
│   ├── tsconfig.json
│   └── ...
├── energy-carbon-mobile/              # 移动端项目（UniApp + Vue3）
│   ├── pages/                         # 页面目录
│   │   ├── home/                      # 首页仪表盘
│   │   ├── entry/                     # 数据录入
│   │   ├── alarm/                     # 告警中心
│   │   ├── report/                    # 报表查看
│   │   ├── profile/                   # 个人中心
│   │   ├── scan/                      # 扫码功能
│   │   └── login/                     # 登录页
│   ├── components/                    # 公共组件（kpi-card, nav-bar, chart-line）
│   ├── utils/                         # 工具函数（request, auth）
│   ├── static/                        # 静态资源
│   ├── App.vue                        # 应用入口
│   ├── main.js                        # Vue3入口
│   ├── manifest.json                  # 应用配置
│   ├── pages.json                     # 页面路由 + TabBar配置
│   └── uni.scss                       # 主题变量
└── energy-carbon-platform/            # 后端项目（Maven多模块）
    ├── pom.xml
    ├── energy-carbon-admin/           # 启动模块（聚合所有模块）
    ├── energy-carbon-common/          # 公共模块（工具类、常量、异常）
    ├── energy-carbon-system/          # 系统管理模块（用户、角色、菜单、部门、AI服务）
    ├── energy-carbon-monitor/         # 在线监测模块（实时/历史数据、对标、抄表）
    ├── energy-carbon-alarm/           # 报警管理模块（阈值、报警记录）
    ├── energy-carbon-carbon/          # 碳排放管理模块（核算、足迹、CBAM、资产）
    └── energy-carbon-report/          # 统计报表模块（能耗、节能量、成本报表）
```

---

## 快速开始

### 环境准备

| 环境 | 版本要求 | 说明 |
|------|----------|------|
| Node.js | >= 18.x | 前端构建 |
| Java | 1.8 | 后端运行 |
| Maven | >= 3.6 | 后端构建 |
| MySQL | 8.0.x | 业务数据库 |
| Redis | >= 5.0 | 缓存/会话 |
| InfluxDB | 1.8+ | 时序数据存储 |
| MQTT Broker | EMQX 5.9.2（推荐） | 物联网消息传输（可选） |

### 1. 克隆项目

```bash
git clone <repository-url>
cd 能碳数智化
```

### 2. 初始化数据库

```bash
# 创建数据库
create database energy_carbon character set utf8mb4 collate utf8mb4_unicode_ci;

# 执行初始化脚本（推荐使用统一初始化脚本）
mysql -u root -p energy_carbon < energy-carbon-platform/energy-carbon-admin/src/main/resources/db/init_all.sql

# 或分别执行
mysql -u root -p energy_carbon < energy-carbon-platform/energy-carbon-admin/src/main/resources/db/schema_pure.sql
mysql -u root -p energy_carbon < energy-carbon-platform/energy-carbon-admin/src/main/resources/db/data_pure.sql
```

### 3. 启动后端服务

```bash
cd energy-carbon-platform
mvn clean install
mvn spring-boot:run -pl energy-carbon-admin
```

后端服务默认运行在 `http://localhost:8080`

### 4. 启动前端服务

```bash
cd energy-carbon-web
npm install
npm run dev
```

前端服务默认运行在 `http://localhost:3000`

### 5. 访问系统

打开浏览器访问：`http://localhost:3000`

默认管理员账号：`admin` / `123456`

---

## 开发文档

| 文档 | 路径 |
|------|------|
| 需求文档 | [能碳管理平台需求文档.md](./能碳管理平台需求文档.md) |
| 开发计划 | [dev-plan-能碳管理平台/dev-plan-能碳管理平台.html](./dev-plan-能碳管理平台/dev-plan-能碳管理平台.html) |
| 前端开发说明 | [energy-carbon-web/README.md](./energy-carbon-web/README.md) |
| 后端开发说明 | [energy-carbon-platform/README.md](./energy-carbon-platform/README.md) |
| 用户使用说明 | [用户使用说明.md](./用户使用说明.md) |
| 部署文档 | [部署文档.md](./部署文档.md) |
| 接口文档 | 启动后端后访问：`http://localhost:8080/doc.html` |

---

## 贡献指南

1. **分支管理**：
   - `main`：主分支，仅用于发布稳定版本
   - `develop`：开发分支，日常开发基于此分支
   - `feature/*`：功能分支，每个新功能从 `develop` 切出
   - `bugfix/*`：修复分支，用于修复线上问题

2. **提交规范**：
   - 使用 [Conventional Commits](https://www.conventionalcommits.org/) 规范
   - 示例：`feat(monitor): 添加实时数据WebSocket推送功能`

3. **代码审查**：
   - 所有代码变更需通过 Pull Request 合并
   - 至少一名团队成员审查通过后方可合并

4. **Issue 反馈**：
   - 提交 Issue 时请描述清楚问题现象、复现步骤和环境信息
   - 功能建议请标注 `[Feature Request]` 前缀

---

## 许可证

本项目为源码猿（个人开发者）内部项目，未经授权不得对外传播或用于商业用途。

---

**产品归属**：源码猿（个人开发者）  
**技术支持**：master336 <master336@163.com>  
**文档版本**：V1.3  
**最后更新**：2026-06-25
