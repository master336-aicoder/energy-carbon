import request from '@/utils/request'
import type { Result, PageResult } from '@/types'

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
  createTime: string
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
  createTime: string
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
  createTime: string
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
  createTime: string
  updateTime: string
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
  createTime: string
  updateTime: string
}

/** 能耗查询参数 */
export interface ConsumptionListParams {
  current?: number
  size?: number
  energyUnitId?: number
  meterId?: number
  energyTypeId?: number
  statPeriod?: string
  startDate?: string
  endDate?: string
}

/** 电费查询参数 */
export interface ElectricityListParams {
  current?: number
  size?: number
  meterId?: number
  startDate?: string
  endDate?: string
}

/** 能效查询参数 */
export interface EfficiencyListParams {
  current?: number
  size?: number
  energyUnitId?: number
  startDate?: string
  endDate?: string
}

/** 预算查询参数 */
export interface BudgetListParams {
  current?: number
  size?: number
  energyUnitId?: number
  budgetYear?: number
  budgetType?: string
}

/** 对标查询参数 */
export interface BenchmarkListParams {
  current?: number
  size?: number
  benchmarkName?: string
  industry?: string
}

// ============================================================
// 能耗分析 API
// ============================================================

/** 分页查询能耗数据 */
export function getConsumptionList(params: ConsumptionListParams) {
  return request.get<Result<PageResult<AnalysisConsumption>>>('/monitor/analysis/consumption/list', { params })
}

/** 能耗汇总卡片数据 */
export function getConsumptionSummary(params: {
  energyUnitId?: number
  statPeriod: string
  startDate?: string
  endDate?: string
  energyTypeId?: number
}) {
  return request.get<Result<Record<string, unknown>>>('/monitor/analysis/consumption/summary', { params })
}

/** 能耗趋势数据 */
export function getConsumptionTrend(params: {
  energyUnitId?: number
  statPeriod: string
  startDate?: string
  endDate?: string
  energyTypeId?: number
}) {
  return request.get<Result<Array<Record<string, unknown>>>>('/monitor/analysis/consumption/trend', { params })
}

/** 新增能耗数据 */
export function createConsumption(data: Partial<AnalysisConsumption>) {
  return request.post<Result<null>>('/monitor/analysis/consumption', data)
}

/** 修改能耗数据 */
export function updateConsumption(id: number, data: Partial<AnalysisConsumption>) {
  return request.put<Result<null>>(`/monitor/analysis/consumption/${id}`, data)
}

/** 删除能耗数据 */
export function deleteConsumption(id: number) {
  return request.delete<Result<null>>(`/monitor/analysis/consumption/${id}`)
}

// ============================================================
// 电费分析 API
// ============================================================

/** 分页查询电费明细 */
export function getElectricityList(params: ElectricityListParams) {
  return request.get<Result<PageResult<AnalysisElectricityCost>>>('/monitor/analysis/electricity/list', { params })
}

/** 电费汇总数据 */
export function getElectricitySummary(params: {
  meterId?: number
  startDate?: string
  endDate?: string
}) {
  return request.get<Result<Record<string, unknown>>>('/monitor/analysis/electricity/summary', { params })
}

/** 分时电价统计明细 */
export function getElectricityDetail(params: {
  meterId?: number
  startDate?: string
  endDate?: string
}) {
  return request.get<Result<Array<Record<string, unknown>>>>('/monitor/analysis/electricity/detail', { params })
}

/** 新增电费数据 */
export function createElectricity(data: Partial<AnalysisElectricityCost>) {
  return request.post<Result<null>>('/monitor/analysis/electricity', data)
}

/** 修改电费数据 */
export function updateElectricity(id: number, data: Partial<AnalysisElectricityCost>) {
  return request.put<Result<null>>(`/monitor/analysis/electricity/${id}`, data)
}

/** 删除电费数据 */
export function deleteElectricity(id: number) {
  return request.delete<Result<null>>(`/monitor/analysis/electricity/${id}`)
}

// ============================================================
// 能效分析 API
// ============================================================

/** 分页查询能效数据 */
export function getEfficiencyList(params: EfficiencyListParams) {
  return request.get<Result<PageResult<AnalysisEnergyEfficiency>>>('/monitor/analysis/efficiency/list', { params })
}

/** 能效趋势数据 */
export function getEfficiencyTrend(params: {
  energyUnitId: number
  startDate?: string
  endDate?: string
}) {
  return request.get<Result<Array<Record<string, unknown>>>>('/monitor/analysis/efficiency/trend', { params })
}

/** 能耗强度计算 */
export function getEfficiencyIntensity(params: {
  energyUnitId: number
  startDate?: string
  endDate?: string
}) {
  return request.get<Result<Record<string, unknown>>>('/monitor/analysis/efficiency/intensity', { params })
}

/** 对标分析结果 */
export function getBenchmarkAnalysis(params: {
  energyUnitId: number
  industry: string
  startDate?: string
  endDate?: string
}) {
  return request.get<Result<Record<string, unknown>>>('/monitor/analysis/efficiency/benchmark', { params })
}

/** 新增能效数据 */
export function createEfficiency(data: Partial<AnalysisEnergyEfficiency>) {
  return request.post<Result<null>>('/monitor/analysis/efficiency', data)
}

/** 修改能效数据 */
export function updateEfficiency(id: number, data: Partial<AnalysisEnergyEfficiency>) {
  return request.put<Result<null>>(`/monitor/analysis/efficiency/${id}`, data)
}

/** 删除能效数据 */
export function deleteEfficiency(id: number) {
  return request.delete<Result<null>>(`/monitor/analysis/efficiency/${id}`)
}

// ============================================================
// 能碳预算 API
// ============================================================

/** 分页查询预算列表 */
export function getBudgetList(params: BudgetListParams) {
  return request.get<Result<PageResult<AnalysisEnergyBudget>>>('/monitor/analysis/budget/list', { params })
}

/** 按年度汇总预算执行情况 */
export function getBudgetSummaryByYear(energyUnitId: number) {
  return request.get<Result<Array<Record<string, unknown>>>>('/monitor/analysis/budget/summary/year', {
    params: { energyUnitId }
  })
}

/** 按年度查询预算列表 */
export function getBudgetByYear(energyUnitId: number, year: number) {
  return request.get<Result<AnalysisEnergyBudget[]>>(`/monitor/analysis/budget/year/${year}`, {
    params: { energyUnitId }
  })
}

/** 新增预算 */
export function createBudget(data: Partial<AnalysisEnergyBudget>) {
  return request.post<Result<null>>('/monitor/analysis/budget', data)
}

/** 修改预算 */
export function updateBudget(id: number, data: Partial<AnalysisEnergyBudget>) {
  return request.put<Result<null>>(`/monitor/analysis/budget/${id}`, data)
}

/** 删除预算 */
export function deleteBudget(id: number) {
  return request.delete<Result<null>>(`/monitor/analysis/budget/${id}`)
}

/** 更新预算完成率 */
export function updateBudgetCompletion(id: number) {
  return request.put<Result<null>>(`/monitor/analysis/budget/${id}/completion`)
}

/** 批量更新预算完成率 */
export function batchUpdateBudgetCompletion(energyUnitId: number, year: number) {
  return request.put<Result<null>>('/monitor/analysis/budget/completion/batch', null, {
    params: { energyUnitId, year }
  })
}