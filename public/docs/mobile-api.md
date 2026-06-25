# 能碳管理平台 - 移动端接口文档

> 版本：v1.0  
> 更新日期：2026-06-25  
> 基础路径：`/api`

---

## 一、认证方式

所有接口（除登录外）均需在请求头中携带 JWT Token：

```
Authorization: Bearer <accessToken>
```

**Token 获取方式**：通过 `/auth/login` 接口登录获取。

**Token 有效期**：7200 秒（2 小时），过期后可使用 `refreshToken` 刷新。

---

## 二、通用响应格式

### 2.1 成功响应

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... }
}
```

### 2.2 失败响应

```json
{
  "code": 500,
  "message": "错误信息",
  "data": null
}
```

### 2.3 分页响应

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "current": 1,
    "size": 10,
    "total": 100,
    "records": [ ... ]
  }
}
```

---

## 三、接口列表

> 标记说明：`📱` 表示移动端主要使用的接口  
> `⚙️` 表示管理后台使用的接口（移动端一般不使用）

---

### 3.1 认证模块 (Auth)

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| POST | `/auth/login` | 📱 | 用户登录 |
| GET | `/auth/userinfo` | 📱 | 获取当前用户信息 |
| POST | `/auth/logout` | 📱 | 用户登出 |

#### 3.1.1 用户登录

**POST** `/auth/login`

📱 **移动端使用**

请求参数：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

响应数据：

| 字段 | 类型 | 说明 |
|------|------|------|
| accessToken | String | 访问令牌 |
| refreshToken | String | 刷新令牌 |
| expiresIn | Integer | 过期时间（秒） |

---

#### 3.1.2 获取当前用户信息

**GET** `/auth/userinfo`

📱 **移动端使用**

请求参数：无（从 Token 解析）

响应数据：

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 用户ID |
| username | String | 用户名 |
| nickname | String | 昵称 |
| avatar | String | 头像URL |
| phone | String | 手机号 |
| email | String | 邮箱 |
| roles | Array | 角色列表 |
| permissions | Array | 权限列表 |

---

#### 3.1.3 用户登出

**POST** `/auth/logout`

📱 **移动端使用**

请求参数：无

---

### 3.2 系统管理模块 (System)

#### 3.2.1 字典数据查询

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/system/dictData/type/{dictType}` | 📱 | 按字典类型查询数据 |

**GET** `/system/dictData/type/{dictType}`

📱 **移动端使用**

路径参数：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| dictType | String | 是 | 字典类型编码 |

响应数据：字典数据列表

---

#### 3.2.2 部门管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/system/dept/tree` | 📱 | 查询部门树 |
| GET | `/system/dept/list` | 📱 | 查询部门列表 |
| GET | `/system/dept/{id}` | | 根据ID查询部门 |
| POST | `/system/dept` | ⚙️ | 新增部门 |
| PUT | `/system/dept/{id}` | ⚙️ | 修改部门 |
| DELETE | `/system/dept/{id}` | ⚙️ | 删除部门 |

---

#### 3.2.3 用户管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/system/user/list` | ⚙️ | 分页查询用户列表 |
| GET | `/system/user/{id}` | | 根据ID查询用户 |
| POST | `/system/user` | ⚙️ | 新增用户 |
| PUT | `/system/user/{id}` | ⚙️ | 修改用户 |
| DELETE | `/system/user/{id}` | ⚙️ | 删除用户 |
| POST | `/system/user/assignRole` | ⚙️ | 分配角色 |
| PUT | `/system/user/{id}/status` | ⚙️ | 修改用户状态 |
| PUT | `/system/user/{id}/resetPassword` | ⚙️ | 重置密码 |

#### 3.2.3.1 分页查询用户列表

**GET** `/system/user/list`

请求参数：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| current | Long | 否 | 1 | 页码 |
| size | Long | 否 | 10 | 每页条数 |
| username | String | 否 | | 用户名（模糊查询） |
| status | Integer | 否 | | 状态 |

---

#### 3.2.4 角色管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/system/role/list` | ⚙️ | 分页查询角色列表 |
| GET | `/system/role/all` | | 查询所有角色 |
| GET | `/system/role/{id}` | | 根据ID查询角色 |
| POST | `/system/role` | ⚙️ | 新增角色 |
| PUT | `/system/role/{id}` | ⚙️ | 修改角色 |
| DELETE | `/system/role/{id}` | ⚙️ | 删除角色 |
| POST | `/system/role/assignMenu` | ⚙️ | 分配菜单 |
| GET | `/system/role/{id}/menuIds` | | 获取角色菜单ID列表 |
| PUT | `/system/role/{id}/status` | ⚙️ | 修改角色状态 |

---

#### 3.2.5 菜单管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/system/menu/list` | ⚙️ | 查询菜单列表 |
| GET | `/system/menu/tree` | ⚙️ | 获取菜单树 |
| GET | `/system/menu/{id}` | | 根据ID查询菜单 |
| POST | `/system/menu` | ⚙️ | 新增菜单 |
| PUT | `/system/menu/{id}` | ⚙️ | 修改菜单 |
| DELETE | `/system/menu/{id}` | ⚙️ | 删除菜单 |

