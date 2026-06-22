import request from '@/utils/request'
import type { Result, PageResult, IotGateway, IotDevice } from '@/types'

/** 网关列表查询参数 */
export interface GatewayListParams {
  pageNum?: number
  pageSize?: number
  name?: string
  code?: string
  status?: number
}

/** 网关保存参数 */
export interface GatewaySaveParams {
  id?: number
  code: string
  name: string
  protocol: string
  ip?: string
  port?: number
  status: number
  remark?: string
}

/** 设备列表查询参数 */
export interface IotDeviceListParams {
  pageNum?: number
  pageSize?: number
  name?: string
  code?: string
  gatewayId?: number
  status?: number
}

/** 设备保存参数 */
export interface IotDeviceSaveParams {
  id?: number
  code: string
  name: string
  gatewayId: number
  deviceType: string
  protocol: string
  status: number
  remark?: string
}

/** 获取网关列表 */
export function getGatewayList(params: GatewayListParams) {
  return request.get<Result<PageResult<IotGateway>>>('/iot/gateway/list', { params })
}

/** 获取网关详情 */
export function getGatewayDetail(id: number) {
  return request.get<Result<IotGateway>>(`/iot/gateway/${id}`)
}

/** 新增网关 */
export function createGateway(data: GatewaySaveParams) {
  return request.post<Result<IotGateway>>('/iot/gateway', data)
}

/** 修改网关 */
export function updateGateway(data: GatewaySaveParams) {
  return request.put<Result<IotGateway>>('/iot/gateway', data)
}

/** 删除网关 */
export function deleteGateway(id: number) {
  return request.delete<Result<null>>(`/iot/gateway/${id}`)
}

/** 获取所有网关（用于下拉选择） */
export function getAllGateways() {
  return request.get<Result<IotGateway[]>>('/iot/gateway/all')
}

/** 获取物联网设备列表 */
export function getIotDeviceList(params: IotDeviceListParams) {
  return request.get<Result<PageResult<IotDevice>>>('/iot/device/list', { params })
}

/** 获取物联网设备详情 */
export function getIotDeviceDetail(id: number) {
  return request.get<Result<IotDevice>>(`/iot/device/${id}`)
}

/** 新增物联网设备 */
export function createIotDevice(data: IotDeviceSaveParams) {
  return request.post<Result<IotDevice>>('/iot/device', data)
}

/** 修改物联网设备 */
export function updateIotDevice(data: IotDeviceSaveParams) {
  return request.put<Result<IotDevice>>('/iot/device', data)
}

/** 删除物联网设备 */
export function deleteIotDevice(id: number) {
  return request.delete<Result<null>>(`/iot/device/${id}`)
}

/** 获取网关下的设备列表 */
export function getGatewayDevices(gatewayId: number) {
  return request.get<Result<IotDevice[]>>(`/iot/gateway/${gatewayId}/devices`)
}

/** 获取设备实时数据 */
export function getDeviceRealtimeData(deviceId: number) {
  return request.get<Result<Record<string, number>>>(`/iot/device/${deviceId}/realtime`)
}
