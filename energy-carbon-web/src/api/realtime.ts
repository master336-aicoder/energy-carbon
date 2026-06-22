import request from '@/utils/request'
import type { Result, RealTimeData } from '@/types'

/** 实时数据查询参数 */
export interface RealtimeParams {
  deviceId?: number
  meterId?: number
  dataType?: string
}

/** 历史数据查询参数 */
export interface HistoryParams {
  deviceId?: number
  meterId?: number
  dataType?: string
  startTime: string
  endTime: string
  interval?: string
}

/** 设备树节点 */
export interface DeviceTreeNode {
  id: number
  name: string
  type: 'unit' | 'device' | 'meter'
  children?: DeviceTreeNode[]
  data?: Record<string, unknown>
}

/** 获取实时数据 */
export function getRealtimeData(params: RealtimeParams) {
  return request.get<Result<RealTimeData>>('/monitor/realtime', { params })
}

/** 获取历史数据 */
export function getHistoryData(params: HistoryParams) {
  return request.get<Result<RealTimeData[]>>('/monitor/history', { params })
}

/** 获取设备树 */
export function getDeviceTree() {
  return request.get<Result<DeviceTreeNode[]>>('/monitor/device-tree')
}

/** 获取最近1小时数据（用于实时曲线） */
export function getRecentHourData(meterId: number, dataType?: string) {
  const endTime = new Date()
  const startTime = new Date(endTime.getTime() - 60 * 60 * 1000)
  return request.get<Result<RealTimeData[]>>('/monitor/history', {
    params: {
      meterId,
      dataType,
      startTime: startTime.toISOString(),
      endTime: endTime.toISOString(),
      interval: '1m'
    }
  })
}

/** 获取数据类型列表 */
export function getDataTypes() {
  return request.get<Result<string[]>>('/monitor/data-types')
}