---

#### 3.2.6 字典类型管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/system/dictType/list` | ⚙️ | 分页查询字典类型 |
| GET | `/system/dictType/{id}` | | 根据ID查询字典类型 |
| POST | `/system/dictType` | ⚙️ | 新增字典类型 |
| PUT | `/system/dictType/{id}` | ⚙️ | 修改字典类型 |
| DELETE | `/system/dictType/{id}` | ⚙️ | 删除字典类型 |

---

#### 3.2.7 字典数据管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/system/dictData/list` | ⚙️ | 分页查询字典数据 |
| GET | `/system/dictData/type/{dictType}` | 📱 | 按字典类型查询数据 |
| GET | `/system/dictData/{id}` | | 根据ID查询字典数据 |
| POST | `/system/dictData` | ⚙️ | 新增字典数据 |
| PUT | `/system/dictData/{id}` | ⚙️ | 修改字典数据 |
| DELETE | `/system/dictData/{id}` | ⚙️ | 删除字典数据 |

---

#### 3.2.8 操作日志管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/system/operLog/list` | ⚙️ | 分页查询操作日志 |
| DELETE | `/system/operLog/clear` | ⚙️ | 清空操作日志 |

---

### 3.3 设备监控模块 (Monitor)

#### 3.3.1 用能设备管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/monitor/device/list` | 📱 | 分页查询设备列表 |
| GET | `/monitor/device/{id}` | 📱 | 根据ID查询设备 |
| GET | `/monitor/device/device-tree` | 📱 | 获取设备树 |
| POST | `/monitor/device` | ⚙️ | 新增设备 |
| PUT | `/monitor/device/{id}` | ⚙️ | 修改设备 |
| DELETE | `/monitor/device/{id}` | ⚙️ | 删除设备 |
| PUT | `/monitor/device/{id}/status` | ⚙️ | 修改设备状态 |
| GET | `/monitor/device/export` | | 导出设备CSV |
| POST | `/monitor/device/import` | ⚙️ | 导入设备 |

**GET** `/monitor/device/list`

📱 **移动端使用**

请求参数：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| current | Long | 否 | 1 | 页码 |
| size | Long | 否 | 10 | 每页条数 |
| deviceName | String | 否 | | 设备名称（模糊） |
| deviceType | String | 否 | | 设备类型 |
| status | Integer | 否 | | 状态 |

**GET** `/monitor/device/device-tree`

📱 **移动端使用**

请求参数：无

响应数据：设备树结构（用能单元 -> 设备 -> 仪表）

```json
[
  {
    "id": 1,
    "name": "生产车间A",
    "type": "unit",
    "children": [
      {
        "id": 101,
        "name": "空压机",
        "type": "device",
        "children": [
          { "id": 1001, "name": "电表1", "type": "meter", "data": { "status": 1 } }
        ]
      }
    ]
  }
]
```

---

#### 3.3.2 计量器具管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/monitor/meter/list` | 📱 | 分页查询计量器具 |
| GET | `/monitor/meter/{id}` | 📱 | 根据ID查询计量器具 |
| POST | `/monitor/meter` | ⚙️ | 新增计量器具 |
| PUT | `/monitor/meter/{id}` | ⚙️ | 修改计量器具 |
| DELETE | `/monitor/meter/{id}` | ⚙️ | 删除计量器具 |
| PUT | `/monitor/meter/{id}/status` | ⚙️ | 修改计量器具状态 |
| GET | `/monitor/meter/export` | | 导出CSV |
| POST | `/monitor/meter/import` | ⚙️ | 导入 |

---

#### 3.3.3 实时监测

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/monitor/realtime/list` | 📱 | 查询实时监测数据 |

**GET** `/monitor/realtime/list`

📱 **移动端使用**

请求参数：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| unitId | Long | 否 | | 单元ID |
| energyType | String | 否 | | 能源类型 |
| page | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 20 | 每页条数 |

响应数据：

| 字段 | 类型 | 说明 |
|------|------|------|
| records[].id | Integer | 数据项ID |
| records[].dataItemName | String | 数据项名称 |
| records[].collectValue | Number | 采集值 |
| records[].realValue | Number | 实际值 |
| records[].unit | String | 单位 |

---

#### 3.3.4 状态监测

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/monitor/status/list` | 📱 | 分页查询状态日志 |
| GET | `/monitor/status/latest` | 📱 | 获取设备最新状态 |
| GET | `/monitor/status/online-rate` | 📱 | 获取在线率统计 |

**GET** `/monitor/status/latest`

📱 **移动端使用**

请求参数：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| deviceType | String | 否 | | 设备类型 |

响应数据：

| 字段 | 类型 | 说明 |
|------|------|------|
| deviceId | Long | 设备ID |
| deviceType | String | 设备类型 |
| onlineStatus | Integer | 在线状态（1-在线，0-离线） |
| duration | String | 持续时长 |
| createTime | DateTime | 记录时间 |

