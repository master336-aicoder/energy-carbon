import request from '@/utils/request'
import type { Result, PageResult, MeterRecord, MeterRecordParams } from '@/types'

/** 分页查询抄表记录 */
export function getMeterRecordList(params: MeterRecordParams) {
  return request.get<Result<PageResult<MeterRecord>>>('/monitor/meter-record/list', { params })
}

/** 获取上次抄表记录 */
export function getLastMeterRecord(meterId: number) {
  return request.get<Result<MeterRecord>>(`/monitor/meter-record/last/${meterId}`)
}

/** 新增抄表记录 */
export function createMeterRecord(data: MeterRecordParams) {
  return request.post<Result<null>>('/monitor/meter-record', data)
}

/** 批量新增抄表记录 */
export function batchCreateMeterRecord(dataList: MeterRecord[]) {
  return request.post<Result<null>>('/monitor/meter-record/batch', dataList)
}

/** 修改抄表记录 */
export function updateMeterRecord(id: number, data: MeterRecord) {
  return request.put<Result<null>>(`/monitor/meter-record/${id}`, data)
}

/** 删除抄表记录 */
export function deleteMeterRecord(id: number) {
  return request.delete<Result<null>>(`/monitor/meter-record/${id}`)
}