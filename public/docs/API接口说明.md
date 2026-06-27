# 世通AI+能碳管理平台 API接口说明

## 文档信息

| 项目 | 内容 |
|------|------|
| 项目名称 | 世通AI+能碳管理平台 |
| 版本号 | v1.2.0 |
| 文档日期 | 2026-06-27 |
| 接口规范 | RESTful API |
| 文档工具 | Knife4j (Swagger增强版) |

---

## 目录

1. [接口统一规范](#一接口统一规范)
2. [认证模块](#二认证模块)
3. [系统管理模块](#三系统管理模块)
4. [监控管理模块](#四监控管理模块)
5. [报警管理模块](#五报警管理模块)
6. [碳排管理模块](#六碳排管理模块)
7. [报表管理模块](#七报表管理模块)
8. [分析模块](#八分析模块)
9. [AI服务模块](#九ai服务模块)
10. [工具箱模块](#十工具箱模块)
11. [通知公告模块](#十一通知公告模块)
12. [错误码说明](#十二错误码说明)

---

## 一、接口统一规范

### 1.1 基础信息

| 项目 | 说明 |
|------|------|
| 协议 | HTTP/HTTPS |
| 数据格式 | JSON |
| 字符编码 | UTF-8 |
| 请求方式 | GET / POST / PUT / DELETE |
| 基础URL | `http://localhost:8080` (开发环境) |
| API文档地址 | `http://localhost:8080/doc.html` |

### 1.2 请求URL格式

```
{baseURL}/{module}/{resource}/{action}
```

示例:
- `GET /system/user/list` - 查询用户列表
- `POST /system/user` - 新增用户
- `PUT /system/user/1` - 修改用户
- `DELETE /system/user/1` - 删除用户

### 1.3 响应格式

所有接口统一返回以下JSON结构:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

**字段说明:**

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 提示信息 |
| data | Object | 业务数据 |

**分页响应格式:**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "current": 1,
    "size": 10,
    "total": 100,
    "pages": 10,
    "records": []
  }
}
```

**分页字段说明:**

| 字段 | 类型 | 说明 |
|------|------|------|
| current | Long | 当前页码 |
| size | Long | 每页条数 |
| total | Long | 总记录数 |
| pages | Long | 总页数 |
| records | Array | 当前页数据列表 |

### 1.4 认证方式

系统采用 **JWT Token** 认证方式。

#### 1.4.1 获取Token

通过登录接口获取:

```
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}
```

响应示例:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "expiresIn": 7200
  }
}
```

#### 1.4.2 使用Token

在请求头中携带Token:

```
Authorization: Bearer {token}
```

#### 1.4.3 Token刷新

```
POST /auth/refresh
Authorization: Bearer {refreshToken}
```

### 1.5 公共请求头

| 请求头 | 必填 | 说明 |
|--------|------|------|
| Content-Type | 是 | `application/json` |
| Authorization | 是(除登录外) | `Bearer {token}` |
| Accept | 否 | `application/json` |

### 1.6 通用请求参数

**分页查询参数:**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| current | Long | 否 | 1 | 页码 |
| size | Long | 否 | 10 | 每页条数 |

**日期范围参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| startDate | String | 否 | 开始日期 (yyyy-MM-dd) |
| endDate | String | 否 | 结束日期 (yyyy-MM-dd) |

---

## 二、认证模块

**基础路径:** `/auth`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| POST | `/auth/login` | 用户登录 |
| POST | `/auth/logout` | 用户登出 |
| POST | `/auth/refresh` | 刷新Token |
| GET | `/auth/captcha` | 获取验证码 |
| GET | `/auth/info` | 获取当前登录用户信息 |

### 2.1 登录接口

```
POST /auth/login
```

**请求参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

**响应数据:**

| 字段 | 类型 | 说明 |
|------|------|------|
| token | String | Access Token |
| refreshToken | String | Refresh Token |
| expiresIn | Integer | Token有效期(秒) |

### 2.2 获取当前用户信息

```
GET /auth/info
Authorization: Bearer {token}
```

**响应数据:**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 用户ID |
| username | String | 用户名 |
| nickname | String | 昵称 |
| avatar | String | 头像URL |
| roles | Array | 角色列表 |
| permissions | Array | 权限列表 |

---

## 三、系统管理模块

### 3.1 用户管理

**基础路径:** `/system/user`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/system/user/list` | 分页查询用户列表 |
| GET | `/system/user/{id}` | 根据ID查询用户详情 |
| POST | `/system/user` | 新增用户 |
| PUT | `/system/user/{id}` | 修改用户 |
| DELETE | `/system/user/{id}` | 删除用户 |
| PUT | `/system/user/{id}/status` | 修改用户状态 |
| PUT | `/system/user/{id}/password` | 重置密码 |
| GET | `/system/user/export` | 导出用户列表 |

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 否 | 用户名(模糊查询) |
| nickname | String | 否 | 昵称(模糊查询) |
| deptId | Long | 否 | 部门ID |
| status | Integer | 否 | 状态(1正常 0停用) |

### 3.2 角色管理

**基础路径:** `/system/role`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/system/role/list` | 分页查询角色列表 |
| GET | `/system/role/{id}` | 根据ID查询角色详情 |
| POST | `/system/role` | 新增角色 |
| PUT | `/system/role/{id}` | 修改角色 |
| DELETE | `/system/role/{id}` | 删除角色 |
| GET | `/system/role/{id}/menu` | 获取角色菜单权限 |
| PUT | `/system/role/{id}/menu` | 分配角色菜单权限 |

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| roleName | String | 否 | 角色名称(模糊查询) |
| roleCode | String | 否 | 角色编码(模糊查询) |
| status | Integer | 否 | 状态 |

### 3.3 菜单管理

**基础路径:** `/system/menu`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/system/menu/list` | 查询菜单列表(树形) |
| GET | `/system/menu/{id}` | 根据ID查询菜单详情 |
| POST | `/system/menu` | 新增菜单 |
| PUT | `/system/menu/{id}` | 修改菜单 |
| DELETE | `/system/menu/{id}` | 删除菜单 |
| GET | `/system/menu/tree` | 获取菜单树 |
| GET | `/system/menu/user` | 获取当前用户菜单 |

### 3.4 部门管理

**基础路径:** `/system/dept`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/system/dept/list` | 查询部门列表(树形) |
| GET | `/system/dept/{id}` | 根据ID查询部门详情 |
| POST | `/system/dept` | 新增部门 |
| PUT | `/system/dept/{id}` | 修改部门 |
| DELETE | `/system/dept/{id}` | 删除部门 |
| GET | `/system/dept/tree` | 获取部门树 |

### 3.5 字典管理

**基础路径:** `/system/dict`

#### 字典类型

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/system/dict/type/list` | 分页查询字典类型列表 |
| GET | `/system/dict/type/{id}` | 根据ID查询字典类型详情 |
| POST | `/system/dict/type` | 新增字典类型 |
| PUT | `/system/dict/type/{id}` | 修改字典类型 |
| DELETE | `/system/dict/type/{id}` | 删除字典类型 |

#### 字典数据

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/system/dict/data/list` | 分页查询字典数据列表 |
| GET | `/system/dict/data/type/{dictType}` | 根据类型查询字典数据 |
| GET | `/system/dict/data/{id}` | 根据ID查询字典数据详情 |
| POST | `/system/dict/data` | 新增字典数据 |
| PUT | `/system/dict/data/{id}` | 修改字典数据 |
| DELETE | `/system/dict/data/{id}` | 删除字典数据 |

### 3.6 操作日志

**基础路径:** `/system/log`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/system/log/list` | 分页查询操作日志 |
| GET | `/system/log/{id}` | 查询日志详情 |
| DELETE | `/system/log/{id}` | 删除日志 |
| DELETE | `/system/log/clean` | 清空日志 |

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | String | 否 | 操作模块 |
| operName | String | 否 | 操作人员 |
| status | Integer | 否 | 操作状态 |
| startTime | String | 否 | 开始时间 |
| endTime | String | 否 | 结束时间 |

---

## 四、监控管理模块

### 4.1 用能设备管理

**基础路径:** `/device`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/device/list` | 分页查询设备列表 |
| GET | `/device/{id}` | 根据ID查询设备详情 |
| POST | `/device` | 新增设备 |
| PUT | `/device/{id}` | 修改设备 |
| DELETE | `/device/{id}` | 删除设备 |
| GET | `/device/export` | 导出设备列表 |
| POST | `/device/import` | 导入设备列表 |

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| deviceName | String | 否 | 设备名称(模糊查询) |
| deviceType | String | 否 | 设备类型 |
| deptId | Long | 否 | 部门ID |
| status | Integer | 否 | 状态 |

### 4.1.1 设备树接口

```
GET /monitor/device/device-tree
Authorization: Bearer {token}
```

**功能描述:** 获取设备树形结构数据，按部门/用能单元组织设备层级关系，用于前端设备选择器、树形展示等场景。

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| deptId | Long | 否 | 部门ID，不传则返回全部 |
| deviceType | String | 否 | 设备类型筛选 |
| status | Integer | 否 | 设备状态筛选(1正常 0停用) |
| keyword | String | 否 | 关键词搜索(设备名称/编码) |

**响应示例:**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "label": "总公司",
      "type": "dept",
      "children": [
        {
          "id": 2,
          "label": "一分厂",
          "type": "dept",
          "children": [
            {
              "id": 101,
              "label": "1号变压器",
              "type": "device",
              "deviceCode": "DEV-001",
              "deviceType": "transformer",
              "status": 1
            },
            {
              "id": 102,
              "label": "2号变压器",
              "type": "device",
              "deviceCode": "DEV-002",
              "deviceType": "transformer",
              "status": 1
            }
          ]
        }
      ]
    }
  ]
}
```

**响应字段说明:**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 节点ID(部门ID或设备ID) |
| label | String | 节点显示名称 |
| type | String | 节点类型(dept=部门, device=设备) |
| children | Array | 子节点列表 |
| deviceCode | String | 设备编码(仅设备节点) |
| deviceType | String | 设备类型(仅设备节点) |
| status | Integer | 设备状态(仅设备节点, 1正常 0停用) |

### 4.2 计量器具管理

**基础路径:** `/meter`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/meter/list` | 分页查询计量器具列表 |
| GET | `/meter/{id}` | 根据ID查询计量器具详情 |
| POST | `/meter` | 新增计量器具 |
| PUT | `/meter/{id}` | 修改计量器具 |
| DELETE | `/meter/{id}` | 删除计量器具 |
| GET | `/meter/export` | 导出计量器具列表 |

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| meterName | String | 否 | 计量器具名称 |
| meterType | String | 否 | 计量类型 |
| deviceId | Long | 否 | 关联设备ID |
| status | Integer | 否 | 状态 |

### 4.3 能源品种管理

**基础路径:** `/energy-type`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/energy-type/list` | 分页查询能源品种列表 |
| GET | `/energy-type/{id}` | 根据ID查询能源品种详情 |
| POST | `/energy-type` | 新增能源品种 |
| PUT | `/energy-type/{id}` | 修改能源品种 |
| DELETE | `/energy-type/{id}` | 删除能源品种 |
| GET | `/energy-type/all` | 查询所有能源品种 |

### 4.4 实时监控

**基础路径:** `/realtime`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/realtime/data` | 获取实时数据 |
| GET | `/realtime/meters` | 获取计量器具实时状态 |
| GET | `/realtime/dashboard` | 获取监控仪表盘数据 |
| GET | `/realtime/alarm/latest` | 获取最新报警 |

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| meterIds | String | 否 | 计量器具ID列表(逗号分隔) |
| deptId | Long | 否 | 部门ID |

### 4.5 历史数据

**基础路径:** `/monitor/history`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/monitor/history/list` | 分页查询历史数据 |
| GET | `/monitor/history/trend` | 查询历史趋势数据 |
| GET | `/monitor/history/export` | 导出历史数据 |

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| meterId | Long | 否 | 计量器具ID |
| dataType | String | 否 | 数据类型 |
| startTime | String | 否 | 开始时间 |
| endTime | String | 否 | 结束时间 |

### 4.6 抄表管理

**基础路径:** `/monitor/reading`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/monitor/reading/list` | 分页查询抄表记录 |
| GET | `/monitor/reading/{id}` | 查询抄表记录详情 |
| POST | `/monitor/reading` | 新增抄表记录 |
| PUT | `/monitor/reading/{id}` | 修改抄表记录 |
| DELETE | `/monitor/reading/{id}` | 删除抄表记录 |

### 4.7 班组管理

**基础路径:** `/work-shift`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/work-shift/list` | 分页查询班组列表 |
| GET | `/work-shift/{id}` | 查询班组详情 |
| POST | `/work-shift` | 新增班组 |
| PUT | `/work-shift/{id}` | 修改班组 |
| DELETE | `/work-shift/{id}` | 删除班组 |

### 4.8 状态监测

**基础路径:** `/monitor/status`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/monitor/status/list` | 分页查询状态日志 |
| GET | `/monitor/status/latest` | 获取设备最新状态 |
| GET | `/monitor/status/online-rate` | 获取在线率统计 |

### 4.9 设备对标

**基础路径:** `/monitor/device-compare`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/monitor/device-compare/compare` | 双设备同时段曲线对比 |

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| meterIdA | Long | 是 | 设备A ID |
| meterIdB | Long | 是 | 设备B ID |
| dataType | String | 否 | 数据类型 |
| startTime | String | 是 | 开始时间 |
| endTime | String | 是 | 结束时间 |

### 4.10 时段对标

**基础路径:** `/monitor/time-compare`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/monitor/time-compare/compare` | 单设备不同时段对比 |

---

## 五、报警管理模块

### 5.1 报警阈值

**基础路径:** `/alarm/threshold`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/alarm/threshold/list` | 分页查询阈值列表 |
| GET | `/alarm/threshold/enabled` | 获取所有启用的阈值 |
| GET | `/alarm/threshold/{id}` | 根据ID查询阈值详情 |
| POST | `/alarm/threshold` | 新增阈值 |
| PUT | `/alarm/threshold/{id}` | 修改阈值 |
| PUT | `/alarm/threshold/{id}/enabled` | 启用/禁用阈值 |
| DELETE | `/alarm/threshold/{id}` | 删除阈值 |

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| thresholdName | String | 否 | 阈值名称 |
| metricType | String | 否 | 指标类型 |
| alarmLevel | String | 否 | 报警级别 |
| enabled | Integer | 否 | 启用状态 |

### 5.2 报警记录

**基础路径:** `/alarm/record`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/alarm/record/list` | 分页查询报警记录 |
| GET | `/alarm/record/{id}` | 查询报警记录详情 |
| PUT | `/alarm/record/{id}/handle` | 处理报警 |
| GET | `/alarm/record/statistics` | 报警统计 |
| GET | `/alarm/record/unhandled` | 获取未处理报警数量 |

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| alarmType | String | 否 | 报警类型 |
| alarmLevel | String | 否 | 报警级别 |
| handleStatus | String | 否 | 处理状态 |
| startTime | String | 否 | 开始时间 |
| endTime | String | 否 | 结束时间 |

---

## 六、碳排管理模块

### 6.1 碳排放核算

**基础路径:** `/carbon/accounting`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/carbon/accounting/list` | 分页查询核算列表 |
| GET | `/carbon/accounting/{id}` | 查询核算详情 |
| POST | `/carbon/accounting` | 新增核算 |
| PUT | `/carbon/accounting/{id}` | 修改核算 |
| DELETE | `/carbon/accounting/{id}` | 删除核算 |
| PUT | `/carbon/accounting/{id}/submit` | 提交核算 |
| GET | `/carbon/accounting/{id}/detail` | 查询核算明细 |
| POST | `/carbon/accounting/{id}/detail` | 新增核算明细 |

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| accountingName | String | 否 | 核算名称 |
| accountingYear | Integer | 否 | 核算年份 |
| scopeType | String | 否 | 范围类型 |
| status | String | 否 | 状态 |

### 6.2 排放因子库

**基础路径:** `/carbon/factor`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/carbon/factor/list` | 分页查询因子列表 |
| GET | `/carbon/factor/{id}` | 查询因子详情 |
| POST | `/carbon/factor` | 新增排放因子 |
| PUT | `/carbon/factor/{id}` | 修改排放因子 |
| DELETE | `/carbon/factor/{id}` | 删除排放因子 |
| GET | `/carbon/factor/category/{category}` | 按类别查询因子 |
| GET | `/carbon/factor/energyType/{energyTypeId}` | 按能源类型查询因子 |
| GET | `/carbon/factor/defaults` | 获取默认因子列表 |
| PUT | `/carbon/factor/{id}/setDefault` | 设为默认因子 |

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| factorName | String | 否 | 因子名称 |
| factorCategory | String | 否 | 因子类别 |
| effectiveYear | Integer | 否 | 生效年份 |

### 6.3 碳资产管理

**基础路径:** `/carbon/asset`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/carbon/asset/list` | 分页查询碳资产列表 |
| GET | `/carbon/asset/{id}` | 查询碳资产详情 |
| POST | `/carbon/asset` | 新增碳资产 |
| PUT | `/carbon/asset/{id}` | 修改碳资产 |
| DELETE | `/carbon/asset/{id}` | 删除碳资产 |
| GET | `/carbon/asset/summary` | 资产汇总统计 |
| GET | `/carbon/asset/trade/list` | 查询交易记录 |
| POST | `/carbon/asset/trade` | 新增交易记录 |

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| assetType | String | 否 | 资产类型(QUOTA/CCER/GREEN_CERT) |
| assetName | String | 否 | 资产名称 |
| status | String | 否 | 状态 |

### 6.4 CBAM申报

**基础路径:** `/carbon/cbam`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/carbon/cbam/list` | 分页查询CBAM申报 |
| GET | `/carbon/cbam/{id}` | 查询申报详情 |
| POST | `/carbon/cbam` | 新增申报 |
| PUT | `/carbon/cbam/{id}` | 修改申报 |
| DELETE | `/carbon/cbam/{id}` | 删除申报 |
| PUT | `/carbon/cbam/{id}/submit` | 提交申报 |
| GET | `/carbon/cbam/{id}/detail` | 查询申报明细 |

### 6.5 产品碳足迹

**基础路径:** `/carbon/footprint`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/carbon/footprint/list` | 分页查询碳足迹 |
| GET | `/carbon/footprint/{id}` | 查询碳足迹详情 |
| POST | `/carbon/footprint` | 新增碳足迹 |
| PUT | `/carbon/footprint/{id}` | 修改碳足迹 |
| DELETE | `/carbon/footprint/{id}` | 删除碳足迹 |
| GET | `/carbon/footprint/product/{productId}` | 按产品查询碳足迹 |

### 6.6 供应链碳足迹

**基础路径:** `/carbon/supply-chain`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/carbon/supply-chain/list` | 分页查询供应链碳足迹 |
| GET | `/carbon/supply-chain/{id}` | 查询详情 |
| POST | `/carbon/supply-chain` | 新增记录 |
| PUT | `/carbon/supply-chain/{id}` | 修改记录 |
| DELETE | `/carbon/supply-chain/{id}` | 删除记录 |

### 6.7 碳核查

**基础路径:** `/carbon/verification`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/carbon/verification/list` | 分页查询核查记录 |
| GET | `/carbon/verification/{id}` | 查询核查详情 |
| POST | `/carbon/verification` | 新增核查记录 |
| PUT | `/carbon/verification/{id}` | 修改核查记录 |
| DELETE | `/carbon/verification/{id}` | 删除核查记录 |

### 6.8 碳市场信息

**基础路径:** `/carbon/market`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/carbon/market/list` | 分页查询市场信息 |
| GET | `/carbon/market/{id}` | 查询详情 |
| POST | `/carbon/market` | 新增信息 |
| PUT | `/carbon/market/{id}` | 修改信息 |
| DELETE | `/carbon/market/{id}` | 删除信息 |

---

## 七、报表管理模块

### 7.1 能耗统计报表

**基础路径:** `/report/energy/summary`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/report/energy/summary/list` | 分页查询能耗统计 |
| GET | `/report/energy/summary/range` | 按日期范围查询 |
| POST | `/report/energy/summary` | 新增能耗统计 |
| DELETE | `/report/energy/summary/{id}` | 删除能耗统计 |
| GET | `/report/energy/summary/export` | 导出CSV |

### 7.2 能耗汇总报表

**基础路径:** `/report/energy/total`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/report/energy/total/list` | 分页查询能耗汇总 |
| GET | `/report/energy/total/{id}` | 查询详情 |
| POST | `/report/energy/total` | 新增能耗汇总 |
| DELETE | `/report/energy/total/{id}` | 删除能耗汇总 |
| GET | `/report/energy/total/range` | 按日期范围查询 |

### 7.3 成本统计报表

**基础路径:** `/report/cost`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/report/cost/list` | 分页查询成本统计 |
| POST | `/report/cost` | 新增成本统计 |
| DELETE | `/report/cost/{id}` | 删除成本统计 |

### 7.4 节能量报表

**基础路径:** `/report/saving`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/report/saving/list` | 分页查询节能量 |
| POST | `/report/saving` | 新增节能量记录 |
| DELETE | `/report/saving/{id}` | 删除节能量记录 |

### 7.5 基准能耗

**基础路径:** `/report/baseline`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/report/baseline/list` | 分页查询基准能耗 |
| GET | `/report/baseline/{id}` | 查询详情 |
| POST | `/report/baseline` | 新增基准能耗 |
| PUT | `/report/baseline/{id}` | 修改基准能耗 |
| DELETE | `/report/baseline/{id}` | 删除基准能耗 |

### 7.6 指标录入

**基础路径:** `/report/indicator`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/report/indicator/list` | 分页查询指标录入 |
| POST | `/report/indicator` | 新增指标录入 |
| PUT | `/report/indicator/{id}` | 修改指标录入 |
| DELETE | `/report/indicator/{id}` | 删除指标录入 |

### 7.7 产量录入

**基础路径:** `/report/production`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/report/production/list` | 分页查询产量录入 |
| POST | `/report/production` | 新增产量录入 |
| PUT | `/report/production/{id}` | 修改产量录入 |
| DELETE | `/report/production/{id}` | 删除产量录入 |

---

## 八、分析模块

### 8.1 能耗分析

**基础路径:** `/monitor/analysis/consumption`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/monitor/analysis/consumption/list` | 分页查询能耗数据 |
| GET | `/monitor/analysis/consumption/summary` | 能耗汇总卡片数据 |
| GET | `/monitor/analysis/consumption/trend` | 能耗趋势数据 |
| POST | `/monitor/analysis/consumption` | 新增能耗数据 |
| PUT | `/monitor/analysis/consumption/{id}` | 修改能耗数据 |
| DELETE | `/monitor/analysis/consumption/{id}` | 删除能耗数据 |

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| energyUnitId | Long | 否 | 用能单元ID |
| meterId | Long | 否 | 计量器具ID |
| energyTypeId | Long | 否 | 能源品种ID |
| statPeriod | String | 否 | 统计周期(DAY/MONTH/YEAR) |
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |

### 8.2 电费分析

**基础路径:** `/monitor/analysis/electricity`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/monitor/analysis/electricity/list` | 分页查询电费数据 |
| GET | `/monitor/analysis/electricity/summary` | 电费汇总统计 |
| GET | `/monitor/analysis/electricity/trend` | 电费趋势 |

### 8.3 能效分析

**基础路径:** `/monitor/analysis/efficiency`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/monitor/analysis/efficiency/list` | 分页查询能效数据 |
| GET | `/monitor/analysis/efficiency/summary` | 能效汇总 |
| GET | `/monitor/analysis/efficiency/trend` | 能效趋势 |

### 8.4 能碳预算

**基础路径:** `/monitor/analysis/budget`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/monitor/analysis/budget/list` | 分页查询预算 |
| GET | `/monitor/analysis/budget/{id}` | 查询预算详情 |
| POST | `/monitor/analysis/budget` | 新增预算 |
| PUT | `/monitor/analysis/budget/{id}` | 修改预算 |
| DELETE | `/monitor/analysis/budget/{id}` | 删除预算 |

### 8.5 对标库

**基础路径:** `/monitor/analysis/benchmark`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/monitor/analysis/benchmark/list` | 分页查询标杆值 |
| GET | `/monitor/analysis/benchmark/{id}` | 查询详情 |
| POST | `/monitor/analysis/benchmark` | 新增标杆值 |
| PUT | `/monitor/analysis/benchmark/{id}` | 修改标杆值 |
| DELETE | `/monitor/analysis/benchmark/{id}` | 删除标杆值 |

---

## 九、AI服务模块

### 9.1 AI服务预约

**基础路径:** `/ai/service`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/ai/service/list` | 分页查询服务预约 |
| GET | `/ai/service/{id}` | 查询预约详情 |
| POST | `/ai/service` | 新增服务预约 |
| PUT | `/ai/service/{id}/cancel` | 取消预约 |
| PUT | `/ai/service/{id}/status` | 更新预约状态 |
| DELETE | `/ai/service/{id}` | 删除预约记录 |

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| serviceType | String | 否 | 服务类型 |
| status | String | 否 | 状态 |
| applicant | String | 否 | 申请人 |

### 9.2 AI知识库

**基础路径:** `/ai/knowledge`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/ai/knowledge/list` | 分页查询文档列表 |
| POST | `/ai/knowledge/upload` | 上传文档 |
| GET | `/ai/knowledge/search` | 文档检索 |
| GET | `/ai/knowledge/{id}` | 查询文档详情 |
| DELETE | `/ai/knowledge/{id}` | 删除文档 |

**上传参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| docName | String | 是 | 文档名称 |
| docType | String | 是 | 文档类型 |
| uploadUser | String | 否 | 上传用户 |
| summary | String | 否 | 文档摘要 |
| file | File | 是 | 文件 |

### 9.3 AI报告生成

**基础路径:** `/ai/report`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/ai/report/list` | 分页查询报告任务 |
| POST | `/ai/report` | 创建报告任务 |
| GET | `/ai/report/{id}` | 查询任务状态 |
| PUT | `/ai/report/{id}/cancel` | 取消任务 |
| DELETE | `/ai/report/{id}` | 删除报告任务 |

### 9.4 AI报告模板

**基础路径:** `/ai/report-template`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/ai/report-template/list` | 分页查询模板列表 |
| GET | `/ai/report-template/{id}` | 查询模板详情 |
| POST | `/ai/report-template` | 新增模板 |
| PUT | `/ai/report-template/{id}` | 修改模板 |
| DELETE | `/ai/report-template/{id}` | 删除模板 |

---

## 十、工具箱模块

### 10.1 分时电价

**基础路径:** `/toolkit/electricity-price`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/toolkit/electricity-price/list` | 分页查询电价配置 |
| GET | `/toolkit/electricity-price/current` | 获取当前生效电价 |
| GET | `/toolkit/electricity-price/{id}` | 查询电价详情 |
| POST | `/toolkit/electricity-price` | 新增电价配置 |
| PUT | `/toolkit/electricity-price/{id}` | 修改电价配置 |
| PUT | `/toolkit/electricity-price/{id}/status` | 启用/禁用 |
| DELETE | `/toolkit/electricity-price/{id}` | 删除电价配置 |

### 10.2 错峰排产

**基础路径:** `/toolkit/peak-shifting`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/toolkit/peak-shifting/list` | 分页查询排产计划 |
| POST | `/toolkit/peak-shifting` | 新增排产计划 |
| PUT | `/toolkit/peak-shifting/{id}` | 修改排产计划 |
| DELETE | `/toolkit/peak-shifting/{id}` | 删除排产计划 |
| GET | `/toolkit/peak-shifting/config/list` | 获取排产配置列表 |
| POST | `/toolkit/peak-shifting/config` | 新增排产配置 |
| PUT | `/toolkit/peak-shifting/config/{id}` | 修改排产配置 |
| DELETE | `/toolkit/peak-shifting/config/{id}` | 删除排产配置 |
| GET | `/toolkit/peak-shifting/suggestion` | 智能排产建议 |

### 10.3 绿色工厂申报

**基础路径:** `/toolkit/green-factory`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | `/toolkit/green-factory/list` | 分页查询申报列表 |
| POST | `/toolkit/green-factory` | 创建申报 |
| GET | `/toolkit/green-factory/{id}` | 查询申报详情 |
| POST | `/toolkit/green-factory/{id}/upload-item` | 上传资料 |
| PUT | `/toolkit/green-factory/{id}/progress` | 更新进度 |
| PUT | `/toolkit/green-factory/{id}/submit` | 提交审核 |
| DELETE | `/toolkit/green-factory/{id}` | 删除申报 |

### 10.4 能碳计算器

**基础路径:** `/toolkit/calculator`

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| POST | `/toolkit/calculator/carbon` | 碳排放计算 |
| POST | `/toolkit/calculator/electricity` | 电量计算 |
| POST | `/toolkit/calculator/cost` | 成本计算 |
| GET | `/toolkit/calculator/history` | 查询计算历史 |
| DELETE | `/toolkit/calculator/{id}` | 删除计算记录 |

**碳排放计算参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| activityData | Double | 是 | 活动数据 |
| emissionFactor | Double | 是 | 排放因子 |

**电量计算参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| power | Double | 是 | 功率(kW) |
| hours | Double | 是 | 时长(h) |

**成本计算参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| consumption | Double | 是 | 用电量(kWh) |
| peakPrice | Double | 是 | 峰段电价 |
| flatPrice | Double | 是 | 平段电价 |
| valleyPrice | Double | 是 | 谷段电价 |
| peakRatio | Double | 否 | 峰段比例(默认0.3) |
| flatRatio | Double | 否 | 平段比例(默认0.4) |
| valleyRatio | Double | 否 | 谷段比例(默认0.3) |

---

## 十一、通知公告模块

### 11.1 通知公告管理

| 接口地址 | 请求方式 | 说明 |
|----------|----------|------|
| `/system/notice/list` | GET | 查询通知公告列表 |
| `/system/notice/{id}` | GET | 获取通知公告详情 |
| `/system/notice` | POST | 新增通知公告 |
| `/system/notice/{id}` | PUT | 修改通知公告 |
| `/system/notice/{id}` | DELETE | 删除通知公告 |
| `/system/notice/{id}/dispatch` | POST | 下发通知公告到指定用户 |

### 11.2 我的消息

| 接口地址 | 请求方式 | 说明 |
|----------|----------|------|
| `/system/message/list` | GET | 查询我的消息列表 |
| `/system/message/unread/count` | GET | 获取未读消息数量 |
| `/system/message/{id}/read` | POST | 标记消息为已读 |

### 11.3 下发记录

| 接口地址 | 请求方式 | 说明 |
|----------|----------|------|
| `/system/notice/dispatch/list` | GET | 查询下发记录列表 |
| `/system/notice/dispatch/{id}` | GET | 获取下发记录详情 |

---

## 十二、错误码说明

### 12.1 通用错误码

| 错误码 | 说明 | 处理方式 |
|--------|------|----------|
| 200 | 操作成功 | - |
| 400 | 请求参数错误 | 检查请求参数 |
| 401 | 未授权，Token无效或过期 | 重新登录获取Token |
| 403 | 禁止访问，权限不足 | 联系管理员分配权限 |
| 404 | 资源不存在 | 检查请求路径和参数 |
| 405 | 请求方法不允许 | 检查HTTP请求方法 |
| 500 | 服务器内部错误 | 联系技术支持 |

### 11.2 业务错误码

| 错误码 | 说明 |
|--------|------|
| 1001 | 用户名或密码错误 |
| 1002 | 用户已被禁用 |
| 1003 | 验证码错误 |
| 1004 | Token已过期 |
| 1005 | Token无效 |
| 1006 | 用户不存在 |
| 1007 | 角色不存在 |
| 1008 | 菜单不存在 |
| 1009 | 部门不存在 |
| 1010 | 字典类型不存在 |
| 1011 | 字典数据不存在 |
| 2001 | 设备编码已存在 |
| 2002 | 计量器具编码已存在 |
| 2003 | 能源类型编码已存在 |
| 3001 | 报警阈值不存在 |
| 3002 | 报警记录不存在 |
| 4001 | 核算记录不存在 |
| 4002 | 排放因子不存在 |
| 4003 | 碳资产不存在 |
| 4004 | CBAM申报不存在 |
| 4005 | 产品碳足迹不存在 |
| 5001 | 报表记录不存在 |
| 6001 | 计算参数错误 |

### 11.3 错误响应示例

```json
{
  "code": 401,
  "message": "Token已过期，请重新登录",
  "data": null
}
```

```json
{
  "code": 400,
  "message": "请求参数错误: 用户名不能为空",
  "data": null
}
```

```json
{
  "code": 500,
  "message": "服务器内部错误: 数据库连接失败",
  "data": null
}
```

---

## 附录

### A. 接口分组汇总

| 分组 | 基础路径 | 接口数量 |
|------|----------|----------|
| 认证模块 | /auth | 5 |
| 用户管理 | /system/user | 8 |
| 角色管理 | /system/role | 7 |
| 菜单管理 | /system/menu | 6 |
| 部门管理 | /system/dept | 5 |
| 字典管理 | /system/dict | 10 |
| 操作日志 | /system/log | 4 |
| 用能设备 | /device | 7 |
| 计量器具 | /meter | 6 |
| 能源品种 | /energy-type | 5 |
| 实时监控 | /realtime | 4 |
| 历史数据 | /monitor/history | 3 |
| 抄表管理 | /monitor/reading | 5 |
| 班组管理 | /work-shift | 5 |
| 状态监测 | /monitor/status | 3 |
| 设备对标 | /monitor/device-compare | 1 |
| 时段对标 | /monitor/time-compare | 1 |
| 报警阈值 | /alarm/threshold | 7 |
| 报警记录 | /alarm/record | 5 |
| 碳排放核算 | /carbon/accounting | 8 |
| 排放因子 | /carbon/factor | 9 |
| 碳资产管理 | /carbon/asset | 7 |
| CBAM申报 | /carbon/cbam | 7 |
| 产品碳足迹 | /carbon/footprint | 5 |
| 供应链碳 | /carbon/supply-chain | 5 |
| 碳核查 | /carbon/verification | 5 |
| 碳市场信息 | /carbon/market | 5 |
| 能耗统计报表 | /report/energy/summary | 5 |
| 能耗汇总报表 | /report/energy/total | 5 |
| 成本统计报表 | /report/cost | 3 |
| 节能量报表 | /report/saving | 3 |
| 基准能耗 | /report/baseline | 5 |
| 指标录入 | /report/indicator | 4 |
| 产量录入 | /report/production | 4 |
| 能耗分析 | /monitor/analysis/consumption | 6 |
| 电费分析 | /monitor/analysis/electricity | 3 |
| 能效分析 | /monitor/analysis/efficiency | 3 |
| 能碳预算 | /monitor/analysis/budget | 5 |
| 对标库 | /monitor/analysis/benchmark | 5 |
| AI服务预约 | /ai/service | 6 |
| AI知识库 | /ai/knowledge | 5 |
| AI报告生成 | /ai/report | 5 |
| AI报告模板 | /ai/report-template | 5 |
| 分时电价 | /toolkit/electricity-price | 7 |
| 错峰排产 | /toolkit/peak-shifting | 9 |
| 绿色工厂 | /toolkit/green-factory | 7 |
| 能碳计算器 | /toolkit/calculator | 5 |
| **合计** | - | **约260+** |

### B. 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0.0 | 2026-06-22 | 初始版本 |
| v1.1.0 | 2026-06-24 | 新增设备树接口(/monitor/device/device-tree)，更新文档日期 |
