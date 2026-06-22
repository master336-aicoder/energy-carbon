import request from '@/utils/request'
import type { Result, PageResult } from '@/types'

// ========== 绿色工厂申报 ==========

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

export interface GreenFactoryParams {
  current?: number
  size?: number
  status?: string
  declarationYear?: number
}

/** 分页查询申报列表 */
export function getGreenFactoryList(params: GreenFactoryParams) {
  return request.get<Result<PageResult<GreenFactory>>>('/toolkit/green-factory/list', { params })
}

/** 创建申报 */
export function createGreenFactory(data: GreenFactory) {
  return request.post<Result<GreenFactory>>('/toolkit/green-factory', data)
}

/** 获取申报详情 */
export function getGreenFactoryDetail(id: number) {
  return request.get<Result<GreenFactory>>(`/toolkit/green-factory/${id}`)
}

/** 上传资料 */
export function uploadGreenFactoryItem(id: number, itemId: number, file: File) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('itemId', String(itemId))
  return request.post<Result<null>>(`/toolkit/green-factory/${id}/upload-item`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/** 更新进度 */
export function updateGreenFactoryProgress(id: number, progress: number) {
  return request.put<Result<null>>(`/toolkit/green-factory/${id}/progress`, null, { params: { progress } })
}

/** 提交审核 */
export function submitGreenFactory(id: number) {
  return request.put<Result<null>>(`/toolkit/green-factory/${id}/submit`)
}

/** 删除申报 */
export function deleteGreenFactory(id: number) {
  return request.delete<Result<null>>(`/toolkit/green-factory/${id}`)
}

// ========== 错峰排产 ==========

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

export interface PeakShiftingParams {
  current?: number
  size?: number
  productionLine?: string
  startDate?: string
  endDate?: string
}

/** 分页查询排产计划列表 */
export function getPeakShiftingList(params: PeakShiftingParams) {
  return request.get<Result<PageResult<PeakShifting>>>('/toolkit/peak-shifting/list', { params })
}

/** 新增排产计划 */
export function addPeakShifting(data: PeakShifting) {
  return request.post<Result<null>>('/toolkit/peak-shifting', data)
}

/** 修改排产计划 */
export function updatePeakShifting(id: number, data: PeakShifting) {
  return request.put<Result<null>>(`/toolkit/peak-shifting/${id}`, data)
}

/** 删除排产计划 */
export function deletePeakShifting(id: number) {
  return request.delete<Result<null>>(`/toolkit/peak-shifting/${id}`)
}

/** 获取排产配置列表 */
export function getPeakShiftingConfigList() {
  return request.get<Result<PeakShiftingConfig[]>>('/toolkit/peak-shifting/config/list')
}

/** 新增排产配置 */
export function addPeakShiftingConfig(data: PeakShiftingConfig) {
  return request.post<Result<null>>('/toolkit/peak-shifting/config', data)
}

/** 修改排产配置 */
export function updatePeakShiftingConfig(id: number, data: PeakShiftingConfig) {
  return request.put<Result<null>>(`/toolkit/peak-shifting/config/${id}`, data)
}

/** 删除排产配置 */
export function deletePeakShiftingConfig(id: number) {
  return request.delete<Result<null>>(`/toolkit/peak-shifting/config/${id}`)
}

/** 智能排产建议 */
export function getPeakShiftingSuggestion(productionLine: string, planDate: string) {
  return request.get<Result<Record<string, unknown>>>('/toolkit/peak-shifting/suggestion', {
    params: { productionLine, planDate }
  })
}

// ========== 能碳数据计算器 ==========

export interface CalculatorRecord {
  id?: number
  calcName: string
  calcType: string
  inputParamsJson?: string
  resultJson?: string
  createTime?: string
}

export interface CalculatorParams {
  current?: number
  size?: number
  calcType?: string
}

/** 碳排放计算 */
export function calcCarbon(data: { activityData: number; emissionFactor: number }) {
  return request.post<Result<Record<string, unknown>>>('/toolkit/calculator/carbon', data)
}

/** 电量计算 */
export function calcElectricity(data: { power: number; hours: number }) {
  return request.post<Result<Record<string, unknown>>>('/toolkit/calculator/electricity', data)
}

/** 成本计算 */
export function calcCost(data: {
  consumption: number
  peakPrice: number
  flatPrice: number
  valleyPrice: number
  peakRatio?: number
  flatRatio?: number
  valleyRatio?: number
}) {
  return request.post<Result<Record<string, unknown>>>('/toolkit/calculator/cost', data)
}

/** 查询计算历史记录 */
export function getCalculatorHistory(params: CalculatorParams) {
  return request.get<Result<PageResult<CalculatorRecord>>>('/toolkit/calculator/history', { params })
}

/** 删除计算记录 */
export function deleteCalculatorRecord(id: number) {
  return request.delete<Result<null>>(`/toolkit/calculator/${id}`)
}

// ========== 分时电价 ==========

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

export interface ElectricityPriceParams {
  current?: number
  size?: number
}

/** 分页查询电价配置列表 */
export function getElectricityPriceList(params: ElectricityPriceParams) {
  return request.get<Result<PageResult<ElectricityPrice>>>('/toolkit/electricity-price/list', { params })
}

/** 获取当前生效电价 */
export function getCurrentElectricityPrice() {
  return request.get<Result<ElectricityPrice>>('/toolkit/electricity-price/current')
}

/** 获取电价详情 */
export function getElectricityPriceDetail(id: number) {
  return request.get<Result<ElectricityPrice>>(`/toolkit/electricity-price/${id}`)
}

/** 新增电价配置 */
export function addElectricityPrice(data: ElectricityPrice) {
  return request.post<Result<null>>('/toolkit/electricity-price', data)
}

/** 修改电价配置 */
export function updateElectricityPrice(id: number, data: ElectricityPrice) {
  return request.put<Result<null>>(`/toolkit/electricity-price/${id}`, data)
}

/** 启用/禁用电价 */
export function setElectricityPriceStatus(id: number, status: number) {
  return request.put<Result<null>>(`/toolkit/electricity-price/${id}/status`, null, { params: { status } })
}

/** 删除电价配置 */
export function deleteElectricityPrice(id: number) {
  return request.delete<Result<null>>(`/toolkit/electricity-price/${id}`)
}
