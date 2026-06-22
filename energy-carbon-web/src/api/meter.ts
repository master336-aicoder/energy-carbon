import request from '@/utils/request'
import type { Result, PageResult, Meter } from '@/types'

/** 计量器具列表查询参数 */
export interface MeterListParams {
  current?: number
  size?: number
  meterName?: string
  meterType?: string
  status?: number
}

/** 计量器具保存参数 */
export interface MeterSaveParams {
  id?: number
  meterCode: string
  meterName: string
  meterType: string
  deviceId?: number
  deptId: number
  accuracyClass?: string
  rangeMin?: number
  rangeMax?: number
  installDate?: string
  calibrationDate?: string
  nextCalibrationDate?: string
  status: number
  remark?: string
}

/** 获取计量器具列表 */
export function getMeterList(params: MeterListParams) {
  return request.get<Result<PageResult<Meter>>>('/monitor/meter/list', { params })
}

/** 获取计量器具详情 */
export function getMeterDetail(id: number) {
  return request.get<Result<Meter>>(`/monitor/meter/${id}`)
}

/** 新增计量器具 */
export function createMeter(data: MeterSaveParams) {
  return request.post<Result<Meter>>('/monitor/meter', data)
}

/** 修改计量器具 */
export function updateMeter(id: number, data: MeterSaveParams) {
  return request.put<Result<Meter>>(`/monitor/meter/${id}`, data)
}

/** 删除计量器具 */
export function deleteMeter(id: number) {
  return request.delete<Result<null>>(`/monitor/meter/${id}`)
}

/** 导出计量器具 */
export function exportMeter(params: MeterListParams) {
  return request.get('/monitor/meter/export', {
    params,
    responseType: 'blob'
  })
}

/** 导入计量器具 */
export function importMeter(dataList: MeterSaveParams[]) {
  return request.post<Result<string>>('/monitor/meter/import', dataList)
}

/** 修改计量器具状态 */
export function updateMeterStatus(id: number, status: number) {
  return request.put<Result<null>>(`/monitor/meter/${id}/status?status=${status}`)
}