**GET** `/monitor/status/online-rate`

📱 **移动端使用**

响应数据：

| 字段 | 类型 | 说明 |
|------|------|------|
| total | Long | 设备总数 |
| online | Long | 在线数量 |
| offline | Long | 离线数量 |
| onlineRate | Double | 在线率(%) |

---

#### 3.3.5 历史数据查询

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/monitor/history/list` | 📱 | 分页查询历史数据 |
| GET | `/monitor/history/chart` | 📱 | 查询历史数据曲线 |
| GET | `/monitor/history/data-types` | 📱 | 获取数据类型列表 |

**GET** `/monitor/history/chart`

📱 **移动端使用**

请求参数：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| meterId | Long | 是 | | 计量器具ID |
| dataType | String | 否 | | 数据类型 |
| startTime | String | 是 | | 开始时间（ISO格式） |
| endTime | String | 是 | | 结束时间（ISO格式） |

---

#### 3.3.6 能源品种管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/monitor/energyType/list` | 📱 | 分页查询能源品种 |
| GET | `/monitor/energyType/all` | 📱 | 查询所有能源品种 |
| GET | `/monitor/energyType/{id}` | | 根据ID查询能源品种 |
| POST | `/monitor/energyType` | ⚙️ | 新增能源品种 |
| PUT | `/monitor/energyType/{id}` | ⚙️ | 修改能源品种 |
| DELETE | `/monitor/energyType/{id}` | ⚙️ | 删除能源品种 |
| PUT | `/monitor/energyType/{id}/status` | ⚙️ | 修改能源品种状态 |
| GET | `/monitor/energyType/template/list` | | 分页查询模板列表 |
| GET | `/monitor/energyType/template/{id}` | | 根据ID查询模板 |
| POST | `/monitor/energyType/template` | ⚙️ | 新增模板 |
| PUT | `/monitor/energyType/template/{id}` | ⚙️ | 修改模板 |
| DELETE | `/monitor/energyType/template/{id}` | ⚙️ | 删除模板 |
| GET | `/monitor/energyType/template/{id}/types` | | 根据模板获取能源品种 |

---

#### 3.3.7 计量抄表

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/monitor/meter-record/list` | 📱 | 分页查询抄表记录 |
| GET | `/monitor/meter-record/last/{meterId}` | 📱 | 获取上次抄表记录 |
| POST | `/monitor/meter-record` | 📱 | 新增抄表记录 |
| POST | `/monitor/meter-record/batch` | ⚙️ | 批量新增抄表记录 |
| PUT | `/monitor/meter-record/{id}` | ⚙️ | 修改抄表记录 |
| DELETE | `/monitor/meter-record/{id}` | ⚙️ | 删除抄表记录 |

**POST** `/monitor/meter-record`

📱 **移动端使用**

请求参数：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| meterId | Long | 是 | 计量器具ID |
| startCode | BigDecimal | 是 | 起始表码 |
| endCode | BigDecimal | 是 | 结束表码 |
| recordTime | DateTime | 是 | 抄表时间 |
| unitPrice | BigDecimal | 否 | 单价 |
| remark | String | 否 | 备注 |

> 注：系统自动计算用量（endCode - startCode）和费用（用量 x 单价）

---

#### 3.3.8 能耗分析

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/monitor/analysis/consumption/list` | 📱 | 分页查询能耗数据 |
| GET | `/monitor/analysis/consumption/summary` | 📱 | 能耗汇总卡片数据 |
| GET | `/monitor/analysis/consumption/trend` | 📱 | 能耗趋势数据 |
| POST | `/monitor/analysis/consumption` | ⚙️ | 新增能耗数据 |
| PUT | `/monitor/analysis/consumption/{id}` | ⚙️ | 修改能耗数据 |
| DELETE | `/monitor/analysis/consumption/{id}` | ⚙️ | 删除能耗数据 |

**GET** `/monitor/analysis/consumption/summary`

📱 **移动端使用**

请求参数：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| energyUnitId | Long | 否 | | 用能单元ID |
| statPeriod | String | 否 | MONTH | 统计周期(DAY/MONTH/YEAR) |
| startDate | String | 否 | | 开始日期 |
| endDate | String | 否 | | 结束日期 |
| energyTypeId | Long | 否 | | 能源品种ID |

响应数据：

| 字段 | 类型 | 说明 |
|------|------|------|
| totalConsumption | BigDecimal | 总能耗 |
| samePeriodConsumption | BigDecimal | 同期能耗 |
| prevPeriodConsumption | BigDecimal | 上期能耗 |
| yoyRate | BigDecimal | 同比变化率(%) |
| momRate | BigDecimal | 环比变化率(%) |
| typeRatio | Array | 按能源品种占比 |

---

