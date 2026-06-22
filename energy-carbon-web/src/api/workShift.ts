import request from '@/utils/request'
import type { Result, PageResult, WorkShift } from '@/types'

/** 班组列表查询参数 */
export interface WorkShiftListParams {
  current?: number
  size?: number
  shiftName?: string
  status?: number
}

/** 班组保存参数 */
export interface WorkShiftSaveParams {
  id?: number
  shiftCode: string
  shiftName: string
  startTime?: string
  endTime?: string
  restStart?: string
  restEnd?: string
  workDays?: string
  status: number
  remark?: string
}

/** 获取班组列表 */
export function getWorkShiftList(params: WorkShiftListParams) {
  return request.get<Result<PageResult<WorkShift>>>('/monitor/workShift/list', { params })
}

/** 获取班组详情 */
export function getWorkShiftDetail(id: number) {
  return request.get<Result<WorkShift>>(`/monitor/workShift/${id}`)
}

/** 新增班组 */
export function createWorkShift(data: WorkShiftSaveParams) {
  return request.post<Result<WorkShift>>('/monitor/workShift', data)
}

/** 修改班组 */
export function updateWorkShift(id: number, data: WorkShiftSaveParams) {
  return request.put<Result<WorkShift>>(`/monitor/workShift/${id}`, data)
}

/** 删除班组 */
export function deleteWorkShift(id: number) {
  return request.delete<Result<null>>(`/monitor/workShift/${id}`)
}

/** 获取所有班组 */
export function getAllWorkShifts() {
  return request.get<Result<WorkShift[]>>('/monitor/workShift/all')
}

/** 修改班组状态 */
export function updateWorkShiftStatus(id: number, status: number) {
  return request.put<Result<null>>(`/monitor/workShift/${id}/status?status=${status}`)
}
