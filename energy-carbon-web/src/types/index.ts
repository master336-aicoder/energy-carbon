/** 通用响应结果 */
export interface Result<T = unknown> {
  code: number
  message: string
  data: T
}

/** 分页参数 */
export interface PageParams {
  current?: number
  size?: number
}

/** 分页结果 */
export interface PageResult<T> {
  records: T[]
  total: number
  current: number
  size: number
  pages: number
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

// ==================== 碳排放模块类型 ====================

/** 碳排放核算 */
export interface CarbonAccounting {
  id: number
  accountingName: string
  accountingYear: number
  accountingPeriod: string
  energyUnitId?: number
  scopeType?: string
  totalEmission?: number
  emissionUnit?: string
  status: string
  remark?: string
  details?: CarbonAccountingDetail[]
  createTime?: string
  updateTime?: string
}

/** 核算明细 */
export interface CarbonAccountingDetail {
  id: number
  accountingId: number
  activityName: string
  energyTypeId?: number
  activityData?: number
  activityUnit?: string
  emissionFactor?: number
  factorUnit?: string
  emission?: number
  emissionUnit?: string
  sourceDescription?: string
}

/** 排放因子 */
export interface EmissionFactor {
  id: number
  factorName: string
  factorCategory: string
  energyTypeId?: number
  factorValue: number
  factorUnit: string
  source?: string
  effectiveYear?: number
  defaultFlag?: number
  remark?: string
}

/** 碳核算模型 */
export interface AccountingModel {
  id: number
  modelName: string
  energyUnitId?: number
  modelConfig?: string
  status: number
  remark?: string
}

/** 产品碳足迹 */
export interface CarbonFootprint {
  id: number
  productId?: number
  lifecycleStage?: string
  emission?: number
  emissionUnit?: string
  calcMethod?: string
  dataSource?: string
  calcDate?: string
}

/** 碳核查 */
export interface CarbonVerification {
  id: number
  verificationName: string
  accountingId?: number
  verificationType?: string
  verifier?: string
  verificationDate?: string
  dataSourceDescription?: string
  attachmentPath?: string
  verificationResult?: string
  status: string
  remark?: string
}

// ==================== 统计报表模块类型 ====================

/** 能耗统计 */
export interface EnergySummary {
  id: number
  energyUnitId?: number
  reportType: string
  statDate: string
  energyTypeId?: number
  consumption?: number
  consumptionUnit?: string
  cost?: number
  costUnit?: string
}

/** 能耗汇总 */
export interface EnergyTotal {
  id: number
  energyUnitId?: number
  reportType: string
  statDate: string
  totalConsumption?: number
  totalCost?: number
  comparisonPeriod?: string
  comparisonChangeRate?: number
}

/** 节能量 */
export interface EnergySaving {
  id: number
  energyUnitId?: number
  reportType: string
  statDate: string
  baselineConsumption?: number
  actualConsumption?: number
  savingAmount?: number
  savingRate?: number
  savingAmountUnit?: string
}

/** 用能成本 */
export interface CostStat {
  id: number
  energyUnitId?: number
  reportType: string
  statDate: string
  energyTypeId?: number
  cost?: number
  costUnit?: string
}

/** 基准能耗 */
export interface BaselineConsumption {
  id: number
  energyUnitId?: number
  startDate: string
  endDate: string
  baselineValue?: number
  baselineType?: string
  remark?: string
}

// ==================== 数据录入模块类型 ====================

/** 指标录入 */
export interface EntryIndicator {
  id: number
  energyUnitId?: number
  indicatorName: string
  indicatorType?: string
  indicatorValue?: number
  indicatorUnit?: string
  entryDate: string
  entryPerson?: string
  remark?: string
}

/** 产量录入 */
export interface EntryProduction {
  id: number
  energyUnitId?: number
  productTypeId?: number
  productionDate: string
  outputQuantity?: number
  outputUnit?: string
  batchNumber?: string
  remark?: string
  entryPerson?: string
}

// ========== 在线监测 - 历史数据 ==========

/** 历史数据 */
export interface MonitorHistoryData {
  id?: number
  meterId: number
  meterName?: string
  dataTime: string
  dataValue: number
  dataType: string
  unit?: string
  createTime?: string
}

/** 历史数据查询参数 */
export interface MonitorHistoryParams {
  current?: number
  size?: number
  meterId?: number
  dataType?: string
  startTime: string
  endTime: string
}

// ========== 在线监测 - 计量抄表 ==========

/** 抄表记录 */
export interface MeterRecord {
  id?: number
  meterId: number
  meterName?: string
  meterCode?: string
  recordTime: string
  startCode?: number
  endCode: number
  consumption: number
  cost?: number
  unitPrice?: number
  operator?: string
  remark?: string
  createTime?: string
}

/** 抄表记录查询参数 */
export interface MeterRecordParams {
  current?: number
  size?: number
  meterId?: number
  startTime?: string
  endTime?: string
}

// ========== 在线监测 - 状态监测 ==========

/** 状态日志 */
export interface StatusLog {
  id?: number
  deviceId: number
  deviceName?: string
  deviceType: string
  onlineStatus: number
  offlineTime?: string
  onlineTime?: string
  duration?: number
  createTime?: string
}

/** 在线率统计 */
export interface OnlineRate {
  total: number
  online: number
  offline: number
  onlineRate: number
}

// ========== 在线监测 - 对比数据 ==========

/** 对比数据点 */
export interface CompareDataPoint {
  time: string
  value: number
}

/** 对比数据系列 */
export interface CompareDataSeries {
  seriesName: string
  color: string
  data: CompareDataPoint[]
}

// ========== 报警管理 - 阈值 ==========

/** 报警阈值 */
export interface AlarmThreshold {
  id?: number
  thresholdName: string
  energyUnitId?: number
  energyUnitName?: string
  metricType: string
  compareType: string
  thresholdValue: number
  alarmLevel: string
  enabled: number
  notifyMethod?: string
  remark?: string
  createTime?: string
  updateTime?: string
}

/** 阈值查询参数 */
export interface AlarmThresholdParams {
  current?: number
  size?: number
  thresholdName?: string
  metricType?: string
  alarmLevel?: string
  enabled?: number
}

// ========== 报警管理 - 记录 ==========

/** 报警记录 */
export interface AlarmRecord {
  id?: number
  thresholdId?: number
  thresholdName?: string
  meterId?: number
  meterName?: string
  alarmType: string
  alarmLevel: string
  alarmValue: number
  thresholdValue: number
  alarmTime: string
  handleStatus: string
  handler?: string
  handleTime?: string
  handleRemark?: string
  createTime?: string
}

/** 报警记录查询参数 */
export interface AlarmRecordParams {
  current?: number
  size?: number
  alarmLevel?: string
  handleStatus?: string
  alarmType?: string
  startTime?: string
  endTime?: string
}

/** 报警统计 */
export interface AlarmStatistics {
  total: number
  unhandled: number
  handled: number
  ignored: number
  critical: number
  warn: number
  info: number
}

// ========== 用能分析 - 能耗数据 ==========

/** 能耗数据 */
export interface AnalysisConsumption {
  id: number
  energyUnitId: number
  energyUnitName?: string
  meterId?: number
  meterName?: string
  energyTypeId?: number
  energyTypeName?: string
  statDate: string
  statPeriod: string
  consumption: number
  unit: string
  createTime?: string
}

/** 电费数据 */
export interface AnalysisElectricityCost {
  id: number
  meterId?: number
  meterName?: string
  statDate: string
  peakPower: number
  valleyPower: number
  flatPower: number
  peakCost: number
  valleyCost: number
  flatCost: number
  totalCost: number
  createTime?: string
}

/** 能效数据 */
export interface AnalysisEnergyEfficiency {
  id: number
  energyUnitId: number
  energyUnitName?: string
  statDate: string
  outputValue: number
  outputUnit: string
  energyConsumption: number
  energyUnit: string
  intensity: number
  benchmarkValue?: number
  benchmarkUnit?: string
  createTime?: string
}

/** 能碳预算 */
export interface AnalysisEnergyBudget {
  id: number
  energyUnitId: number
  energyUnitName?: string
  budgetYear: number
  budgetType: string
  budgetTypeName?: string
  targetValue: number
  targetUnit: string
  currentValue: number
  completionRate: number
  status: number
  createTime?: string
  updateTime?: string
}

/** 对标库 */
export interface AnalysisBenchmark {
  id: number
  benchmarkName: string
  industry?: string
  energyTypeId?: number
  energyTypeName?: string
  energyCategory?: string
  benchmarkValue: number
  benchmarkUnit: string
  source?: string
  effectiveDate?: string
  expireDate?: string
  remark?: string
  createTime?: string
  updateTime?: string
}

// ========== AI服务模块类型 ==========

/** AI服务预约记录 */
export interface AiServiceRecord {
  id?: number
  serviceName: string
  serviceType: string
  applicant: string
  contact?: string
  description?: string
  expectedDate?: string
  status: string
  result?: string
  remark?: string
  createTime?: string
  updateTime?: string
}

/** AI知识库文档 */
export interface AiKnowledgeDoc {
  id?: number
  docName: string
  docType: string
  filePath?: string
  fileSize?: number
  summary?: string
  uploadUser?: string
  uploadTime?: string
  status?: number
  createTime?: string
}

/** AI报告生成任务 */
export interface AiReportTask {
  id?: number
  reportName: string
  reportType: string
  templateId?: number
  templateName?: string
  paramsJson?: string
  status: string
  resultPath?: string
  errorMsg?: string
  progress?: number
  createTime?: string
  updateTime?: string
}

/** AI报告模板 */
export interface AiReportTemplate {
  id?: number
  templateName: string
  reportType: string
  templateContent?: string
  variables?: string
  status?: number
  createTime?: string
  updateTime?: string
}

// ========== 工具包模块类型 ==========

/** 绿色工厂申报 */
export interface GreenFactory {
  id?: number
  declarationName: string
  enterpriseName: string
  declarationYear: number
  status: string
  progress?: number
  submitTime?: string
  remark?: string
  createTime?: string
  updateTime?: string
  items?: GreenFactoryItem[]
}

/** 绿色工厂申报资料清单 */
export interface GreenFactoryItem {
  id?: number
  declarationId?: number
  itemName: string
  itemCategory?: string
  required?: number
  uploaded?: number
  filePath?: string
  uploadTime?: string
  remark?: string
}

/** 错峰排产 */
export interface PeakShifting {
  id?: number
  planName: string
  planDate: string
  shiftType?: string
  productionLine?: string
  targetOutput?: number
  actualOutput?: number
  energyConsumption?: number
  energyCost?: number
  savingRate?: number
  status?: number
  createTime?: string
  updateTime?: string
}

/** 排产配置 */
export interface PeakShiftingConfig {
  id?: number
  configName: string
  peakHoursStart?: string
  peakHoursEnd?: string
  valleyHoursStart?: string
  valleyHoursEnd?: string
  flatHoursStart?: string
  flatHoursEnd?: string
  maxPower?: number
  priority?: number
  status?: number
  createTime?: string
  updateTime?: string
}

/** 计算器记录 */
export interface CalculatorRecord {
  id?: number
  calcName: string
  calcType: string
  inputParamsJson?: string
  resultJson?: string
  createTime?: string
}

/** 分时电价配置 */
export interface ElectricityPrice {
  id?: number
  priceName: string
  effectiveDate: string
  peakPrice: number
  flatPrice: number
  valleyPrice: number
  peakHours?: string
  flatHours?: string
  valleyHours?: string
  status?: number
  createTime?: string
  updateTime?: string
}

// ==================== 碳资产管理模块类型 ====================

/** 碳资产 */
export interface CarbonAsset {
  id: number
  assetType: string
  assetName: string
  assetCode?: string
  quantity: number
  unit?: string
  acquireDate?: string
  expireDate?: string
  source?: string
  cost?: number
  status: string
  remark?: string
  createTime?: string
  updateTime?: string
}

/** 碳资产交易 */
export interface CarbonAssetTrade {
  id: number
  assetId: number
  assetName?: string
  tradeType: string
  tradeQuantity: number
  tradePrice: number
  tradeAmount: number
  tradeDate: string
  counterparty?: string
  status?: string
  remark?: string
  createTime?: string
}

/** 资产汇总项 */
export interface AssetSummaryItem {
  assetType: string
  assetTypeName: string
  totalQuantity: number
  totalCost: number
  count: number
  availableCount?: number
  usedCount?: number
  frozenCount?: number
}

/** 市场供需信息 */
export interface CarbonMarketInfo {
  id: number
  infoTitle: string
  infoCategory: string
  content?: string
  source?: string
  publishDate?: string
  status: number
  createTime?: string
  updateTime?: string
}

// ==================== CBAM申报模块类型 ====================

/** CBAM申报 */
export interface CbamDeclaration {
  id: number
  declarationName: string
  declarationPeriod: string
  productName: string
  productCode?: string
  cnCode?: string
  originCountry?: string
  importQuantity: number
  unit?: string
  directEmission?: number
  indirectEmission?: number
  totalEmission?: number
  emissionUnit?: string
  certificateNumber?: string
  status: string
  remark?: string
  details?: CbamDeclarationDetail[]
  createTime?: string
  updateTime?: string
}

/** CBAM明细 */
export interface CbamDeclarationDetail {
  id: number
  declarationId: number
  materialName: string
  materialCategory?: string
  consumption?: number
  unit?: string
  emissionFactor?: number
  emission?: number
  dataSource?: string
}

// ==================== 供应链碳足迹模块类型 ====================

/** 供应链碳足迹 */
export interface SupplyChainCarbon {
  id: number
  supplierName: string
  supplierCode?: string
  productName: string
  productCategory?: string
  carbonFootprintValue: number
  unit?: string
  certType?: string
  certNumber?: string
  validDate?: string
  dataSource?: string
  status: number
  remark?: string
  createTime?: string
  updateTime?: string
}

/** 供应链汇总项 */
export interface SupplyChainSummaryItem {
  supplierName?: string
  productName?: string
  productCount?: number
  supplierCount?: number
  avgCarbonFootprint: number
}