#### 3.3.9 电费分析

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/monitor/analysis/electricity/list` | 📱 | 分页查询电费明细 |
| GET | `/monitor/analysis/electricity/summary` | 📱 | 电费汇总数据 |
| GET | `/monitor/analysis/electricity/detail` | 📱 | 分时电价统计明细 |
| POST | `/monitor/analysis/electricity` | ⚙️ | 新增电费数据 |
| PUT | `/monitor/analysis/electricity/{id}` | ⚙️ | 修改电费数据 |
| DELETE | `/monitor/analysis/electricity/{id}` | ⚙️ | 删除电费数据 |

---

#### 3.3.10 能效分析

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/monitor/analysis/efficiency/list` | 📱 | 分页查询能效数据 |
| GET | `/monitor/analysis/efficiency/trend` | 📱 | 能效趋势数据 |
| GET | `/monitor/analysis/efficiency/intensity` | 📱 | 能耗强度计算 |
| GET | `/monitor/analysis/efficiency/benchmark` | 📱 | 对标分析结果 |
| POST | `/monitor/analysis/efficiency` | ⚙️ | 新增能效数据 |
| PUT | `/monitor/analysis/efficiency/{id}` | ⚙️ | 修改能效数据 |
| DELETE | `/monitor/analysis/efficiency/{id}` | ⚙️ | 删除能效数据 |

---

#### 3.3.11 对标库管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/monitor/analysis/benchmark/list` | 📱 | 分页查询对标数据 |
| GET | `/monitor/analysis/benchmark/industry/{industry}` | 📱 | 查询行业基准 |
| GET | `/monitor/analysis/benchmark/all` | 📱 | 查询所有生效对标数据 |
| POST | `/monitor/analysis/benchmark` | ⚙️ | 新增对标数据 |
| PUT | `/monitor/analysis/benchmark/{id}` | ⚙️ | 修改对标数据 |
| DELETE | `/monitor/analysis/benchmark/{id}` | ⚙️ | 删除对标数据 |

---

#### 3.3.12 能碳预算管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/monitor/analysis/budget/list` | 📱 | 分页查询预算列表 |
| GET | `/monitor/analysis/budget/summary/year` | 📱 | 按年度汇总预算执行情况 |
| GET | `/monitor/analysis/budget/year/{year}` | 📱 | 按年度查询预算列表 |
| POST | `/monitor/analysis/budget` | ⚙️ | 新增预算 |
| PUT | `/monitor/analysis/budget/{id}` | ⚙️ | 修改预算 |
| DELETE | `/monitor/analysis/budget/{id}` | ⚙️ | 删除预算 |
| PUT | `/monitor/analysis/budget/{id}/completion` | ⚙️ | 更新预算完成率 |
| PUT | `/monitor/analysis/budget/completion/batch` | ⚙️ | 批量更新预算完成率 |

---

#### 3.3.13 设备对标

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/monitor/device-compare/compare` | 📱 | 双设备同时段曲线对比 |

请求参数：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| meterIdA | Long | 是 | 设备A ID |
| meterIdB | Long | 是 | 设备B ID |
| dataType | String | 否 | 数据类型 |
| startTime | String | 是 | 开始时间 |
| endTime | String | 是 | 结束时间 |

---

#### 3.3.14 时间对标

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/monitor/time-compare/compare` | 📱 | 同设备不同时间段曲线对比 |

请求参数：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| meterId | Long | 是 | 计量器具ID |
| dataType | String | 否 | 数据类型 |
| startTimeA | String | 是 | 时段A开始 |
| endTimeA | String | 是 | 时段A结束 |
| startTimeB | String | 是 | 时段B开始 |
| endTimeB | String | 是 | 时段B结束 |

---

#### 3.3.15 产品种类管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/monitor/productType/list` | 📱 | 分页查询产品种类 |
| GET | `/monitor/productType/all` | 📱 | 查询所有产品种类 |
| GET | `/monitor/productType/{id}` | | 根据ID查询产品种类 |
| POST | `/monitor/productType` | ⚙️ | 新增产品种类 |
| PUT | `/monitor/productType/{id}` | ⚙️ | 修改产品种类 |
| DELETE | `/monitor/productType/{id}` | ⚙️ | 删除产品种类 |
| PUT | `/monitor/productType/{id}/status` | ⚙️ | 修改产品种类状态 |
| GET | `/monitor/productType/export` | | 导出CSV |
| POST | `/monitor/productType/import` | ⚙️ | 导入 |

---

#### 3.3.16 班组管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/monitor/workShift/list` | 📱 | 分页查询班组列表 |
| GET | `/monitor/workShift/all` | 📱 | 查询所有班组 |
| GET | `/monitor/workShift/{id}` | | 根据ID查询班组 |
| POST | `/monitor/workShift` | ⚙️ | 新增班组 |
| PUT | `/monitor/workShift/{id}` | ⚙️ | 修改班组 |
| DELETE | `/monitor/workShift/{id}` | ⚙️ | 删除班组 |
| PUT | `/monitor/workShift/{id}/status` | ⚙️ | 修改班组状态 |

---

