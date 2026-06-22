import request from '@/utils/request'
import type { Result, PageResult, MonitorHistoryData, MonitorHistoryParams } from '@/types'

/** 分页查询历史数据 */
export function getMonitorHistoryList(params: MonitorHistoryParams) {
  return request.get<Result<PageResult<MonitorHistoryData>>>('/monitor/history/list', { params })
}

/** 查询历史数据曲线（不翻页） */
export function getMonitorHistoryChart(params: { meterId: number; dataType?: string; startTime: string; endTime: string }) {
  return request.get<Result<MonitorHistoryData[]>>('/monitor/history/chart', { params })
}

/** 获取数据类型列表 */
export function getMonitorDataTypes() {
  return request.get<Result<string[]>>('/monitor/history/data-types')
}