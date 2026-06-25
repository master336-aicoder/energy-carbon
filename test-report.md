# 能碳数智化平台 - Web功能测试报告

全面功能测试 | 覆盖全部33个页面

**测试时间:** 2026-06-25 00:27:47 ~ 2026-06-25 00:29:29

**前端地址:** http://localhost:3000

**后端地址:** http://localhost:8080

**登录状态:** 成功

**测试账号:** admin / ******

33

总测试页面

33

通过

0

失败

100.0%

通过率

748ms

平均加载时间

0

高严重度问题

32

中严重度问题

1

低严重度问题

## 模块测试汇总

模块名称 | 页面总数 | 通过数 | 失败数 | 通过率  
---|---|---|---|---  
首页 | 1 | 1 | 0 | 100.0%  
实时监测 | 6 | 6 | 0 | 100.0%  
告警管理 | 2 | 2 | 0 | 100.0%  
能耗分析 | 3 | 3 | 0 | 100.0%  
碳管理 | 6 | 6 | 0 | 100.0%  
报表中心 | 4 | 4 | 0 | 100.0%  
能效对标 | 2 | 2 | 0 | 100.0%  
双碳管理 | 1 | 1 | 0 | 100.0%  
工具箱 | 3 | 3 | 0 | 100.0%  
系统管理 | 5 | 5 | 0 | 100.0%  
  
## 详细测试结果

模块 | 页面名称 | 路由 | 加载状态 | 加载时间 | 按钮 | 表格 | 输入框 | 图表 | JS错误 | 网络错误 | 问题描述  
---|---|---|---|---|---|---|---|---|---|---|---  
首页 | 首页Dashboard | `/dashboard` | 通过 | 781ms | 1 | 3 | 3 | 3 | 0 | 0 | Element Plus弃用警告(3条)  
实时监测 | 实时数据 | `/monitor/realtime` | 通过 | 803ms | 1 | 0 | 8 | 4 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/monitor/realtime"  
实时监测 | 历史查询 | `/monitor/history` | 通过 | 823ms | 17 | 5 | 20 | 2 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/monitor/history"  
实时监测 | 抄表录入 | `/monitor/meter-reading` | 通过 | 690ms | 18 | 5 | 18 | 0 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/monitor/meter-reading  
实时监测 | 运行状态 | `/monitor/status` | 通过 | 645ms | 2 | 3 | 8 | 0 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/monitor/status"  
实时监测 | 同比环比 | `/monitor/time-compare` | 通过 | 797ms | 26 | 4 | 28 | 2 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/monitor/time-compare"  
实时监测 | 设备对比 | `/monitor/device-compare` | 通过 | 819ms | 14 | 3 | 18 | 2 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/monitor/device-compar  
告警管理 | 告警配置 | `/alarm/config` | 通过 | 712ms | 25 | 3 | 22 | 0 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/alarm/config"  
告警管理 | 告警记录 | `/alarm/record` | 通过 | 686ms | 17 | 5 | 21 | 0 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/alarm/record"  
能耗分析 | 能耗统计 | `/analysis/consumption` | 通过 | 839ms | 9 | 5 | 8 | 3 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/analysis/consumption"  
能耗分析 | 用电分析 | `/analysis/electricity` | 通过 | 874ms | 7 | 5 | 4 | 1 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/analysis/electricity"  
能耗分析 | 预算管理 | `/analysis/budget` | 通过 | 636ms | 8 | 4 | 4 | 0 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/analysis/budget"  
碳管理 | 碳核算 | `/carbon/accounting` | 通过 | 856ms | 9 | 4 | 12 | 0 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/carbon/accounting"  
碳管理 | 排放因子 | `/carbon/factor` | 通过 | 683ms | 37 | 4 | 10 | 0 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/carbon/factor"  
碳管理 | 碳足迹 | `/carbon/footprint` | 通过 | 627ms | 5 | 3 | 6 | 0 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/carbon/footprint"  
碳管理 | 碳资产 | `/carbon/asset` | 通过 | 637ms | 3 | 3 | 6 | 0 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/carbon/asset"  
碳管理 | CBAM | `/carbon/cbam` | 通过 | 668ms | 9 | 8 | 6 | 0 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/carbon/cbam"  
碳管理 | 供应链碳 | `/carbon/supply-chain` | 通过 | 657ms | 8 | 3 | 26 | 0 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/carbon/supply-chain"  
报表中心 | 能耗报表 | `/report/energy` | 通过 | 889ms | 15 | 5 | 8 | 0 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/report/energy"  
报表中心 | 成本报表 | `/report/cost` | 通过 | 890ms | 14 | 5 | 10 | 0 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/report/cost"  
报表中心 | 节能量报表 | `/report/saving` | 通过 | 979ms | 14 | 5 | 8 | 0 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/report/saving"  
报表中心 | 汇总报表 | `/report/summary` | 通过 | 900ms | 14 | 5 | 8 | 0 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/report/summary"  
能效对标 | 能效对标 | `/efficiency` | 通过 | 908ms | 6 | 5 | 6 | 3 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/efficiency"  
能效对标 | 能源流向 | `/efficiency/energy-flow` | 通过 | 1001ms | 7 | 5 | 4 | 2 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/efficiency/energy-flo  
双碳管理 | AI智慧管理平台 | `/dual-carbon` | 通过 | 644ms | 3 | 3 | 6 | 0 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/dual-carbon"  
工具箱 | AI报告生成 | `/toolkit/ai-report` | 通过 | 666ms | 3 | 3 | 12 | 0 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/toolkit/ai-report"  
工具箱 | 分时电价 | `/toolkit/electricity-price` | 通过 | 635ms | 3 | 3 | 2 | 0 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/toolkit/electricity-p  
工具箱 | AI知识库 | `/toolkit/knowledge` | 通过 | 643ms | 4 | 3 | 7 | 0 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/toolkit/knowledge"  
系统管理 | 设备管理 | `/system/device` | 通过 | 674ms | 7 | 3 | 14 | 0 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/system/device"  
系统管理 | 计量器具 | `/system/meter` | 通过 | 681ms | 7 | 3 | 16 | 0 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/system/meter"  
系统管理 | 用能单元 | `/system/unit` | 通过 | 628ms | 24 | 3 | 0 | 0 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/system/unit"  
系统管理 | 角色管理 | `/system/role` | 通过 | 673ms | 20 | 3 | 13 | 0 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/system/role"  
系统管理 | 用户管理 | `/system/user` | 通过 | 647ms | 9 | 3 | 9 | 0 | 0 | 0 | Vue Router警告: [Vue Router warn]: No match found for location with path "/system/user"  
  