#### 3.3.17 能碳数据计算器

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| POST | `/toolkit/calculator/carbon` | 📱 | 碳排放计算 |
| POST | `/toolkit/calculator/electricity` | 📱 | 电量计算 |
| POST | `/toolkit/calculator/cost` | 📱 | 成本计算 |
| GET | `/toolkit/calculator/history` | 📱 | 查询计算历史记录 |
| DELETE | `/toolkit/calculator/{id}` | ⚙️ | 删除计算记录 |

**POST** `/toolkit/calculator/carbon`

📱 **移动端使用**

请求参数：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| activityData | Double | 是 | 活动数据 |
| emissionFactor | Double | 是 | 排放因子 |

响应数据：

| 字段 | 类型 | 说明 |
|------|------|------|
| activityData | Double | 活动数据 |
| emissionFactor | Double | 排放因子 |
| emission | BigDecimal | 排放量 |
| unit | String | 单位(tCO2) |

---

#### 3.3.18 分时电价管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/toolkit/electricity-price/list` | 📱 | 分页查询电价配置 |
| GET | `/toolkit/electricity-price/current` | 📱 | 获取当前生效电价 |
| GET | `/toolkit/electricity-price/{id}` | | 获取电价详情 |
| POST | `/toolkit/electricity-price` | ⚙️ | 新增电价配置 |
| PUT | `/toolkit/electricity-price/{id}` | ⚙️ | 修改电价配置 |
| PUT | `/toolkit/electricity-price/{id}/status` | ⚙️ | 启用/禁用电价 |
| DELETE | `/toolkit/electricity-price/{id}` | ⚙️ | 删除电价配置 |

---

#### 3.3.19 绿色工厂申报

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/toolkit/green-factory/list` | 📱 | 分页查询申报列表 |
| GET | `/toolkit/green-factory/{id}` | 📱 | 获取申报详情 |
| POST | `/toolkit/green-factory` | 📱 | 创建申报 |
| POST | `/toolkit/green-factory/{id}/upload-item` | 📱 | 上传资料 |
| PUT | `/toolkit/green-factory/{id}/progress` | 📱 | 更新进度 |
| PUT | `/toolkit/green-factory/{id}/submit` | 📱 | 提交审核 |
| DELETE | `/toolkit/green-factory/{id}` | ⚙️ | 删除申报 |

---

#### 3.3.20 错峰排产管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/toolkit/peak-shifting/list` | 📱 | 分页查询排产计划 |
| GET | `/toolkit/peak-shifting/config/list` | 📱 | 获取排产配置列表 |
| GET | `/toolkit/peak-shifting/suggestion` | 📱 | 智能排产建议 |
| POST | `/toolkit/peak-shifting` | ⚙️ | 新增排产计划 |
| PUT | `/toolkit/peak-shifting/{id}` | ⚙️ | 修改排产计划 |
| DELETE | `/toolkit/peak-shifting/{id}` | ⚙️ | 删除排产计划 |
| POST | `/toolkit/peak-shifting/config` | ⚙️ | 新增排产配置 |
| PUT | `/toolkit/peak-shifting/config/{id}` | ⚙️ | 修改排产配置 |
| DELETE | `/toolkit/peak-shifting/config/{id}` | ⚙️ | 删除排产配置 |

---

### 3.4 告警模块 (Alarm)

#### 3.4.1 报警记录管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/alarm/record/list` | 📱 | 分页查询报警记录 |
| GET | `/alarm/record/statistics` | 📱 | 获取报警统计 |
| GET | `/alarm/record/{id}` | 📱 | 获取报警详情 |
| PUT | `/alarm/record/{id}/handle` | 📱 | 处理报警记录 |
| PUT | `/alarm/record/batch-handle` | ⚙️ | 批量处理报警记录 |

**GET** `/alarm/record/list`

📱 **移动端使用**

请求参数：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| current | Long | 否 | 1 | 页码 |
| size | Long | 否 | 10 | 每页条数 |
| alarmLevel | String | 否 | | 报警级别(CRITICAL/WARN/INFO) |
| handleStatus | String | 否 | | 处理状态(UNHANDLED/HANDLED/IGNORED) |
| alarmType | String | 否 | | 报警类型 |
| startTime | String | 否 | | 开始时间 |
| endTime | String | 否 | | 结束时间 |

**GET** `/alarm/record/statistics`

📱 **移动端使用**

响应数据：

| 字段 | 类型 | 说明 |
|------|------|------|
| total | Long | 报警总数 |
| unhandled | Long | 未处理 |
| handled | Long | 已处理 |
| ignored | Long | 已忽略 |
| critical | Long | 严重级别数 |
| warn | Long | 警告级别数 |
| info | Long | 信息级别数 |

**PUT** `/alarm/record/{id}/handle`

📱 **移动端使用**

请求参数：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| handleStatus | String | 是 | 处理状态(HANDLED/IGNORED) |
| handler | String | 否 | 处理人 |
| handleRemark | String | 否 | 处理备注 |

---

