import request from '@/utils/request'
import type { Result, PageResult, Device } from '@/types'

/** 设备列表查询参数 */
export interface DeviceListParams {
  current?: number
  size?: number
  deviceName?: string
  deviceType?: string
  status?: number
}

/** 设备保存参数 */
export interface DeviceSaveParams {
  id?: number
  deviceCode: string
  deviceName: string
  deviceType: string
  deptId: number
  manufacturer?: string
  model?: string
  powerRating?: number
  voltageLevel?: string
  status: number
  remark?: string
}

/** 获取设备列表 */
export function getDeviceList(params: DeviceListParams) {
  return request.get<Result<PageResult<Device>>>('/monitor/device/list', { params })
}

/** 获取设备详情 */
export function getDeviceDetail(id: number) {
  return request.get<Result<Device>>(`/monitor/device/${id}`)
}

/** 新增设备 */
export function createDevice(data: DeviceSaveParams) {
  return request.post<Result<Device>>('/monitor/device', data)
}

/** 修改设备 */
export function updateDevice(id: number, data: DeviceSaveParams) {
  return request.put<Result<Device>>(`/monitor/device/${id}`, data)
}

/** 删除设备 */
export function deleteDevice(id: number) {
  return request.delete<Result<null>>(`/monitor/device/${id}`)
}

/** 导出设备 */
export function exportDevice(params: DeviceListParams) {
  return request.get('/monitor/device/export', {
    params,
    responseType: 'blob'
  })
}

/** 导入设备 */
export function importDevice(dataList: DeviceSaveParams[]) {
  return request.post<Result<string>>('/monitor/device/import', dataList)
}

/** 修改设备状态 */
export function updateDeviceStatus(id: number, status: number) {
  return request.put<Result<null>>(`/monitor/device/${id}/status?status=${status}`)
}
