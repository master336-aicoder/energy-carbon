import request from '@/utils/request'
import type { Result, PageResult, AlarmThreshold, AlarmThresholdParams, AlarmRecord, AlarmRecordParams, AlarmStatistics } from '@/types'

// ========== 报警阈值 ==========

/** 分页查询阈值列表 */
export function getAlarmThresholdList(params: AlarmThresholdParams) {
  return request.get<Result<PageResult<AlarmThreshold>>>('/alarm/threshold/list', { params })
}

/** 获取所有启用的阈值 */
export function getEnabledThresholds() {
  return request.get<Result<AlarmThreshold[]>>('/alarm/threshold/enabled')
}

/** 获取阈值详情 */
export function getAlarmThresholdDetail(id: number) {
  return request.get<Result<AlarmThreshold>>(`/alarm/threshold/${id}`)
}

/** 新增阈值 */
export function createAlarmThreshold(data: AlarmThreshold) {
  return request.post<Result<null>>('/alarm/threshold', data)
}

/** 修改阈值 */
export function updateAlarmThreshold(id: number, data: AlarmThreshold) {
  return request.put<Result<null>>(`/alarm/threshold/${id}`, data)
}

/** 启用/禁用阈值 */
export function setAlarmThresholdEnabled(id: number, enabled: number) {
  return request.put<Result<null>>(`/alarm/threshold/${id}/enabled?enabled=${enabled}`)
}

/** 删除阈值 */
export function deleteAlarmThreshold(id: number) {
  return request.delete<Result<null>>(`/alarm/threshold/${id}`)
}

// ========== 报警记录 ==========

/** 分页查询报警记录 */
export function getAlarmRecordList(params: AlarmRecordParams) {
  return request.get<Result<PageResult<AlarmRecord>>>('/alarm/record/list', { params })
}

/** 获取报警记录详情 */
export function getAlarmRecordDetail(id: number) {
  return request.get<Result<AlarmRecord>>(`/alarm/record/${id}`)
}

/** 处理报警记录 */
export function handleAlarmRecord(id: number, params: { handleStatus: string; handler?: string; handleRemark?: string }) {
  return request.put<Result<null>>(`/alarm/record/${id}/handle`, null, { params })
}

/** 批量处理报警记录 */
export function batchHandleAlarmRecord(data: { ids: number[]; handleStatus: string; handler?: string; handleRemark?: string }) {
  return request.put<Result<null>>('/alarm/record/batch-handle', data)
}

/** 获取报警统计 */
export function getAlarmStatistics() {
  return request.get<Result<AlarmStatistics>>('/alarm/record/statistics')
}