#### 3.4.2 报警阈值管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/alarm/threshold/list` | 📱 | 分页查询阈值列表 |
| GET | `/alarm/threshold/enabled` | 📱 | 获取所有启用的阈值 |
| GET | `/alarm/threshold/{id}` | | 根据ID查询阈值 |
| POST | `/alarm/threshold` | ⚙️ | 新增阈值 |
| PUT | `/alarm/threshold/{id}` | ⚙️ | 修改阈值 |
| PUT | `/alarm/threshold/{id}/enabled` | ⚙️ | 启用/禁用阈值 |
| DELETE | `/alarm/threshold/{id}` | ⚙️ | 删除阈值 |

---

### 3.5 碳排放模块 (Carbon)

#### 3.5.1 碳排放核算

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/carbon/accounting/list` | 📱 | 分页查询核算列表 |
| GET | `/carbon/accounting/{id}` | 📱 | 获取核算详情（含明细） |
| GET | `/carbon/accounting/report/{id}` | 📱 | 获取排放报告数据 |
| POST | `/carbon/accounting` | ⚙️ | 新增核算 |
| PUT | `/carbon/accounting/{id}` | ⚙️ | 修改核算 |
| DELETE | `/carbon/accounting/{id}` | ⚙️ | 删除核算 |
| PUT | `/carbon/accounting/{id}/submit` | ⚙️ | 提交核算 |
| PUT | `/carbon/accounting/{id}/approve` | ⚙️ | 审核核算 |
| POST | `/carbon/accounting/calculate` | ⚙️ | 排放计算 |

**GET** `/carbon/accounting/report/{id}`

📱 **移动端使用**

响应数据：

| 字段 | 类型 | 说明 |
|------|------|------|
| accountingName | String | 核算名称 |
| accountingYear | Integer | 核算年份 |
| accountingPeriod | String | 核算期次 |
| scope1Emission | BigDecimal | 范围一排放量 |
| scope2Emission | BigDecimal | 范围二排放量 |
| scope3Emission | BigDecimal | 范围三排放量 |
| totalEmission | BigDecimal | 总排放量 |
| emissionUnit | String | 排放单位(tCO2e) |
| scope1Percent | BigDecimal | 范围一占比(%) |
| scope2Percent | BigDecimal | 范围二占比(%) |
| scope3Percent | BigDecimal | 范围三占比(%) |

---

#### 3.5.2 碳核算模型

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/carbon/model/list` | 📱 | 分页查询核算模型 |
| GET | `/carbon/model/{id}` | | 获取核算模型详情 |
| POST | `/carbon/model` | ⚙️ | 新增核算模型 |
| PUT | `/carbon/model/{id}` | ⚙️ | 修改核算模型 |
| DELETE | `/carbon/model/{id}` | ⚙️ | 删除核算模型 |

---

#### 3.5.3 碳资产管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/carbon/asset/list` | 📱 | 分页查询碳资产列表 |
| GET | `/carbon/asset/{id}` | 📱 | 获取碳资产详情 |
| GET | `/carbon/asset/summary` | 📱 | 资产汇总统计 |
| GET | `/carbon/asset/trade/list` | 📱 | 查询交易记录 |
| POST | `/carbon/asset` | ⚙️ | 新增碳资产 |
| PUT | `/carbon/asset/{id}` | ⚙️ | 修改碳资产 |
| DELETE | `/carbon/asset/{id}` | ⚙️ | 删除碳资产 |
| POST | `/carbon/asset/trade` | ⚙️ | 新增交易记录 |

**GET** `/carbon/asset/summary`

📱 **移动端使用**

响应数据：按类型统计（配额/CCER/绿证/总计），包含总数量、总价值、各状态数量。

---

#### 3.5.4 排放因子库

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/carbon/factor/list` | 📱 | 分页查询因子列表 |
| GET | `/carbon/factor/{id}` | | 获取因子详情 |
| GET | `/carbon/factor/category/{category}` | 📱 | 按类别查询因子列表 |
| GET | `/carbon/factor/energyType/{energyTypeId}` | 📱 | 按能源类型查询因子 |
| GET | `/carbon/factor/defaults` | 📱 | 获取默认因子列表 |
| POST | `/carbon/factor` | ⚙️ | 新增排放因子 |
| PUT | `/carbon/factor/{id}` | ⚙️ | 修改排放因子 |
| DELETE | `/carbon/factor/{id}` | ⚙️ | 删除排放因子 |
| PUT | `/carbon/factor/{id}/setDefault` | ⚙️ | 设为默认因子 |

---

#### 3.5.5 产品碳足迹

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/carbon/footprint/list` | 📱 | 分页查询碳足迹 |
| GET | `/carbon/footprint/{id}` | 📱 | 获取产品碳足迹详情 |
| GET | `/carbon/footprint/product/{productId}` | 📱 | 按产品查询碳足迹 |
| POST | `/carbon/footprint` | ⚙️ | 新增碳足迹记录 |
| PUT | `/carbon/footprint/{id}` | ⚙️ | 修改碳足迹记录 |
| DELETE | `/carbon/footprint/{id}` | ⚙️ | 删除碳足迹记录 |

