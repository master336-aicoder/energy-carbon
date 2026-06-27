import request from '@/utils/request'
import type { LogApi } from '@/types/api'

/** 获取操作日志列表 */
export function getLogList(params: LogApi.ListParams) {
  return request.get<LogApi.ListResult>('/system/log/list', { params })
}
