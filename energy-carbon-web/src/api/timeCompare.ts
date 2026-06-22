import request from '@/utils/request'
import type { Result, CompareDataSeries } from '@/types'

/** 同设备不同时段对比 */
export function getTimeCompare(params: {
  meterId: number
  dataType?: string
  startTimeA: string
  endTimeA: string
  startTimeB: string
  endTimeB: string
}) {
  return request.get<Result<CompareDataSeries[]>>('/monitor/time-compare/compare', { params })
}