---

#### 3.5.6 市场供需信息

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/carbon/market/list` | 📱 | 分页查询市场信息 |
| GET | `/carbon/market/{id}` | 📱 | 获取市场信息详情 |
| GET | `/carbon/market/latest` | 📱 | 获取最新市场信息 |
| POST | `/carbon/market` | ⚙️ | 发布市场信息 |
| PUT | `/carbon/market/{id}` | ⚙️ | 修改市场信息 |
| DELETE | `/carbon/market/{id}` | ⚙️ | 删除市场信息 |

---

#### 3.5.7 碳核查

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/carbon/verification/list` | 📱 | 分页查询碳核查列表 |
| GET | `/carbon/verification/{id}` | 📱 | 获取碳核查详情 |
| GET | `/carbon/verification/byAccounting/{accountingId}` | 📱 | 按核算查询核查记录 |
| POST | `/carbon/verification` | ⚙️ | 新增碳核查 |
| PUT | `/carbon/verification/{id}` | ⚙️ | 修改碳核查 |
| DELETE | `/carbon/verification/{id}` | ⚙️ | 删除碳核查 |
| PUT | `/carbon/verification/{id}/complete` | ⚙️ | 提交核查结果 |

---

#### 3.5.8 CBAM申报管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/carbon/cbam/list` | 📱 | 分页查询CBAM申报列表 |
| GET | `/carbon/cbam/{id}` | 📱 | 获取CBAM申报详情（含材料明细） |
| POST | `/carbon/cbam` | ⚙️ | 新增CBAM申报 |
| PUT | `/carbon/cbam/{id}` | ⚙️ | 修改CBAM申报 |
| DELETE | `/carbon/cbam/{id}` | ⚙️ | 删除CBAM申报 |
| POST | `/carbon/cbam/calculate/{id}` | ⚙️ | 排放计算 |
| PUT | `/carbon/cbam/{id}/submit` | ⚙️ | 提交审核 |
| PUT | `/carbon/cbam/{id}/approve` | ⚙️ | 审核通过 |
| PUT | `/carbon/cbam/{id}/reject` | ⚙️ | 审核驳回 |

---

#### 3.5.9 供应链碳足迹管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/carbon/supply-chain/list` | 📱 | 分页查询供应链碳足迹列表 |
| GET | `/carbon/supply-chain/{id}` | 📱 | 获取供应链碳足迹详情 |
| GET | `/carbon/supply-chain/summary/supplier` | 📱 | 按供应商汇总统计 |
| GET | `/carbon/supply-chain/summary/product` | 📱 | 按产品汇总统计 |
| POST | `/carbon/supply-chain` | ⚙️ | 新增供应链碳足迹 |
| PUT | `/carbon/supply-chain/{id}` | ⚙️ | 修改供应链碳足迹 |
| DELETE | `/carbon/supply-chain/{id}` | ⚙️ | 删除供应链碳足迹 |

---

### 3.6 报表模块 (Report)

#### 3.6.1 能耗统计报表

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/report/energy/summary/list` | 📱 | 分页查询能耗统计 |
| GET | `/report/energy/summary/range` | 📱 | 按日期范围查询能耗统计 |
| POST | `/report/energy/summary` | ⚙️ | 新增能耗统计 |
| DELETE | `/report/energy/summary/{id}` | ⚙️ | 删除能耗统计 |
| GET | `/report/energy/summary/export` | | 导出CSV |

---

#### 3.6.2 能耗汇总报表

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/report/energy/total/list` | 📱 | 分页查询能耗汇总 |
| GET | `/report/energy/total/{id}` | | 获取能耗汇总详情 |
| GET | `/report/energy/total/range` | 📱 | 按日期范围查询汇总 |
| POST | `/report/energy/total` | ⚙️ | 新增能耗汇总 |
| DELETE | `/report/energy/total/{id}` | ⚙️ | 删除能耗汇总 |

---

#### 3.6.3 用能成本统计

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/report/cost/list` | 📱 | 分页查询成本统计 |
| GET | `/report/cost/{id}` | | 获取成本统计详情 |
| GET | `/report/cost/range` | 📱 | 按日期范围查询成本 |
| POST | `/report/cost` | ⚙️ | 新增成本统计 |
| DELETE | `/report/cost/{id}` | ⚙️ | 删除成本统计 |

---

#### 3.6.4 节能量统计

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/report/energy/saving/list` | 📱 | 分页查询节能量统计 |
| GET | `/report/energy/saving/{id}` | | 获取节能量详情 |
| GET | `/report/energy/saving/range` | 📱 | 按日期范围查询节能量 |
| POST | `/report/energy/saving` | ⚙️ | 新增节能量统计 |
| DELETE | `/report/energy/saving/{id}` | ⚙️ | 删除节能量统计 |

---

