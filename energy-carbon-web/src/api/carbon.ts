import request from '@/utils/request'
import type { Result, PageResult } from '@/types'

// ==================== 碳排放核算 ====================

export interface CarbonAccounting {
  id: number
  accountingName: string
  accountingYear: number
  accountingPeriod: string
  energyUnitId: number
  scopeType: string
  totalEmission: number
  emissionUnit: string
  status: string
  remark: string
  details?: CarbonAccountingDetail[]
  createTime: string
  updateTime: string
}

export interface CarbonAccountingDetail {
  id: number
  accountingId: number
  activityName: string
  energyTypeId: number
  activityData: number
  activityUnit: string
  emissionFactor: number
  factorUnit: string
  emission: number
  emissionUnit: string
  sourceDescription: string
}

export interface CarbonAccountingListParams {
  current?: number
  size?: number
  accountingName?: string
  accountingYear?: number
  accountingPeriod?: string
  status?: string
}

export interface EmissionCalcParams {
  accountingId: number
  activities: {
    activityName: string
    energyTypeId: number
    activityData: number
    activityUnit: string
    factorId?: number
    sourceDescription?: string
  }[]
}

export interface EmissionReport {
  accountingId: number
  accountingName: string
  accountingYear: number
  accountingPeriod: string
  scope1Emission: number
  scope2Emission: number
  scope3Emission: number
  totalEmission: number
  emissionUnit: string
  scope1Percent: number
  scope2Percent: number
  scope3Percent: number
}

/** 获取核算列表 */
export function getAccountingList(params: CarbonAccountingListParams) {
  return request.get<Result<PageResult<CarbonAccounting>>>('/carbon/accounting/list', { params })
}

/** 获取核算详情 */
export function getAccountingDetail(id: number) {
  return request.get<Result<CarbonAccounting>>(`/carbon/accounting/${id}`)
}

/** 新增核算 */
export function createAccounting(data: Partial<CarbonAccounting>) {
  return request.post<Result<null>>('/carbon/accounting', data)
}

/** 修改核算 */
export function updateAccounting(id: number, data: Partial<CarbonAccounting>) {
  return request.put<Result<null>>(`/carbon/accounting/${id}`, data)
}

/** 删除核算 */
export function deleteAccounting(id: number) {
  return request.delete<Result<null>>(`/carbon/accounting/${id}`)
}

/** 提交核算 */
export function submitAccounting(id: number) {
  return request.put<Result<null>>(`/carbon/accounting/${id}/submit`)
}

/** 审核核算 */
export function approveAccounting(id: number) {
  return request.put<Result<null>>(`/carbon/accounting/${id}/approve`)
}

/** 排放计算 */
export function calculateEmission(data: EmissionCalcParams) {
  return request.post<Result<null>>('/carbon/accounting/calculate', data)
}

/** 获取排放报告 */
export function getEmissionReport(id: number) {
  return request.get<Result<EmissionReport>>(`/carbon/accounting/report/${id}`)
}

// ==================== 排放因子库 ====================

export interface EmissionFactor {
  id: number
  factorName: string
  factorCategory: string
  energyTypeId: number
  factorValue: number
  factorUnit: string
  source: string
  effectiveYear: number
  defaultFlag: number
  remark: string
}

export interface EmissionFactorListParams {
  current?: number
  size?: number
  factorName?: string
  factorCategory?: string
  effectiveYear?: number
}

/** 获取因子列表 */
export function getFactorList(params: EmissionFactorListParams) {
  return request.get<Result<PageResult<EmissionFactor>>>('/carbon/factor/list', { params })
}

/** 获取因子详情 */
export function getFactorDetail(id: number) {
  return request.get<Result<EmissionFactor>>(`/carbon/factor/${id}`)
}

/** 新增因子 */
export function createFactor(data: Partial<EmissionFactor>) {
  return request.post<Result<null>>('/carbon/factor', data)
}

/** 修改因子 */
export function updateFactor(id: number, data: Partial<EmissionFactor>) {
  return request.put<Result<null>>(`/carbon/factor/${id}`, data)
}

/** 删除因子 */
export function deleteFactor(id: number) {
  return request.delete<Result<null>>(`/carbon/factor/${id}`)
}

/** 按类别查询因子 */
export function getFactorByCategory(category: string) {
  return request.get<Result<EmissionFactor[]>>(`/carbon/factor/category/${category}`)
}

