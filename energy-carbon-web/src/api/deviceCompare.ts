import request from '@/utils/request'
import type { Result, CompareDataSeries } from '@/types'

/** 双设备同时段对比 */
export function getDeviceCompare(params: {
  meterIdA: number
  meterIdB: number
  dataType?: string
  startTime: string
  endTime: string
}) {
  return request.get<Result<CompareDataSeries[]>>('/monitor/device-compare/compare', { params })
}