#### 3.6.5 基准能耗

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/report/baseline/list` | 📱 | 分页查询基准能耗 |
| GET | `/report/baseline/{id}` | | 获取基准能耗详情 |
| POST | `/report/baseline` | ⚙️ | 新增基准能耗 |
| PUT | `/report/baseline/{id}` | ⚙️ | 修改基准能耗 |
| DELETE | `/report/baseline/{id}` | ⚙️ | 删除基准能耗 |

---

#### 3.6.6 指标录入

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/entry/indicator/list` | 📱 | 分页查询指标列表 |
| GET | `/entry/indicator/{id}` | | 获取指标详情 |
| POST | `/entry/indicator` | 📱 | 新增指标 |
| PUT | `/entry/indicator/{id}` | ⚙️ | 修改指标 |
| DELETE | `/entry/indicator/{id}` | ⚙️ | 删除指标 |
| POST | `/entry/indicator/import` | ⚙️ | Excel导入指标 |

---

#### 3.6.7 产量录入

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/entry/production/list` | 📱 | 分页查询产量记录 |
| GET | `/entry/production/{id}` | | 获取产量详情 |
| GET | `/entry/production/range` | 📱 | 按日期范围查询产量 |
| POST | `/entry/production` | 📱 | 新增产量记录 |
| PUT | `/entry/production/{id}` | ⚙️ | 修改产量记录 |
| DELETE | `/entry/production/{id}` | ⚙️ | 删除产量记录 |

---

### 3.7 AI服务模块

#### 3.7.1 AI知识库管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/ai/knowledge/list` | 📱 | 分页查询文档列表 |
| GET | `/ai/knowledge/search` | 📱 | 文档检索 |
| GET | `/ai/knowledge/{id}` | 📱 | 获取文档详情 |
| POST | `/ai/knowledge/upload` | ⚙️ | 上传文档 |
| DELETE | `/ai/knowledge/{id}` | ⚙️ | 删除文档 |

---

#### 3.7.2 AI报告生成管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/ai/report/list` | 📱 | 分页查询报告任务列表 |
| GET | `/ai/report/{id}` | 📱 | 查询任务状态 |
| POST | `/ai/report` | 📱 | 创建报告任务 |
| PUT | `/ai/report/{id}/cancel` | 📱 | 取消任务 |
| DELETE | `/ai/report/{id}` | ⚙️ | 删除报告任务 |

---

#### 3.7.3 AI报告模板管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/ai/report-template/list` | 📱 | 分页查询模板列表 |
| GET | `/ai/report-template/all` | 📱 | 获取所有模板 |
| GET | `/ai/report-template/{id}` | 📱 | 获取模板详情 |
| POST | `/ai/report-template` | ⚙️ | 新增模板 |
| PUT | `/ai/report-template/{id}` | ⚙️ | 修改模板 |
| DELETE | `/ai/report-template/{id}` | ⚙️ | 删除模板 |

---

#### 3.7.4 AI服务预约管理

| 方法 | 路径 | 移动端 | 说明 |
|------|------|--------|------|
| GET | `/ai/service/list` | 📱 | 分页查询服务预约列表 |
| GET | `/ai/service/{id}` | 📱 | 获取预约详情 |
| POST | `/ai/service` | 📱 | 新增服务预约 |
| PUT | `/ai/service/{id}/cancel` | 📱 | 取消预约 |
| PUT | `/ai/service/{id}/status` | ⚙️ | 更新预约状态 |
| DELETE | `/ai/service/{id}` | ⚙️ | 删除预约记录 |

---

## 四、错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 401 | 未认证（Token缺失或无效） |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 五、移动端核心接口速查

以下为移动端 App 开发中最常用的核心接口汇总：

| 功能模块 | 接口 | 方法 |
|----------|------|------|
| 登录 | `/auth/login` | POST |
| 获取用户信息 | `/auth/userinfo` | GET |
| 实时监测数据 | `/monitor/realtime/list` | GET |
| 设备在线率 | `/monitor/status/online-rate` | GET |
| 设备最新状态 | `/monitor/status/latest` | GET |
| 历史数据曲线 | `/monitor/history/chart` | GET |
| 能耗汇总 | `/monitor/analysis/consumption/summary` | GET |
| 能耗趋势 | `/monitor/analysis/consumption/trend` | GET |
| 报警统计 | `/alarm/record/statistics` | GET |
| 报警列表 | `/alarm/record/list` | GET |
| 处理报警 | `/alarm/record/{id}/handle` | PUT |
| 碳排放报告 | `/carbon/accounting/report/{id}` | GET |
| 碳资产汇总 | `/carbon/asset/summary` | GET |
| 排放因子查询 | `/carbon/factor/defaults` | GET |
| 能耗统计报表 | `/report/energy/summary/range` | GET |
| 抄表录入 | `/monitor/meter-record` | POST |
| 产量录入 | `/entry/production` | POST |
| 指标录入 | `/entry/indicator` | POST |
| AI报告创建 | `/ai/report` | POST |
| 计算器-碳排放 | `/toolkit/calculator/carbon` | POST |
| 智能排产建议 | `/toolkit/peak-shifting/suggestion` | GET |
| 当前电价 | `/toolkit/electricity-price/current` | GET |