/** 按能源类型查询因子 */
export function getFactorByEnergyType(energyTypeId: number) {
  return request.get<Result<EmissionFactor[]>>(`/carbon/factor/energyType/${energyTypeId}`)
}

/** 获取默认因子列表 */
export function getDefaultFactors() {
  return request.get<Result<EmissionFactor[]>>('/carbon/factor/defaults')
}

/** 设为默认因子 */
export function setDefaultFactor(id: number) {
  return request.put<Result<null>>(`/carbon/factor/${id}/setDefault`)
}

// ==================== 碳核算模型 ====================

export interface AccountingModel {
  id: number
  modelName: string
  energyUnitId: number
  modelConfig: string
  status: number
  remark: string
}

/** 获取模型列表 */
export function getModelList(params: { current?: number; size?: number; modelName?: string }) {
  return request.get<Result<PageResult<AccountingModel>>>('/carbon/model/list', { params })
}

/** 获取模型详情 */
export function getModelDetail(id: number) {
  return request.get<Result<AccountingModel>>(`/carbon/model/${id}`)
}

/** 新增模型 */
export function createModel(data: Partial<AccountingModel>) {
  return request.post<Result<null>>('/carbon/model', data)
}

/** 修改模型 */
export function updateModel(id: number, data: Partial<AccountingModel>) {
  return request.put<Result<null>>(`/carbon/model/${id}`, data)
}

/** 删除模型 */
export function deleteModel(id: number) {
  return request.delete<Result<null>>(`/carbon/model/${id}`)
}

// ==================== 产品碳足迹 ====================

export interface CarbonFootprint {
  id: number
  productId: number
  lifecycleStage: string
  emission: number
  emissionUnit: string
  calcMethod: string
  dataSource: string
  calcDate: string
}

/** 获取碳足迹列表 */
export function getFootprintList(params: { current?: number; size?: number; productId?: number }) {
  return request.get<Result<PageResult<CarbonFootprint>>>('/carbon/footprint/list', { params })
}

/** 获取碳足迹详情 */
export function getFootprintDetail(id: number) {
  return request.get<Result<CarbonFootprint>>(`/carbon/footprint/${id}`)
}

/** 新增碳足迹 */
export function createFootprint(data: Partial<CarbonFootprint>) {
  return request.post<Result<null>>('/carbon/footprint', data)
}

/** 修改碳足迹 */
export function updateFootprint(id: number, data: Partial<CarbonFootprint>) {
  return request.put<Result<null>>(`/carbon/footprint/${id}`, data)
}

/** 删除碳足迹 */
export function deleteFootprint(id: number) {
  return request.delete<Result<null>>(`/carbon/footprint/${id}`)
}

/** 按产品查询碳足迹 */
export function getFootprintByProduct(productId: number) {
  return request.get<Result<CarbonFootprint[]>>(`/carbon/footprint/product/${productId}`)
}

// ==================== 碳核查 ====================

export interface CarbonVerification {
  id: number
  verificationName: string
  accountingId: number
  verificationType: string
  verifier: string
  verificationDate: string
  dataSourceDescription: string
  attachmentPath: string
  verificationResult: string
  status: string
  remark: string
}

export interface VerificationListParams {
  current?: number
  size?: number
  verificationName?: string
  accountingId?: number
  status?: string
}

/** 获取核查列表 */
export function getVerificationList(params: VerificationListParams) {
  return request.get<Result<PageResult<CarbonVerification>>>('/carbon/verification/list', { params })
}

/** 获取核查详情 */
export function getVerificationDetail(id: number) {
  return request.get<Result<CarbonVerification>>(`/carbon/verification/${id}`)
}

/** 新增核查 */
export function createVerification(data: Partial<CarbonVerification>) {
  return request.post<Result<null>>('/carbon/verification', data)
}

/** 修改核查 */
export function updateVerification(id: number, data: Partial<CarbonVerification>) {
  return request.put<Result<null>>(`/carbon/verification/${id}`, data)
}

/** 删除核查 */
export function deleteVerification(id: number) {
  return request.delete<Result<null>>(`/carbon/verification/${id}`)
}

/** 完成核查 */
export function completeVerification(id: number, verificationResult: string) {
  return request.put<Result<null>>(`/carbon/verification/${id}/complete?verificationResult=${encodeURIComponent(verificationResult)}`)
}