import request from '@/utils/request'
import type { Result, PageResult, EnergyType } from '@/types'

/** 能源品种列表查询参数 */
export interface EnergyTypeListParams {
  current?: number
  size?: number
  typeName?: string
  typeCategory?: string
  status?: number
}

/** 能源品种保存参数 */
export interface EnergyTypeSaveParams {
  id?: number
  typeCode: string
  typeName: string
  typeCategory: string
  unit: string
  conversionCoefficient?: number
  carbonFactor?: number
  status: number
  remark?: string
}

/** 获取能源品种列表 */
export function getEnergyTypeList(params: EnergyTypeListParams) {
  return request.get<Result<PageResult<EnergyType>>>('/monitor/energyType/list', { params })
}

/** 获取能源品种详情 */
export function getEnergyTypeDetail(id: number) {
  return request.get<Result<EnergyType>>(`/monitor/energyType/${id}`)
}

/** 新增能源品种 */
export function createEnergyType(data: EnergyTypeSaveParams) {
  return request.post<Result<EnergyType>>('/monitor/energyType', data)
}

/** 修改能源品种 */
export function updateEnergyType(id: number, data: EnergyTypeSaveParams) {
  return request.put<Result<EnergyType>>(`/monitor/energyType/${id}`, data)
}

/** 删除能源品种 */
export function deleteEnergyType(id: number) {
  return request.delete<Result<null>>(`/monitor/energyType/${id}`)
}

/** 获取所有能源品种 */
export function getAllEnergyTypes() {
  return request.get<Result<EnergyType[]>>('/monitor/energyType/all')
}

/** 修改能源品种状态 */
export function updateEnergyTypeStatus(id: number, status: number) {
  return request.put<Result<null>>(`/monitor/energyType/${id}/status?status=${status}`)
}

// ==================== 模板管理 ====================

/** 能源品种模板保存参数 */
export interface EnergyTypeTemplateSaveParams {
  id?: number
  templateName: string
  templateCode: string
  typeIds?: string
  status: number
}

/** 获取能源品种模板列表 */
export function getEnergyTypeTemplateList(params: { current?: number; size?: number; templateName?: string }) {
  return request.get<Result<PageResult<any>>>('/monitor/energyType/template/list', { params })
}

/** 获取能源品种模板详情 */
export function getEnergyTypeTemplateDetail(id: number) {
  return request.get<Result<any>>(`/monitor/energyType/template/${id}`)
}

/** 新增能源品种模板 */
export function createEnergyTypeTemplate(data: EnergyTypeTemplateSaveParams) {
  return request.post<Result<any>>('/monitor/energyType/template', data)
}

/** 修改能源品种模板 */
export function updateEnergyTypeTemplate(id: number, data: EnergyTypeTemplateSaveParams) {
  return request.put<Result<any>>(`/monitor/energyType/template/${id}`, data)
}

/** 删除能源品种模板 */
export function deleteEnergyTypeTemplate(id: number) {
  return request.delete<Result<null>>(`/monitor/energyType/template/${id}`)
}

/** 根据模板获取能源品种列表 */
export function getTypesByTemplate(id: number) {
  return request.get<Result<EnergyType[]>>(`/monitor/energyType/template/${id}/types`)
}