## 问题列表（按严重程度排序）

共发现 **33** 个问题： 高 0 | 中 32 | 低 1

# | 页面 | 路由 | 问题描述 | 严重程度  
---|---|---|---|---  
1 | 实时监测 - 实时数据 | `/monitor/realtime` | Vue Router警告: [Vue Router warn]: No match found for location with path "/monitor/realtime" | 中  
2 | 实时监测 - 历史查询 | `/monitor/history` | Vue Router警告: [Vue Router warn]: No match found for location with path "/monitor/history" | 中  
3 | 实时监测 - 抄表录入 | `/monitor/meter-reading` | Vue Router警告: [Vue Router warn]: No match found for location with path "/monitor/meter-reading | 中  
4 | 实时监测 - 运行状态 | `/monitor/status` | Vue Router警告: [Vue Router warn]: No match found for location with path "/monitor/status" | 中  
5 | 实时监测 - 同比环比 | `/monitor/time-compare` | Vue Router警告: [Vue Router warn]: No match found for location with path "/monitor/time-compare" | 中  
6 | 实时监测 - 设备对比 | `/monitor/device-compare` | Vue Router警告: [Vue Router warn]: No match found for location with path "/monitor/device-compar | 中  
7 | 告警管理 - 告警配置 | `/alarm/config` | Vue Router警告: [Vue Router warn]: No match found for location with path "/alarm/config" | 中  
8 | 告警管理 - 告警记录 | `/alarm/record` | Vue Router警告: [Vue Router warn]: No match found for location with path "/alarm/record" | 中  
9 | 能耗分析 - 能耗统计 | `/analysis/consumption` | Vue Router警告: [Vue Router warn]: No match found for location with path "/analysis/consumption" | 中  
10 | 能耗分析 - 用电分析 | `/analysis/electricity` | Vue Router警告: [Vue Router warn]: No match found for location with path "/analysis/electricity" | 中  
11 | 能耗分析 - 预算管理 | `/analysis/budget` | Vue Router警告: [Vue Router warn]: No match found for location with path "/analysis/budget" | 中  
12 | 碳管理 - 碳核算 | `/carbon/accounting` | Vue Router警告: [Vue Router warn]: No match found for location with path "/carbon/accounting" | 中  
13 | 碳管理 - 排放因子 | `/carbon/factor` | Vue Router警告: [Vue Router warn]: No match found for location with path "/carbon/factor" | 中  
14 | 碳管理 - 碳足迹 | `/carbon/footprint` | Vue Router警告: [Vue Router warn]: No match found for location with path "/carbon/footprint" | 中  
15 | 碳管理 - 碳资产 | `/carbon/asset` | Vue Router警告: [Vue Router warn]: No match found for location with path "/carbon/asset" | 中  
16 | 碳管理 - CBAM | `/carbon/cbam` | Vue Router警告: [Vue Router warn]: No match found for location with path "/carbon/cbam" | 中  
17 | 碳管理 - 供应链碳 | `/carbon/supply-chain` | Vue Router警告: [Vue Router warn]: No match found for location with path "/carbon/supply-chain" | 中  
18 | 报表中心 - 能耗报表 | `/report/energy` | Vue Router警告: [Vue Router warn]: No match found for location with path "/report/energy" | 中  
19 | 报表中心 - 成本报表 | `/report/cost` | Vue Router警告: [Vue Router warn]: No match found for location with path "/report/cost" | 中  
20 | 报表中心 - 节能量报表 | `/report/saving` | Vue Router警告: [Vue Router warn]: No match found for location with path "/report/saving" | 中  
21 | 报表中心 - 汇总报表 | `/report/summary` | Vue Router警告: [Vue Router warn]: No match found for location with path "/report/summary" | 中  
22 | 能效对标 - 能效对标 | `/efficiency` | Vue Router警告: [Vue Router warn]: No match found for location with path "/efficiency" | 中  
23 | 能效对标 - 能源流向 | `/efficiency/energy-flow` | Vue Router警告: [Vue Router warn]: No match found for location with path "/efficiency/energy-flo | 中  
24 | 双碳管理 - AI智慧管理平台 | `/dual-carbon` | Vue Router警告: [Vue Router warn]: No match found for location with path "/dual-carbon" | 中  
25 | 工具箱 - AI报告生成 | `/toolkit/ai-report` | Vue Router警告: [Vue Router warn]: No match found for location with path "/toolkit/ai-report" | 中  
26 | 工具箱 - 分时电价 | `/toolkit/electricity-price` | Vue Router警告: [Vue Router warn]: No match found for location with path "/toolkit/electricity-p | 中  
27 | 工具箱 - AI知识库 | `/toolkit/knowledge` | Vue Router警告: [Vue Router warn]: No match found for location with path "/toolkit/knowledge" | 中  
28 | 系统管理 - 设备管理 | `/system/device` | Vue Router警告: [Vue Router warn]: No match found for location with path "/system/device" | 中  
29 | 系统管理 - 计量器具 | `/system/meter` | Vue Router警告: [Vue Router warn]: No match found for location with path "/system/meter" | 中  
30 | 系统管理 - 用能单元 | `/system/unit` | Vue Router警告: [Vue Router warn]: No match found for location with path "/system/unit" | 中  
31 | 系统管理 - 角色管理 | `/system/role` | Vue Router警告: [Vue Router warn]: No match found for location with path "/system/role" | 中  
32 | 系统管理 - 用户管理 | `/system/user` | Vue Router警告: [Vue Router warn]: No match found for location with path "/system/user" | 中  
33 | 首页 - 首页Dashboard | `/dashboard` | Element Plus弃用警告(3条) | 低  
  
