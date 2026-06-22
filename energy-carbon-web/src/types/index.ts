/** 通用响应结果 */
export interface Result<T = unknown> {
  code: number
  message: string
  data: T
}

/** 分页参数 */
export interface PageParams {
  pageNum?: number
  pageSize?: number
}

/** 分页结果 */
export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
}

/** 用户 */
export interface User {
  id: number
  username: string
  nickname?: string
  avatar?: string
  email?: string
  phone?: string
  status: number
  deptId?: number
  deptName?: string
  roleIds?: number[]
  roles?: Role[]
  createTime?: string
  updateTime?: string
}

/** 角色 */
export interface Role {
  id: number
  name: string
  code: string
  description?: string
  status: number
  sort?: number
  createTime?: string
  updateTime?: string
}

/** 部门 */
export interface Dept {
  id: number
  parentId: number
  name: string
  code?: string
  sort?: number
  status: number
  children?: Dept[]
  createTime?: string
  updateTime?: string
}

/** 菜单 */
export interface Menu {
  id: number
  parentId: number
  name: string
  title: string
  path?: string
  component?: string
  icon?: string
  type: MenuType
  permission?: string
  sort?: number
  status: number
  hidden?: boolean
  keepAlive?: boolean
  external?: boolean
  externalLink?: string
  children?: Menu[]
  createTime?: string
  updateTime?: string
}

/** 菜单类型 */
export type MenuType = 'directory' | 'menu' | 'button'

/** 路由元信息 */
export interface RouteMeta {
  title: string
  icon?: string
  hidden?: boolean
  keepAlive?: boolean
  permission?: string
  [key: string]: unknown
}

/** 前端路由 */
export interface AppRoute {
  path: string
  name?: string
  component?: string
  redirect?: string
  meta?: RouteMeta
  children?: AppRoute[]
}

/** 登录表单 */
export interface LoginForm {
  username: string
  password: string
  captcha?: string
  captchaKey?: string
}

/** 登录结果 */
export interface LoginResult {
  accessToken: string
  refreshToken: string
  expiresIn: number
  tokenType: string
}

/** 用户信息 */
export interface UserInfo {
  id: number
  username: string
  nickname?: string
  avatar?: string
  email?: string
  phone?: string
  deptName?: string
  roles: string[]
  permissions: string[]
  menus: Menu[]
}

/** 字典项 */
export interface DictItem {
  label: string
  value: string | number
  color?: string
  tagType?: 'success' | 'info' | 'warning' | 'danger'
}

/** 字典 */
export interface Dict {
  code: string
  name: string
  items: DictItem[]
}

/** 用能设备 */
export interface Device {
  id: number
  code: string
  name: string
  type: string
  deptId: number
  deptName?: string
  manufacturer?: string
  model?: string
  power?: number
  powerUnit?: string
  voltage?: number
  current?: number
  status: number
  remark?: string
  createTime?: string
  updateTime?: string
}

/** 计量器具 */
export interface Meter {
  id: number
  code: string
  name: string
  type: string
  deviceId?: number
  deviceName?: string
  deptId: number
  deptName?: string
  accuracy?: string
  rangeMin?: number
  rangeMax?: number
  rangeUnit?: string
  status: number
  remark?: string
  createTime?: string
  updateTime?: string
}

/** 能源品种 */
export interface EnergyType {
  id: number
  code: string
  name: string
  category: string
  unit: string
  conversionFactor?: number
  conversionUnit?: string
  carbonFactor?: number
  status: number
  remark?: string
  createTime?: string
  updateTime?: string
}

/** 班组 */
export interface WorkShift {
  id: number
  code: string
  name: string
  deptId: number
  deptName?: string
  leader?: string
  leaderPhone?: string
  startTime?: string
  endTime?: string
  status: number
  remark?: string
  createTime?: string
  updateTime?: string
}

/** 产品种类 */
export interface ProductType {
  id: number
  code: string
  name: string
  category: string
  unit: string
  yieldUnit?: string
  status: number
  remark?: string
  createTime?: string
  updateTime?: string
}

/** 物联网网关 */
export interface IotGateway {
  id: number
  code: string
  name: string
  protocol: string
  ip?: string
  port?: number
  status: number
  remark?: string
  createTime?: string
  updateTime?: string
}

/** 物联网设备 */
export interface IotDevice {
  id: number
  code: string
  name: string
  gatewayId: number
  gatewayName?: string
  deviceType: string
  protocol: string
  status: number
  remark?: string
  createTime?: string
  updateTime?: string
}

/** 实时数据 */
export interface RealTimeData {
  id?: number
  deviceId?: number
  meterId?: number
  meterName?: string
  dataType: string
  value: number
  unit?: string
  timestamp: string
  status?: number
}
