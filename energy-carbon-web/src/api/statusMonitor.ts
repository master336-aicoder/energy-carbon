import request from '@/utils/request'
import type { Result, PageResult, StatusLog, OnlineRate } from '@/types'

/** 分页查询状态日志 */
export function getStatusLogList(params: { current?: number; size?: number; deviceId?: number; deviceType?: string; onlineStatus?: number }) {
  return request.get<Result<PageResult<StatusLog>>>('/monitor/status/list', { params })
}

/** 获取设备最新状态 */
export function getLatestStatus(deviceType?: string) {
  return request.get<Result<StatusLog[]>>('/monitor/status/latest', { params: { deviceType } })
}

/** 获取在线率统计 */
export function getOnlineRate(deviceType?: string) {
  return request.get<Result<OnlineRate>>('/monitor/status/online-rate', { params: { deviceType } })
}