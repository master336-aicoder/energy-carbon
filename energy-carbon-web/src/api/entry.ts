import request from '@/utils/request'
import type { Result, PageResult } from '@/types'

// ==================== 指标录入 ====================

export interface EntryIndicator {
  id: number
  energyUnitId: number
  indicatorName: string
  indicatorType: string
  indicatorValue: number
  indicatorUnit: string
  entryDate: string
  entryPerson: string
  remark: string
}

export interface IndicatorListParams {
  current?: number
  size?: number
  indicatorName?: string
  indicatorType?: string
  startDate?: string
  endDate?: string
}

/** 获取指标列表 */
export function getIndicatorList(params: IndicatorListParams) {
  return request.get<Result<PageResult<EntryIndicator>>>('/entry/indicator/list', { params })
}

/** 获取指标详情 */
export function getIndicatorDetail(id: number) {
  return request.get<Result<EntryIndicator>>(`/entry/indicator/${id}`)
}

/** 新增指标 */
export function createIndicator(data: Partial<EntryIndicator>) {
  return request.post<Result<null>>('/entry/indicator', data)
}

/** 修改指标 */
export function updateIndicator(id: number, data: Partial<EntryIndicator>) {
  return request.put<Result<null>>(`/entry/indicator/${id}`, data)
}

/** 删除指标 */
export function deleteIndicator(id: number) {
  return request.delete<Result<null>>(`/entry/indicator/${id}`)
}

/** 导入指标 */
export function importIndicator(dataList: Partial<EntryIndicator>[]) {
  return request.post<Result<null>>('/entry/indicator/import', dataList)
}

// ==================== 产量录入 ====================

export interface EntryProduction {
  id: number
  energyUnitId: number
  productTypeId: number
  productionDate: string
  outputQuantity: number
  outputUnit: string
  batchNumber: string
  remark: string
  entryPerson: string
}

export interface ProductionListParams {
  current?: number
  size?: number
  productTypeId?: number
  batchNumber?: string
  startDate?: string
  endDate?: string
}

/** 获取产量列表 */
export function getProductionList(params: ProductionListParams) {
  return request.get<Result<PageResult<EntryProduction>>>('/entry/production/list', { params })
}

/** 获取产量详情 */
export function getProductionDetail(id: number) {
  return request.get<Result<EntryProduction>>(`/entry/production/${id}`)
}

/** 新增产量 */
export function createProduction(data: Partial<EntryProduction>) {
  return request.post<Result<null>>('/entry/production', data)
}

/** 修改产量 */
export function updateProduction(id: number, data: Partial<EntryProduction>) {
  return request.put<Result<null>>(`/entry/production/${id}`, data)
}

/** 删除产量 */
export function deleteProduction(id: number) {
  return request.delete<Result<null>>(`/entry/production/${id}`)
}

/** 按日期范围查询产量 */
export function getProductionByRange(startDate: string, endDate: string) {
  return request.get<Result<EntryProduction[]>>('/entry/production/range', {
    params: { startDate, endDate }
  })
}