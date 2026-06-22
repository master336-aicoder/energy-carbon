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

// ==================== 碳资产管理 ====================

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

export interface CarbonAssetListParams {
  current?: number
  size?: number
  assetType?: string
  assetName?: string
  status?: string
}

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

export interface CarbonAssetTradeListParams {
  current?: number
  size?: number
  assetId?: number
  tradeType?: string
}

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

/** 获取碳资产列表 */
export function getAssetList(params: CarbonAssetListParams) {
  return request.get<Result<PageResult<CarbonAsset>>>('/carbon/asset/list', { params })
}

/** 获取碳资产详情 */
export function getAssetDetail(id: number) {
  return request.get<Result<CarbonAsset>>(`/carbon/asset/${id}`)
}

/** 新增碳资产 */
export function createAsset(data: Partial<CarbonAsset>) {
  return request.post<Result<null>>('/carbon/asset', data)
}

/** 修改碳资产 */
export function updateAsset(id: number, data: Partial<CarbonAsset>) {
  return request.put<Result<null>>(`/carbon/asset/${id}`, data)
}

/** 删除碳资产 */
export function deleteAsset(id: number) {
  return request.delete<Result<null>>(`/carbon/asset/${id}`)
}

/** 资产汇总统计 */
export function getAssetSummary() {
  return request.get<Result<AssetSummaryItem[]>>('/carbon/asset/summary')
}

/** 获取交易记录列表 */
export function getTradeList(params: CarbonAssetTradeListParams) {
  return request.get<Result<PageResult<CarbonAssetTrade>>>('/carbon/asset/trade/list', { params })
}

/** 新增交易记录 */
export function createTrade(data: Partial<CarbonAssetTrade>) {
  return request.post<Result<null>>('/carbon/asset/trade', data)
}

// ==================== 市场供需信息 ====================

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

export interface CarbonMarketInfoListParams {
  current?: number
  size?: number
  infoCategory?: string
  infoTitle?: string
}

/** 获取市场信息列表 */
export function getMarketInfoList(params: CarbonMarketInfoListParams) {
  return request.get<Result<PageResult<CarbonMarketInfo>>>('/carbon/market/list', { params })
}

/** 获取市场信息详情 */
export function getMarketInfoDetail(id: number) {
  return request.get<Result<CarbonMarketInfo>>(`/carbon/market/${id}`)
}

/** 发布市场信息 */
export function createMarketInfo(data: Partial<CarbonMarketInfo>) {
  return request.post<Result<null>>('/carbon/market', data)
}

/** 修改市场信息 */
export function updateMarketInfo(id: number, data: Partial<CarbonMarketInfo>) {
  return request.put<Result<null>>(`/carbon/market/${id}`, data)
}

/** 删除市场信息 */
export function deleteMarketInfo(id: number) {
  return request.delete<Result<null>>(`/carbon/market/${id}`)
}

/** 获取最新市场信息 */
export function getLatestMarketInfo(infoCategory?: string, limit?: number) {
  return request.get<Result<CarbonMarketInfo[]>>('/carbon/market/latest', { params: { infoCategory, limit } })
}

// ==================== CBAM申报 ====================

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

export interface CbamDeclarationListParams {
  current?: number
  size?: number
  declarationName?: string
  declarationPeriod?: string
  status?: string
}

/** 获取CBAM申报列表 */
export function getCbamList(params: CbamDeclarationListParams) {
  return request.get<Result<PageResult<CbamDeclaration>>>('/carbon/cbam/list', { params })
}

/** 获取CBAM申报详情 */
export function getCbamDetail(id: number) {
  return request.get<Result<CbamDeclaration>>(`/carbon/cbam/${id}`)
}

/** 新增CBAM申报 */
export function createCbam(data: Partial<CbamDeclaration>) {
  return request.post<Result<null>>('/carbon/cbam', data)
}

/** 修改CBAM申报 */
export function updateCbam(id: number, data: Partial<CbamDeclaration>) {
  return request.put<Result<null>>(`/carbon/cbam/${id}`, data)
}

/** 删除CBAM申报 */
export function deleteCbam(id: number) {
  return request.delete<Result<null>>(`/carbon/cbam/${id}`)
}

/** CBAM排放计算 */
export function calculateCbam(id: number) {
  return request.post<Result<CbamDeclaration>>(`/carbon/cbam/calculate/${id}`)
}

/** 提交CBAM审核 */
export function submitCbam(id: number) {
  return request.put<Result<null>>(`/carbon/cbam/${id}/submit`)
}

/** 审核通过CBAM */
export function approveCbam(id: number) {
  return request.put<Result<null>>(`/carbon/cbam/${id}/approve`)
}

/** 审核驳回CBAM */
export function rejectCbam(id: number) {
  return request.put<Result<null>>(`/carbon/cbam/${id}/reject`)
}

// ==================== 供应链碳足迹 ====================

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

export interface SupplyChainCarbonListParams {
  current?: number
  size?: number
  supplierName?: string
  productName?: string
  productCategory?: string
}

export interface SupplyChainSummaryItem {
  supplierName?: string
  productName?: string
  productCount?: number
  supplierCount?: number
  avgCarbonFootprint: number
}

/** 获取供应链碳足迹列表 */
export function getSupplyChainList(params: SupplyChainCarbonListParams) {
  return request.get<Result<PageResult<SupplyChainCarbon>>>('/carbon/supply-chain/list', { params })
}

/** 获取供应链碳足迹详情 */
export function getSupplyChainDetail(id: number) {
  return request.get<Result<SupplyChainCarbon>>(`/carbon/supply-chain/${id}`)
}

/** 新增供应链碳足迹 */
export function createSupplyChain(data: Partial<SupplyChainCarbon>) {
  return request.post<Result<null>>('/carbon/supply-chain', data)
}

/** 修改供应链碳足迹 */
export function updateSupplyChain(id: number, data: Partial<SupplyChainCarbon>) {
  return request.put<Result<null>>(`/carbon/supply-chain/${id}`, data)
}

/** 删除供应链碳足迹 */
export function deleteSupplyChain(id: number) {
  return request.delete<Result<null>>(`/carbon/supply-chain/${id}`)
}

/** 按供应商汇总 */
export function getSupplyChainSummaryBySupplier() {
  return request.get<Result<SupplyChainSummaryItem[]>>('/carbon/supply-chain/summary/supplier')
}

/** 按产品汇总 */
export function getSupplyChainSummaryByProduct() {
  return request.get<Result<SupplyChainSummaryItem[]>>('/carbon/supply-chain/summary/product')
}