## 测试结论与建议

### 测试结论

本次测试对能碳数智化平台的 **33** 个页面进行了全面的功能测试，涵盖首页、实时监测、告警管理、能耗分析、碳管理、报表中心、能效对标、双碳管理、工具箱和系统管理共 **10** 个模块。

  * 测试通过率：**100.0%** （33/33）
  * 平均页面加载时间：**748ms**
  * 发现高严重度问题：**0** 个
  * 发现中严重度问题：**32** 个（均为Vue Router动态路由注册时序警告）
  * 发现低严重度问题：**1** 个（Element Plus组件API弃用警告）



### 问题分析

**Vue Router "No match found" 警告（影响全部32个动态路由页面）**

该警告是由于前端采用动态路由注册机制（`router.addRoute`）导致的。当浏览器直接导航到动态路由页面时，路由尚未注册完成，Vue Router会先触发"No match found"警告，随后在路由守卫中完成动态路由注册并通过`next({ ...to, replace: true })`重新导航。这是Vue Router动态路由的已知行为模式，不影响页面正常加载和功能使用。建议在路由守卫中增加路由预注册逻辑，避免该警告的产生。

**Element Plus 弃用警告（影响首页Dashboard）**

首页使用了Element Plus的`el-radio`组件，该组件的`label`属性作为值使用的API将在Element Plus 3.0.0版本中被弃用，建议迁移为使用`value`属性替代。

### 建议

  * **路由优化** ：建议在应用初始化时预注册所有动态路由，或在路由守卫中先检查路由是否已注册再执行导航，避免"No match found"警告
  * **组件升级** ：建议将`el-radio`组件的`label`属性迁移为`value`属性，以兼容Element Plus 3.0版本
  * **MQTT服务** ：建议对MQTT服务进行单独测试，确保实时数据推送功能正常（本次测试未覆盖MQTT实时数据）
  * **交互测试** ：建议增加更多的端到端测试用例，覆盖表单提交、数据编辑、删除等交互操作
  * **权限测试** ：建议使用不同角色的账号进行权限测试，验证各模块的访问控制是否正确
  * **性能优化** ：报表中心模块的加载时间略高于平均水平（约900ms），建议关注数据加载优化



能碳数智化平台 Web功能测试报告 | 自动生成于 2026-06-25 00:29:29
