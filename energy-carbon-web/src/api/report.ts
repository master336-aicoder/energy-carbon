import request from '@/utils/request'
import type { Result, PageResult } from '@/types'

// ==================== 能耗统计 ====================

export interface EnergySummary {
  id: number
  energyUnitId: number
  reportType: string
  statDate: string
  energyTypeId: number
  consumption: number
  consumptionUnit: string
  cost: number
  costUnit: string
  createTime: string
}

export interface EnergySummaryListParams {
  current?: number
  size?: number
  reportType?: string
  statDate?: string
  energyTypeId?: number
}

/** 获取能耗统计列表 */
export function getEnergySummaryList(params: EnergySummaryListParams) {
  return request.get<Result<PageResult<EnergySummary>>>('/report/energy/summary/list', { params })
}

/** 按日期范围查询能耗统计 */
export function getEnergySummaryByRange(reportType: string, startDate: string, endDate: string) {
  return request.get<Result<EnergySummary[]>>('/report/energy/summary/range', {
    params: { reportType, startDate, endDate }
  })
}

/** 新增能耗统计 */
export function createEnergySummary(data: Partial<EnergySummary>) {
  return request.post<Result<null>>('/report/energy/summary', data)
}

/** 删除能耗统计 */
export function deleteEnergySummary(id: number) {
  return request.delete<Result<null>>(`/report/energy/summary/${id}`)
}

/** 导出能耗统计 */
export function exportEnergySummary(params: { reportType?: string; startDate?: string; endDate?: string }) {
  return request.get('/report/energy/summary/export', {
    params,
    responseType: 'blob'
  })
}

// ==================== 能耗汇总 ====================

export interface EnergyTotal {
  id: number
  energyUnitId: number
  reportType: string
  statDate: string
  totalConsumption: number
  totalCost: number
  comparisonPeriod: string
  comparisonChangeRate: number
  createTime: string
}

/** 获取能耗汇总列表 */
export function getEnergyTotalList(params: { current?: number; size?: number; reportType?: string; statDate?: string }) {
  return request.get<Result<PageResult<EnergyTotal>>>('/report/energy/total/list', { params })
}

/** 按日期范围查询汇总 */
export function getEnergyTotalByRange(reportType: string, startDate: string, endDate: string) {
  return request.get<Result<EnergyTotal[]>>('/report/energy/total/range', {
    params: { reportType, startDate, endDate }
  })
}

// ==================== 节能量统计 ====================

export interface EnergySaving {
  id: number
  energyUnitId: number
  reportType: string
  statDate: string
  baselineConsumption: number
  actualConsumption: number
  savingAmount: number
  savingRate: number
  savingAmountUnit: string
  createTime: string
}

/** 获取节能量列表 */
export function getEnergySavingList(params: { current?: number; size?: number; reportType?: string; statDate?: string }) {
  return request.get<Result<PageResult<EnergySaving>>>('/report/energy/saving/list', { params })
}

/** 按日期范围查询节能量 */
export function getEnergySavingByRange(reportType: string, startDate: string, endDate: string) {
  return request.get<Result<EnergySaving[]>>('/report/energy/saving/range', {
    params: { reportType, startDate, endDate }
  })
}

// ==================== 用能成本统计 ====================

export interface CostStat {
  id: number
  energyUnitId: number
  reportType: string
  statDate: string
  energyTypeId: number
  cost: number
  costUnit: string
  createTime: string
}

/** 获取成本统计列表 */
export function getCostStatList(params: { current?: number; size?: number; reportType?: string; statDate?: string; energyTypeId?: number }) {
  return request.get<Result<PageResult<CostStat>>>('/report/cost/list', { params })
}

/** 按日期范围查询成本 */
export function getCostStatByRange(reportType: string, startDate: string, endDate: string) {
  return request.get<Result<CostStat[]>>('/report/cost/range', {
    params: { reportType, startDate, endDate }
  })
}

// ==================== 基准能耗 ====================

export interface BaselineConsumption {
  id: number
  energyUnitId: number
  startDate: string
  endDate: string
  baselineValue: number
  baselineType: string
  remark: string
}

/** 获取基准能耗列表 */
export function getBaselineList(params: { current?: number; size?: number; baselineType?: string }) {
  return request.get<Result<PageResult<BaselineConsumption>>>('/report/baseline/list', { params })
}

/** 新增基准能耗 */
export function createBaseline(data: Partial<BaselineConsumption>) {
  return request.post<Result<null>>('/report/baseline', data)
}

/** 修改基准能耗 */
export function updateBaseline(id: number, data: Partial<BaselineConsumption>) {
  return request.put<Result<null>>(`/report/baseline/${id}`, data)
}

/** 删除基准能耗 */
export function deleteBaseline(id: number) {
  return request.delete<Result<null>>(`/report/baseline/${id}